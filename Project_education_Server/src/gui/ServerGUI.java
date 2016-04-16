package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
 * ServerGUI
 * ��ü���� ȭ���� �����ϴ� Ŭ����
 * �� , �� , �Ϸ� ���� �Ǿ� �ִ�. 
 *  BorderLayout �� �⺻���� ��� �ִ�.
 */

public abstract class ServerGUI extends JFrame {
	private static final long serialVersionUID = 30L;
	
	// ä�� �ؽ�Ʈ���
	private JTextArea chatArea;	//ä����¿�
	private JTextField chatText;	//ä�ÿ� ĥ ����
	// ���� ��ư
	private JButton connectBtn;
	//������, ���� ����Ʈ, ����
	private JList<String> userList, fileList;
	private Vector<String> userVec, fileVec;
	
	//���ӿ�  port
	private JTextField  charPort, filePort;
	
	//��ũ��
	private JScrollPane chatPane;
	
	//������
	public ServerGUI() {
		super("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);

		// ȭ�鿡 ����
		add(northPane(), BorderLayout.NORTH);
		add(centerPane(), BorderLayout.CENTER);
		add(southPane(), BorderLayout.SOUTH);
		setJMenuBar(menu());
		setVisible(true);
	}//ServerGUI

	// �޴�
	private JMenuBar menu() {
		JMenuBar mb = new JMenuBar();
		// �޴� ����
		JMenu mnufile = new JMenu("����");
		JMenuItem saveItem = new JMenuItem("����");
		mnufile.add(saveItem);
		JMenuItem openItem = new JMenuItem("����");
		mnufile.add(openItem);
		mnufile.addSeparator();
		JMenuItem exitItem = new JMenuItem("��������");
		mnufile.add(exitItem);
		// ���� ���ý� �߻� �̺�Ʈ
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		});
		// ���� ���ý� �߻� �̺�Ʈ
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveText();
			}
		});
		// ���� �߻� �̺�ư
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		mb.add(mnufile);
		return mb;
	}//menu
	
	//���� ����
	private JPanel northPane() {
		// ��ܿ���
		JPanel northPane = new JPanel(new FlowLayout());
	
		// ��Ʈ ��ȣ
		JLabel cLabel = new JLabel("ä�� Port");
		northPane.add(cLabel);
		charPort = new JTextField(5);
		charPort.setText("8020"); // �⺻���� ����
		northPane.add(charPort);
		
		//���� ��Ʈ��ȣ
		JLabel fLabel = new JLabel("���� Port");
		northPane.add(fLabel);
		filePort = new JTextField(5);
		filePort.setText("8021");
		northPane.add(filePort);
		
		// ���� ��ư
		connectBtn = new JButton("����");
		// ������ �������� �̺�Ʈ �߻�
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConnect();
			}
		});
		
		northPane.add(connectBtn);
		return northPane;
	}//northPane
	
	//�߰� ����
	private JSplitPane centerPane() {
		// ������, ���� ��
		JTabbedPane jtp = new JTabbedPane();
		userVec = new Vector<>();
		fileVec = new Vector<>();
		userList = new JList<>(userVec);
		fileList = new JList<>(fileVec);
		//�ߺ� ���� ����
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtp.addTab("������", userList);
		jtp.addTab("����", fileList);
		
		// ��������Ʈ �̺�Ʈ ó��
		userList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // �̺�Ʈ �ߺ� ���� ����
					Point p = userList.indexToLocation(userList.getSelectedIndex());
					String user = userList.getSelectedValue();
					if(user != null)
						userSelect(p, user);
					
					userList.clearSelection();
				}
			}
		});

		// ��������Ʈ �̺�Ʈ ó��
		fileList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // �̺�Ʈ �ߺ� ���� ����
					fileSelect();
				}
			}
		});

		// ä�� �ؽ�Ʈ ���
		chatArea = new JTextArea(12, 30);
		chatArea.setEditable(false);
		chatPane = new JScrollPane(chatArea);
		// ���� ��ũ�� ���߱�
		chatPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// ���η� ���� ������ (�߰� ����)
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPane, jtp);

		return splitPane;
	}//centerPane

	//���� ����
	private JPanel southPane() {
		// �ϴ� ���� ����
		JPanel southPane = new JPanel(new FlowLayout());
		// ä�ÿ� �� ����.
		chatText = new JTextField(30);
		chatText.setText("�����Է�");
		// �� �Է��� ���͸� ������ �� �̺�Ʈ �߻�
		chatText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					sendChatMsg();
			}
		});
		southPane.add(chatText);
		// ���� ��ư
		JButton enterBtn = new JButton("Enter");
		// ��ư�� ������ �� �̺�Ʈ �߻�
		enterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChatMsg();
			}
		});
		southPane.add(enterBtn);

		return southPane;
	}//southPane
	
	//���� �޼���
	abstract public void receiveChatMsg(String str);
	//������ ����Ʈ
	abstract public void receiveUser(String user);
	//���� ����Ʈ
	abstract public void receiveFile(String file);
	//�޼��� Ŭ���̾�Ʈ�� ����
	abstract public void sendChatMsg();
	//���� ����
	abstract public void sendConnect();
	//��ȭ���� ����
	abstract public void saveText();
	//��ȭ ���� ����
	abstract public void openText();
	//����
	abstract public void exit();
	//��������Ʈ ���ý�
	abstract public void userSelect(Point p, String str);
	//���ϸ���Ʈ ���� ��
	abstract public void fileSelect();

	//=======================================================================
	//ä��â
	public JTextArea getChatArea() 									{		return chatArea;	}			
	//���� ��ư
	public JButton getConnectBtn() 									{		return connectBtn;	}
	
	//����, ���� ����Ʈ
	public JList<String> getUserList() 		{		return userList;	}
	public JList<String> getFileList() 			{		return fileList;	}
	public void setUserList(JList<String> userList) 		{		this.userList = userList;	}
	public void setFileList(JList<String> fileList)		 		{		this.fileList = fileList;	}
	
	//����, ���� ����
	public Vector<String> getUserVec() 								{		return userVec;	}
	public Vector<String> getFileVec() 								{		return fileVec;	}
	public void setUserVec(Vector<String> userVec) 	{		this.userVec = userVec;	}
	public void setFileVec(Vector<String> fileVec) 			{		this.fileVec = fileVec;	}
	
	// PORT
	public JTextField getChatPort() 		{		return charPort;	}
	public JTextField getFilePort() 		{		return filePort;	}
	//ä�ÿ� ĥ ��
	public JTextField getChatText() {		return chatText;	}
	//��ũ�� 
	public JScrollPane getChatPane() {  return chatPane; }
}

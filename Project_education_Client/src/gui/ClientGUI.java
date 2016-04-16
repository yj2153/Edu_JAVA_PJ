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
 * ClientGUI
 * ��ü���� ȭ���� �����ϴ� Ŭ����
 * �� , �� , �Ϸ� ���� �Ǿ� �ִ�. 
 *  BorderLayout �� �⺻���� ��� �ִ�.
 */

public abstract class ClientGUI extends JFrame{
	private static final long serialVersionUID = 30L;
	
	// ä�� �ؽ�Ʈ���
	private JTextArea chatArea;	//ä����¿�
	private JTextField chatText;	//ä�ÿ� ĥ ����
	// ���� ��ư
	private JButton connectBtn;
	//������, ���� ����Ʈ, ����
	private JList<String> userList;
	private JList<String> fileList;
	private Vector<String> userVec;

	private Vector<String> fileVec;
	
	//���ӿ� ip, port, id
	private JTextField ipText;
	private JTextField portText;
	private JTextField idText;
	//���õ� ���� ��
	private String selectValue;
	
	//��ũ�� 
	private JScrollPane chatPane;
	
	//������
	public ClientGUI() {
		super("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);		//ȭ�� ����

		// ȭ�鿡 ����
		add(northPane(), BorderLayout.NORTH);			//��
		add(centerPane(), BorderLayout.CENTER);		//��
		add(southPane(), BorderLayout.SOUTH);			//��
		
		setJMenuBar(menu());		//�޴�����
		setVisible(true);
	}
	
	
	//�޴�
	private JMenuBar menu()
	{
		JMenuBar mb = new JMenuBar();
		//�޴� ����
		JMenu mnumenu = new JMenu("�޴�");
		JMenuItem saveItem = new JMenuItem("����");
		mnumenu.add(saveItem);
		JMenuItem openItem = new JMenuItem("����");
		mnumenu.add(openItem);
		mnumenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("��������");
		mnumenu.add(exitItem);
		//���� ���ý� �߻� �̺�Ʈ
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		}); 
		
		//���� ���ý� �߻� �̺�Ʈ
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveText();
			}
		});
		
		//���� �߻� �̺�ư
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		
		//===============================================
		JMenu mnufile = new JMenu("����");
		JMenuItem uoploadItem = new JMenuItem("���ε�");
		mnufile.add(uoploadItem);
		//���ε� �߻� �̺�Ʈ
		uoploadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upload();
			}
		});
		
		mb.add(mnumenu);
		mb.add(mnufile);
		return mb;
	}// menu
	
	//���� ����
	private JSplitPane northPane() {
		// ��ܿ���
		JPanel northPane = new JPanel(new FlowLayout());
		// ip�ּ�
		ipText = new JTextField(10);
		ipText.setText("127.0.0.1"); // �⺻���� ����
		northPane.add(ipText);
		// ��Ʈ ��ȣ
		portText = new JTextField(5);
		portText.setText("8020"); // �⺻���� ����
		northPane.add(portText);
		// ���̵�
		idText = new JTextField(10);
		idText.setText("id�Է�"); // �⺻���� ����
		northPane.add(idText);
		// ���� ��ư
		connectBtn = new JButton("����");
		// ������ �������� �̺�Ʈ �߻�
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConnect();
			}
		});
		northPane.add(connectBtn);
		
		//���η� â �п� �ϱ�
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, northPane, new JLabel("�ӼӸ� ��� ��� : ����ID >> ä�ó���"));
		
		return splitPane;
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
				if (!e.getValueIsAdjusting()) { // �̺�Ʈ �ߺ� ���� ����]
					selectValue = userList.getSelectedValue();
					Point p = userList.indexToLocation(userList.getSelectedIndex());
					//������ ���� �ƴϸ�
					if(selectValue != null)
						userSelect(selectValue ,p);
					
					//�����ʱ�ȭ
					userList.clearSelection();
				}//end if
			}
		});

		// ���ϸ���Ʈ �̺�Ʈ ó��
		fileList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // �̺�Ʈ �ߺ� ���� ����
					Point p = fileList.indexToLocation(fileList.getSelectedIndex());
					int index = fileList.getSelectedIndex();
					//���õ� ���� �ִٸ�
					if(getFileList().getSelectedValue() != null)
						fileSelect(p, index);
					
					//�����ʱ�ȭ
					getFileList().clearSelection();
				}//end if
			}
		});

		// ä�� �ؽ�Ʈ ���
		chatArea = new JTextArea(12, 30);
		chatArea.setEditable(false);
		chatPane = new JScrollPane(chatArea);
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
	abstract public void userRemove(String user);
	abstract public void userUpdate(String[] users);
	//���� ����Ʈ
	abstract public void fileUpdate(String[] files);
	//�޼��� ������ ����
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
	abstract public void userSelect(String str, Point p);
	//���ϸ���Ʈ ���� ��
	abstract public void fileSelect(Point p, int index);
	//�⺻ �޼���
	abstract public void MessageGUI(String str, int type);
	//���� �޼���
	abstract public int  QMessageGUI(String str, String[] buttons,  int type);
	//���� ���ε�
	abstract public void upload();
	//����Ʈ �ʱ�ȭ
	abstract public void listClear();
	
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
	
	// IP , PORT, ID
	public JTextField getIpText() 			{		return ipText;	}
	public JTextField getPortText() 		{		return portText;	}
	public JTextField getIdText() 			{		return idText;	}
	
	//ä�ÿ� ĥ ��
	public JTextField getChatText() {		return chatText;	}
	
	//��ũ��
	public JScrollPane getChatPane() { return chatPane; }
}

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
 * 전체적인 화면을 구성하는 클래스
 * 상 , 중 , 하로 구성 되어 있다. 
 *  BorderLayout 을 기본으로 잡고 있다.
 */

public abstract class ServerGUI extends JFrame {
	private static final long serialVersionUID = 30L;
	
	// 채팅 텍스트출력
	private JTextArea chatArea;	//채팅출력용
	private JTextField chatText;	//채팅에 칠 내용
	// 접속 버튼
	private JButton connectBtn;
	//접속자, 파일 리스트, 백터
	private JList<String> userList, fileList;
	private Vector<String> userVec, fileVec;
	
	//접속용  port
	private JTextField  charPort, filePort;
	
	//스크롤
	private JScrollPane chatPane;
	
	//생성자
	public ServerGUI() {
		super("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);

		// 화면에 셋팅
		add(northPane(), BorderLayout.NORTH);
		add(centerPane(), BorderLayout.CENTER);
		add(southPane(), BorderLayout.SOUTH);
		setJMenuBar(menu());
		setVisible(true);
	}//ServerGUI

	// 메뉴
	private JMenuBar menu() {
		JMenuBar mb = new JMenuBar();
		// 메뉴 정보
		JMenu mnufile = new JMenu("파일");
		JMenuItem saveItem = new JMenuItem("저장");
		mnufile.add(saveItem);
		JMenuItem openItem = new JMenuItem("열기");
		mnufile.add(openItem);
		mnufile.addSeparator();
		JMenuItem exitItem = new JMenuItem("서버종료");
		mnufile.add(exitItem);
		// 열기 선택시 발생 이벤트
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		});
		// 저장 선택시 발생 이벤트
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveText();
			}
		});
		// 종료 발생 이벤튼
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		mb.add(mnufile);
		return mb;
	}//menu
	
	//상위 영역
	private JPanel northPane() {
		// 상단영역
		JPanel northPane = new JPanel(new FlowLayout());
	
		// 포트 번호
		JLabel cLabel = new JLabel("채팅 Port");
		northPane.add(cLabel);
		charPort = new JTextField(5);
		charPort.setText("8020"); // 기본으로 셋팅
		northPane.add(charPort);
		
		//파일 포트번호
		JLabel fLabel = new JLabel("파일 Port");
		northPane.add(fLabel);
		filePort = new JTextField(5);
		filePort.setText("8021");
		northPane.add(filePort);
		
		// 접속 버튼
		connectBtn = new JButton("생성");
		// 접속을 눌렀을때 이벤트 발생
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConnect();
			}
		});
		
		northPane.add(connectBtn);
		return northPane;
	}//northPane
	
	//중간 영역
	private JSplitPane centerPane() {
		// 접속자, 파일 탭
		JTabbedPane jtp = new JTabbedPane();
		userVec = new Vector<>();
		fileVec = new Vector<>();
		userList = new JList<>(userVec);
		fileList = new JList<>(fileVec);
		//중복 선택 금지
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtp.addTab("접속자", userList);
		jtp.addTab("파일", fileList);
		
		// 유저리스트 이벤트 처리
		userList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // 이벤트 중복 수신 방지
					Point p = userList.indexToLocation(userList.getSelectedIndex());
					String user = userList.getSelectedValue();
					if(user != null)
						userSelect(p, user);
					
					userList.clearSelection();
				}
			}
		});

		// 유저리스트 이벤트 처리
		fileList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // 이벤트 중복 수신 방지
					fileSelect();
				}
			}
		});

		// 채팅 텍스트 출력
		chatArea = new JTextArea(12, 30);
		chatArea.setEditable(false);
		chatPane = new JScrollPane(chatArea);
		// 가로 스크롤 감추기
		chatPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// 세로로 영역 나누기 (중간 영역)
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPane, jtp);

		return splitPane;
	}//centerPane

	//하위 영역
	private JPanel southPane() {
		// 하단 영역 생성
		JPanel southPane = new JPanel(new FlowLayout());
		// 채팅에 쓸 내용.
		chatText = new JTextField(30);
		chatText.setText("내용입력");
		// 다 입력후 엔터를 눌렀을 때 이벤트 발생
		chatText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					sendChatMsg();
			}
		});
		southPane.add(chatText);
		// 엔터 버튼
		JButton enterBtn = new JButton("Enter");
		// 버튼을 눌렀을 때 이벤트 발생
		enterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChatMsg();
			}
		});
		southPane.add(enterBtn);

		return southPane;
	}//southPane
	
	//받은 메세지
	abstract public void receiveChatMsg(String str);
	//접속자 리스트
	abstract public void receiveUser(String user);
	//파일 리스트
	abstract public void receiveFile(String file);
	//메세지 클라이언트로 전송
	abstract public void sendChatMsg();
	//서버 생성
	abstract public void sendConnect();
	//대화내용 저장
	abstract public void saveText();
	//대화 내용 열기
	abstract public void openText();
	//종료
	abstract public void exit();
	//유저리스트 선택시
	abstract public void userSelect(Point p, String str);
	//파일리스트 선택 시
	abstract public void fileSelect();

	//=======================================================================
	//채팅창
	public JTextArea getChatArea() 									{		return chatArea;	}			
	//접속 버튼
	public JButton getConnectBtn() 									{		return connectBtn;	}
	
	//접속, 파일 리스트
	public JList<String> getUserList() 		{		return userList;	}
	public JList<String> getFileList() 			{		return fileList;	}
	public void setUserList(JList<String> userList) 		{		this.userList = userList;	}
	public void setFileList(JList<String> fileList)		 		{		this.fileList = fileList;	}
	
	//접속, 파일 벡터
	public Vector<String> getUserVec() 								{		return userVec;	}
	public Vector<String> getFileVec() 								{		return fileVec;	}
	public void setUserVec(Vector<String> userVec) 	{		this.userVec = userVec;	}
	public void setFileVec(Vector<String> fileVec) 			{		this.fileVec = fileVec;	}
	
	// PORT
	public JTextField getChatPort() 		{		return charPort;	}
	public JTextField getFilePort() 		{		return filePort;	}
	//채팅에 칠 거
	public JTextField getChatText() {		return chatText;	}
	//스크롤 
	public JScrollPane getChatPane() {  return chatPane; }
}

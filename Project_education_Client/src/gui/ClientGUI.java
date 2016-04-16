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
 * 전체적인 화면을 구성하는 클래스
 * 상 , 중 , 하로 구성 되어 있다. 
 *  BorderLayout 을 기본으로 잡고 있다.
 */

public abstract class ClientGUI extends JFrame{
	private static final long serialVersionUID = 30L;
	
	// 채팅 텍스트출력
	private JTextArea chatArea;	//채팅출력용
	private JTextField chatText;	//채팅에 칠 내용
	// 접속 버튼
	private JButton connectBtn;
	//접속자, 파일 리스트, 백터
	private JList<String> userList;
	private JList<String> fileList;
	private Vector<String> userVec;

	private Vector<String> fileVec;
	
	//접속용 ip, port, id
	private JTextField ipText;
	private JTextField portText;
	private JTextField idText;
	//선택된 유저 명
	private String selectValue;
	
	//스크롤 
	private JScrollPane chatPane;
	
	//생성자
	public ClientGUI() {
		super("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);		//화면 셋팅

		// 화면에 셋팅
		add(northPane(), BorderLayout.NORTH);			//상
		add(centerPane(), BorderLayout.CENTER);		//중
		add(southPane(), BorderLayout.SOUTH);			//하
		
		setJMenuBar(menu());		//메뉴설정
		setVisible(true);
	}
	
	
	//메뉴
	private JMenuBar menu()
	{
		JMenuBar mb = new JMenuBar();
		//메뉴 정보
		JMenu mnumenu = new JMenu("메뉴");
		JMenuItem saveItem = new JMenuItem("저장");
		mnumenu.add(saveItem);
		JMenuItem openItem = new JMenuItem("열기");
		mnumenu.add(openItem);
		mnumenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("접속종료");
		mnumenu.add(exitItem);
		//열기 선택시 발생 이벤트
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		}); 
		
		//저장 선택시 발생 이벤트
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveText();
			}
		});
		
		//종료 발생 이벤튼
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		
		//===============================================
		JMenu mnufile = new JMenu("파일");
		JMenuItem uoploadItem = new JMenuItem("업로드");
		mnufile.add(uoploadItem);
		//업로드 발생 이벤트
		uoploadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upload();
			}
		});
		
		mb.add(mnumenu);
		mb.add(mnufile);
		return mb;
	}// menu
	
	//상위 영역
	private JSplitPane northPane() {
		// 상단영역
		JPanel northPane = new JPanel(new FlowLayout());
		// ip주소
		ipText = new JTextField(10);
		ipText.setText("127.0.0.1"); // 기본으로 셋팅
		northPane.add(ipText);
		// 포트 번호
		portText = new JTextField(5);
		portText.setText("8020"); // 기본으로 셋팅
		northPane.add(portText);
		// 아이디
		idText = new JTextField(10);
		idText.setText("id입력"); // 기본으로 셋팅
		northPane.add(idText);
		// 접속 버튼
		connectBtn = new JButton("접속");
		// 접속을 눌렀을때 이벤트 발생
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConnect();
			}
		});
		northPane.add(connectBtn);
		
		//새로로 창 분열 하기
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, northPane, new JLabel("귓속말 사용 방법 : 상대방ID >> 채팅내용"));
		
		return splitPane;
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
				if (!e.getValueIsAdjusting()) { // 이벤트 중복 수신 방지]
					selectValue = userList.getSelectedValue();
					Point p = userList.indexToLocation(userList.getSelectedIndex());
					//값들이 널이 아니면
					if(selectValue != null)
						userSelect(selectValue ,p);
					
					//선택초기화
					userList.clearSelection();
				}//end if
			}
		});

		// 파일리스트 이벤트 처리
		fileList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // 이벤트 중복 수신 방지
					Point p = fileList.indexToLocation(fileList.getSelectedIndex());
					int index = fileList.getSelectedIndex();
					//선택된 값이 있다면
					if(getFileList().getSelectedValue() != null)
						fileSelect(p, index);
					
					//선택초기화
					getFileList().clearSelection();
				}//end if
			}
		});

		// 채팅 텍스트 출력
		chatArea = new JTextArea(12, 30);
		chatArea.setEditable(false);
		chatPane = new JScrollPane(chatArea);
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
	abstract public void userRemove(String user);
	abstract public void userUpdate(String[] users);
	//파일 리스트
	abstract public void fileUpdate(String[] files);
	//메세지 서버로 전송
	abstract public void sendChatMsg();
	//서버 접속
	abstract public void sendConnect();
	//대화내용 저장
	abstract public void saveText();
	//대화 내용 열기
	abstract public void openText();
	//종료
	abstract public void exit();
	//유저리스트 선택시
	abstract public void userSelect(String str, Point p);
	//파일리스트 선택 시
	abstract public void fileSelect(Point p, int index);
	//기본 메세지
	abstract public void MessageGUI(String str, int type);
	//질문 메세지
	abstract public int  QMessageGUI(String str, String[] buttons,  int type);
	//파일 업로드
	abstract public void upload();
	//리스트 초기화
	abstract public void listClear();
	
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
	
	// IP , PORT, ID
	public JTextField getIpText() 			{		return ipText;	}
	public JTextField getPortText() 		{		return portText;	}
	public JTextField getIdText() 			{		return idText;	}
	
	//채팅에 칠 거
	public JTextField getChatText() {		return chatText;	}
	
	//스크롤
	public JScrollPane getChatPane() { return chatPane; }
}

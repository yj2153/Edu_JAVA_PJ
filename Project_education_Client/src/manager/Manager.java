package manager;

import java.io.File;

import server.Message;

/*
 * Manager 는 싱글톤.
 * GUI부분과 Server부분을 이어주는 역활을 한다.
 * Gui에서 요청이 들어오면 Manager -> Server로 넘겨준다
 * Server에서 요청이 들어오면 Manager -> GUI로 넘어간다.
 * Server - > GUI, GUI -> Server 직접 연결하지 않는다. 꼭 manager를 통해야지만 가능
 */

public class Manager {
	private static Manager mgr;
	private ClientGUIMgr guiMgr;
	private ServerMgr serverMgr;
	
	private Manager (){
		guiMgr = new ClientGUIMgr();
		serverMgr = new ServerMgr();
	}
	
	public static Manager getMgr(){
		if(mgr == null)
			mgr = new Manager();
		
		return mgr;
	}
	//GUI ===================================================
	//채팅화면에 뿌릴값
	public void receiveChatMsg(String str)
	{
		guiMgr.receiveChatMsg(str);
	}
	//기본 팝업창
	public void MessageGUI(String str , int type){
	Thread m = new Thread(new Runnable() {
			@Override
			public void run() {
				guiMgr.MessageGUI(str, type);
			}
		});
		m.start();
	}
	//질문 팝업창
	public int QMessageGUI(String str, String[] buttons, int type){
		return guiMgr.QMessageGUI(str,buttons,  type);
	}
	
	//유저 리스트 갱싱
	public void userUpdate(String[] users)
	{
		guiMgr.userUpdate(users);
	}
	public void userRemove(String user)
	{
		guiMgr.userRemove(user);
	}
	//파일 리스트 갱신
	public void fileUpdate(String[] files)
	{
		guiMgr.fileUpdate(files);
	}
	//리스트 클리어
	public void listClear(){
		guiMgr.listClear();
	}
	///Server===================================================
	//접속 시도
	public void connect(String ip , String port, String id)
	{
		serverMgr.connect(ip, port, id);
	}
	//서버로 보낼 메세지
	public void sendMessage(String id, String str)
	{
		serverMgr.getSend().setMessage(id, str);
	}
	//접속  여부 초기화
	public void connectInit(){
		guiMgr.connectEnabled();
	}
	//파일 서버 요청
	public void fileRequest(File file){
		//파일 준비하기
		serverMgr.setUpFile(file);
		//서버 요청메세지
		serverMgr.getSend().setMessage(Message.FILEUPLOAD, file.getName());
	}
	//파일서버 요청
	public void fileRequest(int index){
		//요청하는 파일 번호
		serverMgr.setDownFileNum(index);
		//서버 요청베세지
		serverMgr.getSend().setMessage(Message.FILEDOWN, null);
	}
	
	//파일 폴더
	public File fileFolder(){ 
		return serverMgr.getFileFolder();
	}
	
	//파일일시정지
	public boolean getFileWait() {
		return serverMgr.getFileWait();
	}
	public void setFileWait(boolean b){
		serverMgr.setFileWait(b);
	}
	
	//접속 아이디
	public String getUserID(){
		return serverMgr.getUserID();
	}
	
	//파일서버 사용 여부
	public void setFileConnect(boolean b){
		serverMgr.setFileConnect(b);
	}
	public boolean getFileConnect(){ 
		return serverMgr.getFileConnect();
	}
}

package manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import common.UI;
import server.ChatReceviveServer;
import server.ChatSendServer;
import server.FileServer;
import server.Message;

/*
 * ServerMgr 
 * 서버와 연결하는 부분
 * 소켓을 생성한다,
 * gui에서 요청이 들어오면 Manager를 통해서 들어와서 나의 정보를 뺏어감
 */

public class ServerMgr {
	//서버 접속 여부
	private static boolean SERVER = true;
	
	private Socket sock;
	private ObjectInputStream oisServer;
	private ObjectOutputStream oosServer;
	private String ip;
	private File upFile;
	private int downFileNum;
	
	private ChatSendServer sendServer;
	private ChatReceviveServer receiveServer;
	
	//아이디
	private String id;
	
	//파일폴더위치
	private File fileFolder;
	//파일 일시정지
	private boolean fileWait;
	// 파일서버 접속 여부
	private boolean fileConnect;
	
	public ServerMgr(){
		sendServer = new ChatSendServer(this);
		fileFolder = new File("file");
		fileWait = false;
		fileConnect = false;
	}
	
	// 서버와 접속
	public void connect(String ip, String port, String id) {
		//서버를 사용 하지 않겠다면.
		if(!SERVER)
			return;
		
		try {
			this.ip = ip;
			this.id = id;
			sock = new Socket(ip, Integer.parseInt(port));
			UI.getUI().ouputMsg(sock + ": 연결됨");
			OutputStream toServer = sock.getOutputStream();
			InputStream fromServer = sock.getInputStream();
			oosServer = new ObjectOutputStream(toServer);
			oisServer = new ObjectInputStream(fromServer);
			//로그인 시도
			sendServer.setMessage(Message.LOGIN, id);
			
			//받는 쓰레드 가동
			receiveServer = new ChatReceviveServer(this);
			receiveServer.start();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( connect() )");
			Manager.getMgr().connectInit();
			Manager.getMgr().MessageGUI("서버가 없습니다.",  JOptionPane.WARNING_MESSAGE);
			end();
		}// end catch
	}// connect

	// 연결 종료
	public void end() {
		try {
			if (oosServer != null)
				oosServer.close();
			if (oisServer != null)
				oisServer.close();
			if (sock != null)
				sock.close();
		} catch (IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( end() )");
		}//end catch

		UI.getUI().ouputMsg("연결 종료");
	}// end
	
	
	//파일 업로드 하기
	public void fileUpload(String port){
		new FileServer(ip, port, upFile, Message.FILEUPLOAD).start();
	}
	//파일다운로드하기
	public void fileDown(String port){
		new FileServer	(ip, port, downFileNum, Message.FILEDOWN).start();
	}
	
	//보내는 서버
	public ChatSendServer getSend() { return sendServer; }
	//받는 서버
	public ChatReceviveServer getReceive() { return receiveServer; }
		
	//서버 스트림 정보
	public ObjectInputStream getOisServer() {		return oisServer;	}
	public ObjectOutputStream getOosServer() {		return oosServer;	}
	
	//파일
	public void setUpFile(File file) { upFile = file; }
	public void setDownFileNum(int downFileNum) {	this.downFileNum = downFileNum; 	}
	
	//파일폴더
	public File getFileFolder() { return fileFolder; }

	//파일 일시정지
	public boolean getFileWait() { return fileWait; }
	public void setFileWait(boolean b) { fileWait = b; }
	
	//유저 아이디
	public String getUserID(){ return id; }
	
	//파일서버 사용중
	public boolean getFileConnect() { return fileConnect; }
	public void setFileConnect(boolean b) { fileConnect = b; }
}

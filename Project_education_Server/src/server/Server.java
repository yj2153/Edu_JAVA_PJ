package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import common.UI;
import manager.ServerMgr;

/*
 * Server
 * CatchServer , FileServer가 상속받는 부모 클래스
 * 공통으로 쓰는 소켓받기를 하나로 묶어버림.
 */

public abstract class Server extends Thread{
	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private InputStream fromClient;
	private OutputStream toClient;
	
	private ServerMgr mgr;
	private String sockName;
	
	public Server(Socket s, ServerMgr mgr) {
		this.mgr = mgr;
		sock = s;
		try {
			// 소켓 정보 받아오기
			fromClient = sock.getInputStream();
			toClient = sock.getOutputStream();
			
			oos = new ObjectOutputStream(toClient);
			ois = new ObjectInputStream(fromClient);
		} // end while
		catch (Exception e) {		
			//	 연결 종료 
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( Server() )");
		} // end catch
	}// Server
	
	//종료 
	protected void end(){
		try {		// 연결 종료
			if (ois != null)				ois.close();
			if (oos != null)				oos.close();
			if (sock != null)				sock.close();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( end() )");
		}
	}//end 
	
	//전송하기
	protected void writeObject(ResponseMsg msg) {
		try {
			// 전송하기
			oos.writeObject(msg);
			// 물내리기
			oos.flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( writeObject() )");
		}//end catch
	}//writeObject
	
	// 에러메세지 전송
	protected void sendError(String id, String error) {
		UI.getUI().ouputMsg(new Date().toString() + " : 에러발생");
		ResErrorMsg rlm = new ResErrorMsg();
		rlm.setReqCode(id); // 클라에서 요청한 번호
		rlm.setResCode(Message.ERROR); // 에러 알림 번호
		rlm.setString(error); // 에러 내용
		writeObject(rlm);
	}// sendError

	//클라이언트에서 받은 메세지
	abstract protected void reqMessage(RequestMsg msg);

	public Socket getSock() {		return sock;	}
	public ObjectInputStream getOis() {		return ois;	}
	public ObjectOutputStream getOos() {		return oos;	}
	public OutputStream getToClient() {		return toClient;	}
	public ServerMgr getSMgr() {		return mgr;	}
	public String getSockName() { return sockName; }
	public void setSockName(String name) { sockName = name; }
	
}

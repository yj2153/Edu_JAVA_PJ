package server;

import javax.swing.JOptionPane;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * ChatSendServer
 * 서버에 요청 보내는 부분
 * 처음엔 많아서 하나의 클래스로 분리 하였으나
 * 정리하니 매우 짧아짐. ( ServerMgr에 붙일까.. 생각중.
 * 
  * ServerMgr 을 상속받는 관계가 아님
 */

public class ChatSendServer {
	ServerMgr mgr;

	public ChatSendServer(ServerMgr o) {
		this.mgr = o;
	}

	// =======================================================================================================
	// 서버로 보낼 부분
	// =======================================================================================================
	public void setMessage(String id, String str) {
		
		// 로그인 요청 //로그아웃 요청 //채팅 요청
		RequestMsg msg = new RequestMsg();
		msg.setReqCode(id); // 요청번호 전송
		// 로그인,로그아웃 =아이디 / 채팅 = 내용 / 파일 =
		msg.setString(str); //
		writeObject(msg); // 서버 전송
		
	}// setMessage
	
	// 서버로 전송
	public void writeObject(RequestMsg msg) {
		try {
			mgr.getOosServer().writeObject(msg);
			mgr.getOosServer().flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발샐 ( writeObject() )");
			Manager.getMgr().MessageGUI("서버가 없습니다.",  JOptionPane.WARNING_MESSAGE);
		}//end catch
	}// writeObject
}

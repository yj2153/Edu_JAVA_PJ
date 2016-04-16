package server;

import java.io.IOException;

import javax.swing.JOptionPane;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * ChatReceviveServer
 * 서버에서 받은 응답을 처리하는 클래스
 * 
 * ServerMgr 을 상속받는 관계가 아님
 */

public class ChatReceviveServer extends Thread {
	ServerMgr mgr;
	
	public ChatReceviveServer(ServerMgr mgr) {
		this.mgr = mgr;
	}

	@Override
	public void run() {
		try {
			// 접속이 끝나는 그날까지 돈다.
			ResponseMsg msg = null;
			while (!isInterrupted() && (msg = (ResponseMsg) mgr.getOisServer().readObject()) != null)
				getMessage(msg);
		} // try
		catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( run() )");
			Manager.getMgr().MessageGUI("서버가 없습니다.",  JOptionPane.WARNING_MESSAGE);
		} // end catch
		finally{
			//리스트 내용 통째로 날리기
			Manager.getMgr().connectInit();
			Manager.getMgr().listClear();
			mgr.end();
		}// finally
	}// run

	// 서버에서 응답받은 메세지
	private void getMessage(ResponseMsg msg) {
			String resCode = msg.getResCode();
			//로그인
			if(resCode.equals(Message.LOGIN))
				receive((ResLoginMsg) msg);
			//로그아웃
			else if(resCode.equals(Message.LOGOUT))
				receive((ResLogOutMsg) msg);
			//에러
			else if(resCode.equals(Message.ERROR))
				receive((ResErrorMsg) msg);
			//채팅
			else if(resCode.equals(Message.CHAT))
				receive((ResChatMsg)msg);
			//공지사항
			else if(resCode.equals(Message.NOTICE))
				receive((ResNoticeMsg)msg);
			//강제퇴장
			else if(resCode.equals(Message.CERCION))
				receive((ResCercionMsg) msg);
			//파일서버
			else if(resCode.equals(Message.FILEUPLOAD) || resCode.equals(Message.FILEDOWN))
				receive((ResFileReqMsg) msg);
			//파일 업데이트
			else if(resCode.equals(Message.FILEUPDATE))
				receive((ResFileUpdateMsg) msg);
	}// getMessage

	// 파일 업데이트
	private void receive(ResFileUpdateMsg msg) {
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		Manager.getMgr().fileUpdate(msg.getFileName());//파일 업데이트
	}// receive : ResFileUpdateMsg

	//파일 서버
	private void receive(ResFileReqMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		UI.getUI().ouputMsg(msg.getPort() + " : port 번호");
		String fPort = msg.getPort();
		
		//파일 보내기 시작
		if(msg.getResCode().equals(Message.FILEUPLOAD))
			mgr.fileUpload(fPort);
		else
			mgr.fileDown(fPort);
	}// receive : ResFileReqMsg
	
	// 공지사항
	private void receive(ResNoticeMsg msg) {
		// 응답받은 번호
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		// 서버에서 내려 받은
		UI.getUI().ouputMsg(msg.getString());

		Manager.getMgr().receiveChatMsg(msg.getString());
	}//receive : ResNoticeMsg
	
	// 로그인  메세지
	private void receive(ResLoginMsg msg) {
		// 응답받은 번호
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		//리스트 추가
		Manager.getMgr().userUpdate(msg.getUserName());
		Manager.getMgr().fileUpdate(msg.getFileName());
	}// receive : ResNoticeMsg
	
	// 로그아웃 메세지
	private void receive(ResLogOutMsg msg) {
		// 응답받은 번호
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		//내가 아웃이라면
		if(msg.getString().equals(mgr.getUserID()))
			interrupt();
		else
		//리스트 삭제
		Manager.getMgr().userRemove(msg.getString());
	}// receive : ResNoticeMsg
	
	//강제퇴장
	private void receive(ResCercionMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		
		//재 접속 가능
		Manager.getMgr().MessageGUI("강제퇴장 당하셨습니다.",  JOptionPane.WARNING_MESSAGE);
		interrupt();
	}//receive : ResCercionMsg

	// 에러 메세지
	private void receive(ResErrorMsg msg) {
		// 응답받은 번호
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		// 서버에서 내려 받은
		UI.getUI().ouputMsg(msg.getString());
		
		//경고 메세지
		Manager.getMgr().MessageGUI(msg.getString(),  JOptionPane.WARNING_MESSAGE);
		
		//로그인에서 에러가 나면 
		if(msg.getReqCode().equals(Message.LOGIN))
		{
			//재 접속 가능
			interrupt();
		}//end if
		//파일 업로드에서 에러 메세지를 받았을 경우
		else if(msg.getReqCode().equals(Message.FILEUPLOAD))
		{
			//딱히 문제 없다.
		}//end else if
	}// receive : ResNoticeMsg
	
	// 채팅 메세지
	private void receive(ResChatMsg msg) {
		// 응답받은 번호
		UI.getUI().ouputMsg(msg.getResCode() + " : 응답코드");
		// 서버에서 내려 받은
		UI.getUI().ouputMsg(msg.getString());
		Manager.getMgr().receiveChatMsg(msg.getString());
	}//receive : ResChatMsg
}

package server;

import java.net.Socket;
import java.util.Date;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * FileServer
 * 클라이언트에서 파일을 제외한 기본적인 응답처리 서비
 */

public class ChatServer extends Server{
	//로그인 버그 예외처리
	private boolean loginException;
	
	public ChatServer(Socket s, ServerMgr mgr) {
		super(s, mgr);
		loginException = false;
	}//ChatServer

	@Override
	public void run() {
		// 정보 읽어오기
		RequestMsg msg = null;
		try {
			while ((msg = (RequestMsg) getOis().readObject()) != null)
				reqMessage(msg);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( run() )");
			
			//리스트에서 현재 소켓을 삭제
			getSMgr().removeSock(this);
			
			//동일 아이디로 소켓이 종료 되었을 경우 로그인이 되엇다는 것 조차 알지 못하기 때문에 필요 없다.
			if(loginException)
				return; 
			
			//소켓 연결 끊겼을 때 퇴장했다는 것을 모두에게 알려주기 위한 예외처리
			//============================================================================
			UI.getUI().ouputMsg(new Date().toString() + "/" + getSockName() + " : 종료 요청");
			// 로그아웃 알림
			getSMgr().sendAll(Message.LOGOUT, getSockName());
			//공지사항
			String strMsg = "<" + getSockName() + ">" + "님이 퇴장하셨습니다.";
			Manager.getMgr().sendAll(Message.NOTICE, strMsg);
			// 유저 정보 삭제
			Manager.getMgr().receiveUser(getSockName());
			//============================================================================
			
		}finally {
			// 소켓 종료
			end();
		}//finally
	}// run


	// ===========================================================================================================
	// 클라이언트에서 받은 메세지 읽기
	//요청 값만 읽기
	// ===========================================================================================================
	@Override
	protected void reqMessage(RequestMsg msg) {
		try {
			//요청번호 받기
			String reqCode = msg.getReqCode();
			//로그인
			if(reqCode.equals(Message.LOGIN))
				receiveLogin((RequestMsg) msg);
			//로그아웃
			else if(reqCode.equals(Message.LOGOUT))
				receiveLogOut( getSockName() );
			//채팅
			else if(reqCode.equals(Message.CHAT))
				receiveChat((RequestMsg) msg);
			//파일 
			else if(reqCode.equals(Message.FILEUPLOAD) || reqCode.equals(Message.FILEDOWN))
				receiveFile((RequestMsg) msg);
		} // end try
		catch (Exception e) {
		} // end catch
	}// reqMessage
	
	//로그인 요청이 들어왔을때
	private void receiveLogin(RequestMsg msg) {
		// 접속 아이디 저장
		setSockName(msg.getString());

		UI.getUI().ouputMsg(new Date().toString() + "/" + getSockName() + " : 로그인 요청");
		// 서버에 동일 아이디가 존재한다면 에러 리턴
		if (getSMgr().getSocket(this, getSockName()) != null) {
			//로그인에러 발생
			loginException = true;
			//에러발생
			sendError(msg.getReqCode(), "동일 접속이 불가합니다.");
			return;
		} // end if
		//접속자 리스트 추가
		Manager.getMgr().receiveUser(getSockName());
		
		//로그인 알림
		getSMgr().sendAll(Message.LOGIN, getSockName());
		//공지사항 전송
		String strMsg = "<" + msg.getString() + ">" + "님이 입장하였습니다.";
		Manager.getMgr().sendAll(Message.NOTICE, strMsg);
	}//receiveLogin

	// 정상 종료 알림
	public void receiveLogOut(String userName) {
		UI.getUI().ouputMsg(new Date().toString() + "/" + userName + " : 종료 요청");
		// 종료 응답.
		sendLogOut(userName);
	}// receiveLogOut
	
	//채팅
	private void receiveChat(RequestMsg msg){
		String str = msg.getString();
		//비밀 귀속말인지 판별하기 true라면 귓속말 보내기 (함수 안에서 보내고 있음.
		if(!isSecret(str)){
			UI.getUI().ouputMsg(new Date().toString() + " : 채팅 요청");
			//보낸 사람 이름 붙이기.
			String sendUser = getSockName()  + " : ";
			
			//채팅 알림번호, 채팅내용 ( 보낸 사람 : 내용 
			getSMgr().sendAll(Message.CHAT, sendUser + str);
			// 서버창에 쓸 채팅내용  ( 보낸 사람 : 내용 
			Manager.getMgr().receiveChatMsg(sendUser + str);
		}//end if
	}//receiveChat
	
	// 파일요청이 들어왔을때
	private void receiveFile(RequestMsg msg) {
		UI.getUI().ouputMsg(new Date().toString() + " : 파일 서버 요청");
		
		//파일 서버 알려주기
		sendFile(msg);
	}//receiveFile
	
	// ===========================================================================================================
	// 클라이언트에게 보낼 메세지
	// ===========================================================================================================
	//파일 서버 알려주기.
	public void sendFile(RequestMsg msg){
		ResFileReqMsg rfm = new ResFileReqMsg();
		rfm.setResCode(msg.getReqCode()); // 파일
		rfm.setPort(getSMgr().getfilePort());
		writeObject(rfm);
	}// sendFile
	
	//업데이트 된 파일 알려주기
	public void sendFileUpdate(){
		ResFileUpdateMsg rfum = new ResFileUpdateMsg();
		rfum.setResCode(Message.FILEUPDATE);
		rfum.setFileName(getSMgr().getFileAllName());	//파일리스트
		writeObject(rfum);
	}// sendFileUpdate
	
	//공지사항
	public void sendNotice(String str) {
		UI.getUI().ouputMsg(new Date().toString() + " : 공지사항요청");

		ResNoticeMsg rnm = new ResNoticeMsg();
		rnm.setResCode(Message.NOTICE); // 공지사항
		rnm.setString("::::::: " + str + " :::::::"); // 모두에게 알릴 메세지
		writeObject(rnm);
	}// sendNotice

	// 접속 알림 전송
	public void sendLogin(String[] users,String[] files) {
		ResLoginMsg rlm = new ResLoginMsg();
		rlm.setResCode(Message.LOGIN);	//정상 로그인 알림 번호
		rlm.setUserName(users);			//로그인 한 유저들
		rlm.setFileName(files);			//파일들
		writeObject(rlm);
	}// sendLogin
	
	// 접속 종료 알림
	public void sendLogOut(String user) {
		ResLogOutMsg rlm = new ResLogOutMsg();
		rlm.setResCode(Message.LOGOUT); // 정상종료 알림 번호
		rlm.setString(user); // 로그아웃 한 유저
		writeObject(rlm);
	}// sendLogOut.
	
	//채팅  전송
	public void sendChat(String str) {
		//정상 채팅 알림
		ResChatMsg rlm = new ResChatMsg();
		rlm.setResCode(Message.CHAT);	//채팅 정상 알림 번호
		rlm.setString(str);		//채팅 내용
		writeObject(rlm);
	}// sendChat
	
	//강제퇴장
	public void sendCercion(){
		//강제퇴장 알림
		ResCercionMsg rcm = new ResCercionMsg();
		rcm.setResCode(Message.CERCION);
		writeObject(rcm);
	}// sendCercion
	
	//==========================================================================
	
	//귓속말인지 판별하기. true라면 귓속말 보내기
	private boolean isSecret(String str){
		// 귓속말을 시도 하였는가.
		if (str.contains(">>")) {
			// 아이디 추출하기
			String id = str.split(">")[0];
			// 해당 아이디의 소켓이 존재하는지.
			if (getSMgr().getSocket(id) != null) {
				// 뒷 내용 추출하기
				int index = str.lastIndexOf(">") + 1;
				String temp = str.substring(index, str.length());
				// 누가 보내는지 연결하기
				String strSecret = getSockName() + ">>" + temp;
				
				//나한테도 보내기 (<귓> : 유저 >>  보낸 내용
				sendChat( "<귓> " + str);
				// 귀속말 하기 ( 내용, 보낼 아이디)
				UI.getUI().ouputMsg(new Date().toString() + " : 귓속말");
				getSMgr().sendSecret(id,  "<귓> " + strSecret);
				//귀속말 성공
				return true;
			}//end if
		}//end if
		
		//귓속말 실패
		return false;
	}//isSecret
}

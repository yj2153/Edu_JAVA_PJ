package server;

import java.io.File;
import java.io.Serializable;

/*
 *   Message 
 *   요청, 응답 코드
 *   
 *   나도 헷갈림. (ㅋ...
 */

public class Message implements Serializable {
	private static final long serialVersionUID = 20L;
	
	public static String LOGIN = "login";
	public static String LOGOUT = "logout";
	public static String CHAT = "chat";
	public static String NOTICE = "notice";
	public static String ERROR = "error";
	public static String CERCION = "cercion";
	
	public static String FILEUPLOAD = "fileupload";
	public static String FILEDOWN = "filedown";
	public static String FILEUPDATE = "fileupdate";
}

//클라이언트에서 서버로
//==========================================================================================================
//요청클래스
//==========================================================================================================
class RequestMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String reqCode;		//요청 코드
	private String string;		//보낼내용
	
	public String getReqCode() {		return reqCode;	}
	public void setReqCode(String reqCode) {		this.reqCode = reqCode;	}
	public String getString() {		return string;	}
	public void setString(String userName) {		string = userName;	}
}

//파일 요청 (  RequestMsg 상속 
class ReqFileMsg extends RequestMsg implements Serializable{
	private static final long serialVersionUID = 2L;
	private File file;
	private int fileNum;
	
	public void setFile(File f){  file = f; }
	public File getFile(){ return file; }
	public void setFileNum(int f){  fileNum = f; }
	public int getFileNum(){ return fileNum; }
}


//서버에서 클라이언트로 
//==========================================================================================================
//부모 클래스
//==========================================================================================================
//응답
class ResponseMsg implements Serializable {
	private static final long serialVersionUID = 10L;
	private String resCode;		//응답코드
	private String string;	//보낼 내용
		
	public String getString() {		return string;	}
	public void setString(String string) {		this.string = string;	}
	public String getResCode() {		return resCode;	}
	public void setResCode(String resCode) {		this.resCode = resCode;	}
}
//==========================================================================================================
//부모를 상속 받은 자식 클래스
//==========================================================================================================
//채팅내용 ( ResponseMsg 상속
class ResChatMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 11L;
}

//로그인 ( ResponseMsg 상속
class ResLoginMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 12L;
	String[] userName;
	String[] fileName;
	
	public String[] getUserName() {		return userName;	}
	public void setUserName(String[] userName) {		this.userName = userName;	}
	public String[] getFileName() {		return fileName;	}
	public void setFileName(String[] fileName) {		this.fileName = fileName;	}
}
//로그아웃 ( ResponseMsg 상속
class ResLogOutMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 13L;
}

//에러메세지 ( ResponseMsg 상속
class ResErrorMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 14L;
	private String reqCode;		//요청한 코드
	
	public String getReqCode() {		return reqCode;	}
	public void setReqCode(String reqCode) {		this.reqCode = reqCode;	}
}

//공지사항 ( ResponseMsg 상속
class ResNoticeMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 15L;
}

//파일 서버 요청 ( ResponseMsg 상속
class ResFileReqMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 16L;
	private String port;

	public String getPort() {		return port;	}
	public void setPort(String port) {		this.port = port;	}
}

//강제퇴장 ( ResponseMsg 상속
class ResCercionMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 17L;
}

//파일 다운로드
class ResFileMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 18L;
	private File file;

	public void setFile(File f){  file = f; }
	public File getFile(){ return file; }

}

//파일 갱신 ( ResponseMsg 상속
class ResFileUpdateMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 19L;
	String[] fileName;
	
	public String[] getFileName() {		return fileName;	}
	public void setFileName(String[] fileName) {		this.fileName = fileName;	}
}

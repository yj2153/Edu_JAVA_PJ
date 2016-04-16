package server;

import java.io.File;
import java.io.Serializable;

/*
 *   Message 
 *   ��û, ���� �ڵ�
 *   
 *   ���� �򰥸�. (��...
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

//Ŭ���̾�Ʈ���� ������
//==========================================================================================================
//��ûŬ����
//==========================================================================================================
class RequestMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	private String reqCode;		//��û �ڵ�
	private String string;		//��������
	
	public String getReqCode() {		return reqCode;	}
	public void setReqCode(String reqCode) {		this.reqCode = reqCode;	}
	public String getString() {		return string;	}
	public void setString(String userName) {		string = userName;	}
}

//���� ��û (  RequestMsg ��� 
class ReqFileMsg extends RequestMsg implements Serializable{
	private static final long serialVersionUID = 2L;
	private File file;
	private int fileNum;
	
	public void setFile(File f){  file = f; }
	public File getFile(){ return file; }
	public void setFileNum(int f){  fileNum = f; }
	public int getFileNum(){ return fileNum; }
}


//�������� Ŭ���̾�Ʈ�� 
//==========================================================================================================
//�θ� Ŭ����
//==========================================================================================================
//����
class ResponseMsg implements Serializable {
	private static final long serialVersionUID = 10L;
	private String resCode;		//�����ڵ�
	private String string;	//���� ����
		
	public String getString() {		return string;	}
	public void setString(String string) {		this.string = string;	}
	public String getResCode() {		return resCode;	}
	public void setResCode(String resCode) {		this.resCode = resCode;	}
}
//==========================================================================================================
//�θ� ��� ���� �ڽ� Ŭ����
//==========================================================================================================
//ä�ó��� ( ResponseMsg ���
class ResChatMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 11L;
}

//�α��� ( ResponseMsg ���
class ResLoginMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 12L;
	String[] userName;
	String[] fileName;
	
	public String[] getUserName() {		return userName;	}
	public void setUserName(String[] userName) {		this.userName = userName;	}
	public String[] getFileName() {		return fileName;	}
	public void setFileName(String[] fileName) {		this.fileName = fileName;	}
}
//�α׾ƿ� ( ResponseMsg ���
class ResLogOutMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 13L;
}

//�����޼��� ( ResponseMsg ���
class ResErrorMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 14L;
	private String reqCode;		//��û�� �ڵ�
	
	public String getReqCode() {		return reqCode;	}
	public void setReqCode(String reqCode) {		this.reqCode = reqCode;	}
}

//�������� ( ResponseMsg ���
class ResNoticeMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 15L;
}

//���� ���� ��û ( ResponseMsg ���
class ResFileReqMsg extends ResponseMsg implements Serializable{
	private static final long serialVersionUID = 16L;
	private String port;

	public String getPort() {		return port;	}
	public void setPort(String port) {		this.port = port;	}
}

//�������� ( ResponseMsg ���
class ResCercionMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 17L;
}

//���� �ٿ�ε�
class ResFileMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 18L;
	private File file;

	public void setFile(File f){  file = f; }
	public File getFile(){ return file; }

}

//���� ���� ( ResponseMsg ���
class ResFileUpdateMsg extends ResponseMsg implements Serializable {
	private static final long serialVersionUID = 19L;
	String[] fileName;
	
	public String[] getFileName() {		return fileName;	}
	public void setFileName(String[] fileName) {		this.fileName = fileName;	}
}

package server;

import java.net.Socket;
import java.util.Date;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * FileServer
 * Ŭ���̾�Ʈ���� ������ ������ �⺻���� ����ó�� ����
 */

public class ChatServer extends Server{
	//�α��� ���� ����ó��
	private boolean loginException;
	
	public ChatServer(Socket s, ServerMgr mgr) {
		super(s, mgr);
		loginException = false;
	}//ChatServer

	@Override
	public void run() {
		// ���� �о����
		RequestMsg msg = null;
		try {
			while ((msg = (RequestMsg) getOis().readObject()) != null)
				reqMessage(msg);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( run() )");
			
			//����Ʈ���� ���� ������ ����
			getSMgr().removeSock(this);
			
			//���� ���̵�� ������ ���� �Ǿ��� ��� �α����� �Ǿ��ٴ� �� ���� ���� ���ϱ� ������ �ʿ� ����.
			if(loginException)
				return; 
			
			//���� ���� ������ �� �����ߴٴ� ���� ��ο��� �˷��ֱ� ���� ����ó��
			//============================================================================
			UI.getUI().ouputMsg(new Date().toString() + "/" + getSockName() + " : ���� ��û");
			// �α׾ƿ� �˸�
			getSMgr().sendAll(Message.LOGOUT, getSockName());
			//��������
			String strMsg = "<" + getSockName() + ">" + "���� �����ϼ̽��ϴ�.";
			Manager.getMgr().sendAll(Message.NOTICE, strMsg);
			// ���� ���� ����
			Manager.getMgr().receiveUser(getSockName());
			//============================================================================
			
		}finally {
			// ���� ����
			end();
		}//finally
	}// run


	// ===========================================================================================================
	// Ŭ���̾�Ʈ���� ���� �޼��� �б�
	//��û ���� �б�
	// ===========================================================================================================
	@Override
	protected void reqMessage(RequestMsg msg) {
		try {
			//��û��ȣ �ޱ�
			String reqCode = msg.getReqCode();
			//�α���
			if(reqCode.equals(Message.LOGIN))
				receiveLogin((RequestMsg) msg);
			//�α׾ƿ�
			else if(reqCode.equals(Message.LOGOUT))
				receiveLogOut( getSockName() );
			//ä��
			else if(reqCode.equals(Message.CHAT))
				receiveChat((RequestMsg) msg);
			//���� 
			else if(reqCode.equals(Message.FILEUPLOAD) || reqCode.equals(Message.FILEDOWN))
				receiveFile((RequestMsg) msg);
		} // end try
		catch (Exception e) {
		} // end catch
	}// reqMessage
	
	//�α��� ��û�� ��������
	private void receiveLogin(RequestMsg msg) {
		// ���� ���̵� ����
		setSockName(msg.getString());

		UI.getUI().ouputMsg(new Date().toString() + "/" + getSockName() + " : �α��� ��û");
		// ������ ���� ���̵� �����Ѵٸ� ���� ����
		if (getSMgr().getSocket(this, getSockName()) != null) {
			//�α��ο��� �߻�
			loginException = true;
			//�����߻�
			sendError(msg.getReqCode(), "���� ������ �Ұ��մϴ�.");
			return;
		} // end if
		//������ ����Ʈ �߰�
		Manager.getMgr().receiveUser(getSockName());
		
		//�α��� �˸�
		getSMgr().sendAll(Message.LOGIN, getSockName());
		//�������� ����
		String strMsg = "<" + msg.getString() + ">" + "���� �����Ͽ����ϴ�.";
		Manager.getMgr().sendAll(Message.NOTICE, strMsg);
	}//receiveLogin

	// ���� ���� �˸�
	public void receiveLogOut(String userName) {
		UI.getUI().ouputMsg(new Date().toString() + "/" + userName + " : ���� ��û");
		// ���� ����.
		sendLogOut(userName);
	}// receiveLogOut
	
	//ä��
	private void receiveChat(RequestMsg msg){
		String str = msg.getString();
		//��� �ͼӸ����� �Ǻ��ϱ� true��� �ӼӸ� ������ (�Լ� �ȿ��� ������ ����.
		if(!isSecret(str)){
			UI.getUI().ouputMsg(new Date().toString() + " : ä�� ��û");
			//���� ��� �̸� ���̱�.
			String sendUser = getSockName()  + " : ";
			
			//ä�� �˸���ȣ, ä�ó��� ( ���� ��� : ���� 
			getSMgr().sendAll(Message.CHAT, sendUser + str);
			// ����â�� �� ä�ó���  ( ���� ��� : ���� 
			Manager.getMgr().receiveChatMsg(sendUser + str);
		}//end if
	}//receiveChat
	
	// ���Ͽ�û�� ��������
	private void receiveFile(RequestMsg msg) {
		UI.getUI().ouputMsg(new Date().toString() + " : ���� ���� ��û");
		
		//���� ���� �˷��ֱ�
		sendFile(msg);
	}//receiveFile
	
	// ===========================================================================================================
	// Ŭ���̾�Ʈ���� ���� �޼���
	// ===========================================================================================================
	//���� ���� �˷��ֱ�.
	public void sendFile(RequestMsg msg){
		ResFileReqMsg rfm = new ResFileReqMsg();
		rfm.setResCode(msg.getReqCode()); // ����
		rfm.setPort(getSMgr().getfilePort());
		writeObject(rfm);
	}// sendFile
	
	//������Ʈ �� ���� �˷��ֱ�
	public void sendFileUpdate(){
		ResFileUpdateMsg rfum = new ResFileUpdateMsg();
		rfum.setResCode(Message.FILEUPDATE);
		rfum.setFileName(getSMgr().getFileAllName());	//���ϸ���Ʈ
		writeObject(rfum);
	}// sendFileUpdate
	
	//��������
	public void sendNotice(String str) {
		UI.getUI().ouputMsg(new Date().toString() + " : �������׿�û");

		ResNoticeMsg rnm = new ResNoticeMsg();
		rnm.setResCode(Message.NOTICE); // ��������
		rnm.setString("::::::: " + str + " :::::::"); // ��ο��� �˸� �޼���
		writeObject(rnm);
	}// sendNotice

	// ���� �˸� ����
	public void sendLogin(String[] users,String[] files) {
		ResLoginMsg rlm = new ResLoginMsg();
		rlm.setResCode(Message.LOGIN);	//���� �α��� �˸� ��ȣ
		rlm.setUserName(users);			//�α��� �� ������
		rlm.setFileName(files);			//���ϵ�
		writeObject(rlm);
	}// sendLogin
	
	// ���� ���� �˸�
	public void sendLogOut(String user) {
		ResLogOutMsg rlm = new ResLogOutMsg();
		rlm.setResCode(Message.LOGOUT); // �������� �˸� ��ȣ
		rlm.setString(user); // �α׾ƿ� �� ����
		writeObject(rlm);
	}// sendLogOut.
	
	//ä��  ����
	public void sendChat(String str) {
		//���� ä�� �˸�
		ResChatMsg rlm = new ResChatMsg();
		rlm.setResCode(Message.CHAT);	//ä�� ���� �˸� ��ȣ
		rlm.setString(str);		//ä�� ����
		writeObject(rlm);
	}// sendChat
	
	//��������
	public void sendCercion(){
		//�������� �˸�
		ResCercionMsg rcm = new ResCercionMsg();
		rcm.setResCode(Message.CERCION);
		writeObject(rcm);
	}// sendCercion
	
	//==========================================================================
	
	//�ӼӸ����� �Ǻ��ϱ�. true��� �ӼӸ� ������
	private boolean isSecret(String str){
		// �ӼӸ��� �õ� �Ͽ��°�.
		if (str.contains(">>")) {
			// ���̵� �����ϱ�
			String id = str.split(">")[0];
			// �ش� ���̵��� ������ �����ϴ���.
			if (getSMgr().getSocket(id) != null) {
				// �� ���� �����ϱ�
				int index = str.lastIndexOf(">") + 1;
				String temp = str.substring(index, str.length());
				// ���� �������� �����ϱ�
				String strSecret = getSockName() + ">>" + temp;
				
				//�����׵� ������ (<��> : ���� >>  ���� ����
				sendChat( "<��> " + str);
				// �ͼӸ� �ϱ� ( ����, ���� ���̵�)
				UI.getUI().ouputMsg(new Date().toString() + " : �ӼӸ�");
				getSMgr().sendSecret(id,  "<��> " + strSecret);
				//�ͼӸ� ����
				return true;
			}//end if
		}//end if
		
		//�ӼӸ� ����
		return false;
	}//isSecret
}

package server;

import java.io.IOException;

import javax.swing.JOptionPane;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * ChatReceviveServer
 * �������� ���� ������ ó���ϴ� Ŭ����
 * 
 * ServerMgr �� ��ӹ޴� ���谡 �ƴ�
 */

public class ChatReceviveServer extends Thread {
	ServerMgr mgr;
	
	public ChatReceviveServer(ServerMgr mgr) {
		this.mgr = mgr;
	}

	@Override
	public void run() {
		try {
			// ������ ������ �׳����� ����.
			ResponseMsg msg = null;
			while (!isInterrupted() && (msg = (ResponseMsg) mgr.getOisServer().readObject()) != null)
				getMessage(msg);
		} // try
		catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( run() )");
			Manager.getMgr().MessageGUI("������ �����ϴ�.",  JOptionPane.WARNING_MESSAGE);
		} // end catch
		finally{
			//����Ʈ ���� ��°�� ������
			Manager.getMgr().connectInit();
			Manager.getMgr().listClear();
			mgr.end();
		}// finally
	}// run

	// �������� ������� �޼���
	private void getMessage(ResponseMsg msg) {
			String resCode = msg.getResCode();
			//�α���
			if(resCode.equals(Message.LOGIN))
				receive((ResLoginMsg) msg);
			//�α׾ƿ�
			else if(resCode.equals(Message.LOGOUT))
				receive((ResLogOutMsg) msg);
			//����
			else if(resCode.equals(Message.ERROR))
				receive((ResErrorMsg) msg);
			//ä��
			else if(resCode.equals(Message.CHAT))
				receive((ResChatMsg)msg);
			//��������
			else if(resCode.equals(Message.NOTICE))
				receive((ResNoticeMsg)msg);
			//��������
			else if(resCode.equals(Message.CERCION))
				receive((ResCercionMsg) msg);
			//���ϼ���
			else if(resCode.equals(Message.FILEUPLOAD) || resCode.equals(Message.FILEDOWN))
				receive((ResFileReqMsg) msg);
			//���� ������Ʈ
			else if(resCode.equals(Message.FILEUPDATE))
				receive((ResFileUpdateMsg) msg);
	}// getMessage

	// ���� ������Ʈ
	private void receive(ResFileUpdateMsg msg) {
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		Manager.getMgr().fileUpdate(msg.getFileName());//���� ������Ʈ
	}// receive : ResFileUpdateMsg

	//���� ����
	private void receive(ResFileReqMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		UI.getUI().ouputMsg(msg.getPort() + " : port ��ȣ");
		String fPort = msg.getPort();
		
		//���� ������ ����
		if(msg.getResCode().equals(Message.FILEUPLOAD))
			mgr.fileUpload(fPort);
		else
			mgr.fileDown(fPort);
	}// receive : ResFileReqMsg
	
	// ��������
	private void receive(ResNoticeMsg msg) {
		// ������� ��ȣ
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		// �������� ���� ����
		UI.getUI().ouputMsg(msg.getString());

		Manager.getMgr().receiveChatMsg(msg.getString());
	}//receive : ResNoticeMsg
	
	// �α���  �޼���
	private void receive(ResLoginMsg msg) {
		// ������� ��ȣ
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		//����Ʈ �߰�
		Manager.getMgr().userUpdate(msg.getUserName());
		Manager.getMgr().fileUpdate(msg.getFileName());
	}// receive : ResNoticeMsg
	
	// �α׾ƿ� �޼���
	private void receive(ResLogOutMsg msg) {
		// ������� ��ȣ
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		//���� �ƿ��̶��
		if(msg.getString().equals(mgr.getUserID()))
			interrupt();
		else
		//����Ʈ ����
		Manager.getMgr().userRemove(msg.getString());
	}// receive : ResNoticeMsg
	
	//��������
	private void receive(ResCercionMsg msg){
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		
		//�� ���� ����
		Manager.getMgr().MessageGUI("�������� ���ϼ̽��ϴ�.",  JOptionPane.WARNING_MESSAGE);
		interrupt();
	}//receive : ResCercionMsg

	// ���� �޼���
	private void receive(ResErrorMsg msg) {
		// ������� ��ȣ
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		// �������� ���� ����
		UI.getUI().ouputMsg(msg.getString());
		
		//��� �޼���
		Manager.getMgr().MessageGUI(msg.getString(),  JOptionPane.WARNING_MESSAGE);
		
		//�α��ο��� ������ ���� 
		if(msg.getReqCode().equals(Message.LOGIN))
		{
			//�� ���� ����
			interrupt();
		}//end if
		//���� ���ε忡�� ���� �޼����� �޾��� ���
		else if(msg.getReqCode().equals(Message.FILEUPLOAD))
		{
			//���� ���� ����.
		}//end else if
	}// receive : ResNoticeMsg
	
	// ä�� �޼���
	private void receive(ResChatMsg msg) {
		// ������� ��ȣ
		UI.getUI().ouputMsg(msg.getResCode() + " : �����ڵ�");
		// �������� ���� ����
		UI.getUI().ouputMsg(msg.getString());
		Manager.getMgr().receiveChatMsg(msg.getString());
	}//receive : ResChatMsg
}

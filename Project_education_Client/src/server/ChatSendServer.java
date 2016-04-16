package server;

import javax.swing.JOptionPane;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * ChatSendServer
 * ������ ��û ������ �κ�
 * ó���� ���Ƽ� �ϳ��� Ŭ������ �и� �Ͽ�����
 * �����ϴ� �ſ� ª����. ( ServerMgr�� ���ϱ�.. ������.
 * 
  * ServerMgr �� ��ӹ޴� ���谡 �ƴ�
 */

public class ChatSendServer {
	ServerMgr mgr;

	public ChatSendServer(ServerMgr o) {
		this.mgr = o;
	}

	// =======================================================================================================
	// ������ ���� �κ�
	// =======================================================================================================
	public void setMessage(String id, String str) {
		
		// �α��� ��û //�α׾ƿ� ��û //ä�� ��û
		RequestMsg msg = new RequestMsg();
		msg.setReqCode(id); // ��û��ȣ ����
		// �α���,�α׾ƿ� =���̵� / ä�� = ���� / ���� =
		msg.setString(str); //
		writeObject(msg); // ���� ����
		
	}// setMessage
	
	// ������ ����
	public void writeObject(RequestMsg msg) {
		try {
			mgr.getOosServer().writeObject(msg);
			mgr.getOosServer().flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( writeObject() )");
			Manager.getMgr().MessageGUI("������ �����ϴ�.",  JOptionPane.WARNING_MESSAGE);
		}//end catch
	}// writeObject
}

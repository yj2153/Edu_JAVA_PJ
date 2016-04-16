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
 * CatchServer , FileServer�� ��ӹ޴� �θ� Ŭ����
 * �������� ���� ���Ϲޱ⸦ �ϳ��� �������.
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
			// ���� ���� �޾ƿ���
			fromClient = sock.getInputStream();
			toClient = sock.getOutputStream();
			
			oos = new ObjectOutputStream(toClient);
			ois = new ObjectInputStream(fromClient);
		} // end while
		catch (Exception e) {		
			//	 ���� ���� 
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( Server() )");
		} // end catch
	}// Server
	
	//���� 
	protected void end(){
		try {		// ���� ����
			if (ois != null)				ois.close();
			if (oos != null)				oos.close();
			if (sock != null)				sock.close();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( end() )");
		}
	}//end 
	
	//�����ϱ�
	protected void writeObject(ResponseMsg msg) {
		try {
			// �����ϱ�
			oos.writeObject(msg);
			// ��������
			oos.flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( writeObject() )");
		}//end catch
	}//writeObject
	
	// �����޼��� ����
	protected void sendError(String id, String error) {
		UI.getUI().ouputMsg(new Date().toString() + " : �����߻�");
		ResErrorMsg rlm = new ResErrorMsg();
		rlm.setReqCode(id); // Ŭ�󿡼� ��û�� ��ȣ
		rlm.setResCode(Message.ERROR); // ���� �˸� ��ȣ
		rlm.setString(error); // ���� ����
		writeObject(rlm);
	}// sendError

	//Ŭ���̾�Ʈ���� ���� �޼���
	abstract protected void reqMessage(RequestMsg msg);

	public Socket getSock() {		return sock;	}
	public ObjectInputStream getOis() {		return ois;	}
	public ObjectOutputStream getOos() {		return oos;	}
	public OutputStream getToClient() {		return toClient;	}
	public ServerMgr getSMgr() {		return mgr;	}
	public String getSockName() { return sockName; }
	public void setSockName(String name) { sockName = name; }
	
}

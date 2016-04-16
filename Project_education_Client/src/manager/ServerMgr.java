package manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import common.UI;
import server.ChatReceviveServer;
import server.ChatSendServer;
import server.FileServer;
import server.Message;

/*
 * ServerMgr 
 * ������ �����ϴ� �κ�
 * ������ �����Ѵ�,
 * gui���� ��û�� ������ Manager�� ���ؼ� ���ͼ� ���� ������ ���
 */

public class ServerMgr {
	//���� ���� ����
	private static boolean SERVER = true;
	
	private Socket sock;
	private ObjectInputStream oisServer;
	private ObjectOutputStream oosServer;
	private String ip;
	private File upFile;
	private int downFileNum;
	
	private ChatSendServer sendServer;
	private ChatReceviveServer receiveServer;
	
	//���̵�
	private String id;
	
	//����������ġ
	private File fileFolder;
	//���� �Ͻ�����
	private boolean fileWait;
	// ���ϼ��� ���� ����
	private boolean fileConnect;
	
	public ServerMgr(){
		sendServer = new ChatSendServer(this);
		fileFolder = new File("file");
		fileWait = false;
		fileConnect = false;
	}
	
	// ������ ����
	public void connect(String ip, String port, String id) {
		//������ ��� ���� �ʰڴٸ�.
		if(!SERVER)
			return;
		
		try {
			this.ip = ip;
			this.id = id;
			sock = new Socket(ip, Integer.parseInt(port));
			UI.getUI().ouputMsg(sock + ": �����");
			OutputStream toServer = sock.getOutputStream();
			InputStream fromServer = sock.getInputStream();
			oosServer = new ObjectOutputStream(toServer);
			oisServer = new ObjectInputStream(fromServer);
			//�α��� �õ�
			sendServer.setMessage(Message.LOGIN, id);
			
			//�޴� ������ ����
			receiveServer = new ChatReceviveServer(this);
			receiveServer.start();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( connect() )");
			Manager.getMgr().connectInit();
			Manager.getMgr().MessageGUI("������ �����ϴ�.",  JOptionPane.WARNING_MESSAGE);
			end();
		}// end catch
	}// connect

	// ���� ����
	public void end() {
		try {
			if (oosServer != null)
				oosServer.close();
			if (oisServer != null)
				oisServer.close();
			if (sock != null)
				sock.close();
		} catch (IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( end() )");
		}//end catch

		UI.getUI().ouputMsg("���� ����");
	}// end
	
	
	//���� ���ε� �ϱ�
	public void fileUpload(String port){
		new FileServer(ip, port, upFile, Message.FILEUPLOAD).start();
	}
	//���ϴٿ�ε��ϱ�
	public void fileDown(String port){
		new FileServer	(ip, port, downFileNum, Message.FILEDOWN).start();
	}
	
	//������ ����
	public ChatSendServer getSend() { return sendServer; }
	//�޴� ����
	public ChatReceviveServer getReceive() { return receiveServer; }
		
	//���� ��Ʈ�� ����
	public ObjectInputStream getOisServer() {		return oisServer;	}
	public ObjectOutputStream getOosServer() {		return oosServer;	}
	
	//����
	public void setUpFile(File file) { upFile = file; }
	public void setDownFileNum(int downFileNum) {	this.downFileNum = downFileNum; 	}
	
	//��������
	public File getFileFolder() { return fileFolder; }

	//���� �Ͻ�����
	public boolean getFileWait() { return fileWait; }
	public void setFileWait(boolean b) { fileWait = b; }
	
	//���� ���̵�
	public String getUserID(){ return id; }
	
	//���ϼ��� �����
	public boolean getFileConnect() { return fileConnect; }
	public void setFileConnect(boolean b) { fileConnect = b; }
}

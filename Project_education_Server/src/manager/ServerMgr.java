package manager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import common.UI;
import server.ChatServer;
import server.FileServer;
import server.Message;

/*
 * ServerMgr
 * ChatServer�� FileServer�� ���� �ϴ� ServerMgr  Ŭ����.
 */

public class ServerMgr {
	//ä��, ����
	private ServerSocket chatServerSock;
	private ServerSocket fileServerSock;
	
	//������ Ŭ���̾�Ʈ ����
	private LinkedList<ChatServer> socketList;
	//������ ��ϵ� ���� ����
	private LinkedList<File> fileList;
	private File fileFolder;	//�������� ��ġ
	private String filePort;	//�������� ��ȣ
	
	//������
	public ServerMgr()
	{
		socketList = new LinkedList<>();
		fileList = new LinkedList<>();
		
		fileFolder = new File("file");
	}//ServerMgr
	
	//��Ʈ ����
	public void chatPort(String port){
		try {
			chatServerSock = new ServerSocket(Integer.parseInt(port));
			UI.getUI().ouputMsg(port + "  ä�� ���� ����");
			
			//���� ���� ���� �޾ƿ���.
			Thread sAccepte = new Thread(new Runnable() {
				public void run() {
					while (true) {
						Socket sock;
						try {
							sock = chatServerSock.accept();
							// Ŭ���̾�Ʈ ip ���
							UI.getUI().ouputMsg(sock.getInetAddress().getHostAddress() + ": �����");
							
							//Ŭ���̾�Ʈ ���� ����
							ChatServer mgr = new ChatServer(sock, ServerMgr.this);
							mgr.start();
							//����Ʈ�� �߰�
							socketList.add(mgr);
						} catch (IOException e) {
							UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( filePort() ) 1");
						}//end catch
					}//end while
				}//end run
			});
			sAccepte.start();
			
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( filePort() ) 2");
		}//end catch
	}// chatPort
	
	//����
	public void filePort(String port){
		try {
			filePort = port;
			fileServerSock = new ServerSocket(Integer.parseInt(filePort));
			UI.getUI().ouputMsg(filePort + " ���� ���� ����");
			//���� ���� ���� �޾ƿ���.
			Thread sAccepte = new Thread(new Runnable() {
				public void run() {
					while (true) {
						Socket sock;
						try {
							sock = fileServerSock.accept();
							// Ŭ���̾�Ʈ ip ���
							UI.getUI().ouputMsg(sock.getInetAddress().getHostAddress() + ": ���Ͽ����");
							//���� ���ϵ� ����
							new FileServer(sock, ServerMgr.this).start();
						} catch (IOException e) {
							UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( filePort() ) 1");
						}//end catch
					}//end while
				}//end run
			});
			sAccepte.start();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( filePort() ) 2");
		}//end catch
	}// filePort
	
	//=================================================================================================
	
	// ��ü���
	public void sendAll(String type, String str) {
		for (int i = 0; i < socketList.size(); i++) {
			// ä�ü��� ����
			ChatServer cs = socketList.get(i);
			System.out.println(cs.getSockName());
			
			// ��ο��� ������������
			if (type.equals(Message.NOTICE))
				cs.sendNotice(str);
			// ä��
			else if (type.equals(Message.CHAT))
				cs.sendChat(str);
			// �α���
			else if (type.equals(Message.LOGIN)) {
				String[] users = getUserAllName();
				String[] files = getFileAllName();
				cs.sendLogin(users, files);
			}//end else if
			// �α׾ƿ�
			else if (type.equals(Message.LOGOUT)) {
				cs.sendLogOut(str);
			}//end else if
			// ���Ͼ�����Ʈ �˸�
			else if (type.equals(Message.FILEUPDATE)) {
				cs.sendFileUpdate();
			}//end else if
		}//end for
	}//sendAll

	//�ӼӸ�
	public void sendSecret(String id, String str){
		for(int i = 0; i < socketList.size(); i++)
		{
			//ä�ü��� ����
			ChatServer cs = socketList.get(i);
			//�������� �ϴ� ������ �ִٸ�
			if(id != null && cs.getSockName().equals(id))
			{
				Manager.getMgr().receiveChatMsg(str);
				cs.sendChat(str);
				break;
			}//end if
		}//end for
	}//sendSecret
	
	//��������
	public void sendCercion(String userName){
		ChatServer cs = null;
		if((cs = getSocket(userName)) != null)
			cs.sendCercion();
	}//sendCercion
	
	//ã���� �ϴ� ä�ü��� ����
	public ChatServer getSocket(String id) {
		for (int i = 0; i < socketList.size(); i++) {
			// ä�ü��� ����
			ChatServer cs = socketList.get(i);
			// �ش� ������ �ƴϰ�, �ش� ������ id�� ã�� id���� ���� �ϴٸ�
			if (cs.getSockName().equals(id))
				return cs;
		}//end for
		return null;
	}//getSocket
	
	//�ش� Ŭ������ ���� �ϰ� ã���� �ϴ� ä�ü��� ����
	public ChatServer getSocket(ChatServer sc, String id){
		for(int i = 0; i < socketList.size(); i++){
			//ä�ü��� ����
			ChatServer cs = socketList.get(i);
			//sc�� �ƴϰ�  ID���� ���ٸ�
			if(!cs.equals(sc) && cs.getSockName().equals(id))
				return cs;
		}//end for
		return null;
	}//getSocket
	
	//�ش� ���� ����
	public boolean isFile(String name){
		for (int i = 0; i < fileList.size(); i++) {
			// ���� ���� ����
			if (fileList.get(i).getName().equals(name)) 
				return true;
		}//end for
		return false;
	}//isFile
	
	//���� ����
	public void removeSock(ChatServer cs){
		socketList.remove(cs);
	}//removeSock
	
	//������ ����Ʈ�� ����
	public String[] getUserAllName(){
		String[] listName = new String[socketList.size()];
		for (int i = 0; i < socketList.size(); i++)
			listName[i] = ((ChatServer) socketList.get(i)).getSockName();
		return listName;
	}//getUserAllName

	//���� ����Ʈ �� ����
	public String[] getFileAllName() {
		String[] listName = new String[fileList.size()];
		for (int i = 0; i < fileList.size(); i++)
			listName[i] = ((File) fileList.get(i)).getName();
		return listName;
	}// getFileAllName
	
	// ���� �߰�
	public void fileAdd(File f) {
			fileList.add(f);	
	}//fileAdd
	
	//index��°�� ���� ����
	public File getFile(int index) {
		return fileList.get(index);
	}//getFile

	public String getfilePort(){ return filePort; }
	public File getFileFolder(){ return fileFolder; }
	
}

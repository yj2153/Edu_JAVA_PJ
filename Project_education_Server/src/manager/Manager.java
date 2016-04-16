package manager;

import java.io.File;

/*
 * Manager
 * ServerGUIMgr �� ServerMgr �����ϴ� �̱��� Ŭ����
 * 
 * Gui���� ��û�� ������ Manager -> Server�� �Ѱ��ش�
 * Server���� ��û�� ������ Manager -> GUI�� �Ѿ��.
 * Server - > GUI, GUI -> Server ���� �������� �ʴ´�. �� manager�� ���ؾ����� ����
 */

public class Manager {
	private static Manager mgr;
	private ServerGUIMgr guiMgr;
	private ServerMgr serverMgr;

	private Manager() {
		guiMgr = new ServerGUIMgr();
		serverMgr = new ServerMgr();

		// ���� ã�Ƽ� ����ֱ�
		File[] fList = new File(serverMgr.getFileFolder().getAbsolutePath()).listFiles();
		for (File f : fList) {
			if (f.isFile()) 
				fileAdd(f);
		}//end for
	}//Manager

	public static Manager getMgr() {
		if (mgr == null)
			mgr = new Manager();
		return mgr;
	}

	// GUI ====================================================
	// ������ ����Ʈ �߰�/����
	public void receiveUser(String user) {
		synchronized (this) {
			guiMgr.receiveUser(user);
		}// end synchronized
	}

	// ����ä��â�� �� ����
	public void receiveChatMsg(String str) {
		synchronized (this) {
			guiMgr.receiveChatMsg(str);
		}//end synchronized
	}

	// ���ϸ���Ʈ �߰�
	public void fileAdd(File file){
		synchronized (this) {
			guiMgr.receiveFile(file.getName());
			serverMgr.fileAdd(file);
		}//end synchronized
	}

	// Server =================================================
	// ��Ʈ ����
	public void createPort(String cport, String fport) {
		serverMgr.chatPort(cport);
		serverMgr.filePort(fport);
	}

	// ��������
	public void sendAll(String type, String str) {
		// ����â�� �� ä�ó���
		Manager.getMgr().receiveChatMsg(str);
		// ��ο��� ������ ����
		serverMgr.sendAll(type, str);
	}
	//���� ����
	public void sendCercion(String userName){
		serverMgr.sendCercion(userName);
	}
}

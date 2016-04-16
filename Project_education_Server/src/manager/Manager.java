package manager;

import java.io.File;

/*
 * Manager
 * ServerGUIMgr 와 ServerMgr 관리하는 싱글톤 클래스
 * 
 * Gui에서 요청이 들어오면 Manager -> Server로 넘겨준다
 * Server에서 요청이 들어오면 Manager -> GUI로 넘어간다.
 * Server - > GUI, GUI -> Server 직접 연결하지 않는다. 꼭 manager를 통해야지만 가능
 */

public class Manager {
	private static Manager mgr;
	private ServerGUIMgr guiMgr;
	private ServerMgr serverMgr;

	private Manager() {
		guiMgr = new ServerGUIMgr();
		serverMgr = new ServerMgr();

		// 파일 찾아서 집어넣기
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
	// 접속자 리스트 추가/제거
	public void receiveUser(String user) {
		synchronized (this) {
			guiMgr.receiveUser(user);
		}// end synchronized
	}

	// 서버채팅창에 쓸 내용
	public void receiveChatMsg(String str) {
		synchronized (this) {
			guiMgr.receiveChatMsg(str);
		}//end synchronized
	}

	// 파일리스트 추가
	public void fileAdd(File file){
		synchronized (this) {
			guiMgr.receiveFile(file.getName());
			serverMgr.fileAdd(file);
		}//end synchronized
	}

	// Server =================================================
	// 포트 생성
	public void createPort(String cport, String fport) {
		serverMgr.chatPort(cport);
		serverMgr.filePort(fport);
	}

	// 공지사항
	public void sendAll(String type, String str) {
		// 서버창에 쓸 채팅내용
		Manager.getMgr().receiveChatMsg(str);
		// 모두에게 보내는 내용
		serverMgr.sendAll(type, str);
	}
	//강제 퇴장
	public void sendCercion(String userName){
		serverMgr.sendCercion(userName);
	}
}

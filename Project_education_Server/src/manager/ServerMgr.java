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
 * ChatServer와 FileServer를 관리 하는 ServerMgr  클래스.
 */

public class ServerMgr {
	//채팅, 파일
	private ServerSocket chatServerSock;
	private ServerSocket fileServerSock;
	
	//접속한 클라이언트 관리
	private LinkedList<ChatServer> socketList;
	//서버에 등록된 파일 관리
	private LinkedList<File> fileList;
	private File fileFolder;	//파일폴더 위치
	private String filePort;	//파일포드 번호
	
	//생성자
	public ServerMgr()
	{
		socketList = new LinkedList<>();
		fileList = new LinkedList<>();
		
		fileFolder = new File("file");
	}//ServerMgr
	
	//포트 생성
	public void chatPort(String port){
		try {
			chatServerSock = new ServerSocket(Integer.parseInt(port));
			UI.getUI().ouputMsg(port + "  채팅 서버 생성");
			
			//서버 소켓 정보 받아오기.
			Thread sAccepte = new Thread(new Runnable() {
				public void run() {
					while (true) {
						Socket sock;
						try {
							sock = chatServerSock.accept();
							// 클라이언트 ip 출력
							UI.getUI().ouputMsg(sock.getInetAddress().getHostAddress() + ": 연결됨");
							
							//클라이언트 정보 생성
							ChatServer mgr = new ChatServer(sock, ServerMgr.this);
							mgr.start();
							//리스트에 추가
							socketList.add(mgr);
						} catch (IOException e) {
							UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( filePort() ) 1");
						}//end catch
					}//end while
				}//end run
			});
			sAccepte.start();
			
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( filePort() ) 2");
		}//end catch
	}// chatPort
	
	//실행
	public void filePort(String port){
		try {
			filePort = port;
			fileServerSock = new ServerSocket(Integer.parseInt(filePort));
			UI.getUI().ouputMsg(filePort + " 파일 서버 생성");
			//서버 소켓 정보 받아오기.
			Thread sAccepte = new Thread(new Runnable() {
				public void run() {
					while (true) {
						Socket sock;
						try {
							sock = fileServerSock.accept();
							// 클라이언트 ip 출력
							UI.getUI().ouputMsg(sock.getInetAddress().getHostAddress() + ": 파일연결됨");
							//파일 소켓들 생성
							new FileServer(sock, ServerMgr.this).start();
						} catch (IOException e) {
							UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( filePort() ) 1");
						}//end catch
					}//end while
				}//end run
			});
			sAccepte.start();
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( filePort() ) 2");
		}//end catch
	}// filePort
	
	//=================================================================================================
	
	// 전체방송
	public void sendAll(String type, String str) {
		for (int i = 0; i < socketList.size(); i++) {
			// 채팅서버 추출
			ChatServer cs = socketList.get(i);
			System.out.println(cs.getSockName());
			
			// 모두에게 공지사항전송
			if (type.equals(Message.NOTICE))
				cs.sendNotice(str);
			// 채팅
			else if (type.equals(Message.CHAT))
				cs.sendChat(str);
			// 로그인
			else if (type.equals(Message.LOGIN)) {
				String[] users = getUserAllName();
				String[] files = getFileAllName();
				cs.sendLogin(users, files);
			}//end else if
			// 로그아웃
			else if (type.equals(Message.LOGOUT)) {
				cs.sendLogOut(str);
			}//end else if
			// 파일업데이트 알림
			else if (type.equals(Message.FILEUPDATE)) {
				cs.sendFileUpdate();
			}//end else if
		}//end for
	}//sendAll

	//귓속말
	public void sendSecret(String id, String str){
		for(int i = 0; i < socketList.size(); i++)
		{
			//채팅서버 추출
			ChatServer cs = socketList.get(i);
			//보냈으면 하는 유저가 있다면
			if(id != null && cs.getSockName().equals(id))
			{
				Manager.getMgr().receiveChatMsg(str);
				cs.sendChat(str);
				break;
			}//end if
		}//end for
	}//sendSecret
	
	//강제퇴장
	public void sendCercion(String userName){
		ChatServer cs = null;
		if((cs = getSocket(userName)) != null)
			cs.sendCercion();
	}//sendCercion
	
	//찾고자 하는 채팅서버 추출
	public ChatServer getSocket(String id) {
		for (int i = 0; i < socketList.size(); i++) {
			// 채팅서버 추출
			ChatServer cs = socketList.get(i);
			// 해당 소켓이 아니고, 해당 소켓의 id와 찾을 id값이 동일 하다면
			if (cs.getSockName().equals(id))
				return cs;
		}//end for
		return null;
	}//getSocket
	
	//해당 클래스를 제외 하고 찾고자 하는 채팅서버 추출
	public ChatServer getSocket(ChatServer sc, String id){
		for(int i = 0; i < socketList.size(); i++){
			//채팅서버 추출
			ChatServer cs = socketList.get(i);
			//sc가 아니고  ID값이 같다면
			if(!cs.equals(sc) && cs.getSockName().equals(id))
				return cs;
		}//end for
		return null;
	}//getSocket
	
	//해당 파일 여부
	public boolean isFile(String name){
		for (int i = 0; i < fileList.size(); i++) {
			// 동일 파일 존재
			if (fileList.get(i).getName().equals(name)) 
				return true;
		}//end for
		return false;
	}//isFile
	
	//직접 삭제
	public void removeSock(ChatServer cs){
		socketList.remove(cs);
	}//removeSock
	
	//접속자 리스트명 추출
	public String[] getUserAllName(){
		String[] listName = new String[socketList.size()];
		for (int i = 0; i < socketList.size(); i++)
			listName[i] = ((ChatServer) socketList.get(i)).getSockName();
		return listName;
	}//getUserAllName

	//파일 리스트 명 추출
	public String[] getFileAllName() {
		String[] listName = new String[fileList.size()];
		for (int i = 0; i < fileList.size(); i++)
			listName[i] = ((File) fileList.get(i)).getName();
		return listName;
	}// getFileAllName
	
	// 파일 추가
	public void fileAdd(File f) {
			fileList.add(f);	
	}//fileAdd
	
	//index번째의 파일 추출
	public File getFile(int index) {
		return fileList.get(index);
	}//getFile

	public String getfilePort(){ return filePort; }
	public File getFileFolder(){ return fileFolder; }
	
}

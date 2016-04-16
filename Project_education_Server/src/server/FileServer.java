package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import common.UI;
import manager.Manager;
import manager.ServerMgr;

/*
 * FileServer
 * 클라이언트에서 파일을 요청할때 쓰이는 파일전용 서버
 */

public class FileServer extends Server {

	public FileServer(Socket s, ServerMgr mgr) {
		super(s, mgr);
	}//FileServer

	@Override
	public void run() {
		// 정보 읽어오기
		RequestMsg msg = null;
		try {
			msg = (RequestMsg) getOis().readObject();
			reqMessage(msg);
		} catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( run() )");
		} finally {
			// 소켓 종료
			UI.getUI().ouputMsg("파일 서버 종료");
			end();
		}//end finally
	}// run

	@Override
	protected void reqMessage(RequestMsg msg) {
		//파일 받고
		ReqFileMsg rfm = (ReqFileMsg) msg;
		String reqCode = rfm.getReqCode();
		
		//파일 업로드
		if(reqCode.equals(Message.FILEUPLOAD)){
			UI.getUI().ouputMsg(new Date().toString() + "/"+ " : 파일 업로드");
			//파일복사
			copyFile(rfm.getFile());
			//업로드 , 추가한 파일 명, 없음
			Manager.getMgr().sendAll(Message.FILEUPDATE, "파일 업데이트");
		}//end if
		//파일 다운로드
		else if(reqCode.equals(Message.FILEDOWN)){
			UI.getUI().ouputMsg(new Date().toString() + "/"  + " : 파일 다운로드");
			//선택된 파일 알아오기
			File f = getSMgr().getFile(((ReqFileMsg)msg).getFileNum());
			System.out.println(f.getAbsolutePath());
			File downFile = new File(f.getAbsolutePath());
			//다운로드 , 파일 명, 파일
			sendFile(rfm.getReqCode() ,downFile.getName(), downFile);
		}//else if
	}//reqMessage
	
	//파일 보내기 ( 응답코드 , 보낼 내용,  파일)
	private void sendFile(String code, String name,  File f){
		ResFileMsg rfm = new ResFileMsg();
		rfm.setResCode(code);		//응답코드
		rfm.setString(name);				//파일명
		rfm.setFile(f);							//파일
		writeObject(rfm);
	}// sendFile
	
	//파일 복사
	private void copyFile(File file){
		//복사될 파일
		String name = file.getName();
		try {
			//읽을 파일
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			//복사할 파일
			File copyFile = new File(getSMgr().getFileFolder() + File.separator  + name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(copyFile));
			
			//파일 복사
			int data = 0;
			byte[] buf = new byte[8];
			while((data = bis.read(buf)) != -1){
				bos.write(buf, 0, data);
				bos.flush();
				//진행상태 쏘기
				getToClient().write(data);
				getToClient().flush();
			}//end while
			//복사 종료
			bis.close();
			bos.close();
			
			//서버 리스트에 이름이 없다면 추가 / 있다면 기존 이름 그대로 유지
			if(!getSMgr().isFile(copyFile.getName()))
				Manager.getMgr().fileAdd(copyFile);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( copyFile() )");
		}//end catch
	}// copyFile
}

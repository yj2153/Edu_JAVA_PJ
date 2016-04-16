package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import common.UI;
import gui.ProgressBarGUI;
import manager.Manager;

/*
 * 파일 서버
 * 파일을 전송, 수신 하는 곳
 * 
  * ServerMgr 을 상속받는 관계가 아님 
  * 무관한 관계임.
 */

public class FileServer extends Thread {
	private Socket sock;
	private ObjectInputStream oisServer;
	private ObjectOutputStream oosServer;
	private InputStream fromServer;
	
	// 프로그래스바 표현 할때 쓸 용도
	private int PaintMul = 1;
	// 접속 ip와 port
	private String ip;
	private String port;
	// 접속 타입
	private String type;
	// 파일
	private File file;
	// 파일번호
	private int fileNum;


	// 파일 업로드 셋팅
	public FileServer(String ip, String port, File file, String type) {
		this.ip = ip;
		this.port = port;
		this.type = type;
		this.file = file;
		this.fileNum = -1;
	}// FileServer

	// 파일다운로드 셋팅
	public FileServer(String ip, String port, int fileNum, String type) {
		this.ip = ip;
		this.port = port;
		this.type = type;
		this.file = null;
		this.fileNum = fileNum;
	}// FileServer

	@Override
	public void run() {
		
		// 접속 상태면 기다리기
		if (Manager.getMgr().getFileConnect()) {
			Manager.getMgr().MessageGUI("현재 작업이 끝난 후에 이용바람", JOptionPane.WARNING_MESSAGE);
			return;
		}//end if
		
		connect(ip, port);

		// 서버에 보낼 내용
		ReqFileMsg msg = new ReqFileMsg();
		msg.setReqCode(type); // 요청코드
		msg.setFile(file); // 파일
		msg.setFileNum(fileNum);// 파일번호
		// 보내기
		writeObject(msg);

		// ========================================================
		// 서버에서 받은 내용
		if (type.equals(Message.FILEUPLOAD))
			upload();
		else if (type.equals(Message.FILEDOWN))
			download();

		// 소켓 종료
		end();
	}// run

	// 서버 접속
	private void connect(String ip, String port) {

		try {
			sock = new Socket(ip, Integer.parseInt(port));
			UI.getUI().ouputMsg(sock + ": 파일 서버 연결됨");
			// 접속 중
			Manager.getMgr().setFileConnect(true);
			Manager.getMgr().setFileWait(false);
			
			OutputStream toServer = sock.getOutputStream();
			fromServer = sock.getInputStream();
			oosServer = new ObjectOutputStream(toServer);
			oisServer = new ObjectInputStream(fromServer);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발샐 ( connect() )");
			end();
		}//end catch
	}//connect

	// 소켓 종료
	private void end() {
		try {
			if (oosServer != null)
				oosServer.close();
			if (oisServer != null)
				oisServer.close();
			if (sock != null)
				sock.close();
		} catch (IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :  예외발샐 ( end() )");
		}//end catch
		
		// 접속 종료
		Manager.getMgr().setFileConnect(false);
		UI.getUI().ouputMsg("연결 종료");
	}//end

	// 서버로 전송
	public void writeObject(RequestMsg msg) {
		try {
			oosServer.writeObject(msg);
			oosServer.flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발샐 ( writeObject() )");
			Manager.getMgr().MessageGUI("서버가 없습니다.", JOptionPane.WARNING_MESSAGE);
		}// end catch
	}// writeObject

	// ====================================================================
	// 파일 업로드
	private void upload() {
		// 업로드 중
		try {
			int fileLen = division(file.length(), 1000, 100);
			ProgressBarGUI pbg = new ProgressBarGUI(fileLen, " 업로드 중... ");
			Thread pro = new Thread(pbg);
			pro.start();

			int data = 0, cnt = 0;

			// 연속 갱신을 방지 하기 위해 paintCheck단위씩 끊어 갱신
			long paintCheck = (PaintMul > 1) ? PaintMul : 1;
			int paintAdd = 0;

			while ((data = fromServer.read()) != -1) {
				cnt += data;
				
				//일시정지
				while(Manager.getMgr().getFileWait());
				
				// 연속 갱신을 방지 하기 위해 paintCheck단위씩 끊어 갱신
				if ((paintAdd != (cnt / paintCheck))) {
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				} // end if
			} // end while

			// 완료 메세지
			if (pbg.ProgressDispose())
				Manager.getMgr().MessageGUI("업로드가 완료되었습니다.", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발샐 ( upload() )");
		}//end catch
	}// upload

	// 파일다운로드
	private void download() {
		// 완료 응답
		ResFileMsg rfm = null;

		try {
			rfm = (ResFileMsg) oisServer.readObject();
			UI.getUI().ouputMsg(rfm.getResCode() + " : 응답코드");
			UI.getUI().ouputMsg(rfm.getString() + " : 다운로드 될 파일 명");
		
			// 파일다운로드 라면
			File fDown = rfm.getFile();
			// 복사될 파일명
			String name = fDown.getName();
			
			// 복사될 파일이 있다면
			if (isFile(name)) {
				// 버튼 명
				String[] buttons = { "덮어쓰기", "이어쓰기" };
				int result = Manager.getMgr().QMessageGUI("동일 파일이 존재합니다.", buttons, JOptionPane.QUESTION_MESSAGE);
			
				// 덮어쓰기라면
				if (result == JOptionPane.YES_OPTION) {
					fileWrite(fDown, false);
				} // end if
				//이어쓰기
				else if (result == JOptionPane.NO_OPTION)
					fileWrite(fDown, true);
				else
					UI.getUI().ouputMsg("다운로드 취소");
			} // end if
			
			// 동일 파일이 없다면 덮어쓰기 사용.
			else
				fileWrite(fDown, false);

		} catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발샐 ( download() )");
		} // end if
	}// download

	// ====================================================================
	// 파일쓰기 ( 덮어쓰기 == 전체쓰기 , 이어쓰기
	private void fileWrite(File file, boolean bType) {
		try {
			File fCopy = new File(Manager.getMgr().fileFolder() + File.separator + file.getName());
			FileOutputStream fos;
			// ====================================================
			// 이어쓰기
			if (bType == true)
				fos = new FileOutputStream(fCopy, bType);
			// 덮어쓰기 (전체 쓰기
			else
				fos = new FileOutputStream(fCopy);
			// ====================================================

			// 읽을 파일
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			// 복사할 파일
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// 진행바 생성 //파일 길이
			int fileLen = division(file.length(), 1000, 100);
			ProgressBarGUI pbg = new ProgressBarGUI(fileLen, "다운로드 중... ");
			Thread pro = new Thread(pbg);
			pro.start();

			// 파일 복사, 길이체크
			int data = 0, cnt = 0;
			byte[] buf = new byte[8];

			// 연속 갱신을 방지 하기 위해 paintCheck단위씩 끊어 갱신
			long paintCheck = (PaintMul > 1) ? PaintMul : 1;
			int paintAdd = 0;

			// 카피 파일의 길이
			long fCopyLenth = fCopy.length();

			//======================================================
			// 이어쓰기라면
			if (bType) {
				if ((data = bis.read(new byte[(int) fCopyLenth])) != -1) {
					cnt += data;
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				}//end if
			} // end if
			//======================================================
			
			//파일쓰기
			while ((data = bis.read(buf)) != -1) {

				cnt += data;
				bos.write(buf, 0, data);
				bos.flush();
				
				//일시정지
				while(Manager.getMgr().getFileWait());
				
				// 연속 갱신을 방지 하기 위해 paintCheck단위씩 끊어 갱신
				if ((paintAdd != (cnt / paintCheck))) {
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				}//end if
			}//end while
			
			// 복사 종료
			bis.close();
			bos.close();
			// 완료 메세지
			if (pbg.ProgressDispose()) 
				Manager.getMgr().MessageGUI("다운로드가 완료되었습니다.", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발샐 ( fileWrite() )");
		}//end catch
	}//fileWrite

	// 동일한 파일명이 있는지 검사
	private boolean isFile(String sName) {
		File[] fList = new File(Manager.getMgr().fileFolder().getName()).listFiles();
		for (int i = 0; i < fList.length; i++) {
			// 디렉토리가 아니라면 //찾는 파일명이 있다면
			if (!fList[i].isDirectory() && fList[i].getName().equals(sName))
				return true;
		} // end for
		return false;
	}//isFile

	// max값 이상이라면 min값으로 계속 나눠라
	public int division(long value, int max, int min) {
		int result = 0;

		if (value > max) {
			PaintMul *= min;
			return division(value / min, max, min);
		}//end if

		result = (int) value;

		return result;
	}//division
	// ===================================================================
}

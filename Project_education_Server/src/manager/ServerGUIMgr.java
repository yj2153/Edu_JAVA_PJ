package manager;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.UI;
import gui.ServerGUI;
import server.Message;

/*
 * ServerGUIMgr
 * GUI에 대한 처리를 관리하는 클래스
 * ServerGUI클래스를 상속 받는 구조
 */

public class ServerGUIMgr extends ServerGUI {
	private static final long serialVersionUID = 21L;

	// 채팅창에 입력할 내용
	@Override
	public void receiveChatMsg(String str) {
		getChatArea().append(str);
		getChatArea().append("\n");
		
		//스크롤 바 정리
		getChatPane().getVerticalScrollBar().setValue(getChatPane().getVerticalScrollBar().getMaximum());
	}//receiveChatMsg

	//모두에게 보낼 내용
	@Override
	public void sendChatMsg() {
		//쓴 내용이 없다면 리면
		if(getChatText().getText().length() <= 0)
			return;
		
		String send = getChatText().getText();
		Manager.getMgr().sendAll(Message.NOTICE, send);
		
		//초기화
		getChatText().setText("");
	}//sendChatMsg

	// 접속요청
	@Override
	public void sendConnect(){
		// ip , port, id 를 추출
		String cport = getChatPort().getText();
		String fPort = getFilePort().getText();
		// 서버 생성
		Manager.getMgr().createPort(cport, fPort);
		getConnectBtn().setEnabled(false);
	}//sendConnect

	// 접속자 리스트 추가/제거
	@Override
	public void receiveUser(String user) {
		synchronized (this) {
			// 이름이 있다면 제거
			if (getUserVec().contains(user)) {
				int num = getUserVec().indexOf(user);
				getUserVec().remove(num);
			}//end if
			// 없다면 추가
			else 
				getUserVec().add(user);
		}// synchronized
			// 리스트 갱신
			getUserList().setListData(getUserVec());
	}//receiveUser

	// 파일리스트 추가
	@Override
	public void receiveFile(String file) {
		synchronized (this) {
			getFileVec().add(file);
		}// synchronized
			// 리스트 갱신
			getFileList().setListData(getFileVec());
	}//receiveFile

	// 대화 내용 저장
	@Override
	public void saveText() {
		JFileChooser jfc = new JFileChooser(".");// 현재경로의 파일다이알로그
		jfc.setMultiSelectionEnabled(false);//다중 선택 불가
		//메모장만으로 제한하기
		jfc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
		int x = jfc.showSaveDialog(this);
		// 저장 버튼을 눌럿을때
		if (x == 0) {
			File file = jfc.getSelectedFile();
			copyFile(file);
		}// end if
	}//saveText
	
	//파일 열기
	@Override
	public void openText(){
		JFileChooser jfc = new JFileChooser(".");// 현재경로의 파일다이알로그
		jfc.setMultiSelectionEnabled(false);//다중 선택 불가
		//메모장만으로 제한하기
		jfc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
		int x = jfc.showOpenDialog(this);
		//열기를 눌렀을때
		if (x == 0) {
			//파일 경로 받기
			File file = jfc.getSelectedFile();
			try {
				//메모장실행
				new ProcessBuilder("notepad.exe", file.getAbsolutePath()).start();
			} catch (IOException e) {
				UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( openText() )");
			}//end catch
		}//end if
	}//openText
	
	// 접속 종료
	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}//end exit

	// 파일 복사
	private void copyFile(File f) {
		// 복사 파일
		PrintWriter bos = null;
		try {
			bos = new PrintWriter(new FileOutputStream(f));
			bos.write(getChatArea().getText());
			bos.flush();

		} catch (FileNotFoundException e) {
			UI.getUI().errorMsg(getClass().getName() + " : 예외발생 ( copyFile() )");
		} finally {
			if (bos != null)
				bos.close();
		}//end finally
	}//copyFile

	//유저 선택시 
	@Override
	public void userSelect(Point p, String user) {
		if(getUserVec().size() <= 0)
			return;
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem outItem = new JMenuItem("강제퇴장");
		menu.add(outItem);
		menu.show(getUserList(), p.x + 10, p.y + 5);
		outItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Manager.getMgr().sendCercion(user);
			}
		});
	}//userSelect

	//선택시
	@Override
	public void fileSelect() {
		// TODO Auto-generated method stub
	}//fileSelect

}

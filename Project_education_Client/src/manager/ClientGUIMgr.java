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
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.UI;
import gui.ClientGUI;
import server.Message;

/*
 * ClientGUIMgr 클래스는 ClientGUI 를 상속받는다!
 * 프로그래밍적 처리는 모두 이 클래스에서 전담.
 */

public class ClientGUIMgr extends ClientGUI {
	private static final long serialVersionUID = 21L;
	
	// 채팅창에 입력할 내용
	@Override
	public void receiveChatMsg(String str) {
		getChatArea().append(str);
		getChatArea().append("\n");
		
		//스크롤 바
		getChatPane().getVerticalScrollBar().setValue(getChatPane().getVerticalScrollBar().getMaximum());
	}//receiveChatMsg

	// 보낼 채팅 내용
	@Override
	public void sendChatMsg() {
		//값이 없으면 리턴
		if(getChatText().getText().length() <= 0)
			return;
		
		String send = getChatText().getText();
		Manager.getMgr().sendMessage(Message.CHAT, send);
		//초기화
		getChatText().setText("");
	}//sendChatMsg

	// 접속요청
	@Override
	public void sendConnect() {
		// ip , port, id 를 추출
		String ip = getIpText().getText();
		String port = getPortText().getText();
		String id = getIdText().getText();
		
		//버튼 비활성화
		getConnectBtn().setEnabled(false);
		
		// 서버 접속 요청
		Manager.getMgr().connect(ip, port, id);
	}//sendConnect

	// 버튼살리기
	public void connectEnabled() {
		getConnectBtn().setEnabled(true);
	}//connectEnabled

	// 기본 팝업
	@Override
	public void MessageGUI(String str, int type) {
		JOptionPane.showMessageDialog(this, str, "Message", type);
	}//MessageGUI

	//질문 팝업
	@Override
	public int QMessageGUI(String str, String[] buttons,  int type) {
		int result = JOptionPane.showOptionDialog(this, str, "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
		
		return result;
	}// QMessageGUI
	
	// 접속자 리스트 추가/제거
	@Override
	public void userUpdate(String[] users) {
		synchronized (this) {
			getUserVec().clear();
			for (String user : users)
				getUserVec().add(user);
			// 리스트 갱신
			getUserList().setListData(getUserVec());
		}// end synchronized
	}//userUpdate
	
	//한명 처리
	@Override
	public void userRemove(String user){
			synchronized (this) {
				// 이름이 있다면 제거
				if (getUserVec().contains(user)) {
					int num = getUserVec().indexOf(user);
					getUserVec().remove(num);
				}//end if
			}// synchronized
			// 리스트 갱신
			getUserList().setListData(getUserVec());
	}//userUpdate

	// 파일리스트 추가/제거
	@Override
	public void fileUpdate(String[] files) {
		synchronized (this) {
			getFileVec().clear();
			for (String file : files)
				getFileVec().add(file);
			// 리스트 갱신
			getFileList().setListData(getFileVec());
		}// end synchronized
	}// fileUpdate

	// 대화 내용 저장
	@Override
	public void saveText() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// 현재경로의 파일다이알로그
		jfc.setMultiSelectionEnabled(false);// 다중 선택 불가
		// 메모장만으로 제한하기
		jfc.setFileFilter(new FileNameExtensionFilter("텍스트 파일(*.txt)", "txt"));
		int x = jfc.showSaveDialog(this);
		// 저장 버튼을 눌럿을때
		if (x == 0) {
			File file = jfc.getSelectedFile();
			copyFile(file);
		}//end if
	}// saveText

	// 리스트 올 클리어
	@Override
	public void listClear() {
		getFileVec().clear();
		getUserVec().clear();

		getFileList().setListData(getUserVec());
		getUserList().setListData(getUserVec());
	}// listClear

	// 파일 열기
	@Override
	public void openText() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// 현재경로의 파일다이알로그
		jfc.setMultiSelectionEnabled(false);// 다중 선택 불가
		// 메모장만으로 제한하기
		jfc.setFileFilter(new FileNameExtensionFilter("텍스트 파일(*.txt)", "txt"));
		int x = jfc.showOpenDialog(this);
		// 열기를 눌렀을때
		if (x == 0) {
			// 파일 경로 받기
			File file = jfc.getSelectedFile();
			try {
				// 메모장실행
				new ProcessBuilder("notepad.exe", file.getAbsolutePath()).start();
			} catch (IOException e) {
				UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( openText() )");
			}//end catch
		} // end if
	}// openText
	
	// 접속 종료
	@Override
	public void exit() {
		Manager.getMgr().sendMessage(Message.LOGOUT, Manager.getMgr().getUserID());
	}//exit

	// 파일 복사
	private void copyFile(File f) {
		// 복사 파일
		PrintWriter bos = null;
		try {
			//확장자명이 없다면
			if ((f.getName().lastIndexOf(".")) == -1) {
				//파일 재정의
				f = new File(Manager.getMgr().fileFolder() + File.separator + f.getName() + ".txt");
				
				UI.getUI().ouputMsg("텍스트 파일로 생성");
			}//end if
			//확장자명이 다르다면
			else if(! f.getName().substring(f.getName().lastIndexOf("."), f.getName().length()).equals(".txt")) {
				//확장자명을 뺀 파일명 추출
				String fileName = f.getName().substring(0, f.getName().lastIndexOf(".") -1);
				
				//파일재정의
				f = new File(Manager.getMgr().fileFolder() + File.separator +fileName + ".txt");
				
				UI.getUI().ouputMsg("텍스트 파일이 아닙니다.");
			}//end else if
			
			//파일 복사
			bos = new PrintWriter(new FileOutputStream(f));
			bos.write(getChatArea().getText());
			bos.flush();

		} catch (FileNotFoundException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( copyFile() )");
		} finally {
			if (bos != null)
				bos.close();
		}//end finally
	}//copyFile

	// 유저 선택시
	@Override
	public void userSelect(String str, Point p) {
		if (getUserVec().size() <= 0)
			return;
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem secretItem = new JMenuItem("귓속말");
		menu.add(secretItem);
		menu.show(getUserList(), p.x + 10, p.y + 5);

		secretItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 유저명
				getChatText().setText(str + ">>");
			}
		});
	}//userSelect

	// 선택시
	@Override
	public void fileSelect(Point p, int index) {
		if (getFileVec().size() <= 0)
			return;

		JPopupMenu menu = new JPopupMenu();
		JMenuItem downItem = new JMenuItem("다운로드");
		menu.add(downItem);
		menu.show(getFileList(), p.x + 10, p.y + 5);

		downItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 서버에 있는 파일 위치
				Manager.getMgr().fileRequest(index);
				
			}
		});
	}//fileSelect

	// 파일 업로드 요청
	@Override
	public void upload() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// 현재경로의 파일다이알로그
		jfc.setMultiSelectionEnabled(false);// 다중 선택 불가
		// 메모장만으로 제한하기
		int x = jfc.showOpenDialog(this);
		// 열기를 눌렀을때
		if (x == 0) {
			// 파일 경로 받기
			File file = jfc.getSelectedFile();
			Manager.getMgr().fileRequest(file);
		} // end if
	}//upload
}

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
 * ClientGUIMgr Ŭ������ ClientGUI �� ��ӹ޴´�!
 * ���α׷����� ó���� ��� �� Ŭ�������� ����.
 */

public class ClientGUIMgr extends ClientGUI {
	private static final long serialVersionUID = 21L;
	
	// ä��â�� �Է��� ����
	@Override
	public void receiveChatMsg(String str) {
		getChatArea().append(str);
		getChatArea().append("\n");
		
		//��ũ�� ��
		getChatPane().getVerticalScrollBar().setValue(getChatPane().getVerticalScrollBar().getMaximum());
	}//receiveChatMsg

	// ���� ä�� ����
	@Override
	public void sendChatMsg() {
		//���� ������ ����
		if(getChatText().getText().length() <= 0)
			return;
		
		String send = getChatText().getText();
		Manager.getMgr().sendMessage(Message.CHAT, send);
		//�ʱ�ȭ
		getChatText().setText("");
	}//sendChatMsg

	// ���ӿ�û
	@Override
	public void sendConnect() {
		// ip , port, id �� ����
		String ip = getIpText().getText();
		String port = getPortText().getText();
		String id = getIdText().getText();
		
		//��ư ��Ȱ��ȭ
		getConnectBtn().setEnabled(false);
		
		// ���� ���� ��û
		Manager.getMgr().connect(ip, port, id);
	}//sendConnect

	// ��ư�츮��
	public void connectEnabled() {
		getConnectBtn().setEnabled(true);
	}//connectEnabled

	// �⺻ �˾�
	@Override
	public void MessageGUI(String str, int type) {
		JOptionPane.showMessageDialog(this, str, "Message", type);
	}//MessageGUI

	//���� �˾�
	@Override
	public int QMessageGUI(String str, String[] buttons,  int type) {
		int result = JOptionPane.showOptionDialog(this, str, "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
		
		return result;
	}// QMessageGUI
	
	// ������ ����Ʈ �߰�/����
	@Override
	public void userUpdate(String[] users) {
		synchronized (this) {
			getUserVec().clear();
			for (String user : users)
				getUserVec().add(user);
			// ����Ʈ ����
			getUserList().setListData(getUserVec());
		}// end synchronized
	}//userUpdate
	
	//�Ѹ� ó��
	@Override
	public void userRemove(String user){
			synchronized (this) {
				// �̸��� �ִٸ� ����
				if (getUserVec().contains(user)) {
					int num = getUserVec().indexOf(user);
					getUserVec().remove(num);
				}//end if
			}// synchronized
			// ����Ʈ ����
			getUserList().setListData(getUserVec());
	}//userUpdate

	// ���ϸ���Ʈ �߰�/����
	@Override
	public void fileUpdate(String[] files) {
		synchronized (this) {
			getFileVec().clear();
			for (String file : files)
				getFileVec().add(file);
			// ����Ʈ ����
			getFileList().setListData(getFileVec());
		}// end synchronized
	}// fileUpdate

	// ��ȭ ���� ����
	@Override
	public void saveText() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// �������� ���ϴ��̾˷α�
		jfc.setMultiSelectionEnabled(false);// ���� ���� �Ұ�
		// �޸��常���� �����ϱ�
		jfc.setFileFilter(new FileNameExtensionFilter("�ؽ�Ʈ ����(*.txt)", "txt"));
		int x = jfc.showSaveDialog(this);
		// ���� ��ư�� ��������
		if (x == 0) {
			File file = jfc.getSelectedFile();
			copyFile(file);
		}//end if
	}// saveText

	// ����Ʈ �� Ŭ����
	@Override
	public void listClear() {
		getFileVec().clear();
		getUserVec().clear();

		getFileList().setListData(getUserVec());
		getUserList().setListData(getUserVec());
	}// listClear

	// ���� ����
	@Override
	public void openText() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// �������� ���ϴ��̾˷α�
		jfc.setMultiSelectionEnabled(false);// ���� ���� �Ұ�
		// �޸��常���� �����ϱ�
		jfc.setFileFilter(new FileNameExtensionFilter("�ؽ�Ʈ ����(*.txt)", "txt"));
		int x = jfc.showOpenDialog(this);
		// ���⸦ ��������
		if (x == 0) {
			// ���� ��� �ޱ�
			File file = jfc.getSelectedFile();
			try {
				// �޸������
				new ProcessBuilder("notepad.exe", file.getAbsolutePath()).start();
			} catch (IOException e) {
				UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( openText() )");
			}//end catch
		} // end if
	}// openText
	
	// ���� ����
	@Override
	public void exit() {
		Manager.getMgr().sendMessage(Message.LOGOUT, Manager.getMgr().getUserID());
	}//exit

	// ���� ����
	private void copyFile(File f) {
		// ���� ����
		PrintWriter bos = null;
		try {
			//Ȯ���ڸ��� ���ٸ�
			if ((f.getName().lastIndexOf(".")) == -1) {
				//���� ������
				f = new File(Manager.getMgr().fileFolder() + File.separator + f.getName() + ".txt");
				
				UI.getUI().ouputMsg("�ؽ�Ʈ ���Ϸ� ����");
			}//end if
			//Ȯ���ڸ��� �ٸ��ٸ�
			else if(! f.getName().substring(f.getName().lastIndexOf("."), f.getName().length()).equals(".txt")) {
				//Ȯ���ڸ��� �� ���ϸ� ����
				String fileName = f.getName().substring(0, f.getName().lastIndexOf(".") -1);
				
				//����������
				f = new File(Manager.getMgr().fileFolder() + File.separator +fileName + ".txt");
				
				UI.getUI().ouputMsg("�ؽ�Ʈ ������ �ƴմϴ�.");
			}//end else if
			
			//���� ����
			bos = new PrintWriter(new FileOutputStream(f));
			bos.write(getChatArea().getText());
			bos.flush();

		} catch (FileNotFoundException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( copyFile() )");
		} finally {
			if (bos != null)
				bos.close();
		}//end finally
	}//copyFile

	// ���� ���ý�
	@Override
	public void userSelect(String str, Point p) {
		if (getUserVec().size() <= 0)
			return;
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem secretItem = new JMenuItem("�ӼӸ�");
		menu.add(secretItem);
		menu.show(getUserList(), p.x + 10, p.y + 5);

		secretItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ������
				getChatText().setText(str + ">>");
			}
		});
	}//userSelect

	// ���ý�
	@Override
	public void fileSelect(Point p, int index) {
		if (getFileVec().size() <= 0)
			return;

		JPopupMenu menu = new JPopupMenu();
		JMenuItem downItem = new JMenuItem("�ٿ�ε�");
		menu.add(downItem);
		menu.show(getFileList(), p.x + 10, p.y + 5);

		downItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ������ �ִ� ���� ��ġ
				Manager.getMgr().fileRequest(index);
				
			}
		});
	}//fileSelect

	// ���� ���ε� ��û
	@Override
	public void upload() {
		JFileChooser jfc = new JFileChooser(Manager.getMgr().fileFolder());// �������� ���ϴ��̾˷α�
		jfc.setMultiSelectionEnabled(false);// ���� ���� �Ұ�
		// �޸��常���� �����ϱ�
		int x = jfc.showOpenDialog(this);
		// ���⸦ ��������
		if (x == 0) {
			// ���� ��� �ޱ�
			File file = jfc.getSelectedFile();
			Manager.getMgr().fileRequest(file);
		} // end if
	}//upload
}

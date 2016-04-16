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
 * GUI�� ���� ó���� �����ϴ� Ŭ����
 * ServerGUIŬ������ ��� �޴� ����
 */

public class ServerGUIMgr extends ServerGUI {
	private static final long serialVersionUID = 21L;

	// ä��â�� �Է��� ����
	@Override
	public void receiveChatMsg(String str) {
		getChatArea().append(str);
		getChatArea().append("\n");
		
		//��ũ�� �� ����
		getChatPane().getVerticalScrollBar().setValue(getChatPane().getVerticalScrollBar().getMaximum());
	}//receiveChatMsg

	//��ο��� ���� ����
	@Override
	public void sendChatMsg() {
		//�� ������ ���ٸ� ����
		if(getChatText().getText().length() <= 0)
			return;
		
		String send = getChatText().getText();
		Manager.getMgr().sendAll(Message.NOTICE, send);
		
		//�ʱ�ȭ
		getChatText().setText("");
	}//sendChatMsg

	// ���ӿ�û
	@Override
	public void sendConnect(){
		// ip , port, id �� ����
		String cport = getChatPort().getText();
		String fPort = getFilePort().getText();
		// ���� ����
		Manager.getMgr().createPort(cport, fPort);
		getConnectBtn().setEnabled(false);
	}//sendConnect

	// ������ ����Ʈ �߰�/����
	@Override
	public void receiveUser(String user) {
		synchronized (this) {
			// �̸��� �ִٸ� ����
			if (getUserVec().contains(user)) {
				int num = getUserVec().indexOf(user);
				getUserVec().remove(num);
			}//end if
			// ���ٸ� �߰�
			else 
				getUserVec().add(user);
		}// synchronized
			// ����Ʈ ����
			getUserList().setListData(getUserVec());
	}//receiveUser

	// ���ϸ���Ʈ �߰�
	@Override
	public void receiveFile(String file) {
		synchronized (this) {
			getFileVec().add(file);
		}// synchronized
			// ����Ʈ ����
			getFileList().setListData(getFileVec());
	}//receiveFile

	// ��ȭ ���� ����
	@Override
	public void saveText() {
		JFileChooser jfc = new JFileChooser(".");// �������� ���ϴ��̾˷α�
		jfc.setMultiSelectionEnabled(false);//���� ���� �Ұ�
		//�޸��常���� �����ϱ�
		jfc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
		int x = jfc.showSaveDialog(this);
		// ���� ��ư�� ��������
		if (x == 0) {
			File file = jfc.getSelectedFile();
			copyFile(file);
		}// end if
	}//saveText
	
	//���� ����
	@Override
	public void openText(){
		JFileChooser jfc = new JFileChooser(".");// �������� ���ϴ��̾˷α�
		jfc.setMultiSelectionEnabled(false);//���� ���� �Ұ�
		//�޸��常���� �����ϱ�
		jfc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
		int x = jfc.showOpenDialog(this);
		//���⸦ ��������
		if (x == 0) {
			//���� ��� �ޱ�
			File file = jfc.getSelectedFile();
			try {
				//�޸������
				new ProcessBuilder("notepad.exe", file.getAbsolutePath()).start();
			} catch (IOException e) {
				UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( openText() )");
			}//end catch
		}//end if
	}//openText
	
	// ���� ����
	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}//end exit

	// ���� ����
	private void copyFile(File f) {
		// ���� ����
		PrintWriter bos = null;
		try {
			bos = new PrintWriter(new FileOutputStream(f));
			bos.write(getChatArea().getText());
			bos.flush();

		} catch (FileNotFoundException e) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( copyFile() )");
		} finally {
			if (bos != null)
				bos.close();
		}//end finally
	}//copyFile

	//���� ���ý� 
	@Override
	public void userSelect(Point p, String user) {
		if(getUserVec().size() <= 0)
			return;
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem outItem = new JMenuItem("��������");
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

	//���ý�
	@Override
	public void fileSelect() {
		// TODO Auto-generated method stub
	}//fileSelect

}

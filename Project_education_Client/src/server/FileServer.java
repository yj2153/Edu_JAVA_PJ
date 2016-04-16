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
 * ���� ����
 * ������ ����, ���� �ϴ� ��
 * 
  * ServerMgr �� ��ӹ޴� ���谡 �ƴ� 
  * ������ ������.
 */

public class FileServer extends Thread {
	private Socket sock;
	private ObjectInputStream oisServer;
	private ObjectOutputStream oosServer;
	private InputStream fromServer;
	
	// ���α׷����� ǥ�� �Ҷ� �� �뵵
	private int PaintMul = 1;
	// ���� ip�� port
	private String ip;
	private String port;
	// ���� Ÿ��
	private String type;
	// ����
	private File file;
	// ���Ϲ�ȣ
	private int fileNum;


	// ���� ���ε� ����
	public FileServer(String ip, String port, File file, String type) {
		this.ip = ip;
		this.port = port;
		this.type = type;
		this.file = file;
		this.fileNum = -1;
	}// FileServer

	// ���ϴٿ�ε� ����
	public FileServer(String ip, String port, int fileNum, String type) {
		this.ip = ip;
		this.port = port;
		this.type = type;
		this.file = null;
		this.fileNum = fileNum;
	}// FileServer

	@Override
	public void run() {
		
		// ���� ���¸� ��ٸ���
		if (Manager.getMgr().getFileConnect()) {
			Manager.getMgr().MessageGUI("���� �۾��� ���� �Ŀ� �̿�ٶ�", JOptionPane.WARNING_MESSAGE);
			return;
		}//end if
		
		connect(ip, port);

		// ������ ���� ����
		ReqFileMsg msg = new ReqFileMsg();
		msg.setReqCode(type); // ��û�ڵ�
		msg.setFile(file); // ����
		msg.setFileNum(fileNum);// ���Ϲ�ȣ
		// ������
		writeObject(msg);

		// ========================================================
		// �������� ���� ����
		if (type.equals(Message.FILEUPLOAD))
			upload();
		else if (type.equals(Message.FILEDOWN))
			download();

		// ���� ����
		end();
	}// run

	// ���� ����
	private void connect(String ip, String port) {

		try {
			sock = new Socket(ip, Integer.parseInt(port));
			UI.getUI().ouputMsg(sock + ": ���� ���� �����");
			// ���� ��
			Manager.getMgr().setFileConnect(true);
			Manager.getMgr().setFileWait(false);
			
			OutputStream toServer = sock.getOutputStream();
			fromServer = sock.getInputStream();
			oosServer = new ObjectOutputStream(toServer);
			oisServer = new ObjectInputStream(fromServer);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( connect() )");
			end();
		}//end catch
	}//connect

	// ���� ����
	private void end() {
		try {
			if (oosServer != null)
				oosServer.close();
			if (oisServer != null)
				oisServer.close();
			if (sock != null)
				sock.close();
		} catch (IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :  ���ܹ߻� ( end() )");
		}//end catch
		
		// ���� ����
		Manager.getMgr().setFileConnect(false);
		UI.getUI().ouputMsg("���� ����");
	}//end

	// ������ ����
	public void writeObject(RequestMsg msg) {
		try {
			oosServer.writeObject(msg);
			oosServer.flush();
		} catch (Exception ex) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( writeObject() )");
			Manager.getMgr().MessageGUI("������ �����ϴ�.", JOptionPane.WARNING_MESSAGE);
		}// end catch
	}// writeObject

	// ====================================================================
	// ���� ���ε�
	private void upload() {
		// ���ε� ��
		try {
			int fileLen = division(file.length(), 1000, 100);
			ProgressBarGUI pbg = new ProgressBarGUI(fileLen, " ���ε� ��... ");
			Thread pro = new Thread(pbg);
			pro.start();

			int data = 0, cnt = 0;

			// ���� ������ ���� �ϱ� ���� paintCheck������ ���� ����
			long paintCheck = (PaintMul > 1) ? PaintMul : 1;
			int paintAdd = 0;

			while ((data = fromServer.read()) != -1) {
				cnt += data;
				
				//�Ͻ�����
				while(Manager.getMgr().getFileWait());
				
				// ���� ������ ���� �ϱ� ���� paintCheck������ ���� ����
				if ((paintAdd != (cnt / paintCheck))) {
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				} // end if
			} // end while

			// �Ϸ� �޼���
			if (pbg.ProgressDispose())
				Manager.getMgr().MessageGUI("���ε尡 �Ϸ�Ǿ����ϴ�.", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " : ���ܹ߻� ( upload() )");
		}//end catch
	}// upload

	// ���ϴٿ�ε�
	private void download() {
		// �Ϸ� ����
		ResFileMsg rfm = null;

		try {
			rfm = (ResFileMsg) oisServer.readObject();
			UI.getUI().ouputMsg(rfm.getResCode() + " : �����ڵ�");
			UI.getUI().ouputMsg(rfm.getString() + " : �ٿ�ε� �� ���� ��");
		
			// ���ϴٿ�ε� ���
			File fDown = rfm.getFile();
			// ����� ���ϸ�
			String name = fDown.getName();
			
			// ����� ������ �ִٸ�
			if (isFile(name)) {
				// ��ư ��
				String[] buttons = { "�����", "�̾��" };
				int result = Manager.getMgr().QMessageGUI("���� ������ �����մϴ�.", buttons, JOptionPane.QUESTION_MESSAGE);
			
				// �������
				if (result == JOptionPane.YES_OPTION) {
					fileWrite(fDown, false);
				} // end if
				//�̾��
				else if (result == JOptionPane.NO_OPTION)
					fileWrite(fDown, true);
				else
					UI.getUI().ouputMsg("�ٿ�ε� ���");
			} // end if
			
			// ���� ������ ���ٸ� ����� ���.
			else
				fileWrite(fDown, false);

		} catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( download() )");
		} // end if
	}// download

	// ====================================================================
	// ���Ͼ��� ( ����� == ��ü���� , �̾��
	private void fileWrite(File file, boolean bType) {
		try {
			File fCopy = new File(Manager.getMgr().fileFolder() + File.separator + file.getName());
			FileOutputStream fos;
			// ====================================================
			// �̾��
			if (bType == true)
				fos = new FileOutputStream(fCopy, bType);
			// ����� (��ü ����
			else
				fos = new FileOutputStream(fCopy);
			// ====================================================

			// ���� ����
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			// ������ ����
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// ����� ���� //���� ����
			int fileLen = division(file.length(), 1000, 100);
			ProgressBarGUI pbg = new ProgressBarGUI(fileLen, "�ٿ�ε� ��... ");
			Thread pro = new Thread(pbg);
			pro.start();

			// ���� ����, ����üũ
			int data = 0, cnt = 0;
			byte[] buf = new byte[8];

			// ���� ������ ���� �ϱ� ���� paintCheck������ ���� ����
			long paintCheck = (PaintMul > 1) ? PaintMul : 1;
			int paintAdd = 0;

			// ī�� ������ ����
			long fCopyLenth = fCopy.length();

			//======================================================
			// �̾����
			if (bType) {
				if ((data = bis.read(new byte[(int) fCopyLenth])) != -1) {
					cnt += data;
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				}//end if
			} // end if
			//======================================================
			
			//���Ͼ���
			while ((data = bis.read(buf)) != -1) {

				cnt += data;
				bos.write(buf, 0, data);
				bos.flush();
				
				//�Ͻ�����
				while(Manager.getMgr().getFileWait());
				
				// ���� ������ ���� �ϱ� ���� paintCheck������ ���� ����
				if ((paintAdd != (cnt / paintCheck))) {
					paintAdd = (int) (cnt / paintCheck);
					pbg.ProgressBarPaint(paintAdd);
				}//end if
			}//end while
			
			// ���� ����
			bis.close();
			bos.close();
			// �Ϸ� �޼���
			if (pbg.ProgressDispose()) 
				Manager.getMgr().MessageGUI("�ٿ�ε尡 �Ϸ�Ǿ����ϴ�.", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( fileWrite() )");
		}//end catch
	}//fileWrite

	// ������ ���ϸ��� �ִ��� �˻�
	private boolean isFile(String sName) {
		File[] fList = new File(Manager.getMgr().fileFolder().getName()).listFiles();
		for (int i = 0; i < fList.length; i++) {
			// ���丮�� �ƴ϶�� //ã�� ���ϸ��� �ִٸ�
			if (!fList[i].isDirectory() && fList[i].getName().equals(sName))
				return true;
		} // end for
		return false;
	}//isFile

	// max�� �̻��̶�� min������ ��� ������
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

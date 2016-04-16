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
 * Ŭ���̾�Ʈ���� ������ ��û�Ҷ� ���̴� �������� ����
 */

public class FileServer extends Server {

	public FileServer(Socket s, ServerMgr mgr) {
		super(s, mgr);
	}//FileServer

	@Override
	public void run() {
		// ���� �о����
		RequestMsg msg = null;
		try {
			msg = (RequestMsg) getOis().readObject();
			reqMessage(msg);
		} catch (ClassNotFoundException | IOException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( run() )");
		} finally {
			// ���� ����
			UI.getUI().ouputMsg("���� ���� ����");
			end();
		}//end finally
	}// run

	@Override
	protected void reqMessage(RequestMsg msg) {
		//���� �ް�
		ReqFileMsg rfm = (ReqFileMsg) msg;
		String reqCode = rfm.getReqCode();
		
		//���� ���ε�
		if(reqCode.equals(Message.FILEUPLOAD)){
			UI.getUI().ouputMsg(new Date().toString() + "/"+ " : ���� ���ε�");
			//���Ϻ���
			copyFile(rfm.getFile());
			//���ε� , �߰��� ���� ��, ����
			Manager.getMgr().sendAll(Message.FILEUPDATE, "���� ������Ʈ");
		}//end if
		//���� �ٿ�ε�
		else if(reqCode.equals(Message.FILEDOWN)){
			UI.getUI().ouputMsg(new Date().toString() + "/"  + " : ���� �ٿ�ε�");
			//���õ� ���� �˾ƿ���
			File f = getSMgr().getFile(((ReqFileMsg)msg).getFileNum());
			System.out.println(f.getAbsolutePath());
			File downFile = new File(f.getAbsolutePath());
			//�ٿ�ε� , ���� ��, ����
			sendFile(rfm.getReqCode() ,downFile.getName(), downFile);
		}//else if
	}//reqMessage
	
	//���� ������ ( �����ڵ� , ���� ����,  ����)
	private void sendFile(String code, String name,  File f){
		ResFileMsg rfm = new ResFileMsg();
		rfm.setResCode(code);		//�����ڵ�
		rfm.setString(name);				//���ϸ�
		rfm.setFile(f);							//����
		writeObject(rfm);
	}// sendFile
	
	//���� ����
	private void copyFile(File file){
		//����� ����
		String name = file.getName();
		try {
			//���� ����
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			//������ ����
			File copyFile = new File(getSMgr().getFileFolder() + File.separator  + name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(copyFile));
			
			//���� ����
			int data = 0;
			byte[] buf = new byte[8];
			while((data = bis.read(buf)) != -1){
				bos.write(buf, 0, data);
				bos.flush();
				//������� ���
				getToClient().write(data);
				getToClient().flush();
			}//end while
			//���� ����
			bis.close();
			bos.close();
			
			//���� ����Ʈ�� �̸��� ���ٸ� �߰� / �ִٸ� ���� �̸� �״�� ����
			if(!getSMgr().isFile(copyFile.getName()))
				Manager.getMgr().fileAdd(copyFile);
		} catch (Exception e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( copyFile() )");
		}//end catch
	}// copyFile
}

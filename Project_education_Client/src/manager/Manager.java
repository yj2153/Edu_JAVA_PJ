package manager;

import java.io.File;

import server.Message;

/*
 * Manager �� �̱���.
 * GUI�κа� Server�κ��� �̾��ִ� ��Ȱ�� �Ѵ�.
 * Gui���� ��û�� ������ Manager -> Server�� �Ѱ��ش�
 * Server���� ��û�� ������ Manager -> GUI�� �Ѿ��.
 * Server - > GUI, GUI -> Server ���� �������� �ʴ´�. �� manager�� ���ؾ����� ����
 */

public class Manager {
	private static Manager mgr;
	private ClientGUIMgr guiMgr;
	private ServerMgr serverMgr;
	
	private Manager (){
		guiMgr = new ClientGUIMgr();
		serverMgr = new ServerMgr();
	}
	
	public static Manager getMgr(){
		if(mgr == null)
			mgr = new Manager();
		
		return mgr;
	}
	//GUI ===================================================
	//ä��ȭ�鿡 �Ѹ���
	public void receiveChatMsg(String str)
	{
		guiMgr.receiveChatMsg(str);
	}
	//�⺻ �˾�â
	public void MessageGUI(String str , int type){
	Thread m = new Thread(new Runnable() {
			@Override
			public void run() {
				guiMgr.MessageGUI(str, type);
			}
		});
		m.start();
	}
	//���� �˾�â
	public int QMessageGUI(String str, String[] buttons, int type){
		return guiMgr.QMessageGUI(str,buttons,  type);
	}
	
	//���� ����Ʈ ����
	public void userUpdate(String[] users)
	{
		guiMgr.userUpdate(users);
	}
	public void userRemove(String user)
	{
		guiMgr.userRemove(user);
	}
	//���� ����Ʈ ����
	public void fileUpdate(String[] files)
	{
		guiMgr.fileUpdate(files);
	}
	//����Ʈ Ŭ����
	public void listClear(){
		guiMgr.listClear();
	}
	///Server===================================================
	//���� �õ�
	public void connect(String ip , String port, String id)
	{
		serverMgr.connect(ip, port, id);
	}
	//������ ���� �޼���
	public void sendMessage(String id, String str)
	{
		serverMgr.getSend().setMessage(id, str);
	}
	//����  ���� �ʱ�ȭ
	public void connectInit(){
		guiMgr.connectEnabled();
	}
	//���� ���� ��û
	public void fileRequest(File file){
		//���� �غ��ϱ�
		serverMgr.setUpFile(file);
		//���� ��û�޼���
		serverMgr.getSend().setMessage(Message.FILEUPLOAD, file.getName());
	}
	//���ϼ��� ��û
	public void fileRequest(int index){
		//��û�ϴ� ���� ��ȣ
		serverMgr.setDownFileNum(index);
		//���� ��û������
		serverMgr.getSend().setMessage(Message.FILEDOWN, null);
	}
	
	//���� ����
	public File fileFolder(){ 
		return serverMgr.getFileFolder();
	}
	
	//�����Ͻ�����
	public boolean getFileWait() {
		return serverMgr.getFileWait();
	}
	public void setFileWait(boolean b){
		serverMgr.setFileWait(b);
	}
	
	//���� ���̵�
	public String getUserID(){
		return serverMgr.getUserID();
	}
	
	//���ϼ��� ��� ����
	public void setFileConnect(boolean b){
		serverMgr.setFileConnect(b);
	}
	public boolean getFileConnect(){ 
		return serverMgr.getFileConnect();
	}
}

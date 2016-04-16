package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import common.UI;
import manager.Manager;

public class ProgressBarGUI extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	
	//���α׷�����
	private JProgressBar jp;
	//�ִ밪 , ���� ��
	private int max;
	private int value;
	
	//��ư (�Ͻ����� , ����
	private JButton waitBtn , reBtn;
	
	//������
	public ProgressBarGUI(int max, String title){
		this.max = max;
		
		//�ڽ� ������ 
		setSize(300, 150);
		//���� ���� �˷��ִ� ����
		JPanel northPane = new JPanel(new FlowLayout());
		northPane.add(new JLabel(title));
		add(northPane ,BorderLayout.NORTH);
		//===========================================================================================
		//���α׷����� ����
		JPanel centerPane = new JPanel(new FlowLayout());
		jp = new JProgressBar(0, max);
		jp.setStringPainted(true); // �������� ���
		centerPane.add(jp);
		add(centerPane, BorderLayout.CENTER);

		//===========================================================================================
		JPanel southPane = new JPanel(new FlowLayout());
		waitBtn = new JButton("�Ͻ�����");
		reBtn = new JButton("�����");
		reBtn.setEnabled(false);
		southPane.add(waitBtn);
		southPane.add(reBtn);
		
		//��ư �̺�Ʈ ����
		waitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				waitBtn.setEnabled(false);	//������ư ���̱�
				reBtn.setEnabled(true);		//����� �츮��
				//�Ͻ�����
				Manager.getMgr().setFileWait(true);
			}
		});
		
		//�����
		reBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				waitBtn.setEnabled(true);	//������ư �츮��
				reBtn.setEnabled(false);		//����� ���̱�
				//�Ͻ�����
				Manager.getMgr().setFileWait(false);
			}
		});
		//��ư �߰�
		add(southPane, BorderLayout.SOUTH);
		
		//===========================================================================================
		//��� ����
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frm = this.getSize();
		int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
		int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
		this.setLocation(xpos, ypos);
		//������.
		setVisible(true);
	}/// ProgressBarGUI
	
	// ����� ����
	public void ProgressBarPaint(int i) {
		value = i;
	}// ProgressBarPaint

	@Override
	public void run(){
		//���簪 <= �ִ밪
		while(value <= max){
			try {
				Thread.sleep(100);
				//���α׷����� ����
				jp.setValue(value);
			} catch (InterruptedException e) {
				UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( run() )");
			}//end catch
		}//end while
	}// run
	
	//����� �Ϸ� ����
	public boolean ProgressDispose() {
		//0.5�ʵڿ�
		try {
			Thread.sleep(500);
			dispose();
			return true;
		} catch (InterruptedException e) {
			UI.getUI().errorMsg(getClass().getName() + " :���ܹ߻� ( ProgressDispose() )");
		}//end catch
		return false;
	}//ProgressDispose
}

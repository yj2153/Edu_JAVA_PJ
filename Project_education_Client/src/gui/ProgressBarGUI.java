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
	
	//프로그래스바
	private JProgressBar jp;
	//최대값 , 현재 값
	private int max;
	private int value;
	
	//버튼 (일시정지 , 시작
	private JButton waitBtn , reBtn;
	
	//생성자
	public ProgressBarGUI(int max, String title){
		this.max = max;
		
		//박스 사이즈 
		setSize(300, 150);
		//현재 상태 알려주는 제목
		JPanel northPane = new JPanel(new FlowLayout());
		northPane.add(new JLabel(title));
		add(northPane ,BorderLayout.NORTH);
		//===========================================================================================
		//프로그래스바 생성
		JPanel centerPane = new JPanel(new FlowLayout());
		jp = new JProgressBar(0, max);
		jp.setStringPainted(true); // 진행백분율 출력
		centerPane.add(jp);
		add(centerPane, BorderLayout.CENTER);

		//===========================================================================================
		JPanel southPane = new JPanel(new FlowLayout());
		waitBtn = new JButton("일시정지");
		reBtn = new JButton("재시작");
		reBtn.setEnabled(false);
		southPane.add(waitBtn);
		southPane.add(reBtn);
		
		//버튼 이벤트 생성
		waitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				waitBtn.setEnabled(false);	//정지버튼 죽이기
				reBtn.setEnabled(true);		//재실행 살리기
				//일시정지
				Manager.getMgr().setFileWait(true);
			}
		});
		
		//재실행
		reBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				waitBtn.setEnabled(true);	//정지버튼 살리기
				reBtn.setEnabled(false);		//재실행 죽이기
				//일시정지
				Manager.getMgr().setFileWait(false);
			}
		});
		//버튼 추가
		add(southPane, BorderLayout.SOUTH);
		
		//===========================================================================================
		//가운데 정렬
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frm = this.getSize();
		int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
		int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
		this.setLocation(xpos, ypos);
		//보여라.
		setVisible(true);
	}/// ProgressBarGUI
	
	// 진행바 설정
	public void ProgressBarPaint(int i) {
		value = i;
	}// ProgressBarPaint

	@Override
	public void run(){
		//현재값 <= 최대값
		while(value <= max){
			try {
				Thread.sleep(100);
				//프로그래스바 변경
				jp.setValue(value);
			} catch (InterruptedException e) {
				UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( run() )");
			}//end catch
		}//end while
	}// run
	
	//진행바 완료 상태
	public boolean ProgressDispose() {
		//0.5초뒤에
		try {
			Thread.sleep(500);
			dispose();
			return true;
		} catch (InterruptedException e) {
			UI.getUI().errorMsg(getClass().getName() + " :예외발생 ( ProgressDispose() )");
		}//end catch
		return false;
	}//ProgressDispose
}

package common;

import java.util.Scanner;

//���� �Է� ���� ��Ȳ
@SuppressWarnings("serial")
class NegativeNumberException extends Exception{
	public NegativeNumberException()	{		super("0������ ���� �Է��Ͽ����ϴ�.");	}
}
//���� �ƴ� ���� ��Ȳ
@SuppressWarnings("serial")
class StringException extends Exception{
	public StringException()	{		super("������ �ƴ� ���� �Է��Ͽ����ϴ�.");	}
}

//���ǿ� ���� �ʴ� ���� ��Ȳ
@SuppressWarnings("serial")
class EqualsException extends Exception{
	public EqualsException() { super("���ǿ� ���� �ʴ� ���� �Է��Ͽ����ϴ�.");	}
}

public class UI {

	private static UI other;
	private Scanner input;
	
	// ������
	private UI() {
		// �Է� ��ü
		input = new Scanner(System.in);
	}

	// ��ü��ȯ
	public static UI getUI() {
		if (other == null)
			other = new UI();
		return other;
	}

	// ���� ����
	public int nextValue(String str)  {
		// ��¹���
		inputMsg(str);
		int reNum =0;
		do{
			try {
				//�� �Է� �ޱ�
				reNum = readPositive();
				//0���� �Ǵ� ���ڿ� ����
			} catch (NegativeNumberException | StringException e) {
				System.out.println(e.toString());
				inputMsg(str);
			} 
		//����� �� ���� ���ö�����
		}while(reNum <= 0);
		return reNum;
	}	// end nextValue
	
	//�Է¹��� ���� ifA �Ǵ� ifB �� �ƴ϶�� ���Է�
	public String  nextString(String str, String ifA , String ifB)
	{
		String strTemp = "";
		// ��¹���
		inputMsg(str);
		// ���� �Է� �Ǻ��� �� ���� ���� ����.
		do{
			try {
				//���ڿ� �Է� �ޱ�
				strTemp =readEquals(ifA, ifB);
				//���ǿ� ��߳� ����
			} catch (EqualsException e) {
				System.out.println(e.toString());
				inputMsg(str);
			}
			//����� �� ���� ���� ������
		}while(strTemp.length() <= 0);
		
		return strTemp;
	}//nextString(String str, String ifA ,String ifB)
	
	// ���ڿ� ����
	public String nextString(String str) {
		// ��¹���
		inputMsg(str);
		return input.next();
	}//nextString(
	
	// �Է¿� ����Լ�
	public void inputMsg(String str) {
		System.out.print(str);
	} // inputMsg(String str)
	
	// ����Լ�
	public void ouputMsg(String ...str) {
		StringBuffer sb =new StringBuffer(str[0]);
		for(int i = 1; i < str.length; i++)
			sb.append(str[i]);
		
		System.out.println(sb.toString());
	} // ouputMsg(String str)
	
	public void errorMsg(String str) {
		System.err.println(str);
	} // ouputMsg(String str)

//=============================================================================

	//���� �Է� ����ó�� ����
	public int readPositive() throws NegativeNumberException, StringException //���� ���� ���
	{
		int num = 0;
		//���� ���� �������
		if (input.hasNextInt())
		{
			num = input.nextInt();
			//0���ϸ� �Է¹޾Ҵٸ�
			if (num <= 0) {
				input.nextLine(); // ������ �ѱ��.
				// ���� ���ܰ�ü ����
				NegativeNumberException nne = new NegativeNumberException();
				throw nne;
			}//end if
		} // end if
		else
		{
			input.nextLine(); // ������ �ѱ��.
			//�����ƴ� ���ڿ� �Է� ����
			StringException se = new StringException();
			throw se;
		}//end else
		return num;
	}//readPositive
	
	//���� �Է� ����ó�� ����
	public String readEquals(String ifA, String ifB) throws EqualsException//���� ���� ���
	{
		String strTemp = "";
		strTemp = input.next();
		//���ǿ� �´� ���� �Է��ߴٸ�
		if(strTemp.equalsIgnoreCase(ifA) || strTemp.equalsIgnoreCase(ifB))
			return strTemp;
		else//���ǿ� �մ��Ѱ� �ƴ϶��
		{
			input.nextLine(); // ������ �ѱ��.
			EqualsException ee = new EqualsException();
			throw ee;
		}
	}// end readEquals

}

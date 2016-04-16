package common;

import java.util.Scanner;

//음수 입력 예외 상황
@SuppressWarnings("serial")
class NegativeNumberException extends Exception{
	public NegativeNumberException()	{		super("0이하의 값을 입력하였습니다.");	}
}
//정수 아닌 예외 상황
@SuppressWarnings("serial")
class StringException extends Exception{
	public StringException()	{		super("정수가 아닌 값을 입력하였습니다.");	}
}

//조건에 맞지 않는 예외 상황
@SuppressWarnings("serial")
class EqualsException extends Exception{
	public EqualsException() { super("조건에 맞지 않는 값을 입력하였습니다.");	}
}

public class UI {

	private static UI other;
	private Scanner input;
	
	// 생성자
	private UI() {
		// 입력 객체
		input = new Scanner(System.in);
	}

	// 객체반환
	public static UI getUI() {
		if (other == null)
			other = new UI();
		return other;
	}

	// 정수 리턴
	public int nextValue(String str)  {
		// 출력문구
		inputMsg(str);
		int reNum =0;
		do{
			try {
				//값 입력 받기
				reNum = readPositive();
				//0이하 또는 문자열 예외
			} catch (NegativeNumberException | StringException e) {
				System.out.println(e.toString());
				inputMsg(str);
			} 
		//제대로 된 값이 들어올때까지
		}while(reNum <= 0);
		return reNum;
	}	// end nextValue
	
	//입력받은 값이 ifA 또는 ifB 가 아니라면 재입력
	public String  nextString(String str, String ifA , String ifB)
	{
		String strTemp = "";
		// 출력문구
		inputMsg(str);
		// 정수 입력 판별날 때 까지 무한 루프.
		do{
			try {
				//문자열 입력 받기
				strTemp =readEquals(ifA, ifB);
				//조건에 어긋난 예외
			} catch (EqualsException e) {
				System.out.println(e.toString());
				inputMsg(str);
			}
			//제대로 된 값을 받을 때까지
		}while(strTemp.length() <= 0);
		
		return strTemp;
	}//nextString(String str, String ifA ,String ifB)
	
	// 문자열 리턴
	public String nextString(String str) {
		// 출력문구
		inputMsg(str);
		return input.next();
	}//nextString(
	
	// 입력용 출력함수
	public void inputMsg(String str) {
		System.out.print(str);
	} // inputMsg(String str)
	
	// 출력함수
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

	//정수 입력 예외처리 적용
	public int readPositive() throws NegativeNumberException, StringException //예외 전가 명시
	{
		int num = 0;
		//받은 값이 정수라면
		if (input.hasNextInt())
		{
			num = input.nextInt();
			//0이하를 입력받았다면
			if (num <= 0) {
				input.nextLine(); // 내용을 넘긴다.
				// 음수 예외객체 생성
				NegativeNumberException nne = new NegativeNumberException();
				throw nne;
			}//end if
		} // end if
		else
		{
			input.nextLine(); // 내용을 넘긴다.
			//정수아닌 문자열 입력 예외
			StringException se = new StringException();
			throw se;
		}//end else
		return num;
	}//readPositive
	
	//조건 입력 예외처리 적용
	public String readEquals(String ifA, String ifB) throws EqualsException//예외 전가 명시
	{
		String strTemp = "";
		strTemp = input.next();
		//조건에 맞는 값을 입력했다면
		if(strTemp.equalsIgnoreCase(ifA) || strTemp.equalsIgnoreCase(ifB))
			return strTemp;
		else//조건에 합당한게 아니라면
		{
			input.nextLine(); // 내용을 넘긴다.
			EqualsException ee = new EqualsException();
			throw ee;
		}
	}// end readEquals

}

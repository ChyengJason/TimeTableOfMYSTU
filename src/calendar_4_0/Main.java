package calendar_4_0;

import java.io.File;

import org.eclipse.swt.widgets.Display;

public class Main {
	public static Display display = Display.getDefault();
	public static int State;
	public static boolean isShellexist=false; 
	public static boolean loginState = false;
	public static String loginId ="";
	
	public static void main(String args[])  {
		 try {
			 System.out.println("welcome to the beginning");
			 ClassTable cal;
			 Memorandum Memo;
             
			 init();//初始化
			 
			 while(State!=Tag.END)
			 {
				 if(State==Tag.CLASSTABLE  &&  isShellexist==false)
				 {
					 isShellexist=true;
					 cal = new ClassTable(display);//实例化显示课表
				 }
				 if(State==Tag.MEMORANDUM  &&  isShellexist==false)
				 {
					 isShellexist=true;
					  Memo=new Memorandum(display);//实例化备忘录
				 }
			 }
			 
			 if(State==Tag.END)//状态为END
			 {
				 System.out.println("bye");
				 display.close();
			 }
			 
		 }catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	public static void init()
	{
		File classFile=new File("./classSaveFile.txt");
		if(classFile.exists())
		{
			State=Tag.CLASSTABLE;
		}
		else
		{
			State=Tag.MEMORANDUM;
		}
	}
}
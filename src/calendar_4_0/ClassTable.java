package calendar_4_0;

import java.sql.Time;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ClassTable{
	
	private Shell shellCalendar;
	private static Display display;
	
	public ClassTable(Display display)  {
		// TODO Auto-generated constructor stub
		
		System.out.println("ʵ��������");
		this.display=display;
		
		shellCalendar = new Shell(display,SWT.SHELL_TRIM);
		ClassTableWidget calendarWidget = new ClassTableWidget(shellCalendar);
		
       //��shell��ʾ����
		shellCalendar.open();
        shellCalendar.layout();
        calendarWidget.open();
        
        //���ϼ���shell�����Ƿ�ر�
	    while (!shellCalendar.isDisposed()) {
            if (!display.readAndDispatch() )//���û�еõ������µģ�
            {
            	display.sleep();
            }
            
            if(Main.State!=Tag.CLASSTABLE)
            {
            	shellCalendar.close();//�ر�
            	calendarWidget.stopTimer();
            	Main.isShellexist=false;
            	System.out.println("Ŀǰ״̬Ϊ:"+Main.State);
            }
	    }
	  //������رմ���
	    if(shellCalendar.isDisposed() && Main.State==Tag.CLASSTABLE)
	    {
	    	calendarWidget.stopTimer();
        	Main.State=Tag.END;
	    }
	}

}
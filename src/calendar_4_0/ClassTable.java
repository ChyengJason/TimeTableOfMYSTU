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
		
		System.out.println("实例化日历");
		this.display=display;
		
		shellCalendar = new Shell(display,SWT.SHELL_TRIM);
		ClassTableWidget calendarWidget = new ClassTableWidget(shellCalendar);
		
       //将shell显示出来
		shellCalendar.open();
        shellCalendar.layout();
        calendarWidget.open();
        
        //不断监听shell窗口是否关闭
	    while (!shellCalendar.isDisposed()) {
            if (!display.readAndDispatch() )//如果没有得到处理（猜的）
            {
            	display.sleep();
            }
            
            if(Main.State!=Tag.CLASSTABLE)
            {
            	shellCalendar.close();//关闭
            	calendarWidget.stopTimer();
            	Main.isShellexist=false;
            	System.out.println("目前状态为:"+Main.State);
            }
	    }
	  //如果被关闭窗口
	    if(shellCalendar.isDisposed() && Main.State==Tag.CLASSTABLE)
	    {
	    	calendarWidget.stopTimer();
        	Main.State=Tag.END;
	    }
	}

}
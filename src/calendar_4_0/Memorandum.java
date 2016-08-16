package calendar_4_0;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Memorandum {
	
	private Shell shellMemorandum;
	private static Display display;
	
	public Memorandum(Display display)
	{
		System.out.println("实例化备忘录");
		this.display=display;
		
	    shellMemorandum = new Shell(display,SWT.SHELL_TRIM);
	    MemorandumWidget memorandumWidget = new MemorandumWidget(shellMemorandum);
	    
	  //将shell显示出来
	    shellMemorandum.open();
	    shellMemorandum.layout();
	    memorandumWidget.open();

        //不断监听shell窗口是否关闭
	    while (!shellMemorandum.isDisposed()) {
            if (!display.readAndDispatch() )//如果没有得到处理（猜的）
            {
            	display.sleep();
            }
            
            if(Main.State!=Tag.MEMORANDUM)
            {
            	shellMemorandum.close();//关闭
            	memorandumWidget.stopTimer();//停止流量刷新
            	Main.isShellexist=false;
            	System.out.println(Main.State);
            }
	    }
	  //如果被关闭窗口
	    if(shellMemorandum.isDisposed() && Main.State==Tag.MEMORANDUM)
	    {
	    	memorandumWidget.stopTimer();//停止流量刷新
        	Main.State=Tag.END;
	    }
	}
}

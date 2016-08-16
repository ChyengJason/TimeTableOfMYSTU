package calendar_4_0;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Memorandum {
	
	private Shell shellMemorandum;
	private static Display display;
	
	public Memorandum(Display display)
	{
		System.out.println("ʵ��������¼");
		this.display=display;
		
	    shellMemorandum = new Shell(display,SWT.SHELL_TRIM);
	    MemorandumWidget memorandumWidget = new MemorandumWidget(shellMemorandum);
	    
	  //��shell��ʾ����
	    shellMemorandum.open();
	    shellMemorandum.layout();
	    memorandumWidget.open();

        //���ϼ���shell�����Ƿ�ر�
	    while (!shellMemorandum.isDisposed()) {
            if (!display.readAndDispatch() )//���û�еõ������µģ�
            {
            	display.sleep();
            }
            
            if(Main.State!=Tag.MEMORANDUM)
            {
            	shellMemorandum.close();//�ر�
            	memorandumWidget.stopTimer();//ֹͣ����ˢ��
            	Main.isShellexist=false;
            	System.out.println(Main.State);
            }
	    }
	  //������رմ���
	    if(shellMemorandum.isDisposed() && Main.State==Tag.MEMORANDUM)
	    {
	    	memorandumWidget.stopTimer();//ֹͣ����ˢ��
        	Main.State=Tag.END;
	    }
	}
}

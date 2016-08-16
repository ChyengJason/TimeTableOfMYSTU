package calendar_4_0;

import java.awt.Toolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddText {
	
	/**获取屏幕高度宽度 */ 
    int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
    int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
    /** */
    private MemorandumWidget memoWidget = null;
    /** 显示对象 */
    private Display display = null;
    
    /** 窗口对象 */
    private Shell shell = null;
    
    private String selectedDateYearAndMonth = new String();
    
    /** 设置文本框 **/
    private Text recordText;
    
    private String[] storeDayNotes;
    
    private int day;
    
    public  AddText(Shell parent, String selectedDate, String[] storeDayNotes){
    	day = DateStringProcess(selectedDate);
    	
    	this.storeDayNotes=storeDayNotes;
    	
    	/**  获取display */
        display = parent.getDisplay();
        
        /** 获取shell */
        shell=parent;
        
        /**  设置窗口大小*/
        shell.setSize(500, 300);
        
        /** 获取窗口宽度和高度 */
        int shellH = shell.getBounds().height;
        int shellW = shell.getBounds().width;
        
        /**定位对象窗口坐标 */
        shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
        
        /** 设置布局 */
        GridLayout gridLayout = new GridLayout();
        shell.setLayout(gridLayout);
        
        /** 设置文本框*/
        recordText=new Text(shell,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL); 
        recordText.setText(storeDayNotes[day]); 
        GridData gridData = new GridData();
        
        /** 跨两行 */
        gridData.verticalSpan = 2; 
        /** 跨两列 */
        gridData.horizontalSpan=2; 
        /**垂直方向充满 */
        gridData.verticalAlignment = GridData.FILL;
        /** 抢占垂直方向额外空间*/
        gridData.grabExcessVerticalSpace = true; 
        /** 水平方向充满*/
        gridData.horizontalAlignment = GridData.FILL;
        /** 抢占水平方向额外空间 */
        gridData.grabExcessHorizontalSpace = true;
        
        /** gridData用于文本框*/ 
        recordText.setLayoutData(gridData); 
        
        Button saveButton = new Button(shell,SWT.PUSH);
        saveButton.setText("保存");
       
//        以下代码一直运行错误，所以改写其他方法,即使用全局变量进行操作
//        addSelectionListener(new SelectionAdapter() {  
//        	@Override  
//        	public void widgetSelected(SelectionEvent e) {  
//        		 System.out.println("存储的文本是:"+showText );
//        		 storeDayNotes[day] = recordText.getText();
//     	        System.out.println("storeDayNotes["+day+"]="+storeDayNotes[day]);
//     	        shell.setVisible(false);
//     	        //将storeDayNotes写入指定文件
//     	        FileOperation writeIn = new FileOperation();
//     	        writeIn.writeIntoFile(storeDayNotes, selectedDateYearAndMonth, day);
//        	}
//        }
//        );
        saveButton.addMouseListener(new MouseAdapter() {
        	public void mouseUp(MouseEvent e) {
                // 这里添加点击按钮是所实现的功能代码
        		storeNotes();
            }
        }
        ); 
    }
    /** 处理得到selectedDateYearAndMonth */
    public int DateStringProcess(String selectedDate)
    {
        System.out.println("the selectedDate ="+selectedDate);
	    //获取选定日期的年份和月份
	    char[] middleString = selectedDate.toCharArray();
	
	    for(int i = 5; i < selectedDate.length(); i++){
		  if(middleString[i] == '-'){
			  for(int j = 0; j < i; j++){
				  selectedDateYearAndMonth += middleString[j];
			  }
			  break;
		  }
	  }
	
	  String dayString = selectedDate.substring(selectedDateYearAndMonth.length()+1);
	  int day = Integer.parseInt(dayString);
	  return day;
    }
    
    public void setMemoWidget(MemorandumWidget memoWidget)
    {
    	this.memoWidget=memoWidget;
    }
    
    public void storeNotes()
    {
    	System.out.println("存储的文本是:"+recordText.getText() );
    	 storeDayNotes[day] = recordText.getText();
    	 FileOperation writeIn = new FileOperation();
    	 writeIn.writeIntoFile(storeDayNotes, selectedDateYearAndMonth, day);
    	 shell.close();
    	//刷新显示新内容
    	 memoWidget.displayClblDays(true);
    	 return;
    }
}

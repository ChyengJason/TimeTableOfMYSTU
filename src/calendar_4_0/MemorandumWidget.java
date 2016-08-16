package calendar_4_0;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class MemorandumWidget {
	/**获取屏幕高度和宽度*/
    private int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
    
    /** 显示对象 */
    private Display display = null;
    
    /** 窗口对象 */
    private Shell shell = null;
    
    /**Clabel大小的宽度*/
    private static final int GRIDDATAHINT = 20;
    
    /**获取课表*/
    private static final String TOOPTIPTEXT_ABOUT = "关于";
    
    /** 上一月 */
    private static final String TOOPTIPTEXT_PREVIOUSMONTH = "上一月";
    
    /** 流量 */
    private static final String TOOPTIPTEXT_GET_FLOW = "流量";
    
    /** 下一月 */
    private static final String TOOPTIPTEXT_NEXTMONTH = "下一月";
    
    /** 备忘录 */
    private static final String TOOPTIPTEXT_CALENDAR_CLASS = "日历课程";
    
    /** 星期日 —— 星期六 */
    private static final String[] weekdays = { "星期日", "星期一", "星期二", "星期三","星期四", "星期五", "星期六" };
    
    /**年下拉框显示到当前年的前后多少年*/
    private static final int halfYearItemCount =20;
    
    /**年份下拉框选项的数组*/
    private static final String[] years = new String[halfYearItemCount*2+1];
    
    /**月份：1 —— 12 */
    private static final String[] months = { "1", "2", "3", "4","5", "6", "7", "8", "9", "10","11", "12",};
    /** 农历属性 */
    private static final String[] Tiangan = {"甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};
    private static final String[] Dizhi = {"子(鼠)","丑(牛)","寅(虎)","卯(兔)","辰(龙)","巳(蛇)","午(马)","未(羊)","申(猴)","酉(鸡)","戌(狗)","亥(猪)"};
    private String sChineseNum[] ={ "零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
   
    /**选择的日期：yyyy-MM-dd*/ 
    private String selectedDate="";
    /**年的下拉框*/
    Combo cbo_year;
    /**月的下拉框*/
    Combo cbo_month;
    
    /** 设置timer 刷新流量使用*/
    private Timer timer; 
    
    /**显示日的Clable数组*/
    private final CLabel[] clbl_days = new CLabel[42]; // 6 * 7
    
    /** 日历窗体的背景颜色*/
    private Color COLOR_SHELL_BACKGROUND = null;

    /** 星期X标签的背景颜色*/
    private Color COLOR_CLBLWEEKDAY_BACKGROUND = null;

    /** 白色（得到的为系统的颜色，不需要对其进行资源释放）*/
    private Color COLOR_SYSTEM_WHITE = null;

    /** 黄色（得到的为系统的颜色，不需要对其进行资源释放）*/
    private Color COLOR_SYSTEM_YELLOW = null;
    
    private String storeDayNotes[] = new String[32];//记录当前月每日备忘录
    
    /**
     * 窗口是可见状态时，则隐藏窗口，同时把系统栏中图标删除
     * 窗口是隐藏状态时，则显示窗口，并且在系统栏中显示图标
     * @param shell 窗口
     * @param tray 系统栏图标控件
     */
    private static void toggleDisplay(Shell shell, Tray tray) {
        try {
            shell.setVisible(!shell.isVisible());
            tray.getItem(0).setVisible(!shell.isVisible());
            if (shell.getVisible()) {
                shell.setMinimized(false);
                shell.setActive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MemorandumWidget(Shell shellMemorandum) {
    	
    	/**得到display*/
    	this.display = shellMemorandum.getDisplay();
    	 /**得到大小*/
    	Rectangle parentRec =shellMemorandum.getBounds();
    	 /**设置背景*/
        COLOR_SHELL_BACKGROUND = new Color(display,219, 235, 250);
        /**设置标签背景*/
        COLOR_CLBLWEEKDAY_BACKGROUND = new Color(display, 64, 128, 250);
        /**定义白色*/
        COLOR_SYSTEM_WHITE = display.getSystemColor(SWT.COLOR_WHITE);
        /**定义黄色*/
        COLOR_SYSTEM_YELLOW = display.getSystemColor(SWT.COLOR_YELLOW);
        
//      //设置系统栏图标
//        shell.setImage(display.getSystemImage(org.eclipse.swt.SWT.ICON));
//        
//        //构造系统栏控件
//        final Tray tray = display.getSystemTray();
//        final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
//        //程序启动时，窗口是显示的，所以系统栏图标隐藏
//        trayItem.setVisible(false);
//        trayItem.setToolTipText(shell.getText());
// 
//        trayItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                toggleDisplay(shell, tray);
//            }
//        });
// 
//        final Menu trayMenu = new Menu(shell, SWT.POP_UP);
//        MenuItem showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
//        showMenuItem.setText("显示窗口(&s)");
//        //显示窗口，并隐藏系统栏中的图标
//        showMenuItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                toggleDisplay(shell, tray);
//            }
//        });
// 
//        trayMenu.setDefaultItem(showMenuItem);
// 
//        new MenuItem(trayMenu, SWT.SEPARATOR);
// 
//        //系统栏中的退出菜单，程序只能通过这个菜单退出
//        MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
//        exitMenuItem.setText("退出程序(&x)");
//        
//        exitMenuItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                shell.dispose();
//            }
//        });
// 
//        //在系统栏图标点击鼠标右键时的事件，弹出系统栏菜单
//        trayItem.addMenuDetectListener(new MenuDetectListener(){
//            public void menuDetected(MenuDetectEvent e) {
//                trayMenu.setVisible(true);
//            }
//        });
// 
//        trayItem.setImage(shell.getImage());
// 
//        //注册窗口事件监听器
//        shell.addShellListener(new ShellAdapter() {
// 
//            //点击窗口最小化按钮时，窗口隐藏，系统栏显示图标
//            public void shellIconified(ShellEvent e) {
//                toggleDisplay(shell, tray);
//            }
// 
//            //点击窗口关闭按钮时，并不终止程序，而时隐藏窗口，同时系统栏显示图标
//            public void shellClosed(ShellEvent e) {
//                e.doit = false; //消耗掉原本系统来处理的事件
//                toggleDisplay(shell, tray);
//            }
//        });  

        
        this.shell=shellMemorandum;
         /**设置shell大小*/
        shell.setSize(screenW*3/5, screenH);
         /**设置shell位置*/
        shell.setLocation(screenW/3 , 0);
         /**设置标题*/
        shell.setText("快捷备忘录");
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 7;
        gridLayout.makeColumnsEqualWidth = true;
        shell.setLayout(gridLayout);
        
        shell.setBackground(COLOR_SHELL_BACKGROUND);
        
        cbo_year = new Combo(shell, SWT.DROP_DOWN);
        cbo_year.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        cbo_year.addSelectionListener(cboSelectionListener);
        cbo_year.addKeyListener(cboKeyListener);

        CLabel clabel_y = new CLabel(shell, SWT.CENTER );
        clabel_y.setBackground(COLOR_SHELL_BACKGROUND);
        clabel_y.setSize(5,10);
        clabel_y.setLayoutData(new GridData(SWT.LEFT_TO_RIGHT,SWT.CENTER,false,false));
        clabel_y.setText("年");

        cbo_month = new Combo(shell, SWT.DROP_DOWN);
        cbo_month.setItems(months);
        cbo_month.select(Calendar.getInstance().get(Calendar.MONTH));
        cbo_month.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        cbo_month.addSelectionListener(cboSelectionListener);
        cbo_month.addKeyListener(cboKeyListener);
        
        CLabel clabel_m= new CLabel(shell, SWT.CENTER);
        clabel_m.setBackground(COLOR_SHELL_BACKGROUND);
        clabel_m.setSize(5,5);
        clabel_m.setLayoutData(new GridData(SWT.LEFT_TO_RIGHT,SWT.CENTER,false,false));
        clabel_m.setText("月");
        
        final CLabel clabel_flow= new CLabel(shell, SWT.BORDER | SWT.MULTI);
        
        clabel_flow.setBackground(COLOR_SHELL_BACKGROUND);
//        clabel_flow.setSize(20,5);
        clabel_flow.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false));
        clabel_flow.setText("     流量监控      ");
        clabel_flow.setFont(new org.eclipse.swt.graphics.Font(display, "微软雅黑",11, SWT.NORMAL));
        
      //流量刷新
        timer = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Display.getDefault().syncExec( 
            	  new Runnable() 
            	  {
            	    public void run() 
            	    {
            	    	String flow=new GetFlow().GetFlowString();
            	    	clabel_flow.setText(""+flow);
            	    	System.out.println("memorandum:"+flow);
            	    }
            	  });
            }
        });
        
        //关于
        Button btn_getTimetable = new Button(shell, SWT.CENTER |SWT.BORDER|SWT.PUSH);
        btn_getTimetable.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        btn_getTimetable.setToolTipText(TOOPTIPTEXT_ABOUT);
        btn_getTimetable.setText(TOOPTIPTEXT_ABOUT);
        btn_getTimetable.addSelectionListener(btnSelectionListener);
      
        //日历
        Button btn_login = new Button(shell,  SWT.CENTER );
        btn_login.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        btn_login.setToolTipText(TOOPTIPTEXT_CALENDAR_CLASS );
        btn_login.addSelectionListener(btnSelectionListener);
        btn_login.setText("日历课程");
        
        GridData gridData_1 = null;
        // 将 星期日 —— 星期六 的标签显示出来
        for (int i = 0; i < weekdays.length; i++) {
            CLabel clbl_weekDay = new CLabel(shell, SWT.CENTER | SWT.SHADOW_OUT);
            clbl_weekDay.setForeground(COLOR_SYSTEM_WHITE);
            clbl_weekDay.setBackground(COLOR_CLBLWEEKDAY_BACKGROUND);
            gridData_1 = new GridData(SWT.FILL,SWT.CENTER,true,false);
            gridData_1.widthHint = GRIDDATAHINT;
            gridData_1.heightHint = GRIDDATAHINT;
            clbl_weekDay.setLayoutData(gridData_1);
            clbl_weekDay.setText(weekdays[i]);
         
        }

        // 将当月的所有 日 的标签显示出来
        for (int i = 0; i < clbl_days.length; i++) {
            clbl_days[i] = new CLabel(shell, SWT.FLAT | SWT.CENTER);
            clbl_days[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
            clbl_days[i].setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
            clbl_days[i].addMouseListener(clblMouseListener);
           
        }
        
//        //将shell显示出来
//		shellCalendar.open();
//        shellCalendar.layout();
    }
    
    /**
     * 给年的下拉框设置设置选项
     * @param middleYear 中间年份
     */
    private void setCboYearItems(int middleYear){
        int selectIndex = halfYearItemCount;
        //确保不出现负的年份
        if(middleYear < halfYearItemCount){
            selectIndex = middleYear;
            middleYear =  halfYearItemCount;
        }
        int index = 0;
        for (int i = middleYear-halfYearItemCount; i <= middleYear+halfYearItemCount; i++) {
            years[index++] = ""+i;
        }
        cbo_year.setItems(years);
        cbo_year.select(selectIndex);
    }
    
    /** 
     * 得到指定年月的天数
     * 
     * @param year
     *            年
     * @param month
     *            月(1-12)
     * @return 指定年月的天数，如：year=2008,month=1 就返回 2008年1月的天数：31
     */ 
    private int getDayCountOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // 因为 Calendar中的 month 是 0-11，故month要减去1
        Calendar cal2 = (Calendar) cal.clone();
        cal2.add(Calendar.MONTH, 1);
        cal2.add(Calendar.DAY_OF_MONTH, -1);
        return cal2.get(Calendar.DAY_OF_MONTH);
    } 
    
    /**
     * 得到下拉框中的年和月
     */
    private int[] getYearAndMonth(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR); //年
        int month = now.get(Calendar.MONTH)+1; //月

        if("".equals(cbo_year.getText().trim()))
            cbo_year.setText(year+"");
        else{
            try {
                year = Integer.parseInt(cbo_year.getText().trim());
            } catch (NumberFormatException e) {
                //年 下拉框中的文本不是一个Int型数字，则设为当前年
                cbo_year.setText(year+"");
            }
        }

        if("".equals(cbo_month.getText().trim()))
            cbo_month.setText(month+"");
        else{
            try {
                month = Integer.parseInt(cbo_month.getText().trim());
                if(month<1){
                    month = 1;
                    cbo_month.setText("1");
                }else if(month>12){
                    month = 12;
                    cbo_month.setText("12");
                }
            } catch (NumberFormatException e) {
//              月 下拉框中的文本不是一个Int型数字，则设为当前月
                cbo_month.setText(month+"");
            }
        }
        return new int[]{year, month};
    }
    
    public String open() {
        displayClblDays(true);
        shell.open();
        timer.start();//开始监控流量
//        while (!shell.isDisposed()) {
//            if (!display.readAndDispatch()) {
//                display.sleep();
//            }
//        }
        COLOR_SHELL_BACKGROUND.dispose();
        COLOR_CLBLWEEKDAY_BACKGROUND.dispose();
        return selectedDate;
    } 
    
    //停止timer刷新
    public void stopTimer()
    {
    	timer.stop();
    	System.out.println("Memorandum stop timer");
    }
    /** 
     * 为所有的 日 标签设置相关属性 
     * @param reflushCboYearItems 是否刷新年下拉框中的选项 
     */ 
    public void displayClblDays(boolean reflushCboYearItems) {
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE); // 日
        int currentMonth=calendar.get(Calendar.MONTH)+1;//月
        int currentYear=calendar.get(Calendar.YEAR);
        if(currentMonth==13)currentMonth=1;
        
        int[] yearAndMonth = getYearAndMonth();
        int year = yearAndMonth[0]; //年
        int month = yearAndMonth[1]; //月

        calendar.set(year,month-1,1); //Calendar中的month为0-11，故在此处month要减去1
        int beginIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; //得到指定月份的第一天为星期几，Calendar对象返回的1-7对应星期日-星期六，故减去1
        int endIndex = beginIndex + getDayCountOfMonth(year, month) - 1;
//        System.out.println("指定月份的第一天是："+beginIndex);

        //从文件读取storeDayNotes数组
        String datePassToFileOperation = year + "-" + month + "";

        System.out.println("年"+year+" 月 ="+month);
        System.out.println("文件数据="+datePassToFileOperation);
        
        if(reflushCboYearItems)
            setCboYearItems(year);

        FileOperation readMonth = new FileOperation();
        
        try {
        	//读取保存的月份备忘录数据
			readMonth.readMonthNotes(storeDayNotes, datePassToFileOperation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int day=1;
        for (int i = 0; i < clbl_days.length; i++) {
            if (i >= beginIndex && i <= endIndex) {
            	
            	String content = storeDayNotes[i-beginIndex+1];
            	
            	int lineNum = lineNumOfBox(content);
            	if(lineNum>=4)
            		clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "微软雅黑",9, SWT.NORMAL));
            	else
            		if(lineNum==0)
            		    clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "微软雅黑",12, SWT.NORMAL));
            		else
            		    clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "微软雅黑",10, SWT.NORMAL));
              
            	if(lineNum>=5)content=SubstringLinesOfBox(content,6);
            	
            	clbl_days[i].setText((day+"\n"+content));
//            	clbl_days[i].setText((day+"\n"+lineNum+content));
                clbl_days[i].setToolTipText(year + "-" + month+"-"+day);
                if (day == currentDate && month==currentMonth &&year==currentYear){
                    clbl_days[i].setBackground(COLOR_SYSTEM_YELLOW);
                }else
                    clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
                day++;
            } else {
                clbl_days[i].setText("");
                clbl_days[i].setToolTipText("");
                clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
            }
        }
    }
    
    public int lineNumOfBox(String boxContent)//获取每个格子里有几行
    {
    	if(boxContent.equals(""))return 0;
    	String tem[]=boxContent.split("\n");
    	return tem.length;
    }
    
    public String SubstringLinesOfBox(String BoxContent,int num)//返回前num行的boxConstent
    {
    	if(num<=0)return BoxContent;
    	
    	String tem[]=BoxContent.split("\n");
    	String content="";
    	int i=0;
    	for(i=0;i<num-2;i++)
    		content=content+tem[i]+"\n";
    	content+=tem[i];
    	return content;
    }
    
    private MouseListener clblMouseListener = new MouseAdapter() {
        @Override 
        public void mouseDoubleClick(MouseEvent e) {
        	System.out.println("once double click");
        	AddText addtext;//出现文本框
        	selectedDate = ((CLabel) e.widget).getToolTipText();
        	try{
        		Display display = Display.getDefault();
        		Shell shellText = new Shell(display,SWT.SHELL_TRIM);
        		
        		addtext = new AddText(shellText, selectedDate, storeDayNotes);
        		addtext.setMemoWidget(ReturnThis());

        		//  git clone  git@10.22.25.117:/home/git/calendar.git	
                shellText.open();
                shellText.layout();
//                System.out.println("@Memorandum.java!!啦啦啦");
                displayClblDays(true);
        		
                while (!shellText.isDisposed()) {
                    if (!display.readAndDispatch())
                        display.sleep();
                }
                
        	}catch (Exception e_TextInput) {
        		e_TextInput.printStackTrace();
        	}
        	
        }
    };
    
    public MemorandumWidget ReturnThis()
    {
    	return this;
    }
    
    private SelectionListener btnSelectionListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            String tooptip = ((Button)e.widget).getToolTipText();
            int[] yearAndMonth = getYearAndMonth();
            int year = yearAndMonth[0];
            int month = yearAndMonth[1];
            boolean reflushCboyearItems = true;
            
            if(TOOPTIPTEXT_CALENDAR_CLASS.equals(tooptip)){
            	System.out.println("日历课程");
            	
          	    Main.State=Tag.CLASSTABLE;
          	    return;
            }
            
            if(TOOPTIPTEXT_ABOUT .equals(tooptip)){
            	MessageBox dialog = new MessageBox(shell);
				dialog.setText("About");
				String aboutInfo = "\t桌面日历课表 1.0 \t\n\t开发成员：\n\t程俊森，刘旭\n\t邓富豪,李秀敏\n\t陈玉娇，王妙娜";
				dialog.setMessage(aboutInfo);
				dialog.open();
            	
            	      	
            	      	
            }
            
            displayClblDays(reflushCboyearItems);
        }
    };
    
    private SelectionListener cboSelectionListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            boolean reflushCboyearItems = e.widget == cbo_year ? true : false;
            displayClblDays(reflushCboyearItems);
        }
    };
    
    private KeyListener cboKeyListener = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.keyCode == 16777296 || event.keyCode == 13) {
                boolean reflushCboyearItems = event.widget == cbo_year ? true : false;
                displayClblDays(reflushCboyearItems);
            }else{
                //只能输入数字
                if((event.keyCode<'0' || event.keyCode >'9') && !(event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN))
                    event.doit = false;
            }
        }
    };
}

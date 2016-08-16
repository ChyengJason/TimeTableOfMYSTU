package calendar_4_0;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class ClassTableWidget {
	
	/**��ȡ��Ļ�߶ȺͿ��*/
    private int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
    private int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
    
    /** ��ʾ���� */
    private Display display = null;
    
    /** ���ڶ��� */
    private Shell shell = null;
    
    /**Clabel��С�Ŀ��*/
    private static final int GRIDDATAHINT = 20;
    
    /**��ȡ�α�*/
    private static final String TOOPTIPTEXT_GET_TIMETABLE = "��ȡ�α�";
    
    /** ��һ�� */
    private static final String TOOPTIPTEXT_PREVIOUSMONTH = "��һ��";
    
    /** ���� */
    private static final String TOOPTIPTEXT_GET_FLOW = "����";
    
    /** ��һ�� */
    private static final String TOOPTIPTEXT_NEXTMONTH = "��һ��";
    
    /** ����¼ */
    private static final String MEMORANDUM = "����¼";
    
    /** ������ ���� ������ */
    private static final String[] weekdays = { "������", "����һ", "���ڶ�", "������","������", "������", "������" };
    
    /**����������ʾ����ǰ���ǰ�������*/
    private static final int halfYearItemCount =5;
    
    /**���������ѡ�������*/
    private static final String[] years = new String[halfYearItemCount*2+1];
    
    /**�·ݣ�1 ���� 12 */
    private static final String[] months = { "1", "2", "3", "4","5", "6", "7", "8", "9", "10","11", "12",};
    /** ũ������ */
    private static final String[] Tiangan = {"��","��","��","��","��","��","��","��","��","��"};
    private static final String[] Dizhi = {"��(��)","��(ţ)","��(��)","î(��)","��(��)","��(��)","��(��)","δ(��)","��(��)","��(��)","��(��)","��(��)"};
    private String sChineseNum[] ={ "��", "һ", "��", "��", "��", "��", "��", "��", "��", "��", "ʮ" };
   
    /**ѡ������ڣ�yyyy-MM-dd*/ 
    private String selectedDate="";
    /**���������*/
    private Combo cbo_year;
    /**�µ�������*/
    private Combo cbo_month;
    
    /** ����timer ˢ������ʹ��*/
    private Timer timer; 
    
    /** ���γ��� */
    public static final int TOTAL_MAX_CLASS_AMOUNT = 42;
    
    /**��ʾ�յ�Clable����*/
    private final CLabel[] clbl_days = new CLabel[42]; // 6 * 7
    
    /** ��������ı�����ɫ*/
    private Color COLOR_SHELL_BACKGROUND = null;

    /** ����X��ǩ�ı�����ɫ*/
    private Color COLOR_CLBLWEEKDAY_BACKGROUND = null;

    /** ��ɫ���õ���Ϊϵͳ����ɫ������Ҫ���������Դ�ͷţ�*/
    private Color COLOR_SYSTEM_WHITE = null;

    /** ��ɫ���õ���Ϊϵͳ����ɫ������Ҫ���������Դ�ͷţ�*/
    private Color COLOR_SYSTEM_YELLOW = null;
    
    private Color COLOR_SYSTEM_CYAN = null;
    
    /**�������ÿ���Сʱˢ��һ��ҳ�� */
    private final int TIME_OF_REFLESH = 1800;//
    private int monitor = TIME_OF_REFLESH / 5;
    
    /**
     * �����ǿɼ�״̬ʱ�������ش��ڣ�ͬʱ��ϵͳ����ͼ��ɾ��
     * ����������״̬ʱ������ʾ���ڣ�������ϵͳ������ʾͼ��
     * @param shell ����
     * @param tray ϵͳ��ͼ��ؼ�
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
    
    public ClassTableWidget(Shell shellCalendar) {
    	
    	/**�õ�display*/
    	this.display = shellCalendar.getDisplay();
    	 /**�õ���С*/
    	Rectangle parentRec = shellCalendar.getBounds();
    	 /**���ñ���*/
        COLOR_SHELL_BACKGROUND = new Color(display,219, 235, 250);
        /**���ñ�ǩ����*/
        COLOR_CLBLWEEKDAY_BACKGROUND = new Color(display, 64, 128, 250);
        /**�����ɫ*/
        COLOR_SYSTEM_WHITE = display.getSystemColor(SWT.COLOR_WHITE);
        /**�����ɫ*/
        COLOR_SYSTEM_YELLOW = display.getSystemColor(SWT.COLOR_YELLOW);
        
        COLOR_SYSTEM_CYAN = display.getSystemColor(SWT.COLOR_CYAN);
        
//        //����ϵͳ��ͼ��
////        shell.setImage(display.getSystemImage(SWT.ICON_WORKING));
//        
//        //����ϵͳ���ؼ�
//        final Tray tray = display.getSystemTray();
//        final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
//        //��������ʱ����������ʾ�ģ�����ϵͳ��ͼ������
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
//        showMenuItem.setText("��ʾ����(&s)");
//        //��ʾ���ڣ�������ϵͳ���е�ͼ��
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
//        //ϵͳ���е��˳��˵�������ֻ��ͨ������˵��˳�
//        MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
//        exitMenuItem.setText("�˳�����(&x)");
//        
//        exitMenuItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                shell.dispose();
//            }
//        });
// 
//        //��ϵͳ��ͼ��������Ҽ�ʱ���¼�������ϵͳ���˵�
//        trayItem.addMenuDetectListener(new MenuDetectListener(){
//            public void menuDetected(MenuDetectEvent e) {
//                trayMenu.setVisible(true);
//            }
//        });
// 
//        trayItem.setImage(shell.getImage());
// 
//        //ע�ᴰ���¼�������
//        shell.addShellListener(new ShellAdapter() {
// 
//            //���������С����ťʱ���������أ�ϵͳ����ʾͼ��
//            public void shellIconified(ShellEvent e) {
//                toggleDisplay(shell, tray);
//            }
// 
//            //������ڹرհ�ťʱ��������ֹ���򣬶�ʱ���ش��ڣ�ͬʱϵͳ����ʾͼ��
//            public void shellClosed(ShellEvent e) {
//                e.doit = false; //���ĵ�ԭ��ϵͳ��������¼�
//                toggleDisplay(shell, tray);
//            }
//        });  

        
        this.shell=shellCalendar;
         /**����shell��С*/
        shell.setSize(screenW*3/5, screenH);
         /**����shellλ��*/     
        String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
         //windows����
        	shell.setLocation(screenW/3 , 0);
        }  
        else
        {
        //��������
        	shell.setLocation(screenW , 0);
        }
         /**���ñ���*/
        shell.setText("�γ�������");
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 7;
        gridLayout.makeColumnsEqualWidth = true;//�Ƿ�Ⱦ�

        shell.setLayout(gridLayout);
        
        shell.setBackground(COLOR_SHELL_BACKGROUND);
        
        cbo_year = new Combo(shell, SWT.DROP_DOWN);
        cbo_year.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        cbo_year.addSelectionListener(cboSelectionListener);
        cbo_year.addKeyListener(cboKeyListener);

        CLabel clabel_y = new CLabel(shell, SWT.CENTER );
        clabel_y.setBackground(COLOR_SHELL_BACKGROUND);
//        clabel_y.setSize(3,5);
        clabel_y.setLayoutData(new GridData(SWT.LEFT_TO_RIGHT,SWT.CENTER,false,false));
        clabel_y.setText("��");

        cbo_month = new Combo(shell, SWT.DROP_DOWN);
        cbo_month.setItems(months);
        cbo_month.select(Calendar.getInstance().get(Calendar.MONTH));
        cbo_month.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        cbo_month.addSelectionListener(cboSelectionListener);
        cbo_month.addKeyListener(cboKeyListener);
        
        CLabel clabel_m= new CLabel(shell, SWT.CENTER);
        clabel_m.setBackground(COLOR_SHELL_BACKGROUND);
//        clabel_m.setSize(3,5);
        clabel_m.setLayoutData(new GridData(SWT.LEFT_TO_RIGHT,SWT.CENTER,true,false));
        clabel_m.setText("��");
        
        final CLabel clabel_flow= new CLabel(shell, SWT.BORDER | SWT.MULTI);
        
        clabel_flow.setBackground(COLOR_SHELL_BACKGROUND);
//        clabel_flow.setSize(20,5);
        clabel_flow.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false));
        clabel_flow.setText("     �������      ");
        clabel_flow.setFont(new org.eclipse.swt.graphics.Font(display, "΢���ź�",11, SWT.NORMAL));
        //����ˢ��
        timer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Display.getDefault().syncExec( 
            	  new Runnable() 
            	  {
            	    public void run() 
            	    {
            	    	monitor-=1;
            	    	String flow=new GetFlow().GetFlowString();
            	    	clabel_flow.setText(""+flow);
            	    	System.out.println("calendar:"+flow);
            	    	if(monitor==0)
            	    	{
//            	    		shell.redraw();
            	    		displayClblDays(true);
            	    		System.out.println("ˢ��");
            	    		monitor=TIME_OF_REFLESH / 5;//�������ü����
            	    	}
            	    }
            	  });
            }
        });
    
        
        //��ȡ�α�
        Button btn_getTimetable = new Button(shell, SWT.CENTER | SWT.PUSH);
        GridData gridData=new GridData(SWT.FILL,SWT.CENTER,true,false);

        btn_getTimetable.setLayoutData(gridData);
        btn_getTimetable.setToolTipText(TOOPTIPTEXT_GET_TIMETABLE);
        btn_getTimetable.setText(TOOPTIPTEXT_GET_TIMETABLE);
        btn_getTimetable.addSelectionListener(btnSelectionListener);
      
        //����¼
        Button btn_login = new Button(shell,  SWT.CENTER );
        btn_login.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        btn_login.setToolTipText(MEMORANDUM );
        btn_login.addSelectionListener(btnSelectionListener);
        btn_login.setText("����¼");
        
        GridData gridData_1 = null;
        // �� ������ ���� ������ �ı�ǩ��ʾ����
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
        // �����µ����� �� �ı�ǩ��ʾ����
        for (int i = 0; i < clbl_days.length; i++) {
            clbl_days[i] = new CLabel(shell, SWT.FLAT | SWT.CENTER);
            clbl_days[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
            clbl_days[i].setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
            clbl_days[i].addMouseListener(clblMouseListener);
        }
        
//        //��shell��ʾ����
//		shellCalendar.open();
//        shellCalendar.layout();
    }
    
    /**
     * �������������������ѡ��
     * @param middleYear �м����
     */
    private void setCboYearItems(int middleYear){
        int selectIndex = halfYearItemCount;
        //ȷ�������ָ������
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
     * �õ�ָ�����µ�����
     * 
     * @param year
     *            ��
     * @param month
     *            ��(1-12)
     * @return ָ�����µ��������磺year=2008,month=1 �ͷ��� 2008��1�µ�������31
     */ 
    private int getDayCountOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // ��Ϊ Calendar�е� month �� 0-11����monthҪ��ȥ1
        Calendar cal2 = (Calendar) cal.clone();
        cal2.add(Calendar.MONTH, 1);
        cal2.add(Calendar.DAY_OF_MONTH, -1);
        return cal2.get(Calendar.DAY_OF_MONTH);
    } 
    
    /**
     * �õ��������е������
     */
    private int[] getYearAndMonth(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR); //��
        int month = now.get(Calendar.MONTH)+1; //��

        if("".equals(cbo_year.getText().trim()))
            cbo_year.setText(year+"");
        else{
            try {
                year = Integer.parseInt(cbo_year.getText().trim());
            } catch (NumberFormatException e) {
                //�� �������е��ı�����һ��Int�����֣�����Ϊ��ǰ��
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
//              �� �������е��ı�����һ��Int�����֣�����Ϊ��ǰ��
                cbo_month.setText(month+"");
            }
        }
        return new int[]{year, month};
    }
    
    public String open() {
        displayClblDays(true);
        shell.open();
        timer.start();//��ʼ�������
        COLOR_SHELL_BACKGROUND.dispose();
        COLOR_CLBLWEEKDAY_BACKGROUND.dispose();
        return selectedDate;
    } 
  //ֹͣtimerˢ��
    public void stopTimer() 
    {
//    	timer.wait(1000);
    	timer.stop();
    	System.out.println("calendar stop timer");
    }
    /** 
     * Ϊ���е� �� ��ǩ����������� 
     * @param reflushCboYearItems �Ƿ�ˢ�����������е�ѡ�� 
     */ 

    private void displayClblDays(boolean reflushCboYearItems) {
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE); // ��
        int currentMonth = calendar.get(Calendar.MONTH)+1;//��
        int currentYear = calendar.get(Calendar.YEAR);//��
        int currentWeek=calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = new Date().getHours(); 
     
        if(currentMonth==13)currentMonth=1;
        
        int[] yearAndMonth = getYearAndMonth();
        int year = yearAndMonth[0]; //��
        int month = yearAndMonth[1]; //��

        calendar.set(year,month-1,1); //Calendar�е�monthΪ0-11�����ڴ˴�monthҪ��ȥ1
        int beginIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; //�õ�ָ���·ݵĵ�һ��Ϊ���ڼ���Calendar���󷵻ص�1-7��Ӧ������-���������ʼ�ȥ1
        int endIndex = beginIndex + getDayCountOfMonth(year, month) - 1;

        if(reflushCboYearItems)
            setCboYearItems(year);

        int day=1;
        /** �α� */
        String timeTable[] = new String[TOTAL_MAX_CLASS_AMOUNT];
        
        //��ʼ��timetable
        for(int i=0; i<timeTable.length;i++)
        {
        	timeTable[i]="";
        }
        
    	GetTimetable getTimetable = new GetTimetable(timeTable);
    
    	for (int i = 0; i < clbl_days.length; i++) {
    		  if( i >= 0 && i <= 42){//�޸�
            	clbl_days[i].setText(timeTable[i]);
                clbl_days[i].setToolTipText(year + "-" + month+"-"+day);
              
               clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);      
                day++;
            
            } else {
                clbl_days[i].setText("");
                clbl_days[i].setToolTipText("");
                clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
            }  
        }
    	
    	if(isTimeTableNull (timeTable)==false)//�α�Ϊ��ʱ��
    	{
    		int classNum=classShowLight(timeTable,currentWeek,currentHour);
    		clbl_days[classNum].setBackground(COLOR_SYSTEM_CYAN);
    	}
    }
    
    public int classShowLight(String timeTable[],int currentWeek, int currentHour)//�жϸø�����ʾ���ǵڼ��ڿΣ������Ͽ�
    {
    	  int hour=currentHour;
    	  boolean isShowLight=false;
    	  int basicNum=0;
    	  int classNum=0;
    	  int week=currentWeek;
    	   
    	  while(!isShowLight)
    	  {
    		  basicNum=week-1;
    		  if(0<=hour&&hour<=9)//0~9��
    		  {
    			  classNum=basicNum;
    		  }
    		  
    		  if(10<=hour&&hour<=13)//10-13
    		  {
    			  classNum=basicNum+7;
    		  }
    		  
    		  if(14<=hour&&hour<=15)//14-15
    		  {
    			  classNum=basicNum+14;
    		  }
    		  
    		  if(16<=hour&&hour<=18)//16-18
    		  {
    			  classNum=basicNum+21;
    		  }
    		  
    		  if(19<=hour&&hour<=21)//19-21
    		  {
    			  classNum=basicNum+28;
    		  }
    	
    		  if(22<=hour&&hour<=24)
    		  {
    			  classNum=basicNum+35;
    				  hour=0;
    				  week+=1;
    				  if(week > 7) week=1;
    		  }
    		  else
    		  {
    			  if(timeTable[classNum]=="")
		    	  hour++;
		      else
		    	  isShowLight=true;
    		  }
    		  
    	  }
    	  return classNum;
    }
    
    //�жϿα��Ƿ�Ϊ��
    public  boolean isTimeTableNull (String timeTable[])
    {
    	if(timeTable.length==0)return false;
	    for(int i=0;i<timeTable.length;i++)
	   {
		   if(timeTable[i]!="")
		   {
			   return false;
		   }
	  }
	    return true;
    }
    
    private MouseListener clblMouseListener = new MouseAdapter() {
        @Override 
        public void mouseDoubleClick(MouseEvent e) {
        	System.out.println("once double click");
        }
    };
    
    private SelectionListener btnSelectionListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            String tooptip = ((Button)e.widget).getToolTipText();
            int[] yearAndMonth = getYearAndMonth();
            int year = yearAndMonth[0];
            int month = yearAndMonth[1];
            boolean reflushCboyearItems = true;
            
            if(MEMORANDUM.equals(tooptip)){
            	System.out.println("����¼");
          	    Main.State=Tag.MEMORANDUM;
          	    return;
            }
            
            if(TOOPTIPTEXT_GET_TIMETABLE .equals(tooptip)){
            	System.out.println("��ȡ�α�");
            	//����ǺϷ��û���¼������ֱ�ӵ����α������
            	if(Main.loginState){
            		new CourseQueryUI(Main.loginId, display);
            	}
            	//����ǵ�һ�ε�¼���򵯳���¼���档
            	else{
                	new LoginUI(display);  
            	}	
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
                //ֻ����������
                if((event.keyCode<'0' || event.keyCode >'9') && !(event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN))
                    event.doit = false;
            }
        }
    };
}

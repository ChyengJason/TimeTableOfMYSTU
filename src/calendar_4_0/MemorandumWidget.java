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
    private static final String TOOPTIPTEXT_ABOUT = "����";
    
    /** ��һ�� */
    private static final String TOOPTIPTEXT_PREVIOUSMONTH = "��һ��";
    
    /** ���� */
    private static final String TOOPTIPTEXT_GET_FLOW = "����";
    
    /** ��һ�� */
    private static final String TOOPTIPTEXT_NEXTMONTH = "��һ��";
    
    /** ����¼ */
    private static final String TOOPTIPTEXT_CALENDAR_CLASS = "�����γ�";
    
    /** ������ ���� ������ */
    private static final String[] weekdays = { "������", "����һ", "���ڶ�", "������","������", "������", "������" };
    
    /**����������ʾ����ǰ���ǰ�������*/
    private static final int halfYearItemCount =20;
    
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
    Combo cbo_year;
    /**�µ�������*/
    Combo cbo_month;
    
    /** ����timer ˢ������ʹ��*/
    private Timer timer; 
    
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
    
    private String storeDayNotes[] = new String[32];//��¼��ǰ��ÿ�ձ���¼
    
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
    
    public MemorandumWidget(Shell shellMemorandum) {
    	
    	/**�õ�display*/
    	this.display = shellMemorandum.getDisplay();
    	 /**�õ���С*/
    	Rectangle parentRec =shellMemorandum.getBounds();
    	 /**���ñ���*/
        COLOR_SHELL_BACKGROUND = new Color(display,219, 235, 250);
        /**���ñ�ǩ����*/
        COLOR_CLBLWEEKDAY_BACKGROUND = new Color(display, 64, 128, 250);
        /**�����ɫ*/
        COLOR_SYSTEM_WHITE = display.getSystemColor(SWT.COLOR_WHITE);
        /**�����ɫ*/
        COLOR_SYSTEM_YELLOW = display.getSystemColor(SWT.COLOR_YELLOW);
        
//      //����ϵͳ��ͼ��
//        shell.setImage(display.getSystemImage(org.eclipse.swt.SWT.ICON));
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

        
        this.shell=shellMemorandum;
         /**����shell��С*/
        shell.setSize(screenW*3/5, screenH);
         /**����shellλ��*/
        shell.setLocation(screenW/3 , 0);
         /**���ñ���*/
        shell.setText("��ݱ���¼");
        
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
        clabel_y.setText("��");

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
        clabel_m.setText("��");
        
        final CLabel clabel_flow= new CLabel(shell, SWT.BORDER | SWT.MULTI);
        
        clabel_flow.setBackground(COLOR_SHELL_BACKGROUND);
//        clabel_flow.setSize(20,5);
        clabel_flow.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false));
        clabel_flow.setText("     �������      ");
        clabel_flow.setFont(new org.eclipse.swt.graphics.Font(display, "΢���ź�",11, SWT.NORMAL));
        
      //����ˢ��
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
        
        //����
        Button btn_getTimetable = new Button(shell, SWT.CENTER |SWT.BORDER|SWT.PUSH);
        btn_getTimetable.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        btn_getTimetable.setToolTipText(TOOPTIPTEXT_ABOUT);
        btn_getTimetable.setText(TOOPTIPTEXT_ABOUT);
        btn_getTimetable.addSelectionListener(btnSelectionListener);
      
        //����
        Button btn_login = new Button(shell,  SWT.CENTER );
        btn_login.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        btn_login.setToolTipText(TOOPTIPTEXT_CALENDAR_CLASS );
        btn_login.addSelectionListener(btnSelectionListener);
        btn_login.setText("�����γ�");
        
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
//        while (!shell.isDisposed()) {
//            if (!display.readAndDispatch()) {
//                display.sleep();
//            }
//        }
        COLOR_SHELL_BACKGROUND.dispose();
        COLOR_CLBLWEEKDAY_BACKGROUND.dispose();
        return selectedDate;
    } 
    
    //ֹͣtimerˢ��
    public void stopTimer()
    {
    	timer.stop();
    	System.out.println("Memorandum stop timer");
    }
    /** 
     * Ϊ���е� �� ��ǩ����������� 
     * @param reflushCboYearItems �Ƿ�ˢ�����������е�ѡ�� 
     */ 
    public void displayClblDays(boolean reflushCboYearItems) {
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE); // ��
        int currentMonth=calendar.get(Calendar.MONTH)+1;//��
        int currentYear=calendar.get(Calendar.YEAR);
        if(currentMonth==13)currentMonth=1;
        
        int[] yearAndMonth = getYearAndMonth();
        int year = yearAndMonth[0]; //��
        int month = yearAndMonth[1]; //��

        calendar.set(year,month-1,1); //Calendar�е�monthΪ0-11�����ڴ˴�monthҪ��ȥ1
        int beginIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; //�õ�ָ���·ݵĵ�һ��Ϊ���ڼ���Calendar���󷵻ص�1-7��Ӧ������-���������ʼ�ȥ1
        int endIndex = beginIndex + getDayCountOfMonth(year, month) - 1;
//        System.out.println("ָ���·ݵĵ�һ���ǣ�"+beginIndex);

        //���ļ���ȡstoreDayNotes����
        String datePassToFileOperation = year + "-" + month + "";

        System.out.println("��"+year+" �� ="+month);
        System.out.println("�ļ�����="+datePassToFileOperation);
        
        if(reflushCboYearItems)
            setCboYearItems(year);

        FileOperation readMonth = new FileOperation();
        
        try {
        	//��ȡ������·ݱ���¼����
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
            		clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "΢���ź�",9, SWT.NORMAL));
            	else
            		if(lineNum==0)
            		    clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "΢���ź�",12, SWT.NORMAL));
            		else
            		    clbl_days[i].setFont(new org.eclipse.swt.graphics.Font(display, "΢���ź�",10, SWT.NORMAL));
              
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
    
    public int lineNumOfBox(String boxContent)//��ȡÿ���������м���
    {
    	if(boxContent.equals(""))return 0;
    	String tem[]=boxContent.split("\n");
    	return tem.length;
    }
    
    public String SubstringLinesOfBox(String BoxContent,int num)//����ǰnum�е�boxConstent
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
        	AddText addtext;//�����ı���
        	selectedDate = ((CLabel) e.widget).getToolTipText();
        	try{
        		Display display = Display.getDefault();
        		Shell shellText = new Shell(display,SWT.SHELL_TRIM);
        		
        		addtext = new AddText(shellText, selectedDate, storeDayNotes);
        		addtext.setMemoWidget(ReturnThis());

        		//  git clone  git@10.22.25.117:/home/git/calendar.git	
                shellText.open();
                shellText.layout();
//                System.out.println("@Memorandum.java!!������");
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
            	System.out.println("�����γ�");
            	
          	    Main.State=Tag.CLASSTABLE;
          	    return;
            }
            
            if(TOOPTIPTEXT_ABOUT .equals(tooptip)){
            	MessageBox dialog = new MessageBox(shell);
				dialog.setText("About");
				String aboutInfo = "\t���������α� 1.0 \t\n\t������Ա��\n\t�̿�ɭ������\n\t�˸���,������\n\t���񽿣�������";
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
                //ֻ����������
                if((event.keyCode<'0' || event.keyCode >'9') && !(event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN))
                    event.doit = false;
            }
        }
    };
}

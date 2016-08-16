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
	
	/**��ȡ��Ļ�߶ȿ�� */ 
    int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
    int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
    /** */
    private MemorandumWidget memoWidget = null;
    /** ��ʾ���� */
    private Display display = null;
    
    /** ���ڶ��� */
    private Shell shell = null;
    
    private String selectedDateYearAndMonth = new String();
    
    /** �����ı��� **/
    private Text recordText;
    
    private String[] storeDayNotes;
    
    private int day;
    
    public  AddText(Shell parent, String selectedDate, String[] storeDayNotes){
    	day = DateStringProcess(selectedDate);
    	
    	this.storeDayNotes=storeDayNotes;
    	
    	/**  ��ȡdisplay */
        display = parent.getDisplay();
        
        /** ��ȡshell */
        shell=parent;
        
        /**  ���ô��ڴ�С*/
        shell.setSize(500, 300);
        
        /** ��ȡ���ڿ�Ⱥ͸߶� */
        int shellH = shell.getBounds().height;
        int shellW = shell.getBounds().width;
        
        /**��λ���󴰿����� */
        shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
        
        /** ���ò��� */
        GridLayout gridLayout = new GridLayout();
        shell.setLayout(gridLayout);
        
        /** �����ı���*/
        recordText=new Text(shell,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL); 
        recordText.setText(storeDayNotes[day]); 
        GridData gridData = new GridData();
        
        /** ������ */
        gridData.verticalSpan = 2; 
        /** ������ */
        gridData.horizontalSpan=2; 
        /**��ֱ������� */
        gridData.verticalAlignment = GridData.FILL;
        /** ��ռ��ֱ�������ռ�*/
        gridData.grabExcessVerticalSpace = true; 
        /** ˮƽ�������*/
        gridData.horizontalAlignment = GridData.FILL;
        /** ��ռˮƽ�������ռ� */
        gridData.grabExcessHorizontalSpace = true;
        
        /** gridData�����ı���*/ 
        recordText.setLayoutData(gridData); 
        
        Button saveButton = new Button(shell,SWT.PUSH);
        saveButton.setText("����");
       
//        ���´���һֱ���д������Ը�д��������,��ʹ��ȫ�ֱ������в���
//        addSelectionListener(new SelectionAdapter() {  
//        	@Override  
//        	public void widgetSelected(SelectionEvent e) {  
//        		 System.out.println("�洢���ı���:"+showText );
//        		 storeDayNotes[day] = recordText.getText();
//     	        System.out.println("storeDayNotes["+day+"]="+storeDayNotes[day]);
//     	        shell.setVisible(false);
//     	        //��storeDayNotesд��ָ���ļ�
//     	        FileOperation writeIn = new FileOperation();
//     	        writeIn.writeIntoFile(storeDayNotes, selectedDateYearAndMonth, day);
//        	}
//        }
//        );
        saveButton.addMouseListener(new MouseAdapter() {
        	public void mouseUp(MouseEvent e) {
                // ������ӵ����ť����ʵ�ֵĹ��ܴ���
        		storeNotes();
            }
        }
        ); 
    }
    /** ����õ�selectedDateYearAndMonth */
    public int DateStringProcess(String selectedDate)
    {
        System.out.println("the selectedDate ="+selectedDate);
	    //��ȡѡ�����ڵ���ݺ��·�
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
    	System.out.println("�洢���ı���:"+recordText.getText() );
    	 storeDayNotes[day] = recordText.getText();
    	 FileOperation writeIn = new FileOperation();
    	 writeIn.writeIntoFile(storeDayNotes, selectedDateYearAndMonth, day);
    	 shell.close();
    	//ˢ����ʾ������
    	 memoWidget.displayClblDays(true);
    	 return;
    }
}

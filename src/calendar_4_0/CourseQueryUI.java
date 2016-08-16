package calendar_4_0;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
/*
 * �α�༭���������
 */

/*
 * bug2:�γ̵ڶ��β�ѯ������ˢ��	ok
 * bug3����ѯ�α���ٶ�������Ҫ�Ż�	ok
 * bug4������γ���ͳ��
 */
public class CourseQueryUI {
	Shell shell;				//�α��봰��
	Combo comboYear;			//ѧ�������б�
	Combo comboTerm;			//ѧ�������б�
	Table table;				//�γ̱��
	String studentId;			//ѧ��ѧ��
	String searchkey;			//ѧ��+ѧ��
	String classFileName = "classSaveFile";	//ѡ���Ƿ���α��Ҫ�����Ŀα���
	Display display;
	int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
	public CourseQueryUI(String studentId,Display display)
	{
		this.studentId = studentId;
		this.display = display;
		//shell��������
		shell = new Shell(display,SWT.CLOSE | SWT.MIN);
		shell.setText("�α���");
		//���õ�¼�����ڵ�λ��
        String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
         //windows����
        	shell.setLocation(screenW/4 , 200);
        }  
        else
        {
        //��������
        	shell.setLocation(screenW , 0);
        }
		shell.setBackground(new Color(null, 255, 255, 255));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		//����shellΪ�в��֣�ˮƽ����������岿����Ϊ�������֣�
		RowLayout mainLayout =new RowLayout(SWT.VERTICAL);
		mainLayout.marginWidth = 0;			//��������������shell�����ұ߾�Ϊ0
		mainLayout.marginHeight = 5;		//��������������shell�����±߾�Ϊ5
		mainLayout.spacing = 5;				//�����������֮��ľ���Ϊ5
		shell.setLayout(mainLayout);
		
		//***************************************************************************		
		//�������
		Composite OperationComposite = new Composite(shell,SWT.NONE);
//		OperationComposite.setBackground(new Color(display, 255, 0, 0));
		//���ò�������С
		RowData OpCompositeRowData = new RowData(900,35);							//
		OperationComposite.setLayoutData(OpCompositeRowData);
		
		//���ò�������Ϊ������
		FormLayout opComLayout = new FormLayout();
		OperationComposite.setLayout(opComLayout);
		
		//�����������������
		comboYear = new Combo(OperationComposite,SWT.None);
		String[] itemsYear = {"2013-2014ѧ��","2014-2015ѧ��","2015-2016ѧ��"};
		comboYear.setItems(itemsYear);
		comboYear.select(2);
		FormData yearFrom = new FormData();
		yearFrom.left = new FormAttachment(OperationComposite,5,SWT.RIGHT);	
		yearFrom.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		comboYear.setLayoutData(yearFrom);
		
		//ѧ����������������
		comboTerm = new Combo(OperationComposite,SWT.None);
		String[] itemsTerm ={"����ѧ��","�ļ�ѧ��","�＾ѧ��"};
		comboTerm.setItems(itemsTerm);
		comboTerm.select(2);
		FormData TermFrom = new FormData();
		TermFrom.left = new FormAttachment(comboYear,20,SWT.RIGHT);
		TermFrom.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		comboTerm.setLayoutData(TermFrom);
		
		//��ѯ�α�ť
		Button classCheckBt = new Button(OperationComposite,SWT.PUSH | SWT.CENTER);
		classCheckBt.setText("��ѯ�α�");	
		FormData checkBtForm = new FormData();
        
        if(os.toLowerCase().startsWith("win")){  
         //windows����
    		checkBtForm.left = new FormAttachment(comboTerm,550,SWT.RIGHT);		
        }  
        else
        {
        //��������
        	checkBtForm.left = new FormAttachment(comboTerm,460,SWT.RIGHT);		//linux��������д���
        }
        
		checkBtForm.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		classCheckBt.setLayoutData(checkBtForm);
		classCheckBt.addSelectionListener(new courseSearchListener());
		
		//����α�ť
		Button loadClassBt = new Button(OperationComposite,SWT.PUSH | SWT.CENTER);
		loadClassBt.setText("����α�");	
		FormData loadBtForm = new FormData();
		loadBtForm.left = new FormAttachment(classCheckBt,10,SWT.RIGHT);
		loadBtForm.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		loadClassBt.setLayoutData(loadBtForm);
		loadClassBt.addSelectionListener(new LoadCourseListener());
		
		//***************************************************************************	
		//�γ̱�ͷ���
		Composite tableTitleComposite = new Composite(shell,SWT.NONE);
//		tableTitleComposite.setBackground(new Color(display, 0, 255, 0));
		//���ÿα�����С
		RowData tableCompositeRowData = new RowData(900,330);							//
		tableTitleComposite.setLayoutData(tableCompositeRowData);
		
		//���ÿγ̱�ͷ��岼��
		GridLayout tableCompositeLayout = new GridLayout();
		tableCompositeLayout.numColumns = 1;
		tableCompositeLayout.marginWidth = 5;
		tableCompositeLayout.marginHeight = 5;
		tableTitleComposite.setLayout(tableCompositeLayout);

		//��ͷ��Ϣ
		String[] tableTitle = {"���","�γ�","��ʦ","�ص�","��ֹ��","��һ","�ܶ�","����","����","����","����","����"};
		//����������
		table = new Table(tableTitleComposite,SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);	
		//���ñ�񲼾�
		
		GridData tableGridData = new GridData();		
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.verticalAlignment = GridData.FILL;
		tableGridData.grabExcessVerticalSpace = true;
		table.setLayoutData(tableGridData);
		
		//������ͷ
		TableColumn numCol = new TableColumn(table,SWT.LEFT);	
		TableColumn classCol = new TableColumn(table,SWT.LEFT);
		TableColumn teacherCol = new TableColumn(table,SWT.LEFT);
		TableColumn placeCol = new TableColumn(table,SWT.LEFT);	
		TableColumn weekendCol = new TableColumn(table,SWT.LEFT);
		TableColumn monCol = new TableColumn(table,SWT.LEFT);
		TableColumn tuesCol = new TableColumn(table,SWT.LEFT);	
		TableColumn wedCol = new TableColumn(table,SWT.LEFT);
		TableColumn thurCol = new TableColumn(table,SWT.LEFT);
		TableColumn friCol = new TableColumn(table,SWT.LEFT);	
		TableColumn staCol = new TableColumn(table,SWT.LEFT);
		TableColumn sunCol = new TableColumn(table,SWT.LEFT);
		//���ñ�ͷ����
		numCol.setText(tableTitle[0]);
		classCol.setText(tableTitle[1]);
		teacherCol.setText(tableTitle[2]);
		placeCol.setText(tableTitle[3]);
		weekendCol.setText(tableTitle[4]);
		monCol.setText(tableTitle[5]);
		tuesCol.setText(tableTitle[6]);
		wedCol.setText(tableTitle[7]);
		thurCol.setText(tableTitle[8]);
		friCol.setText(tableTitle[9]);
		staCol.setText(tableTitle[10]);
		sunCol.setText(tableTitle[11]);
		//���ø��еĿ��
		numCol.setWidth(60);
		classCol.setWidth(195);				//�γ���
		teacherCol.setWidth(90);			//��ʦ
		placeCol.setWidth(60);				//�ص�
		weekendCol.setWidth(60);	
		monCol.setWidth(60);
		tuesCol.setWidth(60);
		wedCol.setWidth(60);
		thurCol.setWidth(60);
		friCol.setWidth(60);
		staCol.setWidth(60);
		sunCol.setWidth(60);
		//���ñ��Ԫ�Ŀ���Ƿ�ɱ�
		numCol.setResizable(false);			
		classCol.setResizable(false);
		teacherCol.setResizable(false);
		placeCol.setResizable(false);
		weekendCol.setResizable(false);
		monCol.setResizable(false);
		tuesCol.setResizable(false);
		wedCol.setResizable(false);
		thurCol.setResizable(false);
		friCol.setResizable(false);
		staCol.setResizable(false);
		sunCol.setResizable(false);
		
	 	table.setHeaderVisible(true); 		//���ñ�ͷ�ɼ�
		table.setLinesVisible(true); 		//���ñ���߿ɼ�
		
		shell.pack();
		shell.open();
		shell.addDisposeListener(new ShellDisposelistenner());
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	//shell���ڹرռ�����
	class ShellDisposelistenner implements DisposeListener
	{
		@Override
		//��֤�ڻ�û����α�ֱ�ӹرմ��ڵ�ʱ���������ʱ�α��ļ���
		public void widgetDisposed(DisposeEvent arg0) {
			File tempClassFile = new File("./classInfoFile.txt");
			if(tempClassFile.delete())
			{
				System.out.println("��ɾ��classInfoFile.txt�ļ���");
			}
		}
	}
	
	//����α�ť���¼�����
	class LoadCourseListener implements SelectionListener
	{
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {}
		@Override
		public void widgetSelected(SelectionEvent event) 
		{
			//���ı���α���Ϣ���ļ������������ֲ�ѯ�α�ʱ����ʱ���ɵ�classInfoFile.txt�ļ�
			File classInfoFile = new File("./classInfoFile.txt");
			File classSaveFile = new File("./classSaveFile.txt");
			//����δ����ѯ�α��Ϳյ��롣classInfoFile.txt�ļ����ڣ�˵��֮ǰ�Ѿ���ѯ�����пα���Ϣ���ɣ����ܵ��롣
			//������ѯ�α����е�������
			if(classInfoFile.exists())
			{
				//���֮ǰû�е�����α���classSaveFile.txt�ļ�������
				if(!classSaveFile.exists())
				{
					//��ʾ����α�ɹ��ĶԻ���
					MessageBox savaSuccessInfo = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
					savaSuccessInfo.setText("�α���");
					savaSuccessInfo.setMessage("����α�ɹ���");
					savaSuccessInfo.open();	
					classInfoFile.renameTo(classSaveFile);
				}
				//���֮ǰ�Ѿ������һ�ˣ��ٴε���α�ʱ��ѯ���Ƿ�Ҫ���ǡ�		
				else
				{
					 //������Ϣ�����ʹ�þ���ͼ�겢��ʾ�Ǻͷ�ť
				     MessageBox recoverClassFile = new MessageBox(shell ,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
				     //���öԻ���ı���
				     recoverClassFile.setText("�����ļ�");
				     //���öԻ�����ʾ����Ϣ
				     recoverClassFile.setMessage("�α��Ѿ����ڣ��Ƿ񸲸ǣ�");
				     //�򿪶Ի��򣬽�����ֵ����choice
				     int choice = recoverClassFile.open();
				     //��ӡ����ѡ���ֵ
				     if (choice==SWT.YES){
				    	System.out.print("Yes");
						classSaveFile.delete();
						classInfoFile.renameTo(classSaveFile);
				     }
				     else if ( choice==SWT.NO)
				      System.out.print("No");
				}
			}
			//û�о�����ѯ�α���ֱ�ӿյ���������
			else if(!classInfoFile.exists())
			{
				MessageBox noClassWaring = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
				noClassWaring.setText("Waring");
				noClassWaring.setMessage("����ʧ�ܣ����Ȳ�ѯҪ����Ŀα�");
				noClassWaring.open();	
			}	
		}		
	}
	int BtPushCount = 0;//��ѯ��ť����������
	//��ѯ�α�ť���¼�����
	class courseSearchListener implements SelectionListener
	{
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {}
		@Override
		public void widgetSelected(SelectionEvent event) {
			// TODO Auto-generated method stub
			//��ȡ�����б��ѧ����Ϣ
			String key1 = "" + comboYear.getSelectionIndex();
			System.out.println("key:"+key1);
			String studyYear =comboYear.getItem(comboYear.getSelectionIndex());
			System.out.println("value:"+studyYear);
			//��ȡ�����б��ѧ����Ϣ
			String key2 = "" + comboTerm.getSelectionIndex();
//			System.out.println("key:"+key2);
			String studyTerm =comboTerm.getItem(comboTerm.getSelectionIndex());
//			System.out.println("value:"+studyTerm);
			searchkey = studyYear + studyTerm;
			System.out.println("searchkey:"+searchkey);
			System.out.print("***************************\n\n");
			BtPushCount ++;												//��¼��ѯ��ť���µĴ���
			table.removeAll();											//�����һ�β�ѯ������
			createCoureTable(studentId,searchkey);						//���´����µĿγ̱�����
		}				
	}
	
	//ͨ����ѯ����һ���γ̱���ʾ��UI�ϣ������浽classSaveFile.txt��
	public void createCoureTable(String id,String key)
	{
		File classSaveFile = new File("./classInfoFile.txt");	//��ſα���ļ�
		FileWriter classInfoWriter = null;						//����д��һ�ſγ���Ϣ���ļ��е�Writer	
		String htmlCode = "";									//���һ���γ��ļ������ݣ����л���
		int classSum = 0;										//�γ�����������
		String classDataPath = "./sortOutData/";					//��ſγ���Ϣ����Ŀ¼
//		String[] dataFileName = {"2013-2014","2014-2015","2015-2016",};
		//�����Ŀ¼��
		 String[] dataFileName ={"autunm34", "autunm45","autunm56","spring34","spring45","spring56","summer"};
		 String[] searchKey ={"2013-2014ѧ���＾ѧ��" ,"2014-2015ѧ���＾ѧ��","2015-2016ѧ���＾ѧ��","2013-2014ѧ�괺��ѧ��",
				 "2014-2015ѧ�괺��ѧ��","2015-2016ѧ�괺��ѧ��","�ļ�ѧ��"};	 
		
		//���ɿγ��ļ�����·��
		for(int i=0;i<dataFileName.length;i++){
			if(key.indexOf(searchKey[i]) != -1){
				classDataPath += dataFileName[i];
			}
		}
		
//		System.out.println(classDataPath);
		File dir = new File(classDataPath);						//��ȡ�γ��ļ���Ŀ¼				
		File[] allClassFiles = dir.listFiles();					//Ŀ¼�µ������ļ�����
			
		
		//��������ƥ��γ̵��ļ��У�����γ���Ϣ
		if(!classSaveFile.exists()){
			try {
				classSaveFile.createNewFile();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//��ƥ��γ̵��ļ��У�����γ���Ϣ
		try 
		{
			
			classInfoWriter = new FileWriter(classSaveFile);		//��classSaveFile.txt
			//����./CourseData�µĿγ��ļ�
			for(File file : allClassFiles)
			{	
		        try 
		        {   //��һ���ļ����пγ�ƥ��
		        	BufferedReader reader = new BufferedReader(new FileReader(file));  
		            String tempString = null;  	        
		            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����  
		            while ((tempString = reader.readLine()) != null) {  
		            	htmlCode += tempString;						//��ȡ�����ļ������ݣ����浽htmlCode��
		            }
		            reader.close();                          		//�رն��ļ�
		            //****************************************************************************
		            //��ѧ��(id)�������(key)ѧ��ƥ�� һ�ſγ�
		            if(htmlCode.indexOf(id) != -1 && htmlCode.indexOf(key) != -1 )
		            {
		            	classSum++;													//�γ�����1
		            	ArrayList<String> oneClassInfo = outputCourse(htmlCode);	//���һ�ſγ̵���Ϣ
		            	for(String temp : oneClassInfo)			//��classSaveFile.txt�ļ�д��һ�ſγ̵���Ϣ
		            	{
//		            		System.out.println(temp);
		            		//��ĳ��ѧ��ѧ���һ�ſγ̱��浽�ļ�����
		            		classInfoWriter.write(temp+",");	
		            	}  
		            	classInfoWriter.write("\r\n");			 //����д��һ�ſγ���Ϣ          	
		            }
		            
		          //****************************************************************************
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }
		        htmlCode ="";									//���htmlCode���ݣ���ʼ��һ�ſε�ƥ��ѭ��  
			}	
			classInfoWriter.close();							//����д��һ���γ��ļ����ر�classSaveFile.txt
			System.out.println("�γ�����Ϊ��" + classSum);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//��ȡ�α��ļ�����ʾ��UI��
		try {
			showTableContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//��ȡclassSaveFile.txt�е�������ʾ��UI��
	public void showTableContent() throws IOException
	{
		String[] oneClassInfo;
		String classTime; 
		String tempString;
		File classInfoFile = new File("./classInfoFile.txt");
		try {
			FileReader saveFileReader = new FileReader(classInfoFile);			//�򿪲�ѯ����ʱ�α��ļ�
			BufferedReader saveFileBuffer = new BufferedReader(saveFileReader);
			while (( tempString = saveFileBuffer.readLine()) != null)			//һ��һ�еĶ�ȡһ�ſγ�
			{
				oneClassInfo = tempString.split(",");							//�ָ�һ�ſε���Ϣ
				//���һ�ſγ̵ı��У���ʼ��Ϊ�ո��ַ���
				String[] classItem ={"  ", "  ", "  ", "  ", "  ","  ", "  ", "  ", "  ", "  ", "  ", "  ", };
				//���ָ��ǰ5����γ���Ϣ����뵽���е�ǰ����
				for(int i=0;i<5;i++){	
					classItem[i] = oneClassInfo[i];
				}		
				//����һ�ſγ̵�ʱ��ֲ��ַ����飬��ʾ����һ�������񲿷���
				String[] timetable = {"  ", "  ", "  ", "  ", "  ", "  ", "  ", };
				//ѭ����һ���ж�ڿεĿγ̵�����
				for(int i=5;i<oneClassInfo.length;i+=2)				//i��ʾһ�ſγ���ʱ���λ�ã�i+1��ʾ�ܼ���λ��
				{
					if(oneClassInfo[i+1].equals("��һ"))
					{
						timetable[0] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("�ܶ�"))
					{
						timetable[1] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("����"))
					{
						timetable[2] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("����"))
					{
						timetable[3] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("����"))
					{
						timetable[4] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("����"))
					{
						timetable[5] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("����"))
					{
						timetable[6] = oneClassInfo[i];
					}
				}
				//�����ɵĿγ�ʱ���У����뵽һ�ſγ̵ı�����
				for(int i=0;i<timetable.length;i++){
					classItem[i+5] = timetable[i];
				}
				//����̨����α�
				for(String temp:classItem){
					System.out.print(temp+" ");
				}
				System.out.println();
					
				TableItem item = new TableItem(table,SWT.None);
				item.setText(classItem);							//���ÿγ�ʱ�������
			}
			saveFileReader.close(); 		//�رն��ļ�
			System.out.println("test OK\n");			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// ��ȡ�γ���Ϣ�ķ���
	private String filter(String longInfo, String front, String end, int index) {
		String[] tmp = longInfo.split(front);
		String[] tmp2 = tmp[index].split(end);
		return tmp2[0];
	}
		
	// ���һ�ſγ̵���Ϣ������
	private ArrayList<String> outputCourse(String res) {
		ArrayList<String> classInfo  = new ArrayList<String>();
		String file_info = res;
		
		String classNum = filter(file_info,"<span id=\"ctl00_cpContent_lbl_ClassID\">", "</span>",1);
		classInfo.add(classNum);
		
		String className = filter(file_info,"<span id=\"ctl00_cpContent_lbl_CourseName\">","</span>", 1);
		className = className.replaceAll("\\[[A-Z0-9]+\\]", "");		//ȥ���γ�ǰ��Ŀγ̱��  \\[ --> [
//		System.out.println(className);
		classInfo.add(className);
	
		String classTeacher = filter(file_info, "target=\"_blank\">", "   ", 1);
		classInfo.add(classTeacher);

		String classPlace = filter(file_info, "target=\"_blank\">", "   ", 2);
		classInfo.add(classPlace);
		
		//����1-16�ܣ�����(89��)���ָ����������Ͽ�ʱ��
		String classWeekend,classDay,classTime;
		String[] tempString;
		classTime = filter(file_info, "<span id=\"ctl00_cpContent_lbl_Time\">","</span>", 1);
		tempString = classTime.split("��");
		classWeekend = tempString[0];			 //classWeekend = 1-16��
		classInfo.add(classWeekend);			 //����γ�����
		//���ڽ���һ���ж�οεĿγ�	
		for(int i=1;i<tempString.length;i++)
		{
			classTime = tempString[i];										//classTime = ����(89��),��X(xx)
			int charBegin = classTime.indexOf("(");
			int charEnd = classTime.indexOf(")");
			classDay = classTime.substring(0, charBegin);					//������(89��)������ "����"
			classTime = classTime.substring(charBegin+1,charEnd-1);			//������(89��)��ȡ�� "89"
			classInfo.add(classTime);										//����γ�ʱ��
			classInfo.add(classDay);										//����γ�����
		}
		
		//ѧ��ѧ�ڲ������
		String classTerm = filter(file_info,"<span id=\"ctl00_cpContent_lbl_Semester\">","</span>", 1);
		return classInfo;
	}

	public static void main(String args[])
	{
		new CourseQueryUI("2013101013",Display.getDefault());
	}
}

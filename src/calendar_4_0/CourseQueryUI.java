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
 * 课表编辑，导入界面
 */

/*
 * bug2:课程第二次查询，不能刷新	ok
 * bug3：查询课表的速度慢，需要优化	ok
 * bug4：加入课程数统计
 */
public class CourseQueryUI {
	Shell shell;				//课表导入窗口
	Combo comboYear;			//学年下拉列表
	Combo comboTerm;			//学期下拉列表
	Table table;				//课程表格
	String studentId;			//学生学号
	String searchkey;			//学年+学期
	String classFileName = "classSaveFile";	//选择是否导入课表后，要更换的课表名
	Display display;
	int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
	public CourseQueryUI(String studentId,Display display)
	{
		this.studentId = studentId;
		this.display = display;
		//shell窗口设置
		shell = new Shell(display,SWT.CLOSE | SWT.MIN);
		shell.setText("课表导入");
		//设置登录主窗口的位置
        String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
         //windows环境
        	shell.setLocation(screenW/4 , 200);
        }  
        else
        {
        //其他环境
        	shell.setLocation(screenW , 0);
        }
		shell.setBackground(new Color(null, 255, 255, 255));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		//设置shell为行布局（水平，有两个面板部件分为两个部分）
		RowLayout mainLayout =new RowLayout(SWT.VERTICAL);
		mainLayout.marginWidth = 0;			//设置两个面板距离shell的左右边距为0
		mainLayout.marginHeight = 5;		//设置两个面板距离shell的上下边距为5
		mainLayout.spacing = 5;				//设置两个面板之间的距离为5
		shell.setLayout(mainLayout);
		
		//***************************************************************************		
		//操作面板
		Composite OperationComposite = new Composite(shell,SWT.NONE);
//		OperationComposite.setBackground(new Color(display, 255, 0, 0));
		//设置操作面板大小
		RowData OpCompositeRowData = new RowData(900,35);							//
		OperationComposite.setLayoutData(OpCompositeRowData);
		
		//设置操作面板的为表单布局
		FormLayout opComLayout = new FormLayout();
		OperationComposite.setLayout(opComLayout);
		
		//年份区间下拉框设置
		comboYear = new Combo(OperationComposite,SWT.None);
		String[] itemsYear = {"2013-2014学年","2014-2015学年","2015-2016学年"};
		comboYear.setItems(itemsYear);
		comboYear.select(2);
		FormData yearFrom = new FormData();
		yearFrom.left = new FormAttachment(OperationComposite,5,SWT.RIGHT);	
		yearFrom.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		comboYear.setLayoutData(yearFrom);
		
		//学期区间下拉框设置
		comboTerm = new Combo(OperationComposite,SWT.None);
		String[] itemsTerm ={"春季学期","夏季学期","秋季学期"};
		comboTerm.setItems(itemsTerm);
		comboTerm.select(2);
		FormData TermFrom = new FormData();
		TermFrom.left = new FormAttachment(comboYear,20,SWT.RIGHT);
		TermFrom.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		comboTerm.setLayoutData(TermFrom);
		
		//查询课表按钮
		Button classCheckBt = new Button(OperationComposite,SWT.PUSH | SWT.CENTER);
		classCheckBt.setText("查询课表");	
		FormData checkBtForm = new FormData();
        
        if(os.toLowerCase().startsWith("win")){  
         //windows环境
    		checkBtForm.left = new FormAttachment(comboTerm,550,SWT.RIGHT);		
        }  
        else
        {
        //其他环境
        	checkBtForm.left = new FormAttachment(comboTerm,460,SWT.RIGHT);		//linux下面界面有错误
        }
        
		checkBtForm.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		classCheckBt.setLayoutData(checkBtForm);
		classCheckBt.addSelectionListener(new courseSearchListener());
		
		//导入课表按钮
		Button loadClassBt = new Button(OperationComposite,SWT.PUSH | SWT.CENTER);
		loadClassBt.setText("导入课表");	
		FormData loadBtForm = new FormData();
		loadBtForm.left = new FormAttachment(classCheckBt,10,SWT.RIGHT);
		loadBtForm.top = new FormAttachment(OperationComposite,5,SWT.BOTTOM);
		loadClassBt.setLayoutData(loadBtForm);
		loadClassBt.addSelectionListener(new LoadCourseListener());
		
		//***************************************************************************	
		//课程表头面板
		Composite tableTitleComposite = new Composite(shell,SWT.NONE);
//		tableTitleComposite.setBackground(new Color(display, 0, 255, 0));
		//设置课表面板大小
		RowData tableCompositeRowData = new RowData(900,330);							//
		tableTitleComposite.setLayoutData(tableCompositeRowData);
		
		//设置课程表头面板布局
		GridLayout tableCompositeLayout = new GridLayout();
		tableCompositeLayout.numColumns = 1;
		tableCompositeLayout.marginWidth = 5;
		tableCompositeLayout.marginHeight = 5;
		tableTitleComposite.setLayout(tableCompositeLayout);

		//表头信息
		String[] tableTitle = {"班号","课程","教师","地点","起止周","周一","周二","周三","周四","周五","周六","周日"};
		//创建表格组件
		table = new Table(tableTitleComposite,SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);	
		//设置表格布局
		
		GridData tableGridData = new GridData();		
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.verticalAlignment = GridData.FILL;
		tableGridData.grabExcessVerticalSpace = true;
		table.setLayoutData(tableGridData);
		
		//创建表头
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
		//设置表头文字
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
		//设置各列的宽度
		numCol.setWidth(60);
		classCol.setWidth(195);				//课程名
		teacherCol.setWidth(90);			//教师
		placeCol.setWidth(60);				//地点
		weekendCol.setWidth(60);	
		monCol.setWidth(60);
		tuesCol.setWidth(60);
		wedCol.setWidth(60);
		thurCol.setWidth(60);
		friCol.setWidth(60);
		staCol.setWidth(60);
		sunCol.setWidth(60);
		//设置表格单元的宽度是否可变
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
		
	 	table.setHeaderVisible(true); 		//设置表头可见
		table.setLinesVisible(true); 		//设置表格线可见
		
		shell.pack();
		shell.open();
		shell.addDisposeListener(new ShellDisposelistenner());
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	//shell窗口关闭监听器
	class ShellDisposelistenner implements DisposeListener
	{
		@Override
		//保证在还没导入课表，直接关闭窗口的时候，清除掉临时课表文件。
		public void widgetDisposed(DisposeEvent arg0) {
			File tempClassFile = new File("./classInfoFile.txt");
			if(tempClassFile.delete())
			{
				System.out.println("已删除classInfoFile.txt文件！");
			}
		}
	}
	
	//导入课表按钮的事件处理
	class LoadCourseListener implements SelectionListener
	{
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {}
		@Override
		public void widgetSelected(SelectionEvent event) 
		{
			//更改保存课表信息的文件名，用于区分查询课表时，临时生成的classInfoFile.txt文件
			File classInfoFile = new File("./classInfoFile.txt");
			File classSaveFile = new File("./classSaveFile.txt");
			//避免未经查询课表，就空导入。classInfoFile.txt文件存在，说明之前已经查询过，有课表信息生成，才能导入。
			//经过查询课表，进行导入的情况
			if(classInfoFile.exists())
			{
				//如果之前没有导入过课表，则classSaveFile.txt文件不存在
				if(!classSaveFile.exists())
				{
					//显示导入课表成功的对话框
					MessageBox savaSuccessInfo = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
					savaSuccessInfo.setText("课表导入");
					savaSuccessInfo.setMessage("保存课表成功！");
					savaSuccessInfo.open();	
					classInfoFile.renameTo(classSaveFile);
				}
				//如果之前已经导入过一此，再次导入课表时，询问是否要覆盖。		
				else
				{
					 //创建消息框对象，使用警告图标并显示是和否按钮
				     MessageBox recoverClassFile = new MessageBox(shell ,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
				     //设置对话框的标题
				     recoverClassFile.setText("覆盖文件");
				     //设置对话框显示的消息
				     recoverClassFile.setMessage("课表已经存在，是否覆盖？");
				     //打开对话框，将返回值赋给choice
				     int choice = recoverClassFile.open();
				     //打印出所选择的值
				     if (choice==SWT.YES){
				    	System.out.print("Yes");
						classSaveFile.delete();
						classInfoFile.renameTo(classSaveFile);
				     }
				     else if ( choice==SWT.NO)
				      System.out.print("No");
				}
			}
			//没有经过查询课表，就直接空导入的情况。
			else if(!classInfoFile.exists())
			{
				MessageBox noClassWaring = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
				noClassWaring.setText("Waring");
				noClassWaring.setMessage("导入失败，请先查询要导入的课表！");
				noClassWaring.open();	
			}	
		}		
	}
	int BtPushCount = 0;//查询按钮次数计数器
	//查询课表按钮的事件处理
	class courseSearchListener implements SelectionListener
	{
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {}
		@Override
		public void widgetSelected(SelectionEvent event) {
			// TODO Auto-generated method stub
			//获取下拉列表的学年信息
			String key1 = "" + comboYear.getSelectionIndex();
			System.out.println("key:"+key1);
			String studyYear =comboYear.getItem(comboYear.getSelectionIndex());
			System.out.println("value:"+studyYear);
			//获取下拉列表的学期信息
			String key2 = "" + comboTerm.getSelectionIndex();
//			System.out.println("key:"+key2);
			String studyTerm =comboTerm.getItem(comboTerm.getSelectionIndex());
//			System.out.println("value:"+studyTerm);
			searchkey = studyYear + studyTerm;
			System.out.println("searchkey:"+searchkey);
			System.out.print("***************************\n\n");
			BtPushCount ++;												//记录查询按钮按下的次数
			table.removeAll();											//清空上一次查询的内容
			createCoureTable(studentId,searchkey);						//重新创建新的课程表内容
		}				
	}
	
	//通过查询生成一个课程表，显示在UI上，并保存到classSaveFile.txt中
	public void createCoureTable(String id,String key)
	{
		File classSaveFile = new File("./classInfoFile.txt");	//存放课表的文件
		FileWriter classInfoWriter = null;						//用于写入一门课程信息到文件中的Writer	
		String htmlCode = "";									//存放一个课程文件的内容，进行缓存
		int classSum = 0;										//课程总数计数器
		String classDataPath = "./sortOutData/";					//存放课程信息的主目录
//		String[] dataFileName = {"2013-2014","2014-2015","2015-2016",};
		//分类的目录名
		 String[] dataFileName ={"autunm34", "autunm45","autunm56","spring34","spring45","spring56","summer"};
		 String[] searchKey ={"2013-2014学年秋季学期" ,"2014-2015学年秋季学期","2015-2016学年秋季学期","2013-2014学年春季学期",
				 "2014-2015学年春季学期","2015-2016学年春季学期","夏季学期"};	 
		
		//生成课程文件查找路径
		for(int i=0;i<dataFileName.length;i++){
			if(key.indexOf(searchKey[i]) != -1){
				classDataPath += dataFileName[i];
			}
		}
		
//		System.out.println(classDataPath);
		File dir = new File(classDataPath);						//读取课程文件的目录				
		File[] allClassFiles = dir.listFiles();					//目录下的所有文件引用
			
		
		//创建保存匹配课程的文件夹，保存课程信息
		if(!classSaveFile.exists()){
			try {
				classSaveFile.createNewFile();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//打开匹配课程的文件夹，保存课程信息
		try 
		{
			
			classInfoWriter = new FileWriter(classSaveFile);		//打开classSaveFile.txt
			//遍历./CourseData下的课程文件
			for(File file : allClassFiles)
			{	
		        try 
		        {   //打开一个文件进行课程匹配
		        	BufferedReader reader = new BufferedReader(new FileReader(file));  
		            String tempString = null;  	        
		            // 一次读入一行，直到读入null为文件结束  
		            while ((tempString = reader.readLine()) != null) {  
		            	htmlCode += tempString;						//获取整个文件的内容，保存到htmlCode中
		            }
		            reader.close();                          		//关闭读文件
		            //****************************************************************************
		            //用学号(id)，和年份(key)学期匹配 一门课程
		            if(htmlCode.indexOf(id) != -1 && htmlCode.indexOf(key) != -1 )
		            {
		            	classSum++;													//课程数加1
		            	ArrayList<String> oneClassInfo = outputCourse(htmlCode);	//获得一门课程的信息
		            	for(String temp : oneClassInfo)			//向classSaveFile.txt文件写入一门课程的信息
		            	{
//		            		System.out.println(temp);
		            		//将某个学期学年的一门课程保存到文件当中
		            		classInfoWriter.write(temp+",");	
		            	}  
		            	classInfoWriter.write("\r\n");			 //换行写下一门课程信息          	
		            }
		            
		          //****************************************************************************
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }
		        htmlCode ="";									//清空htmlCode内容，开始下一门课的匹配循环  
			}	
			classInfoWriter.close();							//结束写入一个课程文件，关闭classSaveFile.txt
			System.out.println("课程总数为：" + classSum);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//读取课表文件，显示在UI上
		try {
			showTableContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//读取classSaveFile.txt中的内容显示到UI上
	public void showTableContent() throws IOException
	{
		String[] oneClassInfo;
		String classTime; 
		String tempString;
		File classInfoFile = new File("./classInfoFile.txt");
		try {
			FileReader saveFileReader = new FileReader(classInfoFile);			//打开查询的临时课表文件
			BufferedReader saveFileBuffer = new BufferedReader(saveFileReader);
			while (( tempString = saveFileBuffer.readLine()) != null)			//一行一行的读取一门课程
			{
				oneClassInfo = tempString.split(",");							//分割一门课的信息
				//存放一门课程的表行，初始化为空格字符串
				String[] classItem ={"  ", "  ", "  ", "  ", "  ","  ", "  ", "  ", "  ", "  ", "  ", "  ", };
				//将分割的前5项，即课程信息项加入到表行的前五列
				for(int i=0;i<5;i++){	
					classItem[i] = oneClassInfo[i];
				}		
				//生成一门课程的时间分布字符数组，显示在周一到周五表格部分下
				String[] timetable = {"  ", "  ", "  ", "  ", "  ", "  ", "  ", };
				//循环把一周有多节课的课程调整好
				for(int i=5;i<oneClassInfo.length;i+=2)				//i表示一门课程中时间的位置，i+1表示周几的位置
				{
					if(oneClassInfo[i+1].equals("周一"))
					{
						timetable[0] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周二"))
					{
						timetable[1] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周三"))
					{
						timetable[2] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周四"))
					{
						timetable[3] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周五"))
					{
						timetable[4] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周六"))
					{
						timetable[5] = oneClassInfo[i];
					}
					else if(oneClassInfo[i+1].equals("周日"))
					{
						timetable[6] = oneClassInfo[i];
					}
				}
				//将生成的课程时间行，加入到一门课程的表行中
				for(int i=0;i<timetable.length;i++){
					classItem[i+5] = timetable[i];
				}
				//控制台输出课表
				for(String temp:classItem){
					System.out.print(temp+" ");
				}
				System.out.println();
					
				TableItem item = new TableItem(table,SWT.None);
				item.setText(classItem);							//设置课程时间表内容
			}
			saveFileReader.close(); 		//关闭读文件
			System.out.println("test OK\n");			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// 截取课程信息的方法
	private String filter(String longInfo, String front, String end, int index) {
		String[] tmp = longInfo.split(front);
		String[] tmp2 = tmp[index].split(end);
		return tmp2[0];
	}
		
	// 获得一门课程的信息并返回
	private ArrayList<String> outputCourse(String res) {
		ArrayList<String> classInfo  = new ArrayList<String>();
		String file_info = res;
		
		String classNum = filter(file_info,"<span id=\"ctl00_cpContent_lbl_ClassID\">", "</span>",1);
		classInfo.add(classNum);
		
		String className = filter(file_info,"<span id=\"ctl00_cpContent_lbl_CourseName\">","</span>", 1);
		className = className.replaceAll("\\[[A-Z0-9]+\\]", "");		//去掉课程前面的课程编号  \\[ --> [
//		System.out.println(className);
		classInfo.add(className);
	
		String classTeacher = filter(file_info, "target=\"_blank\">", "   ", 1);
		classInfo.add(classTeacher);

		String classPlace = filter(file_info, "target=\"_blank\">", "   ", 2);
		classInfo.add(classPlace);
		
		//处理1-16周，周五(89节)。分割周数，和上课时间
		String classWeekend,classDay,classTime;
		String[] tempString;
		classTime = filter(file_info, "<span id=\"ctl00_cpContent_lbl_Time\">","</span>", 1);
		tempString = classTime.split("，");
		classWeekend = tempString[0];			 //classWeekend = 1-16周
		classInfo.add(classWeekend);			 //加入课程周数
		//用于解析一周有多次课的课程	
		for(int i=1;i<tempString.length;i++)
		{
			classTime = tempString[i];										//classTime = 周五(89节),周X(xx)
			int charBegin = classTime.indexOf("(");
			int charEnd = classTime.indexOf(")");
			classDay = classTime.substring(0, charBegin);					//从周五(89节)解析出 "周五"
			classTime = classTime.substring(charBegin+1,charEnd-1);			//从周五(89节)截取出 "89"
			classInfo.add(classTime);										//加入课程时间
			classInfo.add(classDay);										//加入课程星期
		}
		
		//学年学期不用输出
		String classTerm = filter(file_info,"<span id=\"ctl00_cpContent_lbl_Semester\">","</span>", 1);
		return classInfo;
	}

	public static void main(String args[])
	{
		new CourseQueryUI("2013101013",Display.getDefault());
	}
}

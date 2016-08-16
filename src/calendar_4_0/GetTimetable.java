package calendar_4_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetTimetable {
	String filePath ="./classSaveFile.txt";
//	static String timeTable1[] = new String[31];
	
	GetTimetable(String timeTable[]){
		initTimeTable(timeTable);
		readFile(timeTable);
	}
	
	//将timeTable字符串数组所有元素赋值为空""
	void initTimeTable(String timeTable[]){
		for(int i = 0; i < ClassTableWidget.TOTAL_MAX_CLASS_AMOUNT; i++){
			timeTable[i] = "";
		}
	}
	
	void readFile(String timeTable[]){
		BufferedReader reader = null;
		File file=new File(filePath);
		if(!file.exists())
		{
			 System.out.println("课表文件不存在");
			   return;
		}
		
		try 	{
			FileInputStream inputStream=new FileInputStream(filePath);
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream, "GBK");
//			InputStreamReader inputStreamReader=new InputStreamReader(inputStream, "utf-8");
		   int size=inputStream.available();//获取文件大小
		   if(size==0)
		   {
			   System.out.println("课表文件为空");
			   return;
		   }
		   else  System.out.println("课表"+size);
			reader = new BufferedReader(inputStreamReader); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,

			String str = null;
			String temp[] = new String[10];
			while ((str = reader.readLine()) != null) {
				System.out.println(str);
				temp = str.split(",");
				for(int i = 0; i < temp.length; i++){
					System.out.println("temp["+i+"]="+temp[i]);
					if(temp.length == 7){
						int index = confirmTheLocation(temp[6], temp[5]);
						String classInfo;
						classInfo = temp[5] + "\n" + temp[1] + "\n" + temp[3] + "\n" + temp[4];
						timeTable[index] = classInfo;
					}
					if(temp.length == 9){
						int index = confirmTheLocation(temp[6], temp[5]);
						String classInfo;
						classInfo = temp[5] + "\n" + temp[1] + "\n" + temp[3] + "\n" + temp[4];
						timeTable[index] = classInfo;
						index = confirmTheLocation(temp[8], temp[7]);
						classInfo = temp[7] + "\n" + temp[1] + "\n" + temp[3] + "\n" + temp[4];
						timeTable[index] = classInfo;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	int confirmTheLocation(String dayNumber, String classSchedule){
//		System.out.println("dayNumber"+dayNumber);
		int dayNum = getDayNumber(dayNumber);
		int classNum = getClassSchedule(classSchedule);
		if((dayNum == 100) || (classNum == 100)){System.out.println("课表文件出错，文件路径为："+filePath);}
//		System.out.println("classNum="+classNum+"   dayNum="+dayNum+"classNum*7+dayNum="+(classNum*7+dayNum));
		int returnNum = classNum*7+dayNum;
		return returnNum;
	}
	
	int getDayNumber(String dayNumber){
		if(dayNumber.equals("周日")){return 0;}
		if(dayNumber.equals("周一")){return 1;}
		if(dayNumber.equals("周二")){return 2;}
		if(dayNumber.equals("周三")){return 3;}
		if(dayNumber.equals("周四")){return 4;}
		if(dayNumber.equals("周五")){return 5;}
		if(dayNumber.equals("周六")){return 6;}
		return 100;
	}
	
	int getClassSchedule(String classSchedule){
		if(classSchedule.contains("1")){return 0;}
		if(classSchedule.contains("3")){return 1;}
		if(classSchedule.contains("6")){return 2;}
		if(classSchedule.contains("8")){return 3;}
		if(classSchedule.contains("A")){return 4;}
		return 100;
	}
	
}

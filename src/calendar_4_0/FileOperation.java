package calendar_4_0;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class FileOperation {
	
	private String classPath = System.getProperty("user.dir");
	private String filePath = new String();
	
	/*
	 * 读取当前月份的所有备忘录信息并赋值给storeDayNotes数组
	 */
	void readMonthNotes(String[] storeDayNotes, String monthPath) throws IOException{
		//初始化，避免为NULL
		for(int i =0; i < storeDayNotes.length; i++){
			storeDayNotes[i] = "";
		}
		
		String storeDay[] = new String[32];

		//文件当前路径
		filePath = "./"+monthPath+".txt";
		System.out.println("文件路径="+filePath);
		
		File file = new File(filePath);
     
        //不存在则创建新文件
        if(!file.exists()){
        	System.out.println("不存在文件，创建一个新文件"+filePath);
        	file.createNewFile();
        } 
        //读取文本并处理文本
        OutputFileProcess(storeDay, filePath);
        
        //处理storeDayNotes加上日期
      		for(int i = 0; i < 32; i++){
      			String middle = storeDay[i];
      			if(middle != null){
      				storeDayNotes[i] = middle.substring(middle.indexOf("Ω")+1);
      			}
      		}
	}
	
	//处理读出文件，同样屏蔽掉\r不显示，保存在storeDay[]
	public void OutputFileProcess(String storeDay[], String filePath) throws IOException
	{
        // 一次读多个字符
        char[] temChars = new char[1000];
        int charread = 0;
        int dayCount = 1;
        int dayInt = 0;
        Reader reader = null;
        
        reader = new InputStreamReader(new FileInputStream(filePath),"GBK");
     // 读入多个字符到字符数组中，charread为一次读取字符数
        try {
        	while ((charread = reader.read(temChars)) != -1)
        	{
                // 同样屏蔽掉\r不显示
                if ((charread != temChars.length)|| (temChars[temChars.length - 1] == '\r')) 
                {
                    for (int i = 0; i < charread; i++) 
                    {
                        if (temChars[i] == '\r') 
                        {
                            continue;
                        } 
                        else 
                        {
                            if(temChars[i] == '∑')
                            {
                            	if(temChars[i+2] == 'Ω')
                            	{
                            		dayInt = temChars[i+1] - '0';
                            	}
                            	else
                            	{
                            		dayInt = (temChars[i+1]-'0')*10+(temChars[i+2]-'0');
                            	}
                            }
                            storeDay[dayInt] += temChars[i];
                        }
                    }
                }
            }
        }catch (Exception e1)
         {
            e1.printStackTrace();
         } finally
           {
              if (reader != null) 
              {
                try {
                    reader.close();
                } catch (IOException e) 
                  {
                	e.printStackTrace();
                  }
            }
        }
	}
	
	void writeIntoFile(String[] storeDayNotes, String monthPath, int day){
//		filePath = classPath+"/storeNotes/"+monthPath+".txt";
		filePath = "./"+monthPath+".txt";
		System.out.println("filePath="+filePath);
		try{
			File file = new File(filePath);
			FileOutputStream out=new FileOutputStream(file,false);
			
			//如果使用追加方式用true
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < 32; i++){
				if (storeDayNotes[i]!=null){
					String putIn = "∑" + i + "Ω" + storeDayNotes[i];
					sb.append(putIn);
				}
			}
			out.write(sb.toString().getBytes("GBK"));
			out.close();
		}
		 catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }
	}
	
}
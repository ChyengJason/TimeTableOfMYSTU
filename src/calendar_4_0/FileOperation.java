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
	 * ��ȡ��ǰ�·ݵ����б���¼��Ϣ����ֵ��storeDayNotes����
	 */
	void readMonthNotes(String[] storeDayNotes, String monthPath) throws IOException{
		//��ʼ��������ΪNULL
		for(int i =0; i < storeDayNotes.length; i++){
			storeDayNotes[i] = "";
		}
		
		String storeDay[] = new String[32];

		//�ļ���ǰ·��
		filePath = "./"+monthPath+".txt";
		System.out.println("�ļ�·��="+filePath);
		
		File file = new File(filePath);
     
        //�������򴴽����ļ�
        if(!file.exists()){
        	System.out.println("�������ļ�������һ�����ļ�"+filePath);
        	file.createNewFile();
        } 
        //��ȡ�ı��������ı�
        OutputFileProcess(storeDay, filePath);
        
        //����storeDayNotes��������
      		for(int i = 0; i < 32; i++){
      			String middle = storeDay[i];
      			if(middle != null){
      				storeDayNotes[i] = middle.substring(middle.indexOf("��")+1);
      			}
      		}
	}
	
	//��������ļ���ͬ�����ε�\r����ʾ��������storeDay[]
	public void OutputFileProcess(String storeDay[], String filePath) throws IOException
	{
        // һ�ζ�����ַ�
        char[] temChars = new char[1000];
        int charread = 0;
        int dayCount = 1;
        int dayInt = 0;
        Reader reader = null;
        
        reader = new InputStreamReader(new FileInputStream(filePath),"GBK");
     // �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
        try {
        	while ((charread = reader.read(temChars)) != -1)
        	{
                // ͬ�����ε�\r����ʾ
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
                            if(temChars[i] == '��')
                            {
                            	if(temChars[i+2] == '��')
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
			
			//���ʹ��׷�ӷ�ʽ��true
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < 32; i++){
				if (storeDayNotes[i]!=null){
					String putIn = "��" + i + "��" + storeDayNotes[i];
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
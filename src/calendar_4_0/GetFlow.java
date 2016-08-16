package calendar_4_0;

import java.awt.Toolkit;
import javax.swing.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class GetFlow 
{ 
	private String flow;
	
	public String GetFlowString()
	{
		try{
			if(!httpget())
			{
				flow="  未登陆  ";
			}
		}catch(Exception e)
		{
			flow=" 未连接网络 ";
		}
		return flow;	
	}
	
	public boolean httpget() throws Exception
	{
		//通过get方法获取流量监控
										
		HttpClient httpClient = new DefaultHttpClient();
				 		
		HttpGet httpGet= new HttpGet("http://192.168.31.4:8080/?url=");
					
		HttpResponse httpResponse = httpClient.execute(httpGet);
					
		String htmlstr=EntityUtils.toString(httpResponse.getEntity());
									
//		System.out.println(htmlstr);
		String s = htmlstr;
					
		if(!s.contains("http://192.168.31.4:8080/customize/authentication/submit.gif"))
		{
			String a[] = s.split("id=\"ub\">");	
			String t = a[1];
			String b[] = t.split(",");
					
			String c[] = t.split("id=\"tb\">");
			String m = c[1];
			String d[] = m.split(",");
					
			flow=b[0]+"M / "+d[0]+"M";
			return true;
		}
		else 
		{
			System.out.println(" 未登陆 ");
			return false;
		}
	}
	
//	public GetFlow()
//	{
//		 jl1 = new JLabel("已用流量:"+l1+"M"+"|"+"预定流量:"+l2+"M");
//		 jl1.setBounds(200, 100, 100, 50);
//		 this.add(jl1);
	 
//		 this.setTitle("流量线程");
//		 this.setLocation((width - 500) / 2, (height - 500) / 2);
//		 this.setSize(300, 200);
//		 this.setVisible(true);
//	}

 
//	public static void main(String[] args)
//	{
//		Runnable aRunnable = new GetFlow();
//		new Thread(aRunnable).start();
//	  
//	}
 
//	public void run() 
//	{
//		while(true){
//			try {   
//				try {
//					if(httpget())
//					{
//						jl1.setText("已用流量:"+l1+"M"+"|"+"预定流量:"+l2+"M");
//					}
//					else 
//					{
//						jl1.setText("未登录");
//					}
//				} 
//				catch (Exception e) {
//					// TODO Auto-generated catch block
//				e.printStackTrace();
//				}
//			Thread.sleep(10000);
//			} 
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
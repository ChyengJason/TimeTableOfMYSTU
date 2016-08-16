package calendar_4_0;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/*
 * 登录学分制，获取个人信息（学号，姓名）
 * 验证登录账号的合法性
 */
public class GetUserInfo {
	private String sessionIdCookie; 	// 登录前的cookie信息
	private String cookie;				// 登录后的cookie信息
	private String viewstate;			// 隐藏表单中的数据
	private String eventvalidation; 	// 隐藏表单中的数据
    String name="";
    String pwd="";
	
	String studentId;					//用户学号
	String studentName;					//用户姓名
	boolean legalUserOrNot=false;		//用户账号的合法标识
	
	public GetUserInfo(){
	}
	public GetUserInfo(String loginUserName, String loginPassWd)
	{
		this.name = loginUserName;
		this.pwd = loginPassWd;
		httpget();
		post();
	}
	//获取登录前的三个数据： viewstate，eventvalidation，ASP.NET_SessionId
	public boolean httpget() {
		DefaultHttpClient httpClient = new DefaultHttpClient(); // http客户端对象
		HttpPost httpPost = new HttpPost("http://credit.stu.edu.cn/portal/stulogin.aspx"); // httppost对象，post网页请求
		HttpResponse httpResponse = null;
		String htmlstr = null;
		boolean webState = false;
		try {
			httpResponse = httpClient.execute(httpPost);		
			try {
				htmlstr = EntityUtils.toString(httpResponse.getEntity());
				// 登录界面html代码转化为字符串
				String htmlstr1 = htmlstr;
				// 字符串处理，获取viewstate的值,/wEPDwUKMTM1MzI1Njg5N2Rk47x7/EAaT/4MwkLGxreXh8mHHxA=
				htmlstr = htmlstr.split(" id=\"__VIEWSTATE\" value=\"")[1];
				htmlstr = htmlstr.split("div")[0];
				htmlstr = htmlstr.split("\" />")[0];
				viewstate = htmlstr;

				// 字符串处理，获取eventvalidation的值,/wEWBAKo25zdBALT8dy8BQLG8eCkDwKk07qFCRXt1F3RFYVdjuYasktKIhLnziqd
				htmlstr1 = htmlstr1.split(" id=\"__EVENTVALIDATION\" value=\"")[1];
				htmlstr1 = htmlstr1.split("div")[0];
				htmlstr1 = htmlstr1.split("\" />")[0];
				eventvalidation = htmlstr1;

				// 获取cookie,ASP.NET_SessionId=alw2wazsxt2sqybe44uapcmr
				// System.out.print("Cookie:"+"\n");
				String tempCookie = httpResponse.getFirstHeader("Set-Cookie").toString();
				String[] strings = tempCookie.split(";");
				tempCookie = strings[0];
				strings = tempCookie.split(":");
				sessionIdCookie = strings[1];
				webState = true;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		} catch (ClientProtocolException e) {		
			System.out.println("请检查您的网络连接！");
			webState = false;
		} catch (IOException e) {
			System.out.println("请检查您的网络连接！");
			webState = false;
		} 
		return webState;
	}

	// 模拟登录,获取登录后的cookie
	public void post() {
		// 生成httpClient客户端
		HttpClient httpClient = new DefaultHttpClient();
		// 生成httpPost请求，UL+httpHeader，进行模拟登录
		HttpPost httpPost = new HttpPost(
				"http://credit.stu.edu.cn/portal/stulogin.aspx");
		httpPost.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpPost.addHeader("Accept-Encoding", "gzip,deflate");
		httpPost.addHeader("Connection", "keep-alive");
		httpPost.addHeader("Cookie", sessionIdCookie); 				// ASP.NET_SessionId
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.addHeader("Referer",
				"http://credit.stu.edu.cn/portal/stulogin.aspx");
		httpPost.addHeader(
				"user-agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");

		// 构建哈希表
		HashMap<String, String> parmas = new HashMap<String, String>();
		parmas.put("__EVENTTARGET", "");
		parmas.put("__EVENTARGUMENT", "");
		parmas.put("__VIEWSTATE", viewstate);				 // 传入viewstate
		parmas.put("__EVENTVALIDATION", eventvalidation);    // 传入eventvalidation
		parmas.put("txtUserID",name); 						// 传入账户
		parmas.put("txtUserPwd",pwd); 						// 传入密码
		parmas.put("btnLogon", "%B5%C7%C2%BC"); 			// 登录的按钮

		// 构建键值列表，要传入表单的信息
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		if (parmas != null) {
			Set<String> keys = parmas.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				pairs.add(new BasicNameValuePair(key, parmas.get(key)));
			}
		}
		
		UrlEncodedFormEntity p_entity = null;
		try {
			p_entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} // 将表单数据编码为utf-8
		httpPost.setEntity(p_entity); // 设置表单	
		HttpResponse httpResponse = null;
		try {
			// 发送登录请求，得到回应
			httpResponse = httpClient.execute(httpPost);
			// 判断是否得到服务器的响应
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) { 
				try{
					// 获取登录后的cookies，保存到string1
					String tempCookie = httpResponse.getFirstHeader("Set-Cookie").toString();
					String[] strings = tempCookie.split(";");
					tempCookie = strings[0];
					strings = tempCookie.split(":");
					cookie = strings[1];
					System.out.println("合法用户");
					legalUserOrNot = true;
					getcode();
				}
				catch (Exception e){
					System.out.println("非法用户");
					legalUserOrNot = false;
				}		
			}
		} catch (ClientProtocolException e1) {
			System.out.println("服务器无响应");
//			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("服务器无响应");
//			e1.printStackTrace();
		} 
	}
	
	//获取学分制个人信息页面代码
	public void getcode() throws Exception {
		//生成一个httpClient对象，相当于一个浏览器客户端
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		//用之前保存的sessionID和cookies信息获取网页源代码
		HttpPost httpPost1 = new HttpPost("http://credit.stu.edu.cn/Student/DisplayStudentInfo.aspx");
		httpPost1.addHeader("Cookie", sessionIdCookie + "; " + cookie);
		HttpResponse httpResponse1 = httpClient.execute(httpPost1);
		String res1 = EntityUtils.toString(httpResponse1.getEntity());
//		System.out.println(res1);
		getUserInfo(res1);
	}
	//解析学分制个人信息页面代码，获得学号，姓名
	public void getUserInfo(String res)
	{
		String resAnalysis = res;
		String tempString;
		String[] resFragment = resAnalysis.split("</b><span id=\"Label1\">");
	
		tempString = resFragment[1];
		resFragment = tempString.split("</span>");
		studentName = resFragment[0];
		System.out.println(studentName);
		
		resFragment = resAnalysis.split("</b><span id=\"Label5\">");
		tempString = resFragment[1];
		resFragment = tempString.split("</span>");
		studentId = resFragment[0];
		System.out.println(studentId);
	
	}
	//返回学号
	public String getStudentId()
	{
		return this.studentId;
	}
	//返回名字
	public String getStudentName()
	{
		return this.studentName;
	}
	//获得验证登录账号是否合法的结果
	public boolean getLoginResult()
	{
		return this.legalUserOrNot;
	}
}

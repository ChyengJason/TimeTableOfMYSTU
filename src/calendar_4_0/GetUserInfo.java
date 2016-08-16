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
 * ��¼ѧ���ƣ���ȡ������Ϣ��ѧ�ţ�������
 * ��֤��¼�˺ŵĺϷ���
 */
public class GetUserInfo {
	private String sessionIdCookie; 	// ��¼ǰ��cookie��Ϣ
	private String cookie;				// ��¼���cookie��Ϣ
	private String viewstate;			// ���ر��е�����
	private String eventvalidation; 	// ���ر��е�����
    String name="";
    String pwd="";
	
	String studentId;					//�û�ѧ��
	String studentName;					//�û�����
	boolean legalUserOrNot=false;		//�û��˺ŵĺϷ���ʶ
	
	public GetUserInfo(){
	}
	public GetUserInfo(String loginUserName, String loginPassWd)
	{
		this.name = loginUserName;
		this.pwd = loginPassWd;
		httpget();
		post();
	}
	//��ȡ��¼ǰ���������ݣ� viewstate��eventvalidation��ASP.NET_SessionId
	public boolean httpget() {
		DefaultHttpClient httpClient = new DefaultHttpClient(); // http�ͻ��˶���
		HttpPost httpPost = new HttpPost("http://credit.stu.edu.cn/portal/stulogin.aspx"); // httppost����post��ҳ����
		HttpResponse httpResponse = null;
		String htmlstr = null;
		boolean webState = false;
		try {
			httpResponse = httpClient.execute(httpPost);		
			try {
				htmlstr = EntityUtils.toString(httpResponse.getEntity());
				// ��¼����html����ת��Ϊ�ַ���
				String htmlstr1 = htmlstr;
				// �ַ���������ȡviewstate��ֵ,/wEPDwUKMTM1MzI1Njg5N2Rk47x7/EAaT/4MwkLGxreXh8mHHxA=
				htmlstr = htmlstr.split(" id=\"__VIEWSTATE\" value=\"")[1];
				htmlstr = htmlstr.split("div")[0];
				htmlstr = htmlstr.split("\" />")[0];
				viewstate = htmlstr;

				// �ַ���������ȡeventvalidation��ֵ,/wEWBAKo25zdBALT8dy8BQLG8eCkDwKk07qFCRXt1F3RFYVdjuYasktKIhLnziqd
				htmlstr1 = htmlstr1.split(" id=\"__EVENTVALIDATION\" value=\"")[1];
				htmlstr1 = htmlstr1.split("div")[0];
				htmlstr1 = htmlstr1.split("\" />")[0];
				eventvalidation = htmlstr1;

				// ��ȡcookie,ASP.NET_SessionId=alw2wazsxt2sqybe44uapcmr
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
			System.out.println("���������������ӣ�");
			webState = false;
		} catch (IOException e) {
			System.out.println("���������������ӣ�");
			webState = false;
		} 
		return webState;
	}

	// ģ���¼,��ȡ��¼���cookie
	public void post() {
		// ����httpClient�ͻ���
		HttpClient httpClient = new DefaultHttpClient();
		// ����httpPost����UL+httpHeader������ģ���¼
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

		// ������ϣ��
		HashMap<String, String> parmas = new HashMap<String, String>();
		parmas.put("__EVENTTARGET", "");
		parmas.put("__EVENTARGUMENT", "");
		parmas.put("__VIEWSTATE", viewstate);				 // ����viewstate
		parmas.put("__EVENTVALIDATION", eventvalidation);    // ����eventvalidation
		parmas.put("txtUserID",name); 						// �����˻�
		parmas.put("txtUserPwd",pwd); 						// ��������
		parmas.put("btnLogon", "%B5%C7%C2%BC"); 			// ��¼�İ�ť

		// ������ֵ�б�Ҫ���������Ϣ
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
		} // �������ݱ���Ϊutf-8
		httpPost.setEntity(p_entity); // ���ñ�	
		HttpResponse httpResponse = null;
		try {
			// ���͵�¼���󣬵õ���Ӧ
			httpResponse = httpClient.execute(httpPost);
			// �ж��Ƿ�õ�����������Ӧ
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) { 
				try{
					// ��ȡ��¼���cookies�����浽string1
					String tempCookie = httpResponse.getFirstHeader("Set-Cookie").toString();
					String[] strings = tempCookie.split(";");
					tempCookie = strings[0];
					strings = tempCookie.split(":");
					cookie = strings[1];
					System.out.println("�Ϸ��û�");
					legalUserOrNot = true;
					getcode();
				}
				catch (Exception e){
					System.out.println("�Ƿ��û�");
					legalUserOrNot = false;
				}		
			}
		} catch (ClientProtocolException e1) {
			System.out.println("����������Ӧ");
//			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("����������Ӧ");
//			e1.printStackTrace();
		} 
	}
	
	//��ȡѧ���Ƹ�����Ϣҳ�����
	public void getcode() throws Exception {
		//����һ��httpClient�����൱��һ��������ͻ���
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		//��֮ǰ�����sessionID��cookies��Ϣ��ȡ��ҳԴ����
		HttpPost httpPost1 = new HttpPost("http://credit.stu.edu.cn/Student/DisplayStudentInfo.aspx");
		httpPost1.addHeader("Cookie", sessionIdCookie + "; " + cookie);
		HttpResponse httpResponse1 = httpClient.execute(httpPost1);
		String res1 = EntityUtils.toString(httpResponse1.getEntity());
//		System.out.println(res1);
		getUserInfo(res1);
	}
	//����ѧ���Ƹ�����Ϣҳ����룬���ѧ�ţ�����
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
	//����ѧ��
	public String getStudentId()
	{
		return this.studentId;
	}
	//��������
	public String getStudentName()
	{
		return this.studentName;
	}
	//�����֤��¼�˺��Ƿ�Ϸ��Ľ��
	public boolean getLoginResult()
	{
		return this.legalUserOrNot;
	}
}

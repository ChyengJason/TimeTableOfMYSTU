package calendar_4_0;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;


public class LoginWidget {
	String regexUname = "[0-9][0-9][a-z]+[0-9]*";	//用于规范用户名输入的正则表达式
//	String regexPasswd = "[0-9a-z\\W]+";
	String userName;								//登录窗口输入的用户名
	String passWd;									//登录窗口输入的密码
	boolean remeberInfoFlag = false;				//记住登录用户按钮的标识
	boolean autoLoginFlag = false;					//自动登录按钮的标识
	private Text unameText;
	private Text pwdText;
	Shell shell;
	Display display;
	int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
	String loginInfoPath = "./LoginInfo.txt";		//未加密前的文件名
	String loginInfoEnPath = "./LoginInfoEn.txt";	//加密后输出的文件名
	String filePlainText = "./LoginInfoPlain.txt";	//解密后的明文文件名
	String lock ="stuCalendar";
	FileEncryptor fileEncryptor =new FileEncryptor(lock, loginInfoPath, loginInfoEnPath, filePlainText);

	public LoginWidget(Shell LoginShell){
		//登录主窗口
		this.shell = LoginShell;
		this.display = LoginShell.getDisplay();
		shell.setSize(400, 280);
		shell.setLayout(null);
		shell.setText("登录界面");
		shell.setBackground(new Color(null, 255, 255, 255));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		//设置登录主窗口的位置
        String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
         //windows环境
        	shell.setLocation(screenW/2 , 200);
        }  
        else
        {
        //其他环境
        	shell.setLocation(screenW , 0);
        }
		
		//汕大的logo
		Label ImageLabel = new Label(shell, SWT.NONE);
		ImageLabel.setImage(new Image(Display.getDefault(),"./images/lala.png"));
		ImageLabel.setBounds(50, 10, 50, 60);
		//标题
		Label titleLabel = new Label(shell, SWT.NONE);
		titleLabel.setBounds(120, 20, 250, 60);
		titleLabel.setText("汕大桌面日历");
		titleLabel.setFont(new Font(Display.getDefault(), "宋体", 28, SWT.BOLD));
		
	
		/********************************************************/
		//用户名输入框
		Label unameLabel = new Label(shell, SWT.NONE);
		unameLabel.setBounds(78, 100, 61, 17);
		unameLabel.setText("用户名:");
		unameText = new Text(shell, SWT.BORDER);
		unameText.setBounds(140, 100, 170, 23);
				
		//用户密码输入框
		Label pwdlabel = new Label(shell, SWT.NONE);
		pwdlabel.setBounds(78, 130, 61, 17);
		pwdlabel.setText(" 密码:");
		pwdText = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		pwdText.setBounds(140, 130, 170, 23);
		
		//记住登录账户按钮
		final Button remeberCheckbox = new Button(shell, SWT.CHECK);
	
		remeberCheckbox.setBounds(100, 160, 98, 17);
		remeberCheckbox.setText("记住登录账户");
		remeberCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remeberInfoFlag = remeberCheckbox.getSelection();		//标记已记住登录账户
//				System.out.println(remeberInfoFlag);
			}		
		});
		
		//自动登录按钮
		final Button loginAutomatic = new Button(shell, SWT.CHECK);
		loginAutomatic.setBounds(250, 160, 98, 17);
		loginAutomatic.setText("自动登录");
		loginAutomatic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				autoLoginFlag = loginAutomatic.getSelection();		//标记自动登录
//				System.out.println(autoLoginFlag);
			}		
		});
		
		//如果能够打开登录文件，获取密码，和用户名。则填入输入框中 
		File loginFile = new File(loginInfoEnPath);
		if(loginFile.exists()){
			try {
				//解密
				fileEncryptor.FileUnLock();
				File loginFileOrigin = new File(filePlainText);
				FileReader readLoginInfo = new FileReader(loginFileOrigin);
				char[] tempchar = new char[1024];
				try {
					//将登录信息文件都读取出来
					int hasRead = readLoginInfo.read(tempchar);
					String tempstring = new String(tempchar,0,hasRead);
					String[] tempS = tempstring.split("\r\n");	//截取文件中用户名，密码	
					//设置用户名，密码为上次记住的状态
					unameText.setText(tempS[0]);
					pwdText.setText(tempS[1]);
					//设置记住登录账户为选择状态
					remeberCheckbox.setSelection(true);	
					remeberInfoFlag=true;
					//设置自动登录为选择状态
					if(tempS[tempS.length-1].equals("autoLogin")){
						loginAutomatic.setSelection(true);
					}
					readLoginInfo.close();
					//删除解码后的登录文件
					loginFileOrigin.delete();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		/********************************************************/
	
		//登录按钮
		Button loginBtn = new Button(shell, SWT.NONE);
		loginBtn.setBounds(140, 190, 130, 27);
		loginBtn.setText("登录系统");
		//登录按钮的响应事件
		loginBtn.addSelectionListener(new loginBtListener());
//		shell.open();
			
	}
	//登录按钮监听器，判断登录账号的合法性，记录选项设置。
	class loginBtListener implements SelectionListener
	 {
		public void widgetSelected(SelectionEvent e) {
			userName = unameText.getText();
			passWd = pwdText.getText();
			//用户名为空，密码不为空，提醒用户输入用户名
			if(userName.equals("") && !passWd.equals("") )
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("请输入用户名！");
				dialog.open();
			}
			//用户名为不为空，密码为空，提醒用户输入密码
			else if( !userName.equals("") && passWd.equals("") )
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("请输入密码！");
				dialog.open();
			}	
			//用户名为空，密码为空，提醒用户输入登录信息
			else if (passWd.equals("") && userName.equals(""))
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("请输入登录信息！");
				dialog.open();
			}
			//用户名，密码登录都不为空
			else if (!passWd.equals("") && !userName.equals(""))
			{
//				System.out.println(userName);
//				System.out.println(passWd);
				//用正则表达式判断用户名是否符合规范
				if(!userName.matches(regexUname))
				{
					MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
					dialog.setText("Warning");
					dialog.setMessage("请输入正确的用户名！");
					dialog.open();
				}
				//用户输入信息完整
				else{
					System.out.println("输入格式正确");
					//**********************************************************
					if(remeberInfoFlag)
					{
						//创建保存登录信息的文件
						File loginInfoFile = new File(loginInfoPath);
						if(!loginInfoFile.exists())
						{
							try {
								loginInfoFile.createNewFile();				
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
							//写入文件
						try {
							FileWriter infoWriter = new FileWriter(loginInfoFile);
							infoWriter.write(userName+"\r\n");
							infoWriter.write(passWd+"\r\n");
							infoWriter.write("rememberUser\r\n");
							if(autoLoginFlag){
								infoWriter.write("autoLogin\r\n");
							}
							infoWriter.close();
							System.out.println("登录信息已保存");
							//进行加密文件操作
							fileEncryptor.FileLock();
							//删除未加密的登录文件
							loginInfoFile.delete();
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
					}
					else{//当标识不记住登录信息，删除原来的登录文件
						File loginInfoFile = new File("./LoginInfo.txt");
						if(loginInfoFile.exists())
							loginInfoFile.delete();
						//删除原来的登录加密文件
						File loginInfoEnFile = new File(loginInfoEnPath);
						if(loginInfoEnFile.exists())
							loginInfoEnFile.delete();
					}
					//**********************************************************
					//检测网络状态
					GetUserInfo getLoginState = new GetUserInfo();
					//如果无法连接网络进行验证
					if(!getLoginState.httpget()){
						MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
						dialog.setText("验证");
						dialog.setMessage("无法连接网络进行验证,\n请检测你的网络连接！");
						dialog.open();
					}
					//如果能连接上网络进行验证
					else{
						//验证登录账号的合法性
						System.out.println(userName + " " + passWd);
						GetUserInfo getUserInfo = new GetUserInfo(userName,passWd);
						boolean loginCheck =getUserInfo.getLoginResult();
						//如果账号合法且记住用户按钮的标识为按下,则保存用户名，密码到文件中
						if(loginCheck)
						{
							shell.dispose();								//关闭登录窗口
							System.out.println("登录成功!");
							String studentID = getUserInfo.getStudentId();	//获得学号
							Main.loginState = true;							//记住登录状态
							Main.loginId = studentID;						//记住用户的学号
							new CourseQueryUI(studentID, display);			//生成课表界面
						}
						//账户不合法，弹出错误信息框
						else
						{
							MessageBox showLoginError = new MessageBox(shell,SWT.ICON_WARNING);
							showLoginError.setText("登录错误");
							showLoginError.setMessage("账户名或密码错误！\r\n请重新登录");
							showLoginError.open();
						}											
					}
					//**********************************************************
				}
				//**********************************************************
			}
		}
		public void widgetDefaultSelected(SelectionEvent arg0) {}
	}
	
}
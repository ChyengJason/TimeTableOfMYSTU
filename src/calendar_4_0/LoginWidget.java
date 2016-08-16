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
	String regexUname = "[0-9][0-9][a-z]+[0-9]*";	//���ڹ淶�û��������������ʽ
//	String regexPasswd = "[0-9a-z\\W]+";
	String userName;								//��¼����������û���
	String passWd;									//��¼�������������
	boolean remeberInfoFlag = false;				//��ס��¼�û���ť�ı�ʶ
	boolean autoLoginFlag = false;					//�Զ���¼��ť�ı�ʶ
	private Text unameText;
	private Text pwdText;
	Shell shell;
	Display display;
	int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
	String loginInfoPath = "./LoginInfo.txt";		//δ����ǰ���ļ���
	String loginInfoEnPath = "./LoginInfoEn.txt";	//���ܺ�������ļ���
	String filePlainText = "./LoginInfoPlain.txt";	//���ܺ�������ļ���
	String lock ="stuCalendar";
	FileEncryptor fileEncryptor =new FileEncryptor(lock, loginInfoPath, loginInfoEnPath, filePlainText);

	public LoginWidget(Shell LoginShell){
		//��¼������
		this.shell = LoginShell;
		this.display = LoginShell.getDisplay();
		shell.setSize(400, 280);
		shell.setLayout(null);
		shell.setText("��¼����");
		shell.setBackground(new Color(null, 255, 255, 255));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		//���õ�¼�����ڵ�λ��
        String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
         //windows����
        	shell.setLocation(screenW/2 , 200);
        }  
        else
        {
        //��������
        	shell.setLocation(screenW , 0);
        }
		
		//�Ǵ��logo
		Label ImageLabel = new Label(shell, SWT.NONE);
		ImageLabel.setImage(new Image(Display.getDefault(),"./images/lala.png"));
		ImageLabel.setBounds(50, 10, 50, 60);
		//����
		Label titleLabel = new Label(shell, SWT.NONE);
		titleLabel.setBounds(120, 20, 250, 60);
		titleLabel.setText("�Ǵ���������");
		titleLabel.setFont(new Font(Display.getDefault(), "����", 28, SWT.BOLD));
		
	
		/********************************************************/
		//�û��������
		Label unameLabel = new Label(shell, SWT.NONE);
		unameLabel.setBounds(78, 100, 61, 17);
		unameLabel.setText("�û���:");
		unameText = new Text(shell, SWT.BORDER);
		unameText.setBounds(140, 100, 170, 23);
				
		//�û����������
		Label pwdlabel = new Label(shell, SWT.NONE);
		pwdlabel.setBounds(78, 130, 61, 17);
		pwdlabel.setText(" ����:");
		pwdText = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		pwdText.setBounds(140, 130, 170, 23);
		
		//��ס��¼�˻���ť
		final Button remeberCheckbox = new Button(shell, SWT.CHECK);
	
		remeberCheckbox.setBounds(100, 160, 98, 17);
		remeberCheckbox.setText("��ס��¼�˻�");
		remeberCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remeberInfoFlag = remeberCheckbox.getSelection();		//����Ѽ�ס��¼�˻�
//				System.out.println(remeberInfoFlag);
			}		
		});
		
		//�Զ���¼��ť
		final Button loginAutomatic = new Button(shell, SWT.CHECK);
		loginAutomatic.setBounds(250, 160, 98, 17);
		loginAutomatic.setText("�Զ���¼");
		loginAutomatic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				autoLoginFlag = loginAutomatic.getSelection();		//����Զ���¼
//				System.out.println(autoLoginFlag);
			}		
		});
		
		//����ܹ��򿪵�¼�ļ�����ȡ���룬���û������������������ 
		File loginFile = new File(loginInfoEnPath);
		if(loginFile.exists()){
			try {
				//����
				fileEncryptor.FileUnLock();
				File loginFileOrigin = new File(filePlainText);
				FileReader readLoginInfo = new FileReader(loginFileOrigin);
				char[] tempchar = new char[1024];
				try {
					//����¼��Ϣ�ļ�����ȡ����
					int hasRead = readLoginInfo.read(tempchar);
					String tempstring = new String(tempchar,0,hasRead);
					String[] tempS = tempstring.split("\r\n");	//��ȡ�ļ����û���������	
					//�����û���������Ϊ�ϴμ�ס��״̬
					unameText.setText(tempS[0]);
					pwdText.setText(tempS[1]);
					//���ü�ס��¼�˻�Ϊѡ��״̬
					remeberCheckbox.setSelection(true);	
					remeberInfoFlag=true;
					//�����Զ���¼Ϊѡ��״̬
					if(tempS[tempS.length-1].equals("autoLogin")){
						loginAutomatic.setSelection(true);
					}
					readLoginInfo.close();
					//ɾ�������ĵ�¼�ļ�
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
	
		//��¼��ť
		Button loginBtn = new Button(shell, SWT.NONE);
		loginBtn.setBounds(140, 190, 130, 27);
		loginBtn.setText("��¼ϵͳ");
		//��¼��ť����Ӧ�¼�
		loginBtn.addSelectionListener(new loginBtListener());
//		shell.open();
			
	}
	//��¼��ť���������жϵ�¼�˺ŵĺϷ��ԣ���¼ѡ�����á�
	class loginBtListener implements SelectionListener
	 {
		public void widgetSelected(SelectionEvent e) {
			userName = unameText.getText();
			passWd = pwdText.getText();
			//�û���Ϊ�գ����벻Ϊ�գ������û������û���
			if(userName.equals("") && !passWd.equals("") )
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("�������û�����");
				dialog.open();
			}
			//�û���Ϊ��Ϊ�գ�����Ϊ�գ������û���������
			else if( !userName.equals("") && passWd.equals("") )
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("���������룡");
				dialog.open();
			}	
			//�û���Ϊ�գ�����Ϊ�գ������û������¼��Ϣ
			else if (passWd.equals("") && userName.equals(""))
			{
				MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
				dialog.setText("Warning");
				dialog.setMessage("�������¼��Ϣ��");
				dialog.open();
			}
			//�û����������¼����Ϊ��
			else if (!passWd.equals("") && !userName.equals(""))
			{
//				System.out.println(userName);
//				System.out.println(passWd);
				//��������ʽ�ж��û����Ƿ���Ϲ淶
				if(!userName.matches(regexUname))
				{
					MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_WARNING);
					dialog.setText("Warning");
					dialog.setMessage("��������ȷ���û�����");
					dialog.open();
				}
				//�û�������Ϣ����
				else{
					System.out.println("�����ʽ��ȷ");
					//**********************************************************
					if(remeberInfoFlag)
					{
						//���������¼��Ϣ���ļ�
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
							//д���ļ�
						try {
							FileWriter infoWriter = new FileWriter(loginInfoFile);
							infoWriter.write(userName+"\r\n");
							infoWriter.write(passWd+"\r\n");
							infoWriter.write("rememberUser\r\n");
							if(autoLoginFlag){
								infoWriter.write("autoLogin\r\n");
							}
							infoWriter.close();
							System.out.println("��¼��Ϣ�ѱ���");
							//���м����ļ�����
							fileEncryptor.FileLock();
							//ɾ��δ���ܵĵ�¼�ļ�
							loginInfoFile.delete();
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
					}
					else{//����ʶ����ס��¼��Ϣ��ɾ��ԭ���ĵ�¼�ļ�
						File loginInfoFile = new File("./LoginInfo.txt");
						if(loginInfoFile.exists())
							loginInfoFile.delete();
						//ɾ��ԭ���ĵ�¼�����ļ�
						File loginInfoEnFile = new File(loginInfoEnPath);
						if(loginInfoEnFile.exists())
							loginInfoEnFile.delete();
					}
					//**********************************************************
					//�������״̬
					GetUserInfo getLoginState = new GetUserInfo();
					//����޷��������������֤
					if(!getLoginState.httpget()){
						MessageBox dialog = new MessageBox(shell,SWT.OK|SWT.ICON_INFORMATION);
						dialog.setText("��֤");
						dialog.setMessage("�޷��������������֤,\n��������������ӣ�");
						dialog.open();
					}
					//��������������������֤
					else{
						//��֤��¼�˺ŵĺϷ���
						System.out.println(userName + " " + passWd);
						GetUserInfo getUserInfo = new GetUserInfo(userName,passWd);
						boolean loginCheck =getUserInfo.getLoginResult();
						//����˺źϷ��Ҽ�ס�û���ť�ı�ʶΪ����,�򱣴��û��������뵽�ļ���
						if(loginCheck)
						{
							shell.dispose();								//�رյ�¼����
							System.out.println("��¼�ɹ�!");
							String studentID = getUserInfo.getStudentId();	//���ѧ��
							Main.loginState = true;							//��ס��¼״̬
							Main.loginId = studentID;						//��ס�û���ѧ��
							new CourseQueryUI(studentID, display);			//���ɿα����
						}
						//�˻����Ϸ�������������Ϣ��
						else
						{
							MessageBox showLoginError = new MessageBox(shell,SWT.ICON_WARNING);
							showLoginError.setText("��¼����");
							showLoginError.setMessage("�˻������������\r\n�����µ�¼");
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
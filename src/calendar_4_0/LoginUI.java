package calendar_4_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/*
 *
 */
public class LoginUI {
	Shell LoginShell;
	Display display; 

	public LoginUI(Display display){
		System.out.println("实例化登录界面");
		this.display = display;
		
		LoginShell = new Shell(display,SWT.CLOSE | SWT.MIN);
		LoginWidget loginWidget = new LoginWidget(LoginShell);
		
		LoginShell.open();
		LoginShell.layout();

		while (!LoginShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	public static void main(String args[]){
		new LoginUI(Display.getDefault());
	}
}
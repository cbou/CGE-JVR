package cge.zeppelin.util;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;


public class Splashscreen extends JWindow
{
	Image bi=Toolkit.getDefaultToolkit().getImage("./resources/pics/splash.png");

	ImageIcon ii=new ImageIcon(bi);

	public Splashscreen()  
	{
		AWTUtilities.setWindowOpaque(Splashscreen.this, false);	
		setSize(ii.getIconWidth(),ii.getIconHeight());
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void paint(Graphics g)
	{
		g.drawImage(bi,0,0,this);
	}

	public void close(){
		dispose();
	}

	public static void main(String[]args) throws Exception
	{
		Splashscreen tss=new Splashscreen();
		Thread.sleep(1000);
		tss.close();
	}
}

package org.terifan.dashboard;

import javax.swing.JFrame;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			Dashboard panel = new Dashboard();
			panel.add(new DashboardComponent(0,0,1,1));
			panel.add(new DashboardComponent(1,0,1,1));
			panel.add(new DashboardComponent(0,1,1,1));
			panel.add(new DashboardComponent(1,1,1,1));
			panel.add(new DashboardComponent(2,0,2,2));
			panel.add(new DashboardComponent(0,2,4,2));
			panel.add(new DashboardComponent(4,0,1,1));
			panel.add(new DashboardComponent(4,1,1,1));
			panel.add(new DashboardComponent(4,2,1,1));
			panel.add(new DashboardComponent(4,3,1,1));

			JFrame frame = new JFrame();
			frame.add(panel);
			frame.setSize(1400, 900);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

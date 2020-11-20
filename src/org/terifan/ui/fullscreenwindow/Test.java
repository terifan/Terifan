package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			FullScreenWindow wnd = new FullScreenWindow(null, "New window", false, true, new BlackWindowBorder())
			{
				@Override protected boolean onWindowClosing(){System.out.println("closing");return true;}
				@Override protected void onWindowClosed(){System.out.println("closed");}
				@Override protected void onWindowResized(){System.out.println("resized");}
				@Override protected void onWindowMinimized(){System.out.println("minimized");}
				@Override protected void onWindowMaximized(){System.out.println("maximized");}
				@Override protected void onWindowRestored(){System.out.println("restored");}
				@Override protected void onWindowGainedFocus(){System.out.println("focused");}
				@Override protected void onWindowLostFocus(){System.out.println("unfocused");}
			};

			JPanel panel1 = new BlackWindowPanel();
			panel1.setBackground(new Color(51,51,51));
			panel1.add(new JButton(new AbstractAction("undecorated")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setUndecorated(!wnd.isUndecorated());
				}
			}));
			panel1.add(new JButton(new AbstractAction("border")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setBorderPainted(!wnd.isBorderPainted());
				}
			}));

			JPanel panel2 = new BlackWindowPanel();
			panel2.setBackground(new Color(51,51,51));
			JLabel label = new JLabel("xxxxxxxxxxxxxxxxxx");
			label.setForeground(Color.WHITE);
			panel2.add(label);

			JPanel panelX = new JPanel(new BorderLayout(2, 2));
			panelX.setBackground(new Color(34, 34, 34));
			panelX.add(panel1, BorderLayout.CENTER);
			panelX.add(panel2, BorderLayout.EAST);

			wnd.setLayout(new BorderLayout());
			wnd.add(panelX);
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

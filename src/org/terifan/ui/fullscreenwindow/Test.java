package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;


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

			JPanel panel = new BlackWindowPanel();
			panel.setBackground(new Color(51,51,51));
			panel.add(new JButton(new AbstractAction("undecorated")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setUndecorated(!wnd.isUndecorated());
				}
			}));
			panel.add(new JButton(new AbstractAction("border")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setBorderPainted(!wnd.isBorderPainted());
				}
			}));

			wnd.getContentPanel().setLayout(new BorderLayout());
			wnd.add(panel);
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

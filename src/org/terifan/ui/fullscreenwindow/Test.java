package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Arrays;
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
			DefaultWindowBorder windowBorder = new BlackWindowBorder();

			WindowMenuBar menuBar = new WindowMenuBar();
			menuBar.add(new WindowMenuItem("File"), new WindowMenuItem("Edit"), new WindowMenuItem("Render"), new WindowMenuItem("Window"), new WindowMenuItem("Help"));
			windowBorder.setMenuBar(menuBar);

			WindowTabBar tabBar = new WindowTabBar();
			tabBar.add(new WindowTabItem("Layout"), new WindowTabItem("Modeling"), new WindowTabItem("Scultping"), new WindowTabItem("UV Editing"), new WindowTabItem("Texture Paint"), new WindowTabItem("Shading"), new WindowTabItem("Animation"), new WindowTabItem("Rendering"), new WindowTabItem("Compositing"), new WindowTabItem("Scripting"));
			tabBar.setSelectedIndex(1);
			windowBorder.setTabBar(tabBar);

			FullScreenWindow wnd = new FullScreenWindow(null, "New window", false, true, windowBorder);

			wnd.setOnClosed(()->System.out.println("closed"));
//			wnd.setOnClosing(()->System.out.println("closing"));
//			wnd.setOnResized(()->System.out.println("resized"));
//			wnd.setOnMinimized(()->System.out.println("minimized"));
//			wnd.setOnMaximized(()->System.out.println("maximized"));
//			wnd.setOnRestored(()->System.out.println("restored"));
//			wnd.setOnGainedFocus(()->System.out.println("focused"));
//			wnd.setOnLostFocus(()->System.out.println("unfocused"));

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

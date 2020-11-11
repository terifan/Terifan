package org.terifan.ui.fullscreenwindow;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			FullScreenWindow wnd = new FullScreenWindow(null, "New window", false, true, 1);
			wnd.getContentPanel().setLayout(new GridLayout(1, 2));
			wnd.add(new JButton(new AbstractAction("undecorated")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setUndecorated(!wnd.isUndecorated());
				}
			}));
			wnd.add(new JButton(new AbstractAction("border")
			{
				@Override
				public void actionPerformed(ActionEvent aE)
				{
					wnd.setBorderVisible(!wnd.isBorderVisible());
				}
			}));
			wnd.setOnClosing(() ->
			{
				System.out.println("closing");
				return true;
			});
			wnd.setOnClosed(() -> System.out.println("closed"));
			wnd.setOnResize(() -> System.out.println("resize"));
			wnd.setOnMinizmie(() -> System.out.println("minimize"));
			wnd.setOnMaximize(() -> System.out.println("maximize"));
			wnd.setOnRestore(() -> System.out.println("restore"));
			wnd.setOnGainedFocus(() -> System.out.println("focused"));
			wnd.setOnLostFocus(() -> System.out.println("unfocused"));
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

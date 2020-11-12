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
			FullScreenWindow wnd = new FullScreenWindow(null, "New window", false, true, 1)
			{
				protected boolean onWindowClosing()
				{
					System.out.println("closing");
					return true;
				}

				protected void onWindowClosed(){System.out.println("closed");}
				protected void onWindowResized(){System.out.println("resized");}
				protected void onWindowMinizmied(){System.out.println("minimized");}
				protected void onWindowMaximized(){System.out.println("maximized");}
				protected void onWindowRestored(){System.out.println("restored");}
				protected void onWindowGainedFocus(){System.out.println("focused");}
				protected void onWindowLostFocus(){System.out.println("unfocused");}
			};
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
			wnd.getContentPanel().setLayout(new GridLayout(1, 2));
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

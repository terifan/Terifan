package org.terifan.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.terifan.ui.Utilities;
import org.terifan.util.log.Log;


public class ConsoleOutputWindow implements AutoCloseable
{
	private String mTitle;
	private JFrame mFrame;
	private JTextPane mServiesDisplayOutput;
	private HashMap<String,Output> mConsoles;
	private StyledDocument mDocument;
	private JScrollPane mServicesDisplayScrollPane;
	private JTabbedPane mTabbedPane;
	private boolean mCancelled;
	private final boolean mCancelOnClose;


	public enum Style
	{
		BLUE,
		GREEN,
		YELLOW,
		RED,
		CYAN,
		MAGENTA,
		BLACK,
		BOLD_BLUE,
		BOLD_GREEN,
		BOLD_YELLOW,
		BOLD_RED,
		BOLD_CYAN,
		BOLD_MAGENTA,
		BOLD_BLACK
	}


	public ConsoleOutputWindow(String aTitle, boolean aCancelOnClose)
	{
		Utilities.setSystemLookAndFeel();

		mTitle = aTitle;
		mCancelOnClose = aCancelOnClose;

		mConsoles = new HashMap<>();

		mServiesDisplayOutput = new JTextPane();
		mServiesDisplayOutput.setEditable(false);

		mDocument = mServiesDisplayOutput.getStyledDocument();

        setupWindow();

        customSetupWindow();

		if (SystemTray.isSupported())
		{
            createTrayIcon();
		}
        else
        {
            mFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent aEvent)
                {
					onClose();
                }
            });
        }

		new Timer(true).schedule(mMemoryUpdater, 1000, 1000);
	}


	public ConsoleOutputWindow setAlwaysOnTop(boolean aAlwaysOnTop)
	{
		mFrame.setAlwaysOnTop(aAlwaysOnTop);
		return this;
	}


	protected void customSetupWindow()
	{
	}


	private void setupWindow()
	{
		javax.swing.text.Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		javax.swing.text.Style regular = mDocument.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "Arial");

		javax.swing.text.Style s;

		s = mDocument.addStyle(Style.RED.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,0));

		s = mDocument.addStyle(Style.BOLD_RED.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.GREEN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,0));

		s = mDocument.addStyle(Style.BOLD_GREEN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.BLUE.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,200));

		s = mDocument.addStyle(Style.BOLD_BLUE.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.YELLOW.name(), regular);
		StyleConstants.setForeground(s, new Color(200,200,0));

		s = mDocument.addStyle(Style.BOLD_YELLOW.name(), regular);
		StyleConstants.setForeground(s, new Color(200,200,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.CYAN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,200));

		s = mDocument.addStyle(Style.BOLD_CYAN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.MAGENTA.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,200));

		s = mDocument.addStyle(Style.BOLD_MAGENTA.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.BLACK.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,0));

		s = mDocument.addStyle(Style.BOLD_BLACK.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle("~System", regular);
		StyleConstants.setForeground(s, new Color(0,0,0));
		StyleConstants.setBackground(s, new Color(255,255,0));

		mServicesDisplayScrollPane = new JScrollPane(mServiesDisplayOutput);
		mServicesDisplayScrollPane.setBorder(null);

		mTabbedPane = new JTabbedPane();
		mTabbedPane.addTab("Services", mServicesDisplayScrollPane);

		mFrame = new JFrame(mTitle);
		mFrame.add(mTabbedPane, BorderLayout.CENTER);
		mFrame.setSize(1024,600);
		mFrame.setLocationRelativeTo(null);
		mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}


	public ConsoleOutputWindow show()
	{
		mFrame.setExtendedState(JFrame.NORMAL);
		mFrame.setVisible(true);
		mFrame.toFront();

		SwingUtilities.invokeLater(()->{
			mFrame.invalidate();
			mFrame.validate();
			mFrame.repaint();
			mServiesDisplayOutput.requestFocusInWindow();
		});

		return this;
	}


	public ConsoleOutputWindow hide()
	{
		mFrame.setVisible(false);
		return this;
	}


	public JFrame getFrame()
	{
		return mFrame;
	}


	protected void onClose()
	{
		if (JOptionPane.showConfirmDialog(mFrame, "Confirm close?", mTitle, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			mCancelled = true;
			if (!mCancelOnClose)
			{
				close();
			}
		}
	}


    private void createTrayIcon()
    {
		try
		{
			Image image;

			try (InputStream in = getClass().getResourceAsStream("console_icon.png"))
			{
				image = ImageIO.read(in);
			}

			MenuItem openItem = new MenuItem("Open status window");
			openItem.addActionListener(e->show());

			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(e->onClose());

			PopupMenu popup = new PopupMenu();
			popup.add(openItem);
			popup.addSeparator();
			popup.add(exitItem);

			TrayIcon trayIcon = new TrayIcon(image, mTitle, popup);
			trayIcon.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent aEvent)
				{
					if (aEvent.getClickCount() > 0 && SwingUtilities.isLeftMouseButton(aEvent))
					{
						show();
					}
				}
			});

			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);

			mFrame.addWindowListener(mWindowListener);
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}
    }


	private WindowListener mWindowListener = new WindowAdapter()
	{
		@Override
		public void windowIconified(WindowEvent aEvent)
		{
			hide();
		}
		@Override
		public void windowClosing(WindowEvent aEvent)
		{
			onClose();
		}
	};


	private TimerTask mMemoryUpdater = new TimerTask()
	{
		@Override
		public void run()
		{
			try
			{
				if (mFrame.isVisible())
				{
					Runtime r = Runtime.getRuntime();
					long m = r.maxMemory();
					long t = r.totalMemory();
					long f = r.freeMemory();
					mFrame.setTitle(mTitle + " Memory: " + (t-f)/1024/1024 + "/" + m/1024/1024);
				}
			}
			catch (Exception e)
			{
			}
		}
	};


	public JTabbedPane getTabbedPane()
	{
		return mTabbedPane;
	}


	public StyledDocument getDocument()
	{
		return mDocument;
	}


	public boolean isCancelled()
	{
		return mCancelled;
	}


	public void appendNew(String aOutput, Object aText)
	{
		Output output = mConsoles.get(aOutput);
		if (output == null)
		{
			synchronized (ConsoleOutputWindow.class)
			{
				output = mConsoles.get(aOutput);
				if (output == null)
				{
					output = new Output();
					mConsoles.put(aOutput, output);

					mTabbedPane.addTab(aOutput, output);
					mTabbedPane.validate();
				}
			}
		}

		output.append(aText == null ? "<null>" : aText.toString());
	}


	public void append(Style aStyle, Object aText)
	{
		append(aStyle.name(), aText);
	}


	public void append(String aStyle, Object aText)
	{
		try
		{
			String text;
			javax.swing.text.Style style;

			if (aText instanceof Throwable)
			{
				text = "\n" + Calendar.now() + "\t" + Log.getStackTraceString((Throwable)aText);
				style = mDocument.getStyle("Exception");
			}
			else
			{
				text = "\n" + Calendar.now() + "\t" + aText;
				style = mDocument.getStyle(aStyle);
			}

			synchronized (ConsoleOutputWindow.class)
			{
				JScrollBar scrollBar = mServicesDisplayScrollPane.getVerticalScrollBar();
				boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 20 >= scrollBar.getMaximum();
				int len = mDocument.getLength();
				if (len > 100000)
				{
					mDocument.remove(0, len - 100000);
					len = mDocument.getLength();
				}
				mDocument.insertString(len, text, style);
				if (scroll)
				{
					mServiesDisplayOutput.setCaretPosition(len + 1);
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.out);
		}
	}


	/**
	 * Calls the disposeOnClose method. Use this in try-resource statements.
	 */
	@Override
	public void close()
	{
		mFrame.dispose();
		System.exit(0);
	}


	private static class Output extends JScrollPane
	{
		private JTextArea mTextArea;


		public Output()
		{
			mTextArea = new JTextArea();
			mTextArea.setEditable(false);
			mTextArea.setTabSize(4);
			mTextArea.setFont(new Font("courier new", Font.PLAIN, 11));

			super.setViewportView(mTextArea);
			super.setBorder(null);
		}


		public synchronized void append(String aText)
		{
			try
			{
				JScrollBar scrollBar = getVerticalScrollBar();
				boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 20 >= scrollBar.getMaximum();

				if (mTextArea.getLineCount() > 10000)
				{
					mTextArea.replaceRange("", 0, mTextArea.getLineEndOffset(0));
				}

				mTextArea.append(aText + "\n");
				if (scroll)
				{
					mTextArea.setCaretPosition(mTextArea.getText().length());
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.out);
			}
		}
	}


	public static void main(String ... args)
	{
		try
		{
			try (ConsoleOutputWindow cow = new ConsoleOutputWindow("Test", true).show())
			{
				for (int j = 0, n = 0; j < 1000; j++)
				{
					for (int i = 0; i < 10; i++, n++)
					{
						cow.append(Style.RED, "line " + n);
						cow.append(Style.YELLOW, "line " + n);
						cow.appendNew("X", "line " + n);
						if (j > 1)
						{
							cow.appendNew("Network", "line " + n);
						}
						if (j > 5)
						{
							cow.appendNew("Error", "line " + n);
						}
						Thread.sleep(50);
					}
					if (cow.isCancelled())
					{
						Log.out.println("ok");
						break;
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

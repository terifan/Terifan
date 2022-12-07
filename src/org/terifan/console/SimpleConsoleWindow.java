package org.terifan.console;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;


public class SimpleConsoleWindow implements AutoCloseable
{
	public final static TextStyle RED = new TextStyle("RED", new Color(180, 0, 0), Color.WHITE, false);
	public final static TextStyle GREEN = new TextStyle("GREEN", new Color(0, 180, 0), Color.WHITE, false);
	public final static TextStyle BLUE = new TextStyle("BLUE", new Color(0, 0, 180), Color.WHITE, false);
	public final static TextStyle YELLOW = new TextStyle("YELLOW", new Color(180, 180, 0), Color.WHITE, false);
	public final static TextStyle MAGENTA = new TextStyle("MAGENTA", new Color(180, 0, 180), Color.WHITE, false);
	public final static TextStyle CYAN = new TextStyle("CYAN", new Color(0, 180, 180), Color.WHITE, false);
	public final static TextStyle GRAY = new TextStyle("GRAY", new Color(160, 160, 160), Color.WHITE, false);
	public final static TextStyle BLACK = new TextStyle("BLACK", Color.BLACK, Color.WHITE, false);

	private HashMap<String, TextPane> mTabs;
	private boolean mCancelled;
	private boolean mMinimizeToTray;
	private int mTextLimit;
	private boolean mAllowForceShutdown;

	private JFrame mFrame;
	private JTabbedPane mTabbedPane;
	private JPanel mContentPanel;
	private TrayIcon mTrayIcon;


	public SimpleConsoleWindow()
	{
		this("");
	}


	public SimpleConsoleWindow(String aTitle)
	{
		mTabs = new HashMap<>();
		mTabbedPane = new JTabbedPane();
		mContentPanel = new JPanel(new BorderLayout());
		mContentPanel.add(mTabbedPane, BorderLayout.CENTER);

		show();
		setTextLimit(100_000);
		setTitle(aTitle);
	}


	int getTextLimit()
	{
		return mTextLimit;
	}


	public SimpleConsoleWindow setTextLimit(int aTextLimit)
	{
		mTextLimit = aTextLimit;
		return this;
	}


	public SimpleConsoleWindow setMinimizeToTrayEnabled(boolean aMinimizeToTray)
	{
		mMinimizeToTray = aMinimizeToTray;
		return this;
	}


	public SimpleConsoleWindow setTitle(String aTitle)
	{
		mFrame.setTitle(aTitle);
		return this;
	}


	public SimpleConsoleWindow setAlwaysOnTop(boolean aAlwaysOnTop)
	{
		mFrame.setAlwaysOnTop(aAlwaysOnTop);
		return this;
	}


	public SimpleConsoleWindow setAllowForceShutdown(boolean aAllowForceShutdown)
	{
		mAllowForceShutdown = aAllowForceShutdown;
		return this;
	}


	public boolean isCancelled()
	{
		return mCancelled;
	}


	public void show()
	{
		if (mFrame == null)
		{
			mFrame = new JFrame();
			mFrame.add(mContentPanel);
			mFrame.setSize(1024, 600);
			mFrame.setLocationRelativeTo(null);
			mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			if (SystemTray.isSupported())
			{
				createTrayIcon();
			}
			else
			{
				mFrame.addWindowListener(mWindowListener);
			}
		}

		mFrame.setExtendedState(JFrame.NORMAL);
		mFrame.setVisible(true);
		mFrame.toFront();

		SwingUtilities.invokeLater(() ->
		{
			mFrame.invalidate();
			mFrame.validate();
			mFrame.repaint();
		});
	}


	public void hide()
	{
		mFrame.setVisible(false);
	}


	protected void onClose()
	{
		if (mCancelled && mAllowForceShutdown)
		{
			if (JOptionPane.showConfirmDialog(mFrame, "Waiting for application to stop. Force shutdown?", mFrame.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}
			return;
		}

		if (!mCancelled && JOptionPane.showConfirmDialog(mFrame, "Confirm close?", mFrame.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			mCancelled = true;
		}
	}


	private void createTrayIcon()
	{
		try
		{
			Image image;

			try ( InputStream in = getClass().getResourceAsStream("console_icon.png"))
			{
				image = ImageIO.read(in);
			}
			catch (Exception e)
			{
				image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
			}

			MenuItem openItem = new MenuItem("Open status window");
			openItem.addActionListener(e -> show());

			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(e -> onClose());

			PopupMenu popup = new PopupMenu();
			popup.add(openItem);
			popup.addSeparator();
			popup.add(exitItem);

			mTrayIcon = new TrayIcon(image, mFrame.getTitle(), popup);
			mTrayIcon.addMouseListener(new MouseAdapter()
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
			tray.add(mTrayIcon);

			mFrame.addWindowListener(mWindowListener);
		}
		catch (Exception | Error e)
		{
			e.printStackTrace(System.out);
		}
	}

	private WindowListener mWindowListener = new WindowAdapter()
	{
		@Override
		public void windowIconified(WindowEvent aEvent)
		{
			if (mMinimizeToTray && hasTrayIcon())
			{
				hide();
			}
		}


		@Override
		public void windowClosing(WindowEvent aEvent)
		{
			onClose();
		}
	};


	private boolean hasTrayIcon()
	{
		return mTrayIcon != null;
	}


	public void append(String aTab, TextStyle aStyle, Object aText, Object... aParameters)
	{
		getOrCreateTab(aTab).append(this, aStyle, aText, aParameters);
	}


	public void append(String[] aTabs, TextStyle aStyle, Object aText, Object... aParameters)
	{
		for (String tab : aTabs)
		{
			getOrCreateTab(tab).append(this, aStyle, aText, aParameters);
		}
	}


	private synchronized TextPane getOrCreateTab(String aTab)
	{
		return mTabs.computeIfAbsent(aTab, e ->
		{
			TextPane output = new TextPane();

			mTabbedPane.addTab(aTab, output.getScrollPane());
			mTabbedPane.validate();

			return output;
		});
	}


	public SimpleConsoleWindow redirectSystemOut(String aTab, TextStyle aStyle)
	{
		return redirectSystemOut(new String[]{aTab}, aStyle);
	}


	public SimpleConsoleWindow redirectSystemOut(String[] aTabs, TextStyle aStyle)
	{
		System.setOut(new PrintStream(new ConsoleOutputStream(aTabs, aStyle)));
		return this;
	}


	public SimpleConsoleWindow redirectSystemErr(String aTab, TextStyle aStyle)
	{
		return redirectSystemErr(new String[]{aTab}, aStyle);
	}


	public SimpleConsoleWindow redirectSystemErr(String[] aTabs, TextStyle aStyle)
	{
		System.setErr(new PrintStream(new ConsoleOutputStream(aTabs, aStyle)));
		return this;
	}


	private class ConsoleOutputStream extends OutputStream
	{
		private ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
		private String[] mTabs;
		private TextStyle mStyle;


		public ConsoleOutputStream(String[] aTabs, TextStyle aStyle)
		{
			mTabs = aTabs;
			mStyle = aStyle;
		}


		@Override
		public void write(int aByte)
		{
			if (aByte == '\n')
			{
				append(mTabs, mStyle, mBuffer.toString());
				mBuffer.reset();
			}
			else if (aByte != '\r')
			{
				mBuffer.write(aByte);
			}
		}
	}


	/**
	 * Calls the disposeOnClose method. Use this in try-resource statements.
	 */
	@Override
	public void close()
	{
		if (hasTrayIcon())
		{
			try
			{
				SystemTray.getSystemTray().remove(mTrayIcon);
			}
			catch (Exception e)
			{
				e.printStackTrace(System.out);
			}
			mTrayIcon = null;
		}

		mFrame.dispose();
	}
}

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
import java.io.InputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.terifan.util.log.Log;


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
	private boolean mDisposeOnClose;
	private boolean mShutdownOnClose;
	private boolean mMinimizeToTray;
	private int mTextLimit;
	private Style mDefaultFont;

	private JFrame mFrame;
	private JTabbedPane mTabbedPane;
	private JPanel mContentPanel;
	private TrayIcon mTrayIcon;


	public SimpleConsoleWindow()
	{
		mTabs = new HashMap<>();

		mDefaultFont = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(mDefaultFont, "consolas");

		setTextLimit(100_000);

		mTabbedPane = new JTabbedPane();
		mContentPanel = new JPanel(new BorderLayout());
		mContentPanel.add(mTabbedPane, BorderLayout.CENTER);

		show();
	}


	public JPanel getContentPanel()
	{
		return mContentPanel;
	}



	public int getTextLimit()
	{
		return mTextLimit;
	}


	public SimpleConsoleWindow setTextLimit(int aTextLimit)
	{
		mTextLimit = aTextLimit;
		return this;
	}


	public boolean isMinimizeToTrayEnabled()
	{
		return mMinimizeToTray;
	}


	public SimpleConsoleWindow setMinimizeToTrayEnabled(boolean aMinimizeToTray)
	{
		mMinimizeToTray = aMinimizeToTray;
		return this;
	}


	public SimpleConsoleWindow setDisposeOnClose(boolean aDisposeOnClose)
	{
		mDisposeOnClose = aDisposeOnClose;
		return this;
	}


	public SimpleConsoleWindow setShutdownOnClose(boolean aShutdownOnClose)
	{
		mShutdownOnClose = aShutdownOnClose;
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


	public JTabbedPane getTabbedPane()
	{
		return mTabbedPane;
	}


	public boolean isCancelled()
	{
		return mCancelled;
	}


	public JFrame getFrame()
	{
		return mFrame;
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


	protected Style getDefaultFont()
	{
		return mDefaultFont;
	}


	protected void onClose()
	{
		if (JOptionPane.showConfirmDialog(mFrame, "Confirm close?", mFrame.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			mCancelled = true;

			if (mDisposeOnClose)
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
			e.printStackTrace(Log.out);
		}
	}

	private WindowListener mWindowListener = new WindowAdapter()
	{
		@Override
		public void windowIconified(WindowEvent aEvent)
		{
			if (mMinimizeToTray)
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


	public void append(String aTab, TextStyle aStyle, Object aText)
	{
		TextPane textPane = addTabIfAbsent(aTab);
		textPane.append(this, aStyle, aText);
	}


	private TextPane addTabIfAbsent(String aTab)
	{
		return mTabs.computeIfAbsent(aTab, e->
		{
			TextPane output = new TextPane();

			synchronized (this)
			{
				mTabbedPane.addTab(aTab, output.getScrollPane());
				mTabbedPane.validate();
			}

			return output;
		});
	}


	/**
	 * Calls the disposeOnClose method. Use this in try-resource statements.
	 */
	@Override
	public void close()
	{
		if (mTrayIcon != null)
		{
			SystemTray.getSystemTray().remove(mTrayIcon);
		}

		mFrame.dispose();

		if (mShutdownOnClose)
		{
			System.exit(0);
		}
	}
}

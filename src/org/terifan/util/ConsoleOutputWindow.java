package org.terifan.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.terifan.ui.Utilities;
import org.terifan.util.log.Log;


public class ConsoleOutputWindow implements AutoCloseable
{
	private static Style mDefaultFont;

	private JFrame mFrame;
	private HashMap<String, Output> mConsoles;
	private HashMap<String, TextStyle> mStyles;
	private JTabbedPane mTabbedPane;
	private boolean mCancelled;
	private boolean mDisposeOnClose;
	private boolean mMinimizeToTray;
	private int mTextLimit;


	public ConsoleOutputWindow()
	{
		mConsoles = new HashMap<>();
		mStyles = new HashMap<>();

		mDefaultFont = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(mDefaultFont, "Arial");
		
		setTextLimit(100_000);

		mTabbedPane = new JTabbedPane();

		mFrame = new JFrame();
		mFrame.add(mTabbedPane, BorderLayout.CENTER);
		mFrame.setSize(1024, 600);
		mFrame.setLocationRelativeTo(null);
		mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addStyle(new TextStyle("RED").setForeground(new Color(180, 0, 0)));
		addStyle(new TextStyle("GREEN").setForeground(new Color(0, 180, 0)));
		addStyle(new TextStyle("BLUE").setForeground(new Color(0, 0, 180)));
		addStyle(new TextStyle("YELLOW").setForeground(new Color(180, 180, 0)));
		addStyle(new TextStyle("BLACK").setForeground(Color.BLACK));

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
	}


	public int getTextLimit()
	{
		return mTextLimit;
	}


	public ConsoleOutputWindow setTextLimit(int aTextLimit)
	{
		mTextLimit = aTextLimit;
		return this;
	}


	public boolean isMinimizeToTray()
	{
		return mMinimizeToTray;
	}


	public ConsoleOutputWindow setMinimizeToTray(boolean aMinimizeToTray)
	{
		mMinimizeToTray = aMinimizeToTray;
		return this;
	}


	public ConsoleOutputWindow setDisposeOnClose(boolean aDisposeOnClose)
	{
		mDisposeOnClose = aDisposeOnClose;
		return this;
	}


	public ConsoleOutputWindow setTitle(String aTitle)
	{
		mFrame.setTitle(aTitle);
		return this;
	}


	public ConsoleOutputWindow setAlwaysOnTop(boolean aAlwaysOnTop)
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


	public ConsoleOutputWindow show()
	{
		mFrame.setExtendedState(JFrame.NORMAL);
		mFrame.setVisible(true);
		mFrame.toFront();

		SwingUtilities.invokeLater(() ->
		{
			mFrame.invalidate();
			mFrame.validate();
			mFrame.repaint();
		});

		return this;
	}


	public ConsoleOutputWindow hide()
	{
		mFrame.setVisible(false);
		return this;
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

			TrayIcon trayIcon = new TrayIcon(image, mFrame.getTitle(), popup);
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


	public void append(String aTab, String aStyle, Object aText)
	{
		addTab(aTab);

		Output output = mConsoles.get(aTab);

		output.append(aStyle, aText);
	}


	public ConsoleOutputWindow addTab(String aTab)
	{
		mConsoles.computeIfAbsent(aTab, e->
		{
			Output output = new Output(this);

			synchronized (this)
			{
				for (TextStyle style : mStyles.values())
				{
					style.update(output.mDocument);
				}

				mTabbedPane.addTab(aTab, output.mScrollPane);
				mTabbedPane.validate();
			}

			return output;
		});

		return this;
	}


	public Output getConsole(String aTabName)
	{
		return mConsoles.get(aTabName);
	}


	public void addStyle(TextStyle aStyle)
	{
		mStyles.put(aStyle.mName, aStyle);

		for (Output output : mConsoles.values())
		{
			aStyle.update(output.mDocument);
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


	public static class TextStyle
	{
		String mName;
		Color mBackground;
		Color mForeground;
		boolean mBold;


		public TextStyle(String aName)
		{
			this(aName, Color.BLACK, Color.WHITE, false);
		}


		public TextStyle(String aName, Color aForeground, Color aBackground, boolean aBold)
		{
			mName = aName;
			mBackground = aBackground;
			mForeground = aForeground;
			mBold = aBold;
		}


		public Color getBackground()
		{
			return mBackground;
		}


		public TextStyle setBackground(Color aBackground)
		{
			mBackground = aBackground;
			return this;
		}


		public Color getForeground()
		{
			return mForeground;
		}


		public TextStyle setForeground(Color aForeground)
		{
			mForeground = aForeground;
			return this;
		}


		public boolean isBold()
		{
			return mBold;
		}


		public TextStyle setBold(boolean aBold)
		{
			mBold = aBold;
			return this;
		}


		private void update(StyledDocument aDocument)
		{
			Style regular = aDocument.addStyle("regular", mDefaultFont);

			Style s = aDocument.addStyle(mName, regular);
			StyleConstants.setForeground(s, mForeground);
			StyleConstants.setBackground(s, mBackground);
			StyleConstants.setBold(s, mBold);
		}
	}


	public static class Output
	{
		private final ConsoleOutputWindow mWindow;
		private JTextPane mTextArea;
		private StyledDocument mDocument;
		private JScrollPane mScrollPane;


		Output(ConsoleOutputWindow aWindow)
		{
			mWindow = aWindow;

			mTextArea = new JTextPane();
			mTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

			mDocument = mTextArea.getStyledDocument();
			
			mScrollPane = new JScrollPane();
			mScrollPane.setViewportView(mTextArea);
			mScrollPane.setBorder(null);

			setEditable(false);
		}


		public JTextPane getTextArea()
		{
			return mTextArea;
		}
		
		
		public boolean isEditable()
		{
			return mTextArea.isEditable();
		}

		
		public Output setEditable(boolean aEditable)
		{
			mTextArea.setEditable(aEditable);
			return this;
		}


		public synchronized void append(String aStyle, Object aText)
		{
			try
			{
				String text;

				if (aText instanceof Throwable)
				{
					text = "\n" + Calendar.now() + "\t" + Log.getStackTraceString((Throwable)aText);
				}
				else
				{
					text = "\n" + Calendar.now() + "\t" + aText;
				}

				synchronized (mWindow)
				{
					JScrollBar scrollBar = mScrollPane.getVerticalScrollBar();

					boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 20 >= scrollBar.getMaximum();

					int len = mDocument.getLength();

					if (len > mWindow.getTextLimit())
					{
						mDocument.remove(0, len - mWindow.getTextLimit());
						len = mDocument.getLength();
					}

					mDocument.insertString(len, text, mDocument.getStyle(aStyle));

					if (scroll)
					{
						mTextArea.setCaretPosition(len + 1);
					}
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.out);
			}
		}
	}


	public static void main(String... args)
	{
		try
		{
			Utilities.setSystemLookAndFeel();

			try (ConsoleOutputWindow cow = new ConsoleOutputWindow().show())
			{
				cow.addTab("dummy");
				
				for (int j = 0, n = 0; j < 1000; j++)
				{
					for (int i = 0; i < 10; i++, n++)
					{
						String text = "line " + n;
						String style = i == 9 ? "RED" : i > 5 ? "GREEN" : "BLACK";

						cow.append("All", style, text);
						cow.append("All", style, text);
						if (i == 9)
						{
							cow.append("Error", style, text);
						}
						else if (i > 5)
						{
							cow.append("Network", style, text);
						}
						else
						{
							cow.append("General", style, text);
						}
						Thread.sleep(50);
					}

					if (cow.isCancelled())
					{
						Log.out.println("was cancelled");
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

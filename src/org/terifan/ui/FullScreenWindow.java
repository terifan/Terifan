package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class FullScreenWindow implements MouseListener, MouseMotionListener
{
	public static final Color BORDER_COLOR = new Color(38, 38, 38);

	private static BufferedImage IMAGE_CLOSE;
	private static BufferedImage IMAGE_CLOSE_ARMED;
	private static BufferedImage IMAGE_MAXIMIZED;
	private static BufferedImage IMAGE_MAXIMIZED_ARMED;
	private static BufferedImage IMAGE_MINIMIZED;
	private static BufferedImage IMAGE_MINIMIZED_ARMED;
	private static BufferedImage IMAGE_RESTORE;
	private static BufferedImage IMAGE_RESTORE_ARMED;

	private boolean mMousePressed;
	private int mMouseX;
	private int mMouseY;

	protected JFrame mFrame;
	protected int mArmedButton;
	protected Point mFramePosition;
	protected JPanel mMainPanel;
	protected JComponent mContentPane;
	protected String mWindowTitle;
	protected Dimension mInitialSize;
	protected Rectangle[] mButtonRects;
	protected int mBorderWidth;
	protected int mTitleBarHeight;
	protected int mLastKnownWidth;


	public FullScreenWindow()
	{
		loadResources();

		mInitialSize = new Dimension(1024, 768);
		mWindowTitle = "New window";

		mArmedButton = -1;
		mBorderWidth = 3;
		mTitleBarHeight = 32;
		mLastKnownWidth = 0;

		mMainPanel = new JPanel(new BorderLayout());
		mMainPanel.add(mWindowTitleBar, BorderLayout.NORTH);

		mWindowTitleBar.addMouseListener(this);
		mWindowTitleBar.addMouseMotionListener(this);
		mWindowTitleBar.setForeground(new Color(255, 255, 255));
		mWindowTitleBar.setBackground(new Color(18, 18, 18));
		mWindowTitleBar.setFont(mWindowTitleBar.getFont().deriveFont(17f));

		mFrame = new JFrame(mWindowTitle);
		mFrame.add(mMainPanel);
		mFrame.setSize(mInitialSize);
		mFrame.addComponentListener(mComponentAdapter);
		mFrame.addWindowListener(mWindowClosingListener);
//		mFrame.addWindowStateListener(mWindowStateListener);
		mFrame.setUndecorated(true);
//		frame.setExtendedState(aState);

//		if (aState == JFrame.NORMAL)
		{
			mMainPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, mBorderWidth));
		}
//		else
//		{
//			mMainPanel.setBorder(null);
//		}
	}


	protected synchronized void loadResources()
	{
		IMAGE_CLOSE = Utilities.readImageResource(FullScreenWindow.class, "window_button_close.png");
		IMAGE_CLOSE_ARMED = Utilities.readImageResource(FullScreenWindow.class, "window_button_close_armed.png");
		IMAGE_MAXIMIZED = Utilities.readImageResource(FullScreenWindow.class, "window_button_maximize.png");
		IMAGE_MAXIMIZED_ARMED = Utilities.readImageResource(FullScreenWindow.class, "window_button_maximized_armed.png");
		IMAGE_MINIMIZED = Utilities.readImageResource(FullScreenWindow.class, "window_button_minimize.png");
		IMAGE_MINIMIZED_ARMED = Utilities.readImageResource(FullScreenWindow.class, "window_button_minimize_armed.png");
		IMAGE_RESTORE = Utilities.readImageResource(FullScreenWindow.class, "window_button_restore.png");
		IMAGE_RESTORE_ARMED = Utilities.readImageResource(FullScreenWindow.class, "window_button_restore_armed.png");
	}


	public void updateSize()
	{
		int w = mMainPanel.getWidth();
		int h = IMAGE_CLOSE.getHeight();

		mButtonRects = new Rectangle[3];

		int w0 = (int)Math.ceil(IMAGE_CLOSE.getWidth() * mTitleBarHeight / (double)h);
		int w1 = (int)Math.ceil(IMAGE_MAXIMIZED.getWidth() * mTitleBarHeight / (double)h);
		int w2 = (int)Math.ceil(IMAGE_MINIMIZED.getWidth() * mTitleBarHeight / (double)h);

		mButtonRects[0] = new Rectangle(w - w0 - w1 - w2, 0, w2, mTitleBarHeight);
		mButtonRects[1] = new Rectangle(w - w0 - w1, 0, w1, mTitleBarHeight);
		mButtonRects[2] = new Rectangle(w - w0, 0, w0, mTitleBarHeight);
	}


	public JPanel getMainPanel()
	{
		return mMainPanel;
	}


//	protected void show(JComponent aContentPanel, boolean aMaximized)
//	{
//		mContentPane = aContentPanel;
//
//		mMainPanel.add(mContentPane, BorderLayout.CENTER);
//
//		setArmedButton(aMaximized ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
//	}
	void closeWindow()
	{
		SwingUtilities.invokeLater(() -> mFrame.dispose());
	}


	public void revalidate()
	{
		mMainPanel.invalidate();
		mFrame.revalidate();
	}

	private WindowAdapter mWindowClosingListener = new WindowAdapter()
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			closeWindow();
		}
	};

//	private WindowAdapter mWindowStateListener = new WindowAdapter()
//	{
//		@Override
//		public void windowStateChanged(WindowEvent aEvent)
//		{
//			if (aEvent.getNewState() == JFrame.MAXIMIZED_BOTH || (aEvent.getOldState() == JFrame.MAXIMIZED_BOTH && aEvent.getNewState() == JFrame.NORMAL))
//			{
//				setArmedButton(aEvent.getNewState());
//			}
//		}
//	};
	private ComponentAdapter mComponentAdapter = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent aEvent)
		{
			revalidate();
			updateSize();
		}


		@Override
		public void componentMoved(ComponentEvent aEvent)
		{
			if (mFrame.isVisible() && (mFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0)
			{
				mLastKnownWidth = mFrame.getWidth();
				mFramePosition = mFrame.getLocationOnScreen();
			}
		}
	};


	protected void onClose()
	{
	}


	boolean isMaximized()
	{
		return mFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
	}


	void setLocation(int aX, int aY)
	{
		mFrame.setLocation(aX, aY);
	}


	@Override
	public void mouseClicked(MouseEvent aEvent)
	{
	}


	@Override
	public void mousePressed(MouseEvent aEvent)
	{
		mFrame.requestFocus();

		update(aEvent);

		if (aEvent.getButton() == MouseEvent.BUTTON1)
		{
			mMousePressed = true;
			mMouseX = aEvent.getX();
			mMouseY = aEvent.getY();
		}
	}


	@Override
	public void mouseExited(MouseEvent aEvent)
	{
		if (mArmedButton != -1)
		{
			mArmedButton = -1;
			mMainPanel.repaint();
		}
	}


	@Override
	public void mouseEntered(MouseEvent aEvent)
	{
		update(aEvent);
	}


	@Override
	public void mouseReleased(MouseEvent aEvent)
	{
		mMousePressed = false;

		update(aEvent);

		int state = aEvent.getClickCount() > 1 ? 1 : mArmedButton;

		mMousePressed = false;
		mArmedButton = -1;

		switch (state)
		{
			case 0:
				mFrame.setExtendedState(JFrame.ICONIFIED);
				break;
			case 1:
				if (mFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
				{
					mFrame.setExtendedState(JFrame.NORMAL);
				}
				else
				{
					mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
				break;
			case 2:
				mFrame.dispose();
				break;
			default:
				break;
		}
	}


	@Override
	public void mouseDragged(MouseEvent aEvent)
	{
		update(aEvent);

		if (mFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
		{
			int x = mLastKnownWidth / 2;

			mFrame.setExtendedState(JFrame.NORMAL);
			mFrame.setLocation(aEvent.getXOnScreen() - x, aEvent.getYOnScreen());
			
			mMouseX = x;
		}
		else
		{
			mFrame.setLocation(aEvent.getXOnScreen() - mMouseX, aEvent.getYOnScreen() - mMouseY);
		}
	}


	@Override
	public void mouseMoved(MouseEvent aEvent)
	{
		update(aEvent);
	}


	private void update(MouseEvent aEvent)
	{
		int x = aEvent.getX();
		int y = aEvent.getY();

		int i = -1;
		int newState = -1;
		for (Rectangle r : mButtonRects)
		{
			i++;
			if (r.contains(x, y))
			{
				newState = i;
				break;
			}
		}

		if (mArmedButton != newState)
		{
			mArmedButton = newState;
			mMainPanel.repaint();
		}
	}

	private JComponent mWindowTitleBar = new JComponent()
	{
		@Override
		protected void paintComponent(Graphics aGraphics)
		{
			aGraphics.setColor(getBackground());
			aGraphics.fillRect(0, 0, getWidth(), getHeight());

			new TextBox(mFrame.getTitle()).setBounds(0, 0, mButtonRects[0].x, getHeight()).setAnchor(Anchor.WEST).setForeground(getForeground()).setMargins(0, 4, 0, 4).setFont(mWindowTitleBar.getFont()).render(aGraphics);

			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			paintImage(aGraphics, mButtonRects[0], mArmedButton == 0 ? IMAGE_MINIMIZED_ARMED : IMAGE_MINIMIZED);
			paintImage(aGraphics, mButtonRects[1], mFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH ? mArmedButton == 1 ? IMAGE_RESTORE_ARMED : IMAGE_RESTORE : mArmedButton == 1 ? IMAGE_MAXIMIZED_ARMED : IMAGE_MAXIMIZED);
			paintImage(aGraphics, mButtonRects[2], mArmedButton == 2 ? IMAGE_CLOSE_ARMED : IMAGE_CLOSE);
		}


		private void paintImage(Graphics aGraphics, Rectangle aRect, BufferedImage aImage)
		{
			aGraphics.drawImage(aImage, aRect.x, aRect.y, aRect.width, aRect.height, null);
		}


		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(1, mTitleBarHeight);
		}
	};


	public FullScreenWindow setVisible(boolean aState)
	{
		mFrame.setVisible(aState);
		return this;
	}


	public static void main(String... args)
	{
		try
		{
			FullScreenWindow wnd = new FullScreenWindow();
			wnd.getMainPanel().add(new JLabel("test"));
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class FullScreenWindow
{
	protected Color mBorderColor = new Color(38, 38, 38);
	protected Color mButtonArmedBackground = new Color(64, 64, 64);
	protected Color mCloseButtonArmedBackground = new Color(232, 17, 35);
	protected Color mButtonSymbolColor = new Color(255, 255, 255);
	protected Color mButtonSymbolShadowColor = new Color(255, 255, 255, 64);

	private boolean mMousePressed;
	private int mMouseX;
	private int mMouseY;

	protected JFrame mFrame;
	protected int mArmedButton;
	protected Point mFramePosition;
	protected JPanel mContentPanel;
	protected JComponent mContentPane;
	protected JComponent mBorderPanel;
	protected String mWindowTitle;
	protected Dimension mInitialSize;
	protected Rectangle[] mButtonRects;
	protected int mBorderWidth;
	protected int mTitleBarHeight;
	protected int mTitleBarButtonWidth;
	protected int mLastKnownWidth;
	protected boolean mMouseDragged;


	public FullScreenWindow()
	{
		mInitialSize = new Dimension(1024, 768);
		mWindowTitle = "New window";

		mArmedButton = -1;
		mBorderWidth = 30;
		mTitleBarHeight = 32;
		mLastKnownWidth = 0;
		mTitleBarHeight = 43;
		mTitleBarButtonWidth = 69;

		mContentPanel = new JPanel(new BorderLayout());
		mContentPanel.add(mTitleBar, BorderLayout.NORTH);

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.add(mContentPanel, BorderLayout.CENTER);

		mTitleBar.addMouseListener(mTitleBarMouseListener);
		mTitleBar.addMouseMotionListener(mTitleBarMouseListener);
		mTitleBar.setForeground(new Color(255, 255, 255));
		mTitleBar.setBackground(new Color(18, 18, 18));
		mTitleBar.setFont(mTitleBar.getFont().deriveFont(17f));

		mFrame = new JFrame(mWindowTitle);
		mFrame.add(mBorderPanel);
		mFrame.setSize(mInitialSize);
		mFrame.addComponentListener(mComponentAdapter);
		mFrame.addWindowListener(mWindowClosingListener);
		mFrame.setLocationRelativeTo(null);
		mFrame.setUndecorated(true);
		mFrame.addWindowStateListener(mWindowStateListener);
//		mFrame.setExtendedState(aState);

		mBorderPanel.addMouseListener(mBorderMouseListener);
		mBorderPanel.addMouseMotionListener(mBorderMouseListener);
		
		mContentPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent aEvent)
			{
				mFrame.setCursor(Cursor.getDefaultCursor());
			}
		});

		updateBorder(JFrame.NORMAL);
	}


	protected MouseAdapter mBorderMouseListener = new MouseAdapter()
	{
		private Point mClickPoint;
		private Rectangle mStartBounds;
		private Integer mCursor;


		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mStartBounds = mFrame.getBounds();
			mClickPoint = aEvent.getPoint();
			mCursor = getCursor(aEvent.getPoint());
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			resizeBox(aEvent.getPoint());
		}


		@Override
		public void mouseReleased(MouseEvent aE)
		{
			mCursor = null;
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			mFrame.setCursor(Cursor.getPredefinedCursor(mCursor != null ? mCursor : getCursor(aEvent.getPoint())));
		}


		@Override
		public void mouseEntered(MouseEvent aEvent)
		{
			mStartBounds = mFrame.getBounds();
			mFrame.setCursor(Cursor.getPredefinedCursor(mCursor != null ? mCursor : getCursor(aEvent.getPoint())));
		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			mFrame.setCursor(Cursor.getDefaultCursor());
		}
	
		
		private void resizeBox(Point aPoint)
		{
//			Rectangle mStartBounds = mStartBounds;
			Rectangle b = mFrame.getBounds();

//			switch (mCursor)
//			{
//				case Cursor.W_RESIZE_CURSOR:
//				case Cursor.NW_RESIZE_CURSOR:
//				case Cursor.SW_RESIZE_CURSOR:
//					int o = b.x;
//					b.x = Math.min(mStartBounds.x - mClickPoint.x + aPoint.x, mStartBounds.x + mStartBounds.width/* - aNode.getMinSize().width*/);
//					b.width += o - b.x;
//					break;
//			}

//			switch (mCursor)
//			{
//				case Cursor.N_RESIZE_CURSOR:
//				case Cursor.NW_RESIZE_CURSOR:
//				case Cursor.NE_RESIZE_CURSOR:
//					int o = b.y;
//					b.y = Math.min(mStartBounds.y - mClickPoint.y + aPoint.y, mStartBounds.y + mStartBounds.height/* - aNode.getMinSize().height*/);
//					b.height += o - b.y;
//					break;
//			}
//
//			switch (mCursor)
//			{
//				case Cursor.SW_RESIZE_CURSOR:
//				case Cursor.S_RESIZE_CURSOR:
//				case Cursor.SE_RESIZE_CURSOR:
//					b.height = mStartBounds.height - mClickPoint.y + aPoint.y;
//					break;
//			}
//
			switch (mCursor)
			{
				case Cursor.E_RESIZE_CURSOR:
				case Cursor.SE_RESIZE_CURSOR:
				case Cursor.NE_RESIZE_CURSOR:
					b.width = mStartBounds.width - mClickPoint.x + aPoint.x;
					break;
			}

//			b.width = Math.min(aNode.getMaxSize().width, Math.max(aNode.getMinSize().width, b.width));
//			b.height = Math.min(aNode.getMaxSize().height, Math.max(aNode.getMinSize().height, b.height));

			mFrame.setBounds(b);
		}
		
	
		private int getCursor(Point aPoint)
		{
			boolean rx = true;
			boolean ry = true;

			if (!rx && !ry)
			{
				return Cursor.DEFAULT_CURSOR;
			}

			int PX = 30;
			int PY = 30;
			Rectangle bounds = mStartBounds;

			if (aPoint.y < PY)
			{
				if (aPoint.x < PX)
				{
					return rx ? ry ? Cursor.NW_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR;
				}
				if (aPoint.x >= bounds.width - PX)
				{
					return rx ? ry ? Cursor.NE_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR;
				}
				if (ry)
				{
					return Cursor.N_RESIZE_CURSOR;
				}
			}
			else if (aPoint.y >= bounds.height - PY)
			{
				if (aPoint.x < PX)
				{
					return rx ? ry ? Cursor.SW_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR;
				}
				if (aPoint.x >= bounds.width - PX)
				{
					return rx ? ry ? Cursor.SE_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR;
				}
				if (ry)
				{
					return Cursor.S_RESIZE_CURSOR;
				}
			}
			else if (aPoint.x < PX && rx)
			{
				return Cursor.W_RESIZE_CURSOR;
			}
			else if (aPoint.x >= bounds.width - PX && rx)
			{
				return Cursor.E_RESIZE_CURSOR;
			}

			return Cursor.DEFAULT_CURSOR;
		}
	};


	public void updateSize()
	{
		int w = mContentPanel.getWidth();

		mButtonRects = new Rectangle[3];

		mButtonRects[0] = new Rectangle(w - 3 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight);
		mButtonRects[1] = new Rectangle(w - 2 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight);
		mButtonRects[2] = new Rectangle(w - 1 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight);
	}


	public JPanel getContentPanel()
	{
		return mContentPanel;
	}


	void closeWindow()
	{
		SwingUtilities.invokeLater(() -> mFrame.dispose());
	}


	public void revalidate()
	{
		mContentPanel.invalidate();
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

	private WindowAdapter mWindowStateListener = new WindowAdapter()
	{
		@Override
		public void windowStateChanged(WindowEvent aEvent)
		{
			updateBorder(aEvent.getNewState());
		}
	};


	protected void updateBorder(int aState)
	{
		if (aState == JFrame.NORMAL)
		{
			mBorderPanel.setBorder(BorderFactory.createLineBorder(mBorderColor, mBorderWidth));
		}
		else
		{
			mBorderPanel.setBorder(null);
		}
	}


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

	private MouseAdapter mTitleBarMouseListener = new MouseAdapter()
	{
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
			if (mArmedButton != -1 && !mMouseDragged)
			{
				mArmedButton = -1;
				mContentPanel.repaint();
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
			mMouseDragged = false;

			update(aEvent);

			switch (aEvent.getClickCount() > 1 ? 1 : mArmedButton)
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

			mMousePressed = false;
			mArmedButton = -1;
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			mMouseDragged = true;

			if (mArmedButton != -1)
			{
				return;
			}

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
				mFrame.setLocation(aEvent.getXOnScreen() - mMouseX - mBorderWidth, aEvent.getYOnScreen() - mMouseY - mBorderWidth);
			}
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			update(aEvent);
		}
	};


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
			mContentPanel.repaint();
		}
	}

	private JComponent mTitleBar = new JComponent()
	{
		@Override
		protected void paintComponent(Graphics aGraphics)
		{
			aGraphics.setColor(getBackground());
			aGraphics.fillRect(0, 0, getWidth(), getHeight());

			new TextBox(mFrame.getTitle()).setBounds(0, 0, mButtonRects[0].x, getHeight()).setAnchor(Anchor.WEST).setForeground(getForeground()).setMargins(0, 4, 0, 4).setFont(mTitleBar.getFont()).render(aGraphics);

			Graphics2D g = (Graphics2D)aGraphics;
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			paintMinimizeButton(g, mButtonRects[0], mArmedButton == 0);
			if (mFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
			{
				paintRestoreButton(g, mButtonRects[1], mArmedButton == 1);
			}
			else
			{
				paintMaximizeButton(g, mButtonRects[1], mArmedButton == 1);
			}
			paintCloseButton(g, mButtonRects[2], mArmedButton == 2);
		}


		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(1, mTitleBarHeight);
		}
	};


	protected void paintCloseButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2;
		if (aArmed)
		{
			aGraphics.setColor(mCloseButtonArmedBackground);
			aGraphics.fill(aBounds);
		}
		aGraphics.setColor(mButtonSymbolShadowColor);
		aGraphics.drawLine(cx - 7 + 1, cy - 7, cx + 7, cy + 7 - 1);
		aGraphics.drawLine(cx - 7, cy - 7 + 1, cx + 7 - 1, cy + 7);
		aGraphics.drawLine(cx + 7 - 1, cy - 7, cx - 7, cy + 7 - 1);
		aGraphics.drawLine(cx + 7, cy - 7 + 1, cx - 7 + 1, cy + 7);
		aGraphics.setColor(mButtonSymbolColor);
		aGraphics.drawLine(cx - 7, cy - 7, cx + 7, cy + 7);
		aGraphics.drawLine(cx + 7, cy - 7, cx - 7, cy + 7);
	}


	protected void paintRestoreButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2;
		if (aArmed)
		{
			aGraphics.setColor(mButtonArmedBackground);
			aGraphics.fill(aBounds);
		}
		aGraphics.setColor(mButtonSymbolColor);
		aGraphics.drawRect(cx - 6 - 2, cy - 6 + 2, 12, 12);
		aGraphics.drawLine(cx - 6 + 2, cy - 6, cx - 6 + 2, cy - 6 + 1);
		aGraphics.drawLine(cx - 6 + 2, cy - 6 - 1, cx + 6 + 2, cy - 6 - 1);
		aGraphics.drawLine(cx + 6 + 2, cy - 6, cx + 6 + 2, cy + 6 - 2);
		aGraphics.drawLine(cx + 6 - 1, cy + 6 - 2, cx + 6 + 1, cy + 6 - 2);
	}


	protected void paintMinimizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		if (aArmed)
		{
			aGraphics.setColor(mButtonArmedBackground);
			aGraphics.fill(aBounds);
		}
		aGraphics.setColor(mButtonSymbolColor);
		aGraphics.drawLine(aBounds.x + aBounds.width / 2 - 7, aBounds.y + aBounds.height / 2, aBounds.x + aBounds.width / 2 + 7, aBounds.y + aBounds.height / 2);
	}


	protected void paintMaximizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		if (aArmed)
		{
			aGraphics.setColor(mButtonArmedBackground);
			aGraphics.fill(aBounds);
		}
		aGraphics.setColor(mButtonSymbolColor);
		aGraphics.drawRect(aBounds.x + aBounds.width / 2 - 7, aBounds.y + aBounds.height / 2 - 7, 15, 15);
	}


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
			wnd.getContentPanel().add(new JLabel("test"));
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

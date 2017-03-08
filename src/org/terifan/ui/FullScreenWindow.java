package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class FullScreenWindow
{
	protected Color mBorderColor = new Color(38, 38, 38);
	protected Color mButtonArmedBackground = new Color(64, 64, 64);
	protected Color mCloseButtonArmedBackground = new Color(232, 17, 35);
	protected Color mButtonSymbolColor = new Color(255, 255, 255);
	protected Color mButtonSymbolShadowColor = new Color(255, 255, 255, 64);

	protected JFrame mFrame;
	protected int mArmedButton;
	protected Point mFramePosition;
	protected JComponent mContentPanel;
	protected JPanel mMainPanel;
	protected JPanel mBorderPanel;
	protected String mWindowTitle;
	protected Dimension mInitialSize;
	protected Rectangle[] mButtonRects;
	protected int mBorderSize;
	protected int mTitleBarHeight;
	protected int mTitleBarButtonWidth;
	protected int mLastKnownWidth;
	protected Dimension mMinSize;
	protected Dimension mMaxSize;
	protected boolean mResizeHor;
	protected boolean mResizeVer;
	protected boolean mMouseDragged;
	protected boolean mUndecorated;
	protected boolean mBorderVisible;


	public FullScreenWindow()
	{
		mWindowTitle = "New window";
		mInitialSize = new Dimension(1024, 768);
		mResizeVer = true;
		mResizeHor = true;
		mBorderVisible = true;
		mBorderSize = 5;
		mArmedButton = -1;
		mTitleBarHeight = 43;
		mTitleBarButtonWidth = 69;

		mMinSize = new Dimension(2 * mBorderSize + 4 * mTitleBarButtonWidth, 2 * mBorderSize + mTitleBarHeight);
		mMaxSize = new Dimension(32000, 32000);

		mTitleBar.addMouseListener(mTitleBarMouseListener);
		mTitleBar.addMouseMotionListener(mTitleBarMouseListener);
		mTitleBar.setForeground(new Color(255, 255, 255));
		mTitleBar.setBackground(new Color(18, 18, 18));
//		mTitleBar.setFont(mTitleBar.getFont().deriveFont(17f));

mTitleBar.setForeground(new Color(0x0));
mTitleBar.setBackground(new Color(0x91CE11));
mButtonArmedBackground = new Color(0x82B90F);
mButtonSymbolColor = new Color(0,0,0);
mButtonSymbolShadowColor = new Color(0,0,0,64);
mBorderColor = new Color(0x91CE11);

		mContentPanel = new JPanel(new BorderLayout());

		mMainPanel = new JPanel(new BorderLayout());
		mMainPanel.add(mTitleBar, BorderLayout.NORTH);
		mMainPanel.add(mContentPanel, BorderLayout.CENTER);

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.add(mMainPanel, BorderLayout.CENTER);
		mBorderPanel.addMouseListener(mBorderMouseListener);
		mBorderPanel.addMouseMotionListener(mBorderMouseListener);

		mFrame = new JFrame(mWindowTitle);
		mFrame.add(mBorderPanel);
		mFrame.setSize(mInitialSize);
		mFrame.addComponentListener(mComponentAdapter);
		mFrame.addWindowListener(mWindowStateListener);
		mFrame.addWindowStateListener(mWindowStateListener);
		mFrame.setLocationRelativeTo(null);
		mFrame.setUndecorated(true);

		updateBorder(JFrame.NORMAL);
	}


	public FullScreenWindow add(JComponent aComponent)
	{
		mContentPanel.add(aComponent);
		return this;
	}


	public FullScreenWindow setLocation(int aX, int aY)
	{
		mFrame.setLocation(aX, aY);
		return this;
	}


	public JComponent getContentPanel()
	{
		return mContentPanel;
	}


	public FullScreenWindow dispose()
	{
		mFrame.dispose();
		return this;
	}


	public FullScreenWindow revalidate()
	{
		mMainPanel.invalidate();
		mFrame.revalidate();
		return this;
	}


	public FullScreenWindow setVisible(boolean aState)
	{
		mFrame.setVisible(aState);
		return this;
	}
	
	
	public boolean isBorderVisible()
	{
		return mBorderVisible;
	}
	
	
	public FullScreenWindow setBorderVisible(boolean aBorderVisible)
	{
		mBorderVisible = aBorderVisible;
		updateBorder(mFrame.getExtendedState());
		return this;
	}
	
	
	public boolean isUndecorated()
	{
		return mUndecorated;
	}

	
	public FullScreenWindow setUndecorated(boolean aUndecorated)
	{
		mUndecorated = aUndecorated;
		mTitleBar.setVisible(!mUndecorated);
		updateBorder(mFrame.getExtendedState());
		return this;
	}


	private void updateSize()
	{
		int w = mMainPanel.getWidth();

		mButtonRects = new Rectangle[]
		{
			new Rectangle(w - 3 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight),
			new Rectangle(w - 2 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight),
			new Rectangle(w - 1 * mTitleBarButtonWidth, 0, mTitleBarButtonWidth, mTitleBarHeight)
		};
	}


	protected MouseAdapter mBorderMouseListener = new MouseAdapter()
	{
		private Rectangle mStartBounds;
		private Point mClickPoint;
		private Integer mCursor;


		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mStartBounds = mFrame.getBounds();
			mClickPoint = aEvent.getPoint();
			mCursor = getCursor(mClickPoint);
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			mMouseDragged = true;
			Point p = aEvent.getLocationOnScreen();
			p.x -= mStartBounds.x;
			p.y -= mStartBounds.y;
			resizeBox(p);
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			mMouseDragged = false;
			mCursor = null;

			mFrame.setCursor(Cursor.getPredefinedCursor(getCursor(aEvent.getPoint())));
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			if (!mMouseDragged && mBorderVisible)
			{
				if (mStartBounds == null)
				{
					mStartBounds = mFrame.getBounds();
				}
				mFrame.setCursor(Cursor.getPredefinedCursor(getCursor(aEvent.getPoint())));
			}
		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			if (!mMouseDragged)
			{
				mFrame.setCursor(Cursor.getDefaultCursor());
			}
		}


		private void resizeBox(Point aPoint)
		{
			Rectangle b = new Rectangle(mStartBounds);

			switch (mCursor)
			{
				case Cursor.W_RESIZE_CURSOR:
				case Cursor.NW_RESIZE_CURSOR:
				case Cursor.SW_RESIZE_CURSOR:
					int o = b.x;
					b.x = Math.max(mStartBounds.x + mStartBounds.width - mMaxSize.width, Math.min(mStartBounds.x - mClickPoint.x + aPoint.x, mStartBounds.x + mStartBounds.width - mMinSize.width));
					b.width += o - b.x;
					break;
			}

			switch (mCursor)
			{
				case Cursor.N_RESIZE_CURSOR:
				case Cursor.NW_RESIZE_CURSOR:
				case Cursor.NE_RESIZE_CURSOR:
					int o = b.y;
					b.y = Math.max(mStartBounds.y + mStartBounds.height - mMaxSize.height, Math.min(mStartBounds.y - mClickPoint.y + aPoint.y, mStartBounds.y + mStartBounds.height - mMinSize.height));
					b.height += o - b.y;
					break;
			}

			switch (mCursor)
			{
				case Cursor.SW_RESIZE_CURSOR:
				case Cursor.S_RESIZE_CURSOR:
				case Cursor.SE_RESIZE_CURSOR:
					b.height = mStartBounds.height - mClickPoint.y + aPoint.y;
					break;
			}

			switch (mCursor)
			{
				case Cursor.E_RESIZE_CURSOR:
				case Cursor.SE_RESIZE_CURSOR:
				case Cursor.NE_RESIZE_CURSOR:
					b.width = mStartBounds.width - mClickPoint.x + aPoint.x;
					break;
			}

			b.width = Math.min(mMaxSize.width, Math.max(mMinSize.width, b.width));
			b.height = Math.min(mMaxSize.height, Math.max(mMinSize.height, b.height));

			mFrame.setBounds(b);
		}


		private int getCursor(Point aPoint)
		{
			if (!mResizeHor && !mResizeVer)
			{
				return Cursor.DEFAULT_CURSOR;
			}

			if (aPoint.y < mBorderSize)
			{
				if (aPoint.x < mBorderSize)
				{
					return mResizeHor ? mResizeVer ? Cursor.NW_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR;
				}
				if (aPoint.x >= mStartBounds.width - mBorderSize)
				{
					return mResizeHor ? mResizeVer ? Cursor.NE_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR;
				}
				if (mResizeVer)
				{
					return Cursor.N_RESIZE_CURSOR;
				}
			}
			else if (aPoint.y >= mStartBounds.height - mBorderSize)
			{
				if (aPoint.x < mBorderSize)
				{
					return mResizeHor ? mResizeVer ? Cursor.SW_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR;
				}
				if (aPoint.x >= mStartBounds.width - mBorderSize)
				{
					return mResizeHor ? mResizeVer ? Cursor.SE_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR : Cursor.S_RESIZE_CURSOR;
				}
				if (mResizeVer)
				{
					return Cursor.S_RESIZE_CURSOR;
				}
			}
			else if (aPoint.x < mBorderSize && mResizeHor)
			{
				return Cursor.W_RESIZE_CURSOR;
			}
			else if (aPoint.x >= mStartBounds.width - mBorderSize && mResizeHor)
			{
				return Cursor.E_RESIZE_CURSOR;
			}

			return Cursor.DEFAULT_CURSOR;
		}
	};

	private WindowAdapter mWindowStateListener = new WindowAdapter()
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			dispose();
		}


		@Override
		public void windowStateChanged(WindowEvent aEvent)
		{
			updateBorder(aEvent.getNewState());
		}
	};

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
			if (mFrame.isVisible() && !isMaximized())
			{
				mLastKnownWidth = mFrame.getWidth();
				mFramePosition = mFrame.getLocationOnScreen();
			}
		}
	};

	private MouseAdapter mTitleBarMouseListener = new MouseAdapter()
	{
		private Point mClickPoint;
		
		
		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mFrame.requestFocus();
			mClickPoint = aEvent.getPoint();

			updateArmedButton(aEvent);

		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			if (mArmedButton != -1 && !mMouseDragged)
			{
				mArmedButton = -1;
				mMainPanel.repaint();
			}
		}


		@Override
		public void mouseEntered(MouseEvent aEvent)
		{
			updateArmedButton(aEvent);
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			mMouseDragged = false;

			updateArmedButton(aEvent);

			switch (aEvent.getClickCount() > 1 ? 1 : mArmedButton)
			{
				case 0:
					mFrame.setExtendedState(JFrame.ICONIFIED);
					break;
				case 1:
					if (isMaximized())
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

			updateArmedButton(aEvent);

			if (isMaximized())
			{
				int x = mLastKnownWidth / 2;

				mFrame.setExtendedState(JFrame.NORMAL);
				mFrame.setLocation(aEvent.getXOnScreen() - x, aEvent.getYOnScreen());

				mClickPoint.x = x;
			}
			else
			{
				mFrame.setLocation(aEvent.getXOnScreen() - mClickPoint.x - mBorderSize, aEvent.getYOnScreen() - mClickPoint.y - mBorderSize);
			}
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			updateArmedButton(aEvent);
		}

		
		private void updateArmedButton(MouseEvent aEvent)
		{
			if (!mMouseDragged)
			{
				mFrame.setCursor(Cursor.getDefaultCursor());

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
		}
	};

	private JComponent mTitleBar = new JComponent()
	{
		@Override
		protected void paintComponent(Graphics aGraphics)
		{
			Graphics2D g = (Graphics2D)aGraphics;
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());

			new TextBox(mFrame.getTitle()).setBounds(0, 0, mButtonRects[0].x, getHeight()).setAnchor(Anchor.WEST).setForeground(getForeground()).setMargins(0, 4, 0, 4).setFont(mTitleBar.getFont()).setMaxLineCount(1).render(g);

			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			paintMinimizeButton(g, mButtonRects[0], mArmedButton == 0);
			if (isMaximized())
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


	protected void updateBorder(int aState)
	{
		if (mBorderVisible && !mUndecorated && aState == JFrame.NORMAL)
		{
			mBorderPanel.setBorder(BorderFactory.createLineBorder(mBorderColor, mBorderSize));
		}
		else
		{
			mBorderPanel.setBorder(null);
		}
	}


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


	private boolean isMaximized()
	{
		return (mFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
	}


	public static void main(String... args)
	{
		try
		{
			FullScreenWindow wnd = new FullScreenWindow();
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
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class FullScreenWindow
{
	private DefaultWindowBorder mWindowBorder;

	protected JFrame mFrame;
	protected JDialog mDialog;
	protected final Window mWindow;

	protected WindowButtonType mArmedButton;
	protected WindowButtonType mHoverButton;
	protected boolean mMousePressed;
	private boolean mMaximized;

	protected JPanel mBorderPanel;
	protected Dimension mInitialSize;
	protected Dimension mMinSize;
	protected Dimension mMaxSize;
	protected JComponent mContentPanel;
	protected boolean mWindowFocused;
	protected int mLastKnownNormalWidth;
	protected int mLayoutSize;

	protected boolean mResizeHor;
	protected boolean mResizeVer;
	protected boolean mUndecorated;
	protected boolean mBorderPainted;

	private Rectangle mWindowBounds;
	private Point mClickPoint;
	private Point mMousePoint;
	private Integer mCursor;
	private long mExtendedStateTime;

	private Runnable mOnClosed;


	public FullScreenWindow(String aTitle)
	{
		this(null, aTitle, false, false, new DefaultWindowBorder());
	}


	public FullScreenWindow(Frame aParent, String aTitle, boolean aDialog, boolean aModal, DefaultWindowBorder aWindowBorder)
	{
		mInitialSize = new Dimension(1024, 768);
		mResizeVer = true;
		mResizeHor = true;
		mBorderPainted = true;

		mWindowBorder = aWindowBorder;
		mContentPanel = new JPanel(new BorderLayout());

		Handler handler = new Handler();

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.setBorder(mBorderWrapper);
		mBorderPanel.add(mContentPanel, BorderLayout.CENTER);
		mBorderPanel.addMouseListener(handler);
		mBorderPanel.addMouseMotionListener(handler);

		if (aDialog)
		{
			mWindow = mDialog = new JDialog(aParent, aTitle, aModal);
			mDialog.setUndecorated(true);
			mWindowBorder.setButtons(WindowButtonType.CLOSE);
		}
		else
		{
			mWindow = mFrame = new JFrame(aTitle);
			mFrame.setUndecorated(true);
			mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			mWindowBorder.setButtons(WindowButtonType.MINIMIZE, WindowButtonType.MAXIMIZE, WindowButtonType.CLOSE);
		}

		mWindow.add(mBorderPanel);
		mWindow.setSize(mInitialSize);
		mWindow.setLocationRelativeTo(null);
		mWindow.addComponentListener(handler);
		mWindow.addWindowListener(handler);
		mWindow.addWindowStateListener(handler);
		mWindow.addWindowFocusListener(handler);

		updateBorder();

		mMinSize = new Dimension(200, 40);
		mMaxSize = new Dimension(32000, 32000);
		mMousePoint = new Point();
	}


	/**
	 * Add a component to this window.
	 */
	public FullScreenWindow add(JComponent aComponent)
	{
		mContentPanel.add(aComponent);
		return this;
	}


	/**
	 * Add a component to this window.
	 */
	public FullScreenWindow add(JComponent aComponent, Object aConstraints)
	{
		mContentPanel.add(aComponent, aConstraints);
		return this;
	}


	/**
	 * Components visible inside this window are added to this component.
	 */
	public JComponent getContentPanel()
	{
		return mContentPanel;
	}


	public JFrame getFrame()
	{
		return mFrame;
	}


	public Window getWindow()
	{
		return mWindow;
	}


	public JDialog getDialog()
	{
		return mDialog;
	}


	public boolean isResizeHorizontal()
	{
		return mResizeHor;
	}


	public boolean isResizeVertical()
	{
		return mResizeVer;
	}


	public FullScreenWindow setLocation(int aX, int aY)
	{
		mWindow.setLocation(aX, aY);
		return this;
	}


	public FullScreenWindow dispose()
	{
		mWindow.dispose();
		return this;
	}


	public FullScreenWindow revalidate()
	{
		mContentPanel.invalidate();
		mWindow.revalidate();
		return this;
	}


	public FullScreenWindow setVisible(boolean aState)
	{
		mWindow.setVisible(aState);
		return this;
	}


	public boolean isBorderPainted()
	{
		return mBorderPainted;
	}


	public FullScreenWindow setBorderPainted(boolean aBorderPainted)
	{
		mBorderPainted = aBorderPainted;
		updateBorder();
		return this;
	}


	public boolean isUndecorated()
	{
		return mUndecorated;
	}


	public FullScreenWindow setUndecorated(boolean aUndecorated)
	{
		mUndecorated = aUndecorated;
		updateBorder();
		return this;
	}


	public void repaint()
	{
		mWindow.repaint();
	}


	public void removeAll()
	{
		mContentPanel.removeAll();
	}


	private void resizeBox(Point aPoint)
	{
		Rectangle b = new Rectangle(mWindowBounds);

		switch (mCursor)
		{
			case Cursor.W_RESIZE_CURSOR:
			case Cursor.NW_RESIZE_CURSOR:
			case Cursor.SW_RESIZE_CURSOR:
				int o = b.x;
				b.x = Math.max(mWindowBounds.x + mWindowBounds.width - mMaxSize.width, Math.min(mWindowBounds.x - mClickPoint.x + aPoint.x, mWindowBounds.x + mWindowBounds.width - mMinSize.width));
				b.width += o - b.x;
				break;
		}

		switch (mCursor)
		{
			case Cursor.N_RESIZE_CURSOR:
			case Cursor.NW_RESIZE_CURSOR:
			case Cursor.NE_RESIZE_CURSOR:
				int o = b.y;
				b.y = Math.max(mWindowBounds.y + mWindowBounds.height - mMaxSize.height, Math.min(mWindowBounds.y - mClickPoint.y + aPoint.y, mWindowBounds.y + mWindowBounds.height - mMinSize.height));
				b.height += o - b.y;
				break;
		}

		switch (mCursor)
		{
			case Cursor.SW_RESIZE_CURSOR:
			case Cursor.S_RESIZE_CURSOR:
			case Cursor.SE_RESIZE_CURSOR:
				b.height = mWindowBounds.height - mClickPoint.y + aPoint.y;
				break;
		}

		switch (mCursor)
		{
			case Cursor.E_RESIZE_CURSOR:
			case Cursor.SE_RESIZE_CURSOR:
			case Cursor.NE_RESIZE_CURSOR:
				b.width = mWindowBounds.width - mClickPoint.x + aPoint.x;
				break;
		}

		b.width = Math.min(mMaxSize.width, Math.max(mMinSize.width, b.width));
		b.height = Math.min(mMaxSize.height, Math.max(mMinSize.height, b.height));

		mWindow.setBounds(b);
	}


	private void updateBorder()
	{
		mWindowBorder.updateState(mBorderPainted, mMaximized, mWindowFocused);

		mBorderPanel.invalidate();
		mBorderPanel.validate();
		mBorderPanel.repaint();
	}


	private Border mBorderWrapper = new Border()
	{
		Insets ZERO = new Insets(0, 0, 0, 0);

		@Override
		public void paintBorder(Component aComponent, Graphics aGraphics, int aX, int aY, int aWidth, int aHeight)
		{
			if (!mUndecorated)
			{
				mWindowBorder.paintBorder(FullScreenWindow.this, (Graphics2D)aGraphics, aX, aY, aWidth, aHeight, mHoverButton, mArmedButton, mMousePoint);
			}
		}


		@Override
		public Insets getBorderInsets(Component aC)
		{
			if (!mUndecorated)
			{
				return mWindowBorder.getBorderInsets();
			}
			return ZERO;
		}


		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}
	};


	public String getTitle()
	{
		return mFrame != null ? mFrame.getTitle() : mDialog.getTitle();
	}


	public void setTitle(String aTitle)
	{
		if (mFrame != null)
		{
			mFrame.setTitle(aTitle);
		}
		else
		{
			mDialog.setTitle(aTitle);
		}

		mBorderPanel.repaint();
	}


	public void setLocationByPlatform(boolean aState)
	{
		mWindow.setLocationByPlatform(aState);
	}


	public void setSize(int aWidth, int aHeight)
	{
		mWindow.setSize(aWidth, aHeight);
	}


	public void setExtendedState(int aState)
	{
		if (mFrame != null)
		{
			mFrame.setExtendedState(aState);
			mExtendedStateTime = System.currentTimeMillis();
		}
	}


	public void setIconImages(List<Image> aIcons)
	{
		mWindow.setIconImages(aIcons);
	}


	public void addWindowListener(WindowListener aWindowListener)
	{
		mWindow.addWindowListener(aWindowListener);
	}


	public void toFront()
	{
		mWindow.toFront();
	}


	public void toBack()
	{
		mWindow.toBack();
	}


	BorderIntersectionType mBorderIntersectionType;


	public void setLayout(LayoutManager2 aLayout)
	{
		getContentPanel().setLayout(aLayout);
	}


	private class Handler extends WindowAdapter implements MouseListener, MouseMotionListener, WindowFocusListener, WindowStateListener, ComponentListener
	{
		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mWindow.requestFocus();

			mClickPoint = aEvent.getPoint();

			mBorderIntersectionType = mWindowBorder.intersectBorder(FullScreenWindow.this, mClickPoint);

			mCursor = mBorderIntersectionType.CURSOR;

			mWindowBounds = mWindow.getBounds();
			mMousePressed = true;

			mArmedButton = mHoverButton = mWindowBorder.intersectButton(FullScreenWindow.this, mClickPoint);
			mBorderPanel.repaint();

			if (mBorderIntersectionType == BorderIntersectionType.MOVE && aEvent.getClickCount() > 1)
			{
				if (mMaximized)
				{
					setExtendedState(JFrame.NORMAL);
				}
				else
				{
					setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			}
		}


		@Override
		public void mouseEntered(MouseEvent aEvent)
		{
			mMousePoint = aEvent.getPoint();

			mBorderPanel.repaint();
		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			mMousePoint = aEvent.getPoint();

			if (mBorderIntersectionType != BorderIntersectionType.MOVE)
			{
				mHoverButton = null;
				mWindow.setCursor(Cursor.getDefaultCursor());
			}

			mBorderPanel.repaint();
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			mMousePoint = aEvent.getPoint();

			BorderIntersectionType bi = mWindowBorder.intersectBorder(FullScreenWindow.this, mMousePoint);

			if (mBorderPainted)
			{
				if (bi != BorderIntersectionType.NONE)
				{
					mWindowBounds = mWindow.getBounds();
				}

				mWindow.setCursor(Cursor.getPredefinedCursor(bi.CURSOR));
			}

			WindowButtonType old = mHoverButton;
			mHoverButton = mWindowBorder.intersectButton(FullScreenWindow.this, mMousePoint);

			if (old != mHoverButton || mWindowBorder.onMouseMotion(mMousePoint))
			{
				mBorderPanel.repaint();
			}
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			if (System.currentTimeMillis() - mExtendedStateTime < 100) // bug work-around, double clicking on the titlebar to maximize it triggers a mouse drag...
			{
				return;
			}

			mMousePoint = aEvent.getPoint();

			if (mBorderIntersectionType.RESIZE)
			{
				Point p = aEvent.getLocationOnScreen();
				p.x -= mWindowBounds.x;
				p.y -= mWindowBounds.y;
				resizeBox(p);
				return;
			}

			WindowButtonType tmp = mWindowBorder.intersectButton(FullScreenWindow.this, aEvent.getPoint());
			mHoverButton = tmp == mArmedButton ? tmp : null;
			mBorderPanel.repaint();

			if (mBorderIntersectionType == BorderIntersectionType.MOVE)
			{
				if (mMaximized)
				{
					int x = mLastKnownNormalWidth / 2;
					setExtendedState(JFrame.NORMAL);
					mWindow.setLocation(aEvent.getXOnScreen() - x, aEvent.getYOnScreen());
					mClickPoint.x = x;
				}
				else
				{
					mWindow.setLocation(aEvent.getXOnScreen() - mClickPoint.x, aEvent.getYOnScreen() - mClickPoint.y);
				}
			}
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			BorderIntersectionType oldIntersection = mBorderIntersectionType;

			Point p = aEvent.getPoint();
			mBorderIntersectionType = BorderIntersectionType.NONE;
			mMousePressed = false;
			mCursor = null;

			BorderIntersectionType bi = mWindowBorder.intersectBorder(FullScreenWindow.this, p);

			mWindow.setCursor(Cursor.getPredefinedCursor(bi.CURSOR));

			if (mFrame != null && oldIntersection == BorderIntersectionType.MOVE && aEvent.getLocationOnScreen().y == 0)
			{
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				return;
			}

			WindowButtonType newArmedButton = mWindowBorder.intersectButton(FullScreenWindow.this, aEvent.getPoint());

			if (mArmedButton != null && newArmedButton == mArmedButton)
			{
				switch (mArmedButton)
				{
					case MINIMIZE:
						setExtendedState(JFrame.ICONIFIED);
						break;
					case MAXIMIZE:
						setExtendedState(JFrame.MAXIMIZED_BOTH);
						break;
					case RESTORE:
						setExtendedState(JFrame.NORMAL);
						break;
					case CLOSE:
						mWindow.dispatchEvent(new WindowEvent(mWindow, WindowEvent.WINDOW_CLOSING));
						break;
				}

				mArmedButton = null;
			}
			else
			{
				mArmedButton = newArmedButton;
			}
			mBorderPanel.repaint();
		}


		@Override
		public void mouseClicked(MouseEvent aE)
		{
		}


		@Override
		public void windowGainedFocus(WindowEvent aEvent)
		{
			mWindowFocused = true;
			updateBorder();
			onWindowGainedFocus();
		}


		@Override
		public void windowLostFocus(WindowEvent aEvent)
		{
			mWindowFocused = false;
			updateBorder();
			onWindowLostFocus();
		}


		@Override
		public void windowClosing(WindowEvent e)
		{
			if (onWindowClosing())
			{
				dispose();
				onWindowClosed();
			}
		}


		@Override
		public void windowStateChanged(WindowEvent aEvent)
		{
			switch (aEvent.getNewState())
			{
				case JFrame.MAXIMIZED_BOTH:
					mMaximized = true;
					if (mFrame != null)
					{
						mWindowBorder.setButtons(WindowButtonType.MINIMIZE, WindowButtonType.RESTORE, WindowButtonType.CLOSE);
					}
					onWindowMaximized();
					break;
				case JFrame.NORMAL:
					mMaximized = false;
					if (mFrame != null)
					{
						mWindowBorder.setButtons(WindowButtonType.MINIMIZE, WindowButtonType.MAXIMIZE, WindowButtonType.CLOSE);
					}
					onWindowRestored();
					break;
				case JFrame.ICONIFIED:
					mMaximized = false;
					onWindowMinimized();
					break;
			}

			updateBorder();
		}


		@Override
		public void componentResized(ComponentEvent aEvent)
		{
			revalidate();
			onWindowResized();
		}


		@Override
		public void componentMoved(ComponentEvent aEvent)
		{
			if (mWindow.isVisible() && mFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH)
			{
				mLastKnownNormalWidth = mWindow.getWidth();
			}
		}


		@Override
		public void componentShown(ComponentEvent aE)
		{
		}


		@Override
		public void componentHidden(ComponentEvent aE)
		{
		}
	};


	public void setOnClosed(Runnable aRunnable)
	{
		mOnClosed = aRunnable;
	}


	private void onWindowClosed()
	{
		if (mOnClosed != null)
		{
			mOnClosed.run();
		}
	}


	private boolean onWindowClosing()
	{
		return true;
	}


	private void onWindowResized()
	{
	}


	private void onWindowMinimized()
	{
	}


	private void onWindowMaximized()
	{
	}


	private void onWindowRestored()
	{
	}


	private void onWindowGainedFocus()
	{
	}


	private void onWindowLostFocus()
	{
	}
}

package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import static org.terifan.ui.ColorSet.*;


public class FullScreenWindow
{
	protected ColorSet mBorderInner = new ColorSet();
	protected ColorSet mBorderOuter = new ColorSet();
	protected ColorSet mTitleBarBackground = new ColorSet();
	protected ColorSet[] mTitleBarForeground =
	{
		new ColorSet(), new ColorSet(), new ColorSet()
	};
	protected ColorSet mCloseButtonBackground = new ColorSet();
	protected ColorSet mCloseButtonForeground = new ColorSet();
	protected ColorSet mCloseButtonForegroundShadow = new ColorSet();
	protected ColorSet mWindowButtonBackground = new ColorSet();
	protected ColorSet mWindowButtonForeground = new ColorSet();

	protected final JFrame mFrame;
	protected final JDialog mDialog;
	protected final Window mWindow;

	protected int mArmedButton;
	protected Point mWindowPosition;
	protected JComponent mContentPanel;
	protected JPanel mBorderPanel;
	protected String[] mTitle;
	protected Dimension mInitialSize;
	protected Rectangle[] mButtonRects;
	protected int mBorderSize;
	protected int mTitleBarHeight;
	protected int mTitleBarButtonHeight;
	protected int mTitleBarButtonWidth;
	protected int mLastKnownWidth;
	protected Dimension mMinSize;
	protected Dimension mMaxSize;
	protected boolean mResizeHor;
	protected boolean mResizeVer;
	protected boolean mMouseDragged;
	protected boolean mUndecorated;
	protected boolean mBorderVisible;
	protected int mLayoutSize;
	protected boolean mFocused;
	protected Font[] mTitleBarFont;
	protected int mTitleBarButtonSymbolSize;
	private final int mStyle;
	private OnClosedAction mOnClosedAction;
	private OnClosingAction mOnClosingAction;
	private OnResizeAction mOnResizeAction;
	private OnMaximizeAction mOnMaximizeAction;
	private OnMinimizeAction mOnMinimizeAction;
	private OnRestoreAction mOnRestoreAction;
	private OnGainedFocusAction mOnGainedFocusAction;
	private OnLostFocusAction mOnLostFocusAction;


	public FullScreenWindow(String aTitle)
	{
		this(null, aTitle, false, false, 1);
	}


	public FullScreenWindow(Frame aParent, String aTitle, boolean aDialog, boolean aModal, int aStyle)
	{
		mTitle = new String[3];
		mTitle[0] = aTitle;
		mTitle[1] = "";
		mTitle[2] = "";
		mInitialSize = new Dimension(1024, 768);
		mResizeVer = true;
		mResizeHor = true;
		mBorderVisible = true;
		mArmedButton = -1;
		mTitleBarFont = new Font[3];
		mTitleBarFont[0] = new Font("segoe ui", Font.BOLD, 17);
		mTitleBarFont[1] = new Font("segoe ui", Font.BOLD, 17);
		mTitleBarFont[2] = new Font("segoe ui", Font.PLAIN, 17);
		mTitleBarButtonSymbolSize = 14;

		mMinSize = new Dimension(2 * mBorderSize + 4 * mTitleBarButtonWidth, mBorderSize + mTitleBarHeight);
		mMaxSize = new Dimension(32000, 32000);

		mStyle = aStyle;

		switch (mStyle)
		{
			case 0:
				setupLightStyle();
				break;
			case 1:
			case 3:
				setupDarkStyle();
				break;
			case 2:
				setupGradientStyle();
				break;
		}

		mContentPanel = new JPanel(new BorderLayout());

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.add(mContentPanel, BorderLayout.CENTER);
		mBorderPanel.addMouseListener(mBorderMouseListener);
		mBorderPanel.addMouseMotionListener(mBorderMouseListener);

		if (aDialog)
		{
			mDialog = new JDialog(aParent, mTitle[0], aModal);
			mDialog.add(mBorderPanel);
			mDialog.setSize(mInitialSize);
			mDialog.addComponentListener(mComponentAdapter);
			mDialog.addWindowListener(mWindowStateListener);
			mDialog.addWindowStateListener(mWindowStateListener);
			mDialog.addWindowFocusListener(mWindowStateListener);
			mDialog.setLocationRelativeTo(null);
			mDialog.setUndecorated(true);

			mWindow = mDialog;
			mFrame = null;
		}
		else
		{
			mFrame = new JFrame(mTitle[0]);
			mFrame.add(mBorderPanel);
			mFrame.setSize(mInitialSize);
			mFrame.addComponentListener(mComponentAdapter);
			mFrame.addWindowListener(mWindowStateListener);
			mFrame.addWindowStateListener(mWindowStateListener);
			mFrame.addWindowFocusListener(mWindowStateListener);
			mFrame.setLocationRelativeTo(null);
			mFrame.setUndecorated(true);
			mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			mWindow = mFrame;
			mDialog = null;
		}


		updateDimensions();
		updateButtonPositions(100);
		updateBorder(JFrame.NORMAL, true);
	}


	protected void setupGradientStyle()
	{
		mTitleBarBackground.add(new Color(255, 255, 255), DEFAULT);
		mTitleBarBackground.add(new Color(145, 206, 17), FOCUSED);
		mTitleBarForeground[0].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[0].add(new Color(0, 0, 0), FOCUSED);
		mTitleBarForeground[1].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[1].add(new Color(0, 0, 0), FOCUSED);
		mTitleBarForeground[2].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[2].add(new Color(0, 0, 0), FOCUSED);

		mBorderInner.add(new Color(255, 255, 255), DEFAULT);
		mBorderInner.add(new Color(145, 206, 17), FOCUSED);
		mBorderOuter.add(new Color(240, 240, 240), DEFAULT);
		mBorderOuter.add(new Color(118, 157, 47), FOCUSED);

		mCloseButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mCloseButtonBackground.add(new Color(145, 206, 17, 0), FOCUSED);
		mCloseButtonBackground.add(new Color(199, 80, 80), ARMED);
		mCloseButtonBackground.add(new Color(199, 80, 80), FOCUSED | ARMED);
		mCloseButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mCloseButtonForeground.add(new Color(255, 255, 255), ARMED);
		mCloseButtonForeground.add(new Color(0, 0, 0), FOCUSED);
		mCloseButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 128), DEFAULT);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 128), ARMED);
		mCloseButtonForegroundShadow.add(new Color(0, 0, 0, 64), FOCUSED);
		mCloseButtonForegroundShadow.add(new Color(230, 230, 230, 128), FOCUSED | ARMED);

		mWindowButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mWindowButtonBackground.add(new Color(145, 206, 17, 0), FOCUSED);
		mWindowButtonBackground.add(new Color(54, 101, 179), ARMED);
		mWindowButtonBackground.add(new Color(54, 101, 179), FOCUSED | ARMED);
		mWindowButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mWindowButtonForeground.add(new Color(255, 255, 255), ARMED);
		mWindowButtonForeground.add(new Color(0, 0, 0), FOCUSED);
		mWindowButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
	}


	protected void setupLightStyle()
	{
		mTitleBarBackground.add(new Color(255, 255, 255), DEFAULT);
		mTitleBarBackground.add(new Color(145, 206, 17), FOCUSED);
		mTitleBarForeground[0].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[0].add(new Color(0, 0, 0), FOCUSED);
		mTitleBarForeground[1].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[1].add(new Color(0, 0, 0), FOCUSED);
		mTitleBarForeground[2].add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground[2].add(new Color(0, 0, 0), FOCUSED);

		mBorderInner.add(new Color(255, 255, 255), DEFAULT);
		mBorderInner.add(new Color(145, 206, 17), FOCUSED);
		mBorderOuter.add(new Color(240, 240, 240), DEFAULT);
		mBorderOuter.add(new Color(240, 240, 240), FOCUSED);

		mCloseButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mCloseButtonBackground.add(new Color(145, 206, 17), FOCUSED);
		mCloseButtonBackground.add(new Color(232, 17, 35), ARMED);
		mCloseButtonBackground.add(new Color(232, 17, 35), FOCUSED | ARMED);
		mCloseButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mCloseButtonForeground.add(new Color(255, 255, 255), ARMED);
		mCloseButtonForeground.add(new Color(0, 0, 0), FOCUSED);
		mCloseButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 64), DEFAULT);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 64), ARMED);
		mCloseButtonForegroundShadow.add(new Color(0, 0, 0, 64), FOCUSED);
		mCloseButtonForegroundShadow.add(new Color(230, 230, 230, 64), FOCUSED | ARMED);

		mWindowButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mWindowButtonBackground.add(new Color(145, 206, 17), FOCUSED);
		mWindowButtonBackground.add(new Color(210, 210, 210), ARMED);
		mWindowButtonBackground.add(new Color(130, 185, 15), FOCUSED | ARMED);
		mWindowButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mWindowButtonForeground.add(new Color(0, 0, 0), ARMED);
		mWindowButtonForeground.add(new Color(0, 0, 0), FOCUSED);
		mWindowButtonForeground.add(new Color(0, 0, 0), FOCUSED | ARMED);
	}


	protected void setupDarkStyle()
	{
		mTitleBarBackground.add(new Color(17, 17, 17), DEFAULT);
		mTitleBarForeground[0].add(new Color(255, 255, 255), DEFAULT);
		mTitleBarForeground[1].add(new Color(255, 255, 255), DEFAULT);
		mTitleBarForeground[2].add(new Color(160, 160, 160), DEFAULT);

		if(mStyle==3)
		{
			mBorderInner.add(new Color(0, 0, 0), DEFAULT);
			mBorderInner.add(new Color(0, 0, 0), FOCUSED);
			mBorderOuter.add(new Color(145,206,17), DEFAULT);
			mBorderOuter.add(new Color(145,206,17), FOCUSED);
		}
		else
		{
			mBorderInner.add(new Color(0, 0, 0), DEFAULT);
			mBorderInner.add(new Color(0, 0, 0), FOCUSED);
			mBorderOuter.add(new Color(240, 240, 240), DEFAULT);
			mBorderOuter.add(new Color(38, 38, 38), FOCUSED);
		}

		mCloseButtonBackground.add(new Color(17, 17, 17), DEFAULT);
		mCloseButtonBackground.add(new Color(232, 17, 35), ARMED);
		mCloseButtonBackground.add(new Color(232, 17, 35), FOCUSED | ARMED);
		mCloseButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mCloseButtonForeground.add(new Color(255, 255, 255), ARMED);
		mCloseButtonForeground.add(new Color(255, 255, 255), FOCUSED);
		mCloseButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 64), DEFAULT);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 64), ARMED);
		mCloseButtonForegroundShadow.add(new Color(255, 255, 255, 64), FOCUSED);
		mCloseButtonForegroundShadow.add(new Color(230, 230, 230, 64), FOCUSED | ARMED);

		mWindowButtonBackground.add(new Color(17, 17, 17), DEFAULT);
		mWindowButtonBackground.add(new Color(220, 220, 220), ARMED);
		mWindowButtonBackground.add(new Color(64, 64, 64), FOCUSED | ARMED);
		mWindowButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mWindowButtonForeground.add(new Color(0, 0, 0), ARMED);
		mWindowButtonForeground.add(new Color(255, 255, 255), FOCUSED);
		mWindowButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
	}


	protected void updateDimensions()
	{
		float scale = Utilities.getDPIScale();

		mBorderSize = (int)(4 * scale);
		mTitleBarHeight = (int)(21 * scale);
		mTitleBarButtonHeight = (int)(20 * scale);
		mTitleBarButtonWidth = (int)(34 * scale);
		mTitleBarButtonSymbolSize = (int)(5 + 5 * scale);

		mTitleBarFont[0] = new Font("segoe ui", Font.BOLD, (int)(9 * scale));
		mTitleBarFont[1] = new Font("segoe ui", Font.BOLD, (int)(9 * scale));
		mTitleBarFont[2] = new Font("segoe ui", Font.PLAIN, (int)(9 * scale));
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


	public boolean isFocused()
	{
		return mFocused;
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


	public boolean isBorderVisible()
	{
		return mBorderVisible;
	}


	public FullScreenWindow setBorderVisible(boolean aBorderVisible)
	{
		mBorderVisible = aBorderVisible;
		if (mFrame != null)
		{
			updateBorder(mFrame.getExtendedState(), mWindow.isFocused());
		}
		else
		{
			updateBorder(JFrame.NORMAL, mWindow.isFocused());
		}
		return this;
	}


	public boolean isUndecorated()
	{
		return mUndecorated;
	}


	public FullScreenWindow setUndecorated(boolean aUndecorated)
	{
		mUndecorated = aUndecorated;
		if (mFrame != null)
		{
			updateBorder(mFrame.getExtendedState(), mFrame.isFocused());
		}
		else
		{
			updateBorder(JFrame.NORMAL, mDialog.isFocused());
		}
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

	protected MouseAdapter mBorderMouseListener = new MouseAdapter()
	{
		private Rectangle mStartBounds;
		private Point mClickPoint;
		private Integer mCursor;
		private boolean mResize;


		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mWindow.requestFocus();

			mStartBounds = mWindow.getBounds();
			mClickPoint = aEvent.getPoint();
			mCursor = getCursor(mClickPoint);
			mResize = !isMaximized() && isBorder(mClickPoint);

			updateButtons(aEvent);
		}


		@Override
		public void mouseEntered(MouseEvent aEvent)
		{
			updateButtons(aEvent);
		}


		@Override
		public void mouseMoved(MouseEvent aEvent)
		{
			if (mBorderVisible && isBorder(aEvent.getPoint()))
			{
				mStartBounds = mWindow.getBounds();
				mWindow.setCursor(Cursor.getPredefinedCursor(getCursor(aEvent.getPoint())));
			}
			else
			{
				mWindow.setCursor(Cursor.getDefaultCursor());
			}

			updateButtons(aEvent);
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			mMouseDragged = aEvent.getX() < mButtonRects[0].x;

			if (mResize)
			{
				Point p = aEvent.getLocationOnScreen();
				p.x -= mStartBounds.x;
				p.y -= mStartBounds.y;
				resizeBox(p);
				return;
			}

			if (mArmedButton != -1)
			{
				return;
			}

			updateButtons(aEvent);

			if (aEvent.getY() < mTitleBarHeight)
			{
				if (isMaximized())
				{
					int x = mLastKnownWidth / 2;

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
			boolean wasDragged = mMouseDragged;

			Point p = aEvent.getPoint();
			mMouseDragged = false;
			mCursor = null;

			mWindow.setCursor(Cursor.getPredefinedCursor(getCursor(p)));

			if (mFrame != null && wasDragged && aEvent.getLocationOnScreen().y == 0)
			{
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				return;
			}

			updateButtons(aEvent);

			switch (aEvent.getClickCount() > 1 && p.x > mBorderSize && p.y < mTitleBarHeight && p.x < mButtonRects[0].x ? 1 : mArmedButton)
			{
				case 0:
					setExtendedState(JFrame.ICONIFIED);
					break;
				case 1:
					if (isMaximized())
					{
						setExtendedState(JFrame.NORMAL);
					}
					else
					{
						setExtendedState(JFrame.MAXIMIZED_BOTH);
					}
					break;
				case 2:
					mWindow.dispatchEvent(new WindowEvent(mWindow, WindowEvent.WINDOW_CLOSING));
					break;
				default:
					break;
			}

			mArmedButton = -1;
		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			if (!mMouseDragged)
			{
				mWindow.setCursor(Cursor.getDefaultCursor());
			}

			if (mArmedButton != -1 && !mMouseDragged)
			{
				mArmedButton = -1;
				mBorderPanel.repaint();
			}
		}


		protected boolean isBorder(Point aPoint)
		{
			return !isButton(aPoint) && aPoint.y < mBorderSize || aPoint.x < mBorderSize || aPoint.y > mWindow.getHeight() - mBorderSize || aPoint.x > mWindow.getWidth() - mBorderSize;
		}


		protected boolean isButton(Point aPoint)
		{
			if (aPoint.y < mTitleBarHeight)
			{
				for (Rectangle r : mButtonRects)
				{
					if (r.contains(aPoint))
					{
						return true;
					}
				}
			}

			return false;
		}


		private void updateButtons(MouseEvent aEvent)
		{
			if (!mMouseDragged)
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
					mBorderPanel.repaint();
				}
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

			mWindow.setBounds(b);
		}


		private int getCursor(Point aPoint)
		{
			if (!mResizeHor && !mResizeVer || isMaximized())
			{
				return Cursor.DEFAULT_CURSOR;
			}
			if (mResizeVer)
			{
				if (aPoint.y < mBorderSize)
				{
					if (mResizeHor)
					{
						if (aPoint.x < mBorderSize)
						{
							return Cursor.NW_RESIZE_CURSOR;
						}
						if (aPoint.x >= mStartBounds.width - mBorderSize)
						{
							return Cursor.NE_RESIZE_CURSOR;
						}
					}
					return Cursor.N_RESIZE_CURSOR;
				}
				if (aPoint.y >= mStartBounds.height - mBorderSize)
				{
					if (mResizeHor)
					{
						if (aPoint.x < mBorderSize)
						{
							return Cursor.SW_RESIZE_CURSOR;
						}
						if (aPoint.x >= mStartBounds.width - mBorderSize)
						{
							return Cursor.SE_RESIZE_CURSOR;
						}
					}
					return Cursor.S_RESIZE_CURSOR;
				}
			}
			if (mResizeHor)
			{
				if (aPoint.x < mBorderSize)
				{
					return Cursor.W_RESIZE_CURSOR;
				}
				if (aPoint.x >= mStartBounds.width - mBorderSize)
				{
					return Cursor.E_RESIZE_CURSOR;
				}
			}

			return Cursor.DEFAULT_CURSOR;
		}
	};


	private WindowAdapter mWindowStateListener = new WindowAdapter()
	{
		@Override
		public void windowGainedFocus(WindowEvent aEvent)
		{
			if (mFrame != null)
			{
				updateBorder(mFrame.getExtendedState(), true);
			}
			else
			{
				updateBorder(JFrame.NORMAL, true);
			}

			if (mOnGainedFocusAction != null)
			{
				mOnGainedFocusAction.onWindowGainedFocus();
			}
		}


		@Override
		public void windowLostFocus(WindowEvent aEvent)
		{
			if (mFrame != null)
			{
				updateBorder(mFrame.getExtendedState(), false);
			}
			else
			{
				updateBorder(JFrame.NORMAL, true);
			}

			if (mOnLostFocusAction != null)
			{
				mOnLostFocusAction.onWindowLostFocus();
			}
		}


		@Override
		public void windowClosing(WindowEvent e)
		{
			if (mOnClosingAction != null && !mOnClosingAction.onWindowClosing())
			{
				return;
			}

			dispose();

			if (mOnClosedAction != null)
			{
				mOnClosedAction.onWindowClosed();
			}
		}


		@Override
		public void windowStateChanged(WindowEvent aEvent)
		{
			updateBorder(aEvent.getNewState(), mWindow.isFocused());

			if (aEvent.getNewState() == JFrame.MAXIMIZED_BOTH && mOnMaximizeAction != null)
			{
				mOnMaximizeAction.onWindowMaximize();
			}
			else if (aEvent.getNewState() == JFrame.NORMAL && mOnRestoreAction != null)
			{
				mOnRestoreAction.onWindowRestore();
			}
			else if (aEvent.getNewState() == JFrame.ICONIFIED && mOnMinimizeAction != null)
			{
				mOnMinimizeAction.onWindowMinimize();
			}
		}
	};

	private ComponentAdapter mComponentAdapter = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent aEvent)
		{
			revalidate();

			if (mOnResizeAction != null)
			{
				mOnResizeAction.onWindowResize();
			}
		}


		@Override
		public void componentMoved(ComponentEvent aEvent)
		{
			if (mWindow.isVisible() && !isMaximized())
			{
				mLastKnownWidth = mWindow.getWidth();
				mWindowPosition = mWindow.getLocationOnScreen();
			}
		}
	};


	protected void updateBorder(int aState, boolean aFocused)
	{
		mFocused = aFocused;

		if (!mUndecorated)
		{
			mBorderPanel.setBorder(mTitleBorder);
		}
		else
		{
			mBorderPanel.setBorder(null);
		}

		mBorderPanel.invalidate();
		mBorderPanel.validate();
		mBorderPanel.repaint();
	}

	private Border mTitleBorder = new Border()
	{
		@Override
		public void paintBorder(Component aComponent, Graphics aGraphics, int aX, int aY, int aWidth, int aHeight)
		{
			updateButtonPositions(aWidth);

			boolean maximized = isMaximized();

			Graphics2D g = (Graphics2D)aGraphics;
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			paintBorder(g, aWidth, aHeight, maximized);

			if (mStyle == 2)
			{
				paintTitleBarGradient(g, aWidth);
			}
			else
			{
				paintTitleBar(g, aWidth, maximized);
			}

			if (mFrame != null)
			{
				paintMinimizeButton(g, mButtonRects[0], mArmedButton == 0, maximized);
				paintCloseButton(g, mButtonRects[2], mArmedButton == 2, maximized);

				if (maximized)
				{
					paintRestoreButton(g, mButtonRects[1], mArmedButton == 1, maximized);
				}
				else
				{
					paintMaximizeButton(g, mButtonRects[1], mArmedButton == 1, maximized);
				}
			}
			else
			{
				paintCloseButton(g, mButtonRects[2], mArmedButton == 2, maximized);
			}
		}


		protected void paintBorder(Graphics2D aGraphics, int aWidth, int aHeight, boolean aMaximized)
		{
			aGraphics.setColor(mBorderInner.get(mFocused));
			aGraphics.fillRect(0, 0, aWidth, mBorderSize);

			if (mBorderVisible && !aMaximized)
			{
				aGraphics.fillRect(0, mBorderSize, mBorderSize, aHeight - mBorderSize - mBorderSize);
				aGraphics.fillRect(0, aHeight - mBorderSize, aWidth, mBorderSize);
				aGraphics.fillRect(aWidth - mBorderSize, mBorderSize, mBorderSize, aHeight - mBorderSize - mBorderSize);

				aGraphics.setColor(mBorderOuter.get(mFocused));
				aGraphics.drawRect(0, 0, aWidth - 1, aHeight - 1);
			}
		}


		protected void paintTitleBar(Graphics2D aGraphics, int aWidth, boolean aMaximized)
		{
			aGraphics.setColor(mTitleBarBackground.get(mFocused));
			if (mBorderVisible && !aMaximized)
			{
				aGraphics.fillRect(mBorderSize, mBorderSize, aWidth - mBorderSize - mBorderSize, mTitleBarHeight - mBorderSize);
			}
			else
			{
				aGraphics.fillRect(0, 0, aWidth, mTitleBarHeight);
			}

			Rectangle rect = new TextBox(mTitle[0])
				.setBounds(mBorderSize, 0, mButtonRects[0].x - mBorderSize, mTitleBarButtonHeight)
				.setFont(mTitleBarFont[0])
				.setForeground(mTitleBarForeground[0].get(mFocused))
				.setMargins(aMaximized ? 0 : mBorderSize, 4, 0, 0)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1)
				.render(aGraphics)
				.measure();

			int x = rect.x + rect.width;

			if (!mTitle[1].isEmpty())
			{
				x += new TextBox(mTitle[1])
					.setBounds(x, 0, mButtonRects[0].x - x, mTitleBarButtonHeight)
					.setFont(mTitleBarFont[1])
					.setForeground(mTitleBarForeground[1].get(mFocused))
					.setMargins(aMaximized ? 0 : mBorderSize, 0, 0, 4)
					.setAnchor(Anchor.WEST)
					.setMaxLineCount(1)
					.render(aGraphics)
					.measure().width;
			}

			if (!mTitle[2].isEmpty())
			{
				new TextBox(mTitle[2])
					.setBounds(x, 0, mButtonRects[0].x - x, mTitleBarButtonHeight)
					.setFont(mTitleBarFont[2])
					.setForeground(mTitleBarForeground[2].get(mFocused))
					.setMargins(aMaximized ? 0 : mBorderSize, 0, 0, 4)
					.setAnchor(Anchor.WEST)
					.setMaxLineCount(1)
					.render(aGraphics);
			}
		}


		protected void paintTitleBarGradient(Graphics2D aGraphics, int aWidth)
		{
			int bs = mBorderVisible ? mBorderSize : 0;

			Color[] grad1 = new Color[]
			{
				new Color(145,206,17), new Color(242, 249, 230)
			};
			Color[] grad2 = new Color[]
			{
				new Color(145,206,17), new Color(242, 249, 230, 0)
			};

			Paint p = aGraphics.getPaint();

			aGraphics.setPaint(new LinearGradientPaint(0, 0, 0, mTitleBarHeight, GRAD_RANGE, grad1, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fillRect(bs, 1, aWidth - bs - bs, mTitleBarHeight - 1);
			aGraphics.setPaint(new LinearGradientPaint(bs, 1, bs+mTitleBarHeight/2, 1, GRAD_RANGE, grad2, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fillRect(bs, 1, mTitleBarHeight/2, mTitleBarHeight - 1);
			aGraphics.setPaint(new LinearGradientPaint(aWidth - bs, 1, aWidth - bs - mTitleBarHeight/2, 1, GRAD_RANGE, grad2, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fillRect(aWidth - bs - mTitleBarHeight/2, 1, mTitleBarHeight/2, mTitleBarHeight - 1);
			aGraphics.setPaint(p);

			Rectangle rect = new TextBox(mTitle[0])
				.setBounds(mBorderSize, 0, aWidth - mBorderSize, mTitleBarButtonHeight)
				.setFont(mTitleBarFont[0])
				.setForeground(mTitleBarForeground[0].get(mFocused))
				.setMargins(0, 4, 0, 4)
				.setAnchor(Anchor.CENTER)
				.setMaxLineCount(1)
				.render(aGraphics)
				.measure();

			int x = rect.x + rect.width;

			if (!mTitle[1].isEmpty())
			{
				x += new TextBox(mTitle[1])
					.setBounds(x, 0, mButtonRects[0].x - x, mTitleBarButtonHeight)
					.setFont(mTitleBarFont[1])
					.setForeground(mTitleBarForeground[1].get(mFocused))
					.setMargins(0, 0, 0, 4)
					.setAnchor(Anchor.WEST)
					.setMaxLineCount(1)
					.render(aGraphics)
					.measure().width;
			}

			if (!mTitle[2].isEmpty())
			{
				new TextBox(mTitle[2])
					.setBounds(x, 0, mButtonRects[0].x - x, mTitleBarButtonHeight)
					.setFont(mTitleBarFont[2])
					.setForeground(mTitleBarForeground[2].get(mFocused))
					.setMargins(0, 0, 0, 4)
					.setAnchor(Anchor.WEST)
					.setMaxLineCount(1)
					.render(aGraphics);
			}
		}


		@Override
		public Insets getBorderInsets(Component aC)
		{
			if (mBorderVisible)
			{
				if (isMaximized())
				{
					return new Insets(mTitleBarHeight, 0, 0, 0);
				}
				return new Insets(mTitleBarHeight, mBorderSize, mBorderSize, mBorderSize);
			}
			return new Insets(mTitleBarHeight, 0, 0, 0);
		}


		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}
	};
	protected static final float[] GRAD_RANGE = new float[]
	{
		0f, 1f
	};


	protected void updateButtonPositions(int aWidth)
	{
		if (aWidth != mLayoutSize)
		{
			boolean maximized = isMaximized();

			mLayoutSize = aWidth;
			mButtonRects = new Rectangle[]
			{
				new Rectangle(mLayoutSize - (mDialog!=null?1:3) * mTitleBarButtonWidth - (maximized ? 0 : mBorderSize), maximized ? 0 : 1, (mDialog!=null?0:1)*mTitleBarButtonWidth, mTitleBarButtonHeight),
				new Rectangle(mLayoutSize - (mDialog!=null?1:2) * mTitleBarButtonWidth - (maximized ? 0 : mBorderSize), maximized ? 0 : 1, (mDialog!=null?0:1)*mTitleBarButtonWidth, mTitleBarButtonHeight),
				new Rectangle(mLayoutSize - 1 * mTitleBarButtonWidth - (maximized ? 0 : mBorderSize), maximized ? 0 : 1, mTitleBarButtonWidth, mTitleBarButtonHeight)
			};
		}
	}


	protected void paintCloseButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed, boolean aMaximized)
	{
		aGraphics.setColor(mCloseButtonBackground.get(mFocused, aArmed));
		if (aMaximized || aArmed)
		{
			aGraphics.fill(aBounds);
		}
		else
		{
			aGraphics.fillRect(aBounds.x, aBounds.y + mBorderSize, aBounds.width, aBounds.height - mBorderSize);
		}

//		if (aArmed)
//		{
//			Paint p = aGraphics.getPaint();
//			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(199,80,80),new Color(219,141,141)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
//			aGraphics.fill(aBounds);
//			aGraphics.setPaint(new LinearGradientPaint(aBounds.x+aBounds.width, 0, aBounds.x, 0, new float[]{0f,1f}, new Color[]{new Color(199,80,80),new Color(219,141,141,0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
//			aGraphics.fill(aBounds);
//			aGraphics.setPaint(p);
//		}
		int S = mTitleBarButtonSymbolSize / 2;
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2 + (aMaximized || aArmed ? 0 : mBorderSize/2);
		aGraphics.setColor(mCloseButtonForegroundShadow.get(mFocused, aArmed));
		aGraphics.drawLine(cx - S + 1, cy - S, cx + S, cy + S - 1);
		aGraphics.drawLine(cx - S, cy - S + 1, cx + S - 1, cy + S);
		aGraphics.drawLine(cx + S - 1, cy - S, cx - S, cy + S - 1);
		aGraphics.drawLine(cx + S, cy - S + 1, cx - S + 1, cy + S);
		aGraphics.setColor(mCloseButtonForeground.get(mFocused, aArmed));
		aGraphics.drawLine(cx - S, cy - S, cx + S, cy + S);
		aGraphics.drawLine(cx + S, cy - S, cx - S, cy + S);
	}


	protected void paintRestoreButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed, boolean aMaximized)
	{
		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
		if (aMaximized || aArmed)
		{
			aGraphics.fill(aBounds);
		}
		else
		{
			aGraphics.fillRect(aBounds.x, aBounds.y + mBorderSize, aBounds.width, aBounds.height - mBorderSize);
		}

//		if (aArmed)
//		{
//			Paint p = aGraphics.getPaint();
//			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
//			aGraphics.fill(aBounds);
//			aGraphics.setPaint(p);
//		}
		int S = mTitleBarButtonSymbolSize / 2 - 1;
		int L = 1 + S / 2;
		int cx = aBounds.x + aBounds.width / 2 - L / 2 - 1;
		int cy = aBounds.y + aBounds.height / 2 + L / 2 + (aMaximized || aArmed ? 0 : mBorderSize/2);
		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
		aGraphics.drawRect(cx - S, cy - S, 2 * S, 2 * S);
		aGraphics.drawLine(cx - S + L, cy - S - L, cx - S + L, cy - S);
		aGraphics.drawLine(cx - S + L, cy - S - L, cx + S + L, cy - S - L);
		aGraphics.drawLine(cx + S + L, cy - S - L, cx + S + L, cy + S - L);
		aGraphics.drawLine(cx + S, cy + S - L, cx + S + L, cy + S - L);
	}


	protected void paintMinimizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed, boolean aMaximized)
	{
		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
		if (aMaximized || aArmed)
		{
			aGraphics.fill(aBounds);
		}
		else
		{
			aGraphics.fillRect(aBounds.x, aBounds.y + mBorderSize, aBounds.width, aBounds.height - mBorderSize);
		}

//		if (aArmed)
//		{
//			Paint p = aGraphics.getPaint();
//			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
//			aGraphics.fill(aBounds);
//			aGraphics.setPaint(p);
//		}
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2 + (aMaximized || aArmed ? 0 : mBorderSize/2);
		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
		aGraphics.drawLine(cx - 7, cy, cx + 7, cy);
	}


	protected void paintMaximizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed, boolean aMaximized)
	{
		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
		if (aMaximized || aArmed)
		{
			aGraphics.fill(aBounds);
		}
		else
		{
			aGraphics.fillRect(aBounds.x, aBounds.y + mBorderSize, aBounds.width, aBounds.height - mBorderSize);
		}

//		if (aArmed)
//		{
//			Paint p = aGraphics.getPaint();
//			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
//			aGraphics.fill(aBounds);
//			aGraphics.setPaint(p);
//		}

		int S = mTitleBarButtonSymbolSize / 2;
		int cx = aBounds.x + aBounds.width / 2 - S;
		int cy = aBounds.y + aBounds.height / 2 - S + (aMaximized || aArmed ? 0 : mBorderSize/2);
		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
		aGraphics.drawRect(cx, cy, 2 * S + 1, 2 * S + 1);
	}


	private boolean isMaximized()
	{
		if (mFrame != null)
		{
			return (mFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
		}

		return false;
	}


	public String getTitle()
	{
		return mTitle[0];
	}


	public void setTitle(String aTitle)
	{
		setTitle(aTitle, mTitle[1], mTitle[2]);
	}


	public void setTitle(String aTitle, String aTitleExtra1, String aTitleExtra2)
	{
		mTitle[0] = aTitle;
		mTitle[1] = aTitleExtra1;
		mTitle[2] = aTitleExtra2;

		if (mFrame != null)
		{
			mFrame.setTitle(mTitle[0]);
		}
		else
		{
			mDialog.setTitle(mTitle[0]);
		}

		mBorderPanel.repaint(0, 0, mWindow.getWidth(), mTitleBarHeight);
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


	public void setOnClosed(OnClosedAction aAction)
	{
		mOnClosedAction = aAction;
	}


	public void setOnClosing(OnClosingAction aAction)
	{
		mOnClosingAction = aAction;
	}


	public void setOnResize(OnResizeAction aAction)
	{
		mOnResizeAction = aAction;
	}


	public void setOnMinizmie(OnMinimizeAction aAction)
	{
		mOnMinimizeAction = aAction;
	}


	public void setOnMaximize(OnMaximizeAction aAction)
	{
		mOnMaximizeAction = aAction;
	}


	public void setOnRestore(OnRestoreAction aAction)
	{
		mOnRestoreAction = aAction;
	}


	public void setOnGainedFocus(OnGainedFocusAction aAction)
	{
		mOnGainedFocusAction = aAction;
	}


	public void setOnLostFocus(OnLostFocusAction aAction)
	{
		mOnLostFocusAction = aAction;
	}


	@FunctionalInterface
	public interface OnClosedAction
	{
		void onWindowClosed();
	}


	@FunctionalInterface
	public interface OnClosingAction
	{
		boolean onWindowClosing();
	}


	@FunctionalInterface
	public interface OnResizeAction
	{
		void onWindowResize();
	}


	@FunctionalInterface
	public interface OnMinimizeAction
	{
		void onWindowMinimize();
	}


	@FunctionalInterface
	public interface OnMaximizeAction
	{
		void onWindowMaximize();
	}


	@FunctionalInterface
	public interface OnRestoreAction
	{
		void onWindowRestore();
	}


	@FunctionalInterface
	public interface OnGainedFocusAction
	{
		void onWindowGainedFocus();
	}


	@FunctionalInterface
	public interface OnLostFocusAction
	{
		void onWindowLostFocus();
	}


	public static void main(String... args)
	{
		try
		{
			FullScreenWindow wnd = new FullScreenWindow(null, "New window", true, true, 1);
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
			wnd.setOnClosing(()->{System.out.println("closing");return true;});
			wnd.setOnClosed(()->System.out.println("closed"));
			wnd.setOnResize(()->System.out.println("resize"));
			wnd.setOnMinizmie(()->System.out.println("minimize"));
			wnd.setOnMaximize(()->System.out.println("maximize"));
			wnd.setOnRestore(()->System.out.println("restore"));
			wnd.setOnGainedFocus(()->System.out.println("focused"));
			wnd.setOnLostFocus(()->System.out.println("unfocused"));
			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

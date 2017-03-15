package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import static org.terifan.ui.ColorSet.*;


public class FullScreenWindow
{
	protected ColorSet mBorderInner = new ColorSet();
	protected ColorSet mBorderOuter = new ColorSet();
	protected ColorSet mTitleBarBackground = new ColorSet();
	protected ColorSet mTitleBarForeground = new ColorSet();
	protected ColorSet mCloseButtonBackground = new ColorSet();
	protected ColorSet mCloseButtonForeground = new ColorSet();
	protected ColorSet mCloseButtonForegroundShadow = new ColorSet();
	protected ColorSet mWindowButtonBackground = new ColorSet();
	protected ColorSet mWindowButtonForeground = new ColorSet();

	protected JFrame mFrame;
	protected int mArmedButton;
	protected Point mFramePosition;
	protected JComponent mContentPanel;
	protected JPanel mBorderPanel;
	protected String mWindowTitle;
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
	protected Font mTitleBarFont;


	public FullScreenWindow()
	{
		mWindowTitle = "New window";
		mInitialSize = new Dimension(1024, 768);
		mResizeVer = true;
		mResizeHor = true;
		mBorderVisible = true;
		mBorderSize = 8;
		mArmedButton = -1;
		mTitleBarHeight = 49;
		mTitleBarButtonHeight = 40;
		mTitleBarButtonWidth = 69;
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 17);

		mMinSize = new Dimension(2 * mBorderSize + 4 * mTitleBarButtonWidth, mBorderSize + mTitleBarHeight);
		mMaxSize = new Dimension(32000, 32000);

		setupGradientStyle();
//		setupDarkStyle();
//		setupLightStyle();

		mContentPanel = new JPanel(new BorderLayout());

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.add(mContentPanel, BorderLayout.CENTER);
		mBorderPanel.addMouseListener(mBorderMouseListener);
		mBorderPanel.addMouseMotionListener(mBorderMouseListener);

		mFrame = new JFrame(mWindowTitle);
		mFrame.add(mBorderPanel);
		mFrame.setSize(mInitialSize);
		mFrame.addComponentListener(mComponentAdapter);
		mFrame.addWindowListener(mWindowStateListener);
		mFrame.addWindowStateListener(mWindowStateListener);
		mFrame.addWindowFocusListener(mWindowStateListener);
		mFrame.setLocationRelativeTo(null);
		mFrame.setUndecorated(true);

		updateBorder(JFrame.NORMAL, true);
	}


	protected void setupGradientStyle()
	{
		mTitleBarBackground.add(new Color(255, 255, 255), DEFAULT);
		mTitleBarBackground.add(new Color(145, 206, 17), FOCUSED);
		mTitleBarForeground.add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground.add(new Color(0, 0, 0), FOCUSED);

		mBorderInner.add(new Color(255, 255, 255), DEFAULT);
		mBorderInner.add(new Color(145, 206, 17), FOCUSED);
		mBorderOuter.add(new Color(240, 240, 240), DEFAULT);
		mBorderOuter.add(new Color(118,157,47), FOCUSED);

		mCloseButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mCloseButtonBackground.add(new Color(145, 206, 17, 0), FOCUSED);
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
		mWindowButtonBackground.add(new Color(145, 206, 17, 0), FOCUSED);
		mWindowButtonBackground.add(new Color(210, 210, 210), ARMED);
		mWindowButtonBackground.add(new Color(180,180,180), FOCUSED | ARMED);
		mWindowButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mWindowButtonForeground.add(new Color(0, 0, 0), ARMED);
		mWindowButtonForeground.add(new Color(0, 0, 0), FOCUSED);
		mWindowButtonForeground.add(new Color(0, 0, 0), FOCUSED | ARMED);
	}


	protected void setupLightStyle()
	{
		mTitleBarBackground.add(new Color(255, 255, 255), DEFAULT);
		mTitleBarBackground.add(new Color(145, 206, 17), FOCUSED);
		mTitleBarForeground.add(new Color(200, 200, 200), DEFAULT);
		mTitleBarForeground.add(new Color(0, 0, 0), FOCUSED);

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
		mTitleBarBackground.add(new Color(255, 255, 255), DEFAULT);
		mTitleBarBackground.add(new Color(0, 0, 0), FOCUSED);
		mTitleBarForeground.add(new Color(255, 255, 255), DEFAULT);

		mBorderInner.add(new Color(255, 255, 255), DEFAULT);
		mBorderInner.add(new Color(0, 0, 0), FOCUSED);
		mBorderOuter.add(new Color(240, 240, 240), DEFAULT);
		mBorderOuter.add(new Color(38, 38, 38), FOCUSED);

		mCloseButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mCloseButtonBackground.add(new Color(0, 0, 0), FOCUSED);
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

		mWindowButtonBackground.add(new Color(255, 255, 255), DEFAULT);
		mWindowButtonBackground.add(new Color(0, 0, 0), FOCUSED);
		mWindowButtonBackground.add(new Color(220, 220, 220), ARMED);
		mWindowButtonBackground.add(new Color(64, 64, 64), FOCUSED | ARMED);
		mWindowButtonForeground.add(new Color(180, 180, 180), DEFAULT);
		mWindowButtonForeground.add(new Color(0, 0, 0), ARMED);
		mWindowButtonForeground.add(new Color(255, 255, 255), FOCUSED);
		mWindowButtonForeground.add(new Color(255, 255, 255), FOCUSED | ARMED);
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
		mContentPanel.invalidate();
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
		updateBorder(mFrame.getExtendedState(), mFrame.isFocused());
		return this;
	}


	public boolean isUndecorated()
	{
		return mUndecorated;
	}


	public FullScreenWindow setUndecorated(boolean aUndecorated)
	{
		mUndecorated = aUndecorated;
		updateBorder(mFrame.getExtendedState(), mFrame.isFocused());
		return this;
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
			mFrame.requestFocus();

			mStartBounds = mFrame.getBounds();
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
				mStartBounds = mFrame.getBounds();
				mFrame.setCursor(Cursor.getPredefinedCursor(getCursor(aEvent.getPoint())));
			}
			else
			{
				mFrame.setCursor(Cursor.getDefaultCursor());
			}

			updateButtons(aEvent);
		}


		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			mMouseDragged = true;

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

			if (isMaximized())
			{
				int x = mLastKnownWidth / 2;

				mFrame.setExtendedState(JFrame.NORMAL);
				mFrame.setLocation(aEvent.getXOnScreen() - x, aEvent.getYOnScreen());

				mClickPoint.x = x;
			}
			else
			{
				mFrame.setLocation(aEvent.getXOnScreen() - mClickPoint.x, aEvent.getYOnScreen() - mClickPoint.y);
			}
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			boolean wasDragged = mMouseDragged;
			
			Point p = aEvent.getPoint();
			mMouseDragged = false;
			mCursor = null;

			mFrame.setCursor(Cursor.getPredefinedCursor(getCursor(p)));

			if (wasDragged && aEvent.getLocationOnScreen().y == 0)
			{
				mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				return;
			}

			updateButtons(aEvent);

			switch (aEvent.getClickCount() > 1 && p.x > mBorderSize && p.y < mTitleBarHeight && p.x < mLayoutSize - mBorderSize ? 1 : mArmedButton)
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
		public void mouseExited(MouseEvent aEvent)
		{
			if (!mMouseDragged)
			{
				mFrame.setCursor(Cursor.getDefaultCursor());
			}

			if (mArmedButton != -1 && !mMouseDragged)
			{
				mArmedButton = -1;
				mBorderPanel.repaint();
			}
		}


		protected boolean isBorder(Point aPoint)
		{
			return !isButton(aPoint) && aPoint.y < mBorderSize || aPoint.x < mBorderSize || aPoint.y > mFrame.getHeight() - mBorderSize || aPoint.x > mFrame.getWidth() - mBorderSize;
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

			mFrame.setBounds(b);
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
			updateBorder(mFrame.getExtendedState(), true);
		}


		@Override
		public void windowLostFocus(WindowEvent aEvent)
		{
			updateBorder(mFrame.getExtendedState(), false);
		}


		@Override
		public void windowClosing(WindowEvent e)
		{
			dispose();
		}


		@Override
		public void windowStateChanged(WindowEvent aEvent)
		{
			updateBorder(aEvent.getNewState(), mFrame.isFocused());
		}
	};

	private ComponentAdapter mComponentAdapter = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent aEvent)
		{
			revalidate();
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
			boolean maximized = isMaximized();

			if (aWidth != mLayoutSize)
			{
				mLayoutSize = aWidth;

				mButtonRects = new Rectangle[]
				{
					new Rectangle(mLayoutSize - 3 * mTitleBarButtonWidth - 1 - (maximized ? 1 : mBorderSize), maximized ? 0 : 1, mTitleBarButtonWidth, mTitleBarButtonHeight),
					new Rectangle(mLayoutSize - 2 * mTitleBarButtonWidth - 1 - (maximized ? 1 : mBorderSize), maximized ? 0 : 1, mTitleBarButtonWidth, mTitleBarButtonHeight),
					new Rectangle(mLayoutSize - 1 * mTitleBarButtonWidth - 1 - (maximized ? 1 : mBorderSize), maximized ? 0 : 1, mTitleBarButtonWidth, mTitleBarButtonHeight)
				};
			}

			Graphics2D g = (Graphics2D)aGraphics;
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			if (mBorderVisible)
			{
				g.setColor(mBorderInner.get(mFocused));
				g.fillRect(0, 0, aWidth, mBorderSize);
				g.fillRect(0, mBorderSize, mBorderSize, aHeight - mBorderSize - mBorderSize);
				g.fillRect(0, aHeight - mBorderSize, aWidth, mBorderSize);
				g.fillRect(aWidth - mBorderSize, mBorderSize, mBorderSize, aHeight - mBorderSize - mBorderSize);

				if (!maximized)
				{
					g.setColor(mBorderOuter.get(mFocused));
					g.drawRect(0, 0, aWidth - 1, aHeight - 1);
				}
			}

//			g.setColor(mTitleBarBackground.get(mFocused));
//			g.fillRect(mBorderSize, mBorderSize, aWidth - mBorderSize - mBorderSize, mTitleBarHeight - mBorderSize);

			int bs = mBorderVisible ? mBorderSize : 0;

			Paint p = g.getPaint();
			g.setPaint(new LinearGradientPaint(0, 0, 0, mTitleBarHeight, new float[]{0f,1f}, new Color[]{new Color(154,205,61),new Color(242,249,230)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			g.fillRect(bs, 1, aWidth - bs - bs, mTitleBarHeight - 1);
			g.setPaint(new LinearGradientPaint(bs, 1, mTitleBarHeight, 1, new float[]{0f,1f}, new Color[]{new Color(154,205,61),new Color(242,249,230,0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			g.fillRect(bs, 1, mTitleBarHeight, mTitleBarHeight - 1);
			g.setPaint(new LinearGradientPaint(aWidth - bs, 1, aWidth - mTitleBarHeight - bs, 1, new float[]{0f,1f}, new Color[]{new Color(154,205,61),new Color(242,249,230,0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			g.fillRect(aWidth-mTitleBarHeight-bs, 1, mTitleBarHeight, mTitleBarHeight - 1);
			g.setPaint(p);

			new TextBox(mFrame.getTitle())
//				.setBounds(mBorderSize, 0, mButtonRects[0].x - mBorderSize, mTitleBarHeight)
				.setBounds(mBorderSize, 0, aWidth - mBorderSize, mTitleBarButtonHeight)
				.setFont(mTitleBarFont)
				.setForeground(mTitleBarForeground.get(mFocused))
				.setMargins(0, 4, 0, 4)
//				.setAnchor(Anchor.WEST)
				.setAnchor(Anchor.CENTER)
				.setMaxLineCount(1)
				.render(g);

			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			paintMinimizeButton(g, mButtonRects[0], mArmedButton == 0);
			if (maximized)
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
		public Insets getBorderInsets(Component aC)
		{
			if (mBorderVisible)
			{
				return new Insets(mTitleBarHeight, mBorderSize, mBorderSize, mBorderSize);
			}
			else
			{
				return new Insets(mTitleBarHeight, 0, 0, 0);
			}
		}


		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}
	};


	protected void paintCloseButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2;
//		aGraphics.setColor(mCloseButtonBackground.get(mFocused, aArmed));
//		aGraphics.fill(aBounds);

		if (aArmed)
		{
			Paint p = aGraphics.getPaint();
			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(199,80,80),new Color(219,141,141)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fill(aBounds);
			aGraphics.setPaint(new LinearGradientPaint(aBounds.x+aBounds.width, 0, aBounds.x, 0, new float[]{0f,1f}, new Color[]{new Color(199,80,80),new Color(219,141,141,0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fill(aBounds);
			aGraphics.setPaint(p);
		}

		aGraphics.setColor(mCloseButtonForegroundShadow.get(mFocused, aArmed));
		aGraphics.drawLine(cx - 7 + 1, cy - 7, cx + 7, cy + 7 - 1);
		aGraphics.drawLine(cx - 7, cy - 7 + 1, cx + 7 - 1, cy + 7);
		aGraphics.drawLine(cx + 7 - 1, cy - 7, cx - 7, cy + 7 - 1);
		aGraphics.drawLine(cx + 7, cy - 7 + 1, cx - 7 + 1, cy + 7);
		aGraphics.setColor(mCloseButtonForeground.get(mFocused, aArmed));
		aGraphics.drawLine(cx - 7, cy - 7, cx + 7, cy + 7);
		aGraphics.drawLine(cx + 7, cy - 7, cx - 7, cy + 7);
	}


	protected void paintRestoreButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
		int cx = aBounds.x + aBounds.width / 2;
		int cy = aBounds.y + aBounds.height / 2;
//		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
//		aGraphics.fill(aBounds);

		if (aArmed)
		{
			Paint p = aGraphics.getPaint();
			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fill(aBounds);
			aGraphics.setPaint(p);
		}

		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
		aGraphics.drawRect(cx - 6 - 2, cy - 6 + 2, 12, 12);
		aGraphics.drawLine(cx - 6 + 2, cy - 6, cx - 6 + 2, cy - 6 + 1);
		aGraphics.drawLine(cx - 6 + 2, cy - 6 - 1, cx + 6 + 2, cy - 6 - 1);
		aGraphics.drawLine(cx + 6 + 2, cy - 6, cx + 6 + 2, cy + 6 - 2);
		aGraphics.drawLine(cx + 6 - 1, cy + 6 - 2, cx + 6 + 1, cy + 6 - 2);
	}


	protected void paintMinimizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
//		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
//		aGraphics.fill(aBounds);

		if (aArmed)
		{
			Paint p = aGraphics.getPaint();
			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fill(aBounds);
			aGraphics.setPaint(p);
		}

		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
		aGraphics.drawLine(aBounds.x + aBounds.width / 2 - 7, aBounds.y + aBounds.height / 2, aBounds.x + aBounds.width / 2 + 7, aBounds.y + aBounds.height / 2);
	}


	protected void paintMaximizeButton(Graphics2D aGraphics, Rectangle aBounds, boolean aArmed)
	{
//		aGraphics.setColor(mWindowButtonBackground.get(mFocused, aArmed));
//		aGraphics.fill(aBounds);

		if (aArmed)
		{
			Paint p = aGraphics.getPaint();
			aGraphics.setPaint(new LinearGradientPaint(0, aBounds.y, 0, aBounds.y+aBounds.height, new float[]{0f,1f}, new Color[]{new Color(54,101,179),new Color(130,160,208)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
			aGraphics.fill(aBounds);
			aGraphics.setPaint(p);
		}

		aGraphics.setColor(mWindowButtonForeground.get(mFocused, aArmed));
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

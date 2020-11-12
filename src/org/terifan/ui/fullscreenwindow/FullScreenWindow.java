package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.terifan.ui.Anchor;
import org.terifan.ui.ColorSet;
import org.terifan.ui.TextBox;
import org.terifan.ui.Utilities;
import static org.terifan.ui.ColorSet.*;


public class FullScreenWindow extends WindowAdapter implements Border, MouseListener, MouseMotionListener, WindowFocusListener, WindowStateListener, ComponentListener
{
	protected ColorSet mBorderInner = new ColorSet();
	protected ColorSet mBorderOuter = new ColorSet();
	protected ColorSet mTitleBarBackground = new ColorSet();
	protected ColorSet mTitleBarForeground = new ColorSet();

	protected JFrame mFrame;
	protected JDialog mDialog;
	protected final Window mWindow;

	protected int mArmedButton;
	protected int[] mActiveButtons;
	protected int mButtonWidth;
	protected int mButtonHeight;
	protected int mTitleWidth;
	protected boolean mMousePressed;

	protected JComponent mContentPanel;
	protected JPanel mBorderPanel;
	protected Dimension mInitialSize;
	protected Dimension mMinSize;
	protected Dimension mMaxSize;
	protected boolean mMoveWindow;
	protected boolean mFocused;
	protected int mLastKnownNormalWidth;
	protected int mLayoutSize;

	protected boolean mResizeHor;
	protected boolean mResizeVer;
	protected boolean mUndecorated;
	protected boolean mBorderVisible;
	protected int mBorderSize;
	protected int mTitleBarHeight;
	protected Font mTitleBarFont;

	private Rectangle mStartBounds;
	private Point mClickPoint;
	private Integer mCursor;
	private boolean mResize;

	private OnClosedAction mOnClosedAction;
	private OnClosingAction mOnClosingAction;
	private OnResizeAction mOnResizeAction;
	private OnMaximizeAction mOnMaximizeAction;
	private OnMinimizeAction mOnMinimizeAction;
	private OnRestoreAction mOnRestoreAction;
	private OnGainedFocusAction mOnGainedFocusAction;
	private OnLostFocusAction mOnLostFocusAction;
	private BufferedImage mButtonTemplateImage;


	public FullScreenWindow(String aTitle) throws IOException
	{
		this(null, aTitle, false, false, 1);
	}


	public FullScreenWindow(Frame aParent, String aTitle, boolean aDialog, boolean aModal, int aStyle) throws IOException
	{
		mInitialSize = new Dimension(1024, 768);
		mResizeVer = true;
		mResizeHor = true;
		mBorderVisible = true;
		mArmedButton = -1;

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_light.png"));
		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 3;

		setupDarkStyle();

		mContentPanel = new JPanel(new BorderLayout());

		mBorderPanel = new JPanel(new BorderLayout());
		mBorderPanel.add(mContentPanel, BorderLayout.CENTER);
		mBorderPanel.addMouseListener(this);
		mBorderPanel.addMouseMotionListener(this);

		if (aDialog)
		{
			mWindow = mDialog = new JDialog(aParent, aTitle, aModal);
			mDialog.setUndecorated(true);
			mActiveButtons = new int[]{2};
		}
		else
		{
			mWindow = mFrame = new JFrame(aTitle);
			mFrame.setUndecorated(true);
			mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			mActiveButtons = new int[]{0,1,2};
		}

		mWindow.add(mBorderPanel);
		mWindow.setSize(mInitialSize);
		mWindow.setLocationRelativeTo(null);
		mWindow.addComponentListener(this);
		mWindow.addWindowListener(this);
		mWindow.addWindowStateListener(this);
		mWindow.addWindowFocusListener(this);

		mBorderSize = 4;
		mTitleBarHeight = mButtonHeight + mBorderSize;
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 12);

		mMinSize = new Dimension(2 * mBorderSize + 3 * mButtonWidth, mBorderSize + mTitleBarHeight);
		mMaxSize = new Dimension(32000, 32000);

		updateBorder(JFrame.NORMAL, true);
	}


	protected void setupDarkStyle()
	{
		mTitleBarBackground.add(new Color(238,238,242), DEFAULT);
		mTitleBarForeground = new ColorSet().add(new Color(0, 0, 0), DEFAULT);

		mBorderInner.add(new Color(240,240,240), DEFAULT);
		mBorderInner.add(new Color(240,240,240), FOCUSED);
		mBorderOuter.add(new Color(204,206,219), DEFAULT);
		mBorderOuter.add(new Color(155,159,185), FOCUSED);
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


	protected boolean isBorder(Point aPoint)
	{
		return aPoint.x < mLayoutSize - mActiveButtons.length * mButtonWidth && aPoint.y < mBorderSize || aPoint.x < mBorderSize || aPoint.y > mWindow.getHeight() - mBorderSize || aPoint.x > mWindow.getWidth() - mBorderSize;
	}


	private void updateButtons(MouseEvent aEvent)
	{
		int x = aEvent.getX() - mTitleWidth - mBorderSize;
		int y = aEvent.getY();

		mArmedButton = x < 0 || y > mButtonHeight ? -1 : x / mButtonWidth;
		mBorderPanel.repaint();
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


	protected void updateBorder(int aState, boolean aFocused)
	{
		mFocused = aFocused;

		if (!mUndecorated)
		{
			mBorderPanel.setBorder(this);
		}
		else
		{
			mBorderPanel.setBorder(null);
		}

		mBorderPanel.invalidate();
		mBorderPanel.validate();
		mBorderPanel.repaint();
	}


	@Override
	public void paintBorder(Component aComponent, Graphics aGraphics, int aX, int aY, int aWidth, int aHeight)
	{
		if (aWidth != mLayoutSize)
		{
			mLayoutSize = aWidth;
			mTitleWidth = mLayoutSize - mBorderSize - mActiveButtons.length * mButtonWidth - 1;
		}

		boolean maximized = isMaximized();

		if (mFrame != null)
		{
			mActiveButtons = new int[]{0, maximized ? 1 : 3, 2};
		}
		else
		{
			mActiveButtons = new int[]{2};
		}

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		paintBorder(g, aWidth, aHeight, maximized);
		paintTitleBar(g, aWidth, maximized);

		for (int i = 0; i < mActiveButtons.length; i++)
		{
			int sx = mActiveButtons[i] * (mButtonWidth + 1);
			int sy = (mButtonHeight + 1) * (mArmedButton == i ? mMousePressed ? 2 : 1 : 0);
			int dx = mBorderSize + mTitleWidth + i * mButtonWidth;
			int dy = 1;

			g.drawImage(mButtonTemplateImage, dx, dy, dx + mButtonWidth, dy + mButtonHeight, sx, sy, sx + mButtonWidth, sy + mButtonHeight, null);
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


	private void paintBorder(Graphics2D aGraphics, int aWidth, int aHeight, boolean aMaximized)
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


	private void paintTitleBar(Graphics2D aGraphics, int aWidth, boolean aMaximized)
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

		new TextBox(getTitle())
			.setBounds(mBorderSize, 0, mTitleWidth, mButtonHeight)
			.setFont(mTitleBarFont)
			.setForeground(mTitleBarForeground.get(mFocused))
			.setMargins(aMaximized ? 0 : mBorderSize, 4, 0, 0)
			.setAnchor(Anchor.WEST)
			.setMaxLineCount(1)
			.render(aGraphics)
			.measure();
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


	public void toBack()
	{
		mWindow.toBack();
	}


	@Override
	public void mousePressed(MouseEvent aEvent)
	{
		mWindow.requestFocus();

		mStartBounds = mWindow.getBounds();
		mClickPoint = aEvent.getPoint();
		mCursor = getCursor(mClickPoint);
		mResize = !isMaximized() && isBorder(mClickPoint);
		mMousePressed = true;
		mMoveWindow = aEvent.getX() < mBorderSize + mTitleWidth && aEvent.getY() < mTitleBarHeight;

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
		if (mResize)
		{
			Point p = aEvent.getLocationOnScreen();
			p.x -= mStartBounds.x;
			p.y -= mStartBounds.y;
			resizeBox(p);
			return;
		}

		updateButtons(aEvent);

		if (mArmedButton != -1)
		{
			return;
		}

		if (mMoveWindow)
		{
			if (isMaximized())
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
		boolean wasMoving = mMoveWindow;

		Point p = aEvent.getPoint();
		mMoveWindow = false;
		mMousePressed = false;
		mCursor = null;

		mWindow.setCursor(Cursor.getPredefinedCursor(getCursor(p)));

		if (mFrame != null && wasMoving && aEvent.getLocationOnScreen().y == 0)
		{
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			return;
		}

		updateButtons(aEvent);

		switch (mArmedButton)
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
		if (!mMoveWindow)
		{
			mWindow.setCursor(Cursor.getDefaultCursor());

			if (mArmedButton != -1)
			{
				mArmedButton = -1;
				mBorderPanel.repaint();
			}
		}
	}


	@Override
	public void mouseClicked(MouseEvent aE)
	{
	}


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
}

package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.terifan.ui.Anchor;
import org.terifan.ui.ColorSet;
import static org.terifan.ui.ColorSet.FOCUSED;
import org.terifan.ui.TextBox;
import static org.terifan.ui.ColorSet.UNFOCUSED;


public class DefaultWindowBorder
{
	protected ColorSet mBorderInner;
	protected ColorSet mBorderOuter;
	protected ColorSet mTitleBarBackground;
	protected ColorSet mTitleBarForeground;
	protected Font mTitleBarFont;
	protected WindowButtonType[] mButtons;
	protected BufferedImage mButtonTemplateImage;
	protected int mButtonWidth;
	protected int mButtonHeight;
	protected int mBorderSize;
	protected int mTitleBarHeight;
	protected boolean mMaximized;
	protected boolean mBorderPainted;
	protected boolean mWindowFocused;
	protected final Rectangle mBounds;
	protected final Rectangle mButtonBounds;
	protected WindowMenuBar mMenuBar;
	protected WindowTabBar mTabBar;


	public DefaultWindowBorder() throws IOException
	{
		mBounds = new Rectangle();
		mButtonBounds = new Rectangle();

		setupStyle();
	}


	protected void setupStyle() throws IOException
	{
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 13);

		mTitleBarBackground = new ColorSet()
			.add(new Color(255, 255, 255), UNFOCUSED)
			.add(new Color(238, 238, 242), FOCUSED);
		mTitleBarForeground = new ColorSet()
			.add(new Color(0, 0, 0), UNFOCUSED)
			.add(new Color(0, 0, 0), FOCUSED);
		mBorderInner = new ColorSet()
			.add(new Color(255, 255, 255), UNFOCUSED)
			.add(new Color(240, 240, 240), FOCUSED);
		mBorderOuter = new ColorSet()
			.add(new Color(204, 206, 219), UNFOCUSED)
			.add(new Color(155, 159, 185), FOCUSED);

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_1.png"));

		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 4;

		mBorderSize = 4;
	}


	protected void setButtons(WindowButtonType... aButtons)
	{
		mButtons = aButtons;
		mButtonBounds.setSize(mButtonWidth * mButtons.length, mButtonHeight);
		mButtonBounds.x = mBounds.x + mBounds.width - (mBorderPainted ? mBorderSize : 0) - mButtonBounds.width;
		mButtonBounds.y = mBorderPainted ? 1 : 0;
	}


	protected void updateState(boolean aBorderPainted, boolean aMaximized, boolean aWindowFocused)
	{
		mMaximized = aMaximized;
		mWindowFocused = aWindowFocused;
		mBorderPainted = !aMaximized && aBorderPainted;
		mTitleBarHeight = mButtonHeight + mBorderSize;
	}


	protected void paintBorder(FullScreenWindow aWindow, Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, WindowButtonType aHoverButton, WindowButtonType aArmedButton, Point aPointer)
	{
		mBounds.setBounds(aX, aY, aWidth, aHeight);

		mButtonBounds.x = mBounds.x + mBounds.width - (mBorderPainted ? mBorderSize : 0) - mButtonBounds.width;
		mButtonBounds.y = mBorderPainted ? 1 : 0;

		int top = mTitleBarHeight;

		paintTitleBar(aGraphics, aX, aY, aWidth, top, aPointer);
		paintBorder(aGraphics, aX, aY + top, aWidth, aHeight - top, aPointer);
		paintTitleText(aGraphics, aWindow, aX, aY, mButtonBounds.x, top, aPointer);
		paintButtons(aGraphics, aArmedButton, aHoverButton, aPointer);
	}


	protected void paintButtons(Graphics2D aGraphics, WindowButtonType aArmedButton, WindowButtonType aHoverButton, Point aPointer)
	{
		int dx = mButtonBounds.x;
		int dy = mButtonBounds.y;

		for (int i = 0; i < mButtons.length; i++)
		{
			dx += paintButton(aGraphics, aArmedButton, aHoverButton, dx, dy, mButtons[i], aPointer);
		}
	}


	protected int paintButton(Graphics2D aGraphics, WindowButtonType aArmedButton, WindowButtonType aHoverButton, int aDx, int aDy, WindowButtonType aButton, Point aPointer)
	{
		int row = aArmedButton == aButton ? aHoverButton == aButton ? 2 : 1 : aHoverButton == aButton ? 1 : mWindowFocused ? 0 : 3;

		int sx = (mButtonWidth + 1) * aButton.ordinal();
		int sy = (mButtonHeight + 1) * row;

		aGraphics.drawImage(mButtonTemplateImage, aDx, aDy, aDx + mButtonWidth, aDy + mButtonHeight, sx, sy, sx + mButtonWidth, sy + mButtonHeight, null);

		return mButtonWidth;
	}


	protected void paintTitleText(Graphics2D aGraphics, FullScreenWindow aWindow, int aX, int aY, int aWidth, int aHeight, Point aPointer)
	{
		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int s = mBorderSize;

		new TextBox(aWindow.getTitle())
			.setBounds(aX + s, aY + 1, aWidth - s, aHeight - 1)
			.setForeground(mTitleBarForeground.get(mWindowFocused))
			.setFont(mTitleBarFont)
			.setAnchor(Anchor.WEST)
			.setMaxLineCount(1)
			.render(aGraphics);
	}


	protected void paintBorder(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, Point aPointer)
	{
		if (mBorderPainted)
		{
			int s = mBorderSize;

			aGraphics.setColor(mBorderInner.get(mWindowFocused));
			aGraphics.fillRect(aX, aY, s, aHeight);
			aGraphics.fillRect(aX, aY + aHeight - s, aWidth, s);
			aGraphics.fillRect(aWidth - s, aY, s, aHeight);

			aGraphics.setColor(mBorderOuter.get(mWindowFocused));
			aGraphics.drawLine(aX, aY, aX, aY + aHeight - 1);
			aGraphics.drawLine(aX, aY + aHeight - 1, aX + aWidth, aY + aHeight - 1);
			aGraphics.drawLine(aX + aWidth - 1, aY, aX + aWidth - 1, aY + aHeight - 1);
		}
	}


	protected void paintTitleBar(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, Point aPointer)
	{
		aGraphics.setColor(mTitleBarBackground.get(mWindowFocused));
		aGraphics.fillRect(aX, aY, aWidth, aHeight);

		if (mBorderPainted)
		{
			aGraphics.setColor(mBorderOuter.get(mWindowFocused));
			aGraphics.drawLine(aX, aY, aX, aY + aHeight - 1);
			aGraphics.drawLine(aX, aY, aX + aWidth - 1, aY);
			aGraphics.drawLine(aX + aWidth - 1, aY, aX + aWidth - 1, aY + aHeight - 1);
		}
	}


	protected Insets getBorderInsets()
	{
		int s = mBorderPainted ? mBorderSize : 0;
		return new Insets(mTitleBarHeight, s, s, s);
	}


	protected boolean onMouseMotion(Point aMousePoint)
	{
		return true;
	}


	protected WindowButtonType intersectButton(FullScreenWindow aWindow, Point aPoint)
	{
		if (mButtonBounds.contains(aPoint))
		{
			return mButtons[(aPoint.x - mButtonBounds.x) / mButtonWidth];
		}

		return null;
	}


	/**
	 * Test whether or not the Point provided intersects the border and return the direction.
	 *
	 * @return Cursor.DEFAULT_CURSOR if no intersection occurs or one of the directional cursors e.g. Cursor.NW_RESIZE_CURSOR
	 */
	protected BorderIntersectionType intersectBorder(FullScreenWindow aWindow, Point aPoint)
	{
		boolean hor = aWindow.isResizeHorizontal();
		boolean ver = aWindow.isResizeVertical();

		if (!mBorderPainted)
		{
			return BorderIntersectionType.NONE;
		}
		if (ver)
		{
			if (aPoint.y < mBorderSize)
			{
				if (hor)
				{
					if (aPoint.x < mBorderSize)
					{
						return BorderIntersectionType.NORTHWEST;
					}
					if (aPoint.x >= mBounds.width - mBorderSize)
					{
						return BorderIntersectionType.NORTHEAST;
					}
				}
				if (aPoint.y > 0 && aPoint.x > mButtonBounds.x)
				{
					return BorderIntersectionType.NONE;
				}
				return BorderIntersectionType.NORTH;
			}
			if (aPoint.y >= mBounds.height - mBorderSize)
			{
				if (hor)
				{
					if (aPoint.x < mBorderSize)
					{
						return BorderIntersectionType.SOUTHWEST;
					}
					if (aPoint.x >= mBounds.width - mBorderSize)
					{
						return BorderIntersectionType.SOUTHEAST;
					}
				}
				return BorderIntersectionType.SOUTH;
			}
		}
		if (hor)
		{
			if (aPoint.x < mBorderSize)
			{
				return BorderIntersectionType.WEST;
			}
			if (aPoint.x >= mBounds.width - mBorderSize)
			{
				return BorderIntersectionType.EAST;
			}
		}

		return BorderIntersectionType.NONE;
	}


	public WindowMenuBar getMenuBar()
	{
		return mMenuBar;
	}


	public DefaultWindowBorder setMenuBar(WindowMenuBar aMenuBar)
	{
		mMenuBar = aMenuBar;
		return this;
	}


	public WindowTabBar getTabBar()
	{
		return mTabBar;
	}


	public DefaultWindowBorder setTabBar(WindowTabBar aTabBar)
	{
		mTabBar = aTabBar;
		return this;
	}
}

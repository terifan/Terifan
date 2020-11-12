package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
	protected Rectangle mBounds;
	protected boolean mMaximized;
	protected boolean mBorderPainted;
	protected Rectangle mButtonBounds;
	protected boolean mWindowFocused;


	public DefaultWindowBorder() throws IOException
	{
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 12);
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

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_light.png"));
		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 3;

		mBorderSize = 4;

		mBounds = new Rectangle();
		mButtonBounds = new Rectangle();
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
	}


	protected void paintBorder(FullScreenWindow aWindow, Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, WindowButtonType aHoverButton, WindowButtonType aArmedButton)
	{
		mBounds.setBounds(aX, aY, aWidth, aHeight);

		mButtonBounds.x = mBounds.x + mBounds.width - (mBorderPainted ? mBorderSize : 0) - mButtonBounds.width;
		mButtonBounds.y = mBorderPainted ? 1 : 0;

		int top = mButtonHeight + mBorderSize;

		paintTitleBar(aGraphics, aX, aY, aWidth, top);
		paintBorder(aGraphics, aX, aY + top, aWidth, aHeight - top);
		paintTitleText(aGraphics, aWindow, aX, aY, mButtonBounds.x, top);
		paintButtons(aGraphics, aArmedButton, aHoverButton);
	}


	protected void paintButtons(Graphics2D aGraphics, WindowButtonType aArmedButton, WindowButtonType aHoverButton)
	{
		int dx = mButtonBounds.x;
		int dy = mButtonBounds.y;

		for (int i = 0; i < mButtons.length; i++, dx+=mButtonWidth)
		{
			int sx = mButtons[i].ordinal() * (mButtonWidth + 1);
			int sy = (mButtonHeight + 1) * (aArmedButton == mButtons[i] ? aHoverButton == mButtons[i] ? 2 : 0 : aHoverButton == mButtons[i] ? 1 : 0);

			aGraphics.drawImage(mButtonTemplateImage, dx, dy, dx + mButtonWidth, dy + mButtonHeight, sx, sy, sx + mButtonWidth, sy + mButtonHeight, null);
		}
	}


	protected void paintTitleText(Graphics2D aGraphics, FullScreenWindow aWindow, int aX, int aY, int aWidth, int aHeight)
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


	protected void paintBorder(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight)
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


	protected void paintTitleBar(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight)
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
		return new Insets(mButtonHeight + mBorderSize, s, s, s);
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
	 * @return
	 *   Cursor.DEFAULT_CURSOR if no intersection occurs or one of the directional cursors e.g. Cursor.NW_RESIZE_CURSOR
	 */
	protected int intersectBorder(FullScreenWindow aWindow, Point aPoint)
	{
		boolean hor = aWindow.isResizeHorizontal();
		boolean ver = aWindow.isResizeVertical();

		if (!mBorderPainted)
		{
			return Cursor.DEFAULT_CURSOR;
		}
		if (ver)
		{
			if (aPoint.y < mBorderSize)
			{
				if (hor)
				{
					if (aPoint.x < mBorderSize)
					{
						return Cursor.NW_RESIZE_CURSOR;
					}
					if (aPoint.x >= mBounds.width - mBorderSize)
					{
						return Cursor.NE_RESIZE_CURSOR;
					}
				}
				if (aPoint.y > 0 && aPoint.x > mButtonBounds.x)
				{
					return Cursor.DEFAULT_CURSOR;
				}
				return Cursor.N_RESIZE_CURSOR;
			}
			if (aPoint.y >= mBounds.height - mBorderSize)
			{
				if (hor)
				{
					if (aPoint.x < mBorderSize)
					{
						return Cursor.SW_RESIZE_CURSOR;
					}
					if (aPoint.x >= mBounds.width - mBorderSize)
					{
						return Cursor.SE_RESIZE_CURSOR;
					}
				}
				return Cursor.S_RESIZE_CURSOR;
			}
		}
		if (hor)
		{
			if (aPoint.x < mBorderSize)
			{
				return Cursor.W_RESIZE_CURSOR;
			}
			if (aPoint.x >= mBounds.width - mBorderSize)
			{
				return Cursor.E_RESIZE_CURSOR;
			}
		}

		return Cursor.DEFAULT_CURSOR;
	}


	/**
	 * Test whether or not the Point provided intersects the border allowing the window to be dragged.
	 */
	protected boolean intersectDragHandle(FullScreenWindow aWindow, Point aPoint)
	{
		int top = mButtonHeight + mBorderSize;
		if (mBorderPainted)
		{
			return aPoint.x >= mBorderSize && aPoint.y >= mBorderSize && aPoint.x < mButtonBounds.x && aPoint.y < top;
		}
		return aPoint.x < mButtonBounds.x && aPoint.y < top;
	}
}

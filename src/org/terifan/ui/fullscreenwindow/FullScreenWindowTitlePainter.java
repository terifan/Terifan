package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
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
import static org.terifan.ui.ColorSet.DEFAULT;
import static org.terifan.ui.ColorSet.FOCUSED;
import org.terifan.ui.TextBox;


public class FullScreenWindowTitlePainter
{
	protected ColorSet mBorderInner = new ColorSet();
	protected ColorSet mBorderOuter = new ColorSet();
	protected ColorSet mTitleBarBackground = new ColorSet();
	protected ColorSet mTitleBarForeground = new ColorSet();
	protected Font mTitleBarFont;
	protected Insets mInsets;
	protected WindowButtonType[] mButtons;
	private BufferedImage mButtonTemplateImage;
	protected int mButtonWidth;
	protected int mButtonHeight;
	protected int mBorderSize;
	private Rectangle mBounds;
	private boolean mMaximized;
	private boolean mBorderPainted;
	private Rectangle mButtonBounds;
	private boolean mWindowFocused;


	public FullScreenWindowTitlePainter() throws IOException
	{
		mTitleBarBackground.add(new Color(238,238,242), DEFAULT);
		mTitleBarForeground = new ColorSet().add(new Color(0, 0, 0), DEFAULT);
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 12);

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_light.png"));
		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 3;

		mBorderSize = 4;

		mBorderInner.add(new Color(240,240,240), DEFAULT);
		mBorderInner.add(new Color(240,240,240), FOCUSED);
		mBorderOuter.add(new Color(204,206,219), DEFAULT);
		mBorderOuter.add(new Color(155,159,185), FOCUSED);

		mBounds = new Rectangle();
		mButtonBounds = new Rectangle();

		mInsets = new Insets(0,0,0,0);
		mInsets.set(mButtonHeight + mBorderSize, mBorderSize, mBorderSize, mBorderSize);
	}


	public void setButtons(WindowButtonType... aButtons)
	{
		mButtons = aButtons;
		mButtonBounds.setSize(mButtonWidth * mButtons.length, mButtonHeight);
		mButtonBounds.x = mBounds.x + mBounds.width - (mBorderPainted ? mBorderSize : 0) - mButtonBounds.width;
		mButtonBounds.y = mBorderPainted ? 1 : 0;
	}


	public void paintBorder(FullScreenWindow aWindow, Graphics2D aGraphics, boolean aBorderPainted, boolean aMaximized, boolean aWindowFocused, int aX, int aY, int aWidth, int aHeight, WindowButtonType aHoverButton, WindowButtonType aArmedButton)
	{
		mMaximized = aMaximized;
		mWindowFocused = aWindowFocused;
		mBorderPainted = !mMaximized && aBorderPainted;
		mBounds.setBounds(aX, aY, aWidth, aHeight);
		mInsets.set(mButtonHeight + (aBorderPainted ? mBorderSize : 0), mBorderSize, mBorderSize, mBorderSize);

		mButtonBounds.x = mBounds.x + mBounds.width - (mBorderPainted ? mBorderSize : 0) - mButtonBounds.width;
		mButtonBounds.y = mBorderPainted ? 1 : 0;

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		aGraphics.setColor(mBorderInner.get(mWindowFocused));
		aGraphics.fillRect(mBounds.x + mBorderSize, mBounds.y, mButtonBounds.x - mBounds.x, mInsets.top);

		if (mBorderPainted)
		{
			aGraphics.setColor(mBorderInner.get(mWindowFocused));
			aGraphics.fillRect(0, mBorderSize, mBorderSize, mBounds.height - mBorderSize - mBorderSize);
			aGraphics.fillRect(0, mBounds.height - mBorderSize, mBounds.width, mBorderSize);
			aGraphics.fillRect(mBounds.width - mBorderSize, mBorderSize, mBorderSize, mBounds.height - mBorderSize - mBorderSize);

			aGraphics.setColor(mBorderOuter.get(mWindowFocused));
			aGraphics.drawRect(0, 0, mBounds.width - 1, mBounds.height - 1);
		}

		new TextBox(aWindow.getTitle())
			.setBounds(aX + mBorderSize, aY + 1, mButtonBounds.x - mBorderSize, mButtonHeight + mBorderSize - 1)
			.setForeground(mTitleBarForeground.get(aWindowFocused))
			.setFont(mTitleBarFont)
			.setAnchor(Anchor.WEST)
			.setMaxLineCount(1)
			.render(aGraphics);

		for (int i = 0; i < mButtons.length; i++)
		{
			int sx = mButtons[i].ordinal() * (mButtonWidth + 1);
			int sy = (mButtonHeight + 1) * (aArmedButton == mButtons[i] ? aHoverButton == mButtons[i] ? 2 : 0 : aHoverButton == mButtons[i] ? 1 : 0);
			int dx = mButtonBounds.x + i * mButtonWidth;
			int dy = mButtonBounds.y;

			aGraphics.drawImage(mButtonTemplateImage, dx, dy, dx + mButtonWidth, dy + mButtonHeight, sx, sy, sx + mButtonWidth, sy + mButtonHeight, null);
		}
	}


	public Insets getBorderInsets()
	{
		return mInsets;
	}


	public WindowButtonType intersectButton(FullScreenWindow aWindow, Point aPoint)
	{
		if (mButtonBounds.contains(aPoint))
		{
			return mButtons[(aPoint.x - mButtonBounds.x) / mButtonWidth];
		}

		return null;
	}


	public int intersectBorder(FullScreenWindow aWindow, Point aPoint)
	{
		boolean resizeHor = aWindow.isResizeHorizontal();
		boolean resizeVer = aWindow.isResizeVertical();

		if (!resizeHor && !resizeVer || mMaximized)
		{
			return Cursor.DEFAULT_CURSOR;
		}
		if (resizeVer)
		{
			if (aPoint.y < mBorderSize)
			{
				if (resizeHor)
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
				if (resizeHor)
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
		if (resizeHor)
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


	boolean intersectDragHandle(FullScreenWindow aWindow, Point aPoint)
	{
		return
			    mBorderPainted && aPoint.x >= mBorderSize && aPoint.y >= mBorderSize && aPoint.x < mButtonBounds.x && aPoint.y < mInsets.top
			|| !mBorderPainted && aPoint.x < mButtonBounds.x && aPoint.y < mInsets.top;
	}
}
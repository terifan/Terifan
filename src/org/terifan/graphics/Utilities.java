package org.terifan.graphics;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


public final class Utilities
{
	private Utilities()
	{
	}


	/**
	 * Clips the string provided to specified length.
	 */
	public static String clipString(String aString, FontMetrics aFontMetrics, int aLength)
	{
		aString = aString.trim();

		if (aString.isEmpty() || aLength == 0)
		{
			return "";
		}

		if (aFontMetrics.stringWidth(aString) < aLength)
		{
			return aString;
		}

		char[] chars = (aString + "..").toCharArray();
		int len = aString.length() + 2;

		for (;len > 0; len--)
		{
			if (len > 3)
			{
				chars[len - 3] = '.';
			}

			if (aFontMetrics.charsWidth(chars, 0, len) < aLength)
			{
				break;
			}
		}

		return new String(chars, 0, len);
	}


	/**
	 * Draws a dotted rectangle.
	 */
	public static void drawDottedRect(Graphics aGraphics, int x, int y, int w, int h, boolean aAlign)
	{
		if (aAlign)
		{
			 w |= 1;
			 h |= 1;
		}

		int j = 0;
		for (int i = 0; i < w; i++, j++, x++)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		x--;
		j++;
		for (int i = 0; i < h; i++, j++, y++)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		y--;
		j++;
		for (int i = 0; i < w; i++, j++, x--)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		x++;
		j++;
		for (int i = 0; i < h; i++, j++, y--)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
	}


	/**
	 * Draws a scaled image and it's frame border that is resized in only one
	 * direction. The center area of the image is resized in both directions.
	 *
	 * @param aGraphics
	 *   draws on this Graphics context
	 * @param aImage
	 *   the image to draw
	 * @param aPositionX
	 *   offset x
	 * @param aPositionY
	 *   offset y
	 * @param aWidth
	 *   width including frame width
	 * @param aHeight
	 *   height including frame height
	 * @param aFrameLeft
	 *   left frame width
	 * @param aFrameRight
	 *   right frame width
	 */
	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameLeft, int aFrameRight)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX+aFrameLeft, aPositionY+aHeight, 0, 0, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY, aPositionX+aWidth-aFrameRight, aPositionY+aHeight, aFrameLeft, 0, tw-aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY, aPositionX+aWidth, aPositionY+aHeight, tw-aFrameRight, 0, tw, th, null);
	}


	/**
	 * Draws a scaled image and it's frame border that is resized in only one
	 * direction. The center area of the image is resized in both directions.
	 *
	 * @param aGraphics
	 *   draws on this Graphics context
	 * @param aImage
	 *   the image to draw
	 * @param aPositionX
	 *   offset x
	 * @param aPositionY
	 *   offset y
	 * @param aWidth
	 *   width including frame width
	 * @param aHeight
	 *   height including frame height
	 * @param aFrameTop
	 *   top frame height
	 * @param aFrameLeft
	 *   left frame width
	 * @param aFrameBottom
	 *   bottom frame height
	 * @param aFrameRight
	 *   right frame width
	 */
	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameTop, int aFrameLeft, int aFrameBottom, int aFrameRight)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX+aFrameLeft, aPositionY+aFrameTop, 0, 0, aFrameLeft, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY, aPositionX+aWidth-aFrameRight, aPositionY+aFrameTop, aFrameLeft, 0, tw-aFrameRight, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY, aPositionX+aWidth, aPositionY+aFrameTop, tw-aFrameRight, 0, tw, aFrameTop, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY+aFrameTop, aPositionX+aFrameLeft, aPositionY+aHeight-aFrameBottom, 0, aFrameTop, aFrameLeft, th-aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY+aFrameTop, aPositionX+aWidth-aFrameRight, aPositionY+aHeight-aFrameBottom, aFrameLeft, aFrameTop, tw-aFrameRight, th-aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY+aFrameTop, aPositionX+aWidth, aPositionY+aHeight-aFrameBottom, tw-aFrameRight, aFrameTop, tw, th-aFrameBottom, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY+aHeight-aFrameBottom, aPositionX+aFrameLeft, aPositionY+aHeight, 0, th-aFrameBottom, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY+aHeight-aFrameBottom, aPositionX+aWidth-aFrameRight, aPositionY+aHeight, aFrameLeft, th-aFrameBottom, tw-aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY+aHeight-aFrameBottom, aPositionX+aWidth, aPositionY+aHeight, tw-aFrameRight, th-aFrameBottom, tw, th, null);
	}


	public static void enableTextAntialiasing(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		}
	}


	public static void enableAntialiasing(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}


	public static void enableBilinear(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
	}
}
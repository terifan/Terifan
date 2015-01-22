package org.terifan.ui.listview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import org.terifan.forms.Anchor;


/**
 * @deprecated use org.terifan.ui.TextBox instead
 */
@Deprecated
public class TextRenderer
{
	private static FontRenderContext FRC = new FontRenderContext(new AffineTransform(), true, false);


	public TextRenderer()
	{
	}


	public int drawString(Graphics aGraphics, Color aBackground, Color aForeground, String aText, int aRectX, int aRectY, int aTextOffsetY)
	{
		Font font = aGraphics.getFont();
		LineMetrics lm = font.getLineMetrics(aText, FRC);
		int w = (int)font.getStringBounds(aText, FRC).getWidth();
		int lh = (int)(lm.getHeight()-lm.getDescent());

		if (aTextOffsetY == 0)
		{
			aTextOffsetY = (int)lm.getHeight();
		}

		drawString(aGraphics, aBackground, aForeground, aText, aRectX, aRectY, w, aTextOffsetY, 0, lh);

		return lh;
	}


	public void drawString(Graphics aGraphics, Color aBackground, Color aForeground, String aText, int aRectX, int aRectY, int aRectWidth, int aRectHeight, int aOffsetX, int aOffsetY)
	{
		if (aRectHeight <= 0 || aRectWidth <= 0)
		{
			return;
		}

		Font font = aGraphics.getFont();
		Graphics2D g = (Graphics2D)aGraphics;

		if (aBackground != null)
		{
			g.setColor(aBackground);
			g.fillRect(aRectX, aRectY, aRectWidth, aRectHeight);
		}

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(aForeground);
		g.setFont(font);
		g.drawString(aText, aRectX, aRectY + aOffsetY);
	}


	/**
	 * Draws a string within the bounds specified.
	 *
	 * @param aGraphics
	 *   draw on this context
	 * @param aString
	 *   the string to draw
	 * @param aRectX
	 *   x offset
	 * @param aRectY
	 *   y offset
	 * @param aRectWidth
	 *   width of the bounds to draw within
	 * @param aRectHeight
	 *   height of the bounds to draw within
	 * @param aAnchor
	 *   a Anchor value specifying the orientation of the text.
	 * @param aForeground
	 *   Text color to use or null
	 * @param aBackground
	 *   Text background or null
	 * @param aMultiline
	 *   True if long lines should be split
	 * @return
	 *
	 */
	public Rectangle drawString(Graphics aGraphics, String aString, int aRectX, int aRectY, int aRectWidth, int aRectHeight, Anchor aAnchor, Color aForeground, Color aBackground, boolean aMultiline)
	{
		if (aString == null)
		{
			throw new IllegalArgumentException("Text is null.");
		}

		Font font = aGraphics.getFont();

		if (aForeground == null && aBackground != null)
		{
			aForeground = aGraphics.getColor();
		}

		ArrayList<String> list;

		if (aMultiline)
		{
			list = lineBreakText(aString, font, aRectWidth);
		}
		else
		{
			list = new ArrayList<>();
			list.add(clipString(aString, font, aRectWidth));
		}

		LineMetrics lm = font.getLineMetrics("Adgj", FRC);
		int lineHeight = (int)Math.ceil(lm.getHeight());

		Rectangle bounds = null;

		int lineCount = Math.min(list.size(), aRectHeight / lineHeight);

		if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
		{
			aRectY += Math.max(0, aRectHeight-lineCount*lineHeight);
		}
		else if (aAnchor == Anchor.CENTER || aAnchor == Anchor.WEST || aAnchor == Anchor.EAST)
		{
			aRectY += Math.max(0, (aRectHeight-lineCount*lineHeight)/2);
		}

		for (int i = 0; i < lineCount; i++)
		{
			String str = list.get(i);

			int x = aRectX;
			int w = getStringLength(str, font);

			if (aAnchor == Anchor.NORTH || aAnchor == Anchor.CENTER || aAnchor == Anchor.SOUTH)
			{
				x += (aRectWidth-w)/2;
			}
			else if (aAnchor == Anchor.NORTH_EAST || aAnchor == Anchor.EAST || aAnchor == Anchor.SOUTH_EAST)
			{
				x += aRectWidth-w;
			}

			int y = aRectY+i*lineHeight;

			drawString(aGraphics, aBackground, aForeground, str, x, y, lineHeight);

			if (bounds == null)
			{
				bounds = new Rectangle(x, y, w, lineHeight);
			}
			else
			{
				bounds.add(x, y);
				bounds.add(x+w, y+lineHeight);
			}
		}

		return bounds;
	}


	private static int getStringLength(String aString, Font aFont)
	{
		return (int)aFont.getStringBounds(aString, FRC).getWidth();
	}


	public static ArrayList<String> lineBreakText(String aString, Font aFont, int aWidth)
	{
		ArrayList<String> list = new ArrayList<>();

		for (String str : aString.split("\n"))
		{
			do
			{
				int w = getStringLength(str, aFont);
				String tmp;

				if (w > aWidth)
				{
					int offset = findStringLimit(str, aFont, aWidth);
					int temp = Math.max(str.lastIndexOf(' ', offset), Math.max(str.lastIndexOf('.', offset), Math.max(str.lastIndexOf('-', offset), str.lastIndexOf('_', offset))));
					offset = Math.max(1, temp > 1 ? temp : offset);

					tmp = str.substring(0,offset);
					str = str.substring(offset).trim();
				}
				else
				{
					tmp = str.trim();
					str = "";
				}

				list.add(tmp.trim());
			}
			while (str.length() > 0);
		}

		return list;
	}


	private static int findStringLimit(String aString, Font aFont, int aWidth)
	{
		int min = 0;
		int max = aString.length();

		while (Math.abs(min-max) > 1)
		{
			int mid = (max+min)/2;

			int w = getStringLength(aString.substring(0,mid), aFont);

			//System.out.printf("%d\t%d\t%d\t%d\t%d\n", min, max, mid, aWidth, w);

			if (w > aWidth)
			{
				max = mid;
			}
			else
			{
				min = mid;
			}
		}

		return min;
	}


	public static String clipString(String aString, Font aFont, int aLength)
	{
		aString = aString.trim();

		if (aString.isEmpty() || aLength == 0)
		{
			return "";
		}

		if (aFont.getStringBounds(aString, FRC).getWidth() < aLength)
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

			if (aFont.getStringBounds(chars, 0, len, FRC).getWidth() < aLength)
			{
				break;
			}
		}

		return new String(chars, 0, len);
	}
}
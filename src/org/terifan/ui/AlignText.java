package org.terifan.ui;

import java.awt.Graphics;
import java.awt.Point;


public final class AlignText
{
	public static Point align(Graphics aGraphics, Anchor aAnchor, int aWidth, int aHeight, String aText)
	{
		int x, y;

		switch (aAnchor)
		{
			case NORTH_WEST:
			case NORTH:
			case NORTH_EAST:
				y = 0;
				break;
			case WEST:
			case CENTER:
			case EAST:
				y = aHeight / 2 + aGraphics.getFontMetrics().getDescent();
				break;
			default:
				y = aHeight - aGraphics.getFontMetrics().getAscent();
				break;
		}

		switch (aAnchor)
		{
			case NORTH_WEST:
			case WEST:
			case SOUTH_WEST:
				x = 0;
				break;
			case NORTH:
			case CENTER:
			case SOUTH:
				x = (aWidth - (int)aGraphics.getFontMetrics().getStringBounds(aText, aGraphics).getWidth()) / 2;
				break;
			default:
				x = aWidth - (int)aGraphics.getFontMetrics().getStringBounds(aText, aGraphics).getWidth();
				break;
		}

		return new Point(x, y);
	}
}

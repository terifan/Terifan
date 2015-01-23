package org.terifan.ui.listview.layout;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.SortOrder;
import org.terifan.ui.Icon;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewColumn;
import org.terifan.ui.listview.ListViewHeaderRenderer;


public class ColumnHeaderRenderer implements ListViewHeaderRenderer
{
	protected boolean mExtendLastItem;


	public ColumnHeaderRenderer()
	{
	}


	public void setExtendLastItem(boolean aExtendLastItem)
	{
		mExtendLastItem = aExtendLastItem;
	}


	@Override
	public boolean getExtendLastItem()
	{
		return mExtendLastItem;
	}


	@Override
	public void paintRowHeader(ListView aListView, Graphics aGraphics, int x, int y, int w, int h, boolean aIsSelected, boolean aIsArmed, boolean aIsRollover)
	{
	}


	@Override
	public void paintColumnHeader(ListView aListView, ListViewColumn aColumn, Graphics aGraphics, int x, int y, int w, int h, boolean aIsSelected, boolean aIsArmed, boolean aIsRollover, SortOrder aSorting, boolean aFirstColumn, boolean aLastColumn)
	{
		Utilities.enableTextAntialiasing(aGraphics);
		StyleSheet style = aListView.getStylesheet();

		aGraphics.setFont(style.getFont("header"));

		FontMetrics fontMetrics = aGraphics.getFontMetrics(aGraphics.getFont());

		if (aIsRollover && aIsArmed)
		{
			BufferedImage background = style.getScaledImage("headerBackgroundRolloverArmed", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
		else if (aIsArmed)
		{
			BufferedImage background = style.getScaledImage("headerBackgroundArmed", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
		else if (aIsRollover)
		{
			BufferedImage background = style.getScaledImage("headerBackgroundRollover", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
		else
		{
			BufferedImage background = style.getScaledImage("headerBackground", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}

		Icon sortIcon = style.getIcon(aSorting == SortOrder.ASCENDING ? "arrowAscending" : "arrowDescending");

		int iw = aSorting != SortOrder.UNSORTED ? sortIcon.getIconWidth()+10 : 0;
		String label = Utilities.clipString(aColumn.getLabel(), fontMetrics, Math.max(w - iw - 10,1));
		int cw = fontMetrics.stringWidth(label);

		if (aSorting != SortOrder.UNSORTED)
		{
			cw += 10+sortIcon.getIconWidth();
		}

		int z = x;

		switch (aColumn.getAlignment())
		{
			case LEFT:
				z += 5;
				break;
			case CENTER:
				z += Math.max((w-cw)/2,5);
				break;
			case RIGHT:
				z += Math.max(w-cw-5,5);
				break;
			default:
				throw new RuntimeException("Unsupported alignment: " + aColumn.getAlignment());
		}

		aGraphics.setColor(aIsArmed ? style.getColor("headerForegroundArmed") : style.getColor("headerForeground"));
		aGraphics.drawString(label, z+(aIsArmed?1:0), y+(aIsArmed?1:0) + (h + fontMetrics.getHeight()) / 2 - fontMetrics.getDescent());
		z += fontMetrics.stringWidth(label);

		if (aSorting != SortOrder.UNSORTED)
		{
			z = Math.min(z + 10, z + w - 10 - sortIcon.getIconWidth());
			sortIcon.paintIcon(null, aGraphics, z+(aIsArmed?1:0), (h - sortIcon.getIconHeight()) / 2+(aIsArmed?1:0));
		}

		if (!aLastColumn || !getExtendLastItem())
		{
			if (aIsArmed)
			{
				BufferedImage background = style.getScaledImage("headerSeperatorArmed", 1, h-1);
				aGraphics.drawImage(background, x+w-1, y, null);
			}
			else
			{
				BufferedImage background = style.getScaledImage("headerSeperator", 1, h-1);
				aGraphics.drawImage(background, x+w-1, y, null);
			}
		}
	}


	@Override
	public void paintColumnHeaderLeading(ListView aListView, Graphics aGraphics, int x, int y, int w, int h)
	{
		if (w > 0 && h > 1)
		{
			StyleSheet style = aListView.getStylesheet();

			BufferedImage background = style.getScaledImage("headerBackground", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
	}


	@Override
	public void paintColumnHeaderTrailing(ListView aListView, Graphics aGraphics, int x, int y, int w, int h)
	{
		if (w > 0 && h > 1)
		{
			StyleSheet style = aListView.getStylesheet();

			BufferedImage background = style.getScaledImage("headerBackground", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
	}


	@Override
	public void paintUpperLeftCorner(ListView aListView, Graphics aGraphics, int x, int y, int w, int h)
	{
		if (w > 0 && h > 1)
		{
			StyleSheet style = aListView.getStylesheet();

			BufferedImage background = style.getScaledImage("headerBackground", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
	}


	@Override
	public void paintUpperRightCorner(ListView aListView, Graphics aGraphics, int x, int y, int w, int h)
	{
		if (w > 0 && h > 1)
		{
			StyleSheet style = aListView.getStylesheet();

			BufferedImage background = style.getScaledImage("headerBackground", w, h-1);
			aGraphics.drawImage(background, x, y, null);
			aGraphics.setColor(style.getColor("headerBorder"));
			aGraphics.drawLine(x, y+h-1, x+w, y+h-1);
		}
	}


	@Override
	public int getColumnHeaderHeight(ListView aListView)
	{
		return aListView.getStylesheet().getInt("headerColumnHeight");
	}


	@Override
	public int getRowHeaderWidth()
	{
		return 0;
	}
}
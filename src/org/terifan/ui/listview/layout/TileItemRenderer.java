package org.terifan.ui.listview.layout;

import org.terifan.ui.listview.ListViewLayoutVertical;
import org.terifan.ui.listview.ListViewLayoutHorizontal;
import org.terifan.ui.Orientation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.terifan.ui.Anchor;
import org.terifan.ui.Icon;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewColumn;
import org.terifan.ui.listview.ListViewItem;
import org.terifan.ui.listview.ListViewItemRenderer;
import org.terifan.ui.listview.ListViewLayout;
import org.terifan.ui.listview.ListViewModel;


public class TileItemRenderer implements ListViewItemRenderer
{
	private static FontRenderContext FRC = new FontRenderContext(new AffineTransform(), false, false);
	private final static int PADDING_HEIGHT = 20;

	private Dimension mItemSize;
	private Orientation mOrientation;
	private int mIconWidth;


	/**
	 * @param aItemWidth
	 *   Preferred width of an item.
	 * @param aItemHeight
	 *   Preferred height of an item.
	 * @param aOrientation
	 *
	 */
	public TileItemRenderer(Dimension aItemSize, int aIconWidth, Orientation aOrientation)
	{
		mItemSize = aItemSize;
		mOrientation = aOrientation;
		mIconWidth = aIconWidth;
	}


	@Override
	public int getItemPreferredWidth(ListView aListView)
	{
		return mItemSize.width;
	}


	@Override
	public int getItemMaximumWidth(ListView aListView)
	{
		return mItemSize.width;
	}


	@Override
	public int getItemMinimumWidth(ListView aListView)
	{
		return mItemSize.width;
	}


	@Override
	public int getItemPreferredHeight(ListView aListView)
	{
		return mItemSize.height;
	}


	@Override
	public int getItemMaximumHeight(ListView aListView)
	{
		return mItemSize.height;
	}


	@Override
	public int getItemMinimumHeight(ListView aListView)
	{
		return mItemSize.height;
	}


	@Override
	public int getItemWidth(ListView aListView, ListViewItem aItem)
	{
		return mItemSize.width;
	}


	@Override
	public int getItemHeight(ListView aListView, ListViewItem aItem)
	{
		return mItemSize.height;
	}


	@Override
	public void paintItem(Graphics2D aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, ListViewItem aItem)
	{
		StyleSheet style = aListView.getStylesheet();
		ListViewModel model = aListView.getModel();
		boolean selected = aListView.isItemSelected(aItem);

		aGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		aOriginX += 1;
		aOriginY += 1;
		aWidth -= 2;
		aHeight -= 2;

		if (selected)
		{
			BufferedImage im = style.getScaledImage("thumbBorderSelectedBackground", aWidth, aHeight, 3, 3, 3, 3);
			aGraphics.drawImage(im, aOriginX, aOriginY, null);
		}

		{
			int x = aOriginX+3;
			int y = aOriginY+3;
			int w = mIconWidth + 10;
			int h = aHeight-10;

			Icon icon = aItem.getIcon(aListView.getModel().getColumn(0));
			boolean drawBorder = icon != null;

			int tw = mIconWidth;
			int th = mItemSize.height-20;

			if (icon == null)
			{
				icon = style.getScaledIcon("thumbPlaceholder", tw, th, true);
			}

			double f = Math.min(tw/(double)icon.getIconWidth(), th/(double)icon.getIconHeight());
			tw = (int)(f*icon.getIconWidth());
			th = (int)(f*icon.getIconHeight());

			int tx = x+(w-tw)/2;
			int ty = y+(h-th)/2;

			if (drawBorder)
			{
				BufferedImage im = style.getScaledImage(selected ? "thumbBorderSelected" : "thumbBorderUnselected", tw+3+6, th+3+7, 3, 3, 7, 6);
				aGraphics.drawImage(im, tx-3, ty-3, null);
			}

			icon.paintIcon(aListView, aGraphics, tx, ty, tw, th);
		}

		LineMetrics lm = aGraphics.getFont().getLineMetrics("Adgj", FRC);
		int lineHeight = (int)lm.getHeight() + 1;

		int itemHeight = aHeight - 4;

		int x = aOriginX + mIconWidth + 16;
		int y = 4;

		for (int col = 0; col < model.getColumnCount(); col++)
		{
			ListViewColumn column = model.getColumn(col);

			Object label = aItem.getValue(column);
			if (column.getFormatter() != null)
			{
				label = column.getFormatter().format(label);
			}

			if (label != null && y+lineHeight < itemHeight && label.toString().length() > 0)
			{
				Rectangle dim = aListView.getTextRenderer().drawString(aGraphics, label.toString(), x, aOriginY+y, aWidth-5-16-mIconWidth, itemHeight-y, Anchor.NORTH_WEST, col != 0 ? Color.GRAY : style.getColor("itemForeground"), style.getColor("itemBackground"), false);

				y += 1 + dim.height;

				if (col == 0)
				{
					y += 1;
				}
			}
		}

		if (aListView.getFocusItem() == aItem)
		{
			Utilities.drawDottedRect(aGraphics, aOriginX+1, aOriginY+1, aWidth-2, aHeight-2, false);
		}
	}


	@Override
	public ListViewLayout createListViewLayout(ListView aListView)
	{
		if (mOrientation == Orientation.VERTICAL)
		{
			return new ListViewLayoutVertical(aListView, 5);
		}
		else
		{
			return new ListViewLayoutHorizontal(aListView);
		}
	}
}
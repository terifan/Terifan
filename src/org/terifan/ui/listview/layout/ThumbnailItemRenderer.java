package org.terifan.ui.listview.layout;

import org.terifan.ui.listview.ListViewLayoutHorizontal;
import org.terifan.ui.listview.ListViewLayoutVertical;
import org.terifan.forms.Orientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.terifan.forms.Anchor;
import org.terifan.forms.Icon;
import org.terifan.graphics.Utilities;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewItem;
import org.terifan.ui.listview.ListViewItemRenderer;
import org.terifan.ui.listview.ListViewLayout;


public class ThumbnailItemRenderer implements ListViewItemRenderer
{
	public final static int DEFAULT_LABEL_HEIGHT = 15;
	public final static int ITEM_PAD_HOR = 20;
	public final static int ITEM_PAD_VER = 20;
	public final static int ITEM_SPACE_HOR = 4;
	public final static int ITEM_SPACE_VER = 4;


	private Dimension mItemSize;
	private Orientation mOrientation;
	private int mLabelHeight;


	public ThumbnailItemRenderer(Dimension aItemSize, Orientation aOrientation)
	{
		this(aItemSize, aOrientation, DEFAULT_LABEL_HEIGHT);
	}


	public ThumbnailItemRenderer(Dimension aItemSize, Orientation aOrientation, int aLabelHeight)
	{
		mItemSize = aItemSize;
		mOrientation = aOrientation;
		mLabelHeight = aLabelHeight;
	}


	@Override
	public int getItemMinimumWidth(ListView aListView)
	{
		return getItemPreferredWidth(aListView);
	}


	@Override
	public int getItemMaximumWidth(ListView aListView)
	{
		return getItemPreferredWidth(aListView);
	}


	@Override
	public int getItemPreferredWidth(ListView aListView)
	{
		return mItemSize.width + ITEM_PAD_HOR + ITEM_SPACE_HOR;
	}


	@Override
	public int getItemMinimumHeight(ListView aListView)
	{
		return getItemPreferredHeight(aListView);
	}


	@Override
	public int getItemMaximumHeight(ListView aListView)
	{
		return getItemPreferredHeight(aListView);
	}


	@Override
	public int getItemPreferredHeight(ListView aListView)
	{
		return mItemSize.height + ITEM_PAD_VER + ITEM_SPACE_VER + mLabelHeight;
	}


	@Override
	public int getItemWidth(ListView aListView, ListViewItem aItem)
	{
		return getItemPreferredWidth(aListView);
	}


	@Override
	public int getItemHeight(ListView aListView, ListViewItem aItem)
	{
		return getItemPreferredHeight(aListView);
	}


	@Override
	public void paintItem(Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, ListViewItem aItem)
	{
		StyleSheet style = aListView.getStylesheet();
		boolean selected = aListView.isItemSelected(aItem);

		int x = aOriginX;
		int y = aOriginY;
		int w = aWidth;
		int h = aHeight;

		int sw = mItemSize.width + ITEM_PAD_HOR;
		int sh = mItemSize.height + mLabelHeight + ITEM_PAD_VER;
		int sx = x+(w-sw)/2;
		int sy = y+h-sh;

		Icon icon = aItem.getIcon(0);

		boolean drawBorder = icon != null && aItem.getRenderingHint(ListViewRenderingHints.KEY_DRAW_BORDER) != ListViewRenderingHints.VALUE_DRAW_BORDER_OFF;

		if (icon == null)
		{
			icon = style.getScaledIcon("thumbPlaceholder", mItemSize.width, mItemSize.height, true);
		}

		double f = Math.min(mItemSize.width / (double)icon.getIconWidth(), mItemSize.height / (double)icon.getIconHeight());
		int tw = (int)(f * icon.getIconWidth());
		int th = (int)(f * icon.getIconHeight());
		int tx = x + (w - tw) / 2;
		int ty = y + h - 8 - th - mLabelHeight;

		if (selected)
		{
			BufferedImage im = style.getScaledImage("thumbBorderSelectedBackground", sw, sh, 3, 3, 3, 3);
			aGraphics.drawImage(im, sx, sy, null);
		}

		if (drawBorder)
		{
//			boolean ninePatch = style.getBoolean("thumbBorderNinePatch", false);
//			if (ninePatch)
//			{
//			}
//			else
			{
				BufferedImage im = style.getScaledImage(selected ? "thumbBorderSelected" : "thumbBorderUnselected", tw+3+6, th+3+7, 3, 3, 7, 6);
				aGraphics.drawImage(im, tx-3, ty-3, null);
			}
		}

		icon.paintIcon(null, aGraphics, tx, ty, tw, th);

		Object label = aItem.getValue(0);

		if (label != null && mLabelHeight > 0)
		{
			if (aListView.getModel().getColumn(0).getFormatter() != null)
			{
				label = aListView.getModel().getColumn(0).getFormatter().format(label);
			}

			aListView.getTextRenderer().drawString(aGraphics, label.toString(), x+2, y+h-mLabelHeight-2, w-4, mLabelHeight, Anchor.NORTH, style.getColor("itemForeground"), null, true);
		}

		if (aListView.getFocusItem() == aItem)
		{
			Utilities.drawDottedRect(aGraphics, sx+1, sy+1, sw-2, sh-2, false);
		}
	}


	@Override
	public ListViewLayout createListViewLayout(ListView aListView)
	{
		if (mOrientation == Orientation.VERTICAL)
		{
			return new ListViewLayoutVertical(aListView, 100);
		}
		else
		{
			return new ListViewLayoutHorizontal(aListView);
		}
	}
}
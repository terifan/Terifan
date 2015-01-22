package org.terifan.ui.listview.layout;

import org.terifan.ui.listview.ListViewLayoutVertical;
import org.terifan.ui.listview.ListViewLayoutHorizontal;
import org.terifan.forms.Orientation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import org.terifan.forms.Anchor;
import org.terifan.ui.StyleSheet;
import org.terifan.graphics.Utilities;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewColumn;
import org.terifan.ui.listview.ListViewItem;
import org.terifan.ui.listview.ListViewItemRenderer;
import org.terifan.ui.listview.ListViewLayout;
import org.terifan.ui.listview.ListViewModel;


public class CardItemRenderer implements ListViewItemRenderer
{
	protected int PADDING = 13;

	protected Dimension mItemSize;
	protected Orientation mOrientation;
	protected int mLabelWidth;
	protected int mRowHeight;


	/**
	 * @param aItemWidth
	 *   Preferred width of an item.
	 * @param aItemHeight
	 *   Preferred height of an item.
	 * @param aOrientation
	 *   The orientation
	 */
	public CardItemRenderer(Dimension aItemSize, int aLabelWidth, Orientation aOrientation)
	{
		mItemSize = aItemSize;
		mLabelWidth = aLabelWidth;
		mOrientation = aOrientation;
		mRowHeight = 16;
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
		return aListView.getModel().getColumnCount()*mRowHeight;
	}


	@Override
	public int getItemMaximumHeight(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemMinimumHeight(ListView aListView)
	{
		return mRowHeight;
	}


	@Override
	public int getItemWidth(ListView aListView, ListViewItem aItem)
	{
		return mItemSize.width;
	}


	@Override
	public int getItemHeight(ListView aListView, ListViewItem aItem)
	{
		ListViewModel model = aListView.getModel();
		int h = 0;
		for (int i = 0, sz = model.getColumnCount(); i < sz; i++)
		{
			Object value = aItem.getValue(i);
			ListViewColumn column = model.getColumn(i);
			if (column.getFormatter() != null)
			{
				value = column.getFormatter().format(value);
			}
			if (value != null && value.toString().length() > 0)
			{
				h += mRowHeight;
			}
		}
		return PADDING+Math.max(mRowHeight, h);
	}


	@Override
	public void paintItem(Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, ListViewItem aItem)
	{
		Utilities.enableTextAntialiasing(aGraphics);
		StyleSheet style = aListView.getStylesheet();
		ListViewModel model = aListView.getModel();

		aOriginX += 6;
		aOriginY += 6;
		aWidth -= 6;
		aHeight -= 6;

		if (aListView.isItemSelected(aItem))
		{
			Utilities.drawScaledImage(aGraphics, style.getImage("cardBackgroundSelected"), aOriginX, aOriginY, aWidth, aHeight, 18, 1, 6, 6);
		}
		else
		{
			Utilities.drawScaledImage(aGraphics, style.getImage("cardBackgroundNormal"), aOriginX, aOriginY, aWidth, aHeight, 18, 1, 6, 6);
		}

		int rowCount = Math.max(1, (aHeight - 4) / mRowHeight);

//		Font plain = style.getFont("item");
//		Font bold = style.getFont("label");
//		Color plainColor = style.getColor("itemForeground");
//		Color boldColor = style.getColor("itemLabelForeground");

		int x = aOriginX;
		int y = aOriginY+2;

		Color foreground = style.getColor("itemForeground");
		Color background = style.getColor("itemBackground");

		for (int col = 0, rowIndex = 0; col < model.getColumnCount() && rowIndex < rowCount; col++)
		{
			ListViewColumn column = model.getColumn(col);

			Object value = aItem.getValue(col);
			if (column.getFormatter() != null)
			{
				value = column.getFormatter().format(value);
			}
			if (value != null)
			{
//				boolean sorted = model.getSortedColumn() == column;
//				boolean focus = aListView.getFocusItem() == aItem && aListView.getSelectionMode() == SelectionMode.CELL;

//				aGraphics.setFont(col == 0 ? bold : plain);
//				aGraphics.setColor(col == 0 ? boldColor : plainColor);

				aListView.getTextRenderer().drawString(aGraphics, column.getLabel(), x+5, y, mLabelWidth, mRowHeight, Anchor.NORTH_WEST, foreground, background, false);

				aListView.getTextRenderer().drawString(aGraphics, value.toString(), x+5+5+mLabelWidth, y, aWidth-15-5-mLabelWidth, mRowHeight, Anchor.NORTH_WEST, foreground, background, false);

//				Component c = column.getListViewCellRenderer().getListViewCellRendererComponent(aListView, aItem, col, aItem.isSelected(), focus, false, sorted);
//				c.setBounds(x, y, aWidth, rowHeight);
//				c.paint(aGraphics);

				y += mRowHeight;
				rowIndex++;
			}
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
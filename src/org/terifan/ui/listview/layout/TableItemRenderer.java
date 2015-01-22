package org.terifan.ui.listview.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewCellRenderer;
import org.terifan.ui.listview.ListViewColumn;
import org.terifan.ui.listview.ListViewItem;
import org.terifan.ui.listview.ListViewItemRenderer;
import org.terifan.ui.listview.ListViewLayout;
import org.terifan.ui.listview.ListViewLayoutVertical;
import org.terifan.ui.listview.ListViewModel;
import org.terifan.ui.listview.SelectionMode;
import org.terifan.ui.listview.util.Colors;


public class TableItemRenderer implements ListViewItemRenderer
{
	protected int mItemHeight = 19;
	protected int mFocusableColumnIndex;


	public void setFocusableColumnIndex(int aIndex)
	{
		mFocusableColumnIndex = aIndex;
	}


	public int getFocusableColumnIndex()
	{
		return mFocusableColumnIndex;
	}


	@Override
	public int getItemPreferredWidth(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemMaximumWidth(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemMinimumWidth(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemPreferredHeight(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemMaximumHeight(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemMinimumHeight(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemHeight(ListView aListView, ListViewItem aItem)
	{
		return mItemHeight;
	}


	@Override
	public int getItemWidth(ListView aListView, ListViewItem aItem)
	{
		return getItemPreferredWidth(aListView);
	}


	@Override
	public void paintItem(Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, ListViewItem aItem)
	{
		int x = aOriginX;
		
		ListViewModel model = aListView.getModel();

		for (int col = 0; col < model.getColumnCount(); col++)
		{
			ListViewColumn column = model.getColumn(col);
		
			int w = column.getWidth();

			if (col+1 == model.getColumnCount())
			{
				w = aWidth;
			}

			boolean sorted = model.getSortedColumn() == column;

			boolean focus = aListView.getFocusItem() == aItem && col == mFocusableColumnIndex && aListView.getSelectionMode() == SelectionMode.CELL;

			boolean rollover = aListView.getRolloverItem() == aItem;

			Component c = getCellRenderer(aListView, aItem, col).getListViewCellRendererComponent(aListView, aItem, col, aListView.isItemSelected(aItem), focus, rollover, sorted);
			c.setBounds(x+1, aOriginY+1, w-1, aHeight-1);
			c.paint(aGraphics);

			aGraphics.setColor(Color.GRAY);
			aGraphics.drawRect(x, aOriginY, w, aHeight);

			x += w;
		}
	}


	@Override
	public ListViewLayout createListViewLayout(ListView aListView)
	{
		return new ListViewLayoutVertical(aListView, 1);
	}


	protected ListViewCellRenderer getCellRenderer(ListView aListView, ListViewItem aItem, int aIndex)
	{
		Object v = aItem.getValue(aIndex);
		if (v instanceof JComponent)
		{
			return new ComponentWrapper((JComponent) v);
		}
		return new CellRenderer();
	}


	class ComponentWrapper implements ListViewCellRenderer
	{
		JComponent c;
		public ComponentWrapper(JComponent c)
		{
			this.c = c;
		}
		@Override
		public JComponent getListViewCellRendererComponent(ListView aListView, ListViewItem aItem, int aColumnIndex, boolean aIsSelected, boolean aHasFocus, boolean aIsRollover, boolean aIsSorted)
		{
			if (c.getParent() == null)
			{
				aListView.add(c);
			}
			c.setForeground(Colors.getTextForeground(aListView.getStylesheet(), aListView.getSelectionMode(), aIsSorted, aIsSelected, aIsRollover, aHasFocus, true));
			c.setBackground(Colors.getCellBackground(aListView.getStylesheet(), aListView.getSelectionMode(), aIsSorted, aIsSelected, aIsRollover, aHasFocus, true));
			c.setFont(aListView.getFont());

			return c;
		}
	}
}
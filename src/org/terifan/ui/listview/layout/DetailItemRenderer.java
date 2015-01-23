package org.terifan.ui.listview.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;
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


public class DetailItemRenderer implements ListViewItemRenderer
{
	protected boolean mExtendLastItem;


	public void setExtendLastItem(boolean aExtendLastItem)
	{
		mExtendLastItem = aExtendLastItem;
	}


	public boolean getExtendLastItem()
	{
		return mExtendLastItem;
	}


	@Override
	public int getItemMinimumWidth(ListView aListView)
	{
		int w = 0;
		ListViewModel model = aListView.getModel();
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			ListViewColumn column = model.getColumn(i);
			if (column.isVisible())
			{
				w += column.getWidth();
			}
		}
		return w;
	}


	@Override
	public int getItemMaximumWidth(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemPreferredWidth(ListView aListView)
	{
		return getItemMinimumWidth(aListView);
	}


	@Override
	public int getItemMinimumHeight(ListView aListView)
	{
		return aListView.getStylesheet().getInt("itemHeight");
	}


	@Override
	public int getItemMaximumHeight(ListView aListView)
	{
		return 32767;
	}


	@Override
	public int getItemPreferredHeight(ListView aListView)
	{
		return getItemMinimumHeight(aListView);
	}


	@Override
	public int getItemWidth(ListView aListView, ListViewItem aItem)
	{
		return getItemPreferredWidth(aListView);
	}


	@Override
	public int getItemHeight(ListView aListView, ListViewItem aItem)
	{
		return aListView.getStylesheet().getInt("itemHeight");
	}


	@Override
	public void paintItem(Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, ListViewItem aItem)
	{
		StyleSheet style = aListView.getStylesheet();

		int x = aOriginX;

		ListViewModel model = aListView.getModel();

		boolean isSelected = aListView.isItemSelected(aItem);
		boolean isRollover = aListView.getRolloverItem() == aItem;

		if (aListView.getSelectionMode() == SelectionMode.ROW || aListView.getSelectionMode() == SelectionMode.SINGLE_ROW)
		{
			Color c;
			if (isSelected)
			{
				if (isRollover)
				{
					c = style.getColor("itemSelectedRolloverBackground");
				}
				else if (style.getColor("itemSelectedBackground") != null)
				{
					c = style.getColor("itemSelectedBackground");
				}
				else
				{
					c = style.getColor("itemSelectedUnfocusedBackground");
				}
			}
			else if (isRollover)
			{
				c = style.getColor("itemRolloverBackground");
			}
			else
			{
				c = style.getColor("itemBackground");
			}
			aGraphics.setColor(c);
			aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
		}

		for (int col = 0; col < model.getColumnCount(); col++)
		{
			ListViewColumn column = model.getColumn(col);

			int w = column.getWidth();

			if (mExtendLastItem && col + 1 == model.getColumnCount())
			{
				w = aWidth;
			}

			boolean sorted = model.getSortedColumn() == column;

			boolean focus = aListView.getFocusItem() == aItem && aListView.getModel().getColumn(col).isFocusable() && aListView.getSelectionMode() != SelectionMode.ROW && aListView.getSelectionMode() != SelectionMode.SINGLE_ROW;

			Component c = getCellRenderer(aListView, aItem, col).getListViewCellRendererComponent(aListView, aItem, col, isSelected, focus, isRollover, sorted);
			c.setBounds(x+1, aOriginY, w-1, aHeight-1);
			c.paint(aGraphics);

			x += w;
		}

		int thickness = style.getInt("itemHorizontalLineThickness");
		if (thickness > 0)
		{
			aGraphics.setColor(style.getColor("horizontalLine"));
			aGraphics.drawLine(aOriginX, aOriginY + aHeight - thickness, aOriginX + aWidth, aOriginY + aHeight - 1);
		}

		if (aListView.getFocusItem() == aItem && (aListView.getSelectionMode() == SelectionMode.ROW || aListView.getSelectionMode() == SelectionMode.SINGLE_ROW))
		{
			aGraphics.setColor(style.getColor("focusRect"));
			Utilities.drawDottedRect(aGraphics, aOriginX, aOriginY, aWidth, aHeight - 1, false);
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


	static class ComponentWrapper implements ListViewCellRenderer
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

package org.terifan.ui.listview;

import java.awt.Graphics2D;


public interface ListViewItemRenderer<E extends ListViewItem>
{
	public int getItemMinimumWidth(ListView aListView);

	public int getItemMaximumWidth(ListView aListView);

	public int getItemPreferredWidth(ListView aListView);

	public int getItemMinimumHeight(ListView aListView);

	public int getItemMaximumHeight(ListView aListView);

	public int getItemPreferredHeight(ListView aListView);

	public int getItemWidth(ListView aListView, E aItem);

	public int getItemHeight(ListView aListView, E aItem);

	public void paintItem(Graphics2D aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListView aListView, E aItem);

	public ListViewLayout createListViewLayout(ListView aListView);
}
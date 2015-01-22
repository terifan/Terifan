package org.terifan.ui.listview;

import javax.swing.JComponent;

public interface ListViewCellRenderer
{
	public JComponent getListViewCellRendererComponent(ListView aListView, ListViewItem aItem, int aColumnIndex, boolean aIsSelected, boolean aHasFocus, boolean aIsRollover, boolean aIsSorted);
}

package org.terifan.ui.listview;

import java.awt.Graphics;
import javax.swing.SortOrder;


public interface ListViewHeaderRenderer
{
	public void paintRowHeader(ListView aListView, Graphics aGraphics, int x, int y, int w, int h, boolean aIsSelected, boolean aIsArmed, boolean aIsRollover);

	public void paintColumnHeader(ListView aListView, ListViewColumn aColumn, Graphics aGraphics, int x, int y, int w, int h, boolean aIsSelected, boolean aIsArmed, boolean aIsRollover, SortOrder aSorting, boolean aFirstColumn, boolean aLastColumn);

	//TODO: remove
	public void paintColumnHeaderLeading(ListView aListView, Graphics aGraphics, int x, int y, int w, int h);

	//TODO: remove
	public void paintColumnHeaderTrailing(ListView aListView, Graphics aGraphics, int x, int y, int w, int h);

	public void paintUpperLeftCorner(ListView aListView, Graphics aGraphics, int x, int y, int w, int h);

	public void paintUpperRightCorner(ListView aListView, Graphics aGraphics, int x, int y, int w, int h);

	public int getColumnHeaderHeight(ListView aListView);

	public int getRowHeaderWidth();

	public boolean getExtendLastItem();
}
package org.terifan.ui.listview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import org.terifan.forms.Orientation;


public interface ListViewLayout
{
	public Orientation getLayoutOrientation();

	public void paint(Graphics2D aGraphics);

	public LocationInfo getLocationInfo(int aLocationX, int aLocationY);

	public int getMarginLeft();

	public Dimension getPreferredSize();

	public Dimension getMinimumSize();

	public int getItemsPerRun();

	public ListViewItem getItemRelativeTo(ListViewItem aItem, int aDiffX, int aDiffY);

	public ArrayList<ListViewItem> getItemsIntersecting(ListViewItem aFromItem, ListViewItem aToItem);

	public ArrayList<ListViewItem> getItemsIntersecting(int x1, int y1, int x2, int y2, ArrayList<ListViewItem> aList);

	public boolean getItemBounds(ListViewItem aItem, Rectangle aRectangle);

	public ListViewItem getFirstItem();

	public ListViewItem getLastItem();
}
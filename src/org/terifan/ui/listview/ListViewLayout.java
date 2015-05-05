package org.terifan.ui.listview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import org.terifan.ui.Orientation;


public interface ListViewLayout<T extends ListViewItem>
{
	public Orientation getLayoutOrientation();

	public void paint(Graphics2D aGraphics);

	public LocationInfo getLocationInfo(int aLocationX, int aLocationY);

	public int getMarginLeft();

	public Dimension getPreferredSize();

	public Dimension getMinimumSize();

	public int getItemsPerRun();

	public T getItemRelativeTo(T aItem, int aDiffX, int aDiffY);

	public ArrayList<T> getItemsIntersecting(T aFromItem, T aToItem);

	public ArrayList<T> getItemsIntersecting(int x1, int y1, int x2, int y2, ArrayList<T> aList);

	public boolean getItemBounds(T aItem, Rectangle aRectangle);

	public T getFirstItem();

	public T getLastItem();
}
package org.terifan.ui.fullscreenwindow;

import java.util.ArrayList;


public class WindowTabBar
{
	private ArrayList<WindowTabItem> mItems;
	private int mSelectedIndex;


	public WindowTabBar()
	{
		mItems = new ArrayList<>();
	}


	public void add(WindowTabItem... aItems)
	{
		for (WindowTabItem item : aItems)
		{
			item.setParent(this);
			mItems.add(item);
		}
	}


	public ArrayList<WindowTabItem> getItems()
	{
		return mItems;
	}


	public int getSelectedIndex()
	{
		return mSelectedIndex;
	}


	public void setSelectedIndex(int aSelectedIndex)
	{
		mSelectedIndex = aSelectedIndex;
	}
}

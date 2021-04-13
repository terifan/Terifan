package org.terifan.ui.fullscreenwindow;

import java.util.ArrayList;


public class WindowMenuBar
{
	private ArrayList<WindowMenuItem> mItems;


	public WindowMenuBar()
	{
		mItems = new ArrayList<>();
	}


	public void add(WindowMenuItem... aItems)
	{
		for (WindowMenuItem item : aItems)
		{
			item.setParent(this);
			mItems.add(item);
		}
	}


	public ArrayList<WindowMenuItem> getItems()
	{
		return mItems;
	}
}

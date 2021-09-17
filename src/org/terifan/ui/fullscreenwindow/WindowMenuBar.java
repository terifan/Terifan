package org.terifan.ui.fullscreenwindow;

import java.util.ArrayList;


public class WindowMenuBar
{
	private ArrayList<WindowMenuItem> mItems;


	public WindowMenuBar()
	{
		mItems = new ArrayList<>();
	}


	public WindowMenuBar add(WindowMenuItem aItem)
	{
		aItem.setParent(this);
		mItems.add(aItem);
		return this;
	}


	public ArrayList<WindowMenuItem> getItems()
	{
		return mItems;
	}
}

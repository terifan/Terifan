package org.terifan.ui.fullscreenwindow;

import java.util.ArrayList;


public class WindowMenuBar
{
	private WindowMenuSelectionHandler mMenuSelectionHandler;
	private ArrayList<WindowMenuItem> mItems;
	private int mSelectedIndex;


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


	public WindowMenuBar setSelectedMenu(WindowMenuItem aMenu)
	{
		mSelectedIndex = mItems.indexOf(aMenu);
		return this;
	}


	public WindowMenuSelectionHandler getMenuSelectionHandler()
	{
		return mMenuSelectionHandler;
	}


	public WindowMenuBar setOnMenuSelected(WindowMenuSelectionHandler aMenuSelectionHandler)
	{
		mMenuSelectionHandler = aMenuSelectionHandler;
		return this;
	}


	public void selectMenu(WindowMenuItem aItem)
	{
		WindowMenuSelectionHandler handler = aItem.getMenuSelectionHandler();

		if (handler == null)
		{
			handler = mMenuSelectionHandler;
		}
		if (handler != null)
		{
			if (!handler.menuSelected(aItem))
			{
				return;
			}
		}

		setSelectedMenu(aItem);
	}
}

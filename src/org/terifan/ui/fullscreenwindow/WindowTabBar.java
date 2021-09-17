package org.terifan.ui.fullscreenwindow;

import java.util.ArrayList;


public class WindowTabBar
{
	private ArrayList<WindowTabItem> mItems;
	private int mSelectedIndex;
	private WindowTabSelectionHandler mTabSelectedHandler;


	public WindowTabBar()
	{
		mItems = new ArrayList<>();
	}


	public WindowTabBar add(WindowTabItem aItem)
	{
		aItem.setParent(this);
		mItems.add(aItem);
		return this;
	}


	public ArrayList<WindowTabItem> getItems()
	{
		return mItems;
	}


	public WindowTabItem getItem(int aIndex)
	{
		return mItems.get(aIndex);
	}


	public WindowTabItem getItem(String aLabel)
	{
		for (WindowTabItem item : mItems)
		{
			if (aLabel.equals(item.getLabel()))
			{
				return item;
			}
		}
		return null;
	}


	public int getSelectedIndex()
	{
		return mSelectedIndex;
	}


	public WindowTabBar setSelectedIndex(int aSelectedIndex)
	{
		mSelectedIndex = aSelectedIndex;
		return this;
	}


	public WindowTabBar setSelectedTab(WindowTabItem aTab)
	{
		mSelectedIndex = mItems.indexOf(aTab);
		return this;
	}


	public WindowTabBar setOnTabSelected(WindowTabSelectionHandler aTabHandler)
	{
		mTabSelectedHandler = aTabHandler;
		return this;
	}


	public WindowTabSelectionHandler getTabSelectedHandler()
	{
		return mTabSelectedHandler;
	}


	public void selectTab(WindowTabItem aTab)
	{
		WindowTabSelectionHandler handler = aTab.getTabSelectedHandler();

		if (handler == null)
		{
			handler = mTabSelectedHandler;
		}
		if (handler != null)
		{
			if (!handler.tabSelected(aTab))
			{
				return;
			}
		}

		setSelectedTab(aTab);
	}


	public int indexOf(WindowTabItem aTabButton)
	{
		return mItems.indexOf(aTabButton);
	}
}

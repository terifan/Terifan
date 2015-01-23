package org.terifan.ui.listview;


public class LocationInfo
{
	private ListViewGroup mGroup;
	private boolean mGroupButton;
	private ListViewItem mItem;


	protected LocationInfo()
	{
	}


	public void setGroup(ListViewGroup aGroup)
	{
		mGroup = aGroup;
	}


	public ListViewGroup getGroup()
	{
		return mGroup;
	}


	public void setItem(ListViewItem aItem)
	{
		mItem = aItem;
	}


	public ListViewItem getItem()
	{
		return mItem;
	}


	public void setGroupButton(boolean aGroupButton)
	{
		mGroupButton = aGroupButton;
	}


	public boolean isGroupButton()
	{
		return mGroupButton;
	}


	public boolean isGroup()
	{
		return mGroup != null;
	}


	public boolean isItem()
	{
		return mItem != null;
	}


	@Override
	public boolean equals(Object aOther)
	{
		if (aOther instanceof LocationInfo)
		{
			LocationInfo other = (LocationInfo)aOther;
			return other.mItem == mItem || (mGroup != null && other.mGroupButton == mGroupButton && other.mGroup == mGroup);
		}
		return false;
	}


	@Override
	public String toString()
	{
		return mItem + " " + mGroup + " " + mGroupButton;
	}


	@Override
	public int hashCode()
	{
		return (mItem == null ? 0 : mItem.hashCode()) ^ (mGroup == null ? 0 : mGroup.hashCode()) ^ (mGroupButton ? 1 : 0);
	}
}

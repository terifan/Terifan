package org.terifan.ui.listview;

import org.terifan.util.SortedMap;
import java.util.ArrayList;


public class ListViewGroup<T extends ListViewItem>
{
	protected ListViewGroup<T> mParent;
	protected SortedMap<Object,ListViewGroup<T>> mChildren;
	protected ArrayList<T> mItems;
	protected int mItemCount;
	protected int mGroupCount;
	protected boolean mCollapsed;
	protected int mLevel;
	protected Object mGroupValue;
	protected boolean mIsSelected;


	protected ListViewGroup(ListViewGroup aParent, int aLevel, Object aGroupValue)
	{
		mParent = aParent;
		mLevel = aLevel;
		mGroupValue = aGroupValue;
	}


	public ListViewGroup getParent()
	{
		return mParent;
	}


	public int getGroupIndex(ListViewGroup aGroup)
	{
		return mChildren.indexOfValue(aGroup);
	}


	public ListViewGroup<T> getChildGroupByIndex(int aIndex)
	{
		return mChildren.get(mChildren.getKeys().get(aIndex));
	}


	public SortedMap<Object,ListViewGroup<T>> getChildren()
	{
		return mChildren;
	}


	public ListViewGroup getChild(Object aKey)
	{
		return mChildren.get(aKey);
	}


	public void setChildren(SortedMap<Object,ListViewGroup<T>> aChildren)
	{
		mChildren = aChildren;
	}


	public void setItems(ArrayList<T> aItems)
	{
		mItems = aItems;
	}


	public ArrayList<T> getItems()
	{
		return mItems;
	}


	public T getItem(int aIndex)
	{
		return mItems.get(aIndex);
	}


	/**
	 * Gets the number of items in this group.
	 *
	 * @return
	 *   Return number of items in this group.
	 */
	public int getItemCount()
	{
		return mItemCount;
	}


	public int getGroupCount()
	{
		return mGroupCount;
	}


	public boolean isCollapsed()
	{
		return mCollapsed;
	}


	public void setCollapsed(boolean aState)
	{
		mCollapsed = aState;
	}


	@Override
	public String toString()
	{
		return "ListViewGroup[GroupValue="+getGroupValue()+", GroupCount="+getGroupCount()+", ItemCount="+getItemCount()+", Level="+getLevel()+", Collapsed="+isCollapsed()+", Selected="+isSelected()+"]";
	}


	public Object getGroupValue()
	{
		return mGroupValue;
	}


	public int getLevel()
	{
		return mLevel;
	}


	protected void aggregate()
	{
		if (mChildren != null)
		{
			for (Object k : mChildren.getKeys())
			{
				ListViewGroup group = mChildren.get(k);

				group.aggregate();

				mItemCount += group.mItemCount;
				mGroupCount += group.mGroupCount+1;
			}
		}
		else
		{
			mItemCount = mItems.size();
		}
	}


	public boolean isSelected()
	{
		return mIsSelected;
	}


	protected void setSelected(boolean aState)
	{
		mIsSelected = aState;
	}


	public ListViewGroup findContainingGroup(T aItem)
	{
		if (mItems != null)
		{
			if (mItems.contains(aItem))
			{
				return this;
			}
		}
		if (mChildren != null)
		{
			for (Object o : mChildren.getKeys())
			{
				ListViewGroup group = mChildren.get(o).findContainingGroup(aItem);
				if (group != null)
				{
					return group;
				}
			}
		}

		return null;
	}


	public ListViewGroup getRoot()
	{
		ListViewGroup group = this;
		while (group.getParent() != null)
		{
			group = group.getParent();
		}
		return group;
	}


	public ListViewGroup getSiblingGroup(int aDirection, boolean aVisibleOnly)
	{
		if (aDirection != -1 && aDirection != 1)
		{
			throw new IllegalArgumentException("aDirection must be +1 or -1 only.");
		}
		if (mParent == null)
		{
			return null; // root has no siblings
		}

		mTempBoolean = false;
		mTempGroup = null;

		return getSiblingGroupHeavy(getRoot(), aDirection, aVisibleOnly);
/*
		int siblingIndex = mParent.getChildGroupIndex(containingGroup);

		if (siblingIndex == -1)
		{
			throw new RuntimeException();
		}

		ListViewGroup siblingGroup;

		for (;;)
		{
			siblingIndex += aDirection;

			if (siblingIndex < 0 || siblingIndex >= mParent.getGroupCount())
			{
				if (mParent.getParent() == null)
				{
					return null;
				}
				return getSiblingGroupHeavy(getRoot(), aDirection, aVisibleOnly);
			}

			siblingGroup = mParent.getChildGroupByIndex(siblingIndex);

			if (!aVisibleOnly || !siblingGroup.isCollapsed())
			{
				return siblingGroup;
			}
		}
*/
	}

	private ListViewGroup mTempGroup;
	private boolean mTempBoolean;

	private synchronized ListViewGroup getSiblingGroupHeavy(ListViewGroup<T> aGroup, int aDirection, boolean aVisibleOnly)
	{
		if (aGroup.mChildren != null)
		{
			for (Object key : aGroup.mChildren.getKeys())
			{
				ListViewGroup<T> group = aGroup.mChildren.get(key);

				if (aDirection == -1 && group == this)
				{
					return mTempGroup;
				}

				if (aDirection == 1 && mTempBoolean && mLevel == group.getLevel() && (!aVisibleOnly || !group.isCollapsed()))
				{
					return group;
				}

				if (aDirection == -1 && mLevel == group.getLevel() && (!aVisibleOnly || !group.isCollapsed()))
				{
					mTempGroup = group;
				}

				if (aDirection == 1 && group == this)
				{
					mTempBoolean = true;
				}

				if (group.getLevel() < mLevel && (!aVisibleOnly || !group.isCollapsed()))
				{
					ListViewGroup result = getSiblingGroupHeavy(group, aDirection, aVisibleOnly);

					if (result != null)
					{
						return result;
					}
				}
			}
		}

		return null;
	}
}
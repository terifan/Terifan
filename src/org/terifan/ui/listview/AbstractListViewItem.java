package org.terifan.ui.listview;

import org.terifan.ui.Icon;


public abstract class AbstractListViewItem implements ListViewItem
{
	@Override
	public Icon getIcon(int aIndex)
	{
		return null;
	}


//	@Override
//	public void setIcon(int aIndex, Icon aIcon)
//	{
//	}
//
//
//	@Override
//	public void setValue(int aIndex, Object aValue)
//	{
//	}
	
	
	@Override
	public Object getRenderingHint(Object aKey)
	{
		return null;
	}


	/**
	 * Override this method to lazily load the item when it becomes visible.
	 * 
	 * Note: this method is called from the Swing rendering thread and must not block. Call repaint to update the ListView with any changed state.
	 * 
	 * This implementation does nothing.
	 */
	@Override
	public void loadState(boolean aItemVisible)
	{
	}
}

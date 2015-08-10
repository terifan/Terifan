package org.terifan.ui.listview;

import org.terifan.ui.Icon;


public abstract class AbstractListViewItem implements ListViewItem
{
	@Override
	public Icon getIcon(int aIndex)
	{
		return null;
	}
	
	
	@Override
	public Object getRenderingHint(Object aKey)
	{
		return null;
	}
}

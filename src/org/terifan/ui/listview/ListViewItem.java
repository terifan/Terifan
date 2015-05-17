package org.terifan.ui.listview;

import org.terifan.ui.Icon;


public interface ListViewItem
{
	Object getValue(int aIndex);

//	void setValue(int aIndex, Object aValue);

	Icon getIcon(int aIndex);

//	void setIcon(int aIndex, Icon aIcon);

	Object getRenderingHint(Object aKey);

	@Override
	boolean equals(Object aObject);

	@Override
	int hashCode();

	/**
	 * Item will be rendered, load any state necessary for it.
	 * 
	 * @param aItemVisible 
	 *   true if it is within the visible rect and is rendered and false if it is neighbouring the visible rect (ie a row/column above/below).
	 */
	void loadState(boolean aItemVisible);
}
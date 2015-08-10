package org.terifan.ui.listview;

import org.terifan.ui.Icon;


public interface ListViewItem
{
	Object getValue(int aIndex);

	Icon getIcon(int aIndex);

	Object getRenderingHint(Object aKey);

	/**
	 * Implementations should load item state necessary to display the item. The method is called repeatedly as items become visible in the ListView.
	 * 
	 * @param aBackground
	 *   true if the state is loaded in the background and the item might be hidden.
	 * @return
	 *   true item need to be repainted
	 */
	boolean loadState(boolean aBackground) throws Exception;
}
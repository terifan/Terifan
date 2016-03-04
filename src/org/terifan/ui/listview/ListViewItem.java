package org.terifan.ui.listview;

import org.terifan.ui.Icon;


public interface ListViewItem
{
	Object getValue(ListViewColumn aColumn);

	Icon getIcon(ListViewColumn aColumn);

	Object getRenderingHint(Object aKey);

	/**
	 * Implementations should load item state necessary to display the item and ensure the method isStateLoaded return true if the state don't need to be loaded again.
	 * 
	 * @param aBackground
	 *   true if the state is loaded as a background task. The ListView implementation only calls this method with a 'false' value.
	 * @return
	 *   true item need to be repainted
	 */
	boolean loadState(boolean aBackground) throws Exception;

	/**
	 * Return true if the state is loaded.
	 */
	boolean isStateLoaded();
}
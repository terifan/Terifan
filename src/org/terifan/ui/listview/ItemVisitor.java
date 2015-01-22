package org.terifan.ui.listview;

public interface ItemVisitor
{
	/**
	 * Visits an item.
	 * 
	 * @param aItem
	 *   the item being visited
	 * @return
	 *   a non-null Object if the visitor should stop visiting any more items.
	 *   The Object is returned by the Model visitor method.
	 */
	public Object visit(ListViewItem aItem);
}

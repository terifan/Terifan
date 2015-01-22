package org.terifan.ui.listview;

public interface GroupVisitor
{
	/**
	 * Visits a group.
	 *
	 * @param aGroup
	 *   the group being visited
	 * @return
	 *   a non-null Object if the visitor should stop visiting any more groups.
	 *   The Object is returned by the Model visitor method.
	 */
	public Object visit(ListViewGroup aGroup);
}

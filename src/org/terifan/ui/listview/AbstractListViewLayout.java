package org.terifan.ui.listview;


public abstract class AbstractListViewLayout implements ListViewLayout
{
	protected ListView mListView;


	private class FirstItemVisitor implements GroupVisitor
	{
		ListViewItem mItem;
		@Override
		public Object visit(ListViewGroup aGroup)
		{
			if (aGroup.getItemCount() > 0)
			{
				mItem = aGroup.getItem(0);
				return Boolean.TRUE; // stop visiting once we have found an item
			}
			return null;
		}
	}


	@Override
	public ListViewItem getFirstItem()
	{
		FirstItemVisitor v = new FirstItemVisitor();
		mListView.getModel().visitGroups(false, true, v);
		return v.mItem;
	}


	private class LastItemVisitor implements GroupVisitor
	{
		ListViewItem mItem;
		@Override
		public Object visit(ListViewGroup aGroup)
		{
			if (aGroup.getItemCount() > 0)
			{
				mItem = aGroup.getItem(aGroup.getItemCount()-1);
			}
			// continue visiting trough out the entire tree
			return null;
		}
	}


	@Override
	public ListViewItem getLastItem()
	{
		LastItemVisitor v = new LastItemVisitor();
		mListView.getModel().visitGroups(false, true, v);
		return v.mItem;
	}
}

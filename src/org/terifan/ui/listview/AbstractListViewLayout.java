package org.terifan.ui.listview;


public abstract class AbstractListViewLayout<T extends ListViewItem> implements ListViewLayout<T>
{
	protected ListView<T> mListView;


	private class FirstItemVisitor<T extends ListViewItem> implements GroupVisitor<T>
	{
		T mItem;
		@Override
		public Object visit(ListViewGroup<T> aGroup)
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
	public T getFirstItem()
	{
		FirstItemVisitor<T> v = new FirstItemVisitor<>();
		mListView.getModel().visitGroups(false, true, v);
		return v.mItem;
	}


	private class LastItemVisitor<T extends ListViewItem> implements GroupVisitor<T>
	{
		T mItem;
		@Override
		public Object visit(ListViewGroup<T> aGroup)
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
	public T getLastItem()
	{
		LastItemVisitor<T> v = new LastItemVisitor<>();
		mListView.getModel().visitGroups(false, true, v);
		return v.mItem;
	}
}

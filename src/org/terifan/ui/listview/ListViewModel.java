package org.terifan.ui.listview;

import java.util.Iterator;
import org.terifan.ui.listview.util.Formatter;
import org.terifan.util.SortedMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.Icon;
import javax.swing.SortOrder;
import org.terifan.util.log.Log;


public class ListViewModel<T extends ListViewItem> implements Iterable<T>
{
	protected ArrayList<ListViewColumn> mColumns;
	protected ArrayList<Integer> mGroups;
	protected ArrayList<T> mItems;
	protected ListViewGroup mTree;
	protected ListViewColumn mSortedColumn;


	public ListViewModel()
	{
		mColumns = new ArrayList<>();
		mGroups = new ArrayList<>();
		mItems = new ArrayList<>();
	}


	public void clear()
	{
		mItems = new ArrayList<>();
		mTree = null;
	}

	// -- Items -----------------

	public T addItem(T aItem)
	{
		if (aItem == null)
		{
			throw new IllegalArgumentException("Item is null.");
		}

		mItems.add(aItem);
		return aItem;
	}


	public void removeItem(T aItem)
	{
		mItems.remove(aItem);
	}


	public void removeItems(Collection<T> aItems)
	{
		mItems.removeAll(aItems);
	}


	public T getItem(int aIndex)
	{
		if (aIndex < 0 || aIndex >= mItems.size())
		{
			throw new IllegalArgumentException("Index out of range: index: " + aIndex + ", size: " + mItems.size());
		}

		return mItems.get(aIndex);
	}


	public T getSortedItem(final int aIndex)
	{
		if (aIndex < 0 || aIndex >= mItems.size())
		{
			throw new IllegalArgumentException("Index out of range: index: " + aIndex + ", size: " + mItems.size());
		}

		return (T)visitItems(false, new ItemVisitor<T>()
		{
			int index;
			@Override
			public Object visit(T aItem)
			{
				return index++ == aIndex ? aItem : null;
			}
		});
	}


	public int getItemCount()
	{
		return mItems.size();
	}


	public int indexOf(T aItem)
	{
		return mItems.indexOf(aItem);
	}


	public Object getValueAt(int aRow, int aColumn)
	{
		return mItems.get(aRow).getValue(getColumn(aColumn));
	}


	public Icon getIconAt(int aRow, int aColumn)
	{
		return null;
	}


	public T findItemByColumnValue(int aColumnIndex, Object aObject)
	{
		for (T item : mItems)
		{
			if (item.getValue(getColumn(aColumnIndex)).equals(aObject))
			{
				return item;
			}
		}
		return null;
	}


	@Override
	public Iterator<T> iterator()
	{
		return mItems.iterator();
	}


	// -- Groups -----------------

	public boolean addGroup(String aColumnName)
	{
		int index = getColumnIndex(aColumnName);

		if (index == -1)
		{
			throw new IllegalArgumentException("No column with ID: " + aColumnName);
		}

		if (mGroups.contains(index))
		{
			return false;
		}

		mGroups.add(index);
		return true;
	}


	public boolean removeGroup(String aColumnName)
	{
		int index = getColumnIndex(aColumnName);

		if (index == -1)
		{
			throw new IllegalArgumentException("No column with ID: " + aColumnName);
		}

		for (int i = 0; i < mGroups.size(); i++)
		{
			if (mGroups.get(i) == index)
			{
				mGroups.remove(i);
				return true;
			}
		}

		return false;
	}


	public int getGroup(int aIndex)
	{
		return mGroups.get(aIndex);
	}


	public int getGroupCount()
	{
		return mGroups.size();
	}


	public boolean isGrouped(int aIndex)
	{
		return mGroups.contains(aIndex);
	}


	public boolean collapse(Object ... aGroupValues)
	{
		ListViewGroup group = getGroup(aGroupValues);

		if (group == null)
		{
			return false;
		}

		group.setCollapsed(true);

		return true;
	}


	public boolean collapseChildren(Object ... aGroupValues)
	{
		ListViewGroup group = getGroup(aGroupValues);

		if (group == null)
		{
			return false;
		}

		collapseChildrenImpl(group);

		return true;
	}


	protected void collapseChildrenImpl(ListViewGroup aGroup)
	{
		if (aGroup.getChildren() != null)
		{
			for (Object key : aGroup.getChildren().getKeys())
			{
				ListViewGroup child = aGroup.getChild(key);

				child.setCollapsed(true);
				collapseChildrenImpl(child);
			}
		}
	}


	public boolean expand(Object ... aGroupValues)
	{
		ListViewGroup group = getGroup(aGroupValues);

		if (group == null)
		{
			return false;
		}

		group.setCollapsed(false);

		return true;
	}


	public boolean expandChildren(Object ... aGroupValues)
	{
		ListViewGroup group = getGroup(aGroupValues);

		if (group == null)
		{
			return false;
		}

		expandChildrenImpl(group);

		return true;
	}


	protected void expandChildrenImpl(ListViewGroup aGroup)
	{
		if (aGroup.getChildren() != null)
		{
			for (Object key : aGroup.getChildren().getKeys())
			{
				ListViewGroup child = aGroup.getChild(key);

				child.setCollapsed(false);
				expandChildrenImpl(child);
			}
		}
	}

	// -- Columns -----------------

	public ListViewColumn addColumn(String aKeyAndLabel, int aWidth)
	{
		return addColumn(aKeyAndLabel, aKeyAndLabel, aWidth);
	}


	public ListViewColumn addColumn(String aKey, String aLabel, int aWidth)
	{
		ListViewColumn column = new ListViewColumn(this, aKey, aLabel, aWidth);
		mColumns.add(column);
		return column;
	}


	public void removeColumn(int aIndex)
	{
		mColumns.remove(aIndex);
	}


	public void removeColumn(String aId)
	{
		mColumns.remove(getColumnIndex(aId));
	}


	public Iterable<ListViewColumn> getColumns()
	{
		return mColumns;
	}


	public int getColumnIndex(String aColumnName)
	{
		for (int i = 0; i < mColumns.size(); i++)
		{
			if (mColumns.get(i).getKey().equals(aColumnName))
			{
				return i;
			}
		}

		return -1;
	}


	public int getColumnIndex(ListViewColumn aColumn)
	{
		return mColumns.indexOf(aColumn);
	}


	public ListViewColumn getColumn(String aColumnId)
	{
		for (ListViewColumn c : mColumns)
		{
			if (aColumnId.equals(c.getKey()))
			{
				return c;
			}
		}

		return null;
	}


	public ListViewColumn getColumn(int aIndex)
	{
		return mColumns.get(aIndex);
	}


	public int getColumnCount()
	{
		return mColumns.size();
	}


	public void setSortedColumn(ListViewColumn aSortedColumnId)
	{
		mSortedColumn = aSortedColumnId;
	}


	public void setSortedColumn(String aSortedColumnId)
	{
		setSortedColumn(getColumn(aSortedColumnId));
	}


	public ListViewColumn getSortedColumn()
	{
		return mSortedColumn;
	}


	public int getSortedColumnIndex()
	{
		return mColumns.indexOf(mSortedColumn);
	}

	// -- Tree -----------------

	public ListViewGroup getRoot()
	{
		if (mTree == null)
		{
			validate();
		}

		return mTree;
	}


	public void validate()
	{
		compile();
		sort();
	}


	public void sort()
	{
		sortRecursive(getRoot(), 0);
	}


	public ArrayList<T> getItems()
	{
		return mItems;
	}


	protected void sortRecursive(ListViewGroup aParent, int aLevel)
	{
		SortedMap<Object,ListViewGroup> children = aParent.getChildren();

		ArrayList list;
		ListViewColumn column;
		Comparator comparator;

		if (children == null)
		{
			if (mSortedColumn == null)
			{
				return;
			}

			column = mSortedColumn;
			list = aParent.getItems();
			comparator = column.getComparator();
		}
		else
		{
			for (Object key : children.getKeys())
			{
				sortRecursive(children.get(key), aLevel+1);
			}

			column = mColumns.get(mGroups.get(aLevel));
			list = aParent.getChildren().getKeys();
			comparator = column.getGroupComparator() == null ? column.getComparator() : column.getGroupComparator();
		}

		ComparatorProxy c = new ComparatorProxy(comparator, getColumnIndex(column), column.getSortOrder() == SortOrder.DESCENDING);
		Collections.sort(list, c);
	}


	protected void compile()
	{
		int groupCount = mGroups.size();

		if (groupCount == 0)
		{
			ListViewGroup root = new ListViewGroup(null,0,null);
			ArrayList<T> items = new ArrayList<>();
			root.setItems(items);

			for (int i = 0; i < mItems.size(); i++)
			{
				items.add(mItems.get(i));
			}

			root.aggregate();

			mTree = root;
		}
		else
		{
			ListViewGroup<T> root = new ListViewGroup<T>(null,0,null);
			root.setChildren(new SortedMap<>());

			for (T item : mItems)
			{
				assert item != null : "ListViewModel contains an item that is null";

				ListViewGroup<T> group = root;

				for (int groupIndex = 0; groupIndex < groupCount; groupIndex++)
				{
					int groupColumnIndex = mGroups.get(groupIndex);

					Object groupKey = item.getValue(getColumn(groupColumnIndex));

					Formatter formatter = mColumns.get(groupColumnIndex).getGroupFormatter();
					if (formatter != null)
					{
						groupKey = formatter.format(groupKey);
					}

					ListViewGroup<T> next = group.getChildren().get(groupKey);

					if (next == null)
					{
						next = new ListViewGroup(group, groupIndex, groupKey);

						if (groupIndex == groupCount-1)
						{
							next.setItems(new ArrayList<>());
						}
						else
						{
							next.setChildren(new SortedMap<>());
						}
						group.getChildren().put(groupKey, next);
					}

					group = next;
				}

				group.getItems().add(item);
			}

			root.aggregate();

//			System.out.println("[i="+root.getItemCount() + " g=" + root.getGroupCount() + "]");
//			System.out.println(root);

			mTree = root;
		}
	}


	public boolean contains(T aItem)
	{
		return mItems.contains(aItem);
	}


	protected ListViewGroup getGroup(Object ... aGroupValues)
	{
		if (mGroups.isEmpty())
		{
			return null;
		}

		ListViewGroup group = getRoot();

		for (Object key : aGroupValues)
		{
			group = group.getChild(key);

			if (group == null)
			{
				return null;
			}
		}

		return group;
	}


	public Object visitGroups(boolean aVisitCollapsedGroups, boolean aOnlyVisitLeafGroups, GroupVisitor aVisitor)
	{
		return visitGroups(aVisitCollapsedGroups, aOnlyVisitLeafGroups, aVisitor, getRoot());
	}


	public Object visitItems(boolean aVisitCollapsedGroups, ItemVisitor<T> aVisitor)
	{
		if (aVisitor == null)
		{
			throw new IllegalArgumentException("ItemVisitor is null.");
		}

		return visitItems(aVisitCollapsedGroups, aVisitor, getRoot());
	}


	private Object visitGroups(boolean aVisitCollapsedGroups, boolean aOnlyVisitLeafGroups, GroupVisitor aVisitor, ListViewGroup aGroup)
	{
		SortedMap<Object,ListViewGroup> children = aGroup.getChildren();

		if (!aOnlyVisitLeafGroups || children == null)
		{
			Object o = aVisitor.visit(aGroup);
			if (o != null) return o;
		}

		if (children != null)
		{
			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);
				if (aVisitCollapsedGroups || !group.isCollapsed())
				{
					Object o = visitGroups(aVisitCollapsedGroups, aOnlyVisitLeafGroups, aVisitor, group);
					if (o != null) return o;
				}
			}
		}

		return null;
	}


	private Object visitItems(boolean aVisitCollapsedGroups, ItemVisitor<T> aVisitor, ListViewGroup<T> aGroup)
	{
		SortedMap<Object,ListViewGroup<T>> children = aGroup.getChildren();

		if (children != null)
		{
			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);
				if (aVisitCollapsedGroups || !group.isCollapsed())
				{
					Object o = visitItems(aVisitCollapsedGroups, aVisitor, group);
					if (o != null)
					{
						return o;
					}
				}
			}
		}
		else
		{
			for (T item : aGroup.getItems())
			{
				Object o = aVisitor.visit(item);
				if (o != null)
				{
					return o;
				}
			}
		}

		return null;
	}


	protected class ComparatorProxy<E> implements Comparator<E>
	{
		private int mColumnIndex;
		private boolean mDescending;
		private Comparator mComparator;

		public ComparatorProxy(Comparator aComparator, int aColumnIndex, boolean aDescending)
		{
			mComparator = aComparator;
			mColumnIndex = aColumnIndex;
			mDescending = aDescending;
		}

		@Override
		public int compare(E t1, E t2)
		{
			if (mDescending)
			{
				E t = t1;
				t1 = t2;
				t2 = t;
			}

			Object v1 = t1;
			Object v2 = t2;

			if (!(mComparator instanceof ListViewItemComparator) && (t1 instanceof ListViewItem))
			{
				v1 = ((T)t1).getValue(getColumn(mColumnIndex));
				v2 = ((T)t2).getValue(getColumn(mColumnIndex));
			}

			if (v1 == null && v2 != null)
			{
				return -1;
			}
			else if (v1 != null && v2 == null)
			{
				return 1;
			}
			else if (v1 == null && v2 == null)
			{
				return 0;
			}

			if (mComparator != null)
			{
				return mComparator.compare(v1, v2);
			}
			else
			{
				if (v1 instanceof Comparable)
				{
					return ((Comparable)v1).compareTo(v2);
				}
				else
				{
					return v1.toString().compareTo(v2.toString());
				}
			}
		}
	}
}
package org.terifan.ui.listview;

import org.terifan.ui.listview.util.Formatter;
import java.awt.Font;
import java.util.Comparator;
import javax.swing.SortOrder;
import org.terifan.forms.Alignment;


public class ListViewColumn
{
	protected String mId;
	protected Alignment mAlignment;
	protected String mLabel;
	protected int mWidth;
	protected int mIconWidth;
	protected boolean mVisible;
	protected boolean mFocusable;
	protected boolean mGroupOnSort;
	protected Font mListFont;
	protected Font mHeaderFont;
	protected SortOrder mInitialSortOrder;
	protected SortOrder mSortOrder;
	protected Comparator mComparator;
	protected Comparator mGroupComparator;
	protected Formatter mFormatter;
	protected Formatter mGroupFormatter;


	public ListViewColumn(String aId, String aLabel, int aWidth)
	{
		setId(aId);
		setLabel(aLabel);
		setWidth(aWidth);
		setAlignment(Alignment.LEFT);
		setVisible(true);
		setInitialSortOrder(SortOrder.ASCENDING);
		setSortOrder(SortOrder.ASCENDING);
	}


	public boolean isGroupOnSort()
	{
		return mGroupOnSort;
	}


	public ListViewColumn setGroupOnSort(boolean aGroupOnSort)
	{
		mGroupOnSort = aGroupOnSort;
		return this;
	}


	public ListViewColumn setFormatter(Formatter aFormatter)
	{
		mFormatter = aFormatter;

		return this;
	}


	public Formatter getFormatter()
	{
		return mFormatter;
	}


	public ListViewColumn setGroupFormatter(Formatter aGroupFormatter)
	{
		mGroupFormatter = aGroupFormatter;

		return this;
	}


	public Formatter getGroupFormatter()
	{
		return mGroupFormatter;
	}


	public ListViewColumn setInitialSortOrder(SortOrder aInitialSortOrder)
	{
		mInitialSortOrder = aInitialSortOrder;
		setSortOrder(aInitialSortOrder);
		return this;
	}


	public SortOrder getInitialSortOrder()
	{
		return mInitialSortOrder;
	}


	public ListViewColumn setSortOrder(SortOrder aSortOrder)
	{
		mSortOrder = aSortOrder;
		return this;
	}


	public SortOrder getSortOrder()
	{
		return mSortOrder;
	}


	public ListViewColumn setLabel(String aLabel)
	{
		mLabel = aLabel;
		return this;
	}


	public String getLabel()
	{
		return mLabel;
	}


	public ListViewColumn setFocusable(boolean aFocusable)
	{
		mFocusable = aFocusable;
		return this;
	}


	public boolean isFocusable()
	{
		return mFocusable;
	}


	public ListViewColumn setWidth(int aWidth)
	{
		mWidth = aWidth;
		return this;
	}


	public int getWidth()
	{
		return mWidth;
	}


	public ListViewColumn setIconWidth(int aIconWidth)
	{
		mIconWidth = aIconWidth;
		return this;
	}


	public int getIconWidth()
	{
		return mIconWidth;
	}


	/**
	 * Sets the alignment.
	 *
	 * @param aAlignment
	 *   The alignment
	 * @return
	 *   return this column
	 */
	public ListViewColumn setAlignment(Alignment aAlignment)
	{
		mAlignment = aAlignment;
		return this;
	}


	public Alignment getAlignment()
	{
		return mAlignment;
	}


	public ListViewColumn setVisible(boolean aVisible)
	{
		mVisible = aVisible;
		return this;
	}


	public boolean isVisible()
	{
		return mVisible;
	}


	public ListViewColumn setId(String aId)
	{
		mId = aId;
		return this;
	}


	public String getId()
	{
		return mId;
	}


	public ListViewColumn setComparator(Comparator aComparator)
	{
		mComparator = aComparator;
		return this;
	}


	public Comparator getComparator()
	{
		return mComparator;
	}


	public ListViewColumn setGroupComparator(Comparator aGroupComparator)
	{
		mGroupComparator = aGroupComparator;
		return this;
	}


	public Comparator getGroupComparator()
	{
		return mGroupComparator;
	}


	@Override
	public String toString()
	{
		return mLabel;
	}
}
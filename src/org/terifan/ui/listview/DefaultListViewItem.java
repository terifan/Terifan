package org.terifan.ui.listview;

import java.util.Arrays;
import org.terifan.ui.Icon;


public class DefaultListViewItem extends AbstractListViewItem
{
	protected Object [] mValues;
	protected Icon [] mIcons;


	public DefaultListViewItem(int aColumnCount)
	{
		mValues = new Object[aColumnCount];
		mIcons = new Icon[aColumnCount];
	}


	public DefaultListViewItem(Object ... aValues)
	{
		mValues = aValues;
		mIcons = new Icon[mValues.length];
	}


	@Override
	public Object getValue(int aIndex)
	{
		if (aIndex < 0 || aIndex >= mValues.length)
		{
			throw new IllegalArgumentException("Item don't have column index: " + aIndex + ", max: " + mValues.length);
		}

		return mValues[aIndex];
	}


	@Override
	public void setValue(int aIndex, Object aValue)
	{
		if (aIndex < 0 || aIndex >= mValues.length)
		{
			throw new IllegalArgumentException("Item don't have column index: " + aIndex);
		}

		mValues[aIndex] = aValue;
	}


	@Override
	public Icon getIcon(int aIndex)
	{
//		if (aIndex < 0 || aIndex >= mValues.length)
//		{
//			throw new IllegalArgumentException("Item don't have column index: " + aIndex);
//		}

		if (aIndex >= mValues.length)
		{
			return null;
		}

		return mIcons[aIndex];
	}


	@Override
	public void setIcon(int aIndex, Icon aIcon)
	{
		if (aIndex < 0 || aIndex >= mValues.length)
		{
			throw new IllegalArgumentException("Item don't have column index: " + aIndex);
		}

		mIcons[aIndex] = aIcon;
	}


	@Override
	public String toString()
	{
		return Arrays.asList(mValues).toString();
	}


	@Override
	public boolean equals(Object aObject)
	{
		if (aObject instanceof DefaultListViewItem)
		{
			return Arrays.equals(((DefaultListViewItem)aObject).mValues, mValues);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		return Arrays.hashCode(mValues);
	}
}
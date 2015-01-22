package org.terifan.ui.listview.util;

import java.util.Comparator;


public class SimpleStringComparator implements Comparator<String>
{
	private boolean mMatchCase;


	public SimpleStringComparator(boolean aMatchCase)
	{
		mMatchCase = aMatchCase;
	}


	@Override
	public int compare(String o1, String o2)
	{
		if ((Object) o1 == o2)
		{
			return 0;
		}
		if (o1 == null)
		{
			return -1;
		}
		if (o2 == null)
		{
			return 1;
		}
		if (mMatchCase)
		{
			return o1.compareTo(o2);
		}
		else
		{
			return o1.compareToIgnoreCase(o2);
		}
	}
}

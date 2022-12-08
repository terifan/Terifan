package org.terifan.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * A <code>Supplier</code> return the elements from a <code>List</code>.
 */
public class ListSupplier<T> implements Supplier<T>
{
	private List<T> mList;
	private AtomicInteger mIndex;


	public ListSupplier(List<T> aList)
	{
		mList = aList;
		mIndex = new AtomicInteger();
	}


	@Override
	public T get()
	{
		int i = mIndex.getAndIncrement();
		return i >= mList.size() ? null : mList.get(i);
	}
}

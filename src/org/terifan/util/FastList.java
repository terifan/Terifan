package org.terifan.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;


public class FastList<T> implements Iterable<T>
{
	private final static int GROWTH = 1000;
	
	protected Class<T> mType;
	protected T[] mElementData;
	protected int mSize;


	public FastList(Class<T> aType)
	{
		mType = aType;
		mElementData = (T[])Array.newInstance(aType, 0);
	}


	public FastList<T> add(T aElement)
	{
		if (mSize == mElementData.length)
		{
			resize(mSize + GROWTH);
		}
		mElementData[mSize++] = aElement;
		return this;
	}


	public T get(int aIndex)
	{
		return (T)mElementData[aIndex];
	}


	public FastList<T> trimToSize()
	{
		if (mSize != mElementData.length)
		{
			resize(mSize);
		}
		return this;
	}


	public FastList<T> clear()
	{
		for (int i = 0; i < mSize; i++)
		{
			mElementData[i] = null;
		}
		mSize = 0;
		return this;
	}


	public T[] array()
	{
		return (T[])mElementData;
	}


	public boolean isEmpty()
	{
		return mSize == 0;
	}


	public int size()
	{
		return mSize;
	}


	public FastList<T> ensureCapacity(int aSize)
	{
		resize(Math.max(mSize, aSize));
		return this;
	}


	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			int mIndex;
			
			@Override
			public boolean hasNext()
			{
				return mIndex < mSize;
			}


			@Override
			public T next()
			{
				return (T)mElementData[mIndex++];
			}
		};
	}


	public void set(int aIndex, T aElement)
	{
		if (aIndex >= mSize)
		{
			resize(aIndex + GROWTH);
		}
		mElementData[aIndex] = aElement;
		if (aIndex >= mSize)
		{
			mSize = aIndex + 1;
		}
	}


	public void addAll(T[] aElements)
	{
		resize(mSize + aElements.length);
		System.arraycopy(aElements, 0, mElementData, mSize, aElements.length);
		mSize += aElements.length;
	}


	protected void resize(int aSize)
	{
		mElementData = Arrays.copyOfRange(mElementData, 0, aSize);
	}
}

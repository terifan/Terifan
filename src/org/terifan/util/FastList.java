package org.terifan.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;


public abstract class FastList<T> implements Iterable<T>
{
	private final static int GROWTH = 1000;

	protected Class<T> mType;
	protected T[] mElements;
	protected int mSize;
	protected boolean mInitializeElements;


	public FastList(Class<T> aType)
	{
		this(aType, false);
	}


	public FastList(Class<T> aType, boolean aInitializeElements)
	{
		mType = aType;
		mInitializeElements = aInitializeElements;
		mElements = (T[])Array.newInstance(aType, 0);
	}


	public FastList<T> add(T aElement)
	{
		if (mSize == mElements.length)
		{
			resize(mSize + GROWTH);
		}
		mElements[mSize++] = aElement;
		return this;
	}


	public T get(int aIndex)
	{
		return (T)mElements[aIndex];
	}


	public FastList<T> trimToSize()
	{
		if (mSize != mElements.length)
		{
			resize(mSize);
		}
		return this;
	}


	public FastList<T> clear()
	{
		for (int i = 0; i < mSize; i++)
		{
			mElements[i] = null;
		}
		mSize = 0;
		return this;
	}


	public T[] array()
	{
		return (T[])mElements;
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
		if (aSize > mElements.length)
		{
			resize(aSize);
		}
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
				return (T)mElements[mIndex++];
			}
		};
	}


	public void set(int aIndex, T aElement)
	{
		if (aIndex >= mSize)
		{
			resize(aIndex + GROWTH);
		}
		mElements[aIndex] = aElement;
		if (aIndex >= mSize)
		{
			mSize = aIndex + 1;
		}
	}


	public void addAll(T... aElements)
	{
		if (mSize + aElements.length > mElements.length)
		{
			resize(mSize + aElements.length + GROWTH);
		}
		System.arraycopy(aElements, 0, mElements, mSize, aElements.length);
		mSize += aElements.length;
	}


	protected void resize(int aSize)
	{
		mElements = Arrays.copyOfRange(mElements, 0, aSize);

		if (mInitializeElements)
		{
			for (int i = mSize; i < aSize; i++)
			{
				mElements[i] = newInstance();
			}
		}
	}


	protected abstract T newInstance();


	@Override
	public String toString()
	{
		return "FastList{" + "mElementData=" + Arrays.toString(mElements) + '}';
	}
}

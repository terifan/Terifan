package org.terifan.util;

import java.util.Arrays;
import java.util.Iterator;


public final class IntArray implements Cloneable, Iterable<Integer>
{
	private final static int GROWTH = 1000;

	private int[] mValues;
	private int mSize;


	public IntArray()
	{
		mValues = new int[0];
	}


	public IntArray(int[] aValues)
	{
		this(aValues, aValues.length);
	}


	public IntArray(int[] aValues, int aSize)
	{
		mValues = aValues;
		mSize = aSize;
	}
	
	
	public int indexOf(int aValue)
	{
		for (int i = 0; i < mSize; i++)
		{
			if (mValues[i] == aValue)
			{
				return i;
			}
		}

		return -1;
	}


	public IntArray removeValue(int aValue)
	{
		for (int i = 0, j = 0; i < mSize; )
		{
			if (mValues[i] == aValue)
			{
				if (mValues[mSize - 1] != aValue)
				{
					mValues[j++] = mValues[mSize - 1];
				}
				mSize--;
			}
			else
			{
				mValues[j++] = mValues[i++];
			}
		}
		
		return this;
	}

	
	public int[] array()
	{
		return mValues;
	}


	public int get(int aIndex)
	{
		return mValues[aIndex];
	}
	
	
	public IntArray add(int aValue)
	{
		if (mSize == mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + GROWTH);
		}
		mValues[mSize++] = aValue;
		return this;
	}
	
	
	public IntArray add(int... aValues)
	{
		add(aValues, 0, aValues.length);
		return this;
	}
	
	
	public IntArray add(int[] aValues, int aOffset, int aLength)
	{
		if (mSize + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + aLength + GROWTH);
		}
		System.arraycopy(aValues, aOffset, mValues, mSize, aLength);
		mSize += aLength;
		return this;
	}
	
	
	public IntArray set(int aIndex, int aValue)
	{
		if (aIndex >= mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, aIndex + GROWTH);
		}
		mValues[aIndex] = aValue;
		if (aIndex >= mSize)
		{
			mSize = aIndex + 1;
		}
		return this;
	}


	public IntArray set(int aIndex, int... aValues)
	{
		set(aIndex, aValues, 0, aValues.length);
		return this;
	}


	public IntArray set(int aIndex, int[] aValues, int aOffset, int aLength)
	{
		if (aIndex + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, aIndex + aLength);
			mSize = aIndex + aLength;
			System.arraycopy(aValues, aOffset, mValues, aIndex, aLength);
		}
		else
		{
			System.arraycopy(aValues, aOffset, mValues, aIndex, aLength);
			mSize = Math.max(mSize, aIndex + aLength);
		}
		return this;
	}
	
	
	public IntArray clear()
	{
		mSize = 0;
		mValues = new int[0];
		return this;
	}
	
	
	public IntArray trimToSize()
	{
		mValues = Arrays.copyOfRange(mValues, 0, mSize);
		return this;
	}
	

	public int size()
	{
		return mSize;
	}
	
	
	public boolean isEmpty()
	{
		return mSize == 0;
	}


	@Override
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			int mIndex = 0;

			@Override
			public boolean hasNext()
			{
				return mIndex < mSize;
			}

			@Override
			public Integer next()
			{
				return mValues[mIndex++];
			}
		};
	}
	
	
	@Override
	public IntArray clone()
	{
		try
		{
			return (IntArray)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return new IntArray().add(mValues, 0, mSize);
		}
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mSize; i++)
		{
			if (i > 0)
			{
				sb.append(",");
			}
			sb.append(mValues[i]);
		}
		return sb.toString();
	}
}

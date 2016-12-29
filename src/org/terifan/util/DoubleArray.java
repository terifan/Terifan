package org.terifan.util;

import java.util.Arrays;
import java.util.Iterator;


public final class DoubleArray implements Cloneable, Iterable<Double>
{
	private final static int GROWTH = 1000;

	private double[] mValues;
	private int mSize;


	public DoubleArray()
	{
		mValues = new double[0];
	}
	
	
	public double[] array()
	{
		return mValues;
	}


	public double get(int aIndex)
	{
		return mValues[aIndex];
	}
	
	
	public DoubleArray add(double aValue)
	{
		if (mSize == mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + GROWTH);
		}
		mValues[mSize++] = aValue;
		return this;
	}
	
	
	public DoubleArray add(double... aValues)
	{
		add(aValues, 0, aValues.length);
		return this;
	}
	
	
	public DoubleArray add(double[] aValues, int aOffset, int aLength)
	{
		if (mSize + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + aLength + GROWTH);
		}
		System.arraycopy(aValues, aOffset, mValues, mSize, aLength);
		mSize += aLength;
		return this;
	}
	
	
	public DoubleArray set(int aIndex, double aValue)
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
	
	
	public DoubleArray set(int aIndex, double... aValues)
	{
		set(aIndex, aValues, 0, aValues.length);
		return this;
	}
	
	
	public DoubleArray set(int aIndex, double[] aValues, int aOffset, int aLength)
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
	
	
	public DoubleArray clear()
	{
		mSize = 0;
		mValues = new double[0];
		return this;
	}
	
	
	public DoubleArray compact()
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
	public Iterator<Double> iterator()
	{
		return new Iterator<Double>()
		{
			int mIndex = 0;

			@Override
			public boolean hasNext()
			{
				return mIndex < mSize;
			}

			@Override
			public Double next()
			{
				return mValues[mIndex++];
			}
		};
	}
	
	
	@Override
	public DoubleArray clone()
	{
		try
		{
			return (DoubleArray)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return new DoubleArray().add(mValues, 0, mSize);
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

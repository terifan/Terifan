package org.terifan.util;

import java.util.TreeMap;


public class SparseDoubleArray 
{
	private final static int BITS = 8;
	private final static int SIZE = (1 << BITS) - 1;

	private TreeMap<Integer,double[]> mValues;


	public SparseDoubleArray()
	{
		mValues = new TreeMap<>();
	}


	public void set(int aIndex, double aValue)
	{
		getChunk(aIndex)[aIndex & SIZE] = aValue;
	}


	public double get(int aIndex, double aDefaultValue)
	{
		return getChunk(aIndex)[aIndex & SIZE];
	}


	private double[] getChunk(int aIndex)
	{
		if (aIndex < 0)
		{
			throw new IllegalArgumentException("" + aIndex);
		}

		int index = aIndex >>> BITS;
		double[] chunk = mValues.get(index);

		if (chunk == null)
		{
			chunk = new double[SIZE + 1];
			mValues.put(index, chunk);
		}
		
		return chunk;
	}
}

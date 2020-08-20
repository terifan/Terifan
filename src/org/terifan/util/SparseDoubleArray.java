package org.terifan.util;

import java.util.Iterator;
import java.util.TreeMap;


public class SparseDoubleArray implements Iterable<Integer>
{
	private TreeMap<Integer, Double> mValues;


	public SparseDoubleArray()
	{
		mValues = new TreeMap<>();
	}


	public void set(int aIndex, double aValue)
	{
		mValues.put(aIndex, aValue);
	}


	public double get(int aIndex, double aDefaultValue)
	{
		return mValues.getOrDefault(aIndex, aDefaultValue);
	}


	public double add(int aIndex, double aValue)
	{
		return mValues.put(aIndex, mValues.getOrDefault(aIndex, 0.0) + aValue);
	}


	public int size()
	{
		return mValues.isEmpty() ? 0 : mValues.lastKey();
	}


	public double[] toArray()
	{
		double[] array = new double[size()];
		for (int i = 0, sz = size(); i < sz; i++)
		{
			array[i] = get(i, 0.0);
		}
		return array;
	}


	@Override
	public Iterator<Integer> iterator()
	{
		return mValues.keySet().iterator();
	}


	public double maxValue()
	{
		double max = -Double.MAX_VALUE;
		for (Double d : mValues.values())
		{
			if (d > max)
			{
				max = d;
			}
		}
		return max;
	}


	public int minIndex()
	{
		return mValues.firstKey();
	}


	public int maxIndex()
	{
		return mValues.lastKey();
	}


	public boolean contains(Integer aIndex)
	{
		return mValues.containsKey(aIndex);
	}
}

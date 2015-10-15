package org.terifan.util;

import java.util.Iterator;
import java.util.TreeMap;


public class SparseDoubleArray implements Iterable<Integer>
{
	private TreeMap<Integer,Double> mValues;


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
		return mValues.get(aIndex);
	}
	
	
	public int size()
	{
		return mValues.lastKey();
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
}

package org.terifan.util;

import java.util.TreeMap;


/**
 * TODO: optimize
 */
public class SparseDoubleArray 
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
		return mValues.getOrDefault(aIndex, aDefaultValue);
	}
}

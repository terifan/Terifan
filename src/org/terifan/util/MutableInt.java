package org.terifan.util;


public class MutableInt
{
	public int value;


	public MutableInt()
	{
	}


	public MutableInt(int aValue)
	{
		value = aValue;
	}


	@Override
	public String toString()
	{
		return Integer.toString(value);
	}
}

package org.terifan.util;


public class MutableLong
{
	public long value;


	public MutableLong()
	{
	}


	public MutableLong(long aValue)
	{
		value = aValue;
	}


	@Override
	public String toString()
	{
		return Long.toString(value);
	}
}

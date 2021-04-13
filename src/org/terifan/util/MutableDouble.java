package org.terifan.util;


public class MutableDouble
{
	public double value;


	public MutableDouble()
	{
	}


	public MutableDouble(double aValue)
	{
		value = aValue;
	}


	public void setMax(double aValue)
	{
		if (aValue > value)
		{
			value = aValue;
		}
	}


	@Override
	public String toString()
	{
		return Double.toString(value);
	}
}

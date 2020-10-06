package org.terifan.util;

import java.util.ArrayList;
import java.util.Collection;


public class TypedArrayList extends ArrayList
{
	private final static long serialVersionUID = 1L;


	public TypedArrayList()
	{
	}


	public TypedArrayList(Collection aCollection)
	{
		super(aCollection);
	}


	public boolean getBoolean(int aIndex)
	{
		return (Boolean)super.get(aIndex);
	}


	public byte getByte(int aIndex)
	{
		return (Byte)super.get(aIndex);
	}


	public short getShort(int aIndex)
	{
		return (Short)super.get(aIndex);
	}


	public int getInt(int aIndex)
	{
		return (Integer)super.get(aIndex);
	}


	public long getLong(int aIndex)
	{
		return (Long)super.get(aIndex);
	}


	public float getFloat(int aIndex)
	{
		return (Float)super.get(aIndex);
	}


	public double getDouble(int aIndex)
	{
		return (Double)super.get(aIndex);
	}


	public String getString(int aIndex)
	{
		return (String)super.get(aIndex);
	}


	public ArrayList<Short> asShortList()
	{
		return this;
	}
}

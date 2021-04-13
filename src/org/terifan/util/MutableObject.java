package org.terifan.util;


public class MutableObject<T>
{
	public T value;


	public MutableObject()
	{
	}


	public MutableObject(T aValue)
	{
		this.value = aValue;
	}
}

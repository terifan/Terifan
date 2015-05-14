package org.terifan.util;


public class VolatileResource<T> implements AutoCloseable
{
	private VolatileResourceFactory mFactory;
	private T mInstance;
	private Object mOwner;


	VolatileResource(VolatileResourceFactory aFactory, T aInstance, Object aOwner)
	{
		mInstance = aInstance;
		mOwner = aOwner;
		mFactory = aFactory;
	}


	public T get()
	{
		return mInstance;
	}


	public Object getOwner()
	{
		return mOwner;
	}


	@Override
	public void close()
	{
		mFactory.remove(this);
	}
}

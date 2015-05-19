package org.terifan.util;


public class SharedResource<T> implements AutoCloseable
{
	private SharedResourceFactory mFactory;
	private T mInstance;
	private Object mOwner;


	SharedResource(SharedResourceFactory aFactory, T aInstance, Object aOwner)
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

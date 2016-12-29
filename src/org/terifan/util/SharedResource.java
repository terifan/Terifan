package org.terifan.util;

import java.util.UUID;



public class SharedResource<T> implements AutoCloseable
{
	private SharedResourceFactory mFactory;
	private T mInstance;
	private UUID mOwner;


	SharedResource(SharedResourceFactory aFactory, T aInstance, UUID aOwner)
	{
		mInstance = aInstance;
		mOwner = aOwner;
		mFactory = aFactory;
	}


	public T get()
	{
		return mInstance;
	}


	public UUID getOwner()
	{
		return mOwner;
	}


	@Override
	public void close()
	{
		mFactory.remove(this);
	}
}

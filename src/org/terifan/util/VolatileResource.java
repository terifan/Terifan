package org.terifan.util;


public class VolatileResource<T> implements AutoCloseable
{
	private VolatileResourceSupplier mSupplier;
	private boolean mWrite;


	VolatileResource(VolatileResourceSupplier aSupplier, boolean aWrite) throws Exception
	{
		mSupplier = aSupplier;
		mWrite = aWrite;
	}


	public T get()
	{
		return (T)mSupplier.get();
	}


	@Override
	public synchronized void close() throws Exception
	{
		if (mSupplier != null)
		{
			mSupplier.release(mWrite);
			mSupplier = null;
		}
	}
}

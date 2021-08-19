package org.terifan.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;


public abstract class VolatileResourceSupplier<T> implements AutoCloseable
{
	private T mInstance;
	private long mLockCount;
	private ReentrantReadWriteLock mLock;
	private long mExpireTime;
	private CleanerThread mCleaner;
	private long mReleaseDelay;


	public VolatileResourceSupplier()
	{
		mLock = new ReentrantReadWriteLock();
		mReleaseDelay = 1000;
	}


	public long getReleaseDelay()
	{
		return mReleaseDelay;
	}


	/**
	 * Sets the time before a resource is released after the most recent use. Default is 1000 ms.
	 *
	 * @param aReleaseTimeoutMillis
	 *   time in millis before a resource is released after the most recent use. Default is 1000 ms.
	 * @return
	 *   this instance
	 */
	public VolatileResourceSupplier<T> setReleaseDelay(long aReleaseTimeoutMillis)
	{
		mReleaseDelay = aReleaseTimeoutMillis;
		return this;
	}


	/**
	 * Implementations should instantiate and return an instance of the resource.
	 *
	 * @return
	 *   an instance of the resource
	 */
	protected abstract T create() throws Exception;


	/**
	 * Implementations should release the instance provided.
	 *
	 * @param aInstance
	 *   an instance of the resource
	 */
	protected abstract void release(T aInstance) throws Exception;


	/**
	 * Acquires the resource for shared usage by a multiple concurrent threads.
	 *
	 * @return
	 *   a lock that require closing. Best practise is to use a try-resource statement for this.
	 */
	public VolatileResource<T> aquire() throws Exception
	{
		mLock.readLock().lock();

		synchronized (this)
		{
			if (mInstance == null)
			{
				mInstance = create();
			}
		}

		return new VolatileResource(this, false);
	}


	/**
	 * Locks the resource for exclusive usage by a single thread.
	 *
	 * @return
	 *   a lock that require closing. Best practise is to use a try-resource statement for this.
	 */
	public VolatileResource<T> lock() throws Exception
	{
		mLock.writeLock().lock();

		synchronized (this)
		{
			if (mInstance == null)
			{
				mInstance = create();
			}
		}

		return new VolatileResource(this, true);
	}


	/**
	 * Immediately releases a resource if one exists.
	 */
	@Override
	public synchronized void close() throws Exception
	{
		if (mCleaner != null)
		{
			synchronized (mCleaner)
			{
				mCleaner.interrupt();
			}
		}
		else if (mInstance != null)
		{
			release(mInstance);
			mInstance = null;
			mCleaner = null;
		}
	}


	T get()
	{
		return mInstance;
	}


	void release(boolean aWrite) throws Exception
	{
		if (aWrite)
		{
			mLock.writeLock().unlock();
		}
		else
		{
			mLock.readLock().unlock();
		}

		synchronized (this)
		{
			mLockCount--;
			if (mLockCount == 0)
			{
				if (mCleaner == null)
				{
					mCleaner = new CleanerThread();
					mCleaner.start();
				}
				mExpireTime = System.currentTimeMillis() + mReleaseDelay;
			}
		}
	}


	private class CleanerThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				synchronized (mCleaner)
				{
					mCleaner.wait(mExpireTime - System.currentTimeMillis());
				}
			}
			catch (InterruptedException e)
			{
			}

			synchronized (VolatileResourceSupplier.this)
			{
				if (mLockCount == 0)
				{
					if (mInstance != null)
					{
						try
						{
							release(mInstance);
							mInstance = null;
							mCleaner = null;
						}
						catch (Exception e)
						{
							e.printStackTrace(System.out);
						}
					}
				}
			}
		}
	}
}

package org.terifan.util;

import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * A generic Pool implementation that will pool instances of objects for a limited time.
 *
 * If a pooled instances is older than the time limit it will be destroyed when the claim method is called.
 */
public abstract class Pool<E> implements AutoCloseable
{
	private TreeMap<Long, E> mPool = new TreeMap<>();
	private int mCapacity;
	private long mExpireTime;
	private boolean mOpen;
	private boolean mYoungFirst;


	/**
	 * Create a new Pool
	 *
	 * @param aCapacity
	 *   number of items to maintain in the Pool.
	 * @param aExpireTimeSeconds
	 *   maximum age of an item in seconds. If zero or negative then items will never expire.
	 */
	public Pool(int aCapacity, int aExpireTimeSeconds)
	{
		mOpen = true;
		mExpireTime = aExpireTimeSeconds <= 0 ? Long.MAX_VALUE : 1000L * aExpireTimeSeconds;

		capacity(aCapacity);
	}


	/**
	 * Return number of items in the Pool.
	 */
	public int getPoolSize()
	{
		return mPool.size();
	}


	/**
	 * Creates a new instance.
	 *
	 * @return
	 *  a new instance
	 */
	protected abstract E create();


	/**
	 * Release any resources used by the provided instance.
	 *
	 * This implementation does nothing.
	 *
	 * @param aItem
	 *   the item being destroyed
	 */
	protected void destroy(E aItem)
	{
	}


	/**
	 * Optional method that resets an item before the item enters the pool. The release method calls this for any item not destroyed.
	 *
	 * This implementation always return true.
	 *
	 * @param aItem
	 *   the item being pooled
	 * @return
	 *   true if the item is usable. If not, the item will be destroyed with a
	 *   call to the destroy method.
	 */
	protected boolean reset(E aItem)
	{
		return true;
	}


	/**
	 * Optional method that decides if an item should be reused or destroyed. The cleanUp method calls this for any item about to be destroyed.
	 * This method is not called for items when the size of the pool exceed the maximum size.
	 *
	 * This implementation always return false.
	 *
	 * @param aItem
	 *   the item being pooled
	 * @return
	 *   true if the item is still usable and should be added to the pool. If not, the item will be destroyed.
	 */
	protected boolean reuse(E aItem)
	{
		return false;
	}


	/**
	 * Optional method that prepares an item for use or reuse. Called after
	 * an item has been created or is about to be used after being pooled.
	 *
	 * This implementation always return true.
	 *
	 * Note: if the prepare method return false for an item that has just been
	 * created using the create method then an exception will be thrown in the
	 * claim method.
	 *
	 * @param aItem
	 *   the item about to be used
	 * @return
	 *   true if the item is usable. If not, the item will be destroyed with
	 *   a call to the destroy method and the pool with claim another instance.
	 */
	protected boolean prepare(E aItem)
	{
		return true;
	}


	/**
	 * Return an instance by either creating a new item or claiming one from the pool.
	 * <p>
	 * This method calls the create method when the pool is empty. The prepare method is always called on each instance.
	 *
	 * @return
	 *   an item
	 * @throws IllegalStateException
	 *   if the Pool is closed
 	 */
	public synchronized E claim() throws IllegalStateException
	{
		if (!mOpen)
		{
			throw new IllegalStateException("This pool is closed.");
		}

		for (;;)
		{
			Entry<Long,E> e;

			if (mYoungFirst)
			{
				e = mPool.pollLastEntry();
			}
			else
			{
				e = mPool.pollFirstEntry();
			}

			if (e == null)
			{
				E item = create();
				if (!prepare(item))
				{
					throw new RuntimeException("Create method returned an instance that are not ready for use.");
				}
				return item;
			}

			E item = e.getValue();

			if (System.currentTimeMillis() - e.getKey() < mExpireTime && prepare(item))
			{
				return item;
			}
			else
			{
				destroy(item);
			}
		}
	}


	/**
	 * Call this method when an instance is no longer used. The instance provided will either be destroyed or added to the pool for later reuse.
	 *
	 * @param aItem
	 *   the item that is no longer used.
	 */
	public synchronized void release(E aItem)
	{
		if (mPool.size() < mCapacity && reset(aItem))
		{
			add(aItem);
		}
		else
		{
			destroy(aItem);
		}
	}


	private void add(E aItem)
	{
		for (;;)
		{
			Long key = System.currentTimeMillis();

			if (!mPool.containsKey(key)) // handle cases where multiple instances are released the same millisecond.
			{
				mPool.put(key, aItem);
				return;
			}
		}
	}


	/**
	 * Clears all items from the pool. The destroy method will be called for
	 * each item in the pool.
	 */
	public synchronized void clear()
	{
		Exception ex = null;

		for (E item : mPool.values())
		{
			try
			{
				destroy(item);
			}
			catch (Exception e)
			{
				ex = e;
			}
		}

		mPool.clear();

		if (ex != null)
		{
			throw new RuntimeException("An exception occured while clearing an item.", ex);
		}
	}


	/**
	 * Destroys excessive items and any pooled instances that are older than the expire time.
	 *
	 * Calling this method from a Timer is recommended to release instances that may consume resources.
	 */
	public synchronized void cleanUp()
	{
		while (!mPool.isEmpty())
		{
			Long key = mPool.firstKey();

			if (mPool.size() <= mCapacity && System.currentTimeMillis() - key < mExpireTime)
			{
				break;
			}

			E item = mPool.remove(key);

			if (mPool.size() < mCapacity - 1 && reuse(item))
			{
				add(item);
			}
			else
			{
				destroy(item);
			}
		}
	}


	/**
	 * Return the number of items in this pool.
	 */
	public synchronized int size()
	{
		return mPool.size();
	}


	/**
	 * Return the capacity of this pool.
	 */
	public synchronized int capacity()
	{
		return mCapacity;
	}


	/**
	 * Set the capacity of this pool. This method calls cleanUp to remove any excessive items.
	 */
	public synchronized void capacity(int aNewCapacity)
	{
		if (aNewCapacity < 1)
		{
			throw new IllegalArgumentException("Illegal capacity: " + aNewCapacity);
		}

		mCapacity = aNewCapacity;

		if (mPool.size() > mCapacity)
		{
			cleanUp();
		}
	}


	/**
	 * Return true if this Pool is open for business.
	 */
	public boolean isOpen()
	{
		return mOpen;
	}


	/**
	 * Sets the capacity to zero and destroys and pooled instance. Any future attempts to claim instances will fail.
	 * It's still possible to release instances to a closed Pool.
	 */
	@Override
	public void close()
	{
		mOpen = false;

		clear();
	}


	/**
	 * If true the youngest entries will be claimed from the pool
	 */
	public boolean isYoungFirst()
	{
		return mYoungFirst;
	}


	/**
	 * Decides in what order entries are claimed from the pool. Default value is false.
	 *
	 * A young-first pool will reuse a smaller set of items and remove unused items quicker. An old-first pool will act in a round-robin
	 * fashion returning items a fair amount of times.
	 *
	 * @param aYoungFirst
	 *   if true the youngest entries will be claimed from the pool first
	 */
	public void setYoungFirst(boolean aYoungFirst)
	{
		mYoungFirst = aYoungFirst;
	}
}
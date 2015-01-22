package org.terifan.data.cache;


/**
 * Listener used to monitor the state of a cache.
 */
public class AbstractCacheStateListener<K, V> implements CacheStateListener<K, V>
{
	/**
	 * Called when an entry is added.
	 */
	@Override
	public void entryAdded(Cache<K, V> aCache, K aKey, V aValue)
	{
	}


	/**
	 * Called when an entry is removed due to the cache capacity is reached.
	 */
	@Override
	public void entryDropped(Cache<K, V> aCache, K aKey, V aValue)
	{
	}


	/**
	 * Called when an entry is removed.
	 */
	@Override
	public void entryRemoved(Cache<K, V> aCache, K aKey, V aValue)
	{
	}


	/**
	 * Called when an entry is added with a new value.
	 */
	@Override
	public void entryUpdated(Cache<K, V> aCache, K aKey, V aValue)
	{
	}
}
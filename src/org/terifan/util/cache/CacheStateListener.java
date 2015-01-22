package org.terifan.util.cache;


/**
 * Listener used to monitor the state of a cache.
 */
public interface CacheStateListener<K, V>
{
	/**
	 * Called when an entry is added.
	 */
	public void entryAdded(Cache<K, V> aCache, K aKey, V aValue);


	/**
	 * Called when an entry is added with a new value.
	 */
	public void entryUpdated(Cache<K, V> aCache, K aKey, V aValue);


	/**
	 * Called when an entry is removed.
	 */
	public void entryRemoved(Cache<K, V> aCache, K aKey, V aValue);


	/**
	 * Called when an entry is removed due to the cache capacity is reached.
	 */
	public void entryDropped(Cache<K, V> aCache, K aKey, V aValue);
}
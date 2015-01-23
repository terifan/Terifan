package org.terifan.util.cache;


public abstract class AbstractCacheBackend<K,V> implements CacheBackend<K,V>
{
	/**
	 * Restores an item based on the provided key and populates the provided
	 * cache with it.
	 *
	 * Note: implementations of the method must put an item into the cache for
	 * it the be returned to the requester.
	 *
	 * @param aCache
	 *   the relevant Cache instance
	 * @param aKey
	 *   the item key for the item
	 * @return
	 *   true if the cache item has been re-populated otherwise false
	 */
	@Override
	public boolean retrieveItem(Cache<K,V> aCache, K aKey)
	{
		return false;
	}


//	/**
//	 * Flushes an item from the cache. This method is called when an object is
//	 * dropped from the Cache due to size constraints.
//	 *
//	 * Note: when this method is called then the item has already been removed
//	 * from the cache.
//	 *
//	 * @param aCache
//	 *   the Cache instance
//	 * @param aKey
//	 *   the item key for the item
//	 * @param aValue
//	 *   the value of the item
//	 */
//	@Override
//	public void itemExpired(Cache<K,V> aCache, K aKey, V aValue)
//	{
//	}
//
//
//	/**
//	 * Removes an item from the cache. This method is called when an object is
//	 * deliberately removed from the Cache.
//	 *
//	 * Note: when this method is called then the item has already been removed
//	 * from the cache.
//	 *
//	 * Note: there's no guarantee that the key actually exists/have existed in
//	 * the cache.
//	 *
//	 * @param aCache
//	 *   the Cache instance
//	 * @param aKey
//	 *   the item key for the item
//	 */
//	@Override
//	public void itemRemoved(Cache<K,V> aCache, K aKey)
//	{
//	}
}

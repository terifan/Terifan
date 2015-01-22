package org.terifan.data.cache;


public interface CacheBackend<K,V>
{
	/**
	 * Retrieves an item based on the provided key and populates the provided cache with it.
	 *
	 * Note: implementations of the method must put an item into the cache and return true for it the be returned to the requester.
	 *
	 * @param aCache
	 *   the relevant Cache instance
	 * @param aKey
	 *   the item key for the item
	 * @return
	 *   true if the cache item has been re-populated otherwise false
	 */
	public boolean retrieveItem(Cache<K,V> aCache, K aKey);


//	/**
//	 * An item was removed from the Cache due to size or time constraints.
//	 *
//	 * Note: when this method is called then the item has already been removed from the cache.
//	 *
//	 * @param aCache
//	 *   the Cache instance
//	 * @param aKey
//	 *   the item key for the item
//	 * @param aValue
//	 *   the value of the item
//	 */
//	public void itemExpired(Cache<K,V> aCache, K aKey, V aValue);
//
//
//	/**
//	 * An item was deliberately removed from the Cache.
//	 *
//	 * Note: when this method is called then the item has already been removed from the cache. There's no guarantee that the key actually
//	 * exists/have existed in the cache.
//	 *
//	 * @param aCache
//	 *   the Cache instance
//	 * @param aKey
//	 *   the item key for the item
//	 */
//	public void itemRemoved(Cache<K,V> aCache, K aKey);
}

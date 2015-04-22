package org.terifan.util.cache;


public interface CacheObjectListener<K,V>
{
	/**
	 * Object added to a cache.
	 */
	int ADDED = 0;
	/**
	 * Object explicitly removed from a cache.
	 */
	int REMOVED = 1;
	/**
	 * Object removed from a cache due to it being full.
	 */
	int DROPPED = 2;

	void cacheStateChanged(Cache aCache, K aKey, V aValue, int aAction);
}

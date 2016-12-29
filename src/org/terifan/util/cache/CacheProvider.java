package org.terifan.util.cache;


@FunctionalInterface
public interface CacheProvider<K,V>
{
	/**
	 * Retrieve a value mapped to a certain key, possible from a backend. The Cache will call this implementation if no mapping exists in the Cache.
	 *
	 * @param aKey
	 *   Key whose associated value is to be returned. Must not be null or zero length.
	 * @return
	 *   The value mapped to the key or null if no such value exists.
	 */
	V get(K aKey);
}

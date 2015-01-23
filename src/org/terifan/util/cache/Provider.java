package org.terifan.util.cache;


public abstract class Provider<K,V>
{
	private Cache<K,V> mCache;


	public Provider(long aCapacity, int aMillis)
	{
		mCache = new Cache<>(aCapacity);
		mCache.setExpireTime(aMillis);
	}


	protected abstract V load(Cache<K,V> aCache, K aKey);


	public V get(K aKey)
	{
		V value = mCache.get(aKey);

		if (value == null)
		{
			value = load(mCache, aKey);
		}

		return value;
	}
}

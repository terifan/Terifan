package org.terifan.data.cache;


public interface CacheItem
{
	public void entryAdded(Cache aCache, Object aKey);

	public void entryRemoved(Cache aCache, Object aKey);
}

package org.terifan.util.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * The Cache class is an implementation of a LRU cache with a specified
 * capacity. When the size of the Cache exceeds the capacity, items will be
 * removed to accommodate the new items. Adding one item may result in
 * multiple items being removed.<p>
 *
 * The capacity and size parameters may be actual memory sizes, estimated sizes
 * or counts. A simplified version of the put method exists when size equals
 * one, as in a count based Cache.<p>
 *
 * CacheStateListeners can be used to monitor how the cache receives removes
 * items.<p>
 *
 * @param <K>
 *   the key type used by this cache
 * @param <V>
 *   the value type used by this cache
 */
public class Cache<K,V> implements Iterable<K>
{
	private long mCapacity;
	private long mUsedSize;
	private int mExpireTime;
	private LinkedHashMap<K,Entry<K,V>> mMap;


	public class Entry<K,V>
	{
		private V value;
		private long time;
		private long size;
	}


	/**
	 * Constructs a new standalone Cache object with a certain capacity.
	 *
	 * @param aCapacity
	 *   capacity of this Cache. Items will be removed when the total size
	 *   exceeds this value.
	 */
	public Cache(long aCapacity)
	{
		mCapacity = aCapacity;
		mExpireTime = Integer.MAX_VALUE;
		mMap = new LinkedHashMapImpl<K, Entry<K, V>>();
	}


	private class LinkedHashMapImpl<K,V> extends LinkedHashMap<K,V>
	{
//		@Override
//		protected boolean removeEldestEntry(Map.Entry<K, V> aEldest)
//		{
//			return mUsedSize > mCapacity && mMap.size() > 0;
//		}
	};


	public int getExpireTime()
	{
		return mExpireTime;
	}


	public void setExpireTime(int aExpireTime)
	{
		mExpireTime = aExpireTime;
	}


	/**
	 * Sets the capacity of this Cache.<p>
	 *
	 * Note: when reducing capacity items will be removed from this Cache
	 * instance without effecting other joined instances.
	 *
	 * @param aCapacity
	 *   capacity of this Cache. Items will be removed from the Cache when the
	 *   total size exceeds this value.
	 */
	public synchronized void setCapacity(long aCapacity)
	{
		mCapacity = aCapacity;

		shrink();
	}


	/**
	 * Gets the capacity of this Cache.
	 *
	 * @return
	 *   the capacity of this Cache
	 */
	public synchronized long getCapacity()
	{
		return mCapacity;
	}


	/**
	 * Associates the specified value with the specified key in this map.<p>
	 *
	 * This method calls all CacheStateListeners of this Cache if the Cache is
	 * full and aKey is not already in the Cache.<p>
	 *
	 * This method will always reject all items that exceed the capacity.
	 *
	 * @param aKey
	 *   Key with which the specified value is to be associated. Must not be
	 *   null or zero length.
	 * @param aValue
	 *   Value to be associated with the specified key.
	 * @param aItemSize
	 *   the size of the item.
	 * @return
	 *   Previous value associated with specified key, or null if there was no
	 *   mapping for key. A null return can also indicate that the map
	 *   previously associated null with the specified key.
	 */
	public synchronized V put(K aKey, V aValue, long aItemSize)
	{
		if (aItemSize > mCapacity)
		{
			return null;
		}

		V prevValue;

		if (mMap.containsKey(aKey))
		{
			Entry<K,V> entry = mMap.remove(aKey);
			mMap.put(aKey, entry);

			prevValue = entry.value;

			mUsedSize += aItemSize - entry.size;

			entry.value = aValue;
			entry.size = aItemSize;
			entry.time = System.currentTimeMillis();
		}
		else
		{
			prevValue = null;

			Entry entry = new Entry();
			entry.value = aValue;
			entry.size = aItemSize;
			entry.time = System.currentTimeMillis();

			mUsedSize += aItemSize;
			mMap.put(aKey, entry);
		}

		shrink();

		return prevValue;
	}


	private void shrink()
	{
		while (mUsedSize > mCapacity && mMap.size() > 0)
		{
			removeImpl(oldestKey(), true);
		}

		if (mExpireTime < Integer.MAX_VALUE)
		{
			long threshold = System.currentTimeMillis() - mExpireTime;

			while (mMap.size() > 0)
			{
				K last = oldestKey();
				Entry entry = mMap.get(last);
				if (entry == null || entry.time > threshold)
				{
					break;
				}
				removeImpl(last, true);
			}
		}
	}


	/**
	 * Returns the value to which the specified key is mapped in this identity
	 * hash map, or null if the map contains no mapping for this key. A return
	 * value of null does not necessarily indicate that the map contains no
	 * mapping for the key; it is also possible that the map explicitly maps
	 * the key to null. The containsKey method may be used to distinguish
	 * these two cases.<p>
	 *
	 * Getting a value will cause a reorder of items. The retrieved key will be
	 * moved to the top of the cache.
	 *
	 * @param aKey
	 *   Key whose associated value is to be returned. Must not be null or
	 *   zero length.
	 * @return
	 *   The value to which this map maps the specified key, or null if the
	 *   map contains no mapping for this key.
	 */
	public synchronized V get(K aKey)
	{
		Entry<K, V> entry = mMap.remove(aKey);

		if (entry != null)
		{
			mMap.put(aKey, entry);

			return entry.value;
		}

		return null;
	}


	public synchronized V get(K aKey, Provider<K,V> aProvider)
	{
		Entry<K, V> entry = mMap.get(aKey);

		if (entry != null)
		{
			mMap.put(aKey, mMap.remove(aKey));

			return entry.value;
		}

		if (aProvider != null)
		{
			try
			{
				V value = aProvider.create(aKey);

				if (value != null)
				{
					put(aKey, value, 1);
				}

				return value;
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}

		return null;
	}


	@FunctionalInterface
	public interface Provider<K,V>
	{
		V create(K aKey) throws Exception;
	}


	/**
	 * Returns the value to which the specified key is mapped in this identity
	 * hash map, or null if the map contains no mapping for this key. A return
	 * value of null does not necessarily indicate that the map contains no
	 * mapping for the key; it is also possible that the map explicitly maps
	 * the key to null. The containsKey method may be used to distinguish
	 * these two cases.<p>
	 *
	 * This method will not effect the order of items.
	 *
	 * @param aKey
	 *   Key whose associated value is to be returned. Must not be null or
	 *   zero length.
	 * @return
	 *   The value to which this map maps the specified key, or null if the
	 *   map contains no mapping for this key.
	 */
	public synchronized V peek(K aKey)
	{
		Entry<K,V> entry = mMap.get(aKey);
		if (entry == null)
		{
			return null;
		}
		return entry.value;
	}


	/**
	 * Returns true if this map contains a mapping for the specified key.
	 *
	 * @param aKey
	 *	 Key whose presence in this map is to be tested.
	 * @return
	 *	 True if this map contains a mapping for the specified key.
	 */
	public synchronized boolean containsKey(K aKey)
	{
		boolean b = mMap.containsKey(aKey);

		if (b)
		{
			mMap.put(aKey, mMap.remove(aKey));
		}

		return b;
	}


	/**
	 * Moves the key provided to the top of the MRU list but only if the key
	 * already exists in the list.
	 *
	 * @param aKey
	 *	 Key that should be bumped.
	 * @return
	 *	 True if this map contains a mapping for the specified key.
	 */
	public synchronized boolean bump(K aKey)
	{
		Entry<K, V> old = mMap.remove(aKey);
		if (old != null)
		{
			mMap.put(aKey, old);
			return true;
		}
		return false;
	}


	/**
	 * Removes the mapping for this key from this map if it is present.
	 *
	 * @param aKey
	 *    Key whose mapping is to be removed from the map.
	 * @return
	 *    Previous value associated with specified key, or null if there
	 *    was no mapping for key.
	 */
	public synchronized V remove(K aKey)
	{
		return removeImpl(aKey, false);
	}


	public synchronized void removeAll(Collection<K> aKeys)
	{
		for (K key : aKeys)
		{
			removeImpl(key, false);
		}
	}


	private synchronized V removeImpl(K aKey, boolean aDropped)
	{
		Entry<K,V> entry = mMap.remove(aKey);

		if (entry == null)
		{
			throw new NoSuchElementException();
		}

		mUsedSize -= entry.size;

		return entry.value;
	}


	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return
	 *   the number of key-value mappings in this map.
	 */
	public synchronized int size()
	{
		return mMap.size();
	}


	/**
	 * Returns the total item size of this map.
	 *
	 * @return
	 *   the total item size of this map.
	 */
	public synchronized long getUsedSize()
	{
		return mUsedSize;
	}


	/**
	 * Removes all mappings from this map. This method iterates the contents of
	 * this cache removing items from the end. Listeners will be called for
	 * every item removed.
	 */
	public synchronized void clear()
	{
		while (mMap.size() > 0)
		{
			remove(oldestKey());
		}

		mUsedSize = 0;
		mMap.clear();
	}


	private K oldestKey()
	{
		return mMap.keySet().iterator().next();
	}


	/**
	 * Removes all mappings from this map without calling any listeners or the
	 * CacheBackend if one exists.
	 */
	public synchronized void clearQuiet()
	{
		mUsedSize = 0;
		mMap.clear();
	}


	/**
	 * Returns true if this map contains no key-value mappings.
	 *
	 * @return
	 *   true if this map contains no key-value mappings.
	 */
	public synchronized boolean isEmpty()
	{
		return mMap.isEmpty();
	}


	/**
	 * Returns a set view of the keys contained in this map.
	 *
	 * @return
	 *   a set view of the keys contained in this map.
	 */
	public synchronized Set<K> keySet()
	{
		return mMap.keySet();
	}


	/**
	 * Returns an iterator for the keys in this Cache. The iterator is sorted
	 * with the most recently used item first and the least recently used item
	 * last.<p>
	 *
	 * Note: the Iterator must not be used to remove items.
	 */
	@Override
	public synchronized Iterator<K> iterator()
	{
		return mMap.keySet().iterator();
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			Cache<Integer,String> cache = new Cache<>(3);
//			cache.put(1, "a", 1);
//			cache.put(2, "b", 1);
//			cache.put(1, "A", 1);
//			cache.put(3, "c", 1);
//			cache.put(4, "d", 1);
//
//			System.out.println(cache.keySet());
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}
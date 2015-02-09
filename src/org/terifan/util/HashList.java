package org.terifan.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This class implements accessory methods to handle a HashMap<K,ArrayList<V>> structure.
 * Each key item contain a list of value items.
 *
 * @param <K>
 *   key type
 * @param <V>
 *   value type
 */
public class HashList<K,V> extends HashMap<K,ArrayList<V>> implements Iterable<ArrayList<V>>
{
	public HashList()
	{
	}


	public void add(K aKey, V aValue)
	{
		getList(aKey).add(aValue);
	}


	public void add(K aKey, int aIndex, V aValue)
	{
		getList(aKey).add(aIndex, aValue);
	}


	public void addAll(K aKey, Collection<V> aValue)
	{
		getList(aKey).addAll(aValue);
	}


	public void addAll(K aKey, int aIndex, Collection<V> aValue)
	{
		getList(aKey).addAll(aIndex, aValue);
	}


	public void set(K aKey, int aIndex, V aValue)
	{
		getList(aKey).set(aIndex, aValue);
	}


	public V get(K aKey, int aIndex)
	{
		if (!super.containsKey(aKey))
		{
			return null;
		}
		return super.get(aKey).get(aIndex);
	}


	/**
	 * Return the list for this this key. If the key is new a list is created;
	 * this method will always return a list.
	 *
	 * @param aKey
	 *   the key
	 * @return
	 *   a list associated with the key
	 */
	public ArrayList<V> getList(K aKey)
	{
		if (super.containsKey(aKey))
		{
			return super.get(aKey);
		}
		else
		{
			ArrayList<V> list = new ArrayList<>();
			super.put(aKey, list);
			return list;
		}
	}


	/**
	 * Remove the element from the list and the list if it becomes empty.
	 *
	 * @param aKey
	 *   the key
	 * @param aIndex
	 *   the n-th element to remove
	 * @return
	 *   the old value of n-th element
	 */
	public V remove(K aKey, int aIndex)
	{
		if (!super.containsKey(aKey))
		{
			return null;
		}

		V value = super.get(aKey).remove(aIndex);

		if (super.get(aKey).isEmpty())
		{
			super.remove(aKey);
		}

		return value;
	}


	/**
	 * Removes all elements associated with the key.
	 */
	public void clear(K aKey)
	{
		super.remove(aKey);
	}


	public int size(K aKey)
	{
		if (!super.containsKey(aKey))
		{
			return 0;
		}
		return super.get(aKey).size();
	}


	public boolean isEmpty(K aKey)
	{
		if (!super.containsKey(aKey))
		{
			return false;
		}
		return super.get(aKey).isEmpty();
	}


	public ArrayList<V> toList()
	{
		ArrayList<V> list = new ArrayList<>();
		for (K k : keySet())
		{
			list.addAll(getList(k));
		}
		return list;
	}


	@Override
	public Iterator<ArrayList<V>> iterator()
	{
		return values().iterator();
	}
}
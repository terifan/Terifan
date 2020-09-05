package org.terifan.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import org.terifan.util.SortedLinkedMap.Entry;


/**
 * Sorted map which allow duplicate keys.
 */
public class SortedLinkedMap<K, V> implements Iterable<Entry<K, V>>
{
	protected LinkedList<Entry<K,V>> mValues = new LinkedList<>();
	protected Comparator<? super K> mComparator;


	public SortedLinkedMap()
	{
		mValues = new LinkedList<>();
	}


	public SortedLinkedMap(Comparator<? super K> aComparator)
	{
		this();

		mComparator = aComparator;
	}


	public void add(K aKey, V aValue)
	{
		int i = 0;
		if (mComparator != null)
		{
			for (Entry<K,V> entry : mValues)
			{
				if (mComparator.compare(aKey, entry.key) < 0)
				{
					break;
				}
				i++;
			}
		}
		else
		{
			Comparable<? super K> key = (Comparable<? super K>)aKey;
			for (Entry<K,V> entry : mValues)
			{
				if (key.compareTo(entry.key) < 0)
				{
					break;
				}
				i++;
			}
		}

		mValues.add(i, new Entry<>(aKey, aValue));
	}


	public K getKey(int aIndex)
	{
		return mValues.get(aIndex).key;
	}


	public V getValue(int aIndex)
	{
		return mValues.get(aIndex).value;
	}


	public void remove(int aIndex)
	{
		mValues.remove(aIndex);
	}


	public int size()
	{
		return mValues.size();
	}


	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return mValues.iterator();
	}


	@Override
	public String toString()
	{
		return mValues.toString();
	}


	public Stream<Entry<K,V>> stream()
	{
		return mValues.stream();
	}


	public static class Entry<K,V>
	{
		private K key;
		private V value;


		public Entry(K aKey, V aValue)
		{
			this.key = aKey;
			this.value = aValue;
		}


		public K getKey()
		{
			return key;
		}


		public V getValue()
		{
			return value;
		}


		@Override
		public String toString()
		{
			return "{" + "key=" + key + ", value=" + value + '}';
		}
	}


	public static void main(String... args)
	{
		try
		{
			SortedLinkedMap<Double, String> map = new SortedLinkedMap<>();
			map.add(0.5, "a");
			map.add(0.75, "b");
			map.add(0.5, "c");
			map.add(0.75, "d");
			map.add(0.1, "e");
			map.add(1.0, "f");

			System.out.println(map);

			for (Entry<Double, String> entry : map)
			{
				System.out.println(entry);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

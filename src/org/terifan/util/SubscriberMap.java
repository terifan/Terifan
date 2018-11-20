package org.terifan.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;


public class SubscriberMap<K, V, S> implements Iterable<K>
{
	private HashMap<K, Entry> mMap;
	private BiConsumer<K,V> mEntryAddedListener;
	private BiConsumer<K,V> mEntryRemovedListener;
	private BiConsumer<K,S> mSubscriberAddedListener;
	private BiConsumer<K,S> mSubscriberRemovedListener;


	public SubscriberMap()
	{
		mMap = new HashMap<>();
	}


	public synchronized SubscriberMap<K, V, S> add(K aKey, V aValue, S aSubscriber)
	{
		Entry entry = mMap.computeIfAbsent(aKey, k->new Entry(aValue));
		
		boolean first = entry.getSecond().isEmpty();
		
		entry.add(aSubscriber);

		if (first && mEntryAddedListener != null)
		{
			mEntryAddedListener.accept(aKey, aValue);
		}
		if (mSubscriberAddedListener != null)
		{
			mSubscriberAddedListener.accept(aKey, aSubscriber);
		}

		return this;
	}


	public synchronized SubscriberMap<K, V, S> subscribe(K aKey, S aSubscriber)
	{
		Entry entry = mMap.get(aKey);

		if (entry == null)
		{
			throw new IllegalArgumentException("Key not found: " + aKey);
		}

		entry.add(aSubscriber);

		if (mSubscriberAddedListener != null)
		{
			mSubscriberAddedListener.accept(aKey, aSubscriber);
		}

		return this;
	}


	public synchronized SubscriberMap<K, V, S> unsubscribe(K aKey, S aSubscriber)
	{
		Entry entry = mMap.get(aKey);

		if (entry != null)
		{
			if (mSubscriberRemovedListener != null)
			{
				mSubscriberRemovedListener.accept(aKey, aSubscriber);
			}

			if (entry.remove(aSubscriber))
			{
				mMap.remove(aKey);

				if (mEntryRemovedListener != null)
				{
					mEntryRemovedListener.accept(aKey, entry.getFirst());
				}
			}
		}
		
		return this;
	}


	public synchronized SubscriberMap<K, V, S> unsubscribeAll(K aKey)
	{
		Entry entry = mMap.remove(aKey);

		if (entry != null)
		{
			if (mSubscriberRemovedListener != null)
			{
				for (S subscriber : entry.getSecond())
				{
					mSubscriberRemovedListener.accept(aKey, subscriber);
				}
			}

			if (mEntryRemovedListener != null)
			{
				mEntryRemovedListener.accept(aKey, entry.getFirst());
			}
		}
		
		return this;
	}


	public synchronized SubscriberMap<K, V, S> clear()
	{
		for (Object key : mMap.keySet().toArray())
		{
			unsubscribeAll((K)key);
		}
		
		return this;
	}


	public synchronized Set<K> keySet()
	{
		return mMap.keySet();
	}


	@Override
	public synchronized Iterator<K> iterator()
	{
		return mMap.keySet().iterator();
	}


	public synchronized Set<S> subscribersOf(K aKey)
	{
		return Collections.unmodifiableSet(mMap.get(aKey).getSecond());
	}


	public synchronized List<K> subscribedBy(S aSubscriber)
	{
		ArrayList<K> keys = new ArrayList<>();
		
		for (K key : this)
		{
			if (mMap.get(key).getSecond().contains(aSubscriber))
			{
				keys.add(key);
			}
		}

		return keys;
	}


	public synchronized boolean containsKey(K aKey)
	{
		return mMap.containsKey(aKey);
	}


	@Override
	public String toString()
	{
		return "SubscriberMap{" + "mMap=" + mMap + '}';
	}


	public SubscriberMap<K, V, S> setOnEntryAdded(BiConsumer<K, V> aListener)
	{
		mEntryAddedListener = aListener;
		return this;
	}


	public SubscriberMap<K, V, S> setOnEntryRemoved(BiConsumer<K, V> aListener)
	{
		mEntryRemovedListener = aListener;
		return this;
	}


	public SubscriberMap<K, V, S> setOnSubscriberAdded(BiConsumer<K, S> aListener)
	{
		mSubscriberAddedListener = aListener;
		return this;
	}


	public SubscriberMap<K, V, S> setOnSubscriberRemoved(BiConsumer<K, S> aListener)
	{
		mSubscriberRemovedListener = aListener;
		return this;
	}


	private class Entry extends Tuple<V, HashSet<S>>
	{
		public Entry(V aValue)
		{
			super(aValue, new HashSet<S>());
		}
		
		synchronized void add(S aSubscriber)
		{
			getSecond().add(aSubscriber);
		}
		
		synchronized boolean remove(S aSubscriber)
		{
			HashSet<S> set = getSecond();
			set.remove(aSubscriber);
			return set.isEmpty();
		}


		@Override
		public String toString()
		{
			return "{" + getFirst() + ", " + getSecond() + '}';
		}
	}
	
	
	public static void main(String... args)
	{
		try
		{
			SubscriberMap<String,Integer,String> map = new SubscriberMap<>();
			
			map.setOnSubscriberRemoved((t,u)->System.out.println("release " + u));
			map.setOnEntryRemoved((t,u)->System.out.println("remove resource " + u));
			
			map.add("one", 1, "dog");
			map.add("two", 2, "dog");
			map.add("three", 3, "pig");
			map.subscribe("one", "cat");
			map.subscribe("two", "cat");
			map.subscribe("two", "pig");

			System.out.println(map);
			
			System.out.println(map.subscribersOf("one"));
			System.out.println(map.subscribedBy("cat"));
			
			map.unsubscribe("one", "dog");
			System.out.println(map);
			
			map.unsubscribe("one", "cat");
			System.out.println(map);
			
			map.unsubscribeAll("two");
			System.out.println(map);
			
			map.clear();
			System.out.println(map);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

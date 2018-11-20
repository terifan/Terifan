package org.terifan.util;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;


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


	public synchronized SubscriberMap<K, V, S> add(K aKey, Function<K,V> aSupplier, S aSubscriber)
	{
		Entry entry = mMap.computeIfAbsent(aKey, k->new Entry(aSupplier.apply(k)));
		
		boolean first = entry.mSet.isEmpty();
		
		entry.add(aSubscriber);

		if (first && mEntryAddedListener != null)
		{
			mEntryAddedListener.accept(aKey, entry.mValue);
		}
		if (mSubscriberAddedListener != null)
		{
			mSubscriberAddedListener.accept(aKey, aSubscriber);
		}

		return this;
	}

	
	public synchronized V get(K aKey)
	{
		Entry entry = mMap.get(aKey);

		if (entry != null)
		{
			return entry.mValue;
		}

		return null;
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
					mEntryRemovedListener.accept(aKey, entry.mValue);
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
				for (S subscriber : entry.mSet)
				{
					mSubscriberRemovedListener.accept(aKey, subscriber);
				}
			}

			if (mEntryRemovedListener != null)
			{
				mEntryRemovedListener.accept(aKey, entry.mValue);
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


	/**
	 * Returns a Set of subscribers of the key specified.
	 */
	public synchronized Set<S> subscribersOf(K aKey)
	{
		return Collections.unmodifiableSet(mMap.get(aKey).mSet);
	}


	/**
	 * Returns a List of keys the subscriber specified is linked to.
	 */
	public synchronized List<K> subscribedBy(S aSubscriber)
	{
		ArrayList<K> keys = new ArrayList<>();
		
		for (K key : this)
		{
			if (mMap.get(key).mSet.contains(aSubscriber))
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

	
	public int size()
	{
		return mMap.size();
	}

	
	public boolean isEmpty()
	{
		return mMap.isEmpty();
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

	
	public synchronized Lease<K,V> lease(K aKey, Function<K,V> aSupplier, S aSubscriber)
	{
		Entry entry = mMap.get(aKey);
		
		if (entry == null)
		{
			add(aKey, aSupplier, aSubscriber);
		}
		else
		{
			subscribe(aKey, aSubscriber);
		}

		return new Lease<>(this, aKey, aSubscriber);
	}
	
	
	public static class Lease<K,V> implements Closeable
	{
		private K mKey;
		private Object mSubscriber;
		private SubscriberMap mMap;


		public Lease(SubscriberMap aMap, K aKey, Object aSubscriber)
		{
			mMap = aMap;
			mKey = aKey;
			mSubscriber = aSubscriber;
		}


		public K getKey()
		{
			return mKey;
		}


		public V getValue()
		{
			return (V)mMap.get(mKey);
		}


		@Override
		public void close()
		{
			mMap.unsubscribe(mKey, mSubscriber);
		}
	}
	

	public class Entry
	{
		V mValue;
		HashSet<S> mSet;
		
		public Entry(V aValue)
		{
			mValue = aValue;
			mSet = new HashSet<>();
		}
		
		synchronized void add(S aSubscriber)
		{
			mSet.add(aSubscriber);
		}
		
		synchronized boolean remove(S aSubscriber)
		{
			mSet.remove(aSubscriber);
			return mSet.isEmpty();
		}


		@Override
		public String toString()
		{
			return "{" + mValue + ", " + mSet + '}';
		}
	}
	
	
	public static void xmain(String... args)
	{
		try
		{
			SubscriberMap<String,Integer,String> map = new SubscriberMap<>();
			
			map.setOnSubscriberRemoved((t,u)->System.out.println("release " + u));
			map.setOnEntryRemoved((t,u)->System.out.println("remove resource " + u));
			
			map.add("one", t->1, "dog");
			map.add("two", t->2, "dog");
			map.add("three", t->3, "pig");
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
	
	
	public static void main(String... args)
	{
		try
		{
			final Function<String,byte[]> supplier = s->{
				System.out.println("created resource " + s);
				return ("\"resource " + s + "\"").getBytes();
			};

			final SubscriberMap<String,byte[],Thread> map = new SubscriberMap<>();

			map.setOnEntryRemoved((t,u)->System.out.println(("removed " + new String(u)) + (map.isEmpty()?"    empty!":"")));

			String[] words = {"dog","cat","cow","pig"};

			for (int i = 0; i < 20; i++)
			{
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							Random rnd = new Random();

							Thread.sleep(rnd.nextInt(1000));

//							{
//							String word = words[rnd.nextInt(words.length)];
//
//							map.add(word, supplier, this);
//
//							Thread.sleep(rnd.nextInt(1000));
//
//							map.unsubscribe(word, this);
//							}

							String word = words[rnd.nextInt(words.length)];

							try (Lease<String,byte[]> x = map.lease(word, supplier, this))
							{
								System.out.println("using " + new String(x.getValue()));
								
								Thread.sleep(rnd.nextInt(1000));
							}
						}
						catch (Throwable e)
						{
							e.printStackTrace(System.err);
						}
					}
				}.start();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * Warning: implementation not tested....
 *
 * e.g.
 * <pre>
 * class Person
 * {
 *    private final Constant<String> mCountry = Constant.of();
 *
 *    public String getCountry()
 *    {
 *       return mCountry.orElseSet(() -> "Sweden"); // expensive operation to find the country
 *    }
 *	}
 * </pre>
 *
 */
public class Constant<T>
{
	private volatile T mInstance;
	private Supplier<T> mSupplier;


	private Constant()
	{
	}


	public static <T> Constant<T> of()
	{
		Constant c = new Constant();
		return c;
	}


	public static <T> Constant<T> of(T aValue)
	{
		Constant c = new Constant();
		c.mInstance = aValue;
		return c;
	}


	public static <T> Constant<T> supplier(Supplier<T> aSupplier)
	{
		Constant c = new Constant();
		c.mSupplier = aSupplier;
		return c;
	}


	public static <T, U> Function<T, U> function(Set<T> aKeys, Function<T, U> aFunction)
	{
		HashMap<T, U> map = new HashMap<>();
		return aKey ->
		{
			if (!aKeys.contains(aKey))
			{
				throw new IllegalArgumentException("" + aKey);
			}
			return map.computeIfAbsent(aKey, k -> aFunction.apply(k));
		};
	}


	public static <T, U> Function<T, U> function(Function<T, U> aFunction)
	{
		HashMap<T, U> map = new HashMap<>();
		return aKey -> map.computeIfAbsent(aKey, k -> aFunction.apply(k));
	}


	public static <T, U> Map<T, U> map(Set<T> aKeys, Function<T, U> aFunction)
	{
		HashMap<T, U> map = new HashMap<>();
		return new Map<T, U>()
		{
			@Override
			public int size()
			{
				return aKeys.size();
			}


			@Override
			public boolean isEmpty()
			{
				return aKeys.isEmpty();
			}


			@Override
			public boolean containsKey(Object aKey)
			{
				return aKeys.contains((T)aKey);
			}


			@Override
			public boolean containsValue(Object aValue)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public U get(Object aKey)
			{
				if (!aKeys.contains((T)aKey))
				{
					throw new IllegalArgumentException("" + aKey);
				}
				return map.computeIfAbsent((T)aKey, k -> aFunction.apply(k));
			}


			@Override
			public U put(T aKey, U aValue)
			{
				return map.put(aKey, aValue);
			}


			@Override
			public U remove(Object aKey)
			{
				return map.remove((T)aKey);
			}


			@Override
			public void putAll(Map<? extends T, ? extends U> aMap)
			{
				map.putAll(aMap);
			}


			@Override
			public void clear()
			{
				map.clear();
			}


			@Override
			public Set<T> keySet()
			{
				return aKeys;
			}


			@Override
			public Collection<U> values()
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public Set<Map.Entry<T, U>> entrySet()
			{
				throw new UnsupportedOperationException();
			}
		};
	}


	public static <T> List<T> list(int aSize, Function<Object, T> aProducer)
	{
		HashMap<Integer, T> map = new HashMap<>();

		return new List<T>()
		{
			@Override
			public int size()
			{
				return aSize;
			}


			@Override
			public boolean isEmpty()
			{
				return aSize == 0;
			}


			@Override
			public boolean contains(Object aObject)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					int i;

					@Override
					public boolean hasNext()
					{
						return i < aSize;
					}


					@Override
					public T next()
					{
						return get(i++);
					}
				};
			}


			@Override
			public Object[] toArray()
			{
				return StreamSupport.stream(((Iterable<T>)() -> iterator()).spliterator(), false).collect(Collectors.toList()).toArray();
			}


			@Override
			public <T> T[] toArray(T[] a)
			{
				return StreamSupport.stream(((Iterable<T>)() -> (Iterator<T>)iterator()).spliterator(), false).collect(Collectors.toList()).toArray(a);
			}


			@Override
			public boolean add(T aE)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean remove(Object aO)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean containsAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean addAll(Collection<? extends T> aC)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean addAll(int aIndex, Collection<? extends T> aC)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean removeAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public boolean retainAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public void clear()
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public T get(int aIndex)
			{
				T v = (T)map.get(aIndex);
				if (v == null)
				{
					synchronized (this)
					{
						v = (T)map.get(aIndex);
						if (v == null)
						{
							map.put(aIndex, v = aProducer.apply(aIndex));
						}
					}
				}
				return v;
			}


			@Override
			public T set(int aIndex, T aElement)
			{
				return map.put(aIndex, aElement);
			}


			@Override
			public void add(int aIndex, T aElement)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public T remove(int aIndex)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public int indexOf(Object aO)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public int lastIndexOf(Object aO)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public ListIterator<T> listIterator()
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public ListIterator<T> listIterator(int aIndex)
			{
				throw new UnsupportedOperationException();
			}


			@Override
			public List<T> subList(int aFromIndex, int aToIndex)
			{
				throw new UnsupportedOperationException();
			}
		};
	}


	public T orElse(T aOther)
	{
		T v = mInstance;
		if (v == null)
		{
			synchronized (this)
			{
				v = mInstance;
				if (v == null)
				{
					mInstance = v = aOther;
				}
			}
		}
		return v;
	}


	public T orElseSet(Supplier<T> aSupplier)
	{
		T v = mInstance;
		if (v == null)
		{
			synchronized (this)
			{
				v = mInstance;
				if (v == null)
				{
					mInstance = v = aSupplier.get();
				}
			}
		}
		return v;
	}


	public T orElseThrow()
	{
		T v = mInstance;
		if (v == null)
		{
			synchronized (this)
			{
				v = mInstance;
				if (v == null)
				{
					throw new IllegalStateException();
				}
			}
		}
		return v;
	}


	public T get()
	{
		T v = mInstance;
		if (v == null)
		{
			synchronized (this)
			{
				v = mInstance;
				if (v == null)
				{
					mInstance = v = mSupplier.get();
				}
			}
		}
		return v;
	}
}

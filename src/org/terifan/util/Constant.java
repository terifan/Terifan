package org.terifan.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Supplier;


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
//		c.mInstance = aValue;
		return c;
	}


	public static <T> Constant<T> supplier(Supplier<T> aSupplier)
	{
		Constant c = new Constant();
		c.mSupplier = aSupplier;
		return c;
	}


	public static <T,U> List<T> list(int aSize, Function<Object,T> aProducer)
	{
		return new List<T>()
		{
			Object[] buffer = new Object[aSize];


			@Override
			public int size()
			{
				return aSize;
			}


			@Override
			public boolean isEmpty()
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean contains(Object aO)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public Iterator<T> iterator()
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public Object[] toArray()
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public <T> T[] toArray(T[] a)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean add(T aE)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean remove(Object aO)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean containsAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean addAll(Collection<? extends T> aC)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean addAll(int aIndex, Collection<? extends T> aC)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean removeAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public boolean retainAll(Collection<?> aC)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public void clear()
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public T get(int aIndex)
			{
				T v = (T)buffer[aIndex];
				if (v == null)
				{
					synchronized (this)
					{
						v = (T)buffer[aIndex];
						if (v == null)
						{
							buffer[aIndex] = v = aProducer.apply(aIndex);
						}
					}
				}
				return v;
			}


			@Override
			public T set(int aIndex, T aElement)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public void add(int aIndex, T aElement)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public T remove(int aIndex)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public int indexOf(Object aO)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public int lastIndexOf(Object aO)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public ListIterator<T> listIterator()
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public ListIterator<T> listIterator(int aIndex)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}


			@Override
			public List<T> subList(int aFromIndex, int aToIndex)
			{
				throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
			}
		};
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

package org.terifan.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


class PrioritizedQueue<E> //implements BlockingQueue<E>
{
//	private LinkedList<E> mQueue;
//	private Function<E,Integer> mComparable;
//	private PrioritizedFixedThreadExecutor<E> mExecutor;
//
//
//	public PrioritizedQueue(PrioritizedFixedThreadExecutor aExecutor, Function<E,Integer> aComparator)
//	{
//		mQueue = new LinkedList<>();
//		mComparable = aComparator;
//		mExecutor = aExecutor;
//	}
//
//
//	@Override
//	public boolean add(E aElement)
//	{
//		return mQueue.add(aElement);
//	}
//
//
//	@Override
//	public boolean offer(E aElement)
//	{
//		return mQueue.offer(aElement);
//	}
//
//
//	@Override
//	public void put(E aElement) throws InterruptedException
//	{
//		mQueue.add(aElement);
//	}
//
//
//	@Override
//	public boolean offer(E aElement, long aTimeout, TimeUnit aUnit) throws InterruptedException
//	{
//		return mQueue.add(aElement);
//	}
//
//
//	@Override
//	public E take() throws InterruptedException
//	{
//		int closest = 0;
//		int distance = Integer.MAX_VALUE;
//		int sz = size();
//
//		if (sz == 0)
//		{
//			return null;
//		}
//
//		for (int i = 0; i < sz; i++)
//		{
//			E task = mExecutor.getTask((Future)mQueue.get(i));
//
//			int d = Math.abs(mComparable.apply(task));
//
//			if (d < distance)
//			{
//				distance = d;
//				closest = i;
//
//				if (distance == 0)
//				{
//					break;
//				}
//			}
//		}
//
//		return mQueue.remove(closest);
//	}
//
//
//	@Override
//	public E poll(long aTimeout, TimeUnit aUnit) throws InterruptedException
//	{
//		return take();
//	}
//
//
//	@Override
//	public int remainingCapacity()
//	{
//		return Integer.MAX_VALUE;
//	}
//
//
//	@Override
//	public boolean remove(Object aElement)
//	{
//		return mQueue.remove(aElement);
//	}
//
//
//	@Override
//	public boolean contains(Object aElement)
//	{
//		return mQueue.contains(aElement);
//	}
//
//
//	@Override
//	public E remove()
//	{
//		return mQueue.remove();
//	}
//
//
//	@Override
//	public int size()
//	{
//		return mQueue.size();
//	}
//
//
//	@Override
//	public boolean isEmpty()
//	{
//		return mQueue.isEmpty();
//	}
//
//
//	@Override
//	public Iterator<E> iterator()
//	{
//		return mQueue.iterator();
//	}
//
//
//	@Override
//	public Object[] toArray()
//	{
//		return mQueue.toArray();
//	}
//
//
//	@Override
//	public <T> T[] toArray(T[] a)
//	{
//		return mQueue.toArray(a);
//	}
//
//
//	@Override
//	public void clear()
//	{
//		mQueue.clear();
//	}
//
//
//	@Override
//	public int drainTo(Collection<? super E> aC)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public int drainTo(Collection<? super E> aC, int aMaxElements)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public E poll()
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public E element()
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public E peek()
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public boolean containsAll(Collection<?> aC)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public boolean addAll(Collection<? extends E> aC)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public boolean removeAll(Collection<?> aC)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//
//	@Override
//	public boolean retainAll(Collection<?> aC)
//	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
}

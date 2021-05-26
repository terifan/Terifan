package org.terifan.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


class PriorityBlockingQueue<E> implements BlockingQueue<E>
{
	private LinkedList<E> mQueue;
	private Function<E,Integer> mComparable;
	private PriorityThreadExecutor<E> mExecutor;


	public PriorityBlockingQueue(PriorityThreadExecutor aExecutor, Function<E,Integer> aComparator)
	{
		mQueue = new LinkedList<>();
		mComparable = aComparator;
		mExecutor = aExecutor;
	}


	@Override
	public boolean add(E aElement)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean offer(E aElement)
	{
		return mQueue.offer(aElement);
	}


	@Override
	public void put(E aE) throws InterruptedException
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean offer(E aE, long aTimeout, TimeUnit aUnit) throws InterruptedException
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public E take() throws InterruptedException
	{
		int closest = 0;
		int distance = Integer.MAX_VALUE;

		for (int i = 0, sz = size(); i < sz; i++)
		{
			E task = mExecutor.getTask((Future)mQueue.get(i));

			int d = Math.abs(mComparable.apply(task));

			if (d < distance)
			{
				distance = d;
				closest = i;

				if (distance == 0)
				{
					break;
				}
			}
		}

		return mQueue.remove(closest);
	}


	@Override
	public E poll(long aTimeout, TimeUnit aUnit) throws InterruptedException
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public int remainingCapacity()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean remove(Object aElement)
	{
		return mQueue.remove(aElement);
	}


	@Override
	public boolean contains(Object aElement)
	{
		return mQueue.contains(aElement);
	}


	@Override
	public int drainTo(Collection<? super E> aC)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public int drainTo(Collection<? super E> aC, int aMaxElements)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public E remove()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public E poll()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public E element()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public E peek()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public int size()
	{
		return mQueue.size();
	}


	@Override
	public boolean isEmpty()
	{
		return mQueue.isEmpty();
	}


	@Override
	public Iterator<E> iterator()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean containsAll(Collection<?> aC)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean addAll(Collection<? extends E> aC)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean removeAll(Collection<?> aC)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public boolean retainAll(Collection<?> aC)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public void clear()
	{
		mQueue.clear();
	}
}

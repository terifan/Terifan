package org.terifan.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;


public class BreadthFirstIterator<T> implements Iterable<T>, Iterator<T>
{
	private LinkedList<T> mNext;
	private LinkedList<T> mCurrent;
	private Function<T, List<T>> mProvider;


	/**
	 * Create iterator that visits tree nodes in breadth first order.
	 *
	 * @param aRootNode
	 *   the root of the hierarchy
	 * @param aChildNodesProvider
	 *   a Function returning nodes below a provided node
	 */
	public BreadthFirstIterator(T aRootNode, Function<T, List<T>> aChildNodesProvider)
	{
		this(Arrays.asList(aRootNode), aChildNodesProvider);
	}


	/**
	 * Create iterator that visits tree nodes in breadth first order.
	 *
	 * @param aRootNodes
	 *   the roots of the hierarchy
	 * @param aChildNodesProvider
	 *   a Function returning nodes below a provided node
	 */
	public BreadthFirstIterator(List<T> aRootNodes, Function<T, List<T>> aChildNodesProvider)
	{
		mNext = new LinkedList<>();
		mCurrent = new LinkedList<>(aRootNodes);
		mProvider = aChildNodesProvider;
	}


	@Override
	public Iterator<T> iterator()
	{
		return this;
	}


	@Override
	public boolean hasNext()
	{
		return !mCurrent.isEmpty();
	}


	@Override
	public T next()
	{
		if (mCurrent.isEmpty())
		{
			throw new IllegalStateException();
		}

		T element = mCurrent.remove(0);

		mNext.addAll(mProvider.apply(element));

		if (mCurrent.isEmpty())
		{
			mCurrent.addAll(mNext);
			mNext.clear();
		}

		return element;
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			for (File file : new BreadthFirstIterator<>(Arrays.asList(new File("c:/temp").listFiles(f->f.isDirectory())), f->Arrays.asList(f.listFiles(g->g.isDirectory()))))
//			{
//				System.out.println(file);
//			}
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.terifan.util.PrioritizedFixedThreadExecutor.PriorityRunnableTask;


public class PrioritizedFixedThreadExecutor<T extends Runnable> implements AutoCloseable
{
	private Function<T, Integer> mComparator;
	private List<T> mQueue;
	private Worker mWorker;
	private boolean mClose;


	/**
	 * Create a new executor
	 *
	 * @param aNumThreads a positive number equals number of threads to use, zero or a negative number results in total available processors
	 * minus provided number.
	 * @param aComparator a Function evaluating a task and returning it's priority, lower numbers are executed first. This Function is
	 * called for each task when a task is acquired from the internal queue.
	 */
	public PrioritizedFixedThreadExecutor(int aNumThreads, Function<T, Integer> aComparator)
	{
		int threads = aNumThreads > 0 ? aNumThreads : Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aNumThreads);

		init(aComparator, threads);
	}


	/**
	 * Create a new executor
	 *
	 * @param aThreads number of threads expressed as a number between 0 and 1 out of total available CPUs
	 * @param aComparator a Function evaluating a task and returning it's priority, lower numbers are executed first. This Function is
	 * called for each task when a task is acquired from the internal queue.
	 */
	public PrioritizedFixedThreadExecutor(float aThreads, Function<T, Integer> aComparator)
	{
		int threads = (int)Math.max(1, aThreads * Math.min(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()));

		init(aComparator, threads);
	}


	private void init(Function<T, Integer> aComparator, int aThreads)
	{
		mComparator = aComparator;
		mQueue = new LinkedList<T>();

		mWorker = new Worker();
		mWorker.start();
	}


	private class Worker extends Thread
	{
		@Override
		public void run()
		{
			while (!mClose)
			{
				try
				{
					synchronized (PrioritizedFixedThreadExecutor.class)
					{
						PrioritizedFixedThreadExecutor.class.wait();
					}
				}
				catch (InterruptedException e)
				{
				}

				while (!mQueue.isEmpty())
				{
					take().run();
				}
			}
		}
	}


	public void submit(T aElement)
	{
		mQueue.add(aElement);

		synchronized (PrioritizedFixedThreadExecutor.class)
		{
			PrioritizedFixedThreadExecutor.class.notifyAll();
		}
	}


	@Override
	public void close()
	{
		mClose = true;

		synchronized (PrioritizedFixedThreadExecutor.class)
		{
			PrioritizedFixedThreadExecutor.class.notifyAll();
		}
	}


	private T take()
	{
		T closest = null;
		int distance = Integer.MAX_VALUE;

		for (int i = 0; i < mQueue.size(); i++)
		{
			T task;

			try
			{
				task = mQueue.get(i);
			}
			catch (Exception e)
			{
				break;
			}

			int d = Math.abs(mComparator.apply(task));

			if (d < distance)
			{
				distance = d;
				closest = task;

				if (distance == 0)
				{
					break;
				}
			}
		}

		mQueue.remove(closest);

		return closest;
	}


	public static void main(String ... args)
	{
		try
		{
			Random rnd = new Random(21);

			Function<PriorityRunnableTask, Integer> comparator = task -> task.mValue - 50;

			try (PrioritizedFixedThreadExecutor<PriorityRunnableTask> executor = new PrioritizedFixedThreadExecutor(1, comparator))
			{
				for (int i = 0; i < 20; i++)
				{
					PriorityRunnableTask task = new PriorityRunnableTask(rnd.nextInt(100))
					{
						@Override
						public void run()
						{
							try
							{
								System.out.println("consuming " + mValue);
								Thread.sleep(100);
							}
							catch (Exception e)
							{
								e.printStackTrace(System.out);
							}
						}
					};

					System.out.println("adding " + task.mValue);
					executor.submit(task);

					Thread.sleep(50);
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	static abstract class PriorityRunnableTask implements Runnable
	{
		int mValue;

		PriorityRunnableTask(int aValue)
		{
			mValue = aValue;
		}
	}
}

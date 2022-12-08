package org.terifan.util.executors;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Function;
import org.terifan.util.executors.PrioritizedExecutor.PriorityRunnableTask;


public class PrioritizedExecutor<T extends Runnable> implements AutoCloseable
{
	private Function<T, Double> mComparator;
	private LinkedList<T> mQueue;
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
	public PrioritizedExecutor(int aNumThreads, Function<T, Double> aComparator)
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
	public PrioritizedExecutor(float aThreads, Function<T, Double> aComparator)
	{
		int threads = (int)Math.max(1, aThreads * Math.min(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()));

		init(aComparator, threads);
	}


	private void init(Function<T, Double> aComparator, int aThreads)
	{
		mComparator = aComparator;
		mQueue = new LinkedList<>();

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
				boolean b;
				synchronized (mQueue)
				{
					b = mQueue.isEmpty();
				}

				if (b)
				{
					try
					{
						synchronized (PrioritizedExecutor.class)
						{
							PrioritizedExecutor.class.wait();
						}
					}
					catch (InterruptedException e)
					{
					}
				}

				for (T next; (next = take()) != null;)
				{
					next.run();
				}
			}
		}
	}


	public void submit(T aElement)
	{
		if (aElement == null)
		{
			throw new IllegalArgumentException();
		}

		synchronized (mQueue)
		{
			for (int i = 0; i < mQueue.size(); i++)
			{
				if (mQueue.get(i).equals(aElement))
				{
					return;
				}
			}

			mQueue.add(aElement);
		}

		synchronized (PrioritizedExecutor.class)
		{
			PrioritizedExecutor.class.notifyAll();
		}
	}


	@Override
	public void close()
	{
		mClose = true;

		synchronized (PrioritizedExecutor.class)
		{
			PrioritizedExecutor.class.notifyAll();
		}
	}


	private T take()
	{
		T closest = null;
		double distance = Integer.MAX_VALUE;

		int size;
		synchronized (mQueue)
		{
			size = mQueue.size();
		}

		for (int i = 0; i < size; i++)
		{
			T task;

			synchronized (mQueue)
			{
				task = mQueue.get(i);
			}

			double d = Math.abs(mComparator.apply(task));

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

		if (closest != null)
		{
			synchronized (mQueue)
			{
				mQueue.remove(closest);
			}
		}

		return closest;
	}


	public static void main(String ... args)
	{
		try
		{
			Random rnd = new Random(21);

			Function<PriorityRunnableTask, Double> comparator = task -> task.mValue - 0.5;

			try (PrioritizedExecutor<PriorityRunnableTask> executor = new PrioritizedExecutor(1, comparator))
			{
				for (int i = 0; i < 20; i++)
				{
					PriorityRunnableTask task = new PriorityRunnableTask(rnd.nextDouble())
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
		double mValue;

		PriorityRunnableTask(double aValue)
		{
			mValue = aValue;
		}
	}
}

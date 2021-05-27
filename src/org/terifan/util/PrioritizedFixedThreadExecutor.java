package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.terifan.util.PrioritizedFixedThreadExecutor.PriorityRunnableTask;


public class PrioritizedFixedThreadExecutor<T> implements AutoCloseable
{
	private PrioritizedQueue<T> mQueue;
	private HashMap<Runnable, Future> mFutures;
	private HashMap<Future, Runnable> mRunnables;
	private ExecutorService mExecutorService;
	private int mThreads;


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
		if (aNumThreads > 0)
		{
			mThreads = aNumThreads;
		}
		else
		{
			mThreads = Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aNumThreads);
		}

		mQueue = new PrioritizedQueue(this, aComparator);
		mRunnables = new HashMap<>();
		mFutures = new HashMap<>();
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
		if (aThreads < 0 || aThreads > 1)
		{
			throw new IllegalArgumentException();
		}

		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
		mThreads = Math.max(1, Math.min(cpu, (int)Math.round(cpu * aThreads)));

		mQueue = new PrioritizedQueue(this, aComparator);
		mRunnables = new HashMap<>();
		mFutures = new HashMap<>();
	}


	/**
	 * @see java.util.concurrent.ExecutorService#shutdown
	 */
	public void shutdown()
	{
		if (mExecutorService != null)
		{
			mExecutorService.shutdown();
		}
	}


	/**
	 * @see java.util.concurrent.ExecutorService#shutdownNow
	 */
	public List<Runnable> shutdownNow()
	{
		if (mExecutorService != null)
		{
			return mExecutorService.shutdownNow();
		}

		return new ArrayList<>();
	}


	/**
	 * Submit a task, this method may block if the queue size exceeds the limit.
	 *
	 * @see java.util.concurrent.ExecutorService#submit
	 */
	public Future<?> submit(Runnable aRunnable)
	{
		Future<?> future = init().submit(aRunnable);

		mRunnables.put(future, aRunnable);
		mFutures.put(aRunnable, future);

		return future;
	}


	@Override
	public void close()
	{
		close(Long.MAX_VALUE);
	}


	private synchronized boolean close(long aWaitMillis)
	{
		if (mExecutorService != null)
		{
			try
			{
				mExecutorService.shutdown();

				mExecutorService.awaitTermination(aWaitMillis, TimeUnit.MILLISECONDS);

				return false;
			}
			catch (InterruptedException e)
			{
				return true;
			}
			finally
			{
				mExecutorService = null;
			}
		}

		return false;
	}


	private synchronized ExecutorService init()
	{
		if (mExecutorService == null)
		{
			mExecutorService = new ThreadPoolExecutor(mThreads, mThreads, 0L, TimeUnit.MILLISECONDS, (BlockingQueue<Runnable>)mQueue)
			{
				@Override
				protected void afterExecute(Runnable aRunnable, Throwable aThrowable)
				{
					super.afterExecute(aRunnable, aThrowable);

					synchronized (PrioritizedFixedThreadExecutor.class)
					{
						PrioritizedFixedThreadExecutor.class.notify();
					}

					if (aThrowable != null)
					{
						aThrowable.printStackTrace(System.err);
					}

					mFutures.remove(aRunnable);
				}
			};
		}

		return mExecutorService;
	}


	T getTask(Future aFuture)
	{
		return (T)mRunnables.get(aFuture);
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

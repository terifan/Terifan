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
import org.terifan.util.PriorityThreadExecutor.PriorityRunnableTask;


/**
 * Helper class replacing Executors.newFixedThreadPool()
 */
public class PriorityThreadExecutor<T> implements AutoCloseable
{
	private PriorityBlockingQueue<T> mBlockingQueue;
	private HashMap<RunnableTask, Future> mFutures;
	private HashMap<Future, RunnableTask> mRunnables;
	private ExecutorService mExecutorService;
	private OnCompletion mOnCompletion;
	private int mThreads;
	private int mQueueSizeLimit;


	/**
	 * Create a new executor
	 *
	 * @param aNumThreads a positive number equals number of threads to use, zero or a negative number results in total available processors
	 * minus provided number.
	 */
	public PriorityThreadExecutor(int aNumThreads, Function<T, Integer> aComparator)
	{
		if (aNumThreads > 0)
		{
			mThreads = aNumThreads;
		}
		else
		{
			mThreads = Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aNumThreads);
		}

		mBlockingQueue = new PriorityBlockingQueue(this, aComparator);
		mQueueSizeLimit = Integer.MAX_VALUE;
		mRunnables = new HashMap<>();
		mFutures = new HashMap<>();
	}


	/**
	 * Create a new executor
	 *
	 * @param aThreads number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public PriorityThreadExecutor(float aThreads, Function<T, Integer> aComparator)
	{
		if (aThreads < 0 || aThreads > 1)
		{
			throw new IllegalArgumentException();
		}

		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
		mThreads = Math.max(1, Math.min(cpu, (int)Math.round(cpu * aThreads)));

		mBlockingQueue = new PriorityBlockingQueue(this, aComparator);
		mQueueSizeLimit = Integer.MAX_VALUE;
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
	public Future<?> submit(RunnableTask aRunnable)
	{
		try
		{
			synchronized (PriorityThreadExecutor.class)
			{
				while (mBlockingQueue.size() >= mQueueSizeLimit)
				{
					PriorityThreadExecutor.class.wait();
				}
			}
		}
		catch (InterruptedException e)
		{
		}

		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					aRunnable.run();
				}
				catch (Exception e)
				{
				}
			}
		};

		Future<?> future = init().submit(runnable);

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
			mExecutorService = new ThreadPoolExecutor(mThreads, mThreads, 0L, TimeUnit.MILLISECONDS, (BlockingQueue<Runnable>)mBlockingQueue)
			{
				@Override
				protected void afterExecute(Runnable aRunnable, Throwable aThrowable)
				{
					super.afterExecute(aRunnable, aThrowable);

					synchronized (PriorityThreadExecutor.class)
					{
						PriorityThreadExecutor.class.notify();
					}

					if (mOnCompletion != null)
					{
						mOnCompletion.onCompletion((Future)aRunnable);
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


	public int getQueueSizeLimit()
	{
		return mQueueSizeLimit;
	}


	/**
	 * Sets how many items the blocking queue will contain before the submit methods start blocking.
	 */
	public PriorityThreadExecutor<T> setQueueSizeLimit(int aQueueSizeLimit)
	{
		mQueueSizeLimit = aQueueSizeLimit;
		return this;
	}


	public OnCompletion getOnCompletion()
	{
		return mOnCompletion;
	}


	public PriorityThreadExecutor<T> setOnCompletion(OnCompletion aOnCompletion)
	{
		mOnCompletion = aOnCompletion;
		return this;
	}


	@FunctionalInterface
	public interface RunnableTask
	{
		void run() throws Exception;
	}


	@FunctionalInterface
	public interface OnCompletion<T>
	{
		void onCompletion(T aItem);
	}


	public static void main(String ... args)
	{
		try
		{
			Random rnd = new Random(21);

			Function<PriorityRunnableTask, Integer> comparator = task -> task.mValue - 50;

			try (PriorityThreadExecutor<PriorityRunnableTask> executor = new PriorityThreadExecutor(1, comparator))
			{
				for (int i = 0; i < 10; i++)
				{
					PriorityRunnableTask task = new PriorityRunnableTask(rnd.nextInt(100))
					{
						@Override
						public void run() throws Exception
						{
							System.out.println("consuming " + mValue);
							Thread.sleep(100);
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

	static abstract class PriorityRunnableTask implements RunnableTask
	{
		int mValue;

		PriorityRunnableTask(int aValue)
		{
			mValue = aValue;
		}
	}
}

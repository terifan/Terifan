package org.terifan.util.executors;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Helper class replacing Executors.newFixedThreadPool()
 */
public class FixedThreadExecutor<T> implements AutoCloseable
{
	private final Object LOCK = new Object();
	private final HashMap<CallableTask, AtomicReference> mResultReceivers;
	private final LinkedBlockingQueue mBlockingQueue;
	private final int mThreads;
	private ExecutorService mExecutorService;
	private OnCompletion mOnCompletion;
	private int mQueueLimit;


	/**
	 * Create a new executor
	 *
	 * @param aNumThreads a positive number equals number of threads to use, zero or a negative number results in total available processors
	 * minus provided number.
	 */
	public FixedThreadExecutor(int aNumThreads)
	{
		if (aNumThreads > 0)
		{
			mThreads = aNumThreads;
		}
		else
		{
			mThreads = Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aNumThreads);
		}

		mBlockingQueue = new LinkedBlockingQueue<>();
		mResultReceivers = new HashMap<>();
	}


	/**
	 * Create a new executor
	 *
	 * @param aThreads number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public FixedThreadExecutor(float aThreads)
	{
		if (aThreads < 0 || aThreads > 1)
		{
			throw new IllegalArgumentException();
		}

		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		mThreads = Math.max(1, Math.min(cpu, (int)Math.round(cpu * aThreads)));
		mBlockingQueue = new LinkedBlockingQueue<>();
		mResultReceivers = new HashMap<>();
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
	public void submit(RunnableTask aRunnable)
	{
		doSubmit(init(), aRunnable);
	}


	public void call(AtomicReference aResultReceiver, CallableTask aRunnable)
	{
		mResultReceivers.put(aRunnable, aResultReceiver);
		doSubmit(init(), aRunnable);
	}


	private void doSubmit(ExecutorService aService, Object aRunnable)
	{
		if (mQueueLimit > 0)
		{
			synchronized (LOCK)
			{
				while (mBlockingQueue.size() >= mQueueLimit)
				{
					try
					{
						LOCK.wait(1000);
					}
					catch (InterruptedException e)
					{
						System.out.println("#");
					}
				}
			}
		}

		if (aRunnable instanceof RunnableTask)
		{
			aService.submit(() ->
			{
				try
				{
					((RunnableTask)aRunnable).run();
					synchronized (LOCK)
					{
						LOCK.notify();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace(System.err);
				}
			});
		}
		else if (aRunnable != null)
		{
			aService.submit(() ->
			{
				try
				{
					Object r = ((CallableTask)aRunnable).run();
					synchronized (LOCK)
					{
						LOCK.notify();
					}
					return r;
				}
				catch (Exception e)
				{
					e.printStackTrace(System.err);
					return e;
				}
			});
		}
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

					synchronized (this)
					{
						notify();
					}

					if (mOnCompletion != null)
					{
						mOnCompletion.onCompletion((Future)aRunnable);
					}

					if (aThrowable != null)
					{
						aThrowable.printStackTrace(System.err);
					}
				}
			};
		}

		return mExecutorService;
	}


	public LinkedBlockingQueue<T> getBlockingQueue()
	{
		return mBlockingQueue;
	}


	public int getQueueSizeLimit()
	{
		return mQueueLimit;
	}


	/**
	 * Sets how many items the blocking queue will contain before the submit methods start blocking.
	 */
	public FixedThreadExecutor<T> setQueueLimit(int aQueueSizeLimit)
	{
		mQueueLimit = aQueueSizeLimit;
		return this;
	}


	public OnCompletion getOnCompletion()
	{
		return mOnCompletion;
	}


	public FixedThreadExecutor<T> setOnCompletion(OnCompletion aOnCompletion)
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
	public interface CallableTask<U>
	{
		U run() throws Exception;
	}


	@FunctionalInterface
	public interface OnCompletion<T>
	{
		void onCompletion(T aItem);
	}
}

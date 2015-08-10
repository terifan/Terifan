package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.terifan.util.log.Log;


/**
 * Helper class replacing Executors.newFixedThreadPool()
 */
public class FixedThreadExecutor implements AutoCloseable
{
	private LinkedBlockingQueue<Runnable> mBlockingQueue;
	private ExecutorService mExecutorService;
	private int mThreads;


	public FixedThreadExecutor(int aThreads)
	{
		mThreads = aThreads;
		mBlockingQueue = new LinkedBlockingQueue<>();
	}


	/**
	 *
	 * @param aThreads
	 *   number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public FixedThreadExecutor(float aThreads)
	{
		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		mThreads = Math.max(1, Math.min(cpu, (int)Math.round(cpu * (aThreads - (int)(aThreads - 0.000001)))));
		mBlockingQueue = new LinkedBlockingQueue<>();
	}


	public void submit(Runnable aRunnable)
	{
		if (!mBlockingQueue.contains(aRunnable))
		{
			init().submit(aRunnable);
		}
		else
			Log.out.println("##");
	}


	public void submit(Runnable... aRunnables)
	{
		ExecutorService service = init();

		for (Runnable r : aRunnables)
		{
			if (!mBlockingQueue.contains(r))
			{
				service.submit(r);
			}
		}
	}


	public void submit(Iterable<? extends Runnable> aRunnables)
	{
		ExecutorService service = init();

		for (Runnable r : aRunnables)
		{
			if (!mBlockingQueue.contains(r))
			{
				service.submit(r);
			}
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
			mExecutorService = new ThreadPoolExecutor(mThreads, mThreads, 0L, TimeUnit.MILLISECONDS, mBlockingQueue);
		}

		return mExecutorService;
	}
}

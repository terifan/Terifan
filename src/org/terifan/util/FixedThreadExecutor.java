package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Helper class replacing Executors.newFixedThreadPool()
 */
public class FixedThreadExecutor implements AutoCloseable
{
	private ExecutorService mExecutorService;
	private int mThreads;


	public FixedThreadExecutor(int aThreads)
	{
		mThreads = aThreads;
		init();
	}


	public FixedThreadExecutor(double aThreads)
	{
		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		mThreads = Math.max(1, (int)Math.ceil(cpu * aThreads));

		init();
	}


	public void submit(Runnable aRunnable)
	{
		init();
		mExecutorService.submit(aRunnable);
	}


	public void submit(Runnable... aRunnables)
	{
		init();
		for (Runnable r : aRunnables)
		{
			mExecutorService.submit(r);
		}
	}


	public void submit(Iterable<? extends Runnable> aRunnables)
	{
		init();
		for (Runnable r : aRunnables)
		{
			mExecutorService.submit(r);
		}
	}


	@Override
	public void close()
	{
		close(Long.MAX_VALUE);
	}


	private boolean close(long aWaitMillis)
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


	private void init()
	{
		if (mExecutorService == null)
		{
			mExecutorService = Executors.newFixedThreadPool(mThreads);
		}
	}
}

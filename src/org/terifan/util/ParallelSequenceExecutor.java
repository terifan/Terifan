package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;


public class ParallelSequenceExecutor
{
	private final Worker[] mWorkers;
	private final int mMaxWorkers;
	private boolean mCancelled;
	private Iterator mJobs;
	private int mShutdownCount;
	private int mJobCount;
	private JobExecutor mJobExecutor;

	
	/**
	 * Create a new executor
	 *
	 * 
	 * @param aNumThreads 
	 *   a positive number equals number of threads to use, a negative number results in total available processors minus aNumThreads threads.
	 */
	public ParallelSequenceExecutor(int aThreads)
	{
		if (aThreads > 0)
		{
			mMaxWorkers = aThreads;
		}
		else if (aThreads < 0)
		{
			mMaxWorkers = Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aThreads);
		}
		else
		{
			throw new IllegalArgumentException();
		}

		mWorkers = new Worker[mMaxWorkers];
	}


	/**
	 * Create a new executor
	 *
	 * @param aThreads
	 *   number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public ParallelSequenceExecutor(float aThreads)
	{
		if (aThreads < 0 || aThreads > 1)
		{
			throw new IllegalArgumentException();
		}

		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		mMaxWorkers = Math.max(1, Math.min(cpu, (int)Math.round(cpu * aThreads)));
		mWorkers = new Worker[mMaxWorkers];
	}
	
	
	public void cancel()
	{
		mCancelled = true;
	}
	
	
	public void restart()
	{
		mCancelled = false;
	}
	
	
	public <E> int execute(int aJobCount, JobExecutor<Integer> aJobProvier)
	{
		Iterator<Integer> jobs = new Iterator<Integer>()
		{
			int i;

			@Override
			public boolean hasNext()
			{
				return i < aJobCount;
			}

			@Override
			public Integer next()
			{
				return i++;
			}
		};

		return execute(jobs, aJobProvier);
	}
	
	
	public <E> int execute(E[] aJobs, JobExecutor<E> aJobProvier)
	{
		return execute(Arrays.asList(aJobs), aJobProvier);
	}

	
	public <E> int execute(Iterable<E> aJobs, JobExecutor<E> aJobProvier)
	{
		return execute(aJobs.iterator(), aJobProvier);
	}
	
	
	public synchronized <E> int execute(Iterator<E> aJobs, JobExecutor<E> aJobProvier)
	{
		if (mCancelled)
		{
			return 0;
		}

		mJobs = aJobs;
		mJobExecutor = aJobProvier;
		mShutdownCount = 0;
		mJobCount = 0;

		for (int i = 0; i < mMaxWorkers; i++)
		{
			mWorkers[i] = new Worker();
			mWorkers[i].start();
		}

		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
		}

		return mJobCount;
	}


	private synchronized Object aquire()
	{
		if (mCancelled || !mJobs.hasNext())
		{
			mShutdownCount++;

			if (mShutdownCount == mMaxWorkers)
			{
				notify();
			}

			return null;
		}
		
		mJobCount++;

		return mJobs.next();
	}


	private class Worker<E> extends Thread
	{
		@Override
		public void run()
		{
			for (;;)
			{
				E job = (E)aquire();
				
				if (job == null)
				{
					break;
				}
				
				try
				{
					mJobExecutor.execute(job);
				}
				catch (Throwable e)
				{
					e.printStackTrace(System.out);
				}
			}
		}
	}
	
	
	@FunctionalInterface
	public interface JobExecutor<E>
	{
		void execute(E aJob);
	}
}

package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Iterator;


/**
 * This class executes a sequence of elements in parallel. The sequence is either a fixed number 
 * of iterations, reading from an iterator or array. Each element is sent to the executing function 
 * provided in the call.
 * 
 * Note: if the execution is cancelled and the same instance is to be reused in another execution 
 *       then it's necessary to call the reset method before execute.
 * 
 * E.g.
 *   ParallelSequenceExecutor executor = new ParallelSequenceExecutor(8);
 * 
 *   // call the sample method 100 times, the method must take a single Integer as argument:
 *   executor.execute(100, sampleClass::sampleMethod);
 * 
 *   // call the sample method with a string, the method must take a single String as argument:
 *   executor.execute(new String[]{"a","b"}, System.out::println);
 */
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
	

	/**
	 * Cancel any current and future execution of the sequence iterator.
	 */
	public void cancel()
	{
		mCancelled = true;
	}
	

	/**
	 * After an execution has been cancelled call this method to allow more executions to run.
	 */
	public void restart()
	{
		mCancelled = false;
	}
	
	
	/**
	 * Calls the function provided aJobCount times with the index as an Integer.
	 * 
	 * @return 
	 *   number of elements processed
	 */
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
	
	
	/**
	 * Calls the function provided for each item in the array provided.
	 * 
	 * @return 
	 *   number of elements processed
	 */
	public <E> int execute(E[] aJobs, JobExecutor<E> aJobProvier)
	{
		return execute(Arrays.asList(aJobs), aJobProvier);
	}

	
	/**
	 * Calls the function provided for each item in the iterator provided.
	 * 
	 * @return 
	 *   number of elements processed
	 */
	public <E> int execute(Iterable<E> aJobs, JobExecutor<E> aJobProvier)
	{
		return execute(aJobs.iterator(), aJobProvier);
	}
	
	
	/**
	 * Calls the function provided for each item in the iterator provided.
	 * 
	 * @return 
	 *   number of elements processed
	 */
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


	protected synchronized Object aquire()
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

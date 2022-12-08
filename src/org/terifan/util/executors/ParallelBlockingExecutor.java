package org.terifan.util.executors;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * ParallelBlockingExecutor executes elements from a <code>Supplier</code> in parallel using a <code>Handler</code> with a user specified
 * number of worker threads.
 *
 * <pre>
 * List&lt;Integer&gt; supplier = Arrays.asList(1,2,3);
 * Handler&lt;Integer&gt; handler = i -&gt; System.out.println(i);
 * new ParallelBlockingExecutor(2).execute(supplier, handler);
 * </pre>
 */
public class ParallelBlockingExecutor
{
	private final Object mIteratorLock = new Object();
	private final Worker[] mWorkers;
	private boolean mCancelled;
	private int mShutdownCount;
	private Handler mHandler;
	private Supplier mSupplier;


	/**
	 * Create a new executor
	 *
	 * @param aNumThreads
	 *   a positive number equals number of threads to use, a negative number results in total available processors minus aNumThreads threads.
	 */
	public ParallelBlockingExecutor(int aThreads)
	{
		int count;
		if (aThreads > 0)
		{
			count = aThreads;
		}
		else if (aThreads < 0)
		{
			count = Math.max(1, ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() + aThreads);
		}
		else
		{
			throw new IllegalArgumentException();
		}

		mWorkers = new Worker[count];
	}


	/**
	 * Create a new executor
	 *
	 * @param aThreads
	 *   number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public ParallelBlockingExecutor(float aThreads)
	{
		if (aThreads < 0 || aThreads > 1)
		{
			throw new IllegalArgumentException();
		}

		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		int count = Math.max(1, Math.min(cpu, (int)Math.round(cpu * aThreads)));
		mWorkers = new Worker[count];
	}


	/**
	 * Cancel any current execution.
	 */
	public void cancel()
	{
		mCancelled = true;
	}


	/**
	 * Calls the Handler run method for each element provided by the Iterator in parallel. This method blocks until all elements have been
	 * handled. Acquiring a value from the Iterator is synchronized.
	 */
	public void executeRange(int aStartInclusive, int aEndExclusive, Handler<Integer> aHandler)
	{
		AtomicInteger counter = new AtomicInteger(aStartInclusive);
		execute(() -> {int index = counter.getAndIncrement(); return index >= aEndExclusive ? null : index;}, aHandler);
	}


	/**
	 * Calls the Handler run method for each element provided by the Iterator in parallel. This method blocks until all elements have been
	 * handled. Acquiring a value from the Iterator is synchronized.
	 */
	public <T> void execute(Iterator<T> aIterator, Handler<T> aHandler)
	{
		execute(() ->
		{
			synchronized (mIteratorLock)
			{
				if (!mCancelled && aIterator.hasNext())
				{
					return aIterator.next();
				}
			}
			return null;
		}, aHandler);
	}


	/**
	 * Calls the Handler run method for each element provided by the Supplier in parallel. This method blocks until all elements have been
	 * handled. A null value returned by the Supplier will stop the processing.
	 */
	public synchronized <T> void execute(Supplier<T> aSupplier, Handler<T> aHandler)
	{
		mSupplier = aSupplier;
		mHandler = aHandler;
		mShutdownCount = 0;
		mCancelled = false;

		for (int i = 0; i < mWorkers.length; i++)
		{
			mWorkers[i] = new Worker(i);
			mWorkers[i].start();
		}

		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
		}

		for (int i = 0; i < mWorkers.length; i++)
		{
			mWorkers[i] = null;
		}
	}


	private Object aquire()
	{
		Object parameter;

		if (mCancelled || (parameter = mSupplier.get()) == null)
		{
			synchronized (this)
			{
				mShutdownCount++;

				if (mShutdownCount == mWorkers.length)
				{
					mCancelled = true;
					notify();
				}
			}

			return null;
		}

		return parameter;
	}


	private class Worker<T> extends Thread
	{
		private int mIndex;

		public Worker(int aIndex)
		{
			mIndex = aIndex;
		}

		@Override
		public void run()
		{
			try
			{
				for (;;)
				{
					T parameter = (T)aquire();

					if (parameter == null)
					{
						break;
					}

					try
					{
						mHandler.run(parameter);
					}
					catch (Exception | Error e)
					{
						e.printStackTrace(System.out);
					}
				}
			}
			finally
			{
				mWorkers[mIndex] = null;
			}
		}
	}


	@FunctionalInterface
	public interface Handler<T>
	{
		void run(T aParameter) throws Exception;
	}
}

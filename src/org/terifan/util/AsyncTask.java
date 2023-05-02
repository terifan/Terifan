package org.terifan.util;

import java.util.concurrent.TimeUnit;
import org.terifan.util.executors.FixedThreadExecutor;


public abstract class AsyncTask<Param, Progress, Result>
{
	private static final Object LOCK = new Object();

	private static FixedThreadExecutor mExecutor;
	private static Object mParallelism = 1;

	private boolean mCancelled;
	private boolean mFinished;
	private Result mResult;


	protected AsyncTask()
	{
	}


	/**
	 * Sets how many concurrent threads will process tasks. This value is used next time an executor is created which occur when all
	 * pending tasks have been finished.
	 *
	 * @param aParallelism a positive number equals number of threads to use, zero or a negative number results in total available processors
	 */
	public static void setParallelism(int aParallelism)
	{
		mParallelism = aParallelism;
	}


	/**
	 * Sets how many concurrent threads will process tasks. This value is used next time an executor is created which occur when all
	 * pending tasks have been finished.
	 *
	 * @param aParallelism number of threads expressed as a number between 0 and 1 out of total available CPUs
	 */
	public static void setParallelism(float aParallelism)
	{
		mParallelism = aParallelism;
	}


	/**
	 * Convenience version of execute(java.lang.Object) for use with a simple Runnable object.
	 */
	public static void execute(Runnable aRunnable)
	{
		AsyncTask task = new AsyncTask()
		{
			@Override
			protected Object doInBackground(Object aParam) throws Throwable
			{
				aRunnable.run();
				return null;
			}
		};
		task.execute("");
	}


	/**
	 * Add this AsyncTask to the internal execution queue. When the AsyncTask is executed, methods onPreExecute, doInBackground,
	 * onPostExecute are called in that order.
	 */
	public final AsyncTask execute(Param aParam)
	{
		Runnable task = () ->
		{
			try
			{
				onPreExecute();

				Result result = doInBackground(aParam);

				if (mCancelled)
				{
					onCancelled(result);
				}
				else
				{
					onPostExecute(result);
				}
			}
			catch (Throwable e)
			{
				try
				{
					onError(e);
				}
				catch (Throwable ee)
				{
					// ignore
				}
			}
			finally
			{
				synchronized (LOCK)
				{
					mFinished = true;
					LOCK.notify();
				}
			}
		};

		synchronized (LOCK)
		{
			if (mExecutor == null)
			{
				if (mParallelism instanceof Float)
				{
					mExecutor = new FixedThreadExecutor((Float)mParallelism);
				}
				else
				{
					mExecutor = new FixedThreadExecutor((Integer)mParallelism);
				}
			}
			mExecutor.submit(task::run);
		}

		return this;
	}


	/**
	 * Implementation of the AsyncTask.
	 *
	 * @return this value will be forwarded to either <code>onPostExecute</code> or <code>onCancelled</code>.
	 */
	protected abstract Result doInBackground(Param aParam) throws Throwable;


	/**
	 * Called before <code>doInBackground</code>.
	 */
	protected void onPreExecute() throws Throwable
	{
	}


	/**
	 * Called after <code>doInBackground</code> unless there was an exception or task cancelled.
	 */
	protected void onPostExecute(Result aResult) throws Throwable
	{
	}


	/**
	 * This is an optional method that can be invoked from <code>doInBackground</code> to report progress.
	 */
	protected void onProgressUpdate(Progress aProgress)
	{
	}


	/**
	 * This is an optional method that can be invoked from <code>doInBackground</code> to update the result.
	 */
	protected void onResultUpdate(Result aResult)
	{
	}


	/**
	 * Called after <code>doInBackground</code> if the task was cancelled.
	 */
	protected void onCancelled(Result aResult) throws Throwable
	{
	}


	/**
	 * Called after <code>doInBackground</code> if there was an exception.
	 */
	protected void onError(Throwable aThrowable) throws Throwable
	{
	}


	/**
	 * @return if task was cancelled.
	 */
	public boolean isCancelled()
	{
		return mCancelled;
	}


	/**
	 * Request this task to be cancelled.
	 */
	public final void cancel()
	{
		mCancelled = true;
	}


	/**
	 * Retrieves the result or null if the computation isn't finished.
	 */
	public Result peek()
	{
		return mResult;
	}


	/**
	 * Waits if necessary for the computation to complete, and then retrieves its result.
	 */
	public Result get()
	{
		return get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}


	/**
	 * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result.
	 */
	public Result get(long aTimeOut, TimeUnit aTimeUnit)
	{
		long millis = aTimeUnit.toMillis(aTimeOut);

		for (long startTime = System.currentTimeMillis(); millis > System.currentTimeMillis() - startTime;)
		{
			synchronized (LOCK)
			{
				if (mFinished)
				{
					return mResult;
				}
				try
				{
					LOCK.wait(1000);
				}
				catch (InterruptedException ex)
				{
				}
			}
		}
		return mResult;
	}


	/**
	 * Waits if necessary until the currently queued tasks have been finished. Other threads can still enqueue new tasks.
	 */
	public static void waitFor()
	{
		FixedThreadExecutor prev = mExecutor;
		mExecutor = null;
		if (prev != null)
		{
			prev.close();
		}
	}


//	public static void main(String... args)
//	{
//		try
//		{
//			for (int i = 0; i < 1000; i++)
//			{
//				AsyncTask<Float, Integer, String> task = new AsyncTask<Float, Integer, String>()
//				{
//					@Override
//					protected String doInBackground(Float aParam) throws Throwable
//					{
//						for (int i = 0; i <= 10; i++)
//						{
//							onProgressUpdate(10 * i);
//						}
//						return "value=" + aParam;
//					}
//
//
//					@Override
//					protected void onPostExecute(String aResult) throws Throwable
//					{
//						System.out.println(aResult);
//					}
//
//
//					@Override
//					protected void onProgressUpdate(Integer aProgress)
//					{
//						System.out.println(aProgress + "%");
//					}
//				};
//
//				task.execute((float)i);
//			}
//
//			AsyncTask.execute(() -> System.out.println("#"));
//
//			System.out.println("#".repeat(100));
//
//			AsyncTask.waitFor();
//			AsyncTask.setParallelism(4);
//
//			System.out.println("!".repeat(100));
//
//			for (int i = 0; i < 1000; i++)
//			{
//				AsyncTask<Float, Integer, String> task = new AsyncTask<Float, Integer, String>()
//				{
//					@Override
//					protected String doInBackground(Float aParam) throws Throwable
//					{
//						for (int i = 0; i <= 10; i++)
//						{
//							onProgressUpdate(10 * i);
//						}
//						return "value=" + aParam;
//					}
//
//
//					@Override
//					protected void onPostExecute(String aResult) throws Throwable
//					{
//						System.out.println(aResult);
//					}
//
//
//					@Override
//					protected void onProgressUpdate(Integer aProgress)
//					{
//						System.out.println(aProgress + "%");
//					}
//				};
//
//				task.execute((float)i);
//			}
//
//			AsyncTask.execute(() -> System.out.println("#"));
//
//			System.out.println("#".repeat(100));
//
//			AsyncTask.finish();
//
//			System.out.println("!".repeat(100));
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

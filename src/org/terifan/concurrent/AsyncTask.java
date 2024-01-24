package org.terifan.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public abstract class AsyncTask<Param, Progress, Result>
{
	private static final Object LOCK = new Object();

	private static Consumer<Throwable> mGlobalExceptionHandler = e -> e.printStackTrace(System.err);
	private static ExecutorService mExecutor;

	private boolean mCancelled;
	private boolean mFinished;
	private Result mResult;


	protected AsyncTask()
	{
	}


	public static void setGlobalExceptionHandler(Consumer<Throwable> amGlobalExceptionHandler)
	{
		mGlobalExceptionHandler = amGlobalExceptionHandler;
	}


	/**
	 * Convenience version of execute to run a Runnable in the background.
	 */
	public static void execute(Runnable aTask)
	{
		AsyncTask task = new AsyncTask()
		{
			@Override
			protected Object doInBackground(Object aParam) throws Throwable
			{
				aTask.run();
				return null;
			}
		};
		task.execute();
	}


	/**
	 * Add this AsyncTask to the internal execution queue. When the AsyncTask is executed, methods onPreExecute, doInBackground,
	 * onPostExecute are called in that order. The parameter value is null.
	 */
	public final AsyncTask execute()
	{
		return execute((Param)null);
	}


	/**
	 * Add this AsyncTask to the internal execution queue. When the AsyncTask is executed, methods onPreExecute, doInBackground,
	 * onPostExecute are called in that order.
	 */
	public final AsyncTask execute(Param aParam)
	{
		Task task = () ->
		{
			try
			{
				if (mCancelled)
				{
					onCancelled(null);
					return;
				}

				onPreExecute();

				if (mCancelled)
				{
					onCancelled(null);
					return;
				}

				onPostExecute(doInBackground(aParam));
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
			if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated())
			{
				mExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, (BlockingQueue<Runnable>)new LinkedBlockingQueue())
				{
					@Override
					protected void afterExecute(Runnable aRunnable, Throwable aThrowable)
					{
						if (getQueue().isEmpty())
						{
							synchronized (LOCK)
							{
								if (mExecutor != null)
								{
									mExecutor.shutdown();
								}
							}
						}
					}
				};
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
	 * Called after <code>doInBackground</code> if there was an exception. This implementation invokes the GlobalExceptionHandler which by
	 * default print exception to error stream.
	 */
	protected void onError(Throwable aThrowable) throws Throwable
	{
		mGlobalExceptionHandler.accept(aThrowable);
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
		ExecutorService prev;
		synchronized (LOCK)
		{
			prev = mExecutor;
			mExecutor = null;
		}
		if (prev != null)
		{
			try
			{
				prev.shutdown();
				prev.awaitTermination(1, TimeUnit.DAYS);
			}
			catch (Exception e)
			{
			}
		}
	}


	/**
	 * Extension of a Runnable that catch exceptions and invokes the GlobalExceptionHandler which by default print exception to error stream.
	 */
	@FunctionalInterface
	public interface Task extends Runnable
	{
		void execute() throws Exception;

		@Override
		default void run()
		{
			try
			{
				execute();
			}
			catch (Throwable e)
			{
				mGlobalExceptionHandler.accept(e);
			}
		}
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			AsyncTask<String, Integer, String> task1 = new AsyncTask<String, Integer, String>()
//			{
//				@Override
//				protected String doInBackground(String aParam) throws Throwable
//				{
//					System.out.println("started 1");
//					Thread.sleep(2000);
//					System.out.println("finished 1");
//					return "";
//				}
//
//
//				@Override
//				protected void onCancelled(String aResult) throws Throwable
//				{
//					System.out.println("cancelled 1");
//				}
//
//
//				@Override
//				protected void onError(Throwable aThrowable) throws Throwable
//				{
//					System.out.println("error 1");
//				}
//
//
//				@Override
//				protected void onPreExecute() throws Throwable
//				{
//					System.out.println("pre 1");
//				}
//
//
//				@Override
//				protected void onPostExecute(String aResult) throws Throwable
//				{
//					System.out.println("post 1");
//				}
//
//
//				@Override
//				protected void onResultUpdate(String aResult)
//				{
//					System.out.println("result update 1");
//				}
//
//
//				@Override
//				protected void onProgressUpdate(Integer aProgress)
//				{
//					System.out.println("update 1");
//				}
//			}.execute();
//
//			System.out.println("waiting 1");
//
//			AsyncTask<String, Integer, String> task2 = new AsyncTask<String, Integer, String>()
//			{
//				@Override
//				protected String doInBackground(String aParam) throws Throwable
//				{
//					System.out.println("started 2");
//					Thread.sleep(2000);
//					System.out.println("finished 2");
//					return "";
//				}
//
//
//				@Override
//				protected void onCancelled(String aResult) throws Throwable
//				{
//					System.out.println("cancelled 2");
//				}
//
//
//				@Override
//				protected void onError(Throwable aThrowable) throws Throwable
//				{
//					System.out.println("error 2");
//				}
//
//
//				@Override
//				protected void onPreExecute() throws Throwable
//				{
//					System.out.println("pre 2");
//				}
//
//
//				@Override
//				protected void onPostExecute(String aResult) throws Throwable
//				{
//					System.out.println("post 2");
//				}
//
//
//				@Override
//				protected void onResultUpdate(String aResult)
//				{
//					System.out.println("result update 2");
//				}
//
//
//				@Override
//				protected void onProgressUpdate(Integer aProgress)
//				{
//					System.out.println("update 2");
//				}
//			}.execute();
//
//			System.out.println("waiting 2");
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}


//	public static void xmain(String... args)
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
//			AsyncTask.execute(() -> System.out.println("+".repeat(100)));
//
//			System.out.println("#".repeat(100));
//
//			new Thread()
//			{
//				@Override
//				public void run()
//				{
//					for (int _i = 0; _i < 100; _i++)
//					{
//						int i = _i;
//						AsyncTask.execute(() -> System.out.println("*".repeat(10)+" "+i));
//					}
//				}
//			}.start();
//
//			AsyncTask.waitFor();
//
//			System.out.println("-".repeat(100));
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

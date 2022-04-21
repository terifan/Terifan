package org.terifan.util;

import java.util.concurrent.Future;


/**
 * The TimeLimitedExecutor executes a Runnable for a maximum time. If the time limit is reached the working thread will be interrupted.
 *
 * <pre>
 * String s = new TimeLimitedExecutor<String>().waitFor(5000, work).getResult();
 * </pre>
 */
public class TimeLimitedExecutor<R>
{
	private final Object mLock;
	private long mStartTime;
	private long mStopTime;
	private boolean mExpired;
	private boolean mRunning;
	private boolean mInterrupted;
	private boolean mInterruptOnTimeLimit;
	private R mResult;
	private Exception mException;


	public TimeLimitedExecutor()
	{
		mLock = new Object();
		mInterruptOnTimeLimit = true;
	}


	/**
	 * Sets whether or not the worker thread should be interrupted or not. Default is true.
	 */
	public TimeLimitedExecutor<R> setInterruptOnTimeLimit(boolean aInterruptOnTimeLimit)
	{
		mInterruptOnTimeLimit = aInterruptOnTimeLimit;
		return this;
	}


	/**
	 * Return true if the background thread didn't finished before timeout.
	 */
	public boolean isExpired()
	{
		return mExpired;
	}


	/**
	 * Return true if the work finished within the timeout period. If the response is false then a background thread might still be working.
	 */
	public TimeLimitedExecutorResult<R> waitFor(long aTimeOut, Work<R> aWork)
	{
		if (mStartTime != 0)
		{
			throw new IllegalArgumentException("Instances cannot be reused.");
		}

		Thread thread = new Thread()
		{
			{
				setDaemon(true);
			}


			@Override
			public void run()
			{
				mStartTime = System.currentTimeMillis();
				mRunning = true;
				try
				{
					mResult = aWork.run(TimeLimitedExecutor.this);
				}
				catch (InterruptedException e)
				{
					mInterrupted = true;
				}
				catch (Exception e)
				{
					mException = e;
				}
				catch (Error e)
				{
					mException = new WrappedException(e);
				}
				finally
				{
					mRunning = false;
					mStopTime = System.currentTimeMillis();
					try
					{
						synchronized (mLock)
						{
							mLock.notify();
						}
					}
					catch (Throwable e)
					{
						// ignore
					}
				}
			}
		};
		thread.start();

		try
		{
			synchronized (mLock)
			{
				mLock.wait(mStartTime + aTimeOut);
			}

			mExpired = mRunning;

			if (mRunning && mInterruptOnTimeLimit)
			{
				thread.interrupt();
			}
		}
		catch (Exception | Error e)
		{
			// ignore
		}

		return new TimeLimitedExecutorResult<>(this);
	}


	/**
	 * This method will wait indefinitely until the background thread has finished.
	 */
	public void awaitTermination()
	{
		try
		{
			while (mRunning)
			{
				synchronized (mLock)
				{
					mLock.wait(1000);
				}
			}
		}
		catch (Exception | Error e)
		{
			// ignore
		}
	}


	@FunctionalInterface
	public static interface Work<R>
	{
		R run(TimeLimitedExecutor<R> aExecutor) throws Exception;
	}


	@FunctionalInterface
	public static interface Handler<T>
	{
		void accept(T aValue) throws Exception;
	}


	public static class WrappedException extends RuntimeException
	{
		public WrappedException(Throwable aThrwbl)
		{
			super(aThrwbl);
		}
	}


	public static class TimeLimitedExecutorResult<R>
	{
		TimeLimitedExecutor<R> mExecutor;


		TimeLimitedExecutorResult(TimeLimitedExecutor aExecutor)
		{
			mExecutor = aExecutor;
		}


		public TimeLimitedExecutorResult<R> onExpired(Handler<TimeLimitedExecutor<R>> aHandler)
		{
			if (mExecutor.mInterrupted || mExecutor.mExpired)
			{
				try
				{
					aHandler.accept(mExecutor);
				}
				catch (RuntimeException e)
				{
					throw e;
				}
				catch (Exception | Error e)
				{
					throw new WrappedException(e);
				}
			}
			return this;
		}


		public TimeLimitedExecutorResult<R> onException(Handler<Exception> aHandler)
		{
			if (mExecutor.mException != null)
			{
				try
				{
					aHandler.accept(mExecutor.mException);
				}
				catch (RuntimeException e)
				{
					throw e;
				}
				catch (Exception | Error e)
				{
					throw new WrappedException(e);
				}
			}
			return this;
		}


		public TimeLimitedExecutorResult<R> onDone(Handler<R> aHandler)
		{
			if (isDone())
			{
				try
				{
					aHandler.accept(mExecutor.mResult);
				}
				catch (RuntimeException e)
				{
					throw e;
				}
				catch (Exception | Error e)
				{
					throw new WrappedException(e);
				}
			}
			return this;
		}


		public TimeLimitedExecutorResult<R> awaitTermination()
		{
			mExecutor.awaitTermination();
			return this;
		}


		public boolean isDone()
		{
			return !mExecutor.mRunning && !mExecutor.mExpired;
		}


		public boolean isExpired()
		{
			return mExecutor.mExpired;
		}


		public boolean isRunning()
		{
			return mExecutor.mRunning;
		}


		public R get()
		{
			return mExecutor.mResult;
		}


		public Exception getException()
		{
			return mExecutor.mException;
		}


		/**
		 * Return when background thread started.
		 */
		public long getStartTime()
		{
			return mExecutor.mStartTime;
		}


		/**
		 * Return when background thread finished. Will return zero until thread has stopped.
		 */
		public long getStopTime()
		{
			return mExecutor.mStopTime;
		}
	}


	public static void main(String... args)
	{
		try
		{
			long time = System.currentTimeMillis();

			Work<String> work = e ->
			{
				Thread.sleep(7000);

				if (e.isExpired())
				{
					System.out.println("rollback");
					throw new IllegalStateException("rollback");
				}

				return "bobby";
			};

			String s = new TimeLimitedExecutor<String>()
//				.setInterruptOnTimeLimit(false)
				.waitFor(5000, work)
				.onExpired(e -> System.out.println("timeout"))
				.onException(e -> {throw e;})
				.onDone(e -> System.out.println(e))
				.awaitTermination()
				.onException(e -> {throw e;})
				.get();

			System.out.println(System.currentTimeMillis() - time);
			System.out.println(s);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

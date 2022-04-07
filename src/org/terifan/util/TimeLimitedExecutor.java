package org.terifan.util;

import java.util.function.Consumer;


/**
 * An executor implementation that blocks for a maximum time or until a work has finished.
 *
 * <pre>
 *  TimeLimitedExecutor executor = new TimeLimitedExecutor();
 *
 * 	Work work = () ->
 * 	{
 * 		// perform some work
 * 		Thread.sleep(7000);
 *
 * 		if (executor.isExpired())
 * 		{
 * 			// rollback if possible
 * 		}
 * 	};
 *
 * 	if (executor.waitFor(5000, work))
 * 	{
 * 		// job finshed
 * 	}
 *
 *  // optional
 * 	executor.awaitTermination();
 * </pre>
 */
public class TimeLimitedExecutor
{
	private final Object mLock;
	private long mStartTime;
	private long mStopTime;
	private boolean mExpired;
	private boolean mRunning;
	private Consumer<Throwable> mErrorListener;


	public TimeLimitedExecutor()
	{
		mLock = new Object();
	}


	/**
	 * This listener is invoked with any exception the background thread is throwing.
	 */
	public TimeLimitedExecutor setErrorListener(Consumer<Throwable> aErrorListener)
	{
		mErrorListener = aErrorListener;
		return this;
	}


	/**
	 * Return when background thread started.
	 */
	public long getStartTime()
	{
		return mStartTime;
	}


	/**
	 * Return when background thread finished. Will return zero until thread has stopped.
	 */
	public long getStopTime()
	{
		return mStopTime;
	}


	/**
	 * Return true if the background thread didn't finished before timeout.
	 */
	public boolean isExpired()
	{
		return mExpired;
	}


	/**
	 * Return true if the background thread is still running.
	 */
	public boolean isRunning()
	{
		return mRunning;
	}


	/**
	 * Return true if the work finished within the timeout period. If the response is false then a background thread might still be working.
	 */
	public boolean waitFor(long aTimeOut, Work aWork)
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
					aWork.run();
				}
				catch (Exception | Error e)
				{
					if (mErrorListener != null)
					{
						mErrorListener.accept(e);
					}
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
		}
		catch (Exception | Error e)
		{
			// ignore
		}

		mExpired = mRunning;

		return !mExpired;
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


	public static interface Work
	{
		void run() throws Exception;
	}


	public static void main(String... args)
	{
		try
		{
			long time = System.currentTimeMillis();

			TimeLimitedExecutor timeoutLock = new TimeLimitedExecutor();

			StringBuilder sb = new StringBuilder();

			Work work = () ->
			{
				Thread.sleep(7000);
				sb.append("done");

				if (timeoutLock.isExpired())
				{
					sb.append("---rollback");
				}
			};

			boolean b = timeoutLock.waitFor(5000, work);

			System.out.println(System.currentTimeMillis() - time);
			System.out.println(b);

			timeoutLock.awaitTermination();

			System.out.println(sb);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

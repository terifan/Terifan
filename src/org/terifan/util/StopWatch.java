package org.terifan.util;

import java.util.Arrays;


/**
 * Simple utility class for measuring time.
 *
 * <pre>
 * StopWatch sw = new StopWatch();
 * // do something
 * System.out.println(sw);
 * </pre>
 *
 * <pre>
 * StopWatch sw = new StopWatch("first task");
 * // do something
 * sw.suspend();
 * // do something not included in measurement
 * sw.resume();
 * // do something
 * sw.split("second task");
 * // do something
 * sw.split();
 * // do something
 * sw.split("third task");
 * // do something
 * System.out.println(sw);
 * </pre>
 */
public final class StopWatch
{
	private long mStartTime;
	private long mStopTime;
	private long mSuspendTime;
	private long mAdjustTime;
	private int mSplitCount;
	private long[] mSplitTime;
	private String[] mSplitLabel;
	private String mStartLabel;


	/**
	 * Creates a new instance and also starts the StopWatch.
	 */
	public StopWatch()
	{
		this(null);
	}


	/**
	 * Creates a new instance and also starts the StopWatch.
	 */
	public StopWatch(String aStartLabel)
	{
		start(aStartLabel);
	}


	/**
	 * Start the StopWatch.
	 */
	public void start()
	{
		start(null);
	}


	public synchronized void start(String aStartLabel)
	{
		mStartLabel = aStartLabel;
		mStartTime = System.nanoTime();
		mStopTime = 0;
		mSplitCount = 0;
		mSplitTime = new long[4];
		mSplitLabel = new String[4];
		mSuspendTime = 0;
		mAdjustTime = 0;
	}


	/**
	 * Stops the StopWatch.
	 */
	public void stop()
	{
		if (mStartTime == 0)
		{
			start();
		}

		if (mSuspendTime != 0)
		{
			resume();
		}

		mStopTime = System.nanoTime();
	}


	public void suspend()
	{
		if (mSuspendTime == 0)
		{
			mSuspendTime = System.nanoTime();
		}
	}


	public void resume()
	{
		if (mSuspendTime == 0)
		{
			throw new IllegalStateException("StopWatch isn't suspended.");
		}
		mAdjustTime += System.nanoTime() - mSuspendTime;
		mSuspendTime = 0;
	}


	/**
	 * Return the elapsed time between the StopWatch was started and either the current time or the stop time in milliseconds.
	 */
	public long getMillisTime()
	{
		return getNanoTime() / 1000000;
	}


	/**
	 * Return the elapsed time between the StopWatch was started and either the current time or the stop time in microseconds.
	 */
	public long getMicrosTime()
	{
		return getNanoTime() / 1000;
	}


	/**
	 * Return the elapsed time between the StopWatch was started and either the current time or the stop time in nanoseconds.
	 */
	public long getNanoTime()
	{
		long adjust = mAdjustTime;
		if (mSuspendTime != 0)
		{
			adjust += System.nanoTime() - mSuspendTime;
		}
		if (mStopTime == 0)
		{
			return System.nanoTime() - mStartTime - adjust;
		}
		else
		{
			return mStopTime - mStartTime - adjust;
		}
	}


	public void split()
	{
		split(null);
	}


	public synchronized void split(String aLabel)
	{
		if (mSuspendTime != 0)
		{
			throw new IllegalStateException("Cannot split while StopWatch is suspended.");
		}
		if (mStopTime != 0)
		{
			throw new IllegalStateException("StopWatch already stopped when split was called.");
		}
		if (mSplitCount == mSplitTime.length)
		{
			mSplitTime = Arrays.copyOfRange(mSplitTime, 0, mSplitCount * 3 / 2 + 1);
			mSplitLabel = Arrays.copyOfRange(mSplitLabel, 0, mSplitCount * 3 / 2 + 1);
		}
		mSplitLabel[mSplitCount] = aLabel;
		mSplitTime[mSplitCount++] = System.nanoTime() - mAdjustTime;
	}


	/**
	 * Return the elapsed time between the StopWatch was started and either the current time or the stop time as a String.
	 */
	@Override
	public synchronized String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (mStartLabel != null)
		{
			sb.append(mStartLabel).append(": ");
		}
		long prev = mStartTime;
		for (int i = 0; i < mSplitCount; i++)
		{
			format(mSplitTime[i] - prev, sb);
			prev = mSplitTime[i];
			sb.append(", ");
			if (mSplitLabel[i] != null)
			{
				sb.append(mSplitLabel[i]).append(": ");
			}
		}
		long adjust = mAdjustTime;
		if (mSuspendTime != 0)
		{
			adjust += System.nanoTime() - mSuspendTime;
		}
		if (mStopTime != 0)
		{
			format(mStopTime - prev - adjust, sb);
		}
		else
		{
			format(System.nanoTime() - prev - adjust, sb);
		}
		return sb.toString();
	}


	private StringBuilder format(long aTime, StringBuilder aBuffer)
	{
		aTime /= 1000000;

		int i = aBuffer.length();

		aBuffer.insert(i, aTime % 10);
		aBuffer.insert(i, aTime/10 % 10);
		aBuffer.insert(i, aTime/100 % 10);
		aBuffer.insert(i, ".");
		aTime /= 1000;
		aBuffer.insert(i, aTime % 60);
		aTime /= 60;
		if (aTime > 0)
		{
			aBuffer.insert(i, aTime % 60);
			aTime /= 60;
			if (aTime > 0)
			{
				aBuffer.insert(i, ":");
				aBuffer.insert(i, aTime % 24);
				aTime /= 24;
				if (aTime > 0)
				{
					aBuffer.insert(i, ":");
					aBuffer.insert(i, aTime);
				}
			}
		}

		return aBuffer;
	}


	public static void main(String... args)
	{
		try
		{
			StopWatch w = new StopWatch("first task");
			Thread.sleep(200);
			w.split("second task");
			Thread.sleep(200);
				w.suspend();
				Thread.sleep(200);
				w.resume();
			Thread.sleep(100);
			w.split();
			Thread.sleep(200);
			w.split("third task");
			Thread.sleep(200);
				w.suspend();
				Thread.sleep(200);
			System.out.println(w);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

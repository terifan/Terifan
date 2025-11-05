package org.terifan.util;

import java.util.Timer;
import java.util.TimerTask;


/**
 * DelayedExecutor waits a certain time before executing a task. If another task is scheduled any pending task is cancelled.
 * <pre>
 *   DelayedExecutor exe = new DelayedExecutor();
 *   exe.schedule(() -> System.out.println("hello"), 1000);
 *   exe.schedule(() -> System.out.println("world"), 1000);
 * </pre>
 */
public class DelayedExecutor
{
	private TimerTask mTask;
	private Timer mTimer;


	public DelayedExecutor()
	{
		mTimer = new Timer("DelayedExecutor", true);
	}


	public boolean isPending()
	{
		return mTask != null;
	}


	/**
	 * Schedule an execution automatically cancelling any pending task.
	 * @return true if a pending task was cancelled
	 */
	public boolean schedule(Runnable aRunnable, long aDelay)
	{
		boolean pending = false;
		if (mTask != null)
		{
			mTask.cancel();
			pending = true;
		}
		mTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if (mTask != null)
				{
					aRunnable.run();
					mTask = null;
				}
			}
		};
		mTimer.schedule(mTask, aDelay);
		return pending;
	}


	/**
	 * @return true if the task was run
	 */
	public boolean runImmediately()
	{
		TimerTask task = mTask;
		if (task != null)
		{
			task.cancel();
			task.run();
			mTask = null;
			return true;
		}
		return false;
	}


	/**
	 * Cancel the task
	 * @return true if the task was run
	 */
	public boolean cancel()
	{
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
			return true;
		}
		return false;
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			DelayedExecutor exe = new DelayedExecutor();
//			exe.schedule(()->System.out.println("run"), 1000);
//			Thread.sleep(1500);
//			exe.schedule(()->System.out.println("run"), 1000);
//			Thread.sleep(500);
//			exe.schedule(()->System.out.println("run"), 1000);
//			System.out.println(exe.runImmediately());
//			Thread.sleep(1500);
//			exe.schedule(()->System.out.println("run"), 1000);
//			Thread.sleep(1500);
//			exe.cancel();
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

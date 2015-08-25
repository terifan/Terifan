package org.terifan.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Execute runnable tasks within specified time limits.
 */
public class TimeLimitedExecutor
{
	/**
	 * Execute a runnable task within the specified time limit or terminates the thread.
	 *
	 * @return
	 *   true if the task execution completed
	 */
	public static boolean run(Runnable aRunnable, int aTimeLimitMilliSeconds) throws ExecutionException
	{
		try
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
			ThreadFactory threadFactory = executor.getThreadFactory();
			executor.setThreadFactory((r) -> {
				Thread newThread = threadFactory.newThread(r);
				newThread.setDaemon(true); // must be set for the thread to terminate correctly when the main thread shuts down
				return newThread;
			});

			Future<?> future = executor.submit(aRunnable);
			executor.shutdown();
			boolean b = true;

			try
			{
				future.get(aTimeLimitMilliSeconds, TimeUnit.MILLISECONDS);
			}
			catch (TimeoutException e)
			{
				future.cancel(true);
				b = false;
			}

			if (!executor.awaitTermination(2, TimeUnit.SECONDS)) // todo: 2 seconds, good or bad?
			{
				executor.shutdownNow();
				b = false;
			}

			return b;
		}
		catch (InterruptedException e)
		{
			return false;
		}
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			boolean result = TimeLimitedExecutor.run(()->
//			{
////				try{Thread.sleep(1000);}catch(Exception e){} throw new RuntimeException("xx");
////				try{Thread.sleep(1000);}catch(Exception e){}Log.out.println("a");
//				for(;;);
//			}, 5);
//
//			Log.out.println(result);
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

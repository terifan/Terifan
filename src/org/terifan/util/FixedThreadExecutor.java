package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Helper class replacing Executors.newFixedThreadPool()
 */
public class FixedThreadExecutor implements AutoCloseable
{
	private ExecutorService mExecutorService;
	private int mThreads;


	public FixedThreadExecutor(int aThreads)
	{
		mThreads = aThreads;
		init();
	}


	public FixedThreadExecutor(double aThreads)
	{
		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		mThreads = Math.max(1, (int)Math.ceil(cpu * aThreads));

		init();
	}


	public void submit(Runnable aRunnable)
	{
		init();
		mExecutorService.submit(aRunnable);
	}


	public void submit(Runnable... aRunnables)
	{
		init();
		for (Runnable r : aRunnables)
		{
			mExecutorService.submit(r);
		}
	}


	public void submit(Iterable<? extends Runnable> aRunnables)
	{
		init();
		for (Runnable r : aRunnables)
		{
			mExecutorService.submit(r);
		}
	}


	@Override
	public void close()
	{
		close(Long.MAX_VALUE);
	}


	private boolean close(long aWaitMillis)
	{
		try
		{
			mExecutorService.shutdown();

			mExecutorService.awaitTermination(aWaitMillis, TimeUnit.MILLISECONDS);

			return false;
		}
		catch (InterruptedException e)
		{
			return true;
		}
		finally
		{
			mExecutorService = null;
		}
	}


	private void init()
	{
		if (mExecutorService == null)
		{
			mExecutorService = Executors.newFixedThreadPool(mThreads);
		}
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			FixedThreadExecutor p = new FixedThreadExecutor(2);
//			p.submit(new Runnable(){public void run(){Log.out.println("1");}},
//			new Runnable(){public void run(){Log.out.println("2");}},
//			new Runnable(){public void run(){Log.out.println("3");}},
//			new Runnable(){public void run(){Log.out.println("4");}},
//			new Runnable(){public void run(){Log.out.println("5");}},
//			new Runnable(){public void run(){Log.out.println("6");}},
//			new Runnable(){public void run(){Log.out.println("7");}});
//
//			p.close();
//
//			p.submit(new Runnable(){public void run(){Log.out.println("1");}},
//			new Runnable(){public void run(){Log.out.println("2");}},
//			new Runnable(){public void run(){Log.out.println("3");}},
//			new Runnable(){public void run(){Log.out.println("4");}},
//			new Runnable(){public void run(){Log.out.println("5");}},
//			new Runnable(){public void run(){Log.out.println("6");}},
//			new Runnable(){public void run(){Log.out.println("7");}});
//
//			p.close();
//
//			ArrayList<Task> list2 = new ArrayList<>();
//			list2.add(new Task(1));
//			list2.add(new Task(2));
//			list2.add(new Task(3));
//			list2.add(new Task(4));
//			list2.add(new Task(5));
//			list2.add(new Task(6));
//			list2.add(new Task(7));
//			p.submit(list2);
//
//			p.close();
//
//			for (final Task task : list2)
//			{
//				p.submit(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						task.run();
//					}
//				});
//			}
//
//			p.close();
//
//			try (FixedThreadExecutor q = new FixedThreadExecutor(2))
//			{
//				q.submit(new Task(1));
//				q.submit(new Task(2));
//			}
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
//
//	static class Task implements Runnable
//	{
//		int i;
//
//		public Task(int i)
//		{
//			this.i = i;
//		}
//
//		@Override
//		public void run()
//		{
//			Log.out.println(i);
//		}
//	}
}

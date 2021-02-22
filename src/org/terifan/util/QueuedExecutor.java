package org.terifan.util;

import java.util.ArrayList;


/**
 * Queue tasks to be executed after/before some process is initialized/destroyed. This class is useful for batch writing records to a file or database.
 */
public abstract class QueuedExecutor<T>
{
	private final ArrayList<T> mTasks;
	private final long mExecutionDelay;
	private final long mDestroyDelay;
	private Worker mWorker;


	/**
	 * Create a new QueuedExecutor.
	 *
	 * @param aExecutionDelay
	 *   the delay before execution starts after a task has been scheduled.
	 * @param aDestroyDelay
	 *   the delay before execution stops after the last task has been executed.
	 */
	public QueuedExecutor(long aExecutionDelay, long aDestroyDelay)
	{
		mTasks = new ArrayList<>();
		mExecutionDelay = aExecutionDelay;
		mDestroyDelay = aDestroyDelay;
	}


	/**
	 * Add a task to the executor. Tasks can be scheduled while the executor is working. If no executor is working then an executor instance
	 * will be started.
	 */
	public void schedule(T aTask)
	{
		synchronized (mTasks)
		{
			mTasks.add(aTask);
		}

		synchronized (mTasks)
		{
			if (mWorker == null)
			{
				mWorker = new Worker();
				mWorker.start();
			}
		}
	}


	private class Worker extends Thread
	{
		private final QueuedExecutor<T> mExecutor;


		public Worker()
		{
			mExecutor = QueuedExecutor.this;
		}


		@Override
		public void run()
		{
			try
			{
				Thread.sleep(mExecutionDelay);
			}
			catch (InterruptedException e)
			{
			}

			synchronized (mTasks)
			{
				mExecutor.init();
			}

			for (;;)
			{
				for (;;)
				{
					T task;
					synchronized (mTasks)
					{
						if (mTasks.isEmpty())
						{
							break;
						}
						task = mTasks.remove(0);
					}
					execute(task);
				}

				try
				{
					Thread.sleep(mDestroyDelay);
				}
				catch (InterruptedException e)
				{
				}

				synchronized (mTasks)
				{
					if (mTasks.isEmpty())
					{
						break;
					}
				}
			}

			synchronized (mTasks)
			{
				mWorker = null;
				mExecutor.destroy();
			}
		}
	}


	/**
	 * Override this method and implement the initialization of the executor such as opening a database or file stream.
	 */
	protected void init()
	{
	}


	/**
	 * Override this method and implement the shutdown of the executor such as closing a database or file stream.
	 */
	protected void destroy()
	{
	}


	/**
	 * Implement this method to execute some code on the task.
	 */
	protected abstract void execute(T aTask);


//	public static void main(String ... args)
//	{
//		try
//		{
//			QueuedExecutor<String> executor = new QueuedExecutor<String>(100, 100)
//			{
//				@Override
//				protected void init()
//				{
//					System.out.println("init");
//				}
//				@Override
//				protected void destroy()
//				{
//					System.out.println("destroy");
//				}
//				@Override
//				protected void execute(String aTask)
//				{
//					System.out.println("\t" + aTask);
//					try
//					{
//						Thread.sleep(new java.util.Random().nextInt(100));
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace(System.out);
//					}
//				}
//			};
//
//			for (int i = 0;i<10000;i++)
//			{
//				System.out.println("+schedule " + i);
//				executor.schedule("task " + i);
//				Thread.sleep(new java.util.Random().nextInt(200));
//			}
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

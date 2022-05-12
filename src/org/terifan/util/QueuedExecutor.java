package org.terifan.util;

import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * Executor executing tasks in a background thread. The initializer method is always called when starting executing items and when the queue
 * is emptied a destroy method is called.
 * <p>
 * The initializer can be used to open a file/database while the destroyer can be used to close file/database.
 * </p>
 */
public class QueuedExecutor<T>
{
	private final Object mShuttingDownLock = new Object();
	private final ArrayList<T> mTasks;
	private final long mDestroyDelay;
	private boolean mShuttingDown;
	private Worker mWorker;
	private Consumer<T> mHandler;
	private Runnable mInitializer;
	private Runnable mDestroyer;


	/**
	 * Create a new QueuedExecutor with a lifetime of one second.
	 */
	public QueuedExecutor()
	{
		this(1000);
	}


	/**
	 * Create a new QueuedExecutor.
	 *
	 * @param aExecutionDelay
	 *   the delay before execution starts after a task has been scheduled.
	 * @param aDestroyDelay
	 *   the delay before execution stops after the last task has been executed.
	 */
	public QueuedExecutor(long aDestroyDelay)
	{
		mTasks = new ArrayList<>();
		mDestroyDelay = aDestroyDelay;
		mHandler = dummy -> {};
		mInitializer = () -> {};
		mDestroyer = () -> {};
	}


	public QueuedExecutor setHandler(Consumer<T> aHandler)
	{
		mHandler = aHandler;
		return this;
	}


	public QueuedExecutor setInitializer(Runnable aInitializer)
	{
		mInitializer = aInitializer;
		return this;
	}


	public QueuedExecutor setDestroyer(Runnable aDestroyer)
	{
		mDestroyer = aDestroyer;
		return this;
	}


	/**
	 * Add a task to the executor. Tasks can be scheduled while the executor is working. If no executor is working then an executor instance
	 * will be started.
	 */
	public void schedule(T aTask)
	{
		if (mShuttingDown)
		{
			throw new IllegalStateException("Executor is shutting down and doesn't accept any more tasks");
		}

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


	public void shutdown()
	{
		synchronized (mShuttingDownLock)
		{
			mShuttingDown = true;

			try
			{
				mShuttingDownLock.wait();
			}
			catch (InterruptedException e)
			{
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
			synchronized (mTasks)
			{
				mInitializer.run();
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
					mHandler.accept(task);
				}

				synchronized (mShuttingDownLock)
				{
					if (mShuttingDown)
					{
						mShuttingDownLock.notify();
						break;
					}
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
				mDestroyer.run();
			}
		}
	}
}

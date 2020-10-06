package org.terifan.util;


public abstract class AsyncTask<Result, Progress>
{
	private boolean mCancelled;


	protected AsyncTask()
	{
	}


	/**
	 * Call this to start the AsyncTask. This method invokes onPreExecute, doInBackground, onPostExecute in that order.
	 */
	public final AsyncTask execute()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					onPreExecute();

					Result result = doInBackground();

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
			}
		}.start();

		return this;
	}


	/**
	 * Convenience method to invoke a Runnable. Methods onCancelled and onPostExecute are called with a <code>null</code> as parameter.
	 */
	public final AsyncTask execute(Runnable aRunnable)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					onPreExecute();

					aRunnable.run();

					if (mCancelled)
					{
						onCancelled(null);
					}
					else
					{
						onPostExecute(null);
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
			}
		}.start();

		return this;
	}


	/**
	 * Implementation of the AsyncTask.
	 *
	 * @return
	 *   this value will be forwarded to either <code>onPostExecute</code> or <code>onCancelled</code>.
	 */
	protected abstract Result doInBackground() throws Throwable;


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
	 * This method can be invoked from <code>doInBackground</code> to report progress.
	 */
	protected void onProgressUpdate(Progress aProgress)
	{
	}


	/**
	 * This method can be invoked from <code>doInBackground</code> to update the result.
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
}
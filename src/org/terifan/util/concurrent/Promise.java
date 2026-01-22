package org.terifan.util.concurrent;

import java.util.function.Consumer;


public class Promise<T>
{
	private Throwable mThrowable;
	private boolean mFinished;
	private T mOutput;
	private Consumer<T> mHandler;


	public Promise(Task<T> aFunction)
	{
		try
		{
			// thread
			complete(aFunction.run());
		}
		catch (Throwable e)
		{
			mThrowable = e;
		}
	}


	public Promise<T> complete(T aValue)
	{
		if (!mFinished)
		{
			mFinished = true;
			mOutput = aValue;

			if (mHandler != null)
			{
				mHandler.accept(mOutput);
			}
		}
		return this;
	}


	public Promise<T> reject(Throwable aThrowable)
	{
		mFinished = true;
		mThrowable = aThrowable;
		return this;
	}


	public T get()
	{
		// block
		return mOutput;
	}


	public T getNow()
	{
		return mOutput;
	}


	public Promise then(Consumer<T> aSuccess)
	{
		return then(aSuccess, null);
	}


	public Promise then(Consumer<T> aSuccess, Consumer<Throwable> aFailure)
	{
		// block
		if (mThrowable != null)
		{
			if (aFailure != null)
			{
				aFailure.accept(mThrowable);
			}
		}
		else
		{
			aSuccess.accept(mOutput);
		}
		return this;
	}


	public Promise onError(Consumer<Throwable> aFailure)
	{
		// block
		if (mThrowable != null)
		{
			aFailure.accept(mThrowable);
		}
		return this;
	}


	public Promise onFinally(Runnable aRunnable)
	{
		// block
		aRunnable.run();
		return this;
	}


	public Promise handle(Consumer<T> aHandler)
	{
		mHandler = aHandler;
		if (mFinished)
		{
			mHandler.accept(mOutput);
		}
		return this;
	}


	@FunctionalInterface
	public interface Task<T>
	{
		T run() throws Exception;
	}
}

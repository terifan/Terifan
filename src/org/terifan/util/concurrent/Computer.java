package org.terifan.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.StampedLock;


/**
 * A Computer computes something in parallel. The constructor will invoke the <code>Callable</code> object and the <code>get</code> method
 * will block until the value has finished computing.
 *
 * <pre>
 * 	Computer<String> a = new Computer<>(() -> "a");
 *	Computer<String> b = new Computer<>(() -> "b");
 *	System.out.println(a.get() + b.get());
 * </pre>
 */
public class Computer<T>
{
	private T mValue;
	private ReadWriteLock mLock;
	private Exception mException;
	private Callable<T> mCallable;


	public Computer(Callable<T> aCallable)
	{
		mCallable = aCallable;
		mLock = new StampedLock().asReadWriteLock();
		compute();
	}


	/**
	 * Blocks until the Callable provided in the constructor has produced a value and return the value.
	 *
	 * @return the value produced by the Callable provided in the constructor.
	 */
	public T get()
	{
		try
		{
			mLock.readLock().lock();
			if (mException != null)
			{
				throw new IllegalStateException(mException);
			}
			return mValue;
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	/**
	 * Invokes the Callable provided in the constructor and return the result via the <code>get</code> method.
	 * <br/>
	 * Note: the constructor will invoke the Callable. Only use this method to re-run the Callable.
	 */
	public T next()
	{
		compute();
		return get();
	}


	/**
	 * Same as invoking the <code>get</code> method. This method will clean-up all internal state and any further calls will cause an exception.
	 *
	 * @return the value produced by the Callable provided in the constructor.
	 */
	public T finish()
	{
		try
		{
			return get();
		}
		finally
		{
			mLock = null;
			mCallable = null;
			mException = null;
			mValue = null;
		}
	}


	protected void compute()
	{
		mLock.writeLock().lock();
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					mValue = mCallable.call();
				}
				catch (Exception e)
				{
					mException = e;
				}
				finally
				{
					mLock.writeLock().unlock();
				}
			}
		}.start();
	}


//	public static void main(String... args)
//	{
//		try
//		{
//			Computer<String> a = new Computer<>(() -> "a");
//			Computer<String> b = new Computer<>(() -> "b");
//			System.out.println(a.get() + b.get());
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

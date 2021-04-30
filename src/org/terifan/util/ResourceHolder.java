package org.terifan.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;


/**
 * A ResourceHolder handles a resource that can be asynchronously invoked and closed in a safe way. The closed resource is set to null
 * allowing it to be garbage collected.
 *
 * <pre>
 * 	public MyTestClass()
 *	{
 *		mFile = new ResourceHolder<>();
 *	}
 *
 * 	public open() throws IOException
 *	{
 *		mFile.open(new RandomAccessFile("file", "r"));
 *	}
 *
 *	public void doSometing()
 *	{
 *		if (!mFile.invoke(file -> { file.seek(0); System.out.println(file.read()); })) System.out.println("oops, file closed!");
 *	}
 *
 *	public void close()
 *	{
 *		mFile.close();
 *	}
 * </pre>
 */
public class ResourceHolder<T> implements AutoCloseable
{
	private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
	private T mResource;


	/**
	 * Create a holder of a resource.
	 */
	public ResourceHolder()
	{
	}


	/**
	 * Create a holder of a resource.
	 *
	 * @param aResource
	 *   object to hold
	 */
	public ResourceHolder(T aResource)
	{
		mResource = aResource;
	}


	/**
	 * Sets the resource this instance holds.
	 *
	 * @param aResource
	 *   a resource to be held
	 * @throws IllegalArgumentException
	 *   if a resource is already held by this instance
	 */
	public void open(T aResource)
	{
		mLock.writeLock().lock();
		try
		{
			if (mResource != null)
			{
				throw new IllegalArgumentException("A resource is already held by this ResourceHolder");
			}

			mResource = aResource;
		}
		finally
		{
			mLock.writeLock().unlock();
		}

	}


	/**
	 * Provides the resource held by this class to the provided Invoker instance.
	 *
	 * <pre>
	 * resourceHolder.invoke(file -> doSomethingWith(file));
	 * </pre>
	 *
	 * @param aInvoker
	 *   a class performing operations on the provided resource.
	 * @return
	 *   true if it was invoked
	 */
	public synchronized boolean invoke(Invoker<T> aInvoker)
	{
		mLock.readLock().lock();
		try
		{
			if (mResource == null)
			{
				return false;
			}

			try
			{
				aInvoker.process(mResource);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}

			return true;
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	/**
	 * Provides the resource held by this class to the provided Invoker instance. This method will return a response from the Invoker
	 * instance or null if the instance has been closed.
	 *
	 * <pre>
	 * String s = resourceHolder.invoke(file -> { return doSomethingWith(file); });
	 * </pre>
	 *
	 * @param aInvoker
	 *   a class performing operations on the provided resource.
	 * @return
	 *   the value returned by the Invoker instance or null if the resource has been closed
	 */
	public synchronized <R> R invoke(InvokerWithReturn<T, R> aInvoker)
	{
		return invoke(aInvoker, () -> null);
	}


	/**
	 * Provides the resource held by this class to the provided Invoker instance. This method will return a response from the Invoker
	 * instance or null if the instance has been closed.
	 *
	 * <pre>
	 * String s = resourceHolder.invoke(file -> { return doSomethingWith(file); });
	 * </pre>
	 *
	 * @param aInvoker
	 *   a class performing operations on the provided resource.
	 * @param aDefaultValue
	 *   invoked if the resource has already been closed
	 * @return
	 *   the value returned by the Invoker instance or null if the resource has been closed
	 */
	public synchronized <R> R invoke(InvokerWithReturn<T, R> aInvoker, Supplier<R> aDefaultValue)
	{
		mLock.readLock().lock();
		try
		{
			if (mResource == null)
			{
				return aDefaultValue.get();
			}

			try
			{
				return aInvoker.process(mResource);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new IllegalStateException(e);
			}
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	/**
	 * Release the resource being held. If the resource implements AutoCloseable interface the close method is called.
	 */
	@Override
	public void close()
	{
		T tmp;

		mLock.writeLock().lock();
		try
		{
			tmp = mResource;
			mResource = null;
		}
		finally
		{
			mLock.writeLock().unlock();
		}

		if (tmp instanceof AutoCloseable)
		{
			try
			{
				((AutoCloseable)tmp).close();
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}
	}


	/**
	 * Return true if the resource has been closed.
	 */
	public boolean isAvailable()
	{
		mLock.readLock().lock();
		try
		{
			return mResource != null;
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	public interface Invoker<E>
	{
		void process(E aResource) throws Exception;
	}


	public interface InvokerWithReturn<E, R>
	{
		R process(E aResource) throws Exception;
	}
}

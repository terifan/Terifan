package org.terifan.util;


/**
 * A WeakWrapper wraps a resource that can be asynchronously invoked and closed in a safe way. The closed resource is set to null allowing
 * it to be garbage collected.
 *
 * <pre>
 * 	public MyTestClass()
 *	{
 *		mFile = new WeakWrapper<>();
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
public class WeakWrapper<T>
{
	private T mResource;


	/**
	 * Create a wrapper of a resource.
	 */
	public WeakWrapper()
	{
	}


	/**
	 * Create a wrapper of a resource.
	 *
	 * @param aResource
	 *   object to wrap
	 */
	public WeakWrapper(T aResource)
	{
		mResource = aResource;
	}


	public void open(T aResource)
	{
		if (mResource != null)
		{
			throw new IllegalArgumentException("Already wrapping a resource!");
		}

		mResource = aResource;
	}


	/**
	 * Provides the resource this class is holding to the provided Invoker instance.
	 *
	 * @param aInvoker
	 *   a class performing operations on the provided resource.
	 * @return
	 *   true if it was invoked
	 */
	public synchronized boolean invoke(Invoker<T> aInvoker)
	{
		if (mResource == null)
		{
			return false;
		}

		try
		{
			aInvoker.accept(mResource);
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


	/**
	 * Release the resource being held. If the resource implements AutoCloseable interface the close method is called.
	 */
	public synchronized void close()
	{
		T in = mResource;

		mResource = null;

		if (in instanceof AutoCloseable)
		{
			try
			{
				((AutoCloseable)in).close();
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


	public static interface Invoker<T>
	{
		void accept(T aInstance) throws Exception;
	}
}

package org.terifan.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


/**
 * InputStream storing read information in memory allowing it to be read again after a call to <code>rewind</code> method.
 */
public class CachedInputStream extends InputStream
{
	private InputStream mInputStream;
	private byte[] mCache;
	private int mOffset;


	public CachedInputStream(InputStream aInputStream)
	{
		mInputStream = aInputStream;
		mCache = new byte[65536];
	}


	/**
	 * Closes the original InputStream and reinitilizes this InputStream with the cached data. An CachedInputStream can be closed and rewinded multiple times.
	 */
	public void rewind() throws IOException
	{
		if (mInputStream != null)
		{
			mInputStream.close();
		}

		mInputStream = new ByteArrayInputStream(mCache, 0, mOffset);
		mOffset = 0;
	}


	/**
	 * Releases the internal cache. In normal circumstances this should have to be called.
	 */
	public void clearCache()
	{
		mCache = new byte[0];
	}


	@Override
	public int read() throws IOException
	{
		int b = mInputStream.read();

		if (b != -1)
		{
			ensureCapacity(1);

			mCache[mOffset++] = (byte)b;
		}

		return b;
	}


	@Override
	public int read(byte[] aBuffer, int aOffset, int aLength) throws IOException
	{
		int len = mInputStream.read(aBuffer, aOffset, aLength);
		
		if (len > 0)
		{
			ensureCapacity(len);

			System.arraycopy(aBuffer, aOffset, mCache, mOffset, len);
			mOffset += len;
		}
		
		return len;
	}


	@Override
	public void close() throws IOException
	{
		if (mInputStream != null)
		{
			mInputStream.close();
			mInputStream = null;
		}
	}


	private void ensureCapacity(int aExtra)
	{
		if (mCache.length < mOffset + aExtra)
		{
			mCache = Arrays.copyOfRange(mCache, 0, mOffset + (aExtra & ~0xffff) + 65536);
		}
	}


	public static void main(String... args)
	{
		try
		{
			byte[] src = new byte[10_000_000];
			new java.util.Random().nextBytes(src);
			
			CachedInputStream in = new CachedInputStream(new ByteArrayInputStream(src));
			
			byte[] a = Streams.readAll(in);
			
			in.rewind();
			
			byte[] b = Streams.readAll(in);
			
			in.rewind();
			
			byte[] c = Streams.readAll(in);

			System.out.println(Arrays.equals(a, src));
			System.out.println(Arrays.equals(a, b));
			System.out.println(Arrays.equals(a, c));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

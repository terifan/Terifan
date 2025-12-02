package org.terifan.io;

import java.io.IOException;
import java.io.InputStream;


public class CancellableInputStream extends InputStream implements AutoCloseable
{
	private InputStream mInputStream;
	private boolean mCancel;


	public CancellableInputStream(InputStream aInputStream)
	{
		if (aInputStream == null)
		{
			throw new IllegalArgumentException("Provided InputStream is null");
		}

		mInputStream = aInputStream;
	}


	public void cancel()
	{
		mCancel = true;
	}


	public boolean wasCancelled()
	{
		return mCancel;
	}


	@Override
	public int read() throws IOException
	{
		return mCancel ? -1 : mInputStream.read();
	}


	@Override
	public int read(byte[] b) throws IOException
	{
		return mCancel ? -1 : mInputStream.read(b);
	}


	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return mCancel ? -1 : mInputStream.read(b, off, len);
	}


	@Override
	public void close() throws IOException
	{
		mInputStream.close();
	}
}

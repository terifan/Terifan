package org.terifan.net;

import java.io.IOException;
import java.io.InputStream;


public class CancellableInputStream extends InputStream implements AutoCloseable
{
	private InputStream mStream;
	private boolean mCancel;


	public CancellableInputStream(InputStream aStream)
	{
		mStream = aStream;
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
		return mCancel ? -1 : mStream.read();
	}


	@Override
	public int read(byte[] b) throws IOException
	{
		return mCancel ? -1 : mStream.read(b);
	}


	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return mCancel ? -1 : mStream.read(b, off, len);
	}


	@Override
	public void close() throws IOException
	{
		mStream.close();
	}
}

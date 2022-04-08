package org.terifan.net.http.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;


public class HttpServerRequest
{
	private String mMethod;
	private String mPath;
	private Long mContentLength;
	private InputStream mInputStream;
	private HashMap<String, String> mHeaders;
	private InetAddress mLocalAddress;
	private InetAddress mRemoteAddress;
	private int mLocalPort;
	private int mRemotePort;
	private byte[] mContent;


	HttpServerRequest(Socket aSocket)
	{
		mLocalAddress = aSocket.getLocalAddress();
		mLocalPort = aSocket.getLocalPort();
		mRemoteAddress = aSocket.getInetAddress();
		mRemotePort = aSocket.getPort();
	}


	public InetAddress getLocalAddress()
	{
		return mLocalAddress;
	}


	public int getLocalPort()
	{
		return mLocalPort;
	}


	public InetAddress getRemoteAddress()
	{
		return mRemoteAddress;
	}


	public int getRemotePort()
	{
		return mRemotePort;
	}


	void setPath(String aPath)
	{
		mPath = aPath;
	}


	public String getPath()
	{
		return mPath;
	}


	public String getMethod()
	{
		return mMethod;
	}


	void setMethod(String aMethod)
	{
		mMethod = aMethod;
	}


	void setContentLength(Long aContentLength)
	{
		mContentLength = aContentLength;
	}


	public Long getContentLength()
	{
		return mContentLength;
	}


	/**
	 * Reads all bytes from the InputStream provided by the getInputStream method.
	 */
	public byte[] getContent() throws IOException
	{
		InputStream in = getInputStream();
		if (in != null)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			transfer(in, baos);
			mContent = baos.toByteArray();
			mContentLength = (long)mContent.length;
			mInputStream = null;
		}
		return mContent;
	}


	public InputStream getInputStream()
	{
		return mInputStream;
	}


	void setInputStream(InputStream aInputStream)
	{
		mInputStream = aInputStream;
	}


	public HashMap<String, String> getHeaders()
	{
		return mHeaders;
	}


	void setHeaders(HashMap<String, String> aHeaders)
	{
		mHeaders = aHeaders;

		for (String key : mHeaders.keySet())
		{
			if (key.equalsIgnoreCase("content-length"))
			{
				setContentLength(Long.parseLong(mHeaders.get(key)));
			}
		}
	}


	@Override
	public String toString()
	{
		return "HttpServerRequest{" + "mMethod=" + mMethod + ", mPath=" + mPath + ", mContentLength=" + mContentLength + ", mHeaders=" + mHeaders + ", mLocalAddress=" + mLocalAddress + ", mRemoteAddress=" + mRemoteAddress + ", mLocalPort=" + mLocalPort + ", mRemotePort=" + mRemotePort + '}';
	}


	protected long transfer(InputStream aInputStream, OutputStream aOutputStream) throws IOException
	{
		byte[] buffer = new byte[4096];
		long total = 0;

		for (;;)
		{
			int len = aInputStream.read(buffer);

			if (len > 0)
			{
				aOutputStream.write(buffer, 0, len);
				total += len;
			}

			if (len <= 0 || (mContentLength != null && total == mContentLength))
			{
				break;
			}
		}

		return total;
	}
}

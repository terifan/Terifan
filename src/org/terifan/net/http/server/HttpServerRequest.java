package org.terifan.net.http.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;


public class HttpServerRequest
{
	private byte [] mContent;
	private String mMethod;
	private String mPath;
	private Integer mContentLength;
	private InputStream mInputStream;
	private HashMap<String,String> mHeaders;
	private InetAddress mLocalAddress;
	private InetAddress mRemoteAddress;
	private int mLocalPort;
	private int mRemotePort;


	public HttpServerRequest(Socket aSocket)
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


	void setContent(byte [] aContent)
	{
		mContent = aContent;
	}


	public byte[] getContent() throws IOException
	{
		if (mContent == null && mContentLength != null && mInputStream != null)
		{
            DataInputStream in = new DataInputStream(mInputStream);
            mContent = new byte[mContentLength];
            in.readFully(mContent);
		}

		return mContent;
	}


	void setPath(String aPath)
	{
		mPath = aPath;
	}


	public String getPath()
	{
		return mPath;
	}


	void setContentLength(Integer aContentLength)
	{
		mContentLength = aContentLength;
	}


	public int getContentLength()
	{
		return mContentLength != null ? mContentLength : mContent != null ? mContent.length : 0;
	}


	public InputStream getInputStream()
	{
		return mInputStream;
	}


	void setInputStream(InputStream aInputStream)
	{
		this.mInputStream = aInputStream;
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
				setContentLength(Integer.parseInt(mHeaders.get(key)));
			}
		}
	}


	public String getMethod()
	{
		return mMethod;
	}


	void setMethod(String aMethod)
	{
		this.mMethod = aMethod;
	}
}

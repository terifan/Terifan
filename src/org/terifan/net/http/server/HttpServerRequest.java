package org.terifan.net.http.server;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;


public class HttpServerRequest
{
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


	public Integer getContentLength()
	{
		return mContentLength;
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
		mMethod = aMethod;
	}
}

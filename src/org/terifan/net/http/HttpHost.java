package org.terifan.net.http;

import java.net.SocketAddress;


public class HttpHost
{
	private SocketAddress mAddress;
	private String mHostName;
	private int mPort;


	public HttpHost(SocketAddress aAddress)
	{
		mAddress = aAddress;
	}


	public HttpHost(String aHostName, int aPort)
	{
		mHostName = aHostName;
		mPort = aPort;
	}


	public SocketAddress getAddress()
	{
		return mAddress;
	}


	public String getHostName()
	{
		return mHostName;
	}


	public int getPort()
	{
		return mPort;
	}


	@Override
	public String toString()
	{
		if (mAddress != null)
		{
			return mAddress.toString();
		}

		if (mPort != 80)
		{
			return mHostName;
		}

		return mHostName + ":" + mPort;
	}
}

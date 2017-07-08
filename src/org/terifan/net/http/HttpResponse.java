package org.terifan.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;


public class HttpResponse
{
	protected int mResponseCode;
	protected String mResponseMessage;
	protected long mContentLength;
	protected String mContentEncoding;
	protected String mContentType;
	protected byte[] mContent;
	protected HttpClient mClient;
	protected Map<String, List<String>> mHeaders;


	HttpResponse(HttpURLConnection aConnection) throws IOException
	{
		mResponseCode = aConnection.getResponseCode();
		mResponseMessage = aConnection.getResponseMessage();
		mContentLength = aConnection.getContentLength();
		mContentEncoding = aConnection.getContentEncoding();
		mContentType = aConnection.getContentType();
		mHeaders = aConnection.getHeaderFields();
	}


	public int getResponseCode()
	{
		return mResponseCode;
	}


	public String getResponseMessage()
	{
		return mResponseMessage;
	}


	public String getContentType()
	{
		return mContentType;
	}


	public long getContentLength()
	{
		return mContentLength;
	}


	public String getContentEncoding()
	{
		return mContentEncoding;
	}


	public byte[] getContent()
	{
		return mContent;
	}


	public void setContent(byte[] aContent)
	{
		mContent = aContent;
	}


	public Map<String, List<String>> getHeaders()
	{
		return mHeaders;
	}


	@Override
	public String toString()
	{
		return mResponseCode + " " + mResponseMessage;
	}


	HttpResponse setClient(HttpClient aClient)
	{
		mClient = aClient;
		return this;
	}
}

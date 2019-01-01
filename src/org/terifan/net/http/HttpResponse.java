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
	protected String mRedirect;


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


	protected void setContent(byte[] aContent)
	{
		mContent = aContent;
	}


	public Map<String, List<String>> getHeaders()
	{
		return mHeaders;
	}


	/**
	 * Returns a redirect URL received from the server.
	 *
	 * @return
	 *   a redirect URL received from the server.
	 */
	public String getRedirect()
	{
		return mRedirect;
	}


	protected void setRedirect(String aRedirect)
	{
		mRedirect = aRedirect;
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

package org.terifan.net.http.server;


public class HttpServerResponse
{
	private HttpStatusCode mHttpStatusCode;
	private byte[] mContent;
	private String mContentType;
	private Integer mContentLength;


	HttpServerResponse()
	{
		setStatusCode(HttpStatusCode.OK);
	}


	public HttpStatusCode getStatusCode()
	{
		return mHttpStatusCode;
	}


	public void setStatusCode(HttpStatusCode aHttpStatusCode)
	{
		mHttpStatusCode = aHttpStatusCode;
	}


	public byte[] getContent()
	{
		return mContent;
	}


	public void setContent(byte[] aContent)
	{
		mContent = aContent;
	}


	public String getContentType()
	{
		return mContentType;
	}


	public void setContentType(String aContentType)
	{
		mContentType = aContentType;
	}


	public void setContentLength(Integer aContentLength)
	{
		mContentLength = aContentLength;
	}


	public int getContentLength()
	{
		return mContentLength != null ? mContentLength : mContent != null ? mContent.length : 0;
	}
}

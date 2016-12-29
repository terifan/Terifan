package org.terifan.net.http.server;


public class HttpServerResponse
{
	private HttpStatusCode mHttpStatusCode;
	private byte [] mContent;
	private String mContentType;
	private Integer mContentLength;


	public HttpServerResponse()
	{
		setStatusCode(HttpStatusCode.OK);
	}


	public HttpServerResponse(HttpStatusCode aHttpStatusCode)
	{
		this.mHttpStatusCode = aHttpStatusCode;
	}


	public HttpStatusCode getStatusCode()
	{
		return mHttpStatusCode;
	}


	public void setStatusCode(HttpStatusCode aHttpStatusCode)
	{
		this.mHttpStatusCode = aHttpStatusCode;
	}


	public byte[] getContent()
	{
		return mContent;
	}


	public void setContent(byte[] aContent)
	{
		this.mContent = aContent;
	}


	public String getContentType()
	{
		return mContentType;
	}


	public void setContentType(String aContentType)
	{
		this.mContentType = aContentType;
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

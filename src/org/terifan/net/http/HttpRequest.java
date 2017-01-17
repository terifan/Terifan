package org.terifan.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import org.terifan.io.Streams;


public abstract class HttpRequest<E extends HttpRequest>
{
	protected URL mURL;
	protected OutputStream mTarget;
	protected LinkedHashMap<String, String> mHeaders;
	protected String mMethod;
	protected String mContentType;
	protected String mCharSet;
	protected String mLoginName;
	protected String mPassword;
	protected Long mContentLength;
	protected int mConnectTimeOut;
	protected int mReadTimeOut;


	public HttpRequest()
	{
		mHeaders = new LinkedHashMap<>();
		mCharSet = "utf-8";
		mConnectTimeOut = 1 * 60 * 1000;
		mReadTimeOut = 5 * 60 * 1000;
	}


	public URL getURL()
	{
		return mURL;
	}


	public E setURL(URL url)
	{
		mURL = url;
		return (E)this;
	}


	public E setURL(String url) throws MalformedURLException
	{
		mURL = new URL(url);
		return (E)this;
	}


	public LinkedHashMap<String, String> getHeaders()
	{
		return mHeaders;
	}


	public E setHeader(String aKey, String aValue)
	{
		mHeaders.put(aKey, aValue);
		return (E)this;
	}


	public E setHeaders(LinkedHashMap<String, String> aHeaders)
	{
		mHeaders = aHeaders;
		return (E)this;
	}


	public E addHeaders(LinkedHashMap<String, String> aHeaders)
	{
		mHeaders.putAll(aHeaders);
		return (E)this;
	}


	public String getMethod()
	{
		return mMethod;
	}


	public E setMethod(String aMethod)
	{
		mMethod = aMethod;
		return (E)this;
	}


	public String getContentType()
	{
		return mContentType;
	}


	public E setContentType(String aContentType)
	{
		mContentType = aContentType;
		return (E)this;
	}


	public String getCharSet()
	{
		return mCharSet;
	}


	public E setCharSet(String aCharSet)
	{
		mCharSet = aCharSet;
		return (E)this;
	}


	public E setAuthority(String aLoginName, String aPassword)
	{
		mLoginName = aLoginName;
		mPassword = aPassword;
		return (E)this;
	}


	public OutputStream getOutput()
	{
		return mTarget;
	}


	public E setOutput(OutputStream aTarget)
	{
		mTarget = aTarget;
		return (E)this;
	}


	public Long getContentLength()
	{
		return mContentLength;
	}


	public E setContentLength(Integer aContentLength)
	{
		if (aContentLength == null)
		{
			mContentLength = null;
		}
		else
		{
			mContentLength = (long)(int)aContentLength;
		}
		return (E)this;
	}


	public E setContentLength(Long aContentLength)
	{
		mContentLength = aContentLength;
		return (E)this;
	}


	public int getConnectTimeOut()
	{
		return mConnectTimeOut;
	}


	public E setConnectTimeOut(int aConnectTimeOut)
	{
		mConnectTimeOut = aConnectTimeOut;
		return (E)this;
	}


	public int getReadTimeOut()
	{
		return mReadTimeOut;
	}


	public E setReadTimeOut(int aReadTimeOut)
	{
		mReadTimeOut = aReadTimeOut;
		return (E)this;
	}


	public abstract HttpResponse execute() throws IOException;


	protected HttpURLConnection openConnection() throws IOException
	{
		if (mURL == null)
		{
			throw new IllegalArgumentException("URL is not set.");
		}

		HttpURLConnection conn = (HttpURLConnection)mURL.openConnection();
		conn.setRequestMethod(mMethod);
		conn.setReadTimeout(mReadTimeOut);
		conn.setConnectTimeout(mConnectTimeOut);

		for (Map.Entry<String, String> entry : mHeaders.entrySet())
		{
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}

		if ("POST".equals(mMethod) || "PUT".equals(mMethod))
		{
			conn.setDoOutput(true);

			if (mContentLength != null)
			{
				conn.setFixedLengthStreamingMode(mContentLength);
			}
			else
			{
				conn.setChunkedStreamingMode(1024);
			}
		}

		if (mContentType != null)
		{
			if (mContentType.contains("charset"))
			{
				conn.setRequestProperty("Content-Type", mContentType);
			}
			else
			{
				conn.setRequestProperty("Content-Type", mContentType + "; charset=" + mCharSet);
			}
		}
		else
		{
			conn.setRequestProperty("Content-Type", "application/octet-stream");
		}

		if (mLoginName != null && mPassword != null)
		{
			conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((mLoginName + ":" + mPassword).getBytes()));
		}

		return conn;
	}


	protected HttpResponse buildResponse(HttpURLConnection aConnection) throws IOException
	{
		InputStream in;

		if (aConnection.getResponseCode() > 400)
		{
			in = aConnection.getErrorStream();
		}
		else
		{
			in = aConnection.getInputStream();
		}

		HttpResponse response = new HttpResponse(aConnection);

		if (in != null)
		{
			if (mTarget != null)
			{
				Streams.transfer(in, mTarget);
			}
			else
			{
				response.setContent(Streams.readAll(in));
			}
		}

		return response;
	}
}

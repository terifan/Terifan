package org.terifan.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
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
	protected HttpClient mClient;
	protected ArrayList<AfterExecuteAction> mActions;


	public HttpRequest()
	{
		mHeaders = new LinkedHashMap<>();
		mCharSet = "utf-8";
		mConnectTimeOut = 1 * 60 * 1000;
		mReadTimeOut = 5 * 60 * 1000;
		mActions = new ArrayList<>();
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
		if (aValue != null)
		{
			mHeaders.put(aKey, aValue);
		}
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


	public E setConnectTimeOut(int aConnectTimeOutMillis)
	{
		mConnectTimeOut = aConnectTimeOutMillis;
		return (E)this;
	}


	public int getReadTimeOut()
	{
		return mReadTimeOut;
	}


	public E setReadTimeOut(int aReadTimeOutMillis)
	{
		mReadTimeOut = aReadTimeOutMillis;
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

		if (mClient != null)
		{
			addCookies(conn);
		}
		
		for (Entry<String, String> entry : mHeaders.entrySet())
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

		for (AfterExecuteAction action : mActions)
		{
			action.execute(this, response);
		}
		
		return response;
	}
	
	
	public E addAction(AfterExecuteAction aAction)
	{
		mActions.add(aAction);
		return (E)this;
	}


	private void addCookies(HttpURLConnection aConnection)
	{
		StringBuilder cookieString = new StringBuilder();

		for (Entry<String, String> entry : mClient.getCookies(mURL).entrySet())
		{
			if (cookieString.length() > 0)
			{
				cookieString.append("; ");
			}
			
			String[] values = entry.getValue().split(";");
			boolean skip = false;

			for (int i = 1; i < values.length; i++)
			{
				String[] parts = values[i].split("=");
				
				if (parts.length == 2)
				{
					String key = parts[0].trim();
					String param = parts[1].trim();

					if (key.equals("domain"))
					{
						if (!("."+mURL.getHost()).endsWith(param))
						{
//							System.out.println("host missmatch: url: " + mURL.getHost() + ", param: " + param);
							skip = true;
						}
					}
					if (key.equalsIgnoreCase("path"))
					{
						if (!mURL.getFile().startsWith(param))
						{
//							System.out.println("path missmatch: url: " + mURL.getPath() + ", param: " + param);
							skip = true;
						}
					}
				}
			}
			
			if (!skip)
			{
				cookieString.append(entry.getKey() + "=" + values[0]);
			}
		}

		if (cookieString.length() > 0)
		{
			aConnection.addRequestProperty("Cookie", cookieString.toString());
		}
	}
}

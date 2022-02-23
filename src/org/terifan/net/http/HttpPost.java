package org.terifan.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class HttpPost extends HttpRequest<HttpPost>
{
	private final static String TAG = HttpPost.class.getName();

	protected InputStream mInput;
	protected LinkedHashMap<String,String> mTrailers;


	public HttpPost()
	{
		super();

		mMethod = HttpMethod.POST;
	}


	public HttpPost(URL aURL)
	{
		this();

		mURL = aURL;
	}


	public HttpPost(String aURL)
	{
		this();

		try
		{
			mURL = new URL(aURL);
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException(e);
		}
	}


	HttpPost(HttpClient aClient)
	{
		this();

		mClient = aClient;
	}


	/**
	 * @param aMethod
	 *   one of POST, PUT
	 */
	@Override
	public HttpPost setMethod(HttpMethod aMethod)
	{
		if (aMethod == null || !(aMethod == HttpMethod.POST || aMethod == HttpMethod.PUT))
		{
			throw new IllegalArgumentException();
		}

		mMethod = aMethod;
		return this;
	}


	public HttpPost setInput(byte[] aInput)
	{
		mInput = new ByteArrayInputStream(aInput);
		return this;
	}


	public HttpPost setInput(byte[] aInput, boolean aUseFixedLengthTransfer)
	{
		mInput = new ByteArrayInputStream(aInput);
		mContentLength = (long)aInput.length;
		mFixedLengthStreaming = aUseFixedLengthTransfer;

		return this;
	}


	public HttpPost setInput(InputStream aInput)
	{
		mInput = aInput;
		return this;
	}


	public Object getInput()
	{
		return mInput;
	}


//	public HttpPost putTrailer(String aName, String aValue)
//	{
//		if (mTrailers == null)
//		{
//			mTrailers = new LinkedHashMap<>();
//		}
//		mTrailers.put(aName, aValue);
//		return this;
//	}


	@Override
	public HttpResponse execute() throws IOException
	{
		if (mInput == null && !mParameters.isEmpty())
		{
			setContentType("application/x-www-form-urlencoded");
			setHeader("charset", "utf-8");

			StringBuilder link = new StringBuilder();

			boolean first = true;
			for (Entry<String,String> param : mParameters.entrySet())
			{
				if (!first)
				{
					link.append("&");
				}
				link.append(URLEncoder.encode(param.getKey(), "iso-8859-1") + "=" + URLEncoder.encode(param.getValue(), "iso-8859-1"));
				first = false;
			}

			mInput = new ByteArrayInputStream(link.toString().getBytes(StandardCharsets.UTF_8));
		}

		if (mInput == null)
		{
			throw new IllegalArgumentException("Source is not set.");
		}

		if (mTransferListener != null)
		{
			mTransferListener.prepareSending(mContentLength == null ? -1 : mContentLength);
		}

		HttpURLConnection conn = openConnection();

		OutputStream outputStream = conn.getOutputStream();

		TransferCallback tc = aCount ->
		{
			if (mTransferListener != null)
			{
				mTransferListener.sending(aCount);
			}
		};

		transfer(mInput, outputStream, tc);

		HttpResponse response = buildResponse(conn);

		return response;
	}
}

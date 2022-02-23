package org.terifan.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpGet extends HttpRequest<HttpGet>
{
	public HttpGet()
	{
		super();

		mMethod = HttpMethod.GET;
	}


	public HttpGet(URL aURL)
	{
		this();

		mURL = aURL;
	}


	public HttpGet(String aURL)
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


	HttpGet(HttpClient aClient)
	{
		this();

		mClient = aClient;
	}


	/**
	 * @param aMethod
	 *   one of GET, HEAD, DELETE or OPTIONS
	 */
	@Override
	public HttpGet setMethod(HttpMethod aMethod)
	{
		if (aMethod == null || !(aMethod == HttpMethod.GET || aMethod == HttpMethod.HEAD || aMethod == HttpMethod.DELETE || aMethod == HttpMethod.OPTIONS))
		{
			throw new IllegalArgumentException();
		}

		mMethod = aMethod;
		return this;
	}


	@Override
	public HttpResponse execute() throws IOException
	{
		HttpURLConnection conn = openConnection();

		return buildResponse(conn);
	}
}

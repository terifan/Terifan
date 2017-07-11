package org.terifan.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpGet extends HttpRequest<HttpRequest>
{
	public HttpGet()
	{
		super();

		mMethod = "GET";
	}


	public HttpGet(URL aURL) throws MalformedURLException
	{
		this();

		mURL = aURL;
	}


	public HttpGet(String aURL) throws MalformedURLException
	{
		this(new URL(aURL));
	}


	HttpGet(HttpClient aClient)
	{
		this();
		
		mClient = aClient;
	}


	@Override
	public HttpResponse execute() throws IOException
	{
		HttpURLConnection conn = openConnection();

		return buildResponse(conn);
	}
}

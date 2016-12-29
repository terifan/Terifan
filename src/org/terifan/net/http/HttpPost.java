package org.terifan.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.terifan.io.Streams;


public class HttpPost extends HttpRequest<HttpPost>
{
	protected Object mInput;


	public HttpPost()
	{
		super();

		mMethod = "POST";
	}


	public HttpPost(URL aURL) throws MalformedURLException
	{
		this();

		mURL = aURL;
	}


	public HttpPost(String aURL) throws MalformedURLException
	{
		this(new URL(aURL));
	}


	public HttpPost setInput(byte[] aInput)
	{
		mInput = new ByteArrayInputStream(aInput);
		return this;
	}


	public HttpPost setInput(InputStream aInput)
	{
		mInput = aInput;
		return this;
	}


	public HttpPost setInput(Reader aInput)
	{
		mInput = aInput;
		return this;
	}


	public Object getInput()
	{
		return mInput;
	}


	@Override
	public HttpResponse execute() throws IOException
	{
		if (mInput == null)
		{
			throw new IllegalArgumentException("Source is not set.");
		}

		HttpURLConnection conn = openConnection();

		OutputStream out = conn.getOutputStream();
		Streams.transfer(mInput, out);

		return buildResponse(conn);
	}
}

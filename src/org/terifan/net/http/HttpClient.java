package org.terifan.net.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClient
{
	private String mReferer;
	private String mAgent;
	private HashMap<String, Map<String, String>> mCookies;


	public HttpClient()
	{
		mCookies = new HashMap<>();
		mAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0";
	}


	public HttpGet get(String aUrl) throws MalformedURLException
	{
		return get(new URL(aUrl), false);
	}


	public HttpGet get(String aUrl, boolean aSetReferer) throws MalformedURLException
	{
		return get(new URL(aUrl), aSetReferer);
	}


	public HttpGet get(URL aUrl) throws MalformedURLException
	{
		return get(aUrl, false);
	}


	public HttpGet get(URL aUrl, boolean aSetReferer) throws MalformedURLException
	{
		return new HttpGet(this)
			.setURL(aUrl)
			.setHeader("User-Agent", mAgent)
			.setHeader("Referer", mReferer)
			.addAction((request, response) ->
			{
				if (aSetReferer)
				{
					mReferer = aUrl.toExternalForm();
				}

				copyCookies(request, response);
			});
	}


	public HttpPost post(URL aUrl, boolean aSetReferer) throws MalformedURLException
	{
		return new HttpPost(this)
			.setURL(aUrl)
			.setHeader("User-Agent", mAgent)
			.setHeader("Referer", mReferer)
			.addAction((request, response) ->
			{
				if (aSetReferer)
				{
					mReferer = aUrl.toExternalForm();
				}

				copyCookies(request, response);
			});
	}


	public String getReferer()
	{
		return mReferer;
	}


	public HttpClient setReferer(String aReferer)
	{
		mReferer = aReferer;
		return this;
	}


	public String getAgent()
	{
		return mAgent;
	}


	public HttpClient setAgent(String aAgent)
	{
		mAgent = aAgent;
		return this;
	}


	public HashMap<String, Map<String, String>> getCookies()
	{
		return mCookies;
	}


	protected void copyCookies(HttpRequest aRequest, HttpResponse aResponse)
	{
		String host = aRequest.getURL().getHost();
		Map<String, String> cookies = mCookies.get(host);

		if (cookies == null)
		{
			cookies = new HashMap<>();
			mCookies.put(host, cookies);
		}

		List<String> newCookies = aResponse.getHeaders().get("Set-Cookie");
		if (newCookies != null && !newCookies.isEmpty())
		{
//			System.out.println("SetCookie: " + newCookies);

			for (String value : newCookies)
			{
				int i = value.indexOf("=");
				String key = value.substring(0, i);
				value = value.substring(i + 1);

				cookies.put(key, value);
			}
		}

//		System.out.println("######"+cookies);
	}


	Map<String, String> getCookies(URL aURL)
	{
//		System.out.println("******"+mCookies.computeIfAbsent(aURL.getHost(), e->new HashMap<>()));

		return mCookies.computeIfAbsent(aURL.getHost(), e -> new HashMap<>());
	}


	public HttpClient putCookie(URL aUrl, String aCookieString)
	{
		Map<String, String> cookies = mCookies.get(aUrl.getHost());

		if (cookies == null)
		{
			cookies = new HashMap<>();
			mCookies.put(aUrl.getHost(), cookies);
		}

		int i = aCookieString.indexOf("=");
		String key = aCookieString.substring(0, i);
		String value = aCookieString.substring(i + 1);

		cookies.put(key, value);

		return this;
	}


	public static void main(String... args)
	{
		try
		{
			HttpClient client = new HttpClient();

			HttpResponse response = client
				.get("https://dips.surikat.net/Login")
				.execute();

			System.out.println(response.getHeaders());

			response = client
				.get("https://dips.surikat.net/Login")
				.execute();

			System.out.println(response.getHeaders());
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

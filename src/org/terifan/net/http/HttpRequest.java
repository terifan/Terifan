package org.terifan.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.terifan.util.Strings;


public abstract class HttpRequest<E extends HttpRequest>
{
	private final static String TAG = HttpRequest.class.getName();

	private static HostnameVerifier mOldHostnameVerifier;

	protected URL mURL;
	protected OutputStream mOutput;
	protected ArrayList<AfterExecuteAction> mActions;
	protected LinkedHashMap<String, String> mHeaders;
	protected LinkedHashMap<String, String> mParameters;
	protected HttpMethod mMethod;
	protected String mContentType;
	protected String mCharSet;
	protected String mLoginName;
	protected String mPassword;
	protected String mToken;
	protected Long mContentLength;
	protected int mConnectTimeOut;
	protected int mReadTimeOut;
	protected HttpClient mClient;
	protected TransferListener mTransferListener;
	protected boolean mFixedLengthStreaming;
	protected int mChunkSize;
	protected PrintStream mLog;


	public HttpRequest()
	{
		mHeaders = new LinkedHashMap<>();
		mParameters = new LinkedHashMap<>();
		mActions = new ArrayList<>();
		mConnectTimeOut = 5 * 60 * 1000;
		mReadTimeOut = 5 * 60 * 1000;
		mChunkSize = 8192;
	}


	public E setLog(PrintStream aLog)
	{
		mLog = aLog;
		return (E)this;
	}


	public HttpRequest setTransferListener(TransferListener aTransferListener)
	{
		mTransferListener = aTransferListener;
		return (E)this;
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


	public E setURL(String url)
	{
		try
		{
			mURL = new URL(url);
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException(e);
		}
		return (E)this;
	}


	public E appendURL(String aURL)
	{
		try
		{
			if (mURL == null)
			{
				mURL = new URL(aURL);
			}
			else
			{
				mURL = new URL(mURL.toString() + aURL);
			}
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException(e);
		}
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


	public E setHeaders(Map<String, String> aHeaders)
	{
		mHeaders.putAll(aHeaders);
		return (E)this;
	}


	public LinkedHashMap<String, String> getParameters()
	{
		return mParameters;
	}


	public E setParameter(String aKey, String aValue)
	{
		if (aValue != null)
		{
			mParameters.put(aKey, aValue);
		}
		return (E)this;
	}


	public E setParameters(Map<String, String> aParameters)
	{
		mParameters.putAll(aParameters);
		return (E)this;
	}


	public HttpMethod getMethod()
	{
		return mMethod;
	}


	public abstract E setMethod(HttpMethod aMethod);


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


	/**
	 * Sets the Authorization header to a "Basic" username/password pair. Calling this method will remove the token authorization value.
	 */
	public E setAuthority(String aLoginName, String aPassword)
	{
		mToken = null;
		mLoginName = aLoginName;
		mPassword = aPassword;
		return (E)this;
	}


	/**
	 * Sets the Authorization header to a "Bearer" token. Calling this method will remove the username/password authorization values.
	 */
	public E setAuthority(String aToken)
	{
		mToken = aToken;
		mLoginName = null;
		mPassword = null;
		return (E)this;
	}


	/**
	 * Sets the output stream content is written to. If this method is used then getContent method of HttpResponse class will throw an
	 * exception if called. Note: The output stream isn't closed.
	 *
	 * @param aOutput
	 *   an output stream written to, not closed by this implementation.
	 */
	public E setOutput(OutputStream aOutput)
	{
		mOutput = aOutput;
		return (E)this;
	}


	public E setContentLength(Integer aOutputLength)
	{
		if (aOutputLength == null)
		{
			mContentLength = null;
		}
		else
		{
			mContentLength = (long)(int)aOutputLength;
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


	public E addAction(AfterExecuteAction aAction)
	{
		mActions.add(aAction);
		return (E)this;
	}


	/**
	 * Enables or disables the global hostname/certificate verifier.
	 *
	 * @param aState
	 *   if false registers a HostnameVerifier that accept all host names, if true removes the installed HostnameVerifier or does
	 *   nothing if no HostnameVerifier has been installed.
	 */
	public static synchronized void setValidateTLSCertificateEnabled(boolean aState)
	{
		if (aState && mOldHostnameVerifier != null)
		{
			HttpsURLConnection.setDefaultHostnameVerifier(mOldHostnameVerifier);
			mOldHostnameVerifier = null;
		}

		if (!aState && mOldHostnameVerifier == null)
		{
			try
			{
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) { }
					public void checkServerTrusted(X509Certificate[] certs, String authType) { }
				} };

				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

				HostnameVerifier allHostsValid = (hostname, session) -> true;
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

				mOldHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
			}
			catch (KeyManagementException | NoSuchAlgorithmException e)
			{
				throw new IllegalStateException(e);
			}
		}
	}


	public abstract HttpResponse execute() throws IOException;


	public HttpURLConnection openConnection() throws IOException
	{
		if (mURL == null)
		{
			throw new IllegalArgumentException("URL is not set.");
		}

		URL url = assambleURL();

		log("Opening connection %s %s", mMethod, url);

		HttpURLConnection conn = (HttpURLConnection)url.openConnection(createProxyInstance());
		conn.setRequestMethod(mMethod.name());
		conn.setReadTimeout(mReadTimeOut);
		conn.setConnectTimeout(mConnectTimeOut);
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(false);

		addCookies(conn);
		addHeaders(conn);
		addContentType(conn);
		addAuthorization(conn);
		addStreamingMode(conn);

		return conn;
	}


	private void addStreamingMode(HttpURLConnection aConn)
	{
		if (mMethod == HttpMethod.POST || mMethod == HttpMethod.PUT)
		{
			aConn.setDoOutput(true);

			if (mFixedLengthStreaming)
			{
				aConn.setFixedLengthStreamingMode(mContentLength);
			}
			else
			{
				aConn.setChunkedStreamingMode(-1);
			}
		}
	}


	private void addHeaders(HttpURLConnection aConn)
	{
		for (Entry<String, String> entry : mHeaders.entrySet())
		{
			aConn.addRequestProperty(entry.getKey(), entry.getValue());
		}
	}


	private Proxy createProxyInstance()
	{
		Proxy proxy;
		if (mClient != null && mClient.getProxy() != null)
		{
			proxy = new Proxy(Proxy.Type.HTTP, mClient.getProxy().getAddress());
		}
		else
		{
			proxy = Proxy.NO_PROXY;
		}
		return proxy;
	}


	private void addAuthorization(HttpURLConnection aConnection)
	{
		if (mLoginName != null && mPassword != null || mToken != null)
		{
			log("%s", "\twith basic authorization");

			if (mToken != null)
			{
				aConnection.setRequestProperty("Authorization", "Bearer " + mToken);
			}
			else
			{
				aConnection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((mLoginName + ":" + mPassword).getBytes()));
			}
		}
	}


	private void addContentType(HttpURLConnection aConnection)
	{
		String contentType = null;

		if (mContentType != null && (mCharSet == null || mContentType.contains("charset")))
		{
			contentType = mContentType;
		}
		else if (mContentType != null && mCharSet != null)
		{
			contentType = mContentType + "; charset=" + mCharSet;
		}
		else if (mCharSet != null)
		{
			contentType = "text/plain; charset=" + mCharSet;
		}

		if (contentType != null)
		{
			log("\twith content type \"%s\"", contentType);

			aConnection.setRequestProperty("Content-Type", contentType);
		}
	}


	private URL assambleURL() throws MalformedURLException
	{
		if (mMethod != HttpMethod.GET || mParameters.isEmpty())
		{
			return mURL;
		}

		StringBuilder url = new StringBuilder(mURL.toString());

		boolean first = false;
		if (url.indexOf("?") == -1)
		{
			url.append("?");
			first = true;
		}

		for (Entry<String,String> entry : mParameters.entrySet())
		{
			if (!first)
			{
				url.append("&");
			}

			try
			{
				url.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				url.append("=");
				url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
			}

			first = false;
		}

		return new URL(url.toString());
	}


	protected HttpResponse buildResponse(HttpURLConnection aConnection) throws IOException
	{
		log("Receiving respose %d \"%s\"", aConnection.getResponseCode(), Strings.nullToEmpty(aConnection.getResponseMessage()));

		InputStream in;

		if (aConnection.getResponseCode() >= 400)
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
			if (mTransferListener != null)
			{
				mTransferListener.prepareReceiving(response.getContentLength());
			}

			TransferCallback tc = aByteCount ->
			{
				if (mTransferListener != null)
				{
					mTransferListener.receiving(aByteCount);
				}
			};

			long length;
			long startTime = System.currentTimeMillis();

			if (mOutput != null)
			{
				log("%s", "\tstreaming response to output stream");

				length = transfer(in, mOutput, tc);
				response.setContentProduced(true);
			}
			else
			{
				log("%s", "\tloading response to memory");

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				length = transfer(in, baos, tc);
				response.setContent(baos.toByteArray());
			}

			response.mContentLength = length;

			log("\tfinishing transer of %d bytes in %dms", length, System.currentTimeMillis()-startTime);
		}
		else
		{
			log("%s", "\tfinishing response with no body");
		}

		switch (response.getResponseCode())
		{
			case HttpURLConnection.HTTP_MOVED_TEMP:
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_SEE_OTHER:
				log("\tsetting redirection to \"%s\"", aConnection.getHeaderField("Location"));
				response.setRedirect(aConnection.getHeaderField("Location"));
				break;
		}

		for (AfterExecuteAction action : mActions)
		{
			action.execute(this, response);
		}

		return response;
	}


	private void addCookies(HttpURLConnection aConnection)
	{
		if (mClient != null)
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
//								System.out.println("host missmatch: url: " + mURL.getHost() + ", param: " + param);
								skip = true;
							}
						}
						if (key.equalsIgnoreCase("path"))
						{
							if (!mURL.getFile().startsWith(param))
							{
//								System.out.println("path missmatch: url: " + mURL.getPath() + ", param: " + param);
								skip = true;
							}
						}
					}
				}

				if (!skip)
				{
					log("%s", "\twith cookie \"" + entry.getKey() + "\"");

					cookieString.append(entry.getKey() + "=" + values[0]);
				}
			}

			if (cookieString.length() > 0)
			{
				aConnection.addRequestProperty("Cookie", cookieString.toString());
			}
		}
	}


	protected long transfer(InputStream aInputStream, OutputStream aOutputStream, TransferCallback aCallback) throws IOException
	{
		try
		{
			byte[] buffer = new byte[mChunkSize];
			long total = 0;

			for (;;)
			{
				int len = aInputStream.read(buffer);

				if (len <= 0)
				{
					break;
				}

				aOutputStream.write(buffer, 0, len);
				aCallback.report(len);
				total += len;
			}

			return total;
		}
		finally
		{
			aInputStream.close();
		}
	}


	protected interface TransferCallback
	{
		void report(int aByteCount);
	}


	protected void log(String aFormat, Object... aParams)
	{
		if (mLog != null)
		{
			mLog.printf(aFormat + "%n", aParams);
		}
	}
}

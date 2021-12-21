package org.terifan.net.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import org.terifan.io.ByteArray;
import org.terifan.util.log.Log;


class ConnectionHandler extends Thread
{
	private HttpServerHandler mHttpServerHandler;
	private Socket mSocket;
	private SimpleHttpServer mHttpServer;


	public ConnectionHandler(SimpleHttpServer aHttpServer, HttpServerHandler aHttpServerHandler, Socket aSocket)
	{
		mHttpServerHandler = aHttpServerHandler;
		mSocket = aSocket;
		mHttpServer = aHttpServer;
	}


	@Override
	public void run()
	{
		try
		{
			try (InputStream in = mSocket.getInputStream())
			{
				long startTime = System.currentTimeMillis();

				HttpServerRequest request = parseRequest(in);
				HttpServerResponse response = new HttpServerResponse();

				try
				{
					mHttpServerHandler.service(request, response);
				}
				catch (Error | Exception e)
				{
					e.printStackTrace(Log.out);

					response = new HttpServerResponse();
					response.setContent(("REMOTE-EXCEPTION: " + e.toString()).getBytes());
				}

				try (OutputStream out = mSocket.getOutputStream())
				{
					byte[] header = ("HTTP/1.0 " + response.getStatusCode() + "\r\nContent-Type: " + response.getContentType() + "\r\nContent-Length: " + response.getContentLength() + "\r\n\r\n").getBytes();

					byte[] temp = ByteArray.join(header, response.getContent());

					out.write(temp);
				}

				long endTime = System.currentTimeMillis();

				mHttpServer.printLogDetailed(startTime, request.getLocalAddress().getHostAddress(), request.getMethod(), request.getPath(), request.getLocalPort(), request.getRemoteAddress().getHostAddress(), response.getStatusCode().code, response.getContent().length, (endTime - startTime));
			}
			finally
			{
				mSocket.close();
				mSocket = null;
			}
		}
		catch (Exception | Error e)
		{
			mHttpServer.printLog(e);
		}
	}


	private HttpServerRequest parseRequest(InputStream aInputStream) throws IOException
	{
		HttpServerRequest request = new HttpServerRequest(mSocket);

		LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		boolean pathLoaded = false;

		StringBuilder buf = new StringBuilder(200);
		for (int code = 0; !(code == 0x0d0a0d0a || code == 0x0a0d0a0d);)
		{
			int c = aInputStream.read();

			if (c < 0)
			{
				break;
			}

			code = (code << 8) | c;

			if (c == '\r' || c == '\n')
			{
				if (!pathLoaded)
				{
					request.setMethod(buf.substring(0, buf.indexOf(" ")));
					String path = buf.substring(buf.indexOf(" ") + 1).trim();
					String protocol = path.substring(path.lastIndexOf(" ") + 1).toLowerCase();
					if (protocol.length() == 8 && protocol.contains("http"))
					{
						path = path.substring(0, path.length() - 9); // remove one space and the protocol, eg: " HTTP/1.1"
					}
					request.setPath(path);
					pathLoaded = true;
				}
				else
				{
					int i = buf.indexOf(":");
					if (i == -1)
					{
						headers.put(buf.toString(), null);
					}
					else
					{
						String key = buf.substring(0, i).trim();
						String value = buf.substring(i + 1).trim();
						headers.put(key, value);
					}
				}

				buf.setLength(0);
			}
			else
			{
				buf.append((char)c);
			}
		}

		request.setHeaders(headers);
		request.setInputStream(aInputStream);

		return request;
	}
}

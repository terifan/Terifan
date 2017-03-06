package org.terifan.net.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import org.terifan.io.ByteArray;
import org.terifan.util.log.Log;


public class SimpleHttpServer
{
	private ConnectionListener mConnectionListener;
	private HttpServerHandler mRequestHandler;
	private InetAddress mInetAddress;
	private boolean mDaemon;
	private int mPort;


	public SimpleHttpServer(int aPort, HttpServerHandler aRequestHandler) throws UnknownHostException
	{
		this(aPort, InetAddress.getByName("127.0.0.1"), aRequestHandler);
	}


	public SimpleHttpServer(int aPort, InetAddress aInetAddress, HttpServerHandler aRequestHandler)
	{
		mPort = aPort;
		mInetAddress = aInetAddress;
		mRequestHandler = aRequestHandler;
	}


	/**
	 * Start listening to the port specified in the constructor.
	 *
	 * @param aDaemon
	 *   true if the listener thread should be a daemon thread.
	 */
	public SimpleHttpServer start(boolean aDaemon)
	{
		mDaemon = aDaemon;

		if (mConnectionListener == null)
		{
			synchronized (SimpleHttpServer.class)
			{
				mConnectionListener = new ConnectionListener();
				mConnectionListener.start();
			}
		}

		return this;
	}


	/**
	 * Disconnects this socket listener.
	 *
	 * @param aBlock
	 *   true if this method should block until the socket listener has closed.
	 */
	public void close(boolean aBlock)
	{
		printLog("Shutdown started");

		mConnectionListener.mDisconnect = true;

		if (aBlock)
		{
			while (mConnectionListener.mDisconnect)
			{
				synchronized (SimpleHttpServer.class)
				{
					try
					{
						SimpleHttpServer.class.wait(1000);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}

		mConnectionListener = null;
	}


	protected void printLog(String aMessage)
	{
		//System.out.println(aMessage);
	}


	private class ConnectionListener extends Thread
	{
		private boolean mDisconnect;


		public ConnectionListener()
		{
			super.setDaemon(mDaemon);
			super.setName("SimpleHttpListener.ConnectionListener");
			super.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
			{
				@Override
				public void uncaughtException(Thread t, Throwable e)
				{
					e.printStackTrace(Log.out);
				}
			});
		}


		@Override
		public void run()
		{
			printLog("Start listening to port " + mPort);

			ServerSocket serverSocket = null;

			try
			{
				try
				{
					serverSocket = new ServerSocket(mPort, 0, mInetAddress);
					serverSocket.setSoTimeout(1000);

					printLog("Waiting for connection");

					while (!mDisconnect)
					{
						try
						{
							Socket socket = serverSocket.accept();

							if (socket != null)
							{
								new ConnectionHandler(mRequestHandler, socket).start();
							}
						}
						catch (SocketTimeoutException e)
						{
							printLog("Waiting for connection");
						}
					}
				}
				finally
				{
					if (serverSocket != null)
					{
						serverSocket.close();
					}

					mDisconnect = false;

					synchronized (SimpleHttpServer.class)
					{
						SimpleHttpServer.class.notify();
					}

					printLog("Shutdown completed");
				}
			}
			catch (Throwable e)
			{
				throw new IllegalStateException(e);
			}
		}
	}


	private static class ConnectionHandler extends Thread
	{
		private HttpServerHandler mRequestHandler;
		private Socket mSocket;


		public ConnectionHandler(HttpServerHandler aRequestHandler, Socket aSocket)
		{
			mRequestHandler = aRequestHandler;
			mSocket = aSocket;
		}


		@Override
		public void run()
		{
			try
			{
				try (InputStream in = mSocket.getInputStream())
				{
					HttpServerRequest request = parseRequest(in);
					HttpServerResponse response = new HttpServerResponse();

					try
					{
						mRequestHandler.service(request, response);
					}
					catch (Throwable e)
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
				}
				finally
				{
					mSocket.close();
					mSocket = null;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.out);
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

				code = (code << 8) | c;

				if (c == '\r' || c == '\n')
				{
					if (!pathLoaded)
					{
						request.setMethod(buf.substring(0, buf.indexOf(" ")));
						String path = buf.substring(buf.indexOf(" ")+1).trim();
						String protocol = path.substring(path.lastIndexOf(" ")+1).toLowerCase();
						if (protocol.length() == 8 && protocol.contains("http"))
						{
							path = path.substring(0, path.length()-9); // remove one space and the protocol, eg: " HTTP/1.1"
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
							String value = buf.substring(i+1).trim();
							headers.put(key, value);
						}
					}

					buf.setLength(0);
				}
				else
				{
					buf.append((char) c);
				}
			}

			request.setHeaders(headers);
			request.setInputStream(aInputStream);

			return request;
		}
	}
}

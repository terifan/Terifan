package org.terifan.net.http.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;


public class SimpleHttpServer
{
	protected SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private final Object LOCK = new Object(){};

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


	public HttpServerHandler getRequestHandler()
	{
		return mRequestHandler;
	}


	public SimpleHttpServer setRequestHandler(HttpServerHandler aRequestHandler)
	{
		mRequestHandler = aRequestHandler;
		return this;
	}


	/**
	 * Start listening to the port specified in the constructor.
	 *
	 * @param aDaemon true if the listener thread should be a daemon thread.
	 */
	public SimpleHttpServer start(boolean aDaemon)
	{
		mDaemon = aDaemon;

		if (mConnectionListener == null)
		{
			synchronized (LOCK)
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
	 * @param aBlock true if this method should block until the socket listener has closed.
	 */
	public void shutdown(boolean aBlock)
	{
		ConnectionListener listener;
		synchronized (LOCK)
		{
			listener = mConnectionListener;
			mConnectionListener = null;
		}

		if (listener != null)
		{
			listener.mDisconnect = true;

			if (aBlock)
			{
				while (listener.mDisconnect)
				{
					synchronized (LOCK)
					{
						try
						{
							LOCK.wait(1000);
						}
						catch (InterruptedException e)
						{
						}
					}
				}
			}
		}
	}


	/**
	 * Override this method to handle/print/store log messages.
	 *
	 * @param aMessage
	 *   either a String or an Exception
	 */
	protected void printLog(Object aMessage)
	{
	}


	protected void printLogDetailed(long aStartTime, String aLocalAddress, String aMethod, String aPath, int aLocalPort, String aRemoteAddress, int aResponseCode, int aResponseLength, long aResponseTime)
	{
		printLog(LOG_DATE_FORMAT.format(aStartTime) + " " + aLocalAddress + " " + aMethod + " \"" + aPath + "\" " + aLocalPort + " " + aRemoteAddress + " " + aResponseCode + " " + aResponseLength + " " + aResponseTime);
	}


	private class ConnectionListener extends Thread
	{
		private boolean mDisconnect;


		public ConnectionListener()
		{
			super.setDaemon(mDaemon);
			super.setName("SimpleHttpListener.ConnectionListener");
			super.setUncaughtExceptionHandler((Thread t, Throwable e) -> printLog(e));
		}


		@Override
		public void run()
		{
			printLog("HTTP server starting listening to port " + mPort);

			ServerSocket serverSocket = null;

			try
			{
				try
				{
					serverSocket = new ServerSocket(mPort, 0, mInetAddress);
					serverSocket.setSoTimeout(1000);

					while (!mDisconnect)
					{
						try
						{
							Socket socket = serverSocket.accept();

							if (socket != null)
							{
								new ConnectionHandler(SimpleHttpServer.this, mRequestHandler, socket).start();
							}
						}
						catch (SocketTimeoutException e)
						{
							// ignore
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

					synchronized (LOCK)
					{
						LOCK.notify();
					}

					printLog("HTTP server shutdown");
				}
			}
			catch (Exception | Error e)
			{
				printLog(e);
			}
		}
	}
}

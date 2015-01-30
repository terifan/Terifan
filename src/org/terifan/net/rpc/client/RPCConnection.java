package org.terifan.net.rpc.client;

import org.terifan.net.rpc.shared.RPCRemoteException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import org.terifan.util.Pool;
import org.terifan.io.ByteArray;
import org.terifan.net.rpc.shared.AsyncResult;
import org.terifan.net.rpc.shared.Compression;
import org.terifan.net.rpc.shared.MessageType;
import org.terifan.net.rpc.shared.LoginException;
import org.terifan.net.rpc.shared.Message;
import org.terifan.net.rpc.shared.Part;
import org.terifan.net.rpc.shared.PartType;
import org.terifan.net.rpc.shared.Password;
import org.terifan.net.rpc.shared.Tools;
import org.terifan.security.Cipher;
import org.terifan.security.SHA256;
import org.terifan.security.SecretKey;
import org.terifan.security.Twofish;
import org.terifan.util.Calendar;
import org.terifan.util.ProgressListener;
import org.terifan.util.log.Log;


public class RPCConnection implements Closeable
{
	private URL mServerAddress;
	private UUID mSessionID;
	private Cipher mServerCipher;
	private Cipher mClientCipher;
	private Message mQueueMessage;
	private Compression mCompression;
	private ProgressListener mProgressListener;
	private PrintStream mLogOutput;
	private String mLoginName;
	private String mPassword;
	private boolean mConnected;
	private boolean mClosing;
	private int mMessageIndex;
	private Long mMostRecentResponseTime;
	private Pool<RPCConnection> mConnectionPool;
	private HashMap<String, AsyncResult> mAsyncResults;
	private CallbackListener mCallbackListener;


	public RPCConnection(String aServerAddress)
	{
		init(aServerAddress, null, null, null);
	}


	public RPCConnection(String aServerAddress, String aUserName, String aPassword)
	{
		init(aServerAddress, aUserName, aPassword, null);
	}


	public RPCConnection(URL aServerAddress)
	{
		init(aServerAddress, null, null, null);
	}


	public RPCConnection(URL aServerAddress, String aUserName, String aPassword)
	{
		init(aServerAddress, aUserName, aPassword, null);
	}


	private void init(Object aRPCServerAddress, String aLoginName, String aPassword, PrintStream aLogOutput)
	{
		if (aRPCServerAddress instanceof String)
		{
			try
			{
				aRPCServerAddress = new URL((String)aRPCServerAddress);
			}
			catch (MalformedURLException e)
			{
				throw new IllegalArgumentException("Provided server address is invalid: " + aRPCServerAddress, e);
			}
		}

		mServerAddress = (URL)aRPCServerAddress;
		mCompression = Compression.BEST_SPEED;
		mLogOutput = aLogOutput;
		mLoginName = aLoginName;
		mPassword = aPassword;

		mAsyncResults = new HashMap<>();
	}


	public RPCConnection setAuthorization(String aLoginName, String aPassword)
	{
		mLoginName = aLoginName;
		mPassword = aPassword;
		return this;
	}


	public CallbackListener getCallbackListener()
	{
		return mCallbackListener;
	}


	public RPCConnection setCallbackListener(CallbackListener aCallbackListener)
	{
		mCallbackListener = aCallbackListener;
		return this;
	}


	@Override
	public void close()
	{
		if (mConnectionPool != null)
		{
			printLog("Connection released to pool");

			mConnectionPool.release(this);
		}
		else
		{
			mClosing = true;

			Message msg = new Message(MessageType.DISCONNECT);
			transceive(msg);

			mConnected = false;
			mClientCipher = null;
			mServerCipher = null;
			mQueueMessage = null;
			mServerAddress = null;
			mSessionID = null;
			mProgressListener = null;
			mLoginName = null;
			mPassword = null;
			mClosing = false;
			mMostRecentResponseTime = null;
			mCallbackListener = null;

			printLog("Disconnected from server");
		}
	}


	public RPCConnection setConnectionPool(Pool<RPCConnection> aConnectionPool)
	{
		mConnectionPool = aConnectionPool;
		return this;
	}


	public Pool<RPCConnection> getConnectionPool()
	{
		return mConnectionPool;
	}


	/**
	 * Creates an instance of a RemoteObjectStub. The RemoteObjectStub contains wrapper methods for methods available on the RPC server.
	 *
	 * @return
	 *   an instance of the class prototype provided.
	 */
	public <T extends RemoteServiceStub> T create(Class<? extends RemoteServiceStub> aType)
	{
		try
		{
			RemoteServiceStub instance = (RemoteServiceStub)aType.newInstance();

			instance.init(this);

			return (T)instance;
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * Return true if the connection is still valid.
	 */
	public boolean isValid()
	{
		return true;
	}


	public RPCConnection setProgressListener(ProgressListener aListener)
	{
		mProgressListener = aListener;
		return this;
	}


	public ProgressListener getProgressListener()
	{
		return mProgressListener;
	}


	/**
	 * Enables or disables queuing of messages. AutoCommit is enabled by default.
	 *
	 * when false; messages will be queued in the connection object until either the commit method is called or a none-queueable method is
	 * used. when true; messages will be transmitted immediately to the RPC server.
	 *
	 * @param aAutoCommit if false then messages will be queued in the connection until either the commit method is called or a
	 * none-queueable method is used. If true the each method invoked will generated a call to the RPC server.
	 */
	public RPCConnection setAutoCommit(boolean aAutoCommit)
	{
		if (aAutoCommit)
		{
			commit();

			mQueueMessage = null;
		}
		else
		{
			if (mQueueMessage == null)
			{
				resetQueue();
			}
		}

		return this;
	}


	/**
	 * Return true if each message generate a call to the RPC server or false if messages can be queued.
	 */
	public boolean isAutoCommit()
	{
		return mQueueMessage == null;
	}


	public void commit()
	{
		if (mQueueMessage != null && mQueueMessage.getPartCount() > 0)
		{
			invoke(null);
		}
	}


//	public boolean removeQueuedPart(AsyncResult aAsyncResult)
//	{
//		for (Entry<String,AsyncResult> r : mAsyncResults.entrySet())
//		{
//			if (r.getValue().equals(aAsyncResult))
//			{
//
//			}
//		}
//
//		return false;
//	}


	public RPCConnection setLogOutput(PrintStream aLogOutput)
	{
		mLogOutput = aLogOutput;
		return this;
	}


	public PrintStream getLogOutput()
	{
		return mLogOutput;
	}


	public RPCConnection setCompression(Compression aCompression)
	{
		mCompression = aCompression;
		return this;
	}


	public Compression getCompression()
	{
		return mCompression;
	}


	private void printLog(Object aObject)
	{
		if (mLogOutput != null)
		{
			if (aObject instanceof Exception)
			{
				aObject = Log.getStackTraceStringFlatten((Exception)aObject);
			}

			mLogOutput.println("client: " + aObject);
		}
	}


	private void writeLogEntry(String aLoginName, Message aRequest, Message aResponse, Exception aException, long aTime)
	{
		String sessionID = (aResponse.getSessionID() == null ? aRequest.getSessionID() : aResponse.getSessionID()).toString();
		long time = (System.nanoTime() - aTime);
		String stacktrace = aException == null ? "" : Log.getStackTraceStringFlatten(aException);
		String loginName = aLoginName == null ? "" : aLoginName;

		printLog(Calendar.now() + "\t" + sessionID + "\t" + loginName + "\t" + aRequest.getMessageIndex() + "\t" + aResponse.getMessageIndex() + "\t" + aRequest.getMessageType() + "\t" + aResponse.getMessageType() + "\t" + aRequest.getLength() + "\t" + aResponse.getLength() + "\t" + time + "\t" + stacktrace);

//		for (int i = 0; i < aRequest.getPartCount(); i++)
//		{
//			printLog(aRequest.getMessageIndex()+"\trequest\t"+aRequest.getPart(i));
//		}
//
//		for (int i = 0; i < aResponse.getPartCount(); i++)
//		{
//			printLog(aResponse.getMessageIndex()+"\tresponse\t"+aResponse.getPart(i));
//		}
	}


	Object invoke(String aServiceName, String aMethodName, Object[] aParameters)
	{
		if (mServerAddress == null || mClosing)
		{
			throw new IllegalStateException(mClosing ? "Connection is closing" : "Connection is closed");
		}

		Object invoke = invoke(new Part(aServiceName, aMethodName, "", PartType.REQUEST, aParameters));

		if (invoke instanceof Exception)
		{
			Exception ex = (Exception)invoke;
			throw new RPCRemoteException("Remote invocation caused an exception", ex.getCause());
		}

		return invoke;
	}


	AsyncResult invokeQueued(String aServiceName, String aMethodName, Object[] aParameters)
	{
		if (mServerAddress == null || mClosing)
		{
			throw new IllegalStateException(mClosing ? "Connection is closing" : "Connection is closed");
		}

		AsyncResult result = new AsyncResult(this);

		if (mQueueMessage != null)
		{
			String partId = UUID.randomUUID().toString();

			mAsyncResults.put(partId, result);

			mQueueMessage.addPart(new Part(aServiceName, aMethodName, partId, PartType.ASYNC, aParameters));
		}
		else
		{
			result.set(invoke(aServiceName, aMethodName, aParameters));
		}

		return result;
	}


	private Object invoke(Part aPart)
	{
		Message reply;

		if (mQueueMessage != null && mQueueMessage.getPartCount() > 0)
		{
			if (aPart != null)
			{
				mQueueMessage.addPart(aPart);
			}

			reply = transceive(mQueueMessage);

			resetQueue();
		}
		else
		{
			reply = transceive(new Message(MessageType.CLIENT_MESSAGE, aPart));
		}

		Part replyPart = null;

		for (Part part : reply)
		{
			switch (part.getPartType())
			{
				case REQUEST:
					replyPart = part;
					break;
				case CALLBACK:
					invokeCallback(part);
					break;
				case ASYNC:
					String partId = part.getPartId();
					if (!partId.isEmpty() && mAsyncResults.containsKey(partId))
					{
						if (part.getParameterCount() > 0)
						{
							mAsyncResults.get(partId).set(part.getParameter(0));
						}
						mAsyncResults.remove(partId);
					}
					break;
			}
		}

		Object output = replyPart == null || replyPart.getParameterCount() == 0 ? null : replyPart.getParameter(0);

		if (output instanceof RPCRemoteException)
		{
			throw (RPCRemoteException)output;
		}

		return output;
	}


	private void invokeCallback(Part aPart)
	{
		try
		{
			Method method = Tools.findMethod(mCallbackListener, aPart.getServiceName(), aPart.getMethodName(), aPart.getParameters(), false);

			if (method == null)
			{
				Log.out.println("########## Callback method not found");

				return;
			}

			if (aPart.getParameterCount() == 0)
			{
				method.invoke(mCallbackListener);
			}
			else
			{
				method.invoke(mCallbackListener, aPart.getParameters());
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	private void resetQueue()
	{
		mQueueMessage = new Message(MessageType.CLIENT_MESSAGE);
	}


	private Message transceive(Message aMessage)
	{
		return transceive(aMessage, false);
	}


	private Message transceive(Message aMessage, boolean aRecursion)
	{
		if (!mConnected)
		{
			login();
		}

		Message output = transceiveImpl(aMessage, aRecursion);

		if (output.getMessageType() == MessageType.CHALLANGE)
		{
			clearState();
			login();
			output = transceiveImpl(aMessage, aRecursion);
		}

		if (output.getMessageType() == MessageType.DISCONNECT)
		{
			clearState();
		}

		return output;
	}


	private void clearState()
	{
		mConnected = false;
		mClientCipher = null;
		mServerCipher = null;
		mSessionID = null;
		mMessageIndex = 0;
		mMostRecentResponseTime = null;
	}


	private Message transceiveImpl(Message aMessage, boolean aRecursion)
	{
		long time = System.nanoTime();

		aMessage.setSessionID(mSessionID);

		try
		{
			byte[] requestData = aMessage.encode(mMessageIndex++, mServerCipher, mCompression == null ? Compression.BEST_SPEED : mCompression);

//			printLog("Sending request to server, "+requestData.length+" bytes");
			HttpURLConnection connection = (HttpURLConnection)mServerAddress.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.connect();

			sendRequest(connection, requestData);

			Message response = receiveResponse(connection, aMessage.getMessageIndex());

			writeLogEntry(mLoginName, aMessage, response, null, time);

			if (response.getMessageType() == MessageType.UNAUTHORIZED)
			{
				clearState();

				if (aRecursion)
				{
					throw new RPCRemoteException("Server responded with an invalid message");
				}

				return transceive(aMessage, true);
			}

//			for (int i = 0; i < aMessage.getPartCount(); i++)
//			{
//				printLog("Sending " + aMessage.getPart(i));
//			}
//
//			for (int i = 0; i < response.getPartCount(); i++)
//			{
//				printLog("Received " + response.getPart(i));
//			}
			return response;
		}
		catch (RPCRemoteException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw new RPCRemoteException("A communication error occured while connecting, sending or receiving data from server at: " + mServerAddress, e);
		}
	}


	private void sendRequest(HttpURLConnection aConnection, byte[] aRequestData) throws IOException
	{
		try (OutputStream outputStream = aConnection.getOutputStream())
		{
			for (int offset = 0, remain = aRequestData.length; remain > 0;)
			{
				int chunkSize = Math.min(remain, 4096);
				outputStream.write(aRequestData, offset, chunkSize);

				offset += chunkSize;
				remain -= chunkSize;

				updateWorkProgress(offset, aRequestData.length);
			}
		}
	}


	private Message receiveResponse(HttpURLConnection connection, int aMessageIndex) throws IOException
	{
		try (InputStream inputStream = connection.getInputStream())
		{
			byte[] header = new byte[Message.PUBLIC_HEADER_SIZE];
			inputStream.read(header);

			Message response = Message.decodeHeader(header);

			if (response == null)
			{
				throw new IOException("Received empty response from server.");
			}

			byte[] buf = receiveResponseBody(response, inputStream, header);

//			printLog("Received reply from server, "+response.getLength()+" bytes");
			response.decode(mClientCipher, buf);

			// verify security properties of message
			if (response.getMessageIndex() != aMessageIndex)
			{
				printLog("Request and response message index missmatch: request: " + aMessageIndex + ", response: " + response.getMessageIndex());
				return new Message(MessageType.UNAUTHORIZED);
			}
			if (mMostRecentResponseTime != null && response.getTime() < mMostRecentResponseTime)
			{
				printLog("Server timestamp on messages are out of order.");
				return new Message(MessageType.UNAUTHORIZED);
			}

			mMostRecentResponseTime = response.getTime();

			return response;
		}
	}


	private byte[] receiveResponseBody(Message aResponse, InputStream aInputStream, byte[] aHeader) throws IOException
	{
		byte[] buf = Arrays.copyOfRange(aHeader, 0, aResponse.getLength());

		for (int offset = Message.PUBLIC_HEADER_SIZE, total = aResponse.getLength() - offset, remain = total; remain > 0;)
		{
			int chunkSize = Math.min(remain, 4096);
			int read = aInputStream.read(buf, offset, chunkSize);

			if (read == -1)
			{
				throw new IOException("Premature end of response stream. Only received " + offset + " of " + aResponse.getLength() + " bytes");
			}

			remain -= read;
			offset += read;

			updateWorkProgress(offset, total);
		}

		return buf;
	}


	private void updateWorkProgress(int aOffset, int aMaximum)
	{
		if (mConnected && mProgressListener != null)
		{
			mProgressListener.setWorkProgress(aOffset, aMaximum, null);
		}
	}


	private void login()
	{
		try
		{
			printLog("Requesting authorization");

			// C1
			Message loginMessage = new Message(MessageType.AUTHORIZE);
			loginMessage.addPart(new Part("#", "login", "", PartType.PROTOCOL, mLoginName));
			loginMessage = transceiveImpl(loginMessage, false);
			if (loginMessage.getMessageType() != MessageType.CHALLANGE)
			{
				throw new IOException("Server responded with unexpected message on login attempt. " + loginMessage);
			}

			mSessionID = loginMessage.getSessionID();
			byte[] serverNonce = (byte[])loginMessage.getPart(0).getParameter(0);
			byte[] salt = (byte[])loginMessage.getPart(0).getParameter(1);

			if (serverNonce.length != 16 || salt.length != 16)
			{
				throw new IOException("Server responded with unexpected data types.");
			}

			// C2
			byte[] clientNonce = new byte[16];
			new SecureRandom().nextBytes(clientNonce);
			// C3
			byte[] password = Password.expandPassword(salt, mPassword);
			// C4
			byte[] clientAnswer = new SHA256().digest(ByteArray.join(clientNonce, password, serverNonce));
			// C5
			Message challangeMessage = new Message(MessageType.CHALLANGE_RESPONSE);
			challangeMessage.addPart(new Part("#", "authenticate", "", PartType.PROTOCOL, clientNonce, clientAnswer));
			challangeMessage = transceiveImpl(challangeMessage, false);
			if (challangeMessage.getMessageType() != MessageType.AUTHORIZED)
			{
				printLog("User authentication failed: user name: " + mLoginName);
				throw new LoginException();
			}

			// C6
			byte[] clientVerification = new SHA256().digest(ByteArray.join(serverNonce, password, clientNonce));
			// C7
			byte[] serverAnswer = (byte[])challangeMessage.getPart(0).getParameter(0);
			if (!Arrays.equals(serverAnswer, clientVerification))
			{
				throw new SecurityException("Failed to authenticate server; server responded with an incorrect authorization key.");
			}
			// C8
			byte[] masterKey = new SHA256().digest(ByteArray.join(clientAnswer, serverAnswer, password));
			// C9
			byte[] clientKey = Arrays.copyOfRange(masterKey, 0, 16);
			byte[] serverKey = Arrays.copyOfRange(masterKey, 16, 32);

			mClientCipher = new Twofish(new SecretKey(clientKey));
			mServerCipher = new Twofish(new SecretKey(serverKey));

//			System.out.println("clientNonce=" + Convert.bytesToHex(clientNonce));
//			System.out.println("serverNonce=" + Convert.bytesToHex(serverNonce));
//			System.out.println("serverAnswer=" + Convert.bytesToHex(serverAnswer));
//			System.out.println("clientAnswer=" + Convert.bytesToHex(clientAnswer));
//			System.out.println("salt=" + Convert.bytesToHex(salt));
//			System.out.println("password=" + Convert.bytesToHex(password));
//			System.out.println("masterKey=" + Convert.bytesToHex(masterKey));
//			System.out.println("clientKey=" + Convert.bytesToHex(clientKey));
//			System.out.println("serverKey=" + Convert.bytesToHex(serverKey));
			printLog("Authorized session started: " + mSessionID);

			mConnected = true;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

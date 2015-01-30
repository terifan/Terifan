package org.terifan.net.rpc.server;

import java.util.HashMap;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.terifan.io.ByteArray;
import org.terifan.net.rpc.shared.Compression;
import org.terifan.net.rpc.shared.Message;
import org.terifan.net.rpc.shared.MessageType;
import org.terifan.net.rpc.shared.Part;
import org.terifan.net.rpc.shared.PartType;
import org.terifan.net.rpc.shared.RPCRemoteException;
import org.terifan.net.rpc.shared.Tools;
import org.terifan.security.Cipher;
import org.terifan.security.SHA256;
import org.terifan.security.SecretKey;
import org.terifan.security.Twofish;
import org.terifan.util.Calendar;
import org.terifan.util.log.Log;


public class RPCServer
{
	private ServiceFactory mServiceFactory;
	private Map<UUID, Session> mSessions;
	private Map<UUID, Session> mPendingSessions;
	private Authenticator mAuthenticator;
	private PrintStream mLogOutput;
//	private int mMessageIndex;
//	private Integer mMostRecentRequestIndex;
//	private Long mMostRecentRequestTime;


	public RPCServer()
	{
		mSessions = new HashMap<>();
		mPendingSessions = new HashMap<>();
	}


	public Map<UUID, Session> getSessions()
	{
		return mSessions;
	}


	public RPCServer setAuthenticator(Authenticator aAuthenticator)
	{
		mAuthenticator = aAuthenticator;
		return this;
	}


	public Authenticator getAuthenticator()
	{
		return mAuthenticator;
	}


	public RPCServer setServiceFactory(ServiceFactory aFactory)
	{
		mServiceFactory = aFactory;
		return this;
	}


	public ServiceFactory getServiceFactory()
	{
		return mServiceFactory;
	}


	/**
	 * Processes a request from the RPCClient. This method will decode the message and call the requested methods in the RPCService
	 * implementation. Any response from the RPCService implementation will be encoded and return by this method.
	 *
	 * @param aMessageData the encoded message from the client.
	 * @return the encoded response from the RPCService implementation method called.
	 */
	public byte[] processRequest(byte[] aMessageData) throws IOException
	{
		byte[] messageData = aMessageData.clone();

		int messageIndex;
		Message request = null;
		Message response = null;
		Session session = null;
		Exception exception = null;
		String loginName = null;
		long time = System.nanoTime();

		try
		{
			request = Message.decodeHeader(messageData);

			// process message
			if (request.getMessageType() == MessageType.AUTHORIZE)
			{
				request.decode(null, messageData);
				loginName = (String)request.getPart(0).getParameter(0);
				response = processLogin(request);
				messageIndex = 0;
			}
			else if (request.getMessageType() == MessageType.CHALLANGE_RESPONSE)
			{
				request.decode(null, messageData);
				Session pendingSession = mPendingSessions.get(request.getSessionID());
				if (pendingSession == null)
				{
					throw new IllegalStateException("Session not found: " + request.getSessionID());
				}
				loginName = pendingSession.getUserName();
				response = processAuthenticate(request);
				messageIndex = 1;
			}
			else
			{
				session = mSessions.get(request.getSessionID());

				if (session == null)
				{
					messageIndex = 0;
					response = new Message(MessageType.CHALLANGE);
				}
				else
				{
					messageIndex = session.getMessageIndex();

					loginName = session.getUserName();

					request.decode(session.getServerCipher(), messageData);


					// verify security properties of message
					if (request.getMessageIndex() != session.getMessageIndex())
					{
						throw new SecurityException("Request message index missmatch " + session.getMessageIndex() + " " + request.getMessageIndex());
					}
					if (request.getTime() < session.getLastMessageTime())
					{
						throw new SecurityException("Client timestamp on messages are out of order." + request.getTime()+" < "+session.getLastMessageTime());
					}

					session.incrementMessageIndex();
					session.setLastMessageTime(request.getTime());
					session.setAccessTime(System.currentTimeMillis());


					response = new Message(MessageType.SERVER_MESSAGE);
					response.setSessionID(request.getSessionID());

					for (Part part : request)
					{
						try
						{
							part = invokeMethod(session, part);
						}
						catch (Throwable e)
						{
							printLog(e);

							e = e.getCause() != null ? e.getCause() : e;
							e = new RPCRemoteException("Remote invocation caused an exception", e);

							part = new Part(part.getServiceName(), part.getMethodName(), part.getPartId(), PartType.ERROR, e);
						}

						response.addPart(part);
					}

					for (Part part : session.getCallbacks())
					{
						response.addPart(part);
					}

					if (request.getMessageType() == MessageType.DISCONNECT)
					{
						response.setMessageType(MessageType.DISCONNECT);
						mSessions.remove(request.getSessionID());
					}
				}
			}
		}
		catch (IOException e)
		{
			printLog(e);

			exception = e;

			if (request == null)
			{
				return null;
			}

			response = new Message(MessageType.ERROR);
			response.setSessionID(request.getSessionID());
			response.addPart(new Part("#", "error", "", PartType.ERROR, e));

			// TODO
			messageIndex = 0;
		}

		Cipher cipher = null;
		if (session != null)
		{
			cipher = session.getClientCipher();
		}
		byte[] resp = response.encode(messageIndex, cipher, request.getCompression() == null ? Compression.BEST_SPEED : request.getCompression());

		writeLogEntry(loginName, request, response, exception, time);

		return resp;
	}


	private void writeLogEntry(String aLoginName, Message aRequest, Message aResponse, Exception aException, long aTime)
	{
		long time = System.nanoTime() - aTime;
		String sessionID = (aResponse.getSessionID() == null ? aRequest.getSessionID() : aResponse.getSessionID()).toString();
		String stacktrace = aException == null ? "" : Log.getStackTraceStringFlatten(aException);
		String loginName = aLoginName == null ? "" : aLoginName;
		String now = Calendar.now();

		printLog("server: " + now + "\t" + sessionID + "\t" + loginName + "\t" + aRequest.getMessageIndex() + "\t" + aResponse.getMessageIndex() + "\t" + aRequest.getMessageType() + "\t" + aResponse.getMessageType() + "\t" + aRequest.getLength() + "\t" + aResponse.getLength() + "\t" + time + "\t" + stacktrace);

//		for (int i = 0; i < aRequest.getPartCount(); i++)
//		{
//			printLog("server-request:  " + i + "\t" + aRequest.getPart(i));
//		}
//
//		for (int i = 0; i < aResponse.getPartCount(); i++)
//		{
//			printLog("server-response: " + i + "\t" + aResponse.getPart(i));
//		}
	}


	private Message processLogin(Message aRequest) throws IOException
	{
		String userName = (String)aRequest.getPart(0).getParameter(0);

		// S1
		byte[] serverNonce = new byte[16];

		SecureRandom rnd = new SecureRandom();
		rnd.nextBytes(serverNonce);

		UUID sessionID = UUID.randomUUID();
		Session session = new Session(this, sessionID, userName, serverNonce);
		mPendingSessions.put(sessionID, session);

		// S2
		byte[] salt = mAuthenticator.getUserSalt(session);

		// S3
		Part part = new Part("#", "challange", "", PartType.PROTOCOL, serverNonce, salt);
		Message response = new Message(MessageType.CHALLANGE);
		response.setSessionID(sessionID);
		response.addPart(part);

		//printLog("Pending session: "+session.getSessionID());
		return response;
	}


	private Message processAuthenticate(Message aRequest) throws IOException
	{
		Session session = mPendingSessions.remove(aRequest.getSessionID());

		if (session == null)
		{
			Message response = new Message(MessageType.DISCONNECT);
			response.setSessionID(aRequest.getSessionID());
			return response;
		}

		// S4
		byte[] password = mAuthenticator.getUserPassword(session);
		// S5
		byte[] clientNonce = (byte[])aRequest.getPart(0).getParameter(0);
		byte[] serverNonce = (byte[])session.getProperty("RPCServerNonce");
		byte[] serverVerification = new SHA256().digest(ByteArray.join(clientNonce, password, serverNonce));
		// S6
		byte[] clientAnswer = (byte[])aRequest.getPart(0).getParameter(1);
		if (!Arrays.equals(clientAnswer, serverVerification))
		{
			Message response = new Message(MessageType.DISCONNECT);
			response.setSessionID(aRequest.getSessionID());
			return response;
		}
		// S7
		byte[] serverAnswer = new SHA256().digest(ByteArray.join(serverNonce, password, clientNonce));
		// S8
		byte[] masterKey = new SHA256().digest(ByteArray.join(clientAnswer, serverAnswer, password));
		// S9
		byte[] clientKey = Arrays.copyOfRange(masterKey, 0, 16);
		byte[] serverKey = Arrays.copyOfRange(masterKey, 16, 32);
		// S10
		Message response = new Message(MessageType.AUTHORIZED);
		response.setSessionID(aRequest.getSessionID());
		response.addPart(new Part("#", "authorized", "", PartType.PROTOCOL, serverAnswer));

		session.initialize(new Twofish(new SecretKey(clientKey)), new Twofish(new SecretKey(serverKey)));
		session.setLastMessageTime(aRequest.getTime());

		mSessions.put(session.getSessionID(), session);

		return response;
	}


	private Part wrapPart(Part aRequestingPart, PartType aPartType, Object aOutput)
	{
		if (aOutput instanceof Part)
		{
			throw new IllegalArgumentException("aOutput instanceof Part");
		}

		return new Part(aRequestingPart.getServiceName(), aRequestingPart.getMethodName(), aRequestingPart.getPartId(), aPartType, aOutput);
	}


	protected Part invokeMethod(Session aSession, Part aPart) throws IOException, NoSuchMethodException
	{
		AbstractRemoteService service = mServiceFactory.newInstance(aSession, aPart.getServiceName());

		if (service == null)
		{
			return wrapPart(aPart, PartType.ERROR, new NoSuchMethodException("Service not found: " + aPart));
		}

		Method method = Tools.findMethod(service, aPart.getServiceName(), aPart.getMethodName(), aPart.getParameters(), true);

		//printLog("Invoking: "+aPart);
		if (method == null)
		{
			return wrapPart(aPart, PartType.ERROR, new UnsupportedOperationException("Remote method not found: service: " + aPart));
		}

		if (!mAuthenticator.permitInvocation(aSession, service, method))
		{
			return wrapPart(aPart, PartType.ERROR, new UnsupportedOperationException("You are not authorized to access the remote method: " + aPart));
		}

		try
		{
			return invokeMethod(method, service, aPart);
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.out);

			if (e.getCause() != null)
			{
				e = e.getCause();
			}
			return wrapPart(aPart, PartType.ERROR, new IOException("Exception while executing remote method: " + aPart, e));
		}
	}


	protected Part invokeMethod(Method aMethod, AbstractRemoteService aService, Part aPart) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object response;

		if (aPart.getParameterCount() == 0)
		{
			response = aMethod.invoke(aService);
		}
		else
		{
			response = aMethod.invoke(aService, aPart.getParameters());
		}

		return wrapPart(aPart, aPart.getPartType(), response);
	}


	public RPCServer setLogOutput(PrintStream aLogOutput)
	{
		mLogOutput = aLogOutput;
		return this;
	}


	public PrintStream getLogOutput()
	{
		return mLogOutput;
	}


	private void printLog(Object aObject)
	{
		if (mLogOutput != null)
		{
			if (aObject instanceof Exception)
			{
				aObject = "server: " + Log.getStackTraceStringFlatten((Exception)aObject);
			}

			mLogOutput.println(aObject);
		}
	}
}

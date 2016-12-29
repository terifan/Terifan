package org.terifan.net.rpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.terifan.net.rpc.shared.Part;
import org.terifan.net.rpc.shared.PartType;
import org.terifan.security.Cipher;


public final class Session
{
	private long mCreateTime;
	private long mAccessTime;
	private UUID mSessionID;
	private String mUserName;
	private Cipher mClientCipher;
	private Cipher mServerCipher;
	private Map<String,Object> mProperties;
	private int mMessageIndex;
	private long mLastMessageTime;
	private RPCServer mServer;
	private ArrayList<Part> mCallbacks;


	Session(RPCServer aServer, UUID aSessionID, String aUserName, byte [] aServerNonce)
	{
		mServer = aServer;
		mCreateTime = System.currentTimeMillis();
		mSessionID = aSessionID;
		mUserName = aUserName;
		mCallbacks = new ArrayList<>();
		mProperties = new HashMap<>();
		mProperties.put("RPCServerNonce", aServerNonce);
	}


	void initialize(Cipher aClientCipher, Cipher aServerCipher)
	{
		mClientCipher = aClientCipher;
		mServerCipher = aServerCipher;
		mProperties.remove("RPCServerNonce");
		mMessageIndex = 2;
	}


	public synchronized void callback(String aMethodName, Object... aParameters)
	{
		mCallbacks.add(new Part("", aMethodName, "", PartType.CALLBACK, aParameters));
	}


	public synchronized ArrayList<Part> getCallbacks()
	{
		ArrayList<Part> tmp = new ArrayList<>();
		tmp.addAll(mCallbacks);
		mCallbacks.clear();
		return tmp;
	}


	public RPCServer getServer()
	{
		return mServer;
	}


	public Object getProperty(String aName)
	{
		return mProperties.get(aName);
	}


	public void putProperty(String aName, Object aValue)
	{
		mProperties.put(aName, aValue);
	}


	Cipher getServerCipher()
	{
		return mServerCipher;
	}


	Cipher getClientCipher()
	{
		return mClientCipher;
	}


	public UUID getSessionID()
	{
		return mSessionID;
	}


	public String getUserName()
	{
		return mUserName;
	}


	public long getCreateTime()
	{
		return mCreateTime;
	}


	public long getAccessTime()
	{
		return mAccessTime;
	}


	void setAccessTime(long aAccessTime)
	{
		mAccessTime = aAccessTime;
	}


	long getLastMessageTime()
	{
		return mLastMessageTime;
	}


	void setLastMessageTime(long aTime)
	{
		mLastMessageTime = aTime;
	}


	int getMessageIndex()
	{
		return mMessageIndex;
	}


	void incrementMessageIndex()
	{
		mMessageIndex++;
	}
}
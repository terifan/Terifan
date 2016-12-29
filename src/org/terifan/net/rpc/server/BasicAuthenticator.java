package org.terifan.net.rpc.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import org.terifan.net.rpc.shared.Password;


public class BasicAuthenticator implements Authenticator
{
	private String mUserName;
	private byte[] mSalt;
	private byte[] mPassword;


	public BasicAuthenticator(String aUserName, String aPassword)
	{
		mUserName = aUserName;
		mSalt = new byte[16];
		new SecureRandom().nextBytes(mSalt);
		mPassword = Password.expandPassword(mSalt, aPassword);
	}


	@Override
	public byte[] getUserSalt(Session aSession) throws IOException
	{
		if (aSession.getUserName().equals(mUserName))
		{
			return mSalt;
		}
		return null;
	}


	@Override
	public byte[] getUserPassword(Session aSession) throws IOException
	{
		return mPassword;
	}


	@Override
	public boolean permitInvocation(Session aSession, AbstractRemoteService aService, Method aMethod) throws IOException
	{
		return true;
	}
}

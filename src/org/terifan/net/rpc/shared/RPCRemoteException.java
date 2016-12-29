package org.terifan.net.rpc.shared;


public class RPCRemoteException extends RuntimeException
{
	public RPCRemoteException(String message)
	{
		super(message);
	}


	public RPCRemoteException(String aMessage, Throwable e)
	{
		super(aMessage, e);
	}
}

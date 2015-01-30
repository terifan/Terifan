package org.terifan.net.rpc.shared;

import org.terifan.net.rpc.client.RPCConnection;
import org.terifan.util.log.Log;



public class AsyncResult<E>
{
	private boolean mDone;
	private E mValue;
	private RPCConnection mConnection;


	public AsyncResult(RPCConnection aConnection)
	{
		mConnection = aConnection;
	}


	public boolean isDone()
	{
		return mDone;
	}


	public E get()
	{
		return get(Long.MAX_VALUE, null);
	}


	public E get(long aTimeOutMillis, E aTimeOutValue)
	{
		if (!mDone)
		{
			mConnection.commit();
		}

		// TODO: use locking
		while (!mDone)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
			}
		}

		return mValue;
	}


	public void set(E aValue)
	{
		mDone = true;
		this.mValue = aValue;
	}


//	public boolean cancel()
//	{
//		return mConnection.removeQueuedPart(this);
//	}
}

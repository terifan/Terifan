package org.terifan.net.rpc.client;

import java.util.concurrent.Future;
import org.terifan.net.rpc.shared.AsyncResult;
import org.terifan.net.rpc.shared.ServiceName;
import org.terifan.util.Strings;


public abstract class RemoteServiceStub
{
	private RPCConnection mClient;
	private String mName;


	void init(RPCConnection aClient)
	{
		ServiceName annot = getClass().getAnnotation(ServiceName.class);

		if (annot == null || Strings.isEmptyOrNull(annot.value()))
		{
			throw new IllegalArgumentException("Annotation 'ServiceName' containing the name of the service is missing in the service implementation class.");
		}

		mClient = aClient;
		mName = annot.value();
	}


	protected AsyncResult invokeQueued(String aMethodName, Object... aParameters)
	{
		return mClient.invokeQueued(mName, aMethodName, aParameters);
	}


	protected Object invoke(String aMethodName, Object... aParameters)
	{
		return mClient.invoke(mName, aMethodName, aParameters);
	}
}

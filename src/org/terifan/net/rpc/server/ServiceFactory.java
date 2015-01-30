package org.terifan.net.rpc.server;

import java.util.HashMap;
import org.terifan.net.rpc.shared.ServiceName;


public class ServiceFactory
{
	private HashMap<String, Class<? extends AbstractRemoteService>> mImplementations;


	public ServiceFactory()
	{
		mImplementations = new HashMap<>();
	}


	public ServiceFactory register(Class<? extends AbstractRemoteService> aImplementation)
	{
		ServiceName annot = aImplementation.getAnnotation(ServiceName.class);

		if (annot == null)
		{
			throw new IllegalArgumentException("Annotation RemoteImplementation missing in implementation of " + aImplementation + ".");
		}

		mImplementations.put(annot.value(), aImplementation);

		return this;
	}


	/**
	 * Returns an instance of AbstractRemoteService previously registered to the name provided.
	 *
	 * Replace this method with custom object instantiation if necessary.
	 *
	 * @throws NoSuchMethodException
	 *   if the service name isn't registered
	 */
	protected AbstractRemoteService newInstance(String aServiceName) throws NoSuchMethodException, IllegalAccessException, InstantiationException
	{
		Class<? extends AbstractRemoteService> cls = mImplementations.get(aServiceName);

		if (cls == null)
		{
			throw new NoSuchMethodException("Service not found: " + aServiceName);
		}

		return cls.newInstance();
	}


	protected final AbstractRemoteService newInstance(Session aSession, String aServiceName) throws NoSuchMethodException
	{
		try
		{
			AbstractRemoteService service = newInstance(aServiceName);
			service.setSession(aSession);
			return service;
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			// @Sonar: for security reasons discard IllegalAccessException from thrown exception
			throw new NoSuchMethodException("Service not found: " + aServiceName);
		}
	}
}

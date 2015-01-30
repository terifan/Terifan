package org.terifan.net.rpc.shared;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.terifan.net.rpc.client.CallbackListener;
import org.terifan.net.rpc.server.AbstractRemoteService;


public final class Tools
{
	public static Method findMethod(Object aService, String aServiceName, String aMethodName, Object[] aParameters, boolean aRemote) throws NoSuchMethodException
	{
		Method method = null;

		if (aParameters == null || aParameters.length == 0)
		{
			try
			{
				method = aService.getClass().getMethod(aMethodName);
			}
			catch (NoSuchMethodException e)
			{
				// @Sonar: for security reasons discard NoSuchMethodException from thrown exception
			}
		}
		else
		{
			for (Method m : aService.getClass().getMethods())
			{
				if (m.getName().equals(aMethodName))
				{
					Class[] paramTypes = m.getParameterTypes();

					if (paramTypes.length == aParameters.length)
					{
						boolean match = true;

						for (int i = 0; match && i < paramTypes.length; i++)
						{
//							String type = paramTypes[i].getSimpleName();
//
//							if (type.equals("int"))
//							{
//								type = "integer";
//							}
//							if (type.equals("char"))
//							{
//								type = "character";
//							}

//							if (aParameters[i] == null && paramTypes[i].isPrimitive() || aParameters[i] != null && !type.equalsIgnoreCase(aParameters[i].getClass().getSimpleName()))

							if (aParameters[i] == null && paramTypes[i].isPrimitive() || aParameters[i] != null && !paramTypes[i].isAssignableFrom(aParameters[i].getClass()))
							{
								match = false;
							}
						}

						if (match)
						{
							if (method != null)
							{
								throw new NoSuchMethodException("More than one method take same set of parameters. Method: " + m);
							}
							method = m;
						}
					}
				}
			}
		}

		if (method == null || (aRemote && method.getAnnotation(AbstractRemoteService.RemoteMethod.class) == null) || (!aRemote && method.getAnnotation(CallbackListener.CallbackMethod.class) == null))
		{
			ArrayList<String> types = new ArrayList<>();

			if (aParameters != null)
			{
				for (Object param : aParameters)
				{
					types.add(param == null ? "null" : param.getClass().getSimpleName());
				}
			}

			throw new NoSuchMethodException("Method not found or annotation missing: " + aServiceName + "." + aMethodName + types.toString().replace('[', '(').replace(']', ')'));
		}

		method.setAccessible(true);

		return method;
	}
}

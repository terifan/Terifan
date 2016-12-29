package org.terifan.net.rpc.server;

import java.lang.reflect.Method;
import org.terifan.net.rpc.server.AbstractRemoteService.RemoteMethod;
import org.terifan.net.rpc.server.AbstractRemoteService.RemoteParam;
import org.terifan.net.rpc.server.AbstractRemoteService.RemoteReturn;


abstract class AbstractBuilder
{
	static class MethodWrapper implements Comparable<MethodWrapper>
	{
		String mSignature;
		Method mMethod;
		String mName;
		String mDescription;
		String mReturnType;
		String mReturnDescription;
		ParamWrapper[] mParams;


		public MethodWrapper(Method aMethod)
		{
			RemoteMethod rm = (RemoteMethod) aMethod.getAnnotation(RemoteMethod.class);

			this.mMethod = aMethod;
			this.mName = aMethod.getName();
			this.mDescription = rm.value() == null ? "" : rm.value();
			this.mReturnType = aMethod.getReturnType().getSimpleName();

			if (aMethod.getAnnotation(RemoteReturn.class) != null)
			{
				RemoteReturn rr = ((RemoteReturn) aMethod.getAnnotation(RemoteReturn.class));
				this.mReturnDescription = rr.value();
			}
			else
			{
				this.mReturnDescription = "";
			}

			Class[] paramTypes = aMethod.getParameterTypes();
			this.mParams = new ParamWrapper[paramTypes.length];

			this.mSignature = aMethod.getName() + "(";

			for (int i = 0; i < paramTypes.length; i++)
			{
				if (i > 0)
				{
					this.mSignature += ", ";
				}
				this.mSignature += paramTypes[i].getSimpleName();

				if (aMethod.getAnnotation(RemoteParam.class) != null)
				{
					RemoteParam rp = ((RemoteParam) aMethod.getAnnotation(RemoteParam.class));
					this.mParams[i] = new ParamWrapper(paramTypes[i], rp.value()[2 * i + 0], rp.value()[2 * i + 1]);
				}
				else
				{
					this.mParams[i] = new ParamWrapper(paramTypes[i], "", "");
				}
			}

			this.mSignature += ")";
		}


		@Override
		public int compareTo(MethodWrapper aOther)
		{
			return mSignature.compareTo(aOther.mSignature);
		}


		@Override
		public boolean equals(Object aOther)
		{
			return (aOther instanceof MethodWrapper) && this.mSignature.equals(((MethodWrapper)aOther).mSignature);
		}


		@Override
		public int hashCode()
		{
			return mSignature.hashCode();
		}
	}


	static class ParamWrapper
	{
		String mType;
		String mName;
		String mDescription;


		public ParamWrapper(Class aType, String aName, String aDescription)
		{
			this.mType = aType.getSimpleName();
			this.mName = aName;
			this.mDescription = aDescription;
		}
	}
}

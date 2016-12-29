package org.terifan.net.rpc.server;

import org.terifan.net.rpc.shared.ServiceName;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;


public abstract class AbstractRemoteService
{
	private Session mSession;


	void setSession(Session aSession)
	{
		mSession = aSession;
	}


	protected Session getSession()
	{
		return mSession;
	}


	public final String getName()
	{
		ServiceName annot = getClass().getAnnotation(ServiceName.class);
		
		if (annot == null)
		{
			throw new IllegalStateException("Annotation RemoteImplementation missing in implementation of "+getClass()+".");
		}

		return annot.value();
	}


	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{
		METHOD
	})
	public @interface RemoteMethod
	{
		String value() default "";
	}


	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{
		METHOD
	})
	public @interface RemoteParam
	{
		String [] value();
	}


	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{
		METHOD
	})
	public @interface RemoteReturn
	{
		String value();
	}
}

package org.terifan.net.rpc.client;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class CallbackListener
{
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{
		METHOD
	})
	public @interface CallbackMethod
	{
	}
}

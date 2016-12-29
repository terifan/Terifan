package org.terifan.net.rpc.shared;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
	TYPE
})
public @interface ServiceName
{
	String value();
}
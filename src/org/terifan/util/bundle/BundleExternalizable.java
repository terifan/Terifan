package org.terifan.util.bundle;

import java.io.IOException;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;


public interface BundleExternalizable
{
	default void readExternal(Bundle aBundle) throws IOException {};

	default void writeExternal(Bundle aBundle) throws IOException {};


	@Retention(RUNTIME)
	@Target({FIELD,TYPE})
	public @interface Bundlable
	{
		String value() default "";
	}
}

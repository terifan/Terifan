package org.terifan.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = {ElementType.METHOD, ElementType.FIELD}) @Retention(value = RetentionPolicy.RUNTIME)
public @interface Column
{
	public final static class NO_PRODUCER implements ValueProducer
	{
		@Override
		public Object produce(Object aEntity, Column aColumn)
		{
			return null;
		}
	}


	public String name() default "";


	public boolean publish() default false;


	public boolean generated() default false;


	public boolean nullable() default true;


	public EnumType enumType() default EnumType.ORDINAL;


	public Class<? extends ValueProducer> producer() default NO_PRODUCER.class;


//	public boolean unique() default false;
//
//
//	public boolean insertable() default true;
//
//
//	public boolean updatable() default true;
//
//
//	public String columnDefinition() default "";
//
//
//	public String table() default "";
//
//
//	public int length() default 255;
//
//
//	public int precision() default 0;
//
//
//	public int scale() default 0;
}

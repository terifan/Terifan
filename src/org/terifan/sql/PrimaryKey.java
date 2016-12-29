package org.terifan.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = {ElementType.METHOD, ElementType.FIELD}) @Retention(value = RetentionPolicy.RUNTIME)
public @interface PrimaryKey
{
	public String name() default "";


	public boolean enumName() default false;


	public boolean generated() default false;


//	public boolean publish() default false;
//
//
//	public boolean unique() default false;
//
//
//	public boolean nullable() default true;
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

package org.terifan.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ConsoleLogger
{
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


	public static void println(String aMessage, Object... aArguments)
	{
		try
		{
			StackTraceElement el = new Exception().getStackTrace()[1];

			System.out.println(DATE_FORMAT.format(new Date()) + " " + el.getClassName() + "." + el.getMethodName() + ":" + el.getLineNumber() + " " + String.format(aMessage, aArguments));
		}
		catch (Exception e)
		{
		}
	}


	public static void main(String ... args)
	{
		try
		{
			println("test %s", 4L);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
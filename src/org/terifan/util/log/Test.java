package org.terifan.util.log;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			Log.addTarget(LogLevel.ERROR, new FileTarget("d:\\", "test.log"));
			Log.e("Test", "msg");
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

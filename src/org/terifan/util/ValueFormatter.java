package org.terifan.util;


public class ValueFormatter
{
	public static String formatBytesSize(long aBytes)
	{
		if (aBytes < 1024)
		{
			return aBytes + " B";
		}
		if (aBytes < 1024 * 1024 * 10)
		{
			return String.format("%d KB", aBytes / 1024);
		}
		if (aBytes < 1024 * 1024 * 1024L * 10)
		{
			return String.format("%d MB", aBytes / 1024 / 1024);
		}
		if (aBytes < 1024 * 1024 * 1024L * 1024L * 10)
		{
			return String.format("%d GB", aBytes / 1024 / 1024 / 1024);
		}
		return String.format("%d TB", aBytes / 1024 / 1024 / 1024 / 1024);
	}


	public static String formatBytesPerSecond(long aBytesPerSecond)
	{
		if (aBytesPerSecond < 1024)
		{
			return String.format("%d B/s", aBytesPerSecond);
		}
		if (aBytesPerSecond < 1024 * 1024)
		{
			return String.format("%.1f KB/s", aBytesPerSecond / 1024.0);
		}
		return String.format("%.1f MB/s", aBytesPerSecond / 1024.0 / 1024.0);
	}


	public static String formatDuration(long aMillis)
	{
		if (aMillis < 1000)
		{
			return String.format("0.%3d", aMillis);
		}
		if (aMillis < 60 * 1000)
		{
			return String.format("%.1f", aMillis / 1000.0);
		}
		if (aMillis < 60 * 60 * 1000)
		{
			return String.format("%d:%02d", aMillis / 60000, (aMillis % 60000) / 1000);
		}
		return String.format("%d:%02d:%02d", aMillis / 360000, (aMillis / 60000) % 60, (aMillis % 60000) / 1000);
	}


	public static void main(String ... args)
	{
		try
		{
			System.out.println(formatBytesSize(100));
			System.out.println(formatBytesSize(1000));
			System.out.println(formatBytesSize(10000));
			System.out.println(formatBytesSize(1000000));
			System.out.println(formatBytesSize(10000000));
			System.out.println(formatBytesSize(100000000));
			System.out.println(formatBytesSize(1000000000));
			System.out.println(formatBytesSize(10000000000L));
			System.out.println(formatBytesSize(100000000000L));
			System.out.println(formatBytesSize(1000000000000L));
			System.out.println(formatBytesSize(10000000000000L));
			System.out.println(formatBytesSize(100000000000000L));

			System.out.println(formatDuration(100));
			System.out.println(formatDuration(1000));
			System.out.println(formatDuration(10000));
			System.out.println(formatDuration(100000));
			System.out.println(formatDuration(1000000));
			System.out.println(formatDuration(10000000));
			System.out.println(formatDuration(100000000));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

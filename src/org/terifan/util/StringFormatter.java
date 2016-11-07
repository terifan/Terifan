package org.terifan.util;


public class StringFormatter 
{
	/**
	 * 3s, 1m09s, 1h04m02s
	 */
	public static String asDurationText(long aDuration)
	{
		aDuration /= 1000;
		if (aDuration < 60)
		{
			return aDuration + "s";
		}
		if (aDuration < 3600)
		{
			return String.format("%dm%02ds", aDuration / 60, aDuration % 60);
		}
		return String.format("%dh%02dmd%s", aDuration / 3600, (aDuration / 60) % 60, aDuration % 60);
	}


	/**
	 * H:mm:ss
	 */
	public static String asDuration(long aDuration)
	{
		aDuration /= 1000;
		if (aDuration > 3600)
		{
			return String.format("%d:%02d:%02d", aDuration / 3600, (aDuration / 60) % 60, aDuration % 60);
		}
		return String.format("%02d:%02d", aDuration / 60, aDuration % 60);
	}
}

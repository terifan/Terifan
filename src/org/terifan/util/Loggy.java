package org.terifan.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.terifan.util.log.Log;


/**
 * A very lightweight logger capable of logging directly to file only. Define output directory and logging level using public variables.
 */
public class Loggy
{
	private final static SimpleDateFormat mTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private final static SimpleDateFormat mFilenameFormatter = new SimpleDateFormat("yyyyMMdd");

	public enum LogLevel
	{
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	/**
	 * The output directory of log files
	 */
	public static File LOG_DIRECTORY = new File("c:/temp");
	/**
	 * Minimum level of messages written to the log
	 */
	public static LogLevel LEVEL = LogLevel.DEBUG;


	private Loggy()
	{
	}


	public static void d(String aTag, Object aText)
	{
		println(LogLevel.DEBUG, aTag, aText, null);
	}


	public static void d(String aTag, Object aText, Throwable aException)
	{
		println(LogLevel.DEBUG, aTag, aText, aException);
	}


	public static void i(String aTag, Object aText)
	{
		println(LogLevel.INFO, aTag, aText, null);
	}


	public static void i(String aTag, Object aText, Throwable aException)
	{
		println(LogLevel.INFO, aTag, aText, aException);
	}


	public static void w(String aTag, Object aText)
	{
		println(LogLevel.WARN, aTag, aText, null);
	}


	public static void w(String aTag, Object aText, Throwable aException)
	{
		println(LogLevel.WARN, aTag, aText, aException);
	}


	public static void e(String aTag, Object aText)
	{
		println(LogLevel.ERROR, aTag, aText, null);
	}


	public static void e(String aTag, Object aText, Throwable aException)
	{
		println(LogLevel.ERROR, aTag, aText, aException);
	}


	public static synchronized void println(LogLevel aLevel, String aTag, Object aText, Throwable aException)
	{
		try
		{
			if (LEVEL.ordinal() > aLevel.ordinal())
			{
				return;
			}

			if (aText == null)
			{
				aText = "";
			}
			else if (aException == null && aText instanceof Throwable)
			{
				aException = (Throwable)aText;
				aText = "";
			}
			if (aTag == null)
			{
				aTag = "";
			}

			aText = aText.toString().replace("\t", "").replace("\n", "").replace("\r", "");
			aTag = aTag.replace("\t", "").replace("\n", "").replace("\r", "");

			Date date = new Date();
			String time = mTimeFormatter.format(date);
			String err = aException == null ? "" : aException.toString();
			String filename = mFilenameFormatter.format(date);
			File logFile = new File(LOG_DIRECTORY, filename + ".log");

			if (aException != null && err.isEmpty())
			{
				err = "Unknown error";
			}

			try (FileWriter out = new FileWriter(logFile, true))
			{
				out.write(time + "\t" + aLevel + "\t" + aTag + "\t" + aText + "\t" + err + "\n");

				if (aException != null)
				{
					out.write(Log.getStackTraceString(aException));
				}
			}
		}
		catch (Throwable e)
		{
		}
	}
}

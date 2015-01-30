package org.terifan.util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;
import org.terifan.util.Calendar;


/**
 * File name pattern may contain keywords "tag" or "level" or must be a valid date format string enclosed in braces.
 */
public class FileTarget implements LogTarget
{
	private File mDirectory;
	private String mFileNamePattern;


	/**
	 * Create instance
	 *
	 * @param aFileNamePattern
	 *    a filename pattern what may contain keywords "tag" and "level" and also datetime formatters, e.g. "file_{level}_{yyyyMMdd}.log"
	 * @see
	 *    java.text.SimpleDateFormat
	 */
	public FileTarget(String aDirectory, String aFileNamePattern)
	{
		this(new File(aDirectory), aFileNamePattern);
	}


	/**
	 * Create instance
	 *
	 * @param aFileNamePattern
	 *    a filename pattern what may contain keywords "tag" and "level" and also datetime formatters, e.g. "file_{level}_{yyyyMMdd}.log"
	 * @see
	 *    java.text.SimpleDateFormat
	 */
	public FileTarget(File aDirectory, String aFileNamePattern)
	{
		if (!aDirectory.isDirectory())
		{
			throw new IllegalArgumentException("Provided aDirectory is not a directory: " + aDirectory);
		}

		mDirectory = aDirectory;
		mFileNamePattern = aFileNamePattern;
	}


	@Override
	public synchronized void writeLogEntry(Date aDateTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable)
	{
		try
		{
			File file = getOutputFile(aDateTime, aTag, aLogLevel);

			try (Writer writer = new BufferedWriter(new FileWriter(file, true)))
			{
				if (aThrowable != null)
				{
					writer.write("------------------------------------------------------------------------------------------------");
					writer.write(System.getProperty("line.separator"));
				}
				writer.write(new Calendar().format("yyyy-MM-dd HH:mm:ss.SSS"));
				writer.write("\t");
				writer.write(String.format("%07x", Thread.currentThread().getId()));
				writer.write("\t");
				writer.write(aLogLevel.name());
				writer.write("\t");
				writer.write(aTag);
				writer.write("\t");
				writer.write(aMessage);
				writer.write("\t");
				if (aThrowable != null)
				{
					writer.write(Log.getStackTraceString(aThrowable).replace("\t", "   "));
					writer.write("------------------------------------------------------------------------------------------------");
				}
				writer.write(System.getProperty("line.separator"));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.err);
		}
	}


	private File getOutputFile(Date aDateTime, String aTag, LogLevel aLogLevel) throws IllegalArgumentException
	{
		String name = mFileNamePattern;

		if (name.contains("{"))
		{
			Calendar c = new Calendar(aDateTime.getTime());

			for (int i; (i = name.indexOf("{")) != -1;)
			{
				String key = name.substring(i + 1, name.indexOf("}", i));
				String value;

				switch (key)
				{
					case "tag":
						value = aTag;
						break;
					case "level":
						value = aLogLevel.name();
						break;
					default:
						value = c.format(key).toLowerCase();
						break;
				}

				name = name.replace("{" + key + "}", value);
			}
		}

		return new File(mDirectory, name);
	}


	@Override
	public boolean equals(Object aObject)
	{
		if (aObject instanceof FileTarget)
		{
			FileTarget other = (FileTarget)aObject;
			return mDirectory.equals(other.mDirectory) && mFileNamePattern.equals(other.mFileNamePattern);
		}
		return false;
	}
}

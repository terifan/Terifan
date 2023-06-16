package org.terifan.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


/**
 * Retarded developers at Oracle fail to implement even the most basic functions...
 */
public class DateTimeHelper
{
	public final static LocalDateTime getLocalDateTime(FileTime aFileTime) throws IOException
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(aFileTime.toMillis()), ZoneId.systemDefault());
	}


	public final static long getFileCreationTimeMillis(Path aPath) throws IOException
	{
		return Files.readAttributes(aPath, BasicFileAttributes.class).creationTime().toMillis();
	}


	public final static LocalDateTime getFileCreationTime(Path aPath) throws IOException
	{
		return getLocalDateTimeFromMillis(getFileCreationTimeMillis(aPath));
	}


	public final static LocalDateTime getFileModifiedTime(Path aPath) throws IOException
	{
		return getLocalDateTimeFromMillis(Files.getLastModifiedTime(aPath).toMillis());
	}


	public final static LocalDateTime getLocalDateTimeFromMillis(long aMillis)
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(aMillis), ZoneId.systemDefault());
	}
}

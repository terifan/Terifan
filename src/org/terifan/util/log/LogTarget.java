package org.terifan.util.log;

import java.util.Date;


public interface LogTarget
{
	public void writeLogEntry(Date aDateTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable);
}

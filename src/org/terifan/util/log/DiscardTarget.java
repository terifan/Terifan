package org.terifan.util.log;

import java.util.Date;


public class DiscardTarget implements LogTarget
{
	@Override
	public void writeLogEntry(Date aDateTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable)
	{
	}
}

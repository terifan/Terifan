package org.terifan.util.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MemoryTarget implements LogTarget
{
	private ArrayList<Entry> mEntries;


	public MemoryTarget()
	{
		mEntries = new ArrayList<Entry>();
	}


	@Override
	public void writeLogEntry(Date aDateTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable)
	{
		mEntries.add(new Entry(aLogLevel, aTag, aMessage, aThrowable));
	}


	public void clear()
	{
		mEntries.clear();
	}


	public List<Entry> getEntries()
	{
		return new ArrayList<Entry>(mEntries);
	}


	public void forwardLog(LogTarget aTarget)
	{
		for (Entry entry : mEntries)
		{
			aTarget.writeLogEntry(entry.getDateTime(), entry.getLevel(), entry.getTag(), entry.getMessage(), entry.getThrowable());
		}
	}


	public static class Entry
	{
		private Date mDateTime;
		private LogLevel mLevel;
		private String mTag;
		private String mMessage;
		private Throwable mThrowable;


		public Entry(LogLevel aLevel, String aTag, String aMessage, Throwable aThrowable)
		{
			mDateTime = new Date();
			mLevel = aLevel;
			mTag = aTag;
			mMessage = aMessage;
			mThrowable = aThrowable;
		}


		public Date getDateTime()
		{
			return mDateTime;
		}


		public LogLevel getLevel()
		{
			return mLevel;
		}


		public String getMessage()
		{
			return mMessage;
		}


		public String getTag()
		{
			return mTag;
		}


		public Throwable getThrowable()
		{
			return mThrowable;
		}
	}
}

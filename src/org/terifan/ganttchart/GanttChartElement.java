package org.terifan.ganttchart;

import java.util.ArrayList;


public class GanttChartElement
{
	private ArrayList<GanttChartElement> mSubElements;
	private long mStartTime;
	private long mEndTime;
	private String mDescription;
	private int mColor;


	GanttChartElement(long aStartTime, String aDescription, int aColor)
	{
		mStartTime = aStartTime;
		mEndTime = aStartTime;
		mDescription = aDescription;
		mColor = aColor;
	}


	public ArrayList<GanttChartElement> getSubElements()
	{
		if (mSubElements == null)
		{
			mSubElements = new ArrayList<>();
		}
		return mSubElements;
	}


	public long getStartTime()
	{
		return mStartTime;
	}


	public long getEndTime()
	{
		return mEndTime;
	}


	void setEndTime(long aEndTime)
	{
		mEndTime = aEndTime;
	}


	public String getDescription()
	{
		return mDescription;
	}


	public int getColor()
	{
		return mColor;
	}
}

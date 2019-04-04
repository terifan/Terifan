package org.terifan.ganttchart;

import java.util.ArrayList;


public class Element
{
	ArrayList<Element> mSubItems = new ArrayList<>();
	long mStartTime;
	long mEndTime;
	String mName;
	int mColor;


	Element(long aStartTime, String aName, int aColor)
	{
		mStartTime = aStartTime;
		mEndTime = aStartTime;
		mName = aName;
		mColor = aColor;
	}


	public ArrayList<Element> getSubItems()
	{
		return mSubItems;
	}


	public long getStartTime()
	{
		return mStartTime;
	}


	public long getEndTime()
	{
		return mEndTime;
	}


	public String getName()
	{
		return mName;
	}


	public int getColor()
	{
		return mColor;
	}
}

package org.terifan.ganttchart;

import java.util.ArrayList;


class Element
{
	ArrayList<Element> mSubItems = new ArrayList<>();
	long mStartTime;
	long mEndTime;
	String mName;
	int mColor;


	public Element(long aStartTime, String aName, int aColor)
	{
		mStartTime = aStartTime;
		mEndTime = aStartTime;
		mName = aName;
		mColor = aColor;
	}
}

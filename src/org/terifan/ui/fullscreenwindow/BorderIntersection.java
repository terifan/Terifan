package org.terifan.ui.fullscreenwindow;


public class BorderIntersection
{
	protected BorderIntersectionType mType;
	protected Object mComponent;


	public BorderIntersection(BorderIntersectionType aType)
	{
		mType = aType;
	}


	public BorderIntersection(BorderIntersectionType aType, Object aComponent)
	{
		mType = aType;
		mComponent = aComponent;
	}


	public BorderIntersectionType getType()
	{
		return mType;
	}


	public <T> T getComponent()
	{
		return (T)mComponent;
	}
}

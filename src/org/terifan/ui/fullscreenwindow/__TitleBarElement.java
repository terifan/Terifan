package org.terifan.ui.fullscreenwindow;

import java.awt.Rectangle;
import java.util.ArrayList;


public class __TitleBarElement
{
	private ArrayList<__TitleBarElement> mChildren;
	private Rectangle mBounds;
	private String mLabel;
	private int mId;
	private int mTypeId;


	public __TitleBarElement()
	{
	}


	public int getId()
	{
		return mId;
	}


	public __TitleBarElement setId(int aId)
	{
		mId = aId;
		return this;
	}


	public Rectangle getBounds()
	{
		return mBounds;
	}


	public __TitleBarElement setBounds(Rectangle aBounds)
	{
		mBounds = aBounds;
		return this;
	}


	public __TitleBarElement setBounds(int aX, int aY, int aWidth, int aHeight)
	{
		mBounds = new Rectangle(aX, aY, aWidth, aHeight);
		return this;
	}


	public __TitleBarElement add(__TitleBarElement aElement)
	{
		mChildren.add(aElement);
		return this;
	}


	public ArrayList<__TitleBarElement> getChildren()
	{
		return mChildren;
	}


	public String getLabel()
	{
		return mLabel;
	}


	public __TitleBarElement setLabel(String aLabel)
	{
		mLabel = aLabel;
		return this;
	}


	public int getTypeId()
	{
		return mTypeId;
	}


	public __TitleBarElement setTypeId(int aTypeId)
	{
		mTypeId = aTypeId;
		return this;
	}
}

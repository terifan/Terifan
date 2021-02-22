package org.terifan.ui.fullscreenwindow;

import java.awt.Rectangle;
import java.util.ArrayList;


public class TitleBarElement
{
	private ArrayList<TitleBarElement> mChildren;
	private Rectangle mBounds;
	private String mLabel;
	private int mId;
	private int mTypeId;


	public TitleBarElement()
	{
	}


	public int getId()
	{
		return mId;
	}


	public TitleBarElement setId(int aId)
	{
		mId = aId;
		return this;
	}


	public Rectangle getBounds()
	{
		return mBounds;
	}


	public TitleBarElement setBounds(Rectangle aBounds)
	{
		mBounds = aBounds;
		return this;
	}


	public TitleBarElement setBounds(int aX, int aY, int aWidth, int aHeight)
	{
		mBounds = new Rectangle(aX, aY, aWidth, aHeight);
		return this;
	}


	public TitleBarElement add(TitleBarElement aElement)
	{
		mChildren.add(aElement);
		return this;
	}


	public ArrayList<TitleBarElement> getChildren()
	{
		return mChildren;
	}


	public String getLabel()
	{
		return mLabel;
	}


	public TitleBarElement setLabel(String aLabel)
	{
		mLabel = aLabel;
		return this;
	}


	public int getTypeId()
	{
		return mTypeId;
	}


	public TitleBarElement setTypeId(int aTypeId)
	{
		mTypeId = aTypeId;
		return this;
	}
}

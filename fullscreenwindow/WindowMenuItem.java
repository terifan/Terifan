package org.terifan.ui.fullscreenwindow;

import java.awt.Rectangle;


public class WindowMenuItem
{
	private Rectangle mBounds;
	private WindowMenuBar mParent;
	private String mLabel;


	public WindowMenuItem(String aLabel)
	{
		mLabel = aLabel;
		mBounds = new Rectangle();
	}


	public String getLabel()
	{
		return mLabel;
	}


	void setParent(WindowMenuBar aParent)
	{
		mParent = aParent;
	}


	public Rectangle getBounds()
	{
		return mBounds;
	}


	public void setBounds(Rectangle aBounds)
	{
		mBounds = aBounds;
	}


	public void setBounds(int aX, int aY, int aWidth, int aHeight)
	{
		mBounds = new Rectangle(aX, aY, aWidth, aHeight);
	}
}

package org.terifan.ui.fullscreenwindow;

import java.awt.Rectangle;


public class WindowTabItem
{
	private Rectangle mBounds;
	private String mLabel;
	private WindowTabBar mTabBar;
	private TabSelectedHandler mTabSelectedHandler;


	public WindowTabItem(String aLabel)
	{
		mLabel = aLabel;
		mBounds = new Rectangle();
	}


	public String getLabel()
	{
		return mLabel;
	}


	void setParent(WindowTabBar aParent)
	{
		mTabBar = aParent;
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


	public WindowTabItem setOnTabSelected(TabSelectedHandler aHandler)
	{
		mTabSelectedHandler = aHandler;
		return this;
	}


	public TabSelectedHandler getTabSelectedHandler()
	{
		return mTabSelectedHandler;
	}


	public WindowTabBar getTabBar()
	{
		return mTabBar;
	}
}

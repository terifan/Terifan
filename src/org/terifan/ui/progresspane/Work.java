package org.terifan.ui.progresspane;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Work
{
	private Work mParent;
	private int mLimit;
	private int mProgress;

	protected ArrayList<Work> mChildren;
	protected ProgressPane mPane;
	protected int mHeight;
	private String mLabel;


	public Work(int aLimit)
	{
		this(aLimit, "");
	}


	public Work(int aLimit, String aLabel)
	{
		mLimit = aLimit;
		mHeight = 20;
		mLabel = aLabel;
	}


	public String getLabel()
	{
		return mLabel;
	}


	public void setLabel(String aLabel)
	{
		this.mLabel = aLabel;
	}


	public int getProgress()
	{
		return mProgress;
	}


	public int getLimit()
	{
		return mLimit;
	}


	public void add(Work aWork)
	{
		if (mChildren == null)
		{
			mChildren = new ArrayList<>();
		}

		aWork.mParent = this;
		mChildren.add(aWork);

		getPane().update();
	}


	public ProgressPane getPane()
	{
		if (mPane != null)
		{
			return mPane;
		}
		return mParent.getPane();
	}


	public void finishUnblocked()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				finish();
			}
		}.start();
	}

	public void finish()
	{
		ProgressPane pane = getPane();

		if (mChildren != null)
		{
			while (!mChildren.isEmpty())
			{
				synchronized (this)
				{
					try
					{
						wait(1000);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}

		while (mHeight > 0)
		{
			mHeight--;
			pane.update();

			try
			{
				Thread.sleep(pane.getCollapseAnimationDelay());
			}
			catch (InterruptedException e)
			{
			}
		}

		if (mParent != null)
		{
			synchronized (mParent)
			{
				mParent.notify();
			}
			mParent.mChildren.remove(this);
			pane.update();
		}
	}


	public void incrementProgress()
	{
		mProgress++;
		getPane().update();
	}


	public boolean isLimitReached()
	{
		return mProgress >= mLimit;
	}
}

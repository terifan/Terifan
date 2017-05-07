package org.terifan.ui.progresspane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import javax.swing.JPanel;


public class ProgressPane extends JPanel
{
	private ArrayList<Work> mWork;
	private ArrayList<Work> mPendingWork;
	private int mCollapseAnimationDelay;
	private int mRepaintFrequency;


	public ProgressPane()
	{
		mWork = new ArrayList<>();
		mPendingWork = new ArrayList<>();
		mCollapseAnimationDelay = 25;
		mRepaintFrequency = 50;
	}


	public ProgressPane add(Work aWork)
	{
		aWork.mPane = this;
		synchronized (mPendingWork)
		{
			mPendingWork.add(aWork);
		}
		return this;
	}


	public int getCollapseAnimationDelay()
	{
		return mCollapseAnimationDelay;
	}


	public ProgressPane setCollapseAnimationDelay(int aCollapseAnimationDelay)
	{
		mCollapseAnimationDelay = aCollapseAnimationDelay;
		return this;
	}


	public int getRepaintFrequency()
	{
		return mRepaintFrequency;
	}


	public ProgressPane setRepaintFrequency(int aRepaintFrequency)
	{
		mRepaintFrequency = aRepaintFrequency;
		return this;
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(100,1000);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		aGraphics.setColor(Color.WHITE);
		aGraphics.fillRect(0, 0, getWidth(), getHeight());

		synchronized (mPendingWork)
		{
			mWork.addAll(mPendingWork);
			mPendingWork.clear();
		}
		
		paintImpl(aGraphics, 0, 0, mWork);
	}


	private int paintImpl(Graphics aGraphics, int x, int y, ArrayList<Work> aWork)
	{
		int s = 30;
		int w = getWidth() - x * s;

		for (Work work : aWork)
		{
			if (work.mHeight > 0)
			{
				int p = (int)(work.getProgress() * w / (double)work.getLimit());

				aGraphics.setColor(Color.RED);
				aGraphics.fillRect(x * s, y, p, work.mHeight);
				aGraphics.setColor(Color.BLUE);
				aGraphics.fillRect(x * s + p, y, w-p, work.mHeight);
				aGraphics.setColor(Color.YELLOW);

				Shape oldClip = aGraphics.getClip();
				aGraphics.setClip(x * s, y, w, work.mHeight);
				aGraphics.drawString((work.getProgress() * 100 / (work.getLimit() == 0 ? 100 : work.getLimit())) + "% " + work.getLabel(), w/2-50, y + work.mHeight-5);
				aGraphics.setClip(oldClip);

				y += work.mHeight + 2;
			}

			if (work.mChildren != null && !work.mChildren.isEmpty())
			{
				y = paintImpl(aGraphics, x + 1, y, work.mChildren);
			}
		}

		return y;
	}
}
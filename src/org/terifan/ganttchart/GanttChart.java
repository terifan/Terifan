package org.terifan.ganttchart;

import java.util.ArrayList;
import java.util.TreeMap;
import static org.terifan.ganttchart.GanttChartPanel.C;


public class GanttChart implements AutoCloseable
{
	private final ArrayList<Long> mStack;

	final TreeMap<Long, GanttChartElement> mMap;
	GanttChartPanel mPanel;
	
	private long mStartTime;
	private long mEndTime;
	private int ci;


	public GanttChart()
	{
		mMap = new TreeMap<>();
		mStack = new ArrayList<>();
	}


	public void tick(String aDescription)
	{
		long time = System.nanoTime();
		
		mMap.get(mStack.get(mStack.size() - 1)).getSubElements().add(new GanttChartElement(time, aDescription, C[ci++ % C.length]));

		mEndTime = time;

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}


	public GanttChart enter(String aDescription)
	{
		long time = System.nanoTime();

		if (mMap.isEmpty())
		{
			mStartTime = time - 1; // to avoid divide by zero else where...
			mEndTime = time;
		}
		
		mStack.add(time);
		
		mMap.put(time, new GanttChartElement(time, aDescription, C[ci++ % C.length]));

		if (mPanel != null)
		{
			mPanel.revalidate();
			mPanel.repaint();
		}

		return this;
	}


	public void exit()
	{
		long key = mStack.remove(mStack.size() - 1);
		long time = System.nanoTime();

		mMap.get(key).setEndTime(time);

		mEndTime = time;

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}
	

	@Override
	public void close()
	{
		exit();
	}


	public long getStartTime()
	{
		return mStartTime;
	}


	public long getEndTime()
	{
		return mStack.isEmpty() ? mEndTime : System.nanoTime();
	}
}

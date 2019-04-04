package org.terifan.ganttchart;

import java.util.ArrayList;
import java.util.TreeMap;
import static org.terifan.ganttchart.GanttChartPanel.C;


public class GanttChart implements AutoCloseable
{
	private final ArrayList<Long> mStack;

	final TreeMap<Long, GanttChartElement> mMap;
	GanttChartPanel mPanel;

	private int ci;


	public GanttChart()
	{
		mMap = new TreeMap<>();
		mStack = new ArrayList<>();
	}


	public void tick(String aName)
	{
		mMap.get(mStack.get(mStack.size() - 1)).getSubElements().add(new GanttChartElement(System.nanoTime(), aName, C[ci++ % C.length]));

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}


	public GanttChart start(String aName)
	{
		Long key = System.nanoTime();
		mStack.add(key);
		mMap.put(key, new GanttChartElement(key, aName, C[ci++ % C.length]));

		if (mPanel != null)
		{
			mPanel.revalidate();
			mPanel.repaint();
		}

		return this;
	}


	@Override
	public void close() throws Exception
	{
		Long key = mStack.remove(mStack.size() - 1);
		mMap.get(key).setEndTime(System.nanoTime());

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}
}

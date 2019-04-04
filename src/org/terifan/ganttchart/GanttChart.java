package org.terifan.ganttchart;

import java.util.ArrayList;
import java.util.TreeMap;
import static org.terifan.ganttchart.GanttChartPanel.C;


public class GanttChart implements AutoCloseable
{
	GanttChartPanel mPanel;
	final TreeMap<Long, Element> mMap;
	final ArrayList<Long> mStack;

	private int ci;


	public GanttChart()
	{
		mMap = new TreeMap<>();
		mStack = new ArrayList<>();
	}


	public void tick(String aName)
	{
		mMap.get(mStack.get(mStack.size() - 1)).mSubItems.add(new Element(System.nanoTime(), aName, C[ci++ % C.length]));

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}


	public GanttChart start(String aName)
	{
		Long key = System.nanoTime();
		mStack.add(key);
		mMap.put(key, new Element(key, aName, C[ci++ % C.length]));
		return this;
	}


	@Override
	public void close() throws Exception
	{
		Long key = mStack.remove(mStack.size() - 1);
		mMap.get(key).mEndTime = System.nanoTime();

		if (mPanel != null)
		{
			mPanel.repaint();
		}
	}
}

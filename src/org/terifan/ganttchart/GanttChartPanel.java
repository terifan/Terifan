package org.terifan.ganttchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.JPanel;
import org.terifan.ui.Utilities;


public class GanttChartPanel extends JPanel
{
	private final GanttChart mGanttChart;

	final static int[] C =
	{
		0xEF7586, 0xE599FF, 0xE08453, 0x7F9FBF, 0x56C83F
	};

	private final static Color ROW_LIGHT = new Color(255, 255, 255);
	private final static Color ROW_DARK = new Color(242, 242, 242);
	private final static int MINIMUM_WIDTH = 50;

	private int mRowHeight = 24;
	private int mItemHeight = 9;
	private int mLabelWidth = 200;
	private int mRightMargin = 50;
	private Font mLabelFont = new Font("segoe ui", Font.PLAIN, 12);
	private Font mTimeFont = new Font("segoe ui", Font.PLAIN, 9);

	private final GanntChartDetailPanel mDetailPanel;
	private Element mSelectedElement;


	public GanttChartPanel(GanttChart aGanttChart, GanntChartDetailPanel aDetailPanel)
	{
		mGanttChart = aGanttChart;

		mDetailPanel = aDetailPanel;
		mGanttChart.mPanel = this;

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent aEvent)
			{
				int i = aEvent.getY() / mRowHeight;

				TreeMap<Long, Element> map = mGanttChart.mMap;

				if (i < map.size())
				{
					Element element = map.get(map.keySet().toArray(new Long[map.size()])[i]);
					mDetailPanel.setElement(element);
					mDetailPanel.repaint();

					mSelectedElement = element;

					repaint();
				}
			}
		});
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		Utilities.enableAntialiasing(aGraphics);
		Utilities.enableTextAntialiasing(aGraphics);
		Utilities.enableBilinear(aGraphics);

		TreeMap<Long, Element> map = mGanttChart.mMap;

		int w = Math.max(getWidth(), mLabelWidth + mRightMargin + MINIMUM_WIDTH);
		int h = getHeight();

		aGraphics.setColor(Color.WHITE);
		aGraphics.fillRect(0, 0, w, getHeight());

		for (int i = 0, y = 0; y < h; i++, y += mRowHeight)
		{
			aGraphics.setColor((i & 1) == 0 ? ROW_LIGHT : ROW_DARK);
			aGraphics.fillRect(0, y, w, mRowHeight);
		}

		if (map.isEmpty())
		{
			return;
		}

		Entry<Long, Element> firstEntry = map.firstEntry();
		long start = firstEntry.getValue().mStartTime;
		long end = firstEntry.getValue().mEndTime;

		if (end == start)
		{
			end = System.nanoTime();
		}

		int wi = w - mLabelWidth - mRightMargin;
		int y = 0;

		for (Element item : map.values())
		{
			drawElement(aGraphics, item, y, w, start, end, wi);

			y += mRowHeight;
		}

		drawGrid(aGraphics, wi, h);
	}


	private void drawGrid(Graphics aGraphics, int aContentWidth, int aHeight)
	{
		for (int col = 0; col <= 10; col++)
		{
			int x = mLabelWidth + col * aContentWidth / 10;
			aGraphics.setColor(new Color(0.6f, 0.6f, 0.6f, 0.2f));
			aGraphics.drawLine(x, 0, x, aHeight);

			if (col < 10)
			{
				x += aContentWidth / 10 / 2;
				aGraphics.setColor(new Color(0.8f, 0.8f, 0.8f, 0.2f));
				aGraphics.drawLine(x, 0, x, aHeight);
			}
		}
	}


	private void drawElement(Graphics aGraphics, Element aElement, int aY, int aWidth, long aStartTime, long aEndTime, int aContentWidth)
	{
		if (mSelectedElement == aElement)
		{
			aGraphics.setColor(new Color(0, 116, 232));
			aGraphics.fillRect(0, aY, aWidth, mRowHeight);
		}

		boolean inProgress = aElement.mEndTime == aElement.mStartTime;

		long endTime = inProgress ? System.nanoTime() : aElement.mEndTime;

		int x0 = mLabelWidth + (int)((aElement.mStartTime - aStartTime) * aContentWidth / (aEndTime - aStartTime));
		int x1 = mLabelWidth + (int)((endTime - aStartTime) * aContentWidth / (aEndTime - aStartTime));

		aGraphics.setColor(new Color(aElement.mColor));

		if (inProgress)
		{
			aGraphics.drawRect(x0, aY + (mRowHeight - mItemHeight) / 2, x1 - x0, mItemHeight);
		}
		else
		{
			aGraphics.fillRect(x0, aY + (mRowHeight - mItemHeight) / 2, x1 - x0, mItemHeight);
		}

		for (Element subItem : aElement.mSubItems)
		{
			int x2 = mLabelWidth + (int)((subItem.mStartTime - aStartTime) * aContentWidth / (aEndTime - aStartTime));

			aGraphics.setColor(new Color(subItem.mColor));
			aGraphics.fillRect(x0, aY + (mRowHeight - mItemHeight) / 2, x2 - x0, mItemHeight);

			x0 = x2;
		}

		aGraphics.setColor(mSelectedElement == aElement ? Color.WHITE : Color.BLACK);
		aGraphics.setFont(mTimeFont);
		aGraphics.drawString((aElement.mEndTime - aElement.mStartTime) / 1000000 + "ms", x1 + 5, aY + 15);

		aGraphics.setColor(mSelectedElement == aElement ? Color.WHITE : Color.BLACK);
		aGraphics.setFont(mLabelFont);
		aGraphics.drawString(aElement.mName, 0, aY + 15);
	}


	@Override
	public Dimension preferredSize()
	{
		return new Dimension(mLabelWidth + MINIMUM_WIDTH + mRightMargin, mRowHeight * mGanttChart.mMap.size());
	}
}

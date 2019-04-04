package org.terifan.ganttchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
import static org.terifan.ganttchart.GanttChartPanel.formatTime;
import org.terifan.ui.Utilities;


public class GanntChartDetailPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Element mElement;
	private int mLabelWidth = 100;
	private int mRowHeight = 24;
	private int mItemHeight = 9;
	private int mRightMargin = 50;
	private Font mLabelFont = new Font("segoe ui", Font.PLAIN, 12);
	private Font mTimeFont = new Font("segoe ui", Font.PLAIN, 9);


	public GanntChartDetailPanel()
	{
	}


	public void setElement(Element aElement)
	{
		mElement = aElement;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		Utilities.enableAntialiasing(aGraphics);
		Utilities.enableTextAntialiasing(aGraphics);
		Utilities.enableBilinear(aGraphics);

		int w = Math.max(getWidth(), mLabelWidth + mRightMargin + 50);
		int h = getHeight();

		aGraphics.setColor(Color.WHITE);
		aGraphics.fillRect(0, 0, w, h);

		if (mElement == null)
		{
			return;
		}

		boolean inProgress = mElement.mEndTime == mElement.mStartTime;

		long startTime = mElement.mStartTime;
		long endTime = inProgress ? System.nanoTime() : mElement.mEndTime;

		int wi = w - mLabelWidth - mRightMargin;
		int x0 = 0;
		int y = 0;
		long lastTime = startTime;

		for (int i = 0; i <= mElement.mSubItems.size(); i++)
		{
			int x1;
			long tickTime;

			if (i == mElement.mSubItems.size())
			{
				tickTime = endTime;
				x1 = wi;
			}
			else
			{
				tickTime = mElement.mSubItems.get(i).mStartTime;
				x1 = (int)((tickTime - startTime) * wi / (endTime - startTime));
			}

			aGraphics.setColor(new Color(i == 0 ? mElement.mColor : mElement.mSubItems.get(i - 1).mColor));
			aGraphics.fillRect(mLabelWidth + x0, y + (mRowHeight - mItemHeight) / 2, x1 - x0, mItemHeight);

			aGraphics.setColor(Color.BLACK);
			aGraphics.setFont(mTimeFont);
			aGraphics.drawString(formatTime(tickTime - lastTime), mLabelWidth + x1 + 5, y + 15);

			aGraphics.setColor(Color.BLACK);
			aGraphics.setFont(mLabelFont);
			aGraphics.drawString(i == 0 ? mElement.mName : mElement.mSubItems.get(i - 1).mName, 0, y + 15);

			x0 = x1;
			lastTime = tickTime;
			y += mRowHeight;
		}
	}


	@Override
	public Dimension preferredSize()
	{
		return new Dimension(mLabelWidth + 50 + mRightMargin, mElement == null ? 1 : mRowHeight * mElement.mSubItems.size());
	}
}

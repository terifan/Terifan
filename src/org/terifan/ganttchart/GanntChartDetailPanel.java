package org.terifan.ganttchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;
import static org.terifan.ganttchart.GanttChartPanel.formatTime;
import org.terifan.ui.Utilities;


public class GanntChartDetailPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private GanttChartElement mElement;
	private int mLabelWidth = 100;
	private int mRowHeight = 24;
	private int mBarHeight = 9;
	private int mRightMargin = 50;
	private Font mLabelFont = new Font("segoe ui", Font.PLAIN, 12);
	private Font mTimeFont = new Font("segoe ui", Font.PLAIN, 9);


	public GanntChartDetailPanel()
	{
	}


	public void setElement(GanttChartElement aElement)
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

		ArrayList<GanttChartElement> subElements = mElement.getSubElements();

		long startTime = mElement.getStartTime();
		boolean inProgress = mElement.getEndTime() == startTime;
		long endTime = inProgress ? System.nanoTime() : mElement.getEndTime();

		int wi = w - mLabelWidth - mRightMargin;
		int x0 = 0;
		int y = 0;
		long lastTime = startTime;

		for (int i = 0; i <= subElements.size(); i++)
		{
			int x1;
			long tickTime;

			if (i == subElements.size())
			{
				tickTime = endTime;
				x1 = wi;
			}
			else
			{
				tickTime = subElements.get(i).getStartTime();
				x1 = (int)((tickTime - startTime) * wi / (endTime - startTime));
			}

			aGraphics.setColor(new Color(i == 0 ? mElement.getColor() : subElements.get(i - 1).getColor()));
			aGraphics.fillRect(mLabelWidth + x0, y + (mRowHeight - mBarHeight) / 2, x1 - x0, mBarHeight);

			aGraphics.setColor(Color.BLACK);
			aGraphics.setFont(mTimeFont);
			aGraphics.drawString(formatTime(tickTime - lastTime), mLabelWidth + x1 + 5, y + 15);

			aGraphics.setColor(Color.BLACK);
			aGraphics.setFont(mLabelFont);
			aGraphics.drawString(i == 0 ? mElement.getName() : subElements.get(i - 1).getName(), 0, y + 15);

			x0 = x1;
			lastTime = tickTime;
			y += mRowHeight;
		}
	}


	@Override
	public Dimension preferredSize()
	{
		return new Dimension(mLabelWidth + 50 + mRightMargin, mElement == null ? 1 : mRowHeight * mElement.getSubElements().size());
	}
}

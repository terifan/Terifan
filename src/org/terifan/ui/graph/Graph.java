package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Graph extends JComponent
{
	private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private long[] mValues;
	private Date[] mTimes;
	private long mMaxValue;
	private long mAverageValue;
	private int mBarWidth;
	private int mCounter;


	public Graph()
	{
		mValues = new long[200];
		mTimes = new Date[200];
		mBarWidth = 4;

		setBackground(Color.WHITE);
	}


	public int getBarWidth()
	{
		return mBarWidth;
	}


	public Graph setBarWidth(int aBarWidth)
	{
		mBarWidth = aBarWidth;
		return this;
	}


	public synchronized void add(long aValue)
	{
		if (mValues[mValues.length - 1] == mMaxValue)
		{
			mMaxValue = 0;
			for (int i = 0; i < mValues.length - 1; i++)
			{
				mMaxValue = Math.max(mMaxValue, mValues[i]);
			}
		}

		mAverageValue -= mValues[mValues.length - 1];
		mAverageValue += aValue;

		System.arraycopy(mValues, 0, mValues, 1, mValues.length - 1);
		System.arraycopy(mTimes, 0, mTimes, 1, mTimes.length - 1);
		mValues[0] = aValue;
		mTimes[0] = new Date();

		mCounter++;
		mMaxValue = Math.max(mMaxValue, aValue);

		SwingUtilities.invokeLater(()->repaint());
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int w = getWidth();
		int h = getHeight();
		int right = w - 50;
		int bottom = h - 16;

		aGraphics.setColor(getBackground());
		aGraphics.fillRect(0, 0, w, h);

		double a = mAverageValue / (double)Math.min(mCounter, mValues.length) / mMaxValue;

		aGraphics.setColor(Color.BLACK);
		aGraphics.drawString("" + mMaxValue, right + 5, 13);
		aGraphics.drawString("" + (int)(a*mMaxValue), right + 5, bottom-4);

		int vv = (int)(bottom * a);

		aGraphics.setColor(Color.LIGHT_GRAY);
		aGraphics.drawLine(0, bottom*1/4, right, bottom*1/4);
		aGraphics.drawLine(0, bottom*2/4, right, bottom*2/4);
		aGraphics.drawLine(0, bottom*3/4, right, bottom*3/4);

		aGraphics.setColor(Color.GRAY);
		aGraphics.drawLine(0, bottom, right, bottom);

		aGraphics.setColor(Color.BLUE);
		aGraphics.drawLine(0, bottom-vv, right, bottom-vv);

		for (int i = 0, x = right - mBarWidth; x > -mBarWidth && i < mValues.length; i++)
		{
			int v = (int)(bottom * mValues[i] / (double)mMaxValue);

			aGraphics.setColor(Color.GREEN);
			aGraphics.fillRect(x, bottom - v, mBarWidth, v);

			if (((i-mCounter+1) % 50) == 0 && mTimes[i] != null)
			{
				aGraphics.setColor(Color.BLACK);
				aGraphics.drawString("" + TIME_FORMAT.format(mTimes[i]), x-25, h-3);
			}

			x -= mBarWidth + 1;
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(400, 80);
	}


	public static void main(String ... args)
	{
		try
		{
			Graph graph = new Graph();

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 120);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Random r = new Random();

			for (;;)
			{
				graph.add(r.nextInt(1<<(2+r.nextInt(10))));

				Thread.sleep(1000);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

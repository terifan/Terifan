package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	private int mCounter;
	private String mTitle;
	private long mConstantMaxValue;
	private String mUnit;

	private Color mLineColor = new Color(17, 125, 187);
	private Color mBottomLineColor = new Color(64,64,64);
	private Color mAverageLineColor = new Color(128,160,255);
	private Color mBorderColor = new Color(17, 125, 187);
	private Color mGridColor = new Color(241, 246, 250);
	private Color mFillColor = new Color(17, 125, 187, 32);


	public Graph(String aTitle, String aUnit)
	{
		mTitle = aTitle;
		mUnit = aUnit;

		mValues = new long[100];
		mTimes = new Date[100];

		setBackground(Color.WHITE);
	}


	public Color getBottomLineColor()
	{
		return mBottomLineColor;
	}


	public void setBottomLineColor(Color aBottomLineColor)
	{
		this.mBottomLineColor = aBottomLineColor;
	}


	public Color getAverageLineColor()
	{
		return mAverageLineColor;
	}


	public void setAverageLineColor(Color aAverageLineColor)
	{
		this.mAverageLineColor = aAverageLineColor;
	}


	public Color getLineColor()
	{
		return mLineColor;
	}


	public void setLineColor(Color aLineColor)
	{
		this.mLineColor = aLineColor;
	}


	public long getConstantMaxValue()
	{
		return mConstantMaxValue;
	}


	public Graph setConstantMaxValue(long aConstantMaxValue)
	{
		mConstantMaxValue = aConstantMaxValue;
		mMaxValue = aConstantMaxValue;
		return this;
	}


	public synchronized void add(long aValue)
	{
		if (mConstantMaxValue == 0 && mValues[mValues.length - 1] == mMaxValue)
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

		if (mConstantMaxValue == 0)
		{
			mMaxValue = Math.max(mMaxValue, aValue);
		}

		SwingUtilities.invokeLater(()->repaint());
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int x = 4;
		int y = 28;
		int w = getWidth();
		int h = getHeight();
		int w1 = w - 50 - x;
		int h1 = h - 16 - y;

		double stepSize = w1 / (double)mValues.length;

		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLACK);
		g.drawString(mTitle, 4, 13);
		g.drawString(mMaxValue + " " + mUnit, w1 + 5, 13);

		g.translate(x, y);

		g.setColor(mGridColor);
		for (int i = 0; i < h1; i += h1 / 5)
		{
			g.drawLine(x, i, w1, i);
		}

		int vy = h1 - (int)(h1 * mValues[0] / (mMaxValue == 0 ? 1 : mMaxValue));

		g.setColor(Color.BLACK);
		g.drawString("" + mValues[0], w1 + 5, vy);

		for (int i = 0; i < mValues.length; i++)
		{
			if (((i - mCounter + 1) % 25) == 0 && mTimes[i] != null)
			{
				g.drawString(TIME_FORMAT.format(mTimes[i]), (int)(w1 - i * stepSize)-40, h1 + 13);
			}
		}

		int[] xp = new int[mValues.length + 3];
		int[] yp = new int[mValues.length + 3];

		for (int i = 0; i < mValues.length; i++)
		{
			int v = (int)(h1 * mValues[i] / (double)mMaxValue);

			xp[i] = (int)(w1 - i * stepSize);
			yp[i] = h1 - v;

			g.setColor(mGridColor);
			g.drawLine(xp[i], 0, xp[i], h1);
		}

		g.setColor(mGridColor);
		g.drawLine(x, 0, x, h1);

		xp[mValues.length + 0] = xp[mValues.length - 1];
		yp[mValues.length + 0] = h1;
		xp[mValues.length + 1] = w1;
		yp[mValues.length + 1] = h1;
		xp[mValues.length + 2] = w1;
		yp[mValues.length + 2] = yp[0];

		g.setColor(mFillColor);
		g.fillPolygon(xp, yp, xp.length);
		g.setColor(mLineColor);
		g.drawPolyline(xp, yp, xp.length - 3);

		g.translate(x, -y);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mValues.length * 5, 80);
	}


	public static void main(String ... args)
	{
		try
		{
			Graph graph = new Graph("test", "rpm");

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

				Thread.sleep(100);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

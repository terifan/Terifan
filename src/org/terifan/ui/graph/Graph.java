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
import org.terifan.ui.Anchor;
import org.terifan.ui.TextBox;


public class Graph extends JComponent
{
	private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private final long[] mValues;
	private final long[] mTimes;
	private long mConstantMaxValue;
	private long mCumulativeSum;
	private long mMaxValue;
	private int mCounter;
	private String mTitle;
	private String mUnit;

	private Color mLineColor = new Color(17, 125, 187);
	private Color mFillColor = new Color(17, 125, 187, 32);
	private Color mGridColor = new Color(241, 246, 250);
	private Color mAverageLineColor = new Color(50,255,80, 128);
	private int mBottomLegendHeight = 16;
	private int mRightLegendWidth = 40;
	private int mTitleLegendHeight = 24;
	private int mTimeSpacing = 60;
	private int mGridSize = 8;


	public Graph(String aTitle, String aUnit, int aSequenceLength)
	{
		mTitle = aTitle;
		mUnit = aUnit;

		mValues = new long[aSequenceLength];
		mTimes = new long[aSequenceLength];

		setBackground(Color.WHITE);
	}


	public Color getGridColor()
	{
		return mGridColor;
	}


	public void setGridColor(Color aGridColor)
	{
		this.mGridColor = aGridColor;
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

		mCumulativeSum -= mValues[mValues.length - 1];
		mCumulativeSum += aValue;

		System.arraycopy(mValues, 0, mValues, 1, mValues.length - 1);
		System.arraycopy(mTimes, 0, mTimes, 1, mTimes.length - 1);
		mValues[0] = aValue;
		mTimes[0] = System.currentTimeMillis();

		mCounter++;

		if (mConstantMaxValue == 0)
		{
			mMaxValue = Math.max(mMaxValue, aValue);
		}

		SwingUtilities.invokeLater(() -> repaint());
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int x = 4;
		int y = mTitleLegendHeight;
		int w = getWidth();
		int h = getHeight();
		int w1 = w - mRightLegendWidth - x;
		int h1 = h - mBottomLegendHeight - y;

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		TextBox textBox = new TextBox().setForeground(getForeground()).setBounds(x, 0, w1, y);
		textBox.setAnchor(Anchor.WEST).setText(mTitle).render(g);
		textBox.setAnchor(Anchor.EAST).setText(mMaxValue + " " + mUnit).render(g);

		g.translate(x, y);

		drawGrid(g, w1, h1);
		drawValues(g, w1, h1);
		drawGraph(g, w1, h1);

		g.translate(x, -y);
	}


	private void drawValues(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / (double)(mValues.length - 1);

		TextBox textBox = new TextBox().setForeground(getForeground()).setBounds(0, 0, aWidth, mTitleLegendHeight);

		int vy = aHeight - (int)(aHeight * mValues[0] / (mMaxValue == 0 ? 1 : mMaxValue));

		textBox.setBounds(aWidth + 5, vy - 9, mRightLegendWidth, 18).setAnchor(Anchor.WEST).setText("" + mValues[0]).render(g);

		int modulo = (int)Math.max(Math.ceil(mTimeSpacing / (aWidth / (double)mValues.length)), 2);

		for (int i = 0; i < mValues.length; i++)
		{
			if (((i - mCounter + 1) % modulo) == 0 && mTimes[i] != 0)
			{
				textBox.setBounds(aWidth - (int)(i * stepSize) - mTimeSpacing, aHeight, mTimeSpacing, mBottomLegendHeight).setAnchor(Anchor.EAST).setText(TIME_FORMAT.format(new Date(mTimes[i]))).render(g);
			}
		}

		int y = aHeight - (int)(aHeight * mCumulativeSum / Math.min(mCounter, mValues.length) / (double)mMaxValue);
		g.setColor(mAverageLineColor);
		g.drawLine(0, y, aWidth, y);
	}


	private void drawGrid(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / (double)(mValues.length - 1);

		g.setColor(mGridColor);

		for (int i = 0; i <= aHeight / mGridSize; i++)
		{
			int iy = (int)(i * aHeight / (double)(aHeight / mGridSize));
			g.drawLine(0, iy, aWidth, iy);
		}

		int modulo = (int)Math.max(Math.ceil(mGridSize / (aWidth / (double)mValues.length)), 2);

		for (int i = 0; i < mValues.length; i++)
		{
			if (((i - mCounter) % modulo) == 0)
			{
				int ix = (int)(aWidth - i * stepSize);

				g.drawLine(ix, 0, ix, aHeight);
			}
		}
	}


	private void drawGraph(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / (double)(mValues.length - 1);

		int[] xp = new int[mValues.length + 3];
		int[] yp = new int[mValues.length + 3];

		for (int i = 0; i < mValues.length; i++)
		{
			int v = (int)(aHeight * mValues[i] / (double)mMaxValue);
			xp[i] = (int)(aWidth - i * stepSize);
			yp[i] = aHeight - v;
		}

		xp[mValues.length + 0] = xp[mValues.length - 1];
		yp[mValues.length + 0] = aHeight;
		xp[mValues.length + 1] = aWidth;
		yp[mValues.length + 1] = aHeight;
		xp[mValues.length + 2] = aWidth;
		yp[mValues.length + 2] = yp[0];

		g.setColor(mFillColor);
		g.fillPolygon(xp, yp, xp.length);
		g.setColor(mLineColor);
		g.drawPolyline(xp, yp, xp.length - 3);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mValues.length * 5, 80);
	}


	public static void main(String... args)
	{
		try
		{
			Graph graph = new Graph("test", "rpm", 1000);
			graph.setBackground(new Color(240, 240, 240));
			graph.setGridColor(new Color(221, 226, 230));

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 120);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Random r = new Random();

			for (;;)
			{
				graph.add(r.nextInt(1 << (2 + r.nextInt(10))));

				Thread.sleep(100);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

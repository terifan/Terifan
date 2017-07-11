package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.terifan.ui.Anchor;
import org.terifan.ui.TextBox;


public class OverviewGraph extends JComponent
{
	private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private final long[] mValues;
	private final int[] mRange;
	private double mMaxValue;
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
	private boolean mDrawAverage;


	public OverviewGraph(String aTitle, String aUnit, int aSequenceLength)
	{
		mTitle = aTitle;
		mUnit = aUnit;

		mValues = new long[aSequenceLength];
		mRange = new int[aSequenceLength];

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

			Random rnd = new Random();

	public synchronized void add(long aValue)
	{
		if (mCounter == mValues.length)
		{
			int min = 0;
			int cnt = 0;
			for (int i = 0; i < mCounter - 1; i++)
			{
				if (mRange[i] < mRange[min])
				{
					cnt = 1;
					min = i;
				}
				else if (mRange[i] == mRange[min])
					cnt++;
			}
			int cand = rnd.nextInt(cnt);
			for (int i = 0; i < mCounter - 1; i++)
			{
				if (mRange[i] == mRange[min] && --cand == 0)
				{
					min = i;
					break;
				}
			}

			if (rnd.nextBoolean() && min > 0)
			{
				mValues[min - 1] += mValues[min];
				mRange[min - 1] += mRange[min];
				System.arraycopy(mRange, min + 1, mRange, min, mCounter - min - 1);
				System.arraycopy(mValues, min + 1, mValues, min, mCounter - min - 1);
			}
			else
			{
				mValues[min] += mValues[min + 1];
				mRange[min] += mRange[min + 1];
				System.arraycopy(mRange, min + 2, mRange, min + 1, mCounter - min - 2);
				System.arraycopy(mValues, min + 2, mValues, min + 1, mCounter - min - 2);
			}

			mValues[mCounter - 1] = aValue;
			mRange[mCounter - 1] = 1;
		}
		else
		{
			mValues[mCounter] = aValue;
			mRange[mCounter] = 1;
			mCounter++;
		}

		mMaxValue = 0;
		for (int i = 0; i < mCounter-1; i++)
		{
			mMaxValue = Math.max(mMaxValue, mValues[i] / (double)mRange[i]);
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
//		double stepSize = aWidth / (double)(mCounter);
//
//		TextBox textBox = new TextBox().setForeground(getForeground()).setBounds(0, 0, aWidth, mTitleLegendHeight);
//
//		for (int i = 0; i < mCounter; i++)
//		{
//			textBox.setBounds((int)(i * stepSize), aHeight, mTimeSpacing, mBottomLegendHeight).setAnchor(Anchor.EAST).setText(mValues[i]+"/"+mRange[i]).render(g);
//		}

//		double stepSize = aWidth / (double)(mValues.length - 1);
//
//		TextBox textBox = new TextBox().setForeground(getForeground()).setBounds(0, 0, aWidth, mTitleLegendHeight);
//
//		int vy = aHeight - (int)(aHeight * mValues[0] / (mMaxValue == 0 ? 1 : mMaxValue));
//
//		textBox.setBounds(aWidth + 5, vy - 9, mRightLegendWidth, 18).setAnchor(Anchor.WEST).setText("" + mValues[0]).render(g);
//
//		int modulo = (int)Math.max(Math.ceil(mTimeSpacing / (aWidth / (double)mValues.length)), 2);
//
//		for (int i = 0; i < mValues.length; i++)
//		{
//			if (((i - mCounter + 1) % modulo) == 0 && mRange[i] != 0)
//			{
//				textBox.setBounds(aWidth - (int)(i * stepSize) - mTimeSpacing, aHeight, mTimeSpacing, mBottomLegendHeight).setAnchor(Anchor.EAST).setText(TIME_FORMAT.format(new Date(mRange[i]))).render(g);
//			}
//		}
//
//		if (mDrawAverage)
//		{
//			int y = aHeight - (int)(aHeight * mCumulativeSum / Math.min(mCounter, mValues.length) / (double)mMaxValue);
//			g.setColor(mAverageLineColor);
//			g.drawLine(0, y, aWidth, y);
//		}
	}


	private void drawGrid(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / 40;

		g.setColor(mGridColor);

		for (int i = 0; i <= aHeight / mGridSize; i++)
		{
			int iy = (int)(i * aHeight / (double)(aHeight / mGridSize));
			g.drawLine(0, iy, aWidth, iy);
		}

		for (int x = 0; x < 40; x++)
		{
			int ix = (int)(x * stepSize);
			g.drawLine(ix, 0, ix, aHeight);
		}
	}


	private void drawGraph(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / (double)(mCounter - 1);

		int[] xp = new int[mCounter + 3];
		int[] yp = new int[mCounter + 3];

		for (int i = 0; i < mCounter; i++)
		{
			int v = (int)(aHeight * mValues[i] / mMaxValue / mRange[i]);
			xp[i] = (int)(i * stepSize);
			yp[i] = aHeight - v;
		}

		xp[mCounter + 0] = xp[mCounter - 1];
		yp[mCounter + 0] = aHeight;
		xp[mCounter + 1] = 0;
		yp[mCounter + 1] = aHeight;
		xp[mCounter + 2] = 0;
		yp[mCounter + 2] = yp[0];

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


	public boolean isDrawAverage()
	{
		return mDrawAverage;
	}


	public void setDrawAverage(boolean aDrawAverage)
	{
		mDrawAverage = aDrawAverage;
	}


	public static void main(String... args)
	{
		try
		{
			OverviewGraph graph = new OverviewGraph("test", "rpm", 1000);
			graph.setBackground(new Color(240, 240, 240));
			graph.setGridColor(new Color(221, 226, 230));

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 800);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Random r = new Random();

			for (int i = 0;; i++)
			{
//				graph.add(r.nextInt(1 << (2 + r.nextInt(10))));
				graph.add(Math.abs((int)(100*Math.sin(Math.PI*i/50))));

				Thread.sleep(10);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

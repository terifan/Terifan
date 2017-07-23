package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.terifan.ui.Anchor;
import org.terifan.ui.TextBox;


public class ProgressiveGraph extends JComponent
{
	private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private final long[] mWindow;
	private final long[] mValues;
	private final long[] mTimes;
	private long mConstantMaxValue;
	private long mCumulativeSum;
	private long mMaxValue;
	private int mCounter;
	private String mTitle;
	private String mUnit;
	private int mCurrentCount;
	private long mCurrentSum;
	private Timer mTimer;

	private Font mFont;
	private Color mLineColor;
	private Color mFillColor;
	private Color mGridColor;
	private Color mBorderColor;
	private Color mAverageLineColor;
	private int mBottomLegendHeight;
	private int mRightLegendWidth;
	private int mTitleLegendHeight;
	private int mTimeSpacing;
	private int mGridSize;
	private boolean mDrawAverage;


	public ProgressiveGraph(String aTitle, String aUnit, int aSequenceLength)
	{
		mTitle = aTitle;
		mUnit = aUnit;

		mWindow = new long[5];
		mValues = new long[aSequenceLength];
		mTimes = new long[aSequenceLength];

		mGridSize = 8;
		mTimeSpacing = 60;
		mTitleLegendHeight = 20;
		mRightLegendWidth = 40;
		mBottomLegendHeight = 20;
		mAverageLineColor = new Color(50,255,80, 128);
		mBorderColor = new Color(241, 246, 250);
		mGridColor = new Color(241, 246, 250);
		mFillColor = new Color(17, 125, 187, 32);
		mLineColor = new Color(17, 125, 187);
		mFont = new Font("segoe ui", Font.PLAIN, 12);

		setGridColor(new Color(221, 226, 230));
		setBackground(new Color(240, 240, 240));

		mMaxValue = 1;
	}


	private TimerTask mTask = new TimerTask()
	{
		@Override
		public void run()
		{
			synchronized (ProgressiveGraph.class)
			{
				mCounter++;

				System.arraycopy(mWindow, 0, mWindow, 1, mWindow.length - 1);
				mWindow[0] = mCurrentCount == 0 ? 0 : mCurrentSum / mCurrentCount;

				int t = 0;
				int n = Math.min(mWindow.length, mCounter);
				for (int i = 0; i < n; i++)
				{
					t += mWindow[i];
				}

				System.arraycopy(mValues, 0, mValues, 1, mValues.length - 1);
				System.arraycopy(mTimes, 0, mTimes, 1, mTimes.length - 1);
				mValues[0] = t / n;
				mTimes[0] = System.currentTimeMillis();

				mCurrentCount = 0;
				mCurrentSum = 0;

				mMaxValue = 1;
				for (int i = 0; i < mValues.length; i++)
				{
					mMaxValue = Math.max(mMaxValue, mValues[i]);
				}

				SwingUtilities.invokeLater(() -> repaint());
			}
		}
	};


	public synchronized void start()
	{
		if (mTimer == null)
		{
			mTimer = new Timer(true);
			mTimer.schedule(mTask, 1000, 1000);
		}
	}
	
	
	public synchronized void stop()
	{
		mTimer.cancel();
		mTimer = null;
	}
	

	public void add(long aValue)
	{
		synchronized (ProgressiveGraph.class)
		{
			mCurrentSum += aValue;
			mCurrentCount++;
		}
	}

	
	public void scaleUI(double aScale)
	{
		mGridSize *= aScale;
		mTimeSpacing *= aScale;
		mTitleLegendHeight *= aScale;
		mRightLegendWidth *= aScale;
		mBottomLegendHeight *= aScale;
		mFont = mFont.deriveFont((float)(mFont.getSize() * aScale));
	}


	public Color getBorderColor()
	{
		return mBorderColor;
	}


	public void setBorderColor(Color aBorderColor)
	{
		mBorderColor = aBorderColor;
	}


	public long getCumulativeSum()
	{
		return mCumulativeSum;
	}


	public void setCumulativeSum(long aCumulativeSum)
	{
		mCumulativeSum = aCumulativeSum;
	}


	public int getCounter()
	{
		return mCounter;
	}


	public void setCounter(int aCounter)
	{
		mCounter = aCounter;
	}


	public String getTitle()
	{
		return mTitle;
	}


	public void setTitle(String aTitle)
	{
		mTitle = aTitle;
	}


	public String getUnit()
	{
		return mUnit;
	}


	public void setUnit(String aUnit)
	{
		mUnit = aUnit;
	}


	public Color getAverageLineColor()
	{
		return mAverageLineColor;
	}


	public void setAverageLineColor(Color aAverageLineColor)
	{
		mAverageLineColor = aAverageLineColor;
	}


	public int getBottomLegendHeight()
	{
		return mBottomLegendHeight;
	}


	public void setBottomLegendHeight(int aBottomLegendHeight)
	{
		mBottomLegendHeight = aBottomLegendHeight;
	}


	public Color getLineColor()
	{
		return mLineColor;
	}


	public void setLineColor(Color aLineColor)
	{
		mLineColor = aLineColor;
	}


	public Color getFillColor()
	{
		return mFillColor;
	}


	public void setFillColor(Color aFillColor)
	{
		mFillColor = aFillColor;
	}


	public int getRightLegendWidth()
	{
		return mRightLegendWidth;
	}


	public void setRightLegendWidth(int aRightLegendWidth)
	{
		mRightLegendWidth = aRightLegendWidth;
	}


	public int getTitleLegendHeight()
	{
		return mTitleLegendHeight;
	}


	public void setTitleLegendHeight(int aTitleLegendHeight)
	{
		mTitleLegendHeight = aTitleLegendHeight;
	}


	public int getTimeSpacing()
	{
		return mTimeSpacing;
	}


	public void setTimeSpacing(int aTimeSpacing)
	{
		mTimeSpacing = aTimeSpacing;
	}


	public int getGridSize()
	{
		return mGridSize;
	}


	public void setGridSize(int aGridSize)
	{
		mGridSize = aGridSize;
	}


	public Color getGridColor()
	{
		return mGridColor;
	}


	public void setGridColor(Color aGridColor)
	{
		mGridColor = aGridColor;
	}


	public long getConstantMaxValue()
	{
		return mConstantMaxValue;
	}


	public ProgressiveGraph setConstantMaxValue(long aConstantMaxValue)
	{
		mConstantMaxValue = aConstantMaxValue;
		return this;
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

		TextBox textBox = new TextBox().setFont(mFont).setForeground(getForeground()).setBounds(x, 0, w1, y);
		textBox.setAnchor(Anchor.WEST).setText(mTitle).render(g);
		textBox.setAnchor(Anchor.EAST).setText(mMaxValue + " " + mUnit).render(g);

		g.translate(x, y);

		drawGrid(g, w1, h1);
		drawValues(g, w1, h1);
		drawGraph(g, w1, h1);

		g.setColor(mBorderColor);
		g.drawRect(0, 0, w1, h1);
		
		g.translate(x, -y);
	}


	private void drawValues(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / (double)(mValues.length - 1);

		int vy = aHeight - (int)(aHeight * mValues[0] / (mMaxValue == 0 ? 1 : mMaxValue));

		TextBox textBox = new TextBox()
			.setFont(mFont)
			.setForeground(getForeground())
			.setBounds(aWidth + 5, vy - 9, mRightLegendWidth, 18)
			.setAnchor(Anchor.WEST)
			.setText("" + mValues[0]).render(g);

		int modulo = (int)Math.max(Math.ceil(mTimeSpacing / (aWidth / (double)mValues.length)), 2);

		for (int i = 0; i < mValues.length; i++)
		{
			if (((i - mCounter + 1) % modulo) == 0 && mTimes[i] != 0)
			{
				textBox.setBounds(aWidth - (int)(i * stepSize) - mTimeSpacing, aHeight, mTimeSpacing, mBottomLegendHeight).setAnchor(Anchor.EAST).setText(TIME_FORMAT.format(new Date(mTimes[i]))).render(g);
			}
		}

//		if (mDrawAverage)
//		{
//			int y = aHeight - (int)(aHeight * mCumulativeSum / Math.min(mCounter, mValues.length));
//			g.setColor(mAverageLineColor);
//			g.drawLine(0, y, aWidth, y);
//		}
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
			int v = (int)(aHeight * mValues[i] / mMaxValue);
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
			ProgressiveGraph graph = new ProgressiveGraph("test", "rpm", 100);
			graph.setBackground(Color.BLACK);
			graph.setGridColor(new Color(32,32,32));
			graph.setBorderColor(new Color(64,64,64));
			graph.setFillColor(new Color(17, 187, 55, 200));
			graph.setLineColor(new Color(17, 187, 55));
			graph.scaleUI(2);
			graph.start();

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Random r = new Random();

			for (;;)
			{
				graph.add(r.nextInt(1 << (2 + r.nextInt(10))));

				Thread.sleep(10);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class SpeedGraph extends JComponent
{
	private long mTotalWork;
	private long mAccumWork;
	private int mResolution;
	private long mSumWork;
	private long mSumDuration;
	private int mOffset;
	private double mMax;
	private boolean mFinished;
	private double[] mSpeed;

	private Color mLineColor = new Color(17, 125, 187);
	private Color mFillColor = new Color(17, 125, 187, 32);
	private Color mGridColor = new Color(241, 246, 250);


	public SpeedGraph(long aTotalWork)
	{
		mResolution = 1 + 1000;

		mTotalWork = aTotalWork;

		mSpeed = new double[mResolution];
		Arrays.fill(mSpeed, -1.0);

		setBackground(Color.WHITE);
	}


	public synchronized void addWork(long aDuration, long aWork)
	{
		int x = (int)(mResolution * mAccumWork / mTotalWork);

		if (x != mOffset)
		{
			saveValue();
		}

		mSumWork += aWork;
		mSumDuration += aDuration;
		mOffset = x;

		mAccumWork += aWork;
	}


	public synchronized void finish()
	{
		mFinished = true;
		mOffset = mResolution - 1;

		saveValue();
	}


	private void saveValue()
	{
		if (mSumDuration > 0)
		{
			double speed = mSumWork / (double)mSumDuration;

			System.out.println(mOffset + " " + mSumWork + " " + mSumDuration + " " + speed * 1000000000 / 1024 / 1024);

			mMax = Math.max(mMax, speed);
			mSpeed[mOffset] = speed;
			mSumWork = 0;
			mSumDuration = 0;

			repaint();
		}
	}


	public synchronized long remainingWork()
	{
		return mTotalWork - mAccumWork;
	}


	public Color getGridColor()
	{
		return mGridColor;
	}


	public void setGridColor(Color aGridColor)
	{
		this.mGridColor = aGridColor;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int x = 0;
		int y = 0;
		int w = getWidth();
		int h = getHeight();

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		drawGrid(g, w, h);
		drawGraph(g, w, h);
	}


	private void drawGrid(Graphics2D g, int aWidth, int aHeight)
	{
		g.setColor(mGridColor);

		for (int y = 0; y < 10; y++)
		{
			int iy = (int)(y * aHeight / 10);
			g.drawLine(0, iy, aWidth, iy);
		}

		for (int x = 0; x < 10; x++)
		{
			int ix = (int)(x * aWidth / 10);
			g.drawLine(ix, 0, ix, aHeight);
		}
	}


	private synchronized void drawGraph(Graphics2D g, int aWidth, int aHeight)
	{
		int w = mResolution;

		int[] xp = new int[mOffset + 4];
		int[] yp = new int[mOffset + 4];
		int[] tmp = new int[mOffset + 4];

		double lastSpeed = 0;
		int i = 0;
		for (; i < (mFinished ? mOffset + 1 : mOffset); i++)
		{
			xp[i] = i * aWidth / (w - 1);

			double s = mSpeed[i];
			if (s < 0)
			{
				s = lastSpeed;
				System.out.println("#");
			}

			tmp[i] = aHeight - (int)(aHeight * s / mMax);
			lastSpeed = s;
		}

		int f = 100;
		for (int j = 0; j < i; j++)
		{
			int s = 0;
			for (int k = -f; k < 0; k++)
			{
				s += tmp[Math.min(Math.max(j + k, 0), i)];
			}
			yp[j] = s / f;
		}

		if (i > 0)
		{
			xp[i + 0] = xp[i - 1];
			yp[i + 0] = aHeight;
			xp[i + 1] = 0;
			yp[i + 1] = aHeight;
			xp[i + 2] = 0;
			yp[i + 2] = yp[0];

			g.setColor(mFillColor);
			g.fillPolygon(xp, yp, i + 3);

			g.setColor(mLineColor);
			g.drawPolyline(xp, yp, i);
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(1000, 80);
	}


	public static void main(String... args)
	{
		try
		{
			File[] files = new File("D:\\tmp\\in").listFiles(e -> e.isFile());

			long totalWork = 0;
			for (File file : files)
			{
				totalWork += file.length();
			}

			SpeedGraph graph = new SpeedGraph(totalWork);
			graph.setBackground(new Color(240, 240, 240));
			graph.setGridColor(new Color(221, 226, 230));

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 800);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			byte[] buffer = new byte[16384];

			for (File file : files)
			{
				try (FileInputStream in = new FileInputStream(file); FileOutputStream out = new FileOutputStream(new File("d:\\tmp\\out", file.getName())))
				{
					for (;;)
					{
						long duration = System.nanoTime();
						int len = in.read(buffer);

						if (len == -1)
						{
							break;
						}

						out.write(buffer, 0, len);

						duration = System.nanoTime() - duration;

						graph.addWork(duration, len);
					}
				}
			}

			graph.finish();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

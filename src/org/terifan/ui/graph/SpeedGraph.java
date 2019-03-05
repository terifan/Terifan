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
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class SpeedGraph extends JComponent
{
	private long mTotalWork;
	private long mSumWork;
	private int mResolution;
	private long mAccumWork;
	private long mAccumDuration;
	private int mOffset;
	private double mMax;
	private double[] mSpeed;

	private Color mLineColor = new Color(17, 125, 187);
	private Color mFillColor = new Color(17, 125, 187, 32);
	private Color mGridColor = new Color(241, 246, 250);


	public SpeedGraph(long aTotalWork)
	{
		restart(aTotalWork);

		super.setBackground(Color.WHITE);
	}

	
	public void restart(long aTotalWork)
	{
		mResolution = 1 + 200;
		mTotalWork = 0;
		mSumWork = 0;
		mAccumWork = 0;
		mAccumDuration = 0;
		mOffset = 0;
		mMax = 0;

		mTotalWork = aTotalWork;

		mSpeed = new double[mResolution];
		Arrays.fill(mSpeed, -1.0);
	}
	

	public synchronized void addWork(long aDuration, long aWork)
	{
		int x0 = (int)(mResolution * mSumWork / mTotalWork);
		int x1 = (int)(mResolution * (mSumWork + aWork) / mTotalWork);

//		if (x0 != x1)
//		{
//			long bs = mTotalWork / mResolution;
//
//			if (bs > 0)
//			{
//				long rw = (mSumWork + aWork) % bs;
//
//				if (rw > 0 && rw < aWork)
//				{
//					long rd = aDuration * rw / aWork;
//
//					addWork(aDuration - rd, aWork - rw);
//					addWork(rd, rw);
//					return;
//				}
//			}
//		}

		if (x0 != mOffset)
		{
			saveValue();
		}

		mAccumWork += aWork;
		mAccumDuration += aDuration;
		mOffset = x0;

		mSumWork += aWork;
	}


	public synchronized void finish()
	{
		mOffset = mResolution - 1;

		saveValue();

		mOffset = mResolution;
	}


	private void saveValue()
	{
		if (mAccumDuration > 0)
		{
			double speed = mAccumWork / (double)mAccumDuration;

			mMax = Math.max(mMax, speed);
			mSpeed[mOffset] = speed;
			mAccumWork = 0;
			mAccumDuration = 0;

			repaint();
		}
	}


	public synchronized long remainingWork()
	{
		return mTotalWork - mSumWork;
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
		aWidth--;
		aHeight--;

		for (int y = 0; y <= 10; y++)
		{
			int iy = (int)(y * aHeight / 10);
			g.drawLine(0, iy, aWidth, iy);
		}

		for (int x = 0; x <= 10; x++)
		{
			int ix = (int)(x * aWidth / 10);
			g.drawLine(ix, 0, ix, aHeight);
		}
	}


	private synchronized void drawGraph(Graphics2D g, int aWidth, int aHeight)
	{
		if (mOffset <= 0)
		{
			return;
		}

		int[] xp = new int[mOffset + 3];
		int[] yp = new int[mOffset + 3];
		double[] tmp = new double[mOffset];
		
		int limit = mOffset;

		double last = 0;

		for (int i = 0; i < limit; i++)
		{
			xp[i] = i * aWidth / (mResolution - 1);
		}

		for (int i = 0; i < limit; i++)
		{
			double s = mSpeed[i];

			if (s < 0)
			{
				s = last;
//				s = 0;
			}

			tmp[i] = s;
			last = s;
		}

		double max = 0;
		for (int j = 0; j < limit; j++)
		{
			max = Math.max(max, tmp[j]);
		}

		for (int j = 0; j < limit; j++)
		{
			yp[j] = aHeight - (int)(tmp[j] * aHeight / max);
		}

		xp[limit + 0] = xp[limit - 1];
		yp[limit + 0] = aHeight;
		xp[limit + 1] = 0;
		yp[limit + 1] = aHeight;
		xp[limit + 2] = 0;
		yp[limit + 2] = yp[0];

		g.setColor(mFillColor);
		g.fillPolygon(xp, yp, limit + 3);

		g.setColor(mLineColor);
		g.drawPolyline(xp, yp, limit);
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
//			File[] files = new File("D:\\tmp\\in").listFiles(e -> e.isFile());
//
//			long totalWork = 0;
//			for (File file : files)
//			{
//				totalWork += file.length();
//			}
			
			long totalWork = 10000000;

			SpeedGraph graph = new SpeedGraph(totalWork);
			graph.setBackground(new Color(240, 240, 240));
			graph.setGridColor(new Color(221, 226, 230));

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 800);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Random rnd = new Random(0);
			while (graph.remainingWork() > 0)
			{
//				graph.addWork((1<<rnd.nextInt(25))+rnd.nextInt(1000), 100);
				graph.addWork(1000, 100000);
			}
			
//			byte[] buffer = new byte[16384];
//
//			for (File file : files)
//			{
//				try (FileInputStream in = new FileInputStream(file); FileOutputStream out = new FileOutputStream(new File("d:\\tmp\\out", file.getName())))
//				{
//					for (;;)
//					{
//						long duration = System.nanoTime();
//						int len = in.read(buffer);
//
//						if (len == -1)
//						{
//							break;
//						}
//
////						out.write(buffer, 0, len);
//
//						duration = System.nanoTime() - duration;
//
//						graph.addWork(duration, len);
//					}
//				}
//			}

			graph.finish();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class ProgressGraph extends JComponent
{
	private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private long mAccumWork;
	private long mTotalWork;
	private long mRemaining;
	private long[] mTimeForUnit;

	private Color mLineColor = new Color(17, 125, 187);
	private Color mFillColor = new Color(17, 125, 187, 32);
	private Color mGridColor = new Color(241, 246, 250);
	private int mGridSize = 8;

	private static final int B = 100;

	public ProgressGraph(long aTotalWork)
	{
		mTotalWork = aTotalWork;

		mTimeForUnit = new long[B];
		mRemaining = mTotalWork;

		setBackground(Color.WHITE);
	}


	public synchronized void addWork(long aDuration, long aWork)
	{
		int bucket = (int)(mAccumWork * B / mTotalWork);

		mTimeForUnit[bucket] += aDuration;

		mAccumWork += aWork;
		mRemaining -= aWork;
	}

	
	public synchronized long remainingWork()
	{
		return mRemaining;
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
		int x = 4;
		int y = 4;
		int w = getWidth();
		int h = getHeight();
		int w1 = w - x;
		int h1 = h - y;

		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		drawGrid(g, w1, h1);
		drawGraph(g, w1, h1);
	}


	private void drawGrid(Graphics2D g, int aWidth, int aHeight)
	{
		double stepSize = aWidth / 10;

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
		int W = B;

		int[] xp = new int[W + 3];
		int[] yp = new int[W + 3];

		long slowestUnit = 1;

		for (int i = 0; i < W; i++)
		{
			xp[i] = i * aWidth / W;

			if (mTimeForUnit[i] > slowestUnit)
			{
				slowestUnit = mTimeForUnit[i];
			}
		}

		double scale = aHeight / (double)slowestUnit;

		for (int i = 0; i < W; i++)
		{
			long speed = mAccumWork mTimeForUnit[i] * scale;

			System.out.println(mTimeForUnit[i]+" "+speed);

			yp[i] = (int)speed;
			
		}

		for (int i = 0; i < W; i++)
		{
			yp[i] = aHeight - yp[i];
		}

		xp[W + 0] = xp[W - 1];
		yp[W + 0] = aHeight;
		xp[W + 1] = 0;
		yp[W + 1] = aHeight;
		xp[W + 2] = 0;
		yp[W + 2] = yp[0];

		g.setColor(mFillColor);
		g.fillPolygon(xp, yp, xp.length);
		g.setColor(mLineColor);
		g.drawPolyline(xp, yp, xp.length - 3);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(B, 80);
	}


	public static void main(String... args)
	{
		try
		{
			File[] files = new File("D:\\tmp\\in").listFiles(e->e.isFile());

			long totalWork = 0;
			for (File file : files)
			{
				totalWork += file.length();
			}

			ProgressGraph graph = new ProgressGraph(totalWork);
			graph.setBackground(new Color(240, 240, 240));
			graph.setGridColor(new Color(221, 226, 230));

			JFrame frame = new JFrame();
			frame.add(graph);
			frame.setSize(1200, 800);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			byte[] buffer = new byte[4096];

			for (File file : files)
			{
				try (FileInputStream in = new FileInputStream(file); FileOutputStream out = new FileOutputStream(new File("d:\\tmp\\out", file.getName())))
				{
					for (;;)
					{
						long duration = System.currentTimeMillis();
						int len = in.read(buffer);
						
						if (len == -1)
						{
							break;
						}
						
						out.write(buffer, 0, len);
						
						duration = System.currentTimeMillis() - duration;

						graph.addWork(duration, len);
						graph.repaint();
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

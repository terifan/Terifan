package org.terifan.ui.progresspane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ProgressPane extends JPanel
{
	private ArrayList<Work> mWork;
	
	
	public ProgressPane()
	{
		mWork = new ArrayList<>();
	}
	
	
	public void add(Work aWork)
	{
		mWork.add(aWork);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(100,1000);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		aGraphics.setColor(Color.WHITE);
		aGraphics.fillRect(0, 0, getWidth(), getHeight());
		
		paintImpl(aGraphics, 0, 0, mWork);
	}
	
	
	
	private int paintImpl(Graphics aGraphics, int x, int y, ArrayList<Work> aWork)
	{
		int s = 30;
		int w = getWidth() - x * s;

		for (Work work : aWork)
		{
			if (work.mHeight > 0)
			{
				int p = (int)(work.mCurrent * w / (double)work.mSize);

				aGraphics.setColor(Color.RED);
				aGraphics.fillRect(x * s, y, p, work.mHeight);
				aGraphics.setColor(Color.BLUE);
				aGraphics.fillRect(x * s + p, y, w-p, work.mHeight);

				y += work.mHeight + 2;
			}

			if (work.mChildren != null && !work.mChildren.isEmpty())
			{
				y = paintImpl(aGraphics, x + 1, y, work.mChildren);
			}
		}

		return y;
	}


	public static class Work
	{
		Work mParent;
		ArrayList<Work> mChildren;
		int mSize;
		int mCurrent;
		int mHeight;


		public Work(int aCurrent, int aSize)
		{
			mSize = aSize;
			mCurrent = aCurrent;
			mHeight = 20;
		}
	
	
		public void add(Work aWork)
		{
			if (mChildren == null)
			{
				mChildren = new ArrayList<>();
			}

			aWork.mParent = this;
			mChildren.add(aWork);
		}
	}


	public static void main(String... args)
	{
		try
		{
			ProgressPane pane = new ProgressPane();

			JFrame frame = new JFrame();
			frame.add(pane, BorderLayout.CENTER);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			ArrayList<Work> allWork = new ArrayList<>();

			Random r = new Random();

			for (;;)
			{
				Work work = new Work(0, 10);

				if (r.nextDouble() < 0.2 || allWork.isEmpty())
				{
					pane.add(work);
				}
				else
				{
					allWork.get(r.nextInt(allWork.size())).add(work);
				}

				allWork.add(work);

				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							int s = new Random().nextInt(1000);
							while (work.mCurrent < work.mSize)
							{
								work.mCurrent++;
								sleep(s);
							}
							allWork.remove(work);
							if (work.mChildren != null)
							{
								while (!work.mChildren.isEmpty())
								{
									sleep(500);
								}
							}
							while (work.mHeight > 0)
							{
								work.mHeight--;
								sleep(100);
							}
							if (work.mParent != null)
							{
								work.mParent.mChildren.remove(work);
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}					
				}.start();

				pane.repaint();

				Thread.sleep(500);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

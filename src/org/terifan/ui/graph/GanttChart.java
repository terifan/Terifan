package org.terifan.ui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class GanttChart implements AutoCloseable
{
	private final TreeMap<Long, Element> mMap;
	private final ArrayList<Long> mStack;


	public GanttChart()
	{
		mMap = new TreeMap<>();
		mStack = new ArrayList<>();
	}


	public void tick(String aName)
	{
		mMap.get(mStack.get(mStack.size() - 1)).mSubItems.add(new Element(System.nanoTime(), aName));
	}


	public GanttChart start(String aName)
	{
		Long key = System.nanoTime();
		mStack.add(key);
		mMap.put(key, new Element(key, aName));
		return this;
	}


	@Override
	public void close() throws Exception
	{
		Long key = mStack.remove(mStack.size() - 1);
		mMap.get(key).mEndTime = System.nanoTime();
	}


	private static class Element
	{
		ArrayList<Element> mSubItems = new ArrayList<>();
		long mStartTime;
		long mEndTime;
		String mName;

		public Element(long aStartTime, String aName)
		{
			mStartTime = aStartTime;
			mEndTime = aStartTime;
			mName = aName;
		}
	}


	public JPanel create()
	{
		JPanel panel = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics aGraphics)
			{
				long start = mMap.firstEntry().getValue().mStartTime;
				long end = mMap.firstEntry().getValue().mEndTime;

				int y = 0;
				int w = getWidth();

				aGraphics.setColor(Color.WHITE);
				aGraphics.fillRect(0, 0, w, getHeight());

				for (Element item : mMap.values())
				{
					y += 3;

					int x0 = 200 + (int)((item.mStartTime - start) * (w - 200) / (end - start));
					int x1 = 200 + (int)((item.mEndTime - start) * (w - 200) / (end - start));

					aGraphics.setColor(Color.RED);
					aGraphics.fillRect(x0, y, x1 - x0, 20);

					for (Element subItem : item.mSubItems)
					{
						int x2 = 200 + (int)((subItem.mStartTime - start) * (w - 200) / (end - start));

						aGraphics.setColor(new Color(new Random(x2).nextInt(0xffffff)));
						aGraphics.fillRect(x2, y, x1 - x2, 20);
					}

					aGraphics.setColor(Color.BLACK);
					aGraphics.drawString(item.mName, 0, y + 15);

					aGraphics.setColor(Color.LIGHT_GRAY);
					aGraphics.drawLine(0, y + 23, w, y + 23);

					y += 23;
				}
			}


			@Override
			public Dimension preferredSize()
			{
				return new Dimension(300, 26 * mMap.size());
			}
		};

		return panel;
	}


	public static void main(String... args)
	{
		try
		{
			GanttChart m = new GanttChart();

			try (GanttChart m0 = m.start("adasdasd"))
			{
				Thread.sleep(100);
				try (GanttChart m1 = m.start("bsfsf"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = m.start("csegssg"))
					{
						Thread.sleep(100);
						m2.tick("c2");
						Thread.sleep(100);
						m2.tick("c3");
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = m.start("dddgrt"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = m.start("efhhryh"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
				}
				Thread.sleep(50);
				try (GanttChart m1 = m.start("fhdfthh"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = m.start("gsfsgs"))
					{
						Thread.sleep(100);
						m2.tick("c2");
						Thread.sleep(100);
						m2.tick("c3");
						Thread.sleep(100);
					}
					Thread.sleep(50);
				}
				Thread.sleep(50);
			}

			JFrame frame = new JFrame();
			frame.add(new JScrollPane(m.create()));
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

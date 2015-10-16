package org.terifan.ui.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.terifan.util.Calendar;
import org.terifan.util.log.Log;


public class Chart
{
	public static void main(String ... args)
	{
		try
		{
			int steps = 24*60/4;
			int maxValue = 0;
			TreeMap<String,long[]> table = new TreeMap<>();

			try (LineNumberReader in = new LineNumberReader(new FileReader("d:/ex151015.log")))
			{
				int scale = 24 * 60 * 60 * 1000;
				long startTime = Calendar.parse("2015-10-15").get();

				for (String s; (s = in.readLine()) != null;)
				{
					if (!s.startsWith("#"))
					{
						int time = (int)((Calendar.parse(s.substring(0, 20)).get() - startTime) * (steps) / scale);
						int d = Integer.parseInt(s.substring(s.lastIndexOf(' ') + 1));

						s = s.substring(s.indexOf(" ") + 1); // date
						s = s.substring(s.indexOf(" ") + 1); // time
						s = s.substring(s.indexOf(" ") + 1); // thread
						s = s.substring(s.indexOf(" ") + 1); // ip
						s = s.substring(s.indexOf(" ") + 1); // method
						String url = s.substring(0, s.indexOf(" "));
						s = s.substring(s.indexOf(" ") + 1); // url
						s = s.substring(s.indexOf(" ") + 1); // -
						s = s.substring(s.indexOf(" ") + 1); // port
						String user = s.substring(0, s.indexOf(" ")); // user

						String key = url;

						long[] values = table.get(key);
						if (values == null)
						{
							values = new long[steps];
							table.put(key, values);
						}
						values[time] += d;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace(Log.out);
			}

			String[] series = table.keySet().toArray(new String[table.size()]);
			for (int i = 0; i < steps; i++)
			{
				int s = 0;
				for (int k = 0; k < series.length; k++)
				{
					s += table.get(series[k])[i];
				}
				maxValue = Math.max(maxValue, s);
			}

			int _maxValue = maxValue;

			JPanel panel = new JPanel()
			{
				@Override
				protected void paintComponent(Graphics aGraphics)
				{
					Graphics2D g = (Graphics2D)aGraphics;
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());

					int h = getHeight();
					int w = getWidth();

					for (int k = series.length; --k >= 0;)
					{
						Polygon p = new Polygon();
						for (int i = 0; i < steps; i++)
						{
							int s = 0;
							for (int j = 0; j <= k; j++)
							{
//								if (show[j])
								{
									s += table.get(series[j])[i];
								}
							}
							if(s>_maxValue)Log.out.println(_maxValue+" "+s);
							p.addPoint(i*(w-1)/(steps-1), h-s*h/_maxValue);
						}
						p.addPoint(w-1, h);
						p.addPoint(0, h);

						g.setColor(new Color(0xffffff&series[k].hashCode()));
						g.fillPolygon(p);
					}
				}


				@Override
				public Dimension getPreferredSize()
				{
					return new Dimension(1000,700);
				}
			};

			JFrame frame = new JFrame();

//			JPanel controls = new JPanel();
//			controls.add(new JCheckBox(new AbstractAction("red")
//			{
//				@Override
//				public void actionPerformed(ActionEvent aEvent)
//				{
//					show[0] = ((JCheckBox)aEvent.getSource()).isSelected();
//					frame.repaint();
//				}
//			}){{setSelected(true);}});
//			controls.add(new JCheckBox(new AbstractAction("blue")
//			{
//				@Override
//				public void actionPerformed(ActionEvent aEvent)
//				{
//					show[1] = ((JCheckBox)aEvent.getSource()).isSelected();
//					frame.repaint();
//				}
//			}){{setSelected(true);}});
//			controls.add(new JCheckBox(new AbstractAction("green")
//			{
//				@Override
//				public void actionPerformed(ActionEvent aEvent)
//				{
//					show[2] = ((JCheckBox)aEvent.getSource()).isSelected();
//					frame.repaint();
//				}
//			}){{setSelected(true);}});

//			frame.add(controls, BorderLayout.NORTH);
			frame.add(new JScrollPane(panel), BorderLayout.CENTER);
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

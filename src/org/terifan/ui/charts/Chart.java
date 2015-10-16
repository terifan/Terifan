package org.terifan.ui.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Chart
{
	public static void main(String ... args)
	{
		try
		{
			int[][] values = {
				{10,20,50,30,40,80,20,40,60,70},
				{40,30,60,20,10,20,40,30,20,10},
				{20,10,20,10,30,40,90,20,30,30}
			};

			Color[] colors = {
				Color.RED,
				Color.BLUE,
				Color.GREEN
			};

			boolean[] show = {
				true,
				true,
				true
			};

			JPanel panel = new JPanel()
			{
				@Override
				protected void paintComponent(Graphics aGraphics)
				{
					for (int k = 3; --k >= 0;)
					{
						Polygon p = new Polygon();
						for (int i = 0; i < values[0].length; i++)
						{
							int s = 0;
							for (int j = 0; j <= k; j++)
							{
								if (show[j])
								{
									s += values[j][i];
								}
							}
							p.addPoint(i*50, 200-s);
						}
						p.addPoint((values[0].length-1)*50, 200);
						p.addPoint(0, 200);

						aGraphics.setColor(colors[k]);
						aGraphics.fillPolygon(p);
					}
				}
			};

			JFrame frame = new JFrame();

			JPanel controls = new JPanel();
			controls.add(new JCheckBox(new AbstractAction("red")
			{
				@Override
				public void actionPerformed(ActionEvent aEvent)
				{
					show[0] = ((JCheckBox)aEvent.getSource()).isSelected();
					frame.repaint();
				}
			}){{setSelected(true);}});
			controls.add(new JCheckBox(new AbstractAction("blue")
			{
				@Override
				public void actionPerformed(ActionEvent aEvent)
				{
					show[1] = ((JCheckBox)aEvent.getSource()).isSelected();
					frame.repaint();
				}
			}){{setSelected(true);}});
			controls.add(new JCheckBox(new AbstractAction("green")
			{
				@Override
				public void actionPerformed(ActionEvent aEvent)
				{
					show[2] = ((JCheckBox)aEvent.getSource()).isSelected();
					frame.repaint();
				}
			}){{setSelected(true);}});

			frame.add(controls, BorderLayout.NORTH);
			frame.add(panel, BorderLayout.CENTER);
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

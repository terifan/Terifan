package org.terifan.ganttchart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.terifan.ui.Utilities;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			Utilities.setSystemLookAndFeel();

			GanttChart chart = new GanttChart();
			GanntChartDetailPanel detailPanel = new GanntChartDetailPanel();
			GanttChartPanel chartPanel = new GanttChartPanel(chart, detailPanel);

			JScrollPane scrollPane1 = new JScrollPane(chartPanel);
			scrollPane1.setBorder(null);

			JScrollPane scrollPane2 = new JScrollPane(detailPanel);
			scrollPane2.setBorder(null);

			JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane1, scrollPane2);

			JFrame frame = new JFrame();
			frame.add(panel);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			panel.setContinuousLayout(true);
			panel.setDividerLocation(0.8);

			try (GanttChart m0 = chart.start("adasdasd"))
			{
				Thread.sleep(100);
				try (GanttChart m1 = chart.start("bsfsf"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = chart.start("csegssg"))
					{
						Thread.sleep(150);
						m2.tick("c2");
						Thread.sleep(50);
						m2.tick("c3");
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = chart.start("dddgrt"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = chart.start("efhhryh"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
				}
				Thread.sleep(50);
				try (GanttChart m1 = chart.start("fhdfthh"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = chart.start("gsfsgs"))
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
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

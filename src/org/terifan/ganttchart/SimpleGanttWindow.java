package org.terifan.ganttchart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.terifan.ui.Utilities;


public class SimpleGanttWindow
{
	private JFrame mFrame;
	private JSplitPane mPanel;


	public SimpleGanttWindow(GanttChart aChart)
	{
		Utilities.setSystemLookAndFeel();

		GanttChartDetailPanel detailPanel = new GanttChartDetailPanel();
		GanttChartPanel chartPanel = new GanttChartPanel(aChart, detailPanel);

		JScrollPane scrollPane1 = new JScrollPane(chartPanel);
		scrollPane1.setBorder(null);

		JScrollPane scrollPane2 = new JScrollPane(detailPanel);
		scrollPane2.setBorder(null);

		mPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane1, scrollPane2);

		mFrame = new JFrame();
		mFrame.add(mPanel);
		mFrame.setSize(1024, 768);
		mFrame.setLocationRelativeTo(null);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public void show()
	{
		mFrame.setVisible(true);

		mPanel.setContinuousLayout(true);
		mPanel.setDividerLocation(0.8);
	}
}

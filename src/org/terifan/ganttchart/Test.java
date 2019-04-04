package org.terifan.ganttchart;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			GanttChart chart = new GanttChart();

			new SimpleGanttWindow(chart).show();

			try (GanttChart m0 = chart.enter("adasdasd"))
			{
				Thread.sleep(10);

				chart.enter("start");
				chart.tick("tick");
				chart.close();

				Thread.sleep(100);
				try (GanttChart m1 = chart.enter("bsfsf"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = chart.enter("csegssg"))
					{
						Thread.sleep(150);
						m2.tick("c2");
						Thread.sleep(50);
						m2.tick("c3");
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = chart.enter("dddgrt"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
					try (GanttChart m2 = chart.enter("efhhryh"))
					{
						Thread.sleep(100);
					}
					Thread.sleep(50);
				}
				Thread.sleep(50);
				try (GanttChart m1 = chart.enter("fhdfthh"))
				{
					Thread.sleep(100);
					try (GanttChart m2 = chart.enter("gsfsgs"))
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

package org.terifan.ui.progresspane;

import java.awt.BorderLayout;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;


public class ProgressPaneTest
{
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
				Work work = new Work(10);

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
						int s = new Random().nextInt(1000);
						while (!work.isLimitReached())
						{
							work.incrementProgress();
							try{sleep(s);}catch(Exception e){}
						}
						allWork.remove(work);
						work.finish();
					}
				}.start();

				Thread.sleep(500);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

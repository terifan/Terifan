package org.terifan.ui.progresspane;

import java.awt.BorderLayout;
import static java.lang.Thread.sleep;
import javax.swing.JFrame;


public class ProgressPaneTest1
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

			Work work1 = new Work(30, "Process image");
			Work work2 = new Work(10, "Read image");
			Work work3 = new Work(10, "Resize image");
			Work work4 = new Work(10, "Write image");
			pane.add(work1);
			work1.add(work2);
			work1.add(work3);
			work1.add(work4);

			while (!work2.isLimitReached())
			{
				work1.incrementProgress();
				work2.incrementProgress();
				try{sleep(100);}catch(Exception e){}
			}
			work2.finishUnblocked();
			while (!work3.isLimitReached())
			{
				work1.incrementProgress();
				work3.incrementProgress();
				try{sleep(100);}catch(Exception e){}
			}
			work3.finishUnblocked();
			while (!work4.isLimitReached())
			{
				work1.incrementProgress();
				work4.incrementProgress();
				try{sleep(100);}catch(Exception e){}
			}
			work4.finishUnblocked();
			work1.finish();

			pane.repaint();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

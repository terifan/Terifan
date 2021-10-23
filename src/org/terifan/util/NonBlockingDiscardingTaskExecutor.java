package org.terifan.util;

import java.util.function.Consumer;


/**
 * This class will invoke the Consumer, provided in the constructor, with the values submitted, discarding values while the Consumer is
 * running. The Consumer is re-invoked with the most recent value submitted.
 * <p>
 * E.g. perform costly screen update with the most recent pointer position:
 * <pre>
 * NonBlockingDiscardingTaskExecutor<Point> processor = new NonBlockingDiscardingTaskExecutor<>(pt -> drawSlowUpdateOnScreen(pt));
 * MouseMotionAdapter l = new MouseMotionAdapter() {
 *   public void mouseMoved(MouseEvent aEvent) {
 *     processor.submit(aEvent.getPoint());
 *   }
 * };
 * </pre>
 */
public class NonBlockingDiscardingTaskExecutor<T>
{
	private final Consumer<T> mConsumer;
	private Thread mWorker;
	private T mNextValue;


	public NonBlockingDiscardingTaskExecutor(Consumer<T> aConsumer)
	{
		mConsumer = aConsumer;
	}


	public void submit(T aNextValue)
	{
		synchronized (this)
		{
			mNextValue = aNextValue;

			if (mNextValue != null && mWorker == null)
			{
				mWorker = new Thread()
				{
					{
						setDaemon(true);
					}

					@Override
					public void run()
					{
						for (;;)
						{
							T nextValue;

							synchronized (NonBlockingDiscardingTaskExecutor.this)
							{
								nextValue = mNextValue;
								mNextValue = null;
							}

							if (nextValue == null)
							{
								mWorker = null;
								return;
							}

							try
							{
								mConsumer.accept(nextValue);
							}
							catch (Error | Exception e)
							{
								e.printStackTrace(System.out);
							}
						}
					}
				};

				mWorker.start();
			}
		}
	}

//	public static void main(String ... args)
//	{
//		try
//		{
//			NonBlockingDiscardingTaskExecutor<Point> processor = new NonBlockingDiscardingTaskExecutor<>(pt -> drawUpdateOnScreen(pt));
//
//			MouseMotionAdapter l = new MouseMotionAdapter() {
//				public void mouseMoved(MouseEvent aEvent) {
//					processor.submit(aEvent.getPoint());
//				}
//			};
//
//			JPanel panel = new JPanel();
//			panel.addMouseMotionListener(l);
//
//			JFrame frame = new JFrame();
//			frame.add(panel);
//			frame.setSize(1024, 768);
//			frame.setLocationRelativeTo(null);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setVisible(true);
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

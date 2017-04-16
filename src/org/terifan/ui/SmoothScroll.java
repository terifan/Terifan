package org.terifan.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.Timer;


public class SmoothScroll 
{
	private final JComponent mOwner;
	private Timer mScrollTimer;
	private double mScrollVelocity;
	private double mScrollFraction;


	public SmoothScroll(JComponent aOwner)
	{
		mOwner = aOwner;
	}

	
	public synchronized void smoothScroll(double aPreciseWheelRotation)
	{
		mScrollVelocity += 50 * aPreciseWheelRotation;

		if (mScrollTimer == null)
		{
			ActionListener task = new AbstractAction()
			{
				@Override
				public void actionPerformed(ActionEvent aEvent)
				{
					mScrollFraction += mScrollVelocity;
					
					int scrollInc = (int)mScrollFraction;

					if (scrollInc != 0)
					{
						Rectangle current = mOwner.getVisibleRect();
						current.y += scrollInc;

						mScrollFraction -= scrollInc;

						mOwner.scrollRectToVisible(current);
					}

					mScrollVelocity *= 0.8;

					if (mScrollVelocity < 0 && mScrollFraction + mScrollVelocity > -1 || mScrollVelocity > 0 && mScrollFraction + mScrollVelocity < 1)
					{
						synchronized (SmoothScroll.this)
						{
							mScrollTimer.stop();
							mScrollFraction = 0;
							mScrollVelocity = 0;
							mScrollTimer = null;
						}
					}
				}
			};

			mScrollTimer = new Timer(25, task);
			mScrollTimer.setInitialDelay(0);
			mScrollTimer.start();
		}
	}
}

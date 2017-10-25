package org.terifan.dashboard;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;


class DashboardMouseListener extends MouseAdapter
{
	private DashboardComponent mComponent;
	private Point mClickPosition;
	private boolean mMove;
	private boolean mResize;


	public DashboardMouseListener(DashboardComponent aCustomizableArea)
	{
		mComponent = aCustomizableArea;
	}


	@Override
	public void mousePressed(MouseEvent aEvent)
	{
		mClickPosition = aEvent.getPoint();

		Dashboard panel = mComponent.getDashboard();
		panel.moveToTop(mComponent);
		mComponent.invalidate();
		panel.validate();
		panel.repaint();

		mComponent.getDashboard().setSelected(mComponent);

		mMove = mClickPosition.x < 20 && mClickPosition.y < 20;
		mResize = mClickPosition.x > mComponent.getWidth() - 20 && mClickPosition.y > mComponent.getHeight() - 20;
	}


	@Override
	public void mouseReleased(MouseEvent aEvent)
	{
	}


	@Override
	public void mouseDragged(MouseEvent aEvent)
	{
		if (!mMove && !mResize)
		{
			return;
		}

		Dashboard panel = mComponent.getDashboard();

		if (mMove)
		{
			Point pos = SwingUtilities.convertPoint(mComponent, aEvent.getPoint(), panel);
			mComponent.getBounds().setLocation(4 + (int)Math.round((pos.x - mClickPosition.x)/100.0)*100, 4 + (int)Math.round((pos.y - mClickPosition.y)/100.0)*100);
		}
		else
		{
			Point pos = aEvent.getPoint();
			mComponent.getBounds().setSize((int)Math.max(Math.round(pos.x/100.0), 1)*100 - 4, (int)Math.max(Math.round(pos.y/100.0), 1)*100 - 4);
		}

		mComponent.invalidate();
		panel.validate();
	}
}

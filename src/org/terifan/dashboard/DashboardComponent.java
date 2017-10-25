package org.terifan.dashboard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.JPanel;


public class DashboardComponent extends JPanel
{
	private Rectangle mBounds;
	private Dashboard mDashboard;


	DashboardComponent(int aX, int aY, int aWidth, int aHeight)
	{
		mBounds = new Rectangle(4 + 200*aX, 4 + 200*aY, 200*aWidth - 4, 200*aHeight - 4);

		DashboardMouseListener listener = new DashboardMouseListener(this);
		super.addMouseListener(listener);
		super.addMouseMotionListener(listener);
		super.setBackground(Color.WHITE);
	}


	public void bind(Dashboard aCustomizablePanel)
	{
		mDashboard = aCustomizablePanel;
	}


	public Dashboard getDashboard()
	{
		return mDashboard;
	}


	@Override
	public Rectangle getBounds()
	{
		return mBounds;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int x = 0;
		int y = 0;
		int w = getWidth();
		int h = getHeight();

		aGraphics.setColor(getBackground());
		aGraphics.fillRect(x, y, w, h);
		aGraphics.setColor(Color.GRAY);
		aGraphics.drawRect(x, y, w - 1, h - 1);
		aGraphics.drawRect(x+2, y+2, 16, 16);
		aGraphics.setColor(new Color(220,220,220));
		aGraphics.fillRect(x+w-5, y+h-16, 3, 14);
		aGraphics.fillRect(x+w-16, y+h-5, 14, 3);

		if (mDashboard.getSelected() == this)
		{
			aGraphics.setColor(new Color(156,213,244));
			aGraphics.drawRect(x, y, w - 1, h - 1);
			aGraphics.drawRect(x + 1, y + 1, w - 3, h - 3);
		}
	}
}

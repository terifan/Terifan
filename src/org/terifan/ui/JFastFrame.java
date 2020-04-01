package org.terifan.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;


public class JFastFrame extends JFrame
{
	private GeneralPath gp;


	public JFastFrame()
	{
		MouseInputListener resizeHook = new MouseInputAdapter()
		{
			private Point startPos = null;


			@Override
			public void mousePressed(MouseEvent e)
			{
				if (gp.contains(e.getPoint()))
				{
					startPos = new Point(getWidth() - e.getX(), getHeight() - e.getY());
				}
			}


			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				startPos = null;
			}


			@Override
			public void mouseMoved(MouseEvent e)
			{
				if (gp.contains(e.getPoint()))
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				}
				else
				{
					setCursor(Cursor.getDefaultCursor());
				}
			}


			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (startPos != null)
				{
					int dx = e.getX() + startPos.x;
					int dy = e.getY() + startPos.y;
					setSize(dx, dy);
					repaint();
				}
			}
		};

		addMouseMotionListener(resizeHook);
		addMouseListener(resizeHook);

		setResizable(false);
		setMinimumSize(new Dimension(500, 300));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
	}


	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		int w = getWidth();
		int h = getHeight();
		g.setColor(new Color(150, 150, 150, 200));
		g.drawLine(w - 7, h, w, h - 7);
		g.drawLine(w - 11, h, w, h - 11);
		g.drawLine(w - 15, h, w, h - 15);

		gp = new java.awt.geom.GeneralPath();
		gp.moveTo(w - 17, h);
		gp.lineTo(w, h - 17);
		gp.lineTo(w, h);
		gp.closePath();
	}
}

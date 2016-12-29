package org.terifan.ui.propertygrid;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import org.terifan.ui.StyleSheet;


public class PropertyGridListPane extends JPanel implements Scrollable
{
	protected PropertyGrid mPropertyGrid;
	private int MAX_UNIT_INCREMENT = 100;


	public PropertyGridListPane(PropertyGrid aPropertyGrid)
	{
		mPropertyGrid = aPropertyGrid;

		setLayout(new PropertyGridLayout());
	}


	@Override
	public Dimension getPreferredSize()
	{
		PropertyGridModel model = mPropertyGrid.getModel();
		StyleSheet style = mPropertyGrid.getStylesheet();
		int rowHeight = style.getInt("row_height");

		int width = 0;
		int height = 0;

		for (Iterator<Property> it = model.getRecursiveIterator(); it.hasNext(); )
		{
			Property item = it.next();

			Component component = item.getValueComponent();

			if (component != null)
			{
				width = Math.max(width, component.getPreferredSize().width);
				height += rowHeight;
			}
		}

		return new Dimension(mPropertyGrid.getDividerPosition() + width, height);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		StyleSheet style = mPropertyGrid.getStylesheet();
		PropertyGridModel model = mPropertyGrid.getModel();
		int rowHeight = style.getInt("row_height");

		Graphics2D g = (Graphics2D)aGraphics;

		g.setColor(style.getColor("text_background"));
		g.fillRect(0, 0, getWidth(), getHeight());

		aGraphics.setColor(style.getColor("grid"));

		int y = 0;
		for (Iterator<Property> it = model.getRecursiveIterator(); it.hasNext();)
		{
			it.next();

			y += rowHeight;
			aGraphics.drawLine(0, y-1, getWidth()-1, y-1);
		}

		aGraphics.drawLine(mPropertyGrid.getDividerPosition(), 0, mPropertyGrid.getDividerPosition(), y-1);
	}


	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}


	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}


	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		if (orientation == SwingConstants.HORIZONTAL)
		{
			return visibleRect.width - MAX_UNIT_INCREMENT;
		}
		else
		{
			return visibleRect.height - MAX_UNIT_INCREMENT;
		}
	}


	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL)
		{
			currentPosition = visibleRect.x;
		}
		else
		{
			currentPosition = visibleRect.y;
		}

		if (direction < 0)
		{
			int newPosition = currentPosition - (currentPosition / MAX_UNIT_INCREMENT) * MAX_UNIT_INCREMENT;
			return (newPosition == 0) ? MAX_UNIT_INCREMENT : newPosition;
		}
		else
		{
			return ((currentPosition / MAX_UNIT_INCREMENT) + 1) * MAX_UNIT_INCREMENT - currentPosition;
		}
	}
}
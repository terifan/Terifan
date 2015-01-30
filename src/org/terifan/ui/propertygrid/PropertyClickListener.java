package org.terifan.ui.propertygrid;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import org.terifan.ui.StyleSheet;


class PropertyClickListener extends MouseAdapter
{
	private Property mProperty;
	private boolean mIndent;


	PropertyClickListener(Property aProperty, boolean aIndent)
	{
		mProperty = aProperty;
		mIndent = aIndent;
	}


	@Override
	public void mousePressed(MouseEvent aEvent)
	{
		PropertyGrid propertyGrid = mProperty.getPropertyGrid();
		StyleSheet style = propertyGrid.getStylesheet();
		int indent = propertyGrid.getModel().getIndent(mProperty);
		int indentWidth = style.getInt("indent_width");

		if (mProperty.getPropertyCount() > 0)
		{
			boolean clicked = false;

			if (mIndent)
			{
				int x = Math.max(indent-1,0) * indentWidth;
				clicked = aEvent.getX() >= x && aEvent.getX() <= x+indentWidth;
			}

			if (clicked || aEvent.getClickCount() > 1)
			{
				mProperty.setCollapsed(!mProperty.getCollapsed());
				propertyGrid.setModel(propertyGrid.getModel());
				propertyGrid.redisplay();
				return;
			}
		}

		if (propertyGrid.getModel().getIndent(mProperty) == 0)
		{
			mProperty.getLabelComponent().requestFocus();
			propertyGrid.repaint();
		}
		else if (mProperty.getValueComponent() != null)
		{
			mProperty.getValueComponent().requestFocus();
		}
	}
}

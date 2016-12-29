package org.terifan.ui.propertygrid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;


class PropertyGridIndent extends JComponent
{
	protected Property mProperty;


	public PropertyGridIndent(Property aProperty)
	{
		mProperty = aProperty;

		addMouseListener(new PropertyClickListener(mProperty, true));
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		PropertyGrid propertyGrid = mProperty.getPropertyGrid();
		StyleSheet style = propertyGrid.getStylesheet();
		int indent = propertyGrid.getModel().getIndent(mProperty);
		int indentWidth = style.getInt("indent_width");
		boolean selected = propertyGrid.getSelectedProperty() == mProperty;

		Color background;

		if (indent == 0)
		{
			aGraphics.setFont(style.getFont("group"));
			indent = 1;
			background = style.getColor("indent_background");
		}
		else if (selected)
		{
			boolean focusOwner = Utilities.isWindowFocusOwner(propertyGrid);
			aGraphics.setFont(style.getFont("item"));
			background = focusOwner ? style.getColor("selected_property_background") : style.getColor("grid");
		}
		else
		{
			aGraphics.setFont(style.getFont("item"));
			background = style.getColor("text_background");
		}

		aGraphics.setColor(style.getColor("indent_background"));
		aGraphics.fillRect(0, 0, indentWidth, getHeight());

		aGraphics.setColor(background);
		aGraphics.fillRect(indentWidth, 0, getWidth()-indentWidth, getHeight());

		if (mProperty.getPropertyCount() > 0)
		{
			int x = indent*indentWidth;
			BufferedImage image = mProperty.getCollapsed() ? style.getImage("expand_button") : style.getImage("collapse_button");
			aGraphics.drawImage(image, x-indentWidth+(indentWidth-image.getWidth())/2, (getHeight()-image.getHeight())/2, null);
		}
	}
}
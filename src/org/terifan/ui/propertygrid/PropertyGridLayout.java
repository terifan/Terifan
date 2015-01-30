package org.terifan.ui.propertygrid;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.terifan.ui.StyleSheet;


class PropertyGridLayout implements LayoutManager //, PropertyGridModel.Callback
{
	public PropertyGridLayout()
	{
	}


	@Override
	public void addLayoutComponent(String name, Component comp)
	{
	}


	@Override
	public void removeLayoutComponent(Component comp)
	{
	}


	@Override
	public Dimension preferredLayoutSize(Container target)
	{
		return ((PropertyGridListPane)target).getPreferredSize();
	}


	@Override
	public Dimension minimumLayoutSize(Container target)
	{
		return ((PropertyGridListPane)target).getPreferredSize();
	}


	@Override
	public void layoutContainer(Container target)
	{
		synchronized (target.getTreeLock())
		{
			PropertyGrid propertyGrid = ((PropertyGridListPane)target).mPropertyGrid;
			PropertyGridModel model = propertyGrid.getModel();
			StyleSheet style = propertyGrid.getStylesheet();
			int rowHeight = style.getInt("row_height");

			int y = 0;
			int dividerX = propertyGrid.getDividerPosition();
			int width = target.getWidth();
			int indentWidth = style.getInt("indent_width");

			for (Iterator<Property> it = model.getRecursiveIterator(); it.hasNext(); )
			{
				Property property = it.next();

				layoutPropertyComponents(propertyGrid, property, dividerX, y, width, indentWidth, rowHeight);

				y += rowHeight;
			}
		}
	}


	protected void layoutPropertyComponents(PropertyGrid propertyGrid, Property item, int dividerX, int y, int width, int indentWidth, int rowHeight)
	{
		PropertyGridModel model = propertyGrid.getModel();

		int indent = model.getIndent(item);

		JComponent component = item.getIndentComponent();

		component.setBounds(0, y, Math.max(indent,1) * indentWidth, rowHeight-1);

		if (indent == 0)
		{
			component = item.getLabelComponent();
			component.setBounds(indentWidth, y, width-indentWidth, rowHeight-1);
		}
		else
		{
			component = item.getValueComponent();

			JButton button = item.getDetailButton();
			if (button != null)
			{
				int btnWidth = button.getPreferredSize().width;
				button.setBounds(width-btnWidth, y, btnWidth, rowHeight-1);
				component.setBounds(dividerX+4, y, width-dividerX-btnWidth-4-4, rowHeight-1);
			}
			else
			{
				component.setBounds(dividerX+4, y, width-dividerX-4, rowHeight-1);
			}

			component = item.getLabelComponent();
			component.setBounds(indent * indentWidth, y, dividerX-(indent * indentWidth), rowHeight-1);
		}
	}
}
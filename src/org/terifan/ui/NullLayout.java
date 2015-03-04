package org.terifan.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;


public class NullLayout implements LayoutManager
{
	@Override
	public void addLayoutComponent(String aName, Component aComp)
	{
	}


	@Override
	public void removeLayoutComponent(Component aComp)
	{
	}


	@Override
	public void layoutContainer(Container aParent)
	{
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(0,0);
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		if (aParent.getComponentCount() == 0)
		{
			return new Dimension();
		}

		Rectangle bounds = aParent.getComponent(0).getBounds();

		for (int i = 1; i < aParent.getComponentCount(); i++)
		{
			Component component = aParent.getComponent(i);
			if (component.isVisible())
			{
				bounds.add(component.getBounds());
			}
		}

		return new Dimension(bounds.x + bounds.width, bounds.y + bounds.height);
	}
}

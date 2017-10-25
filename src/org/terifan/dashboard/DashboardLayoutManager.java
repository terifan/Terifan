package org.terifan.dashboard;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;


public class DashboardLayoutManager implements LayoutManager
{
	DashboardLayoutManager(Dashboard aaThis)
	{
	}


	@Override
	public void addLayoutComponent(String aName, Component aComp)
	{
	}


	@Override
	public void removeLayoutComponent(Component aComp)
	{
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		return new Dimension();
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension();
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		synchronized (aParent.getTreeLock())
		{
			Insets insets = aParent.getInsets();

			int w = aParent.getWidth();
			int h = aParent.getHeight();

			for (int i = 0; i < aParent.getComponentCount(); i++)
			{
				Component comp = aParent.getComponent(i);

				if (comp.isVisible())
				{
					DashboardComponent area = (DashboardComponent)comp;

					comp.setBounds(area.getBounds());
				}
			}
		}
	}
}

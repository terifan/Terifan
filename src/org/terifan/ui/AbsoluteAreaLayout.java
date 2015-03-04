package org.terifan.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.JComponent;
import org.terifan.util.log.Log;


public class AbsoluteAreaLayout implements LayoutManager2
{
	private HashMap<Component,Rectangle> mConstraints;
	private int mVerGap;
	private int mHorGap;


	public AbsoluteAreaLayout(int aHorGap, int aVerGap)
	{
		mConstraints = new HashMap<>();
		mHorGap = aHorGap;
		mVerGap = aVerGap;
	}


	@Override
	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
		mConstraints.put(aComp, (Rectangle)aConstraints);
	}


	@Override
	public Dimension maximumLayoutSize(Container aTarget)
	{
		return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	}


	@Override
	public float getLayoutAlignmentX(Container aTarget)
	{
		return 0;
	}


	@Override
	public float getLayoutAlignmentY(Container aTarget)
	{
		return 0;
	}


	@Override
	public void invalidateLayout(Container aParent)
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
		layoutContainer(aParent);

		synchronized (aParent.getTreeLock())
		{
			if (aParent.getComponentCount() == 0)
			{
				return new Dimension();
			}

			Rectangle bounds = null;

			for (int i = 1; i < aParent.getComponentCount(); i++)
			{
				Component component = aParent.getComponent(i);

				if (component.isVisible())
				{
					if (bounds == null)
					{
						bounds = new Rectangle(component.getBounds());
					}
					else
					{
						bounds.add(component.getBounds());
					}
				}
			}

			Insets border = aParent.getInsets();

			if (bounds == null)
			{
				return new Dimension(border.left + border.right, border.top + border.bottom);
			}

			return new Dimension(bounds.x + bounds.width + border.left + border.right, bounds.y + bounds.height + border.top + border.bottom);
		}
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(0, 0);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		synchronized (aParent.getTreeLock())
		{
			Insets border;

			if (aParent instanceof JComponent)
			{
				border = ((JComponent)aParent).getBorder().getBorderInsets((JComponent)aParent);
			}
			else
			{
				border = new Insets(0,0,0,0);
			}

			int x = border.left;
			int y = border.top;
			int w = aParent.getWidth() - border.left - border.right;
			int h = aParent.getHeight() - border.top - border.bottom;

			if (w <= 0)
			{
				Dimension d = guessPreferredSize(aParent);
				w = d.width;
				h = d.height;
			}

			for (int i = 0; i < aParent.getComponentCount(); i++)
			{
				Component comp = aParent.getComponent(i);

				if (comp.isVisible())
				{
					Rectangle rect = mConstraints.get(comp);
					int ix = x + (int)(rect.x * w / 100.0);
					int iy = y + (int)(rect.y * h / 100.0);
					int iw = (int)(rect.width * w / 100.0);
					int ih = (int)(rect.height * h / 100.0);
					if (ix + iw < w)
					{
						iw -= mHorGap;
					}
					if (iy + ih < h)
					{
						ih -= mVerGap;
					}
					comp.setBounds(ix, iy, iw, ih);
				}
			}
		}
	}


	public Rectangle getConstraints(Component aComponent)
	{
		return mConstraints.get(aComponent);
	}


	private Dimension guessPreferredSize(Container aParent)
	{
		int w = 0;
		int h = 0;

		for (int i = 0; i < aParent.getComponentCount(); i++)
		{
			Component comp = aParent.getComponent(i);

			if (comp.isVisible())
			{
				Rectangle rect = mConstraints.get(comp);

				Dimension dim = comp.getPreferredSize();

				if (rect.width >= 100)
				{
					w = (int)Math.max(w, mHorGap + dim.width);
				}
				else
				{
					w = (int)Math.max(w, mHorGap + dim.width / (1 - rect.width / 100.0));
				}
				if (rect.height >= 100)
				{
					h = (int)Math.max(h, mVerGap + dim.height);
				}
				else
				{
					h = (int)Math.max(h, mVerGap + dim.height / (1 - rect.height / 100.0));
				}
			}
		}

		return new Dimension(w, h);
	}
}
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


public class StackedLayout implements LayoutManager2
{
	private HashMap<Component,LayoutParams> mConstraints;
	private int mVerGap;


	public StackedLayout(int aVerGap)
	{
		mConstraints = new HashMap<>();
		mVerGap = aVerGap;
	}


	@Override
	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
		mConstraints.put(aComp, (LayoutParams)aConstraints);
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
			int height = 0;

			for (int i = 0; i < aParent.getComponentCount(); i++)
			{
				Component comp = aParent.getComponent(i);

				if (comp.isVisible())
				{
					LayoutParams params = mConstraints.get(comp);
					Rectangle tmp = comp.getBounds();
					tmp.x = 0;
					tmp.y = height;
					tmp.width = comp.getPreferredSize().width;
					tmp.height = Math.max(comp.getPreferredSize().height, params.height);
					height += tmp.height;
					if (bounds == null)
					{
						bounds = new Rectangle(tmp);
					}
					else
					{
						bounds.add(tmp);
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
			int x = 0;
			int y = 0;
			int w = aParent.getWidth();
			int h = aParent.getHeight();

			if (aParent instanceof JComponent && ((JComponent)aParent).getBorder() != null)
			{
				Insets borderInsets = ((JComponent)aParent).getBorder().getBorderInsets((JComponent)aParent);
				x += borderInsets.left;
				y += borderInsets.top;
				w -= borderInsets.left + borderInsets.right;
				h -= borderInsets.top + borderInsets.bottom;
			}

			Dimension prefSize = guessPreferredSize(aParent);
			if (w <= 0)
			{
				w = prefSize.width;
			}
			int extraHeight = Math.max(h - prefSize.height, 0);

			for (int i = 0; i < aParent.getComponentCount(); i++)
			{
				Component comp = aParent.getComponent(i);

				if (comp.isVisible())
				{
					LayoutParams params = mConstraints.get(comp);
					int ix = x;
					int iy = y;
					int iw = w;
					int ih = (int)(Math.max(params.height, comp.getPreferredSize().height) + params.weight * extraHeight);
					comp.setBounds(ix, iy, iw, ih);
					y += ih + mVerGap;
				}
			}
		}
	}


	public LayoutParams getConstraints(Component aComponent)
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
				LayoutParams params = mConstraints.get(comp);

				Dimension dim = comp.getPreferredSize();

				w = Math.max(w, dim.width);
				h += mVerGap + Math.max(dim.height, params.height);
			}
		}

		return new Dimension(w, h == 0 ? 0 : h - mVerGap);
	}


	public static class LayoutParams
	{
		int height;
		double weight;

		public LayoutParams(int aHeight, double aWeight)
		{
			height = aHeight;
			weight = aWeight;
		}
	}
}
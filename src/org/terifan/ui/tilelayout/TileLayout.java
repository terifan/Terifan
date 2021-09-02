package org.terifan.ui.tilelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;


public class TileLayout implements LayoutManager2
{
	private HashMap<Component, Number> mConstraints;
	private Point mSpacing;


	public TileLayout()
	{
		this(5, 5);
	}


	public TileLayout(int aSpacingX, int aSpacingY)
	{
		mConstraints = new HashMap<>();
		mSpacing = new Point(aSpacingX, aSpacingY);
	}


	@Override
	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
		if (aConstraints != null && Number.class.isAssignableFrom(aConstraints.getClass()))
		{
			mConstraints.put(aComp, (Number)aConstraints);
		}
	}


	@Override
	public Dimension maximumLayoutSize(Container aTarget)
	{
		return preferredLayoutSize(aTarget);
	}


	@Override
	public float getLayoutAlignmentX(Container aTarget)
	{
		return 0f;
	}


	@Override
	public float getLayoutAlignmentY(Container aTarget)
	{
		return 0f;
	}


	@Override
	public void invalidateLayout(Container aTarget)
	{
	}


	@Override
	public void addLayoutComponent(String aName, Component aComp)
	{
	}


	@Override
	public void removeLayoutComponent(Component aComp)
	{
		mConstraints.remove(aComp);
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		Dimension dim = layout(aParent, false);
		return new Dimension(1, dim.height);
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return preferredLayoutSize(aParent);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		layout(aParent, true);
	}


	private Dimension layout(Container aParent, boolean aUpdateBounds)
	{
		Insets insets = aParent.getInsets();

		synchronized (aParent.getTreeLock())
		{
			Dimension parentSize = aParent.getSize();
			parentSize.width -= insets.left + insets.right;

			if (parentSize.width < 0)
			{
				return new Dimension(0, 0);
			}

			ArrayList<ArrayList<Component>> rowComponents = new ArrayList<>();
			ArrayList<ArrayList<Integer>> rowComponentWidths = new ArrayList<>();
			ArrayList<Integer> rowWidths = new ArrayList<>();
			ArrayList<Integer> rowHeights = new ArrayList<>();

			{
				ArrayList<Component> elementComponents = new ArrayList<>();
				ArrayList<Integer> elementWidths = new ArrayList<>();

				int rowWidth = 0;
				int rowHeight = 0;
				for (int i = 0; i < aParent.getComponentCount(); i++)
				{
					Component c = aParent.getComponent(i);
					boolean singleItem = mConstraints.getOrDefault(c, 0).intValue() < 0;

					if (singleItem && !elementComponents.isEmpty())
					{
						rowComponents.add(elementComponents);
						rowComponentWidths.add(elementWidths);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						elementComponents = new ArrayList<>();
						elementWidths = new ArrayList<>();
						rowWidth = 0;
						rowHeight = 0;
					}

					int ew = getPreferredWidth(c, parentSize.width);

					elementWidths.add(ew);
					elementComponents.add(c);

					rowWidth += ew + mSpacing.x;
					rowHeight = Math.max(rowHeight, getPreferredHeight(c));

					if (rowWidth - mSpacing.x >= parentSize.width || singleItem)
					{
						rowComponents.add(elementComponents);
						rowComponentWidths.add(elementWidths);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						elementComponents = new ArrayList<>();
						elementWidths = new ArrayList<>();
						rowWidth = 0;
						rowHeight = 0;
					}
				}

				if (rowWidth > 0)
				{
					rowComponents.add(elementComponents);
					rowComponentWidths.add(elementWidths);
					rowWidths.add(rowWidth);
					rowHeights.add(rowHeight);
				}
			}

			int rowY = insets.top;
			int rowIndex = 0;

			for (ArrayList<Component> elementComponents : rowComponents)
			{
				ArrayList<Integer> elementWidths = rowComponentWidths.get(rowIndex);

				int rowWidth = rowWidths.get(rowIndex) - mSpacing.x * elementWidths.size();
				int rowHeight = rowHeights.get(rowIndex);
				int rowX = 0;
				double err = 0;

				double scale = (parentSize.width - mSpacing.x * (elementWidths.size() - 1)) / (double)rowWidth;

				for (int columnIndex = 0; columnIndex < elementComponents.size(); columnIndex++)
				{
					Component c = elementComponents.get(columnIndex);

					double w;
					double pw = elementWidths.get(columnIndex);

					if (scale >= 1)
					{
						w = pw;
					}
					else if (columnIndex + 1 == elementComponents.size())
					{
						w = parentSize.width - rowX;
					}
					else
					{
						w = pw * scale + err;
					}

					if (aUpdateBounds)
					{
						c.setBounds(insets.left + (int)rowX, rowY, (int)w, rowHeight);
					}

					err = w - (int)w;
					rowX += w + mSpacing.x;
				}

				rowY += rowHeight + mSpacing.y;
				rowIndex++;
			}

			return new Dimension(parentSize.width, rowY - mSpacing.y + insets.bottom);
		}
	}


	private static class Element
	{
		Component component;
		int width;


		public Element(Component aComponent, int aWidth)
		{
			component = aComponent;
			width = aWidth;
		}
	}


	private int getPreferredWidth(Component aItem, int aLayoutWidth)
	{
		Number param = getParam(aItem);

		if (param != null)
		{
			if (param instanceof Double || param instanceof Float)
			{
				return (int)Math.ceil(param.doubleValue() * aLayoutWidth);
			}

			int w = param.intValue();
			if (w < 0)
			{
				w = aLayoutWidth;
			}
			return w;
		}

		return aItem.getPreferredSize().width;
	}


	private int getPreferredHeight(Component aItem)
	{
		return aItem.getPreferredSize().height;
	}


	private Number getParam(Component aComponent)
	{
		return mConstraints.get(aComponent);
	}
}

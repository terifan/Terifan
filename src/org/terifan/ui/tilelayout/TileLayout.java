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
			ArrayList<Integer> rowWidths = new ArrayList<>();
			ArrayList<Integer> rowHeights = new ArrayList<>();

			{
				ArrayList<Component> components = new ArrayList<>();

				int rowWidth = -mSpacing.x;
				int rowHeight = 0;
				for (int i = 0; i < aParent.getComponentCount(); i++)
				{
					Component c = aParent.getComponent(i);
					boolean singleItem = mConstraints.getOrDefault(c, 0).intValue() < 0;

					if (singleItem && !components.isEmpty())
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						components = new ArrayList<>();
						rowWidth = -mSpacing.x;
						rowHeight = 0;
					}

					rowWidth += getPreferredWidth(c, parentSize.width) + mSpacing.x;
					rowHeight = Math.max(rowHeight, getPreferredHeight(c));

					components.add(c);

					if (rowWidth >= parentSize.width || singleItem)
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						components = new ArrayList<>();
						rowWidth = -mSpacing.x;
						rowHeight = 0;
					}
				}

				if (rowWidth > 0)
				{
					rowComponents.add(components);
					rowWidths.add(rowWidth);
					rowHeights.add(rowHeight);
				}
			}

			int rowY = insets.top;
			int rowIndex = 0;

			for (ArrayList<Component> row : rowComponents)
			{
				int rowWidth = rowWidths.get(rowIndex);
				int rowHeight = rowHeights.get(rowIndex);
				double rowX = 0;

				for (int columnIndex = 0; columnIndex < row.size(); columnIndex++)
				{
					Component c = row.get(columnIndex);

					int pw = getPreferredWidth(c, parentSize.width);

					double w;

					Number param = getParam(c);
					if (param != null)
					{
						if (param instanceof Double || param instanceof Float)
						{
							w = (int)Math.ceil(param.doubleValue() * parentSize.width);
						}
						else
						{
							w = param.intValue();
							if (w < 0)
							{
								w = parentSize.width;
							}
						}
					}
					else
					{
						if (parentSize.width > rowWidth)
						{
							w = pw;
						}
						else if (columnIndex + 1 == row.size())
						{
							w = parentSize.width - rowX;
						}
						else
						{
							w = pw * parentSize.width / (double)rowWidth;
						}
					}

					if (aUpdateBounds)
					{
						c.setBounds(insets.left + (int)rowX, rowY, (int)(rowX + w) - (int)rowX, rowHeight);
					}

					rowX += w + mSpacing.x;
				}

				rowY += rowHeight + mSpacing.y;
				rowIndex++;
			}

			Dimension dim = new Dimension(parentSize.width, rowY);
			dim.height += insets.top + insets.bottom;

			return dim;
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
			else
			{
				int w = param.intValue();
				if (w < 0)
				{
					w = aLayoutWidth;
				}
				return w;
			}
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

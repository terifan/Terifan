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
			int width = parentSize.width - insets.left - insets.right;

			if (width < 0)
			{
				return new Dimension(0, 0);
			}

			int offsetY = insets.top;

			for (Row row : layoutRows(aParent, width))
			{
				offsetY = layoutColumns(row, insets.left, offsetY, aUpdateBounds, insets.left + width);
			}

			return new Dimension(width, offsetY - mSpacing.y + insets.bottom);
		}
	}


	private int layoutColumns(Row aRow, int aOffsetX, int aOffsetY, boolean aUpdateBounds, int aTarget)
	{
		double scale = (aTarget - aOffsetX - mSpacing.x * (aRow.elements.size() - 1)) / (double)(aRow.width - mSpacing.x * aRow.elements.size());
		double error = 0;

		for (int columnIndex = 0; columnIndex < aRow.elements.size(); columnIndex++)
		{
			Element element = aRow.elements.get(columnIndex);

			double width;
			if (scale >= 1)
			{
				width = element.width;
			}
			else if (columnIndex + 1 == aRow.elements.size())
			{
				width = aTarget - aOffsetX;
			}
			else
			{
				width = element.width * scale + error;
			}

			if (aUpdateBounds)
			{
				element.component.setBounds(aOffsetX, aOffsetY, (int)width, aRow.height);
			}

			error = width - (int)width;
			aOffsetX += width + mSpacing.x;
		}

		return aOffsetY + aRow.height + mSpacing.y;
	}


	private ArrayList<Row> layoutRows(Container aParent, int aTargetWidth)
	{
		ArrayList<Row> rows = new ArrayList<>();

		Row row = new Row();

		for (int i = 0; i < aParent.getComponentCount(); i++)
		{
			Component c = aParent.getComponent(i);

			boolean singleItem = mConstraints.getOrDefault(c, 0).intValue() < 0;

			if (singleItem && !row.elements.isEmpty())
			{
				rows.add(row);
				row = new Row();
			}

			Element element = new Element(c, getPreferredWidth(c, aTargetWidth));
			row.elements.add(element);
			row.width += element.width + mSpacing.x;
			row.height = Math.max(row.height, getPreferredHeight(c));

			if (row.width - mSpacing.x >= aTargetWidth || singleItem)
			{
				rows.add(row);
				row = new Row();
			}
		}

		if (row.width > 0)
		{
			rows.add(row);
		}

		return rows;
	}


	private static class Row
	{
		ArrayList<Element> elements = new ArrayList<>();
		int width;
		int height;
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

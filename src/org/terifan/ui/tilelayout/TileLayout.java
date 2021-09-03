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
	private boolean V;


	public TileLayout(boolean aVertical)
	{
		this(aVertical, 5, 5);
	}


	public TileLayout(boolean aVertical, int aSpacingX, int aSpacingY)
	{
		V = aVertical;
		mConstraints = new HashMap<>();
		mSpacing = new Point(aSpacingX, aSpacingY);
	}


	public boolean isVertical()
	{
		return V;
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
		return V ? new Dimension(1, dim.height) : new Dimension(dim.width, 1);
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
			int size;
			if (V) size = aParent.getSize().width - insets.left - insets.right;
			else size = aParent.getSize().height - insets.top - insets.bottom;

			if (size < 0)
			{
				return new Dimension(0, 0);
			}

			int offset = V ? insets.top : insets.left;

			for (Row row : layoutRows(aParent, size))
			{
				if (V) offset = layoutColumns(row, insets.left, offset, aUpdateBounds, insets.left + size);
				else offset = layoutColumns(row, offset, insets.top, aUpdateBounds, insets.top + size);
			}

			if (V) return new Dimension(size, offset - mSpacing.y + insets.bottom);
			else return new Dimension(offset - mSpacing.x + insets.right, size);
		}
	}


	private int layoutColumns(Row aRow, int aOffsetX, int aOffsetY, boolean aUpdateBounds, int aTarget)
	{
		double scale;
		if (V) scale = (aTarget - aOffsetX - mSpacing.x * (aRow.elements.size() - 1)) / (double)(aRow.width - mSpacing.x * aRow.elements.size());
		else scale = (aTarget - aOffsetY - mSpacing.y * (aRow.elements.size() - 1)) / (double)(aRow.height - mSpacing.y * aRow.elements.size());

		double error = 0;

		for (int index = 0; index < aRow.elements.size(); index++)
		{
			Element element = aRow.elements.get(index);

			double size;
			if (scale >= 1)
			{
				size = element.size;
			}
			else if (index + 1 == aRow.elements.size())
			{
				if (V) size = aTarget - aOffsetX;
				else size = aTarget - aOffsetY;
			}
			else
			{
				size = element.size * scale + error;
			}

			if (aUpdateBounds)
			{
				if (V) element.component.setBounds(aOffsetX, aOffsetY, (int)size, aRow.height);
				else element.component.setBounds(aOffsetX, aOffsetY, aRow.width, (int)size);
			}

			error = size - (int)size;
			if (V) aOffsetX += size + mSpacing.x;
			else aOffsetY += size + mSpacing.y;
		}

		if (V) return aOffsetY + aRow.height + mSpacing.y;
		else return aOffsetX + aRow.width + mSpacing.x;
	}


	private ArrayList<Row> layoutRows(Container aParent, int aTarget)
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

			Element element = new Element(c, getPreferredSize(c, aTarget));
			row.elements.add(element);

			if (V)
			{
				row.width += element.size + mSpacing.x;
				row.height = Math.max(row.height, getPreferredSize(c));
			}
			else
			{
				row.width = Math.max(row.width, getPreferredSize(c));
				row.height += element.size + mSpacing.y;
			}

			if (singleItem || V && (row.width - mSpacing.x >= aTarget) || !V && (row.height - mSpacing.y >= aTarget))
			{
				rows.add(row);
				row = new Row();
			}
		}

		if (!row.elements.isEmpty())
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
		int size;


		public Element(Component aComponent, int aSize)
		{
			component = aComponent;
			size = aSize;
		}
	}


	private int getPreferredSize(Component aItem, int aLayoutSize)
	{
		Number param = mConstraints.get(aItem);

		if (param != null)
		{
			if (param instanceof Double || param instanceof Float)
			{
				return (int)Math.ceil(param.doubleValue() * aLayoutSize);
			}

			int w = param.intValue();
			if (w < 0)
			{
				w = aLayoutSize;
			}
			return w;
		}

		return V ? aItem.getPreferredSize().width : aItem.getPreferredSize().height;
	}


	private int getPreferredSize(Component aItem)
	{
		return V ? aItem.getPreferredSize().height : aItem.getPreferredSize().width;
	}
}

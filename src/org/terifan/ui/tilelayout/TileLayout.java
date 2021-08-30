package org.terifan.ui.tilelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.HashMap;


public class TileLayout implements LayoutManager2
{
	private int mRowHeight;
	private int mPaddingX;
	private int mPaddingY;


	public TileLayout(int aRowHeight)
	{
		mRowHeight = aRowHeight;
		mPaddingX = 2;
		mPaddingY = 2;
	}


	public int getRowHeight()
	{
		return mRowHeight;
	}


	public TileLayout setRowHeight(int aRowHeight)
	{
		mRowHeight = aRowHeight;
		return this;
	}


	public int getPaddingX()
	{
		return mPaddingX;
	}


	public TileLayout setPaddingX(int aPaddingX)
	{
		mPaddingX = aPaddingX;
		return this;
	}


	public int getPaddingY()
	{
		return mPaddingY;
	}


	public TileLayout setPaddingY(int aPaddingY)
	{
		mPaddingY = aPaddingY;
		return this;
	}

	private HashMap<Component, Object> mConstraints = new HashMap<>();

	@Override
	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
		mConstraints.put(aComp, aConstraints);
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
			int n = aParent.getComponentCount();

			parentSize.width -= insets.left + insets.right;

			ArrayList<ArrayList<Component>> rowComponents = new ArrayList<>();
			ArrayList<Integer> rowWidths = new ArrayList<>();
			ArrayList<Integer> rowHeights = new ArrayList<>();

			{
				ArrayList<Component> components = new ArrayList<>();

				int rowWidth = 0;
				int rowHeight = 0;
				for (int i = 0; i < n; i++)
				{
					Component c = aParent.getComponent(i);
					boolean singleItem = (c instanceof TileLayoutItem) && ((TileLayoutItem)c).getPreferredWidthWeight() < 0;

					if (singleItem && !components.isEmpty())
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						components = new ArrayList<>();
						rowWidth = 0;
						rowHeight = 0;
					}

					rowWidth += getPreferredWidth(c, parentSize.width) + 2 * mPaddingX;
					rowHeight = Math.max(rowHeight, getPreferredHeight(c, rowWidth));

					components.add(c);

					if (rowWidth > parentSize.width || singleItem)
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						rowHeights.add(rowHeight);
						components = new ArrayList<>();
						rowWidth = 0;
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

			int rowY = 0;
			int rowIndex = 0;
			for (ArrayList<Component> row : rowComponents)
			{
				int rowWidth = rowWidths.get(rowIndex);
				int rowHeight = rowHeights.get(rowIndex);
				double rowX = 0;

				for (int columnIndex = 0; columnIndex < row.size(); columnIndex++)
				{
					Component c = row.get(columnIndex);

					int pw = getPreferredWidth(c, parentSize.width) + 2 * mPaddingX;

					double w;
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

					if (aUpdateBounds)
					{
						c.setBounds(insets.left + (int)rowX, insets.top + rowY, (int)(rowX+w)-(int)rowX, rowHeight + 2 * mPaddingY);
					}

					rowX += w;
				}

				rowY += mRowHeight + 2 * mPaddingY;
				rowIndex++;
			}

			Dimension dim = new Dimension(parentSize.width, rowY);
			dim.height += insets.top + insets.bottom;

			return dim;
		}
	}


	private int getPreferredWidth(Component aItem, int aLayoutWidth)
	{
		if (aItem instanceof TileLayoutItem)
		{
			float weight = ((TileLayoutItem)aItem).getPreferredWidthWeight();
			if (weight < 0)
			{
				return aLayoutWidth;
			}
			if (weight > 0)
			{
				return (int)(weight * aLayoutWidth);
			}
		}

		return aItem.getPreferredSize().width;
	}


	private int getPreferredHeight(Component aItem, int aLayoutWidth)
	{
		return aItem.getPreferredSize().height;
	}
}

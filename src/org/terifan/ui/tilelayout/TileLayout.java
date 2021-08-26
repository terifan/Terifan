package org.terifan.ui.tilelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;


public class TileLayout implements LayoutManager2
{
	private Dimension mPreferredLayoutSize;
	private int mRowHeight;
	private int mPaddingX;
	private int mPaddingY;


	public TileLayout(int aRowHeight)
	{
		mRowHeight = aRowHeight;
		mPaddingX = 4;
		mPaddingY = 4;
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
		this.mPaddingX = aPaddingX;
		return this;
	}


	public int getPaddingY()
	{
		return mPaddingY;
	}


	public TileLayout setPaddingY(int aPaddingY)
	{
		this.mPaddingY = aPaddingY;
		return this;
	}


	@Override
	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
	}


	@Override
	public Dimension maximumLayoutSize(Container aTarget)
	{
		return new Dimension(32767, 32767);
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
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		return mPreferredLayoutSize;
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(1, 1);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		int layoutWidth = aParent.getWidth();

		ArrayList<ArrayList<Component>> rows = new ArrayList<>();
		ArrayList<Integer> rowWidths = new ArrayList<>();

		{
			ArrayList<Component> row = new ArrayList<>();

			int rowWidth = 0;
			for (Component item : aParent.getComponents())
			{
				boolean singleItem = (item instanceof TileLayoutItem) && ((TileLayoutItem)item).getPreferredWidthWeight() < 0;

				if (!row.isEmpty() && singleItem)
				{
					rows.add(row);
					rowWidths.add(rowWidth);
					row = new ArrayList<>();
					rowWidth = 0;
				}

				int pw = getPreferredWidth(item, layoutWidth);
				rowWidth += pw + 2 * mPaddingX;

				row.add(item);

				if (rowWidth > layoutWidth || singleItem)
				{
					rows.add(row);
					rowWidths.add(rowWidth);
					row = new ArrayList<>();
					rowWidth = 0;
				}
			}

			if (rowWidth > 0)
			{
				rows.add(row);
				rowWidths.add(rowWidth);
			}
		}

		int rowY = 0;
		int rowIndex = 0;
		for (ArrayList<Component> row : rows)
		{
			double rowX = 0;

			for (int columnIndex = 0; columnIndex < row.size(); columnIndex++)
			{
				Component item = row.get(columnIndex);

				int pw = getPreferredWidth(item, layoutWidth);
				pw += 2 * mPaddingX;

				double w;
				if (layoutWidth > rowWidths.get(rowIndex))
				{
					w = pw;
				}
				else if (columnIndex + 1 == row.size())
				{
					w = layoutWidth - rowX;
				}
				else
				{
					w = pw * layoutWidth / rowWidths.get(rowIndex);
				}

				item.setBounds((int)rowX, rowY, (int)w, mRowHeight + 2 * mPaddingY);

				rowX += w;
			}

			rowY += mRowHeight + 2 * mPaddingY;
			rowIndex++;
		}
	}


	public static int getPreferredWidth(Component aItem, int aLayoutWidth)
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
}

package org.terifan.ui.tilelayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;


public class TileLayout implements LayoutManager
{
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
		return layout(aParent, false);
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
//		return layout(aParent, false);
		return new Dimension(1,1);
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

			{
				ArrayList<Component> components = new ArrayList<>();

				int rowWidth = 0;
				for (int i = 0; i < n; i++)
				{
					Component c = aParent.getComponent(i);
					boolean singleItem = (c instanceof TileLayoutItem) && ((TileLayoutItem)c).getPreferredWidthWeight() < 0;

					if (singleItem && !components.isEmpty())
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						components = new ArrayList<>();
						rowWidth = 0;
					}

					int pw = getPreferredWidth(c, parentSize.width);
					rowWidth += pw + 2 * mPaddingX;

					components.add(c);

					if (rowWidth > parentSize.width || singleItem)
					{
						rowComponents.add(components);
						rowWidths.add(rowWidth);
						components = new ArrayList<>();
						rowWidth = 0;
					}
				}

				if (rowWidth > 0)
				{
					rowComponents.add(components);
					rowWidths.add(rowWidth);
				}
			}

			int rowY = 0;
			int rowIndex = 0;
			for (ArrayList<Component> row : rowComponents)
			{
				double rowX = 0;

				for (int columnIndex = 0; columnIndex < row.size(); columnIndex++)
				{
					Component c = row.get(columnIndex);

					int pw = getPreferredWidth(c, parentSize.width) + 2 * mPaddingX;

					double w;
					if (parentSize.width > rowWidths.get(rowIndex))
					{
						w = pw;
					}
					else if (columnIndex + 1 == row.size())
					{
						w = parentSize.width - rowX;
					}
					else
					{
						w = pw * parentSize.width / rowWidths.get(rowIndex);
					}

					if (aUpdateBounds)
					{
						c.setBounds((int)rowX, rowY, (int)w, mRowHeight + 2 * mPaddingY);
					}

					rowX += w;
				}

				rowY += mRowHeight + 2 * mPaddingY;
				rowIndex++;
			}

			Dimension dim = new Dimension(parentSize.width, rowY);
			dim.height += insets.top + insets.bottom;

			System.out.println(dim);

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
}

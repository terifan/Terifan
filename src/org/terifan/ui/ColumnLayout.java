package org.terifan.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Arrays;
import org.terifan.util.log.Log;


/**
 * This layout combines a flow layout and grid layout.
 */
public class ColumnLayout implements LayoutManager
{
	private int mHorGap;
	private int mVerGap;
	private int mColumns;
	private boolean mFillVertical;
	private double[] mColumnResizeWeight;
	private Insets[] mColumnMargin;
	private Insets[] mColumnPadding;
	private Dimension[] mColumnMinimumSize;


	public ColumnLayout(int aColumns, int aHorGap, int aVerGap)
	{
		if (aColumns < 1)
		{
			throw new IllegalArgumentException("Must have at least one column: " + aColumns);
		}

		mColumns = aColumns;
		mHorGap = aHorGap;
		mVerGap = aVerGap;
		mColumnResizeWeight = new double[mColumns];
		mColumnMargin = new Insets[mColumns];
		mColumnPadding = new Insets[mColumns];
		mColumnMinimumSize = new Dimension[mColumns];

		Arrays.fill(mColumnResizeWeight, 1.0);
	}


	public ColumnLayout setColumnMinimumSize(int aColumnIndex, Dimension aDimension)
	{
		mColumnMinimumSize[aColumnIndex] = aDimension;
		return this;
	}


	public Dimension getColumnMinimumSize(int aColumnIndex)
	{
		return mColumnMinimumSize[aColumnIndex];
	}


	public ColumnLayout setFillVertical(boolean aFillVertical)
	{
		mFillVertical = aFillVertical;
		return this;
	}


	public boolean isFillVertical()
	{
		return mFillVertical;
	}


	public ColumnLayout setColumnResizeWeight(int aColumnIndex, double aWeight)
	{
		mColumnResizeWeight[aColumnIndex] = aWeight;
		return this;
	}


	public double getColumnResizeWeight(int aColumnIndex)
	{
		return mColumnResizeWeight[aColumnIndex];
	}


	public ColumnLayout setColumnMargin(int aColumnIndex, Insets aInsets)
	{
		mColumnMargin[aColumnIndex] = aInsets;
		return this;
	}


	public Insets getColumnMargin(int aColumnIndex)
	{
		return mColumnMargin[aColumnIndex];
	}


	public ColumnLayout setColumnPadding(int aColumnIndex, Insets aInsets)
	{
		mColumnPadding[aColumnIndex] = aInsets;
		return this;
	}


	public Insets getColumnPadding(int aColumnIndex)
	{
		return mColumnPadding[aColumnIndex];
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
		return computeSize(aParent, true, new int[mColumns], null);
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return computeSize(aParent, false, new int[mColumns], null);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		synchronized (aParent.getTreeLock())
		{
			Insets insets = aParent.getInsets();
			int w = aParent.getWidth();
			int h = aParent.getHeight();

			int n = aParent.getComponentCount();
			int[] widths = new int[mColumns];
			int[] heights = new int[n / mColumns];
			Dimension prefSize = computeSize(aParent, true, widths, heights);

			int extraHeight = mFillVertical ? Math.max(0, (h - prefSize.height) / heights.length) : 0;

			double totalWeight = 0;
			for (double rw : mColumnResizeWeight)
			{
				totalWeight += rw;
			}

			int y = insets.top;
			for (int row = 0, i = 0; i < n; row++)
			{
				if (heights[row] == 0)
				{
					i += mColumns;
					continue;
				}

				int x = insets.left;
				int rh = (int)(heights[row] + extraHeight);
				int extraWidth = w - prefSize.width;
				double resizeWeight = totalWeight;

				for (int column = 0; column < mColumns; column++, i++)
				{
					Component comp = aParent.getComponent(i);

					if (comp.isVisible())
					{
						int cw = widths[column];

						double rw = mColumnResizeWeight[column];
						if (rw > 0)
						{
							int s = (int)(rw * extraWidth / resizeWeight);
							resizeWeight -= rw;
							extraWidth -= s;
							cw += s;
						}

						Insets m = mColumnMargin[column];

						if (m != null)
						{
							comp.setBounds(x + m.left, y + m.top, cw - m.left - m.right, rh - m.top - m.bottom);
						}
						else
						{
							comp.setBounds(x, y, cw, rh);
						}

						x += cw + mHorGap;
					}
				}

				y += heights[row] + mVerGap + extraHeight;
			}
		}
	}


	private Dimension computeSize(Container aParent, boolean aPreferred, int[] aWidths, int[] aHeights)
	{
		synchronized (aParent.getTreeLock())
		{
			int n = aParent.getComponentCount();

			if ((n % mColumns) != 0)
			{
				throw new IllegalStateException("Number of items must be dividable by the number of columns: item count=" + n + ", columns=" + mColumns);
			}

			int height = 0;

			for (int row = 0, i = 0; i < n; row++)
			{
				int h = 0;

				for (int column = 0; column < mColumns; column++, i++)
				{
					Component comp = aParent.getComponent(i);

					if (comp.isVisible())
					{
						Dimension d = aPreferred ? comp.getPreferredSize() : comp.getMinimumSize();

						Insets m = mColumnMargin[column];
						if (m != null)
						{
							d.width += m.left + m.right;
							d.height += m.top + m.bottom;
						}

						Insets p = mColumnPadding[column];
						if (p != null)
						{
							d.width += p.left + p.right;
							d.height += p.top + p.bottom;
						}

						Dimension di = mColumnMinimumSize[column];
						if (di != null)
						{
							d.width = Math.max(d.width, di.width);
							d.height = Math.max(d.height, di.height);
						}

						h = Math.max(h, d.height);
						aWidths[column] = Math.max(aWidths[column], d.width);
					}
				}

				if (aHeights != null)
				{
					aHeights[row] = h;
				}

				height += h;

				if (row > 0 && h > 0)
				{
					height += mVerGap;
				}
			}

			int width = 0;
			for (int w : aWidths)
			{
				if (w > 0 && width > 0)
				{
					width += mHorGap;
				}
				width += w;
			}

			Insets insets = aParent.getInsets();

			return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
		}
	}
}

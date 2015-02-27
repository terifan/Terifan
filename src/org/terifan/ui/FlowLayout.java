package org.terifan.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Arrays;
import javax.swing.JComponent;


/**
 * This layout combines a flow layout and grid layout.
 *
 * aaa | bb    | cccc
 * dd  | eeeee | f
 */
public class FlowLayout implements LayoutManager
{
	private int mHorGap;
	private int mVerGap;
	private int mColumns;
	private boolean mFillVertical;
	private double[] mColumnResizeWeight;
	private Insets[] mColumnMargin;
	private Insets[] mColumnPadding;
	private Dimension[] mColumnMinimumSize;


	public FlowLayout(int aColumns, int aHorGap, int aVerGap)
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


	public FlowLayout setColumnMinimumSize(int aColumnIndex, Dimension aDimension)
	{
		mColumnMinimumSize[aColumnIndex] = aDimension;
		return this;
	}


	public Dimension getColumnMinimumSize(int aColumnIndex)
	{
		return mColumnMinimumSize[aColumnIndex];
	}


	public FlowLayout setFillVertical(boolean aFillVertical)
	{
		mFillVertical = aFillVertical;
		return this;
	}


	public boolean isFillVertical()
	{
		return mFillVertical;
	}


	public FlowLayout setColumnResizeWeight(int aColumnIndex, double aWeight)
	{
		mColumnResizeWeight[aColumnIndex] = aWeight;
		return this;
	}


	public double getColumnResizeWeight(int aColumnIndex)
	{
		return mColumnResizeWeight[aColumnIndex];
	}


	public FlowLayout setColumnMargin(int aColumnIndex, Insets aInsets)
	{
		mColumnMargin[aColumnIndex] = aInsets;
		return this;
	}


	public Insets getColumnMargin(int aColumnIndex)
	{
		return mColumnMargin[aColumnIndex];
	}


	public FlowLayout setColumnPadding(int aColumnIndex, Insets aInsets)
	{
		mColumnPadding[aColumnIndex] = aInsets;
		return this;
	}


	public Insets getColumnPadding(int aColumnIndex)
	{
		return mColumnPadding[aColumnIndex];
	}


	@Override
	public void addLayoutComponent(String name, Component comp)
	{
	}


	@Override
	public void removeLayoutComponent(Component comp)
	{
	}


	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		return computeSize(parent, true, new int[mColumns], null);
	}


	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		return computeSize(parent, false, new int[mColumns], null);
	}


	@Override
	public void layoutContainer(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			Insets insets = parent.getInsets();
			int w = parent.getWidth();
			int h = parent.getHeight();

			int[] widths = new int[mColumns];
			int[] heights = new int[parent.getComponentCount() / mColumns];
			Dimension prefSize = computeSize(parent, true, widths, heights);

			int extraHeight = mFillVertical ? Math.max(0, (h - prefSize.height) / heights.length) : 0;
			int n = parent.getComponentCount();

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
					Component comp = parent.getComponent(i);

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


	private Dimension computeSize(Container parent, boolean aPreferred, int[] widths, int[] heights)
	{
		synchronized (parent.getTreeLock())
		{
			if ((parent.getComponentCount() % mColumns) != 0)
			{
				throw new IllegalStateException("Number of items must be dividable by the number of columns: item count=" + parent.getComponentCount() + ", columns=" + mColumns);
			}

			int n = parent.getComponentCount();
			int height = 0;

			for (int row = 0, i = 0; i < n; row++)
			{
				int h = 0;

				for (int column = 0; column < mColumns; column++, i++)
				{
					Component comp = parent.getComponent(i);

					if (comp.isVisible())
					{
						Dimension d = aPreferred ? comp.getPreferredSize() : comp.getMinimumSize();

//						if (comp instanceof JComponent)
//						{
//							Insets z = ((JComponent)comp).getInsets();
//							if (z != null)
//							{
//								d.width += z.left + z.right;
//								d.height += z.top + z.bottom;
//							}
//						}

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
						widths[column] = Math.max(widths[column], d.width);
					}
				}

				if (heights != null)
				{
					heights[row] = h;
				}

				height += h;

				if (row > 0 && h > 0)
				{
					height += mVerGap;
				}
			}

			int width = 0;
			for (int w : widths)
			{
				if (w > 0 && width > 0)
				{
					width += mHorGap;
				}
				width += w;
			}

			Insets insets = parent.getInsets();

			return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
		}
	}
}

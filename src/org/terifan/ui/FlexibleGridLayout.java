package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * This layout combines a flow layout and grid layout.
 *
 * aaa | bb    | cccc
 * dd  | eeeee | f
 */
public class FlexibleGridLayout implements LayoutManager
{
	private int mHorGap;
	private int mVerGap;
	private int mColumns;
	private boolean mFillVertical;
	private double[] mColumnResizeWeight;
	private Insets[] mColumnMargin;
	private Insets[] mColumnPadding;
	private Dimension[] mColumnMinimumSize;


	public FlexibleGridLayout(int aColumns, int aHorGap, int aVerGap)
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


	public FlexibleGridLayout setColumnMinimumSize(int aColumnIndex, Dimension aDimension)
	{
		mColumnMinimumSize[aColumnIndex] = aDimension;
		return this;
	}


	public Dimension getColumnMinimumSize(int aColumnIndex)
	{
		return mColumnMinimumSize[aColumnIndex];
	}


	public FlexibleGridLayout setFillVertical(boolean aFillVertical)
	{
		mFillVertical = aFillVertical;
		return this;
	}


	public boolean isFillVertical()
	{
		return mFillVertical;
	}


	public FlexibleGridLayout setColumnResizeWeight(int aColumnIndex, double aWeight)
	{
		mColumnResizeWeight[aColumnIndex] = aWeight;
		return this;
	}


	public double getColumnResizeWeight(int aColumnIndex)
	{
		return mColumnResizeWeight[aColumnIndex];
	}


	public FlexibleGridLayout setColumnMargin(int aColumnIndex, Insets aInsets)
	{
		mColumnMargin[aColumnIndex] = aInsets;
		return this;
	}


	public Insets getColumnMargin(int aColumnIndex)
	{
		return mColumnMargin[aColumnIndex];
	}


	public FlexibleGridLayout setColumnPadding(int aColumnIndex, Insets aInsets)
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
	public void layoutContainer(Container aParent)
	{
		synchronized (aParent.getTreeLock())
		{
			Insets insets = aParent.getInsets();
			int w = aParent.getWidth();
			int h = aParent.getHeight();

			int[] widths = new int[mColumns];
			int[] heights = new int[aParent.getComponentCount() / mColumns];
			Dimension prefSize = computeSize(aParent, true, widths, heights);

			int extraHeight = mFillVertical ? Math.max(0, (h - prefSize.height) / heights.length) : 0;
			int n = aParent.getComponentCount();

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
						if (rw > 0 && extraWidth > 0)
						{
							int s = (int)(rw * extraWidth / resizeWeight);
							resizeWeight -= rw;
							extraWidth -= s;
							cw += s;
						}

						Insets margin = mColumnMargin[column];

						if (margin != null)
						{
							comp.setBounds(x + margin.left, y + margin.top, cw - margin.left - margin.right, rh - margin.top - margin.bottom);
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


	public static void main(String ... args)
	{
		try
		{
			FlexibleGridLayout layout = new FlexibleGridLayout(3, 0, 0);
			layout.setColumnResizeWeight(0, 1);
			layout.setColumnResizeWeight(0, 5);
			layout.setColumnResizeWeight(0, 2);
			layout.setColumnMinimumSize(0, new Dimension(100, 0));
			layout.setFillVertical(true);

			JPanel panel = new JPanel(layout);
			panel.add(newLabel("aaaaaaaaaaa"));
			panel.add(newLabel("bbbbbbbbbb"));
			panel.add(newLabel("cccccccc"));
			panel.add(newLabel("ddddddddddddddddddddddd"));
			panel.add(newLabel("eeeee"));
			panel.add(newLabel("fffffffffffffffffff"));
			panel.add(newLabel("ggggggggggggggggggggggggggggggggg"));
			panel.add(newLabel("hhhhhhhh"));
			panel.add(newLabel("iiiiiii"));
			panel.add(newLabel("jjjjjjjjjjjjjjjjjjjjjjj"));
			panel.add(newLabel(""));
			panel.add(newLabel(""));

			JFrame frame = new JFrame();
			frame.add(panel);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	private static JLabel newLabel(String aText)
	{
		JLabel x = new JLabel(aText);
		x.setOpaque(true);
		x.setBackground(Color.YELLOW);
		x.setFont(new Font("arial",Font.PLAIN,20));
		x.setBorder(BorderFactory.createLineBorder(Color.RED));
		return x;
	}
}

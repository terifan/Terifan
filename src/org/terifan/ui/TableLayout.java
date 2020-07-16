package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.terifan.util.Strings;


public class TableLayout implements LayoutManager2
{
	private ArrayList<Component> mCurrentRow;
	private ArrayList<ArrayList<Component>> mComponents;
	private ArrayList<Integer> mColumnWidths;
	private ArrayList<Integer> mRowHeights;
	private int mWidth;
	private int mHeight;
	private int mSpacingX;
	private int mSpacingY;


	public TableLayout()
	{
		this(0, 0);
	}


	public TableLayout(int aSpacingX, int aSpacingY)
	{
		mColumnWidths = new ArrayList<>();
		mRowHeights = new ArrayList<>();
		mComponents = new ArrayList<>();
		mSpacingX = aSpacingX;
		mSpacingY = aSpacingY;

		mCurrentRow = new ArrayList<>();
		mComponents.add(mCurrentRow);
	}


	@Override
	public synchronized void addLayoutComponent(Component aComp, Object aConstraints)
	{
		mCurrentRow.add(aComp);
	}


	@Override
	public synchronized void addLayoutComponent(String aName, Component aComp)
	{
		mCurrentRow.add(aComp);
	}


	@Override
	public synchronized void removeLayoutComponent(Component aComp)
	{
		mComponents.forEach(e -> e.remove(aComp));
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return preferredLayoutSize(aParent);
	}


	@Override
	public Dimension maximumLayoutSize(Container aTarget)
	{
		return preferredLayoutSize(aTarget);
	}


	@Override
	public synchronized Dimension preferredLayoutSize(Container aParent)
	{
		return new Dimension(mWidth, mHeight);
	}


	@Override
	public float getLayoutAlignmentX(Container aTarget)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public float getLayoutAlignmentY(Container aTarget)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	public synchronized void invalidateLayout(Container aTarget)
	{
		ArrayList<Integer> columnWidths = new ArrayList<>();
		ArrayList<Integer> rowHeights = new ArrayList<>();

		int rowCount = getRowCount();
		int maxColumns = 0;

		for (int y = 0; y < rowCount; y++)
		{
			int rowHeight = 0;
			for (int x = 0; x < mComponents.get(y).size(); x++)
			{
				Component comp = mComponents.get(y).get(x);
				Dimension d = comp.getPreferredSize();
				rowHeight = Math.max(rowHeight, d.height);
				if (x == columnWidths.size())
				{
					columnWidths.add(d.width);
				}
				else
				{
					int w = Math.max(d.width, columnWidths.get(x));
					columnWidths.set(x, w);
				}
			}
			rowHeights.add(rowHeight);
			maxColumns = Math.max(maxColumns, mComponents.get(y).size());
		}

		mColumnWidths = columnWidths;
		mRowHeights = rowHeights;
		mWidth = columnWidths.stream().mapToInt(e -> e).sum() + mSpacingX * (maxColumns - 1);
		mHeight = rowHeights.stream().mapToInt(e -> e).sum() + mSpacingY * (rowCount - 1);

//		System.out.println(rowCount+"/"+mComponents.size());
//		System.out.println(mColumnWidths);
//		System.out.println(mRowHeights);
//		System.out.println(mWidth);
//		System.out.println(mHeight);
	}


	@Override
	public synchronized void layoutContainer(Container aParent)
	{
		ArrayList<Integer> columnWidths = mColumnWidths;
		ArrayList<Integer> rowHeights = mRowHeights;

		int rowCount = getRowCount();

		int v = 0;
		for (int y = 0; y < rowHeights.size() && y < rowCount; y++)
		{
			int u = 0;
			for (int x = 0; x < columnWidths.size() && x < mComponents.get(y).size(); x++)
			{
				Component comp = mComponents.get(y).get(x);
				comp.setBounds(u, v, columnWidths.get(x), rowHeights.get(y));
				u += columnWidths.get(x) + mSpacingX;
			}
			v += rowHeights.get(y) + mSpacingY;
		}
	}


	private int getRowCount()
	{
		int rowCount;
		for (rowCount = mComponents.size(); rowCount > 0 && mComponents.get(rowCount - 1).isEmpty(); rowCount--)
		{
		}
		return rowCount;
	}


	public synchronized void nextRow()
	{
		if (!mCurrentRow.isEmpty())
		{
			mCurrentRow = new ArrayList<>();
			mComponents.add(mCurrentRow);
		}
	}


	public static void main(String... args)
	{
		try
		{
			TableLayout layout = new TableLayout(5, 5);
			JPanel panel = new JPanel(layout);
			Random rnd = new Random(1);
			for (int r = 0; r < 10; r++)
			{
				for (int c = 0, n = rnd.nextInt(20); c < n; c++)
				{
					JLabel label = new JLabel("<-" + Strings.repeat("-", rnd.nextInt(10)) + "-" + c + "x" + r + "-" + Strings.repeat("-", rnd.nextInt(10)) + "->");
					label.setBackground(new Color(Color.HSBtoRGB(rnd.nextFloat(), 1f, 1f)));
					label.setOpaque(true);
					panel.add(label);
				}
				layout.nextRow();
			}

			JFrame frame = new JFrame();
			frame.add(new JScrollPane(panel));
//			frame.setSize(1024, 768);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
			System.exit(0);
		}
	}
}

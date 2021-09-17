package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class FairGridLayout implements LayoutManager
{
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
		return new Dimension(200, 100);
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(200, 100);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		synchronized (aParent.getTreeLock())
		{
			int n = aParent.getComponentCount();
			Dimension dim = aParent.getSize();

			int[] layout = new int[][]{
				{0},
				{1},
				{2},
				{3},
				{2,2},
				{3,2},
				{3,3},
				{4,3},
				{4,4},
				{5,4},
				{5,5},
				{6,5},
				{6,6},
				{7,6},
				{7,7},
				{5,5,5},
				{6,5,5},
				{6,6,5},
				{6,6,6},
				{7,6,6},
				{7,7,6},
				{7,7,7},
				{8,7,7},
				{8,8,7},
				{8,8,8}
			}[n];

			for (int row = 0, rows = layout.length, index = 0; row < rows; row++)
			{
				for (int col = 0, cols = layout[row]; col < cols; col++, index++)
				{
					int x1 = dim.width * (col + 0) / cols;
					int y1 = dim.height * (row + 0) / rows;
					int x2 = dim.width * (col + 1) / cols;
					int y2 = dim.height * (row + 1) / rows;

					aParent.getComponent(index).setBounds(x1, y1, x2 - x1, y2 - y1);
				}
			}
		}
	}


	public static void main(String ... args)
	{
		try
		{
			JPanel panel = new JPanel(new FairGridLayout());

			JFrame frame = new JFrame();
			frame.add(panel);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			for (int i = 0; i < 24; i++)
			{
				Thread.sleep(2000);

				JLabel lbl = new JLabel("" + i);
				lbl.setBackground(new Color(new Random().nextInt(0xffffff)));
				lbl.setOpaque(true);
				panel.add(lbl);
				panel.invalidate();
				panel.revalidate();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

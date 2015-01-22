package org.terifan.ui.listview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;


public class ListViewBar extends JComponent
{
	private ListView mListView;


	public ListViewBar(ListView aListView)
	{
		mListView = aListView;
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		if (mListView.getBarRenderer() != null)
		{
			super.paintComponent(g);

			mListView.getBarRenderer().render(mListView, (Graphics2D)g, 0, 0, getWidth(), 27);
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = new Dimension();

		if (mListView.getBarRenderer() != null)
		{
			d.height = 27;
		}

		return d;
	}
}
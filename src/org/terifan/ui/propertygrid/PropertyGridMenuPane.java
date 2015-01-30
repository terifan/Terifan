package org.terifan.ui.propertygrid;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.terifan.ui.StyleSheet;


public class PropertyGridMenuPane extends JPanel
{
	protected PropertyGrid mPropertyGrid;
	protected ToolbarButton mToolbarButtonCategory;
	protected ToolbarButton mToolbarButtonSorted;


	public PropertyGridMenuPane(PropertyGrid aPropertyGrid)
	{
		super(new FlowLayout(FlowLayout.LEFT,1,1));

		mPropertyGrid = aPropertyGrid;
		StyleSheet stylesheet = mPropertyGrid.getStylesheet();

		mToolbarButtonCategory = new ToolbarButton(stylesheet.getImage("category_icon"), PropertyGridOrder.CATEGORY);
		mToolbarButtonSorted = new ToolbarButton(stylesheet.getImage("sorted_icon"), PropertyGridOrder.SORTED);

		add(mToolbarButtonCategory);
		add(mToolbarButtonSorted);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		StyleSheet stylesheet = mPropertyGrid.getStylesheet();
		Graphics2D g = (Graphics2D)aGraphics;

		if (stylesheet.getBoolean("antialiasing_text", true))
		{
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		}

		GradientPaint paint = new GradientPaint(0f, 0f, stylesheet.getColor("toolbar_gradient_start"), 0f, 24f, stylesheet.getColor("toolbar_gradient_end"));

		g.setPaint(paint);
		g.fillRect(0, 0, getWidth(), 25);
		g.setColor(stylesheet.getColor("border_color"));
		g.drawLine(0, 25, getWidth(), 25);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(1, 26);
	}


	public class ToolbarButton extends JButton implements ActionListener
	{
		private BufferedImage mIcon;
		private PropertyGridOrder mOrder;

		public ToolbarButton(BufferedImage aIcon, PropertyGridOrder aOrder)
		{
			mOrder = aOrder;
			mIcon = aIcon;

			setOpaque(false);
			addActionListener(this);
		}

		@Override
		protected void paintComponent(Graphics aGraphics)
		{
			if (mPropertyGrid.getOrder() == mOrder)
			{
				StyleSheet stylesheet = mPropertyGrid.getStylesheet();
				aGraphics.setColor(stylesheet.getColor("toolbar_button_selected_background"));
				aGraphics.fillRect(0,0,23,22);
				aGraphics.setColor(stylesheet.getColor("toolbar_button_selected_border"));
				aGraphics.drawRect(0,0,23-1,22-1);
			}
			aGraphics.drawImage(mIcon, 3, 3, null);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(23, 22);
		}

		public void actionPerformed(ActionEvent aEvent)
		{
			mPropertyGrid.setOrder(mOrder);
			mPropertyGrid.redisplay();
		}
	}
}
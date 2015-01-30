package org.terifan.ui.propertygrid;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JComponent;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Anchor;
import org.terifan.ui.Utilities;


public class PropertyGridDescriptionPane extends JComponent
{
	protected PropertyGrid mPropertyGrid;
	protected Point mMouseClick;
	protected int mHeight;


	public PropertyGridDescriptionPane(PropertyGrid aPropertyGrid)
	{
		mPropertyGrid = aPropertyGrid;

		StyleSheet style = mPropertyGrid.getStylesheet();
		mHeight = style.getInt("description_initial_height");
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		StyleSheet style = mPropertyGrid.getStylesheet();
		Graphics2D g = (Graphics2D)aGraphics;

		g.setColor(style.getColor("description_background"));
		g.fillRect(0, 0, getWidth(), getHeight());

		if (mPropertyGrid.getSelectedProperty() != null)
		{
			Font titleFont = mPropertyGrid.getFont().deriveFont(Font.BOLD);

			String title = Utilities.clipString(mPropertyGrid.getSelectedProperty().getLabel(), titleFont, getWidth()-8);

			if (style.getBoolean("antialiasing_text", true))
			{
				Utilities.enableTextAntialiasing(aGraphics);
			}

			g.setFont(titleFont);
			g.setColor(getForeground());
			g.drawString(title, 4, 18);

			g.setFont(mPropertyGrid.getFont());
			g.setColor(getForeground());

			// TODO: ersätt med jeditorpane eller liknande
			mPropertyGrid.getTextRenderer().drawString(g, mPropertyGrid.getSelectedProperty().getDescription(), 4, 18+8, getWidth()-8, getHeight()-18-16+16, Anchor.NORTH_WEST, style.getColor("text_foreground"), style.getColor("description_background"), true);
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(1, mHeight);
	}
}
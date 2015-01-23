package org.terifan.ui.listview;

import java.awt.Graphics2D;
import org.terifan.ui.Icon;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;


public class ListViewBarRenderer
{
	private String mTitle;
	private Icon mIcon;


	public ListViewBarRenderer(String aTitle, Icon aIcon)
	{
		mTitle = aTitle;
		mIcon = aIcon;
	}


	public void render(ListView aListView, Graphics2D aGraphics, int x, int y, int w, int h)
	{
		Utilities.enableTextAntialiasing(aGraphics);
		StyleSheet style = aListView.getStylesheet();

		Utilities.drawScaledImage(aGraphics, style.getImage("barNormal"), x, y, w, h, 5, 5);

		mIcon.paintIcon(aListView, aGraphics, x+15-mIcon.getIconWidth()/2, y+13-mIcon.getIconHeight()/2);

		aGraphics.setFont(style.getFont("bar"));
		aGraphics.setColor(style.getColor("bar"));
		aGraphics.drawString(mTitle, x+31, y+18);
	}
}

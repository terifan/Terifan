package org.terifan.ui.listview;

import java.awt.Graphics2D;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;


public class ListViewGroupRenderer
{
	public void paintGroup(ListView aListView, Graphics2D aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight, ListViewGroup aGroup)
	{
		Utilities.enableTextAntialiasing(aGraphics);

		StyleSheet style = aListView.getStylesheet();

		if (aGroup.isSelected() && aListView.getRolloverGroup() == aGroup)
		{
			aGraphics.setColor(style.getColor("groupSelectedRolloverBackground"));
			aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
			aGraphics.setColor(style.getColor("groupSelectedRolloverForeground"));
		}
		else if (aGroup.isSelected())
		{
			aGraphics.setColor(style.getColor("groupSelectedBackground"));
			aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
			aGraphics.setColor(style.getColor("groupSelectedForeground"));
		}
		else if (aListView.getRolloverGroup() == aGroup)
		{
			aGraphics.setColor(style.getColor("groupRolloverBackground"));
			aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
			aGraphics.setColor(style.getColor("groupRolloverForeground"));
		}
		else
		{
			aGraphics.setColor(style.getColor("groupBackground"));
			aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
			aGraphics.setColor(style.getColor("groupForeground"));
		}

		int cnt = aGroup.getItemCount();
		int textX = aOriginX+20;
		int textY = aOriginY+25;

		ListViewColumn column = aListView.getModel().getColumn(aListView.getModel().getGroup(aGroup.getLevel()));
		String label = column.getLabel();
		Object value = aGroup.getGroupValue();
		String count = "("+cnt+" item"+(cnt!=1?"s":"")+")";

		if (column.getGroupFormatter() == null && column.getFormatter() != null)
		{
			value = column.getFormatter().format(value);
		}
		if (value == null)
		{
			value = "";
		}

		aGraphics.setFont(style.getFont("group"));
		aGraphics.drawString(label+": "+value+" "+count, textX, textY);

		aGraphics.setColor(style.getColor("groupHorizontalLine"));
		for (int i = 1, thickness=style.getInt("groupLineThickness"); i <= thickness; i++)
		{
			aGraphics.drawLine(aOriginX, aOriginY+aHeight-i, aOriginX+aWidth, aOriginY+aHeight-i);
		}

		if (aGroup.isCollapsed())
		{
			aGraphics.drawImage(style.getImage("expandButton"), aOriginX+3, aOriginY+15, null);
		}
		else
		{
			aGraphics.drawImage(style.getImage("collapseButton"), aOriginX+3, aOriginY+15, null);
		}
	}
}

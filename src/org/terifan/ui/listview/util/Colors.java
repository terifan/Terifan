package org.terifan.ui.listview.util;

import java.awt.Color;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.listview.SelectionMode;


public class Colors
{
	public static Color getTextForeground(StyleSheet aStyle, SelectionMode aSelectionMode, boolean aIsSorted, boolean aIsSelected, boolean aIsRollover, boolean aIsFocused, boolean aIsListViewFocused)
	{
		if (aIsSelected)
		{
			if (aIsRollover)
			{
				return aStyle.getColor("itemSelectedRolloverForeground");
			}
			else if (aIsListViewFocused || aStyle.getColor("itemSelectedUnfocusedForeground") == null)
			{
				return aStyle.getColor("itemSelectedForeground");
			}
			else
			{
				return aStyle.getColor("itemSelectedUnfocusedForeground");
			}
		}
		else if (aIsRollover)
		{
			return aStyle.getColor("itemRolloverForeground");
		}
		else if (aIsSorted)
		{
			return aStyle.getColor("itemSortedForeground");
		}
		else
		{
			return aStyle.getColor("itemForeground");
		}
	}


	public static Color getCellBackground(StyleSheet style, SelectionMode aSelectionMode, boolean aIsSorted, boolean aIsSelected, boolean aIsRollover, boolean aIsFocused, boolean aListViewFocused)
	{
		boolean b = aSelectionMode == SelectionMode.ITEM;

		if (aIsSelected && !b)
		{
			if (aIsRollover)
			{
				return style.getColor("itemSelectedRolloverBackground");
			}
			else if (aListViewFocused || style.getColor("itemSelectedUnfocusedBackground") == null)
			{
				return style.getColor("itemSelectedBackground");
			}
			else
			{
				return style.getColor("itemSelectedUnfocusedBackground");
			}
		}
		else if (aIsSorted && (!aIsSelected || b))
		{
			if (aIsRollover)
			{
				return style.getColor("itemSortedRolloverBackground");
			}
			else
			{
				return style.getColor("itemSortedBackground");
			}
		}
		else if (aIsRollover)
		{
			return style.getColor("itemRolloverBackground");
		}
		else
		{
			return style.getColor("itemBackground");
		}
	}


	public static Color getItemBackground(StyleSheet style, SelectionMode aSelectionMode, boolean aIsSortedColumn, boolean aIsSelected, boolean aIsRollover, boolean aIsFocused, boolean aListViewFocused)
	{
		if (aSelectionMode != SelectionMode.ITEM)
		{
			return null;
		}
		else if (aIsSelected)
		{
			if (aIsRollover)
			{
				return style.getColor("itemSelectedRolloverBackground");
			}
			else if (aListViewFocused || style.getColor("itemSelectedUnfocusedBackground") == null)
			{
				return style.getColor("itemSelectedBackground");
			}
			else
			{
				return style.getColor("itemSelectedUnfocusedBackground");
			}
		}
		else
		{
			return null;
		}
	}
}

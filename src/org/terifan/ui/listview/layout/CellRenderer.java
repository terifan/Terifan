package org.terifan.ui.listview.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JComponent;
import org.terifan.forms.Icon;
import org.terifan.ui.StyleSheet;
import org.terifan.data.cache.Cache;
import org.terifan.graphics.Utilities;
import org.terifan.ui.listview.ListView;
import org.terifan.ui.listview.ListViewCellRenderer;
import org.terifan.ui.listview.ListViewColumn;
import org.terifan.ui.listview.ListViewItem;
import org.terifan.ui.listview.SelectionMode;
import org.terifan.ui.listview.util.Colors;


public class CellRenderer extends JComponent implements ListViewCellRenderer
{
	private Rectangle mTempRectangle = new Rectangle();

	protected ListView mListView;
	protected ListViewItem mItem;
	protected boolean mIsSelected;
	protected boolean mIsFocused;
	protected boolean mIsRollover;
	protected boolean mIsSorted;
	protected int mColumnIndex;
	protected Cache<Icon,Image> mCachedHighlightedIcons = new Cache<Icon,Image>(100);
	protected FontMetrics mFontMetrics;
	protected int mIconTextSpacing;


	public CellRenderer()
	{
		setIconTextSpacing(4);
		setOpaque(true);
	}


	@Override
	public JComponent getListViewCellRendererComponent(ListView aListView, ListViewItem aItem, int aColumnIndex, boolean aIsSelected, boolean aIsFocused, boolean aIsRollover, boolean aIsSorted)
	{
		mListView = aListView;
		mItem = aItem;
		mIsSelected = aIsSelected;
		mIsFocused = aIsFocused;
		mIsRollover = aIsRollover;
		mIsSorted = aIsSorted;
		mColumnIndex = aColumnIndex;

		return this;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		StyleSheet style = mListView.getStylesheet();
		Font font = style.getFont("item");
		SelectionMode selectionMode = mListView.getSelectionMode();

		aGraphics.setFont(font);
		mFontMetrics = aGraphics.getFontMetrics(font);

		ListViewColumn column = mListView.getModel().getColumn(mColumnIndex);

		Rectangle rect = getBounds();
		Rectangle tr = mTempRectangle;

		Object value = mItem.getValue(mColumnIndex);
		if (column.getFormatter() != null)
		{
			value = column.getFormatter().format(value);
		}
		if (value == null)
		{
			value = " ";
		}

		String s = computeLabelRect(column, value.toString(), mColumnIndex, rect.x, rect.y, rect.width, rect.height, true, tr);

		if (mIsSelected && !column.isFocusable() && selectionMode != SelectionMode.ROW && selectionMode != SelectionMode.SINGLE_ROW)
		{
			mIsSelected = false;
		}

		Color cellBackground = Colors.getCellBackground(mListView.getStylesheet(), mListView.getSelectionMode(), mIsSorted, mIsSelected, mIsRollover, mIsFocused, true);
		Color itemBackground = Colors.getItemBackground(mListView.getStylesheet(), mListView.getSelectionMode(), mIsSorted, mIsSelected, mIsRollover, mIsFocused, true);
		Color textForeground = Colors.getTextForeground(mListView.getStylesheet(), mListView.getSelectionMode(), mIsSorted, mIsSelected, mIsRollover, mIsFocused, true);

		Color background = itemBackground != null ? itemBackground : cellBackground != null ? cellBackground : mListView.getBackground();

		if (cellBackground != null && (mIsSorted || selectionMode != SelectionMode.ROW && selectionMode != SelectionMode.SINGLE_ROW))
		{
			aGraphics.setColor(cellBackground);
			aGraphics.fillRect(rect.x, rect.y, rect.width, rect.height+1);
		}

		int tx = tr.x+2+column.getIconWidth()+computeIconTextSpacing(column);
		int ty = tr.y - 1 + tr.height - mFontMetrics.getDescent();
		int rx = tr.x+column.getIconWidth()+computeIconTextSpacing(column);
		int ry = rect.y;//tr.y;
		int rw = tr.width-column.getIconWidth()-computeIconTextSpacing(column)+1;
		int rh = rect.height;//tr.height;
		tx -= rx;
		ty -= ry;

		mListView.getTextRenderer().drawString(aGraphics, background, textForeground, s, rx, ry, rw, rh, tx, ty);

		aGraphics.setColor(style.getColor("verticalLine"));
		for (int i = 1, thickness=style.getInt("itemVerticalLineThickness"); i <= thickness; i++)
		{
			aGraphics.drawLine(rect.x+rect.width-i, rect.y, rect.x+rect.width-i, rect.y+rect.height-1);
		}

//		if (mIsRollover)
//		{
//			aGraphics.setColor(style.getColor("line"));
//			aGraphics.drawLine(rect.x, rect.y, rect.x+rect.width, rect.y);
//			aGraphics.drawLine(rect.x, rect.y+rect.height-2, rect.x+rect.width, rect.y+rect.height-2);
//		}

		Icon icon = mItem.getIcon(mColumnIndex);

		if (icon != null && column.getIconWidth() > 0)
		{
			double f = Math.min(column.getIconWidth()/(double)icon.getIconWidth(), rh/(double)icon.getIconHeight());
			int iw = (int)(f*icon.getIconWidth());
			int ih = (int)(f*icon.getIconHeight());

			int ix = tr.x+2+(column.getIconWidth()-iw) / 2;
			int iy = rect.y + (rect.height - ih) / 2;

			/*if (mIsSelected)
			{
				Image image;

				if (mCachedHighlightedIcons.containsKey(icon))
				{
					image = mCachedHighlightedIcons.get(icon);
				}
				else
				{
					image = Utilities.filterHighlight(((ImageIcon)icon).getImage(), style.getColor("itemSelectedBackground"));
					mCachedHighlightedIcons.put(icon, image, 1);
				}

				aGraphics.drawImage(image, ix, iy, null);
			}
			else*/
			{
				icon.paintIcon(null, aGraphics, ix, iy, iw, ih);
			}
		}

		if (mIsFocused) // && mListView.hasFocus())
		{
			if (selectionMode == SelectionMode.ITEM)
			{
				paintFocusRectangle(aGraphics, rx, ry, rw, rh-1);
			}
			else
			{
				paintFocusRectangle(aGraphics, rect.x, rect.y, rect.width, rect.height-1);
			}
		}
	}


	protected String computeLabelRect(ListViewColumn column, String value, int col, int x, int y, int w, int h, boolean aIncludeIcon, Rectangle aDestRectangle)
	{
		String s = Utilities.clipString(value.toString(), mFontMetrics, Math.max(w - 4 - computeIconTextSpacing(column) - column.getIconWidth(), 1));
		int sw = mFontMetrics.stringWidth(s);

		switch (column.getAlignment())
		{
			case LEFT:
				break;
			case CENTER:
				x += Math.max(0, (w - (sw + column.getIconWidth() + computeIconTextSpacing(column))) / 2);
				break;
			case RIGHT:
				x += Math.max(0, w - sw - column.getIconWidth() - computeIconTextSpacing(column) - 5);
				break;
			default:
				throw new RuntimeException("Unsupported alignment: " + column.getAlignment());
		}

		if (aIncludeIcon)
		{
			aDestRectangle.x = x;
			aDestRectangle.width = sw + column.getIconWidth() + computeIconTextSpacing(column) + 3;
		}
		else
		{
			aDestRectangle.x = x + column.getIconWidth() + computeIconTextSpacing(column);
			aDestRectangle.width = sw + 3;
		}

		aDestRectangle.y = y + (h - mFontMetrics.getHeight()) / 2;
		aDestRectangle.height = mFontMetrics.getHeight()+1;

		aDestRectangle.y = Math.max(aDestRectangle.y, y);
		aDestRectangle.height = Math.min(aDestRectangle.y+aDestRectangle.height, y+h)-aDestRectangle.y;

		return s;
	}


	protected void paintFocusRectangle(Graphics aGraphics, int x, int y, int w, int h)
	{
		aGraphics.setColor(mListView.getStylesheet().getColor("focusRect"));
		Utilities.drawDottedRect(aGraphics, x, y, w, h, false);
	}


	public void setIconTextSpacing(int aIconTextSpacing)
	{
		mIconTextSpacing = aIconTextSpacing;
	}


	public int getIconTextSpacing()
	{
		return mIconTextSpacing;
	}


	protected int computeIconTextSpacing(ListViewColumn aColumn)
	{
		return aColumn.getIconWidth() > 0 ? mIconTextSpacing : 0;
	}
}
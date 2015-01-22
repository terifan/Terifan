package org.terifan.ui.listview;

import java.awt.Color;
import org.terifan.util.SortedMap;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.terifan.ui.Orientation;
import org.terifan.ui.StyleSheet;
import org.terifan.util.log.Log;


public class ListViewLayoutVertical extends AbstractListViewLayout
{
	protected Dimension mPreferredSize;
	protected Dimension mMinimumSize;
	protected int mMaxItemsPerRow;


	public ListViewLayoutVertical(ListView aListView, int aMaxItemsPerRow)
	{
		mListView = aListView;
		mMaxItemsPerRow = aMaxItemsPerRow;
		mPreferredSize = new Dimension(1, 1);
	}


	@Override
	public Orientation getLayoutOrientation()
	{
		return Orientation.VERTICAL;
	}


	@Override
	public void paint(Graphics2D aGraphics)
	{
		if (mListView.getModel() == null)
		{
			throw new IllegalStateException("ListView has no model");
		}

		StyleSheet style = mListView.getStylesheet();

		aGraphics.setColor(style.getColor("itemBackground"));
		aGraphics.fillRect(0, 0, mListView.getWidth(), mListView.getHeight());
		aGraphics.setColor(style.getColor("itemForeground"));

		ListViewGroup root = mListView.getModel().getRoot();

		int y = 0;

		SortedMap<Object, ListViewGroup> children = root.getChildren();

		if (children != null)
		{
			int groupHeight = mListView.getStylesheet().getInt("groupHeight");

			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);

				y = paintList(aGraphics, group, 0, y) + groupHeight;
			}
		}
		else
		{
			paintItemList(aGraphics, root, 0, y);
		}
	}


	private int paintList(Graphics2D aGraphics, ListViewGroup aGroup, int aLevel, int aOriginY)
	{
		int groupHeight = mListView.getStylesheet().getInt("groupHeight");
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");

		Rectangle clip = aGraphics.getClipBounds();

		int height = aGroup.isCollapsed() ? 0 : getGroupHeight(aGroup);

		if (clip.y <= aOriginY + height + groupHeight && clip.y + clip.height >= aOriginY)
		{
			mListView.getGroupRenderer().paintGroup(mListView, aGraphics, verticalBarWidth * aLevel, aOriginY, mListView.getWidth() - verticalBarWidth * aLevel, groupHeight, aGroup);

			if (!aGroup.isCollapsed())
			{
				SortedMap<Object, ListViewGroup> children = aGroup.getChildren();

				if (children != null)
				{
					paintVerticalBar(aGraphics, verticalBarWidth * aLevel, aOriginY + groupHeight, verticalBarWidth, height);

					int y = aOriginY;

					for (Object key : children.getKeys())
					{
						ListViewGroup group = children.get(key);

						y = paintList(aGraphics, group, aLevel + 1, y + groupHeight);
					}
				}
				else
				{
					paintItemList(aGraphics, aGroup, aLevel, aOriginY + groupHeight);
				}
			}
		}

		return aOriginY + height;
	}


	private void paintVerticalBar(Graphics2D aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight)
	{
		StyleSheet style = mListView.getStylesheet();

		aGraphics.setColor(style.getColor("indent"));
		aGraphics.fillRect(aOriginX, aOriginY, aWidth, aHeight);
		aGraphics.setColor(style.getColor("indentLine"));
		aGraphics.drawLine(aOriginX + aWidth - 1, aOriginY, aOriginX + aWidth - 1, aOriginY + aHeight - 1);
		aGraphics.drawLine(aOriginX, aOriginY + aHeight - 1, aOriginX + aWidth - 1, aOriginY + aHeight - 1);
	}


	private void paintItemList(Graphics2D aGraphics, ListViewGroup aGroup, int aLevel, int aOriginY)
	{
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");

		ArrayList<ListViewItem> items = aGroup.getItems();
		int y = aOriginY;
		int itemsPerRow = getItemsPerRun();
		double itemWidth = getItemWidth();

		Rectangle clip = aGraphics.getClipBounds();
		ListViewItemRenderer renderer = mListView.getItemRenderer();

		for (int itemIndex = 0, itemCount = items.size(); itemIndex < itemCount;)
		{
			int rowHeight = 0;
			for (int itemRowIndex = 0, _itemIndex = itemIndex; _itemIndex < itemCount && itemRowIndex < itemsPerRow; _itemIndex++, itemRowIndex++)
			{
				ListViewItem item = items.get(_itemIndex);
				int itemHeight = renderer.getItemHeight(mListView, item);
				rowHeight = Math.max(rowHeight, itemHeight);
			}

			if (y >= clip.y + clip.height)
			{
				break;
			}
			else if (clip.y <= y + rowHeight)
			{
				double x = verticalBarWidth * aLevel;
				double error = 0;

				for (int itemRowIndex = 0; itemIndex < itemCount && itemRowIndex < itemsPerRow; itemIndex++, itemRowIndex++)
				{
					ListViewItem item = items.get(itemIndex);

					item.loadState();

//					int itemHeight = renderer.getItemHeight(mListView, item);

					int tmpWidth = (int)(itemWidth + error);

					renderer.paintItem(aGraphics, (int)x, y, tmpWidth, rowHeight, mListView, item);

					x += tmpWidth;
					error += itemWidth - tmpWidth;
				}
			}
			else
			{
				itemIndex += itemsPerRow;
			}

			y += rowHeight;
		}
	}


	@Override
	public LocationInfo getLocationInfo(int aLocationX, int aLocationY)
	{
		SortedMap<Object, ListViewGroup> children = mListView.getModel().getRoot().getChildren();

		if (children != null)
		{
			int groupHeight = mListView.getStylesheet().getInt("groupHeight");
			AtomicInteger y = new AtomicInteger(0);

			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);

				LocationInfo result = getComponentAtImpl(aLocationX, aLocationY, y, group, 0);

				if (result != null)
				{
					return result;
				}
				if (y.get() > aLocationY + groupHeight)
				{
					break;
				}
			}

			return null;
		}
		else
		{
			return getComponentAtImplPoint(mListView.getModel().getRoot(), 0, 0, aLocationX, aLocationY);
		}
	}


	private LocationInfo getComponentAtImpl(int aLocationX, int aLocationY, AtomicInteger aOriginY, ListViewGroup aGroup, int aLevel)
	{
		int groupHeight = mListView.getStylesheet().getInt("groupHeight");
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");
		int height = aGroup.isCollapsed() ? 0 : getGroupHeight(aGroup);

		if (aLocationX > aGroup.getLevel() * verticalBarWidth && aLocationX < mListView.getWidth())
		{
			if (aLocationY >= aOriginY.get() && aLocationY < aOriginY.get() + groupHeight)
			{
				LocationInfo info = new LocationInfo();
				info.setGroup(aGroup);
				info.setGroupButton(aLocationX >= aGroup.getLevel() * verticalBarWidth + 3 && aLocationX < aGroup.getLevel() * verticalBarWidth + 3 + 11);
				return info;
			}
			else
			{
				aOriginY.addAndGet(groupHeight);

				if (aLocationY >= aOriginY.get() && aLocationY < aOriginY.get() + height)
				{
					SortedMap<Object, ListViewGroup> children = aGroup.getChildren();

					if (!aGroup.isCollapsed())
					{
						if (children != null)
						{
							for (Object key : children.getKeys())
							{
								ListViewGroup group = children.get(key);
								LocationInfo info = getComponentAtImpl(aLocationX, aLocationY, aOriginY, group, aLevel + 1);

								if (info != null)
								{
									return info;
								}
							}
						}
						else
						{
							LocationInfo info = getComponentAtImplPoint(aGroup, aLevel, aOriginY.get(), aLocationX, aLocationY);

							if (info != null)
							{
								return info;
							}
						}
					}
				}
			}
		}

		aOriginY.addAndGet(height);

		return null;
	}


	// TODO: prettify
	private LocationInfo getComponentAtImplPoint(ListViewGroup aGroup, int aLevel, int aOriginY, int aLocationX, int aLocationY)
	{
		if (aLocationX < 0 || aLocationX >= mListView.getWidth())
		{
			return null;
		}

		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");

		double x = aLocationX - verticalBarWidth * aLevel;
		int y = aLocationY - aOriginY;

		if (y < 0)
		{
			return null;
		}

		ListViewItemRenderer renderer = mListView.getItemRenderer();
		ArrayList<ListViewItem> items = aGroup.getItems();
		double itemWidth = getItemWidth();
		int itemsPerRow = getItemsPerRun();
		int tempHeight = 0;
		int itemX = 0;
		int row = 0;
		int col = -1;

		for (int i = 0, sz = items.size(); i < sz; i++)
		{
			tempHeight = Math.max(tempHeight, renderer.getItemHeight(mListView, items.get(i)));

			if (++itemX == itemsPerRow || i + 1 == sz)
			{
				y -= tempHeight;

				if (y < 0)
				{
					i -= itemX - 1;
					for (int j = 0; j < itemX; j++, i++)
					{
						x -= itemWidth;
						if (x < 0)
						{
							if (renderer.getItemHeight(mListView, items.get(i)) >= y + tempHeight)
							{
								col = j;
							}
							break;
						}
					}

					break;
				}

				tempHeight = 0;
				itemX = 0;
				row++;
			}
		}

		int index = row * itemsPerRow + col;

		if (col > -1 && col < itemsPerRow && index >= 0 && index < aGroup.getItems().size())
		{
			LocationInfo info = new LocationInfo();
			info.setItem(aGroup.getItems().get(index));
			return info;
		}

		return null;
	}


	private int getGroupHeight(ListViewGroup aGroup)
	{
		return getGroupHeight(aGroup, getItemsPerRun());
	}


	private int getGroupHeight(ListViewGroup aGroup, int aItemsPerRow)
	{
		SortedMap<Object, ListViewGroup> children = aGroup.getChildren();

		if (children != null)
		{
			int groupHeight = mListView.getStylesheet().getInt("groupHeight");
			int height = 0;

			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);

				if (group.isCollapsed())
				{
					height += groupHeight;
				}
				else
				{
					height += groupHeight + getGroupHeight(group, aItemsPerRow);
				}
			}

			return height;
		}
		else
		{
			ListViewItemRenderer renderer = mListView.getItemRenderer();
			ArrayList<ListViewItem> items = aGroup.getItems();
			int height = 0;
			int tempHeight = 0;
			int itemX = 0;

			for (ListViewItem item : items)
			{
				tempHeight = Math.max(tempHeight, renderer.getItemHeight(mListView, item));
				if (++itemX == aItemsPerRow)
				{
					height += tempHeight;
					tempHeight = 0;
					itemX = 0;
				}
			}
			height += tempHeight;

			return height;
		}
	}


	@Override
	public int getItemsPerRun()
	{
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");
		int verticalIndent = verticalBarWidth * Math.max(0, mListView.getModel().getGroupCount() - 1);
		int windowWidth = mListView.getWidth() - verticalIndent;
		int prefItemWidth = Math.max(mListView.getItemRenderer().getItemPreferredWidth(mListView), 1);

		return Math.min(Math.max(1, windowWidth / prefItemWidth), mMaxItemsPerRow <= 0 ? 1 << 30 : mMaxItemsPerRow);
	}


	public double getItemWidth()
	{
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");
		int verticalIndent = verticalBarWidth * Math.max(0, mListView.getModel().getGroupCount() - 1);
		int windowWidth = mListView.getWidth() - verticalIndent;

//		int itemMinWidth = mListView.getItemRenderer().getItemMinimumWidth();
//		int itemMaxWidth = mListView.getItemRenderer().getItemMaximumWidth();
//		return Math.max(Math.min(windowWidth / (double)getItemsPerRun(), itemMaxWidth), itemMinWidth);
		return windowWidth / (double)getItemsPerRun();
	}


	@Override
	public int getMarginLeft()
	{
		int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");

		return verticalBarWidth * Math.max(mListView.getModel().getGroupCount() - 1, 0);
	}


	@Override
	public Dimension getPreferredSize()
	{
		ListViewGroup root = mListView.getModel().getRoot();

		int height = getGroupHeight(root);

		mPreferredSize = new Dimension(mListView.getItemRenderer().getItemMinimumWidth(mListView), height + 10);

		return mPreferredSize;
	}


	@Override
	public Dimension getMinimumSize()
	{
		mMinimumSize = new Dimension(mListView.getItemRenderer().getItemMinimumWidth(mListView), 0);

		return mMinimumSize;
	}


	@Override
	public ListViewItem getItemRelativeTo(ListViewItem aItem, int aDiffX, int aDiffY)
	{
		if (aItem == null)
		{
			throw new IllegalArgumentException("aItem is null");
		}
		if (aDiffX != 0 && aDiffY != 0)
		{
			throw new IllegalArgumentException("Motion only in one direction allowed.");
		}

		ListViewGroup containingGroup = mListView.getModel().getRoot().findContainingGroup(aItem);

		if (containingGroup == null)
		{
			return null;
//			throw new RuntimeException("Failed to find containing group, aItem: " + aItem + ", aDiffX: " + aDiffX + ", aDiffY: "+aDiffY);
		}

		int itemsPerRun = getItemsPerRun();

		int oldIndex = containingGroup.getItems().indexOf(aItem);

		int newIndexTmp = (oldIndex - (oldIndex % itemsPerRun)) + Math.max(0, Math.min(itemsPerRun - 1, (oldIndex % itemsPerRun) + aDiffX));

		int newIndex = aDiffY * itemsPerRun + newIndexTmp;

		if (aDiffX != 0 || containingGroup == mListView.getModel().getRoot())
		{
			if (newIndex < 0 || newIndex >= containingGroup.getItems().size())
			{
				return null;
			}
		}
		else
		{
			if (newIndex < 0)
			{
				ListViewGroup siblingGroup = containingGroup.getSiblingGroup(-1, true);

				if (siblingGroup == null)
				{
					return null;
				}

				int ic = siblingGroup.getItemCount();

				newIndex = (ic == itemsPerRun ? 0 : (ic - (ic % itemsPerRun))) + (oldIndex % itemsPerRun);

				if (newIndex >= ic && ic > itemsPerRun)
				{
					newIndex -= itemsPerRun;
				}

				newIndex = Math.min(newIndex, ic - 1);

				containingGroup = siblingGroup;
			}
			else if (newIndex >= containingGroup.getItems().size())
			{
				ListViewGroup siblingGroup = containingGroup.getSiblingGroup(+1, true);

				if (siblingGroup == null)
				{
					return null;
				}

				newIndex = Math.min(oldIndex % itemsPerRun, siblingGroup.getItemCount() - 1);

				containingGroup = siblingGroup;
			}
		}

		return containingGroup.getItems().get(newIndex);
	}


	@Override
	public ArrayList<ListViewItem> getItemsIntersecting(ListViewItem aFromItem, ListViewItem aToItem)
	{
		ArrayList<ListViewItem> list = new ArrayList<ListViewItem>();

		Rectangle r1 = new Rectangle();
		Rectangle r2 = new Rectangle();

		if (getItemBounds(aFromItem, r1))
		{
			if (getItemBounds(aToItem, r2))
			{
				r1.add(r2);

				getItemsIntersectingImpl(r1.x + 1, r1.y + 1, r1.x + r1.width - 2, r1.y + r1.height - 2, list, mListView.getModel().getRoot(), 0);
			}
		}

		return list;
	}


	@Override
	public ArrayList<ListViewItem> getItemsIntersecting(int x1, int y1, int x2, int y2, ArrayList<ListViewItem> aList)
	{
		if (aList == null)
		{
			aList = new ArrayList<ListViewItem>();
		}

		if (y2 < y1)
		{
			int t = y1;
			y1 = y2;
			y2 = t;
		}

		getItemsIntersectingImpl(x1, y1, x2, y2, aList, mListView.getModel().getRoot(), 0);

		return aList;
	}


	private void getItemsIntersectingImpl(int x1, int y1, int x2, int y2, ArrayList<ListViewItem> aList, ListViewGroup aGroup, int aOffsetY)
	{
		SortedMap<Object, ListViewGroup> children = aGroup.getChildren();

		if (children != null)
		{
			int groupHeight = mListView.getStylesheet().getInt("groupHeight");

			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);

				int height = getGroupHeight(group);

				aOffsetY += groupHeight;

				if (!group.isCollapsed())
				{
					if (y2 > aOffsetY && y1 < aOffsetY + height)
					{
						getItemsIntersectingImpl(x1, y1, x2, y2, aList, group, aOffsetY);
					}

					aOffsetY += height;
				}
			}
		}
		else
		{
			int itemsPerRun = getItemsPerRun();

			int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");
			int verticalIndent = verticalBarWidth * Math.max(0, mListView.getModel().getGroupCount() - 1);
			double itemWidth = getItemWidth();

			x1 = Math.max(0, Math.min(itemsPerRun - 1, (int)((x1 - verticalIndent) / itemWidth)));
			x2 = Math.max(0, Math.min(itemsPerRun - 1, (int)((x2 - verticalIndent) / itemWidth)));

			ListViewItemRenderer renderer = mListView.getItemRenderer();
			ArrayList<ListViewItem> items = aGroup.getItems();
			int localY = 0;
			int rowHeight = 0;
			int itemX = 0;
			int itemY = 0;

			for (int i = 0; i < items.size(); i++)
			{
				rowHeight = Math.max(rowHeight, renderer.getItemHeight(mListView, items.get(i)));

				if (++itemX == itemsPerRun || i == items.size() - 1)
				{
					if (y2 >= (aOffsetY + localY) && y1 < (aOffsetY + localY + rowHeight))
					{
						int min = itemY * itemsPerRun + x1;
						int max = itemY * itemsPerRun + x2;

						if (min > max)
						{
							int t = max;
							max = min;
							min = t;
						}
						max = Math.min(max, items.size() - 1);

						for (int j = min; j <= max; j++)
						{
							aList.add(items.get(j));
						}
					}

					localY += rowHeight;
					rowHeight = 0;
					itemX = 0;
					itemY++;
				}
			}
		}
	}


	@Override
	public boolean getItemBounds(ListViewItem aItem, Rectangle aRectangle)
	{
		return getItemBoundsImpl(aItem, aRectangle, mListView.getModel().getRoot(), 0);
	}


	private boolean getItemBoundsImpl(ListViewItem aItem, Rectangle aRectangle, ListViewGroup aGroup, int aOffsetY)
	{
		SortedMap<Object, ListViewGroup> children = aGroup.getChildren();

		if (children != null)
		{
			int groupHeight = mListView.getStylesheet().getInt("groupHeight");

			for (Object key : children.getKeys())
			{
				ListViewGroup group = children.get(key);

				int height = getGroupHeight(group);

				aOffsetY += groupHeight;

				if (!group.isCollapsed())
				{
					if (getItemBoundsImpl(aItem, aRectangle, group, aOffsetY))
					{
						return true;
					}

					aOffsetY += height;
				}
			}

			return false;
		}
		else
		{
			int itemIndex = aGroup.getItems().indexOf(aItem);

			if (itemIndex == -1)
			{
				return false;
			}

			int itemsPerRun = getItemsPerRun();

			int verticalBarWidth = mListView.getStylesheet().getInt("verticalBarWidth");
			int verticalIndent = verticalBarWidth * Math.max(0, mListView.getModel().getGroupCount() - 1);
			double itemWidth = getItemWidth();

			int row = itemIndex / itemsPerRun;

			int y = 0;
			int height = 0;

			for (int i = 0; i <= row; i++)
			{
				y += height;
				height = getRowHeight(aGroup, i * itemsPerRun);
			}

			aRectangle.x = (int)(itemWidth * (itemIndex % itemsPerRun)) + verticalIndent;
			aRectangle.y = aOffsetY + y;
			aRectangle.width = (int)itemWidth;
			aRectangle.height = height;

			return true;
		}
	}


	private int getRowHeight(ListViewGroup aGroup, int aItemIndex)
	{
		int itemsPerRun = getItemsPerRun();
		ArrayList<ListViewItem> items = aGroup.getItems();
		ListViewItemRenderer renderer = mListView.getItemRenderer();
		int height = 0;

		for (int i = aItemIndex - (aItemIndex % itemsPerRun), j = i; j < i + itemsPerRun; j++)
		{
			height = Math.max(height, renderer.getItemHeight(mListView, items.get(i)));
		}

		return height;
	}
}

package org.terifan.ui.listview;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import javax.swing.SwingUtilities;


class ListViewMouseListener<T extends ListViewItem> extends MouseAdapter implements MouseMotionListener
{
	private HashSet<T> mSelectedItemsClone;
	private Rectangle mTempScrollRect = new Rectangle();
	private ListView<T> mListView;
	private Point mDragStart;
	private boolean mIsControlDown;
	private boolean mIsShiftDown;
	private boolean mPopupTriggered;


	public ListViewMouseListener(ListView<T> aListView)
	{
		mListView = aListView;
		mDragStart = new Point();
		mSelectedItemsClone = new HashSet<>();
	}


	@Override
	public void mousePressed(MouseEvent aEvent)
	{
		mListView.requestFocus();

		if (mListView.getSelectionMode() != SelectionMode.NONE)
		{
			mIsControlDown = aEvent.isControlDown();
			mIsShiftDown = aEvent.isShiftDown();
			mDragStart.setLocation(aEvent.getX(), aEvent.getY());

			mSelectedItemsClone.clear();
			if (mIsControlDown)
			{
				mSelectedItemsClone.addAll(mListView.getSelectedItems());
			}

			process(aEvent, false);

			// use modulo to avoid tripple clicks to be regarded as two double clicks etc.
			if (SwingUtilities.isLeftMouseButton(aEvent) && (aEvent.getClickCount() % 2) == 0)
			{
				mListView.fireSelectionAction(new ListViewEvent(mListView, aEvent));
			}
		}

		if (!aEvent.isPopupTrigger())
		{
			mPopupTriggered = false;
		}
	}


	@Override
	public void mouseReleased(MouseEvent aEvent)
	{
		if (!mListView.getSelectionRectangle().isEmpty())
		{
			mListView.getSelectionRectangle().setSize(0, 0);
			mListView.repaint();
		}

		mSelectedItemsClone.clear();

		if (mPopupTriggered)
		{
			mousePressed(aEvent);
		}

		if (aEvent.isPopupTrigger())
		{
			mPopupTriggered = mListView.firePopupMenu(aEvent.getPoint());
		}
	}


	@Override
	public void mouseMoved(MouseEvent aEvent)
	{
		if (mListView.getRolloverEnabled())
		{
			mListView.updateRollover(aEvent.getPoint());
		}
	}


	@Override
	public void mouseExited(MouseEvent aEvent)
	{
		if (mListView.getRolloverEnabled())
		{
			mListView.updateRollover(aEvent.getPoint());
		}
	}


	@Override
	public void mouseDragged(MouseEvent aEvent)
	{
		if (!mIsShiftDown && mListView.getSelectionMode() != SelectionMode.SINGLE_ROW && mListView.getSelectionMode() != SelectionMode.NONE && SwingUtilities.isLeftMouseButton(aEvent))
		{
			process(aEvent, true);
		}
	}


	private void process(MouseEvent aEvent, boolean aDragged)
	{
		int x = aEvent.getX();
		int y = aEvent.getY();

		if (aDragged)
		{
			int width = Math.abs(mDragStart.x - x);
			int height = Math.abs(mDragStart.y - y);

			if (Math.abs(width) > 3 || Math.abs(height) > 3)
			{
				mListView.getSelectionRectangle().setLocation(Math.min(mDragStart.x, x), Math.min(mDragStart.y, y));
				mListView.getSelectionRectangle().setSize(width, height);
			}
		}

		ListViewLayout<T> layout = mListView.getListViewLayout();

		LocationInfo<T> info = layout.getLocationInfo(x, y);
		boolean isItem = info != null && info.isItem();
		T selectedItem = info == null ? null : info.getItem();

		// click on expand/collapse button
		if (!aDragged && info != null && info.isGroup() && info.isGroupButton())
		{
			info.getGroup().setCollapsed(info.getGroup().isCollapsed());
			mListView.revalidate();
			mListView.repaint();
			return;
		}

		// click on group
		if (!aDragged && info != null && info.isGroup())
		{
			info.getGroup().setSelected(!info.getGroup().isSelected());
			mListView.revalidate();
			mListView.repaint();
			mListView.fireSelectionChanged(new ListViewEvent(mListView, aEvent));
			return;
		}

		boolean changed = false;

		if (mIsShiftDown)
		{
			if (!aDragged)
			{
				mListView.setItemsSelected(false);
				changed = true;
			}
			if (isItem)
			{
				for (T item : layout.getItemsIntersecting(mListView.getAnchorItem(), selectedItem))
				{
					mListView.setItemSelected(item, true);
				}
				changed = true;
			}
		}
		else if (mIsControlDown || (SwingUtilities.isLeftMouseButton(aEvent) || !isItem || !mListView.isItemSelected(selectedItem)))
		{
			mListView.setItemsSelected(false);
			changed = true;
		}

		if (isItem && !mIsShiftDown)
		{
			mListView.setAnchorItem(selectedItem);
			mListView.setFocusItem(selectedItem);
		}

		for (T item : mSelectedItemsClone)
		{
			mListView.setItemSelected(item, true);
		}

		for (T item : layout.getItemsIntersecting(mDragStart.x, mDragStart.y, x, y, null))
		{
			mListView.setItemSelected(item, !mSelectedItemsClone.contains(item));
		}

		if (changed)
		{
			mListView.fireSelectionChanged(new ListViewEvent(mListView, aEvent));
		}

		if (aDragged)
		{
			mTempScrollRect.setBounds(x - 25, y - 25, 50, 50);
			mListView.scrollRectToVisible(mTempScrollRect);
		}
		else if (isItem)
		{
			layout.getItemBounds(selectedItem, mTempScrollRect);
			mListView.scrollRectToVisible(mTempScrollRect);
		}

		if (mListView.getAnchorItem() == null)
		{
			mListView.setAnchorItem(mListView.getFocusItem());
		}

		mListView.repaint();
	}
}
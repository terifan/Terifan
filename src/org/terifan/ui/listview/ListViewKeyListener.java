package org.terifan.ui.listview;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


class ListViewKeyListener extends KeyAdapter
{
	private ListView mListView;


	public ListViewKeyListener(ListView aListView)
	{
		mListView = aListView;
	}


	@Override
	public void keyPressed(KeyEvent aEvent)
	{
		try
		{
			ListViewLayout layout = mListView.getListViewLayout();
			ListViewItem newFocusItem = null;
			ListViewItem focusItem = mListView.getFocusItem();

			if (focusItem == null)
			{
				mListView.setFocusItem(layout.getFirstItem());
				focusItem = mListView.getFocusItem();
			}

			switch (aEvent.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
					newFocusItem = layout.getItemRelativeTo(focusItem, -1, 0);
					break;
				case KeyEvent.VK_RIGHT:
					newFocusItem = layout.getItemRelativeTo(focusItem, 1, 0);
					break;
				case KeyEvent.VK_UP:
					newFocusItem = layout.getItemRelativeTo(focusItem, 0, -1);
					break;
				case KeyEvent.VK_DOWN:
					newFocusItem = layout.getItemRelativeTo(focusItem, 0, 1);
					break;
				case KeyEvent.VK_HOME:
					newFocusItem = layout.getFirstItem();
					break;
				case KeyEvent.VK_END:
					newFocusItem = layout.getLastItem();
					break;
				case KeyEvent.VK_PAGE_UP:
					break;
				case KeyEvent.VK_PAGE_DOWN:
					break;
				case KeyEvent.VK_SPACE:
					if (mListView.getFocusItem() != null)
					{
						if (!aEvent.isControlDown())
						{
							mListView.setItemsSelected(false);
						}
						if (aEvent.isShiftDown())
						{
							for (ListViewItem item : layout.getItemsIntersecting(mListView.getAnchorItem(), mListView.getFocusItem()))
							{
								mListView.setItemSelected(item, true);
							}
						}
						else
						{
							mListView.invertItemSelection(mListView.getFocusItem());
							mListView.setAnchorItem(mListView.getFocusItem());
						}
						mListView.repaint();
						return;
					}
					break;
				case KeyEvent.VK_ENTER:
					mListView.fireSelectionAction(new ListViewEvent(mListView, aEvent));
					break;
				default:
					return;
			}

			if (newFocusItem != null && !aEvent.isShiftDown() && !aEvent.isControlDown())
			{
				mListView.setAnchorItem(newFocusItem);
			}

			if (newFocusItem != null)
			{
				if (!aEvent.isControlDown())
				{
					mListView.setItemsSelected(false);
				}

				mListView.setFocusItem(newFocusItem);

				if (!aEvent.isControlDown())
				{
					mListView.setItemSelected(newFocusItem, true);
				}
				mListView.fireSelectionChanged(new ListViewEvent(mListView, aEvent));
			}

			if (aEvent.isShiftDown() && mListView.getFocusItem() != null)
			{
				mListView.setItemsSelected(false);

				for (ListViewItem item : layout.getItemsIntersecting(mListView.getAnchorItem(), mListView.getFocusItem()))
				{
					mListView.setItemSelected(item, true);
				}
			}

			if (newFocusItem != null)
			{
				mListView.ensureItemIsVisible(newFocusItem);
			}

			if (mListView.getAnchorItem() == null)
			{
				mListView.setAnchorItem(mListView.getFocusItem());
			}

			mListView.repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

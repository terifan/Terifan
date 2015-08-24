package org.terifan.ui.listview;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.terifan.util.FixedThreadExecutor;
import org.terifan.util.log.Log;


class ItemStateLoader<T extends ListViewItem>
{
	private Set<T> mLoading = Collections.synchronizedSet(new HashSet<>());
	private FixedThreadExecutor mExecutor;
	private ListView mListView;


	public ItemStateLoader(ListView aListView)
	{
		mListView = aListView;
		mExecutor = new FixedThreadExecutor(Math.max(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() - 1, 1));
//		mExecutor = new FixedThreadExecutor(1);
	}


	public void fireLoadState(T aItem)
	{
		if (!aItem.isStateLoaded())
		{
			if (mLoading.add(aItem)) 
			{
				return;
			}

			mExecutor.submit(new RunnableItem(aItem));
		}
	}

	
	private class RunnableItem implements Runnable
	{
		private T mItem;


		public RunnableItem(T aItem)
		{
			mItem = aItem;
		}


		@Override
		public void run()
		{
			try
			{
				if (!mItem.isStateLoaded() && mListView.isItemDisplayable(mItem, false))
				{
					boolean changed = mItem.loadState(false);

					if (changed && mListView.isItemDisplayable(mItem, false))
					{
						mListView.repaint();
					}
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.out);
			}
			finally
			{
				mLoading.remove(mItem);
			}
		}


		@Override
		public boolean equals(Object aObj)
		{
			return mItem == aObj;
		}


		@Override
		public int hashCode()
		{
			return mItem.hashCode();
		}
	}
}

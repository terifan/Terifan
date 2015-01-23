package org.terifan.ui.listview;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import org.terifan.ui.listview.layout.ColumnHeaderRenderer;
import org.terifan.ui.listview.layout.DetailItemRenderer;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.terifan.ui.Orientation;
import org.terifan.ui.PopupFactory;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.Utilities;
import org.terifan.ui.listview.util.ListViewFactory;
import org.terifan.util.log.Log;


public class ListView extends JComponent implements Scrollable
{
	private ListViewModel mModel;
	private LocationInfo mRolloverInfo;
	private ListViewItem mFocusItem;
	private ListViewGroup mFocusGroup;
	private ListViewHeaderRenderer mHeaderRenderer;
	private ListViewBarRenderer mBarRenderer;
	private ListViewItemRenderer mItemRenderer;
	private ListViewLayout mLayout;
	private SelectionMode mSelectionMode;
	private boolean mRolloverEnabled;
	private boolean mIsConfigured;
	private StyleSheet mStylesheet;
	private ListViewItem mAnchorItem;
	private ArrayList<ListViewListener> mEventListeners;
	private TextRenderer mTextRenderer;
	private ListViewGroupRenderer mGroupRenderer;
	private String mPlaceholder;
	private ListViewMouseListener mMouseListener;
	private PopupFactory<ListView> mPopupFactory;

	private final Rectangle mSelectionRectangle = new Rectangle();
	private final HashSet<ListViewItem> mSelectedItems;
//	private final HashSet<ListViewItem> mSelectedItemsClone;


	public ListView()
	{
		this(null);
	}


	public ListView(ListViewModel aModel)
	{
		mSelectedItems = new HashSet<>();
//		mSelectedItemsClone = new HashSet<>();
		mEventListeners = new ArrayList<>();

		if (aModel != null)
		{
			setModel(aModel);
		}

		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setHeaderRenderer(new ColumnHeaderRenderer());
		setSelectionMode(SelectionMode.ROW);
		setItemRenderer(new DetailItemRenderer());
		setRolloverEnabled(true);
		setOpaque(true);
		setGroupRenderer(new ListViewGroupRenderer());

		super.setFocusable(true);

		mMouseListener = new ListViewMouseListener(this);
		addMouseListener(mMouseListener);
		addMouseMotionListener(mMouseListener);

		ListViewKeyListener kl = new ListViewKeyListener(this);
		addKeyListener(kl);

		// override JScrollPane actions...
		AbstractAction action = new AbstractAction(){@Override public void actionPerformed(ActionEvent aEvent){}};
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
		registerKeyboardAction(action, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
	}


	public void setStyleSheet(StyleSheet aStylesheet)
	{
		mStylesheet = aStylesheet.getStyleSheet("org.terifan.ui.listview.ListView");
	}


	public StyleSheet getStylesheet()
	{
		if (mStylesheet == null)
		{
			Class clazz = getClass();
			while (!clazz.getName().equals("org.terifan.ui.listview.ListView"))
			{
				clazz = clazz.getSuperclass();
			}

			setStyleSheet(new StyleSheet("org.terifan.ui.listview.ListView", clazz, "resources/stylesheet.xml", "resources", 512*1024));
		}

		return mStylesheet;
	}


	public void setGroupRenderer(ListViewGroupRenderer aGroupRenderer)
	{
		mGroupRenderer = aGroupRenderer;
	}


	public ListViewGroupRenderer getGroupRenderer()
	{
		return mGroupRenderer;
	}


	public void setItemRenderer(ListViewItemRenderer aItemRenderer)
	{
		mItemRenderer = aItemRenderer;

		setListViewLayout(mItemRenderer.createListViewLayout(this));
	}


	public void setListViewLayout(ListViewLayout aLayout)
	{
		mLayout = aLayout;
	}


	public ListViewLayout getListViewLayout()
	{
		return mLayout;
	}


	public ListViewItemRenderer getItemRenderer()
	{
		return mItemRenderer;
	}


	public void setRolloverEnabled(boolean aState)
	{
		mRolloverEnabled = aState;
	}


	public boolean getRolloverEnabled()
	{
		return mRolloverEnabled;
	}


	public void setModel(ListViewModel aModel)
	{
		mModel = aModel;

		mAnchorItem = null;
		mFocusItem = null;
		mRolloverInfo = null;
		mSelectedItems.clear();
//		mSelectedItemsClone.clear();
		mSelectionRectangle.setSize(0, 0);
	}


	public ListViewModel getModel()
	{
		return mModel;
	}


	public void setHeaderRenderer(ListViewHeaderRenderer aRenderer)
	{
		mHeaderRenderer = aRenderer;

		if (mIsConfigured)
		{
			configureEnclosingScrollPane();
		}
	}


	public ListViewHeaderRenderer getHeaderRenderer()
	{
		return mHeaderRenderer;
	}


	public void setBarRenderer(ListViewBarRenderer aRenderer)
	{
		mBarRenderer = aRenderer;

		if (mIsConfigured)
		{
			configureEnclosingScrollPane();
		}
	}


	public ListViewBarRenderer getBarRenderer()
	{
		return mBarRenderer;
	}


	public void setSelectionMode(SelectionMode aMode)
	{
		mSelectionMode = aMode;
	}


	public SelectionMode getSelectionMode()
	{
		return mSelectionMode;
	}


	public ListViewItem getFocusItem()
	{
		return mFocusItem;
	}


	public void setFocusItem(ListViewItem aItem)
	{
		setFocusItem(aItem, false);
	}


	public void setFocusItem(ListViewItem aItem, boolean aSetAnchor)
	{
		mFocusItem = aItem;
		if (aSetAnchor)
		{
			mAnchorItem = aItem;
		}
	}


	public void setPlaceholder(String aMessage)
	{
		mPlaceholder = aMessage;
	}


	public String getPlaceholder()
	{
		return mPlaceholder;
	}


	public int translateColumnIndex(int aIndex)
	{
		return aIndex;
	}


	public void updateColumnSize(int aColumnIndex)
	{
		System.out.println("TODO: updateColumnSize "+aColumnIndex);
	}


	protected void updateRollover(Point aPoint)
	{
		if (mRolloverEnabled)
		{
			LocationInfo info = mLayout.getLocationInfo(aPoint.x, aPoint.y);

			if (info == null && mRolloverInfo != null || info != null && !info.equals(mRolloverInfo))
			{
				mRolloverInfo = info;
				repaint();
			}
		}
	}


	@Override
	protected void paintChildren(Graphics aGraphics)
	{
		super.paintChildren(aGraphics);

		Rectangle r = (Rectangle)mSelectionRectangle.clone();

		if (!r.isEmpty())
		{
			aGraphics.setColor(new Color(111,167,223,128));
			aGraphics.fillRect(r.x+1, r.y+1, r.width, r.height);
			aGraphics.setColor(new Color(105,153,201,128));
			aGraphics.drawRect(r.x, r.y, r.width, r.height);

			if (!r.equals(mSelectionRectangle))
			{
				repaint();
			}
		}
	}


	@Override
	public void paintComponent(Graphics aGraphics)
	{
		try
		{
			super.paintComponent(aGraphics);

			synchronized (getTreeLock())
			{
				mLayout.paint((Graphics2D)aGraphics);

				if (mModel.getItemCount() == 0 && mPlaceholder != null && !mPlaceholder.isEmpty())
				{
					Utilities.enableTextAntialiasing(aGraphics);
					aGraphics.setFont(new Font("arial", Font.ITALIC, 12));
					aGraphics.setColor(Color.BLACK);
					aGraphics.drawString(mPlaceholder, (getWidth()-aGraphics.getFontMetrics().stringWidth(mPlaceholder))/2, Math.min(50, getHeight()-5));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}
	}


	@Override
	public void addNotify()
	{
		super.addNotify();
		configureEnclosingScrollPane();
	}


	protected void configureEnclosingScrollPane()
	{
		mIsConfigured = true;

		Container p = getParent();

		if (p instanceof JViewport)
		{
			Container gp = p.getParent();

			if (gp instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane)gp;
				JViewport viewport = scrollPane.getViewport();

				if (viewport == null || viewport.getView() != this)
				{
					return;
				}

				viewport.addChangeListener((aEvent) ->
				{
					if (mRolloverEnabled)
					{
						Point p1 = MouseInfo.getPointerInfo().getLocation();
						SwingUtilities.convertPointFromScreen(p1, ListView.this);
						updateRollover(p1);
					}
				});

				JPanel columnHeaderView = new JPanel(new BorderLayout());
				columnHeaderView.add(new ListViewBar(this), BorderLayout.NORTH);
				columnHeaderView.add(new ListViewHeader(this, "column_header"), BorderLayout.SOUTH);

				ListViewHeader rowHeaderView;
				rowHeaderView = new ListViewHeader(this, "row_header");

				scrollPane.setRowHeaderView(rowHeaderView);
				scrollPane.setColumnHeaderView(columnHeaderView);
				scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new ListViewHeader(this, "upper_left_corner"));
				scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new ListViewHeader(this, "upper_right_corner"));
				scrollPane.setBorder(null);

//				scrollPane.addMouseWheelListener(new MouseWheelListener()
//				{
//					@Override
//					public void mouseWheelMoved(MouseWheelEvent e)
//					{
//						if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) Log.out.println("unit " + scrollPane.getVerticalScrollBar().getUnitIncrement(1));
////						Log.out.println(e);
//					}
//				});

//				Border border = scrollPane.getBorder();
//
//				if (border == null || border instanceof UIResource)
//				{
//					Border scrollPaneBorder = UIManager.getBorder("Table.scrollPaneBorder");
//
//					if (scrollPaneBorder != null)
//					{
//						scrollPane.setBorder(scrollPaneBorder);
//					}
//				}
			}
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		return mLayout.getPreferredSize();
	}


	@Override
	public Dimension getMinimumSize()
	{
		return mLayout.getMinimumSize();
	}


	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return (Dimension)getPreferredSize().clone();
	}


	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		if (mLayout.getLayoutOrientation() == Orientation.VERTICAL)
		{
			return getParent() instanceof JViewport && (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
		else
		{
			return getParent() instanceof JViewport && (((JViewport)getParent()).getHeight() > getMinimumSize().height);
		}
	}


	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		if (mLayout.getLayoutOrientation() == Orientation.VERTICAL)
		{
			return getParent() instanceof JViewport && (((JViewport)getParent()).getWidth() > getMinimumSize().width);
		}
		else
		{
			return getParent() instanceof JViewport && (((JViewport)getParent()).getWidth() > getPreferredSize().width);
		}
	}


	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		int v = orientation == SwingConstants.VERTICAL ? mItemRenderer.getItemPreferredHeight(this) : mItemRenderer.getItemPreferredWidth(this);
		return (int)Math.ceil(v / 3.0);
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		if (getParent() instanceof JViewport)
		{
			JViewport vp = (JViewport)getParent();
			return orientation == SwingConstants.VERTICAL ? vp.getHeight() : vp.getWidth();
		}
		else
		{
			int v = orientation == SwingConstants.VERTICAL ? mItemRenderer.getItemPreferredHeight(this) : mItemRenderer.getItemPreferredWidth(this);
			return (int)Math.ceil(v / 3.0);
		}
	}


	public void validateLayout()
	{
		getModel().validate();

//		for (Component c = this; c != null; c = c.getParent())
//		{
//			c.revalidate();
//		}

		revalidate();
		requestFocus();
		repaint();
	}


	public boolean isItemSelected(ListViewItem aItem)
	{
		return mSelectedItems.contains(aItem);
//		return mSelectedItems.contains(aItem) ^ mSelectedItemsClone.contains(aItem);
	}


	/**
	 * Return true if an unselected item was selected or if an selected item was unselected.
	 */
	public boolean setItemSelected(ListViewItem aItem, boolean aSelected)
	{
		if (aSelected)
		{
			return mSelectedItems.add(aItem);
		}
		else
		{
			return mSelectedItems.remove(aItem);
		}
	}


	/**
	 * Either marks all items selected or unselected in this ListView.
	 *
	 * @param aSelected
	 *    the new state of all items.
	 */
	public void setItemsSelected(boolean aSelected)
	{
		if (aSelected)
		{
			mSelectedItems.clear();
			for (int i = 0; i < mModel.getItemCount(); i++)
			{
				mSelectedItems.add(mModel.getItem(i));
			}
		}
		else
		{
			mSelectedItems.clear();
		}
	}


	/**
	 * Either marks all items selected or unselected in this ListView.
	 *
	 * @param aSelected
	 *    the new state of all items.
	 */
	public void setItemsSelected(int aStartIndex, int aEndIndex, boolean aSelected)
	{
		aEndIndex = Math.min(aEndIndex, mModel.getItemCount());

		for (int i = aStartIndex; i < aEndIndex; i++)
		{
			if (aSelected)
			{
				mSelectedItems.add(mModel.getItem(i));
			}
			else
			{
				mSelectedItems.remove(mModel.getItem(i));
			}
		}
	}


	public void invertItemSelection(ListViewItem aItem)
	{
		if (mSelectedItems.contains(aItem))
		{
			mSelectedItems.remove(aItem);
		}
		else
		{
			mSelectedItems.add(aItem);
		}
	}


	public synchronized void addListViewListener(ListViewListener aListViewListener)
	{
		mEventListeners.add(aListViewListener);
	}


	public synchronized void removeListViewListener(ListViewListener aListViewListener)
	{
		mEventListeners.remove(aListViewListener);
	}


	protected synchronized void fireSelectionChanged(ListViewEvent aEvent)
	{
		for (ListViewListener listener : mEventListeners)
		{
			listener.selectionChanged(aEvent);
		}
	}


	protected synchronized void fireSelectionAction(ListViewEvent aEvent)
	{
		for (ListViewListener listener : mEventListeners)
		{
			listener.selectionAction(aEvent);
		}
	}


	protected synchronized void fireSortedColumnWillChange(ListViewEvent aEvent)
	{
		for (ListViewListener listener : mEventListeners)
		{
			listener.sortedColumnWillChange(aEvent);
		}
	}


	protected synchronized void fireSortedColumnChanged(ListViewEvent aEvent)
	{
		for (ListViewListener listener : mEventListeners)
		{
			listener.sortedColumnChanged(aEvent);
		}
	}


	protected void fireMouseEvent(MouseEvent aEvent)
	{
		switch (aEvent.getID())
		{
			case MouseEvent.MOUSE_CLICKED:
				mMouseListener.mouseClicked(aEvent);
				break;
			case MouseEvent.MOUSE_PRESSED:
				mMouseListener.mousePressed(aEvent);
				break;
			case MouseEvent.MOUSE_RELEASED:
				mMouseListener.mouseReleased(aEvent);
				break;
			case MouseEvent.MOUSE_MOVED:
				mMouseListener.mouseMoved(aEvent);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				mMouseListener.mouseDragged(aEvent);
				break;
			case MouseEvent.MOUSE_ENTERED:
				mMouseListener.mouseEntered(aEvent);
				break;
			case MouseEvent.MOUSE_EXITED:
				mMouseListener.mouseExited(aEvent);
				break;
			case MouseEvent.MOUSE_WHEEL:
				mMouseListener.mouseWheelMoved((MouseWheelEvent)aEvent);
				break;
		}
	}


	/**
	 * Create and display a pop-up.
	 *
	 * Note: this code also ensure that if the right click occurred on an item the item will be selected before the pop-up is displayed.
	 */
	protected void firePopupMenu(Point aPoint)
	{
		PopupFactory<ListView> factory = getPopupFactory();

		if (factory == null)
		{
			return;
		}

		JPopupMenu menu = factory.createPopup(this);

		if (menu == null)
		{
			return;
		}

		final AWTEventListener listener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event)
			{
				MouseEvent me = (MouseEvent)event;
				if(me.getID() == MouseEvent.MOUSE_PRESSED && me.getComponent() == ListView.this)
				{
					fireMouseEvent(me);
				}
			}
		};

		menu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
				Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);

		menu.show(ListView.this, aPoint.x, aPoint.y);
	}


	/**
	 * Sets the pop-up factory for this ListView.
	 *
	 * @param aMenu
	 *   a pop-up factory or null if non should exist
	 */
	public void setPopupFactory(PopupFactory<ListView> aPopupFactory)
	{
		mPopupFactory = aPopupFactory;
	}


	/**
	 * Returns the pop-up factory assigned for this ListView using setPopupFactory.
	 *
	 * @return
	 *   a PopupFactory or null if one doesn't exists
	 */
	public PopupFactory<ListView> getPopupFactory()
	{
		return mPopupFactory;
	}


	public void ensureItemIsVisible(ListViewItem aItem)
	{
		Rectangle rect = new Rectangle();
		mLayout.getItemBounds(aItem, rect);
		scrollRectToVisible(rect);
	}


	/**
	 * Gets all selected items from this ListView in the order they are
	 * currently displayed.
	 *
	 * @return
	 *   a list of all selected items.
	 */
	public ArrayList<ListViewItem> getSelectedItems()
	{
		final ArrayList<ListViewItem> list = new ArrayList<>(mSelectedItems.size());

		ItemVisitor visitor = new ItemVisitor() {
			@Override
			public Object visit(ListViewItem aItem)
			{
				if (mSelectedItems.contains(aItem))
				{
					list.add(aItem);
				}
				return null;
			}
		};

		mModel.visitItems(true, visitor);

		return list;
	}


	/**
	 * Gets all items from this ListView in the order they are currently
	 * displayed.
	 *
	 * @return
	 *   a list of all items.
	 */
	public ArrayList<ListViewItem> getItems()
	{
		final ArrayList<ListViewItem> list = new ArrayList<>(mModel.getItemCount());

		ItemVisitor visitor = new ItemVisitor() {
			@Override
			public Object visit(ListViewItem aItem)
			{
				list.add(aItem);
				return null;
			}
		};

		mModel.visitItems(true, visitor);

		return list;
	}


	/**
	 * Returns a TextRenderer instance used by all default item renderers. This
	 * method lazily creates and instance of TextRenderer with a 1MiB cache.
	 * Override this method to create a cache of different size.
	 *
	 * @return
	 *   the TextRenderer instance used to render all text.
	 */
	public TextRenderer getTextRenderer()
	{
		if (mTextRenderer == null)
		{
			mTextRenderer = new TextRenderer();
		}

		return mTextRenderer;
	}


	public ListViewItem getRolloverItem()
	{
		return mRolloverInfo == null ? null : mRolloverInfo.getItem();
	}


	public ListViewGroup getRolloverGroup()
	{
		return mRolloverInfo == null ? null : mRolloverInfo.getGroup();
	}


	// TODO: replace
	public void configureKeys()
	{
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A)
				{
					setItemsSelected(true);
					repaint();
				}
			}
		});
	}


	@Override
	public void revalidate()
	{
		if (mModel != null)
		{
			mModel.validate();
		}
		super.revalidate();
	}


	public boolean isItemDisplayable(ListViewItem aItem)
	{
		Rectangle itemRect = new Rectangle();

		mLayout.getItemBounds(aItem, itemRect);

		Container p = getParent();

		if (p instanceof JViewport)
		{
			Container gp = p.getParent();

			if (gp instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane)gp;

				JViewport viewport = scrollPane.getViewport();

				if (viewport != null && viewport.getView() == this)
				{
					int x = scrollPane.getHorizontalScrollBar().getValue();
					int y = scrollPane.getVerticalScrollBar().getValue();
					int w = viewport.getWidth();
					int h = viewport.getHeight();

					return !SwingUtilities.computeIntersection(x, y, w, h, itemRect).isEmpty();
				}
			}
		}

		return true;
	}


	protected ListViewItem getAnchorItem()
	{
		return mAnchorItem;
	}


	protected void setAnchorItem(ListViewItem aItem)
	{
		mAnchorItem = aItem;
	}


	protected HashSet<ListViewItem> getSelectedItemsMap()
	{
		return mSelectedItems;
	}


//	protected HashSet<ListViewItem> getSelectedItemsClone()
//	{
//		return mSelectedItemsClone;
//	}


	public void setRowHeaderRenderer()
	{
	}


	protected Rectangle getSelectionRectangle()
	{
		return mSelectionRectangle;
	}


	public static void main(String ... args)
	{
		try
		{
			ListViewModel listViewModel = new ListViewModel();
			listViewModel.addColumn("test", 100);
			listViewModel.addItem(new DefaultListViewItem("test item"));
			ListView listView = new ListView(listViewModel);
			ListViewFactory.applyDetailLayout(listView, true);

			JFrame frame = new JFrame();
			frame.setSize(1024, 768);
			frame.add(new JScrollPane(listView));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
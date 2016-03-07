package org.terifan.ui.propertygrid;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.terifan.ui.StyleSheet;
import org.terifan.ui.TextRenderer;


public class PropertyGrid extends JPanel
{
	protected StyleSheet mStylesheet;
	protected ArrayList<ChangeListener> mListeners;
	protected PropertyGridModel mModel;
	protected int mDividerPosition;
	protected JScrollPane mScrollPane;
	protected PropertyGridListPane mPanel;
	protected PropertyGridDescriptionPane mDescriptionPane;
	protected Property mSelectedProperty;
	protected PropertyGridMenuPane mToolbar;
	protected PropertyGridOrder mOrder;
	protected TextRenderer mTextRenderer;
	protected JSplitPane mSplitPane;


	public PropertyGrid(PropertyGridModel aPropertyGridModel)
	{
		super(new BorderLayout());

		Class clazz = getClass();
		while (!clazz.getName().equals("org.terifan.ui.propertygrid.PropertyGrid"))
		{
			clazz = clazz.getSuperclass();
		}

		setStylesheet(new StyleSheet("org.terifan.ui.propertygrid.PropertyGrid", clazz, "resources/stylesheet.xml", "resources", 1024*1024));

		mTextRenderer = new TextRenderer();
		mDescriptionPane = new PropertyGridDescriptionPane(this);

		mPanel = new PropertyGridListPane(this);
		mOrder = PropertyGridOrder.CATEGORY;
		mListeners = new ArrayList<ChangeListener>();
		mScrollPane = new JScrollPane(mPanel);
		mScrollPane.setBorder(null);

		mSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mScrollPane, mDescriptionPane);
		mSplitPane.setResizeWeight(1);

		mToolbar = new PropertyGridMenuPane(this);

		setShowDescription(mStylesheet.getBoolean("show_description"));
		setDividerPosition(mStylesheet.getInt("divider_position"));

		add(mToolbar, BorderLayout.NORTH);
		add(mSplitPane, BorderLayout.CENTER);

		setOpaque(true);
		setModel(aPropertyGridModel);
	}


	public void setStylesheet(StyleSheet aStylesheet)
	{
		mStylesheet = aStylesheet;

		if (mScrollPane != null)
		{
			mScrollPane.getVerticalScrollBar().setUnitIncrement(mStylesheet.getInt("row_height"));
		}
	}


	public StyleSheet getStylesheet()
	{
		return mStylesheet;
	}


	public void setOrder(PropertyGridOrder aOrder)
	{
		mOrder = aOrder;
	}


	public PropertyGridOrder getOrder()
	{
		return mOrder;
	}


	protected PropertyGridListPane getRenderer()
	{
		return mPanel;
	}


	public void setDividerPosition(int aDividerPosition)
	{
		mDividerPosition = aDividerPosition;
	}


	public int getDividerPosition()
	{
		return mDividerPosition;
	}


	public void addChangeListener(ChangeListener aChangeListener)
	{
		mListeners.add(aChangeListener);
	}


	public void removeChangeListener(ChangeListener aChangeListener)
	{
		mListeners.remove(aChangeListener);
	}


	public boolean getShowDescription()
	{
		return mDescriptionPane.isVisible();
	}


	public void setShowDescription(boolean aShowDescription)
	{
		if (aShowDescription == mDescriptionPane.isVisible())
		{
			return;
		}

		mDescriptionPane.setVisible(aShowDescription);

		if (aShowDescription)
		{
			super.remove(mScrollPane);

			mSplitPane.setTopComponent(mScrollPane);
			super.add(mSplitPane, BorderLayout.CENTER);
		}
		else
		{
			super.remove(mSplitPane);
			mSplitPane.setTopComponent(new JLabel(""));

			super.add(mScrollPane, BorderLayout.CENTER);
		}
	}


	public PropertyGridModel getModel()
	{
		return mModel;
	}


	public void setModel(PropertyGridModel aPropertyGridModel)
	{
		mModel = aPropertyGridModel;

		mPanel.removeAll();

		Font valueFont = mStylesheet.getFont("item");

		for (Iterator<Property> it = mModel.getRecursiveIterator(); it.hasNext();)
		{
			Property item = it.next();

			item.setPropertyGrid(this);

			mPanel.add(item.getIndentComponent());
			mPanel.add(item.getLabelComponent());

			JComponent component = item.getValueComponent();
			if (component != null)
			{
				mPanel.add(component);
				component.addFocusListener(new PropertyGridEditorListener(item));
				component.setFont(valueFont);
			}

			JButton button = item.getDetailButton();
			if (button != null)
			{
				mPanel.add(button);
			}
		}
	}


	protected Property getSelectedProperty()
	{
		return mSelectedProperty;
	}


	protected void setSelectedProperty(Property aProperty)
	{
		mSelectedProperty = aProperty;

		if (aProperty != null)
		{
			if (aProperty.commitValue())
			{
				if (mListeners.size() > 0)
				{
					ChangeEvent event = new ChangeEvent(aProperty);
					for (ChangeListener o : mListeners)
					{
						o.stateChanged(event);
					}
				}
			}

			mSelectedProperty.getValueComponent().requestFocus();
		}
	}


	protected void redisplay()
	{
		mPanel.invalidate();
		mPanel.validate();
		mScrollPane.invalidate();
		mScrollPane.validate();
		invalidate();
		validate();
		repaint();
	}


	protected TextRenderer getTextRenderer()
	{
		return mTextRenderer;
	}
}
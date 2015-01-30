package org.terifan.ui.propertygrid;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;


public class Property implements Comparable<Property>, Iterable<Property>
{
	protected ArrayList<Property> mElements = new ArrayList<Property>();
	protected String mLabel;
	protected String mDescription;
	protected Object mValue;
	protected boolean mReadOnly;
	protected PropertyGridLabel mLabelComponent;
	protected JComponent mValueComponent;
	protected Object mUserObject;
	protected Object mOriginalValue;
	protected Object mPreviousValue;
	protected JButton mDetailsButtons;
	protected boolean mCollapsed;
	protected PropertyGrid mPropertyGrid;
	private PropertyGridIndent mIndentComponent;


	public Property()
	{
	}


	public Property(String aLabel, String aDescription, Object aValue)
	{
		this(aLabel, aDescription, aValue, false, null, null);
	}


	public Property(String aLabel, String aDescription, Object aValue, boolean aReadOnly)
	{
		this(aLabel, aDescription, aValue, aReadOnly, null, null);
	}


	public Property(String aLabel, String aDescription, Object aValue, boolean aReadOnly, ActionListener aActionListener)
	{
		this(aLabel, aDescription, aValue, aReadOnly, aActionListener, null);
	}


	public Property(String aLabel, String aDescription, Object aValue, boolean aReadOnly, ActionListener aActionListener, Object aUserObject)
	{
		mLabel = aLabel;
		mDescription = aDescription;
		mReadOnly = aReadOnly;
		mUserObject = aUserObject;
		mIndentComponent = new PropertyGridIndent(this);

		if (aValue instanceof JComponent)
		{
			mValue = aValue;
			mPreviousValue = getComponentValue((JComponent)aValue);
		}
		else
		{
			mValue = aValue.toString();
			mPreviousValue = mValue;
		}

		mOriginalValue = mPreviousValue;

		if (aActionListener != null)
		{
			mDetailsButtons = createDetailButton(this, aActionListener);
		}
	}


	protected JButton createDetailButton(final Property aProperty, final ActionListener aActionListener)
	{
		JButton button = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				aActionListener.actionPerformed(new ActionEvent(aProperty, 0, mLabel));
			}
		});

		button.setMargin(new Insets(0,0,0,0));
		button.setOpaque(false);
		button.setFocusable(false);

		button.setActionCommand(mLabel);
		button.setText("...");

		return button;
	}


	public boolean isChanged()
	{
		return !getComponentValue(mValueComponent).equals(mOriginalValue);
	}


	public Object getUserObject()
	{
		return mUserObject;
	}


	public void setUserObject(Object aObject)
	{
		mUserObject = aObject;
	}


	public String getLabel()
	{
		return mLabel;
	}


	public String getDescription()
	{
		return mDescription;
	}


	public Object getValue()
	{
		return mValue;
	}


	public void setValue(String aValue)
	{
		if (mValue instanceof String)
		{
			mValue = aValue;
			((JTextField)mValueComponent).setText(aValue);
		}
		else
		{
			throw new IllegalArgumentException("Non String values must have their value set by calling component specific methods.");
		}
	}


	public boolean getReadOnly()
	{
		return mReadOnly;
	}
	

	public boolean getHasDetails()
	{
		return mDetailsButtons != null;
	}


	public JButton getDetailButton()
	{
		return mDetailsButtons;
	}


	protected boolean commitValue()
	{
		if (mValue instanceof String && mValueComponent instanceof JTextField)
		{
			Object o = mValue;
			mValue = ((JTextField)mValueComponent).getText();
			return !o.equals(mValue);
		}

		Object val = getComponentValue(mValueComponent);
		Object tmp = mPreviousValue;

		mPreviousValue = val;

		return !tmp.equals(val);
	}


	protected Object getComponentValue(JComponent aComponent)
	{
		if (aComponent instanceof JTextField)
		{
			return ((JTextField)aComponent).getText();
		}
		if (aComponent instanceof JCheckBox)
		{
			return ((JCheckBox)aComponent).isSelected();
		}
		if (aComponent instanceof JComboBox)
		{
			return ((JComboBox)aComponent).getSelectedItem();
		}

		System.out.println("Unsupported component: " + aComponent);

		return null;
	}


	protected void configureValueComponent(JComponent aComponent)
	{
		if (aComponent instanceof JCheckBox)
		{
			aComponent.setOpaque(false);
			aComponent.setCursor(Cursor.getDefaultCursor());
		}
		if (aComponent instanceof JComboBox)
		{
			aComponent.setOpaque(false);
			aComponent.setCursor(Cursor.getDefaultCursor());
		}
		if (aComponent instanceof JTextField)
		{
			aComponent.setBorder(null);
		}
	}


	protected JComponent getValueComponent()
	{
		if (mValueComponent == null)
		{
			if (mValue instanceof JComponent)
			{
				mValueComponent = (JComponent)mValue;
			}
			else
			{
				mValueComponent = new JTextField(mValue.toString());
			}
			configureValueComponent(mValueComponent);
		}

		return mValueComponent;
	}


	protected JComponent getLabelComponent()
	{
		if (mLabelComponent == null)
		{
			mLabelComponent = new PropertyGridLabel(this);
		}

		return mLabelComponent;
	}


	protected JComponent getIndentComponent()
	{
		return mIndentComponent;
	}


	@Override
	public int hashCode()
	{
		return mLabel == null ? 0 : mLabel.hashCode();
	}


	@Override
	public boolean equals(Object aObject)
	{
		if (aObject instanceof Property)
		{
			Property p = (Property)aObject;
			return p.mLabel == null ? false : p.getLabel().equals(mLabel);
		}
		return false;
	}


	@Override
	public String toString()
	{
		return mLabel;
	}


	@Override
	public int compareTo(Property aProperty)
	{
		return mLabel.compareTo(aProperty.getLabel());
	}


	public void addProperty(Property aProperty)
	{
		if (mElements == null)
		{
			mElements = new ArrayList<Property>();
		}
		mElements.add(aProperty);
	}


	public Property getProperty(int aIndex)
	{
		if (mElements == null)
		{
			return null;
		}
		return mElements.get(aIndex);
	}


	public int getPropertyCount()
	{
		return mElements == null ? 0 : mElements.size();
	}


	public boolean getCollapsed()
	{
		return mCollapsed;
	}


	public void setCollapsed(boolean aCollapsed)
	{
		mCollapsed = aCollapsed;
	}


	@Override
	public Iterator<Property> iterator()
	{
		ArrayList<Property> list = new ArrayList<Property>(mElements);
		Collections.sort(list);
		return list.iterator();
	}


	protected void getRecursiveElements(ArrayList<Property> aList)
	{
		for (Property item : this)
		{
			aList.add(item);
			if (!item.getCollapsed())
			{
				item.getRecursiveElements(aList);
			}
		}
	}


	protected Integer getIndent(Property aProperty, int aIndent)
	{
		for (Property item : mElements)
		{
			if (item == aProperty)
			{
				return aIndent;
			}
			Integer i = item.getIndent(aProperty, aIndent+1);
			if (i != null)
			{
				return i;
			}
		}
		return null;
	}


	public PropertyGrid getPropertyGrid()
	{
		return mPropertyGrid;
	}


	protected void setPropertyGrid(PropertyGrid aPropertyGrid)
	{
		mPropertyGrid = aPropertyGrid;
	}
}
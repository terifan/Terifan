package org.terifan.ui.propertygrid;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class PropertyGridEditorListener implements FocusListener
{
	private Property mProperty;


	public PropertyGridEditorListener(Property aProperty)
	{
		mProperty = aProperty;
	}


	@Override
	public void focusGained(FocusEvent aEvent)
	{
		if (mProperty.getValueComponent() == aEvent.getComponent())
		{
			synchronized (this)
			{
				mProperty.getPropertyGrid().setSelectedProperty(mProperty);
			}

			mProperty.getPropertyGrid().repaint();
		}
	}


	@Override
	public void focusLost(FocusEvent aEvent)
	{
		if (mProperty.getValueComponent() == aEvent.getComponent())
		{
			synchronized (this)
			{
				if (mProperty.getPropertyGrid().getSelectedProperty() == mProperty)
				{
					mProperty.getPropertyGrid().setSelectedProperty(null);
				}
			}

			mProperty.getPropertyGrid().repaint();
		}
	}
}

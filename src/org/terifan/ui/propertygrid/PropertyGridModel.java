package org.terifan.ui.propertygrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class PropertyGridModel implements Iterable<Property>
{
	protected ArrayList<Property> mElements;


	public PropertyGridModel()
	{
		mElements = new ArrayList<Property>();
	}


	public void addProperty(Property aProperty)
	{
		mElements.add(aProperty);
	}


	public Property getProperty(int aIndex)
	{
		return mElements.get(aIndex);
	}


	public int getPropertyCount()
	{
		return mElements.size();
	}

/*
	protected void iterate(PropertyGrid aPropertyGrid, Callback aCallback, boolean aVisitCollapsedProperties)
	{
		iterate(aPropertyGrid, mElements, aCallback, aVisitCollapsedProperties, aPropertyGrid.getOrder());
	}


	private void iterate(PropertyGrid aPropertyGrid, ArrayList<Property> aElements, Callback aCallback, boolean aVisitCollapsedProperties, PropertyGridOrder aOrder)
	{
		int rowWidth = aPropertyGrid.getRenderer().getWidth();
		int dividerPosition = aPropertyGrid.getDividerPosition();
		int indentWidth = aPropertyGrid.getStylesheet().getInt("indent_width");

		if (aOrder == PropertyGridOrder.CATEGORY)
		{
			for (int i = 0, y = 0; i < aElements.size(); i++)
			{
				Property category = mElements.get(i);
				y = iterateProperty(aPropertyGrid, category, true, -1, 0, y, rowWidth, indentWidth, dividerPosition, category.getLabel(), aCallback, aVisitCollapsedProperties);
			}
		}
		else if (aOrder == PropertyGridOrder.SORTED)
		{
			final ArrayList<Property> list = new ArrayList<Property>();

			PropertyGridModel.Callback callback = new PropertyGridModel.Callback()
			{
				@Override
				public int process(Property aProperty, boolean aVisible, int aColumn, int aPositionX, int aPositionY, int aWidth, int aIndentWidth, int aDividerPosition, String aCategoryLabel)
				{
					if (aProperty.getPropertyCount() == 0)
					{
						list.add(aProperty);
					}
					return 0;
				}
			};

			iterate(aPropertyGrid, mElements, callback, true, PropertyGridOrder.CATEGORY);

			Collections.sort(list);

			for (int i = 0, y = 0; i < list.size(); i++)
			{
				y = iterateProperty(aPropertyGrid, list.get(i), true, 0, 0, y, rowWidth, indentWidth, dividerPosition, "xxx", aCallback, aVisitCollapsedProperties);
			}
		}
		else
		{
			throw new RuntimeException("Unsupported order.");
		}
	}


	private int iterateProperty(PropertyGrid aPropertyGrid, Property aProperty, boolean aVisible, int aColumn, int aPositionX, int aPositionY, int aWidth, int aIndentWidth, int aDividerPosition, String aCategoryLabel, Callback aCallback, boolean aCollapsedProperties)
	{
		aPositionY = aCallback.process(aProperty, aVisible, aColumn, aPositionX, aPositionY, aWidth, aIndentWidth, aDividerPosition, aCategoryLabel);

		aVisible &= !aProperty.getCollapsed();

		if ((aCollapsedProperties || aVisible) && aProperty.getPropertyCount() > 0)
		{
			for (int i = 0; i < aProperty.getPropertyCount(); i++)
			{
				aPositionY = iterateProperty(aPropertyGrid, aProperty.getProperty(i), aVisible, aColumn+1, aPositionX, aPositionY, aWidth, aIndentWidth, aDividerPosition, null, aCallback, aCollapsedProperties);
			}
		}

		return aPositionY;
	}


	protected interface Callback
	{
		public int process(Property aProperty, boolean aVisible, int aColumn, int aPositionX, int aPositionY, int aWidth, int aIndentWidth, int aDividerPosition, String aCategoryLabel);
	}
*/

	protected Integer getIndent(Property aProperty)
	{
		for (Property item : mElements)
		{
			if (item == aProperty)
			{
				return aProperty.getPropertyCount() == 0 ? 1 : 0;
			}
			Integer i = item.getIndent(aProperty, 1);
			if (i != null)
			{
				return i;
			}
		}
		return null;
	}


	@Override
	public Iterator<Property> iterator()
	{
		ArrayList<Property> list = new ArrayList<Property>(mElements);
		Collections.sort(list);
		return list.iterator();
	}


	public Iterator<Property> getRecursiveIterator()
	{
		ArrayList<Property> list = new ArrayList<Property>(mElements);
		Collections.sort(list);

		ArrayList<Property> out = new ArrayList<Property>();
		for (Property property : list)
		{
			out.add(property);
			if (!property.getCollapsed())
			{
				property.getRecursiveElements(out);
			}
		}

		return out.iterator();
	}
}
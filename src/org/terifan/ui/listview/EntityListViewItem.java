package org.terifan.ui.listview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.terifan.util.log.Log;


public class EntityListViewItem extends AbstractListViewItem
{
	private Class<?> mType;


	public EntityListViewItem(Class<?> aType)
	{
		mType = aType;
	}


	@Override
	public Object getValue(ListViewColumn aColumn)
	{
		try
		{
			Method method = (Method)aColumn.getUserObject(EntityListViewItem.class);

			if (method == null)
			{
				for (Method tmp : mType.getDeclaredMethods())
				{
					if (tmp.getName().startsWith("get") && tmp.getParameterCount() == 0 && tmp.getName().substring(3).equalsIgnoreCase(aColumn.getKey()))
					{
						tmp.setAccessible(true);
						method = tmp;
						aColumn.setUserObject(EntityListViewItem.class, method);
						break;
					}
				}
			}

			if (method != null)
			{
				return method.invoke(this);
			}
			else
			{
				return "#missing";
			}
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace(Log.out);

			return "#error";
		}
	}
}
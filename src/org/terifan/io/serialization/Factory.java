package org.terifan.io.serialization;

import java.util.ArrayDeque;
import java.util.HashMap;
import org.terifan.util.log.Log;


public class Factory
{
	private ArrayDeque<HashMap> mStack = new ArrayDeque<>();
	private HashMap mContainer;
	private HashMap mRoot;


	public Factory()
	{
		mRoot = mContainer = new HashMap();
	}


	public HashMap get()
	{
		return mContainer;
	}


	public void enter(String aKey)
	{
		Log.out.println("enter "+aKey);

		HashMap map = new HashMap();
		mContainer.put(aKey, map);

		mStack.push(mContainer);

		mContainer = map;
	}


	public void exit()
	{
		Log.out.println("exit");

		mContainer = mStack.pop();
	}


	public void newObject()
	{
		Log.out.println("new");
	}


	public void setValue(String aKey, Object aValue)
	{
		Log.out.println(this+" "+aKey+" "+aValue);
		mContainer.put(aKey, aValue);

//		try
//		{
//			Field field = mParent.getClass().getDeclaredField(aKey);
//			field.setAccessible(true);
//			field.set(mParent, aValue);
//		}
//		catch (IllegalAccessException | NoSuchFieldException | SecurityException ex)
//		{
//			Logger.getLogger(JSONReader.class.getName()).log(Level.SEVERE, null, ex);
//		}
	}
}

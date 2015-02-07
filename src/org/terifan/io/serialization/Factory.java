package org.terifan.io.serialization;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.terifan.util.log.Log;


public class Factory
{
	private final static String TAG = Factory.class.getName();

	private String mIndent = "";
	private Context mContext;
	private Object mOutput;


	public Factory(Class aType)
	{
		mContext = new Context();
		mContext.type = aType;
	}


	public Object getOutput()
	{
		return mOutput;
	}


	public void startDecoding() throws IOException
	{
	}


	public void endDecoding() throws IOException
	{
	}

	
	public void startElement(String aKey) throws IOException
	{
		print(1, "start element " + aKey);

		try
		{
			mContext.create();
			mContext.field = mContext.object.getClass().getDeclaredField(aKey);
			mContext.field.setAccessible(true);

			Type type = mContext.field.getGenericType();
			if (type instanceof ParameterizedType)
			{
				ParameterizedType paramType = (ParameterizedType)type;
				mContext.type = Class.forName(paramType.getActualTypeArguments()[0].getTypeName());
			}
			else
			{
				mContext.type = mContext.field.getType();
			}
		}
		catch (NoSuchFieldException | SecurityException | ClassNotFoundException e)
		{
			throw new IOException(e);
		}
	}


	public void endElement()
	{
		print(-1, "end element");

		mContext.end();
	}


	public void startObject() throws IOException
	{
		print(1, "start object " + mContext.type);

		mContext.create();
		
		try
		{
			mContext.object = mContext.type.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IOException(e);
		}

		if (mOutput == null)
		{
			mOutput = mContext.object;
		}
	}


	public void endObject() throws IOException
	{
		print(-1, "end object");

		if (mContext.array != null)
		{
			mContext.array.add(mContext.object);
		}
		else if (mContext.parent.object != null)
		{
			try
			{
				mContext.field.set(mContext.parent.object, mContext.object);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				throw new IOException(e);
			}
		}
		
		mContext.end();
	}


	public void startArray()
	{
		print(1, "start array");

		mContext.create();
		mContext.array = new ArrayList();
		mContext.object = mContext.array;
	}


	public void endArray() throws IOException
	{
		print(-1, "end array");

		if (mContext.parent == null || !List.class.isAssignableFrom(mContext.parent.object.getClass()))
		{
			try
			{
				mContext.field.set(mContext.parent.object, castArray((List)mContext.object));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				throw new IOException(e);
			}
		}
		else
		{
			mContext.parent.array.add(mContext.array);
		}
		
		mContext.end();
	}


	public void startArrayElement()
	{
		print(1, "start array element");
	}


	public void endArrayElement()
	{
		print(-1, "end array element");
	}


	public void setValue(Object aValue) throws IOException
	{
		try
		{
			Class type = mContext.field.getType();
			Object value = cast(aValue, type);

			if (mContext.array == mContext.object)
			{
				mContext.array.add(value);
			}
			else
			{
				mContext.field.set(mContext.object, value);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new IOException(e);
		}

		print(0, mContext.field + " = " + aValue);
	}
	
	
	private Object cast(Object aValue, Class aType)
	{
		if (aType == Integer.class || aType == Integer.TYPE)
		{
			return (int)(long)(Long)aValue;
		}

		return aValue;
	}
	
	
	private Object castArray(List aValue)
	{
		Class type = mContext.field.getType();

		Log.out.println(aValue.getClass());

		if (type.isArray())
		{
			int depth = 0;
			do
			{
				depth++;
				type = type.getComponentType();
			} while (type.isArray());

			return castArray(aValue, depth, 1, type);
		}

		return aValue;
	}
	
	
	private Object castArray(List aList, int aDepth, int aLevel, Class aComponentType)
	{
		int[]dims = new int[aDepth - aLevel + 1];
		dims[0] = aList.size();
		Object array = Array.newInstance(aComponentType, dims);

		for (int i = 0; i < aList.size(); i++)
		{
			if (aDepth == aLevel)
			{
				Array.set(array, i, cast(aList.get(i), aComponentType));
			}
			else
			{
				Object arr = castArray((List)aList.get(i), aDepth, aLevel + 1, aComponentType);
				Array.set(array, i, arr);
			}
		}

		return array;
	}
	
	
	private void print(int aIndent, String aString)
	{
		if (aIndent == -1)
		{
			mIndent = mIndent.substring(0, mIndent.length() - 4);
		}
		
		Log.out.println(mIndent + aString);

		if (aIndent == 1)
		{
			mIndent += "... ";
		}
	}


	private static class Context
	{
		Context parent;
		Context child;
		Object object;
		Field field;
		Class type;
		List array;


		private void create()
		{
			Context ctx = new Context();
			ctx.field = field;
			ctx.object = object;
			ctx.type = type;
			ctx.parent = parent;
			ctx.array = array;
			ctx.child = this;
			parent = ctx;
		}


		private void end()
		{
			field = parent.field;
			object = parent.object;
			type = parent.type;
			array = parent.array;
			parent = parent.parent;
			child = null;
		}
	}
}

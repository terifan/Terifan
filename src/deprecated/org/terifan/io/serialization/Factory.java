package deprecated.org.terifan.io.serialization;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		mContext.setType(aType);
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

		mContext.create();

		if (mContext.getParent().getField() != null && Map.class.isAssignableFrom(mContext.getParent().getField().getType()))
		{
			Type type = mContext.getParent().getField().getGenericType();
			ParameterizedType paramType = (ParameterizedType)type;
			String typeName = paramType.getActualTypeArguments()[0].getTypeName();

			Log.out.println(paramType.getActualTypeArguments()[0].getTypeName());
			Log.out.println(paramType.getActualTypeArguments()[1].getTypeName());

			mContext.setMapKey(aKey);

			try
			{
				mContext.getParent().getField().setAccessible(true);
				if (mContext.getParent().getField().get(mContext.getParent().getParent().getObject()) == null)
				{
					mContext.getParent().getField().set(mContext.getParent().getParent().getObject(), new HashMap());
				}

				mContext.setType(Class.forName(typeName));
				mContext.setMapValueType(Class.forName(paramType.getActualTypeArguments()[1].getTypeName()));
			}
			catch (IllegalAccessException | ClassNotFoundException e)
			{
				throw new IOException("Unsupported generic type: " + typeName, e);
			}
		}
		else
		{
			try
			{
				mContext.setField(mContext.getObject().getClass().getDeclaredField(aKey));
				mContext.getField().setAccessible(true);

				Type type = mContext.getField().getGenericType();
				if (type instanceof ParameterizedType)
				{
					ParameterizedType paramType = (ParameterizedType)type;
					String typeName = paramType.getActualTypeArguments()[0].getTypeName();

					try
					{
						mContext.setType(Class.forName(typeName));
					}
					catch (ClassNotFoundException e)
					{
						throw new IOException("Unsupported generic type: " + typeName, e);
					}
				}
				else
				{
					mContext.setType(mContext.getField().getType());
				}
			}
			catch (NoSuchFieldException | SecurityException e)
			{
				throw new IOException(e);
			}
		}
	}


	public void endElement() throws IOException
	{
		print(-1, "end element");

		if (mContext.getParent() == null || !Map.class.isAssignableFrom(mContext.getParent().getObject().getClass()))
		{
//			try
//			{
//				mContext.getParent().getField().set(mContext.getParent().getObject(), castArray((List)mContext.getObject()));
//			}
//			catch (IllegalArgumentException | IllegalAccessException e)
//			{
//				throw new IOException(e);
//			}
		}
		
		mContext.end();
	}


	public void startObject() throws IOException
	{
		print(1, "start object " + mContext.getType());

		mContext.create();
		
		try
		{
			mContext.setObject(mContext.getType().newInstance());
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IOException(e);
		}

		if (mOutput == null)
		{
			mOutput = mContext.getObject();
		}
	}


	public void endObject() throws IOException
	{
		print(-1, "end object");

		if (mContext.getParent().getArray() != null)
		{
			mContext.getParent().getArray().add(mContext.getObject());
		}
		else if (mContext.getParent().getObject() != null)
		{
			try
			{
				mContext.getParent().getField().set(mContext.getParent().getObject(), mContext.getObject());
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
		mContext.setArray(new ArrayList());
		mContext.setObject(mContext.getArray());
	}


	public void endArray() throws IOException
	{
		print(-1, "end array");

		if (mContext.getParent() == null || !List.class.isAssignableFrom(mContext.getParent().getObject().getClass()))
		{
			try
			{
				mContext.getParent().getField().set(mContext.getParent().getObject(), castArray((List)mContext.getObject()));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				throw new IOException(e);
			}
		}
		else
		{
			mContext.getParent().getArray().add(mContext.getArray());
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
			Class type = mContext.getType();
			Object value = cast(aValue, type);

			if (mContext.getMapValueType() != null)
			{
				Log.out.println("###"+mContext.getMapKey()+" = "+aValue);
				Log.out.println("***"+mContext.getParent().getParent().getObject().getClass());
				Log.out.println("***"+mContext.getField());
				Log.out.println("***"+mContext.getField().get(mContext.getParent().getParent().getObject()).getClass());

				Map map = (Map)mContext.getField().get(mContext.getParent().getParent().getObject());
				map.put(mContext.getMapKey(), aValue);
				
				Log.out.println(map);
			}
			else if (mContext.getArray() == mContext.getObject())
			{
				mContext.getArray().add(value);
			}
			else
			{
				mContext.getField().set(mContext.getObject(), value);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new IOException(e);
		}

		print(0, mContext.getField() + " = " + aValue);
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
		Class type = mContext.getParent().getField().getType();

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


}

class Context
{
	private Context parent;
	private Context child;
	private Object object;
	private Field field;
	private Class type;
	private List array;
//	private String typeName;

	private Object mapKey;
	private Class mapValueType;


	public void create()
	{
		Context ctx = new Context();
		ctx.field = field;
		ctx.object = object;
		ctx.type = type;
//		ctx.typeName = typeName;
		ctx.parent = parent;
		ctx.array = array;
		ctx.child = this;
		ctx.mapKey = mapKey;
		ctx.mapValueType = mapValueType;
		parent = ctx;
	}


	public void end()
	{
		field = parent.field;
		object = parent.object;
		type = parent.type;
//		typeName = parent.typeName;
		array = parent.array;
		parent = parent.parent;
		mapKey = parent.mapKey;
		mapValueType = parent.mapValueType;
		child = null;
	}


	public Context getParent()
	{
		return parent;
	}


	public void setParent(Context aParent)
	{
		this.parent = aParent;
	}


	public Context getChild()
	{
		return child;
	}


	public void setChild(Context aChild)
	{
		this.child = aChild;
	}


	public Object getObject()
	{
		return object;
	}


	public void setObject(Object aObject)
	{
		Log.out.println("setting object = " + aObject.getClass());
		this.object = aObject;
	}


	public Field getField()
	{
		return field;
	}


	public void setField(Field aField)
	{
		this.field = aField;
	}


	public Class getType()
	{
		return type;
	}


	public void setType(Class aType)
	{
		this.type = aType;
	}


	public List getArray()
	{
		return array;
	}


	public void setArray(List aArray)
	{
		this.array = aArray;
	}


	public Object getMapKey()
	{
		return mapKey;
	}


	public void setMapKey(Object aApKey)
	{
		this.mapKey = aApKey;
	}


	public Class getMapValueType()
	{
		return mapValueType;
	}


	public void setMapValueType(Class aApValueType)
	{
		this.mapValueType = aApValueType;
	}
}


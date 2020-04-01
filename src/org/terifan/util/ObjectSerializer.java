package org.terifan.util;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


public class ObjectSerializer
{
	private final static HashSet<Class> SIMPLE_TYPES = new HashSet<>(Arrays.asList(
		Boolean.class,
		Short.class,
		Character.class,
		Integer.class,
		Long.class,
		Float.class,
		Double.class,
		Boolean.TYPE,
		Short.TYPE,
		Character.TYPE,
		Integer.TYPE,
		Long.TYPE,
		Float.TYPE,
		Double.TYPE,
		String.class
	));

	private HashMap<Object, Integer> mReferences;


	public ObjectSerializer()
	{
		mReferences = new HashMap<>();
	}


	private byte[] serialize(Object aObject) throws IllegalAccessException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		serialize(aObject, baos, 0);

		return baos.toByteArray();
	}


	private void serialize(Object aObject, OutputStream aOutputStream, int aIndent) throws IllegalAccessException, IOException
	{
		if (mReferences.containsKey(aObject))
		{
			System.out.println(repeat(" ... ", 5 * aIndent) + "#" + mReferences.get(aObject));

			return;
		}

		mReferences.put(aObject, mReferences.size());

		Class<?> cls = aObject.getClass();

		if (!Serializable.class.isAssignableFrom(cls))
		{
			System.out.println(repeat(" ... ", 5 * aIndent) + cls.getCanonicalName() + " [ERROR: NOT-SERIALIZABLE]");
		}
		else if (Externalizable.class.isAssignableFrom(cls))
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (ObjectOutputStream oos = new ObjectOutputStream(baos))
			{
				((Externalizable)aObject).writeExternal(oos);
			}

			System.out.println(repeat(" ... ", 5 * aIndent) + cls.getCanonicalName() + " [EXTERNAL] <" + baos.size() + " bytes>");

			return;
		}
		else
		{
			System.out.println(repeat(" ... ", 5 * aIndent) + cls.getCanonicalName());
		}

		aIndent++;

		ArrayList<Field> fields = getDeclaredFields(cls, new ArrayList<>());

		for (Field field : fields)
		{
			field.setAccessible(true);

			Class<?> valueType = field.getType();
			int arrayDims = 0;

			while (valueType.isArray())
			{
				valueType = valueType.getComponentType();
				arrayDims++;
			}

			Object value = field.get(aObject);

			String typeName;
			if (SIMPLE_TYPES.contains(valueType))
			{
				typeName = field.getType().getSimpleName();
			}
			else
			{
				typeName = field.getType().getCanonicalName();
			}

			System.out.print(repeat(" ... ", 5 * aIndent) + typeName + " " + field.getName() + " = ");

			if (value == null || arrayDims == 0 && SIMPLE_TYPES.contains(valueType))
			{
				System.out.println(value);
			}
			else
			{
				System.out.println();

				if (arrayDims == 0)
				{
					serialize(value, aOutputStream, aIndent + 1);
				}
				else
				{
					serializeArray(value, aOutputStream, aIndent);
				}
			}
		}
	}


	private void serializeArray(Object aObject, OutputStream aOutputStream, int aIndent) throws IllegalAccessException, IOException
	{
		System.out.println(repeat(" ... ", 5 * aIndent) + "{");
		for (int i = 0; i < Array.getLength(aObject); i++)
		{
			Object value = Array.get(aObject, i);

			if (value == null)
			{
				System.out.println(repeat(" ... ", 5 * (aIndent + 1)) + "null");
			}
			else
			{
				Class<?> cls = value.getClass();

				if (cls.isArray())
				{
					serializeArray(value, aOutputStream, aIndent + 1);
				}
				else if (SIMPLE_TYPES.contains(cls))
				{
					System.out.println(repeat(" ... ", 5 * (aIndent + 1)) + value);
				}
				else
				{
					serialize(value, aOutputStream, aIndent + 1);
				}
			}
		}
		System.out.println(repeat(" ... ", 5 * aIndent) + "}");
	}


	private ArrayList<Field> getDeclaredFields(Class<?> aType, ArrayList<Field> aFields)
	{
		for (Field field : aType.getDeclaredFields())
		{
			if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT)) == 0)
			{
				aFields.add(field);
			}
		}

		Class<?> sup = aType.getSuperclass();

		if (sup != Object.class)
		{
			getDeclaredFields(sup, aFields);
		}

		return aFields;
	}


	private static String repeat(String aWord, int aLength)
	{
		char [] buf = new char[aLength];
		char [] src = aWord.toCharArray();
		for (int i = 0; i < aLength; )
		{
			for (int j = 0; i < aLength && j < src.length; j++, i++)
			{
				buf[i] = src[j];
			}
		}

		return new String(buf);
	}


	private static class MyObject
	{
		GregorianCalendar c1;
		GregorianCalendar c2;
		GregorianCalendar[] cc;
		Object[][] x;
		MyExternal ext = new MyExternal();
	}


	private static class MyExternal implements Externalizable
	{
		@Override
		public void writeExternal(ObjectOutput aOut) throws IOException
		{
			aOut.write(1);
			aOut.writeUTF("test");
		}


		@Override
		public void readExternal(ObjectInput aIn) throws IOException, ClassNotFoundException
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}


	public static void main(String ... args)
	{
		try
		{
			GregorianCalendar c = new GregorianCalendar(2019, 10, 29);

			MyObject obj = new MyObject();
			obj.c1 = c;
			obj.c2 = c;
			obj.cc = new GregorianCalendar[]{c,c,c};
			obj.x = new Object[][]{{1,c,3},{c},null,{"test"}};

			new ObjectSerializer().serialize(obj);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ObjectStringifier
{
	private static final String INTENT = "   ";

	private String mStatementTerminator;
	private boolean mStatementTerminatorOnLast;
	private boolean mSimpleClassNames;
	private boolean mFieldTypes;
	private boolean mShortKnownTypes;
	private boolean mIndent;
	private int mArrayLimit;
	private HashMap<Object, Integer> mVisitedObjects;
	private String mArrayLimitSymbol;


	public ObjectStringifier()
	{
		mStatementTerminator = ";";
		mSimpleClassNames = false;
		mStatementTerminatorOnLast = true;
		mFieldTypes = true;
		mShortKnownTypes = true;
		mArrayLimit = 100;
		mArrayLimitSymbol = "...";
		mVisitedObjects = new HashMap<>();
		mIndent = true;
	}


	public static String toString(Object aObject)
	{
		if (aObject == null)
		{
			return null;
		}

		return new ObjectStringifier().asString(aObject);
	}


	public static String toShortString(Object aObject)
	{
		if (aObject == null)
		{
			return null;
		}

		return new ObjectStringifier()
			.setShortKnownTypes(true)
			.setIndent(false)
			.setFieldTypes(false)
			.setArrayLimit(10)
			.setStatementTerminator(", ")
			.setStatementTerminatorOnLast(false)
			.asString(aObject);
	}


	public int getArrayLimit()
	{
		return mArrayLimit;
	}


	public ObjectStringifier setArrayLimit(int aArrayLimit)
	{
		mArrayLimit = aArrayLimit;
		return this;
	}


	public String getArrayLimitSymbol()
	{
		return mArrayLimitSymbol;
	}


	public ObjectStringifier setArrayLimitSymbol(String aArrayLimitSymbol)
	{
		mArrayLimitSymbol = aArrayLimitSymbol;
		return this;
	}


	public boolean isFieldTypes()
	{
		return mFieldTypes;
	}


	public ObjectStringifier setFieldTypes(boolean aFieldTypes)
	{
		mFieldTypes = aFieldTypes;
		return this;
	}


	public boolean isSimpleClassNames()
	{
		return mSimpleClassNames;
	}


	public ObjectStringifier setSimpleClassNames(boolean aShortTypes)
	{
		mSimpleClassNames = aShortTypes;
		return this;
	}


	public boolean isStatementTerminatorOnLast()
	{
		return mStatementTerminatorOnLast;
	}


	public ObjectStringifier setStatementTerminatorOnLast(boolean aStatementTerminatorOnLast)
	{
		mStatementTerminatorOnLast = aStatementTerminatorOnLast;
		return this;
	}


	public String getStatementTerminator()
	{
		return mStatementTerminator;
	}


	public ObjectStringifier setStatementTerminator(String aStatementTerminator)
	{
		mStatementTerminator = aStatementTerminator;
		return this;
	}


	public boolean isShortKnownTypes()
	{
		return mShortKnownTypes;
	}


	public ObjectStringifier setShortKnownTypes(boolean aShortKnownTypes)
	{
		mShortKnownTypes = aShortKnownTypes;
		return this;
	}


	public boolean isIndent()
	{
		return mIndent;
	}


	public ObjectStringifier setIndent(boolean aIndent)
	{
		mIndent = aIndent;
		return this;
	}


	public String asString(Object aObject)
	{
		if (aObject == null)
		{
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrintStream ps = new PrintStream(baos))
		{
			printObject(aObject, aObject.getClass(), ps, 0, true);
		}

		if (mIndent)
		{
			return new String(baos.toByteArray());
		}

		return new String(baos.toByteArray()).replace("\r", "").replace("\n", "").replace(INTENT, "");
	}


	public void print(Object aObject)
	{
		print(aObject, System.out);
	}


	public void print(Object aObject, PrintStream aPrintStream)
	{
		printObject(aObject, aObject.getClass(), aPrintStream, 0, true);
		aPrintStream.println();
	}


	private void printObject(Object aObject, Class aClass, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		String indent = Strings.repeat(INTENT, INTENT.length() * aLevel);

		if (printValue(aObject, aClass, aPrintStream, mVisitedObjects, indent))
		{
			return;
		}

		if (aClass.isArray())
		{
			String typeName = getTypeName(aClass);
			if (typeName.startsWith("class "))
			{
				typeName = typeName.substring(6);
			}

			aPrintStream.println(indent + typeName);

			if (aClass.getSuperclass() != Object.class)
			{
				printObject(aObject, aClass.getSuperclass(), aPrintStream, aLevel + 1, false);
				aPrintStream.println();
			}

			printObjectClass(aObject, aClass, aPrintStream, aLevel + 1, aLast);

			printArray(aObject, aClass.getComponentType(), aPrintStream, indent, aLevel, mVisitedObjects, aLast);
		}
		else
		{
			if (true)
			{
				String typeName = getTypeName(aClass);
				aPrintStream.println(indent + typeName + "{");
			}
			else
			{
				aPrintStream.println(indent + "{");
			}

			if (aClass.getSuperclass() != Object.class)
			{
				printObject(aObject, aClass.getSuperclass(), aPrintStream, aLevel + 1, false);
				aPrintStream.println();
			}

			printObjectClass(aObject, aClass, aPrintStream, aLevel + 1, aLast);

			aPrintStream.print(indent + "}");
		}
	}


	private String getTypeName(Class aClass)
	{
		if (Number.class.isAssignableFrom(aClass) || aClass == String.class)
		{
			return aClass.getSimpleName();
		}

		return mSimpleClassNames ? aClass.getSimpleName() : aClass.getTypeName();
	}


	private void printObjectClass(Object aObject, Class aClass, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		int level = aLevel;
		String indent = Strings.repeat(INTENT, INTENT.length() * aLevel);

		if (printValue(aObject, aClass, aPrintStream, mVisitedObjects, indent))
		{
			return;
		}

		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : aClass.getDeclaredFields())
		{
			if ((field.getModifiers() & Modifier.STATIC) != 0)
			{
				continue;
			}
			fields.add(field);
		}

		for (Field field : fields)
		{
			boolean last = field == fields.get(fields.size() - 1);
			Object value = getValue(aObject, field);
			Class type = field.getType();

			String typeName;
			if (type.isArray())
			{
				Class tmp = type.getComponentType();
				StringBuilder suffix = new StringBuilder("[]");
				while (tmp.isArray())
				{
					tmp = tmp.getComponentType();
					suffix.append("[]");
				}
				typeName = getTypeName(tmp) + suffix;
			}
			else
			{
				typeName = getTypeName(field.getType());
			}

			if (mFieldTypes)
			{
				aPrintStream.print(indent + typeName + " " + field.getName() + "=");
			}
			else
			{
				aPrintStream.print(indent + field.getName() + "=");
			}

			if (type.isArray())
			{
				printArray(value, type.getComponentType(), aPrintStream, indent, level, mVisitedObjects, true);
				if (!last || mStatementTerminatorOnLast)
				{
					aPrintStream.print(mStatementTerminator);
				}
			}
			else if (printValue(value, type, aPrintStream, mVisitedObjects, ""))
			{
				if (!last || mStatementTerminatorOnLast)
				{
					aPrintStream.print(mStatementTerminator);
				}
			}
			else
			{
				Integer ref = mVisitedObjects.get(value);
				if (ref != null)
				{
					printRef(aPrintStream, ref, value);
				}
				else
				{
					mVisitedObjects.put(value, mVisitedObjects.size() + 1);

					aPrintStream.println();
					printObject(value, value.getClass(), aPrintStream, aLevel + 1, last);
					aPrintStream.println();
				}
			}
			aPrintStream.println();
		}
	}


	private void printRef(PrintStream aPrintStream, Integer aRef, Object aValue)
	{
		aPrintStream.print("#ref" + aRef + "[" + getTypeName(aValue.getClass()) + "]");
	}


	private void printArray(Object aValue, Class aComponentType, PrintStream aPrintStream, String aIndent, int aLevel, HashMap<Object, Integer> aVisitedObjects, boolean aLast)
	{
		if (aValue == null)
		{
			aPrintStream.println("null");
			return;
		}

		if (Number.class.isAssignableFrom(aComponentType) || aComponentType.isPrimitive())
		{
			aPrintStream.println("{");
			aPrintStream.print(aIndent + INTENT);
			for (int i = 0; i < Array.getLength(aValue); i++)
			{
				if (i > 0)
				{
					aPrintStream.print(", ");
				}
				if (i >= mArrayLimit)
				{
					aPrintStream.print(mArrayLimitSymbol);
					break;
				}
				aPrintStream.print(Array.get(aValue, i));
			}
			aPrintStream.println();
			aPrintStream.print(aIndent + "}");
		}
		else
		{
			aPrintStream.print("{");
			aPrintStream.println(aIndent + INTENT);
			int len = Array.getLength(aValue);
			for (int i = 0; i < len; i++)
			{
				boolean last = i == len - 1;
				Object value = Array.get(aValue, i);
				if (value == null)
				{
					aPrintStream.print(aIndent + INTENT);
					aPrintStream.print("null");
					aPrintStream.println(last ? "" : ",");
				}
				else if (value.getClass().isArray())
				{
					aPrintStream.print(aIndent + INTENT);
					printArray(value, value.getClass().getComponentType(), aPrintStream, aIndent + INTENT, aLevel + 1, aVisitedObjects, last);
					aPrintStream.println(last ? "" : ",");
				}
				else
				{
					printObject(value, value.getClass(), aPrintStream, aLevel + 1, last);
					aPrintStream.println(last ? "" : ",");
				}
				if (i >= mArrayLimit)
				{
					aPrintStream.println(mArrayLimitSymbol);
					break;
				}
			}
			aPrintStream.print(aIndent + "}");
		}
	}


	private boolean printValue(Object aValue, Class aType, PrintStream aPrintStream, HashMap<Object, Integer> aVisitedObjects, String aIndent)
	{
		if (aValue == null || aType.isPrimitive() || Number.class.isAssignableFrom(aType) || Enum.class.isAssignableFrom(aType) || aType == Boolean.class || aType == Character.class)
		{
			aPrintStream.print(aIndent + aValue);
		}
		else if (aType == String.class)
		{
			aPrintStream.print(aIndent + "\"" + aValue.toString().replace("\"","\\\"").replace("\t","\\\t").replace("\n","\\\n").replace("\r","\\\r") + "\"");
		}
		else
		{
//			Integer ref = aVisitedObjects.get(aValue);
//			if (ref != null)
//			{
//				printRef(aPrintStream, ref, aValue);
//			}
//			else
				if (mShortKnownTypes)
			{
				if (java.util.Date.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Date" : "java.util.Date") + "(\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date)aValue) + "\")");
				}
				else if (java.util.Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Calendar" : "java.util.Calendar") + "(\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(((java.util.Calendar)aValue).getTimeInMillis())) + "\")");
				}
				else if (Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Calendar" : "org.terifan.util.Calendar") + "(\"" + ((Calendar)aValue).format("yyyy-MM-dd HH:mm:ss.SSS") + "\")");
				}
				else if (UUID.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "UUID" : "java.util.UUID") + "(\"" + aValue + "\")");
				}
				else if (List.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent);
					printArray(((List)aValue).toArray(), Object.class, aPrintStream, aIndent + INTENT, 1, aVisitedObjects, true);
				}
				else
				{
					return false;
				}

				aVisitedObjects.put(aValue, aVisitedObjects.size() + 1);
			}
			else
			{
				return false;
			}
		}

		return true;
	}


	private Object getValue(Object aObject, Field aField)
	{
		try
		{
			aField.setAccessible(true);

			return aField.get(aObject);
		}
		catch (IllegalAccessException e)
		{
			return null;
		}
	}


	private static class RecTest1
	{
		RecTest2 recTest2;
	}


	private static class RecTest2
	{
		RecTest3 recTest3;
	}


	private static class RecTest3
	{
		RecTest1 recTest1;
	}


	public static void main(String... args)
	{
		try
		{
			RecTest1 recTest1 = new RecTest1();
			recTest1.recTest2 = new RecTest2();
			recTest1.recTest2.recTest3 = new RecTest3();
			recTest1.recTest2.recTest3.recTest1 = recTest1;

			String s = new ObjectStringifier()
//				.setFieldTypes(false)
//				.setSimpleClassNames(true)
				.setArrayLimit(10)
				.setShortKnownTypes(false)
//				.setIndent(false)
				.asString(recTest1);

			System.out.println(s);

			s = new ObjectStringifier()
//				.setFieldTypes(false)
//				.setSimpleClassNames(true)
				.setArrayLimit(10)
				.setShortKnownTypes(false)
//				.setIndent(false)
				.asString(new Calendar[]
				{
					new Calendar(), new Calendar().roll("day", 1), new Calendar().roll("day", 2)
				}
			);

			System.out.println(s);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

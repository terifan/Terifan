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
import org.terifan.util.log.Log;


public class ObjectStringifier
{
	private String mStatementTerminator;
	private boolean mStatementTerminatorOnLast;
	private boolean mSimpleClassNames;
	private boolean mFieldTypes;
	private boolean mShortKnownTypes;
	private boolean mIndent;
	private int mArrayLimit;
	private HashMap<Object,Integer> mVisitedObjects;
	private String mArrayLimitSymbol;


	public ObjectStringifier()
	{
		mStatementTerminator = ";";
		mSimpleClassNames = false;
		mStatementTerminatorOnLast = true;
		mFieldTypes = true;
		mShortKnownTypes = true;
		mArrayLimit = 1000;
		mArrayLimitSymbol = " ...";
		mVisitedObjects = new HashMap<>();
		mIndent = true;
	}


	public static String toString(Object aObject)
	{
		return new ObjectStringifier()
			.setFieldTypes(false)
			.setSimpleClassNames(true)
			.setShortKnownTypes(true)
			.setStatementTerminator(", ")
			.setStatementTerminatorOnLast(false)
			.setIndent(true)
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrintStream ps = new PrintStream(baos))
		{
			printObject(aObject, aObject.getClass(), ps, 0, true);
		}
		if (mIndent)
		{
			return new String(baos.toByteArray());
		}
		else
		{
			return new String(baos.toByteArray()).replace("\r", "").replace("\n", "").replace("   ", "");
		}
	}


	public void print(Object aObject)
	{
		print(aObject, Log.out);
	}


	public void print(Object aObject, PrintStream aPrintStream)
	{
		printObject(aObject, aObject.getClass(), aPrintStream, 0, true);
		aPrintStream.println();
	}


	private void printObject(Object aObject, Class aClass, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		String indent = new String(new byte[3 * aLevel]).replace('\0',' ');

		if (printValue(aObject, aClass, aPrintStream, mVisitedObjects, indent))
		{
			return;
		}

		if (aClass.isArray())
		{
			String typeName = mSimpleClassNames ? aClass.getSimpleName() : aClass.getCanonicalName();
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

			printArray(aObject, aClass, aPrintStream, indent, aLevel, mVisitedObjects, aLast);
		}
		else
		{
			if (true)
			{
				String typeName = mSimpleClassNames ? aClass.getSimpleName() : aClass.toString();
				if (typeName.startsWith("class "))
				{
					typeName = typeName.substring(6);
				}

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


	private void printObjectClass(Object aObject, Class aClass, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		int level = aLevel;
		String indent = new String(new byte[3*level]).replace('\0',' ');

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
			boolean last = field == fields.get(fields.size()-1);
			Object value = getValue(aObject, field);
			Class type = field.getType();

			String typeName;
			if (Number.class.isAssignableFrom(type) || type == String.class)
			{
				typeName = field.getType().getSimpleName();
			}
			else if (type.isArray())
			{
				Class tmp = type.getComponentType();
				StringBuilder suffix = new StringBuilder("[]");
				while (tmp.isArray())
				{
					tmp = tmp.getComponentType();
					suffix.append("[]");
				}
				typeName = tmp.getSimpleName() + suffix;
			}
			else
			{
				typeName = field.getType().toString();
				if (typeName.startsWith("class "))
				{
					typeName = typeName.substring(6);
				}
			}

			if (mFieldTypes)
			{
				aPrintStream.print(indent + typeName + " "+field.getName() + "=");
			}
			else
			{
				aPrintStream.print(indent + field.getName() + "=");
			}

			if (type.isArray())
			{
				printArray(value, type, aPrintStream, indent, level, mVisitedObjects, true);
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
					aPrintStream.print("#ref" + ref);
				}
				else
				{
					mVisitedObjects.put(value, mVisitedObjects.size()+1);
					aPrintStream.println();
					printObject(value, value.getClass(), aPrintStream, aLevel + 1, last);
					aPrintStream.println();
				}
			}
			aPrintStream.println();
		}
	}


	private void printArray(Object aValue, Class aType, PrintStream aPrintStream, String aIndent, int aLevel, HashMap<Object,Integer> aVisitedObjects, boolean aLast)
	{
		if (Number.class.isAssignableFrom(aType.getComponentType()) || aType.getComponentType().isPrimitive())
		{
			aPrintStream.println("{");
			aPrintStream.print(aIndent + "   ");
			for (int i = 0; i < Array.getLength(aValue); i++)
			{
				if (i > 0)
				{
					aPrintStream.print(", ");
				}
				aPrintStream.print(Array.get(aValue, i));
				if (i >= mArrayLimit)
				{
					aPrintStream.println(aIndent + mArrayLimitSymbol);
					break;
				}
			}
			aPrintStream.println();
			aPrintStream.print(aIndent + "}");
		}
		else
		{
			aPrintStream.print("{");
			aPrintStream.println(aIndent + "   ");
			int len = Array.getLength(aValue);
			for (int i = 0; i < len; i++)
			{
				boolean last = i == len - 1;
				Object value = Array.get(aValue, i);
				if (value == null)
				{
					aPrintStream.print(aIndent + "   ");
					aPrintStream.print("null");
					aPrintStream.println(last ? "" : ",");
				}
				else if (value.getClass().isArray())
				{
					aPrintStream.print(aIndent + "   ");
					printArray(value, value.getClass(), aPrintStream, aIndent + "   ", aLevel + 1, aVisitedObjects, last);
					aPrintStream.println(last ? "" : ",");
				}
				else
				{
					printObject(value, value.getClass(), aPrintStream, aLevel + 1, last);
					aPrintStream.println(last ? "" : ",");
				}
				if (i >= mArrayLimit)
				{
					aPrintStream.println(aIndent + mArrayLimitSymbol);
					break;
				}
			}
			aPrintStream.print(aIndent + "}");
		}
	}


	private boolean printValue(Object aValue, Class aType, PrintStream aPrintStream, HashMap<Object,Integer> aVisitedObjects, String aIndent)
	{
		if (aValue == null || aType.isPrimitive() || Number.class.isAssignableFrom(aType) || Enum.class.isAssignableFrom(aType))
		{
			aPrintStream.print(aIndent + aValue);
		}
		else if (aType == String.class)
		{
			aPrintStream.print(aIndent + "\""+aValue+"\"");
		}
		else
		{
			Integer ref = aVisitedObjects.get(aValue);
			if (ref != null)
			{
				aPrintStream.print(aIndent + "#ref" + ref);
			}
			else if (mShortKnownTypes)
			{
				if (java.util.Date.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Date":"java.util.Date") + "(\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date)aValue)+"\")");
				}
				else if (java.util.Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Calendar":"java.util.Calendar") + "(\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(((java.util.Calendar)aValue).getTimeInMillis()))+"\")");
				}
				else if (Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + (mSimpleClassNames ? "Calendar":"org.terifan.util.Calendar") + "(\"" + ((Calendar)aValue).format("yyyy-MM-dd HH:mm:ss.SSS")+"\")");
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


	public static void main(String ... args)
	{
		try
		{
			String s = new ObjectStringifier()
				.setFieldTypes(false)
				.setSimpleClassNames(true)
				.setStatementTerminator(", ")
				.setStatementTerminatorOnLast(false)
				.setArrayLimit(10)
				.setShortKnownTypes(false)
//				.setIndent(false)
				.asString(new Calendar[]{new Calendar(),new Calendar().roll("day", 1),new Calendar().roll("day", 2)});

			System.out.println(s);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
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
import org.terifan.util.Calendar;
import org.terifan.util.log.Log;


public class ObjectSerializer
{
	private String mStatementTerminator;
	private boolean mStatementTerminatorOnLast;
	private boolean mSimpleClassNames;
	private boolean mFieldTypes;
	private int mArrayLimit;
	private HashMap<Object,Integer> mVisitedObjects;
	private String mArrayLimitSymbol;


	public ObjectSerializer()
	{
		mStatementTerminator = ";";
		mSimpleClassNames = false;
		mStatementTerminatorOnLast = true;
		mFieldTypes = true;
		mArrayLimit = Integer.MAX_VALUE;
		mArrayLimitSymbol = " ...";
		mVisitedObjects = new HashMap<>();
	}


	public static String toString(Object aObject)
	{
		String s = new ObjectSerializer()
			.setFieldTypes(false)
			.setSimpleClassNames(true)
			.setStatementTerminator(", ")
			.setStatementTerminatorOnLast(false)
			.setArrayLimit(10)
			.asString(aObject);

		int i = s.indexOf("{");
		if (i != -1)
		{
			s = s.substring(i);
		}

		return s;
	}


	public int getArrayLimit()
	{
		return mArrayLimit;
	}


	public ObjectSerializer setArrayLimit(int aArrayLimit)
	{
		mArrayLimit = aArrayLimit;
		return this;
	}


	public String getArrayLimitSymbol()
	{
		return mArrayLimitSymbol;
	}


	public ObjectSerializer setArrayLimitSymbol(String aArrayLimitSymbol)
	{
		mArrayLimitSymbol = aArrayLimitSymbol;
		return this;
	}


	public boolean isFieldTypes()
	{
		return mFieldTypes;
	}


	public ObjectSerializer setFieldTypes(boolean aFieldTypes)
	{
		mFieldTypes = aFieldTypes;
		return this;
	}


	public boolean isSimpleClassNames()
	{
		return mSimpleClassNames;
	}


	public ObjectSerializer setSimpleClassNames(boolean aShortTypes)
	{
		mSimpleClassNames = aShortTypes;
		return this;
	}


	public boolean isStatementTerminatorOnLast()
	{
		return mStatementTerminatorOnLast;
	}


	public ObjectSerializer setStatementTerminatorOnLast(boolean aStatementTerminatorOnLast)
	{
		mStatementTerminatorOnLast = aStatementTerminatorOnLast;
		return this;
	}


	public String getStatementTerminator()
	{
		return mStatementTerminator;
	}


	public ObjectSerializer setStatementTerminator(String aStatementTerminator)
	{
		mStatementTerminator = aStatementTerminator;
		return this;
	}


	public String asString(Object aObject)
	{
		if (aObject == null)
		{
			return "null";
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrintStream ps = new PrintStream(baos))
		{
			printObject(aObject, aObject.getClass(), ps, 0, true);
		}
		return new String(baos.toByteArray()).replace("\r","").replace("\n","").replace("   ","");
	}


	public void print(Object aObject, PrintStream aPrintStream)
	{
		printObject(aObject, aObject.getClass(), aPrintStream, 0, true);
		aPrintStream.println();
	}


	private void printObject(Object aObject, Class cls, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		String indent = new String(new byte[3 * aLevel]).replace('\0',' ');

		if (printValue(aObject, cls, aPrintStream, indent))
		{
			return;
		}

		if (true)
		{
			String typeName = mSimpleClassNames ? cls.getSimpleName() : cls.toString();
			if (typeName.startsWith("class "))
			{
				typeName = typeName.substring(6);
			}
			if (typeName.startsWith("interface "))
			{
				typeName = typeName.substring(10);
			}

			aPrintStream.println(indent + typeName + "{");
		}
		else
		{
			aPrintStream.println(indent + "{");
		}

		if (cls.getSuperclass() != Object.class)
		{
			printObject(aObject, cls.getSuperclass(), aPrintStream, aLevel + 1, false);
			aPrintStream.println();
		}

		printObjectClass(aObject, cls, aPrintStream, aLevel + 1, aLast);

		aPrintStream.print(indent + "}");
	}


	private void printObjectClass(Object aObject, Class cls, PrintStream aPrintStream, int aLevel, boolean aLast)
	{
		String indent = new String(new byte[3*aLevel]).replace('\0',' ');

		if (printValue(aObject, cls, aPrintStream, indent))
		{
			return;
		}

		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : cls.getDeclaredFields())
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
				String suffix = "[]";
				while (tmp.isArray())
				{
					tmp = tmp.getComponentType();
					suffix += "[]";
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
				if (typeName.startsWith("interface "))
				{
					typeName = typeName.substring(10);
				}
				if (typeName.startsWith("java.lang.") || typeName.startsWith("java.util."))
				{
					typeName = typeName.substring(10);
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

			if (value == null)
			{
				aPrintStream.print("null");
				if (!last || mStatementTerminatorOnLast)
				{
					aPrintStream.print(mStatementTerminator);
				}
			}
			else if (type.isArray())
			{
				printArray(value, type, aPrintStream, indent, aLevel, true);
				if (!last || mStatementTerminatorOnLast)
				{
					aPrintStream.print(mStatementTerminator);
				}
			}
			else if (printValue(value, type, aPrintStream, ""))
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
					aPrintStream.println();
					mVisitedObjects.put(value, mVisitedObjects.size() + 1);
					printObject(value, value.getClass(), aPrintStream, aLevel + 1, last);
				}
				if (!last || mStatementTerminatorOnLast)
				{
					aPrintStream.print(mStatementTerminator);
				}
			}
			aPrintStream.println();
		}
	}


	private void printArray(Object aValue, Class aType, PrintStream aPrintStream, String aIndent, int aLevel, boolean aLast) throws ArrayIndexOutOfBoundsException, IllegalArgumentException
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
					aPrintStream.println(mArrayLimitSymbol);
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
					printArray(value, value.getClass(), aPrintStream, aIndent + "   ", aLevel + 1, last);
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


	private boolean printValue(Object aValue, Class aType, PrintStream aPrintStream, String aIndent)
	{
		if (aValue == null || aType == null || aType.isPrimitive() || Number.class.isAssignableFrom(aType) || Enum.class.isAssignableFrom(aType) || Character.class.isAssignableFrom(aType))
		{
			aPrintStream.print(aIndent + aValue);
		}
		else if (aType == String.class)
		{
			aPrintStream.print(aIndent + "\""+aValue+"\"");
		}
		else
		{
			Integer ref = mVisitedObjects.get(aValue);
			if (ref != null)
			{
				aPrintStream.print(aIndent + "#ref" + ref);
			}
			else
			{
				if (java.util.Date.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + "\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date)aValue)+"\"");
				}
				else if (java.util.Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + "\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(((java.util.Calendar)aValue).getTimeInMillis()))+"\"");
				}
				else if (Calendar.class.isAssignableFrom(aType))
				{
					aPrintStream.print(aIndent + "\""+((Calendar)aValue).format("yyyy-MM-dd HH:mm:ss.SSS")+"\"");
				}
				else
				{
					return false;
				}

				mVisitedObjects.put(aValue, mVisitedObjects.size() + 1);
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
}
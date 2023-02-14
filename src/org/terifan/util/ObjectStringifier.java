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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private HashSet<Class> mDoNotVisitTypes;
	private HashSet<Object> mDoNotVisitObjects;
	private HashSet<String> mDoNotVisitFields;
	private String mArrayLimitSymbol;
	private PrintStream mPrintStream;


	public ObjectStringifier()
	{
		mStatementTerminator = "; ";
		mSimpleClassNames = false;
		mStatementTerminatorOnLast = true;
		mFieldTypes = true;
		mShortKnownTypes = true;
		mArrayLimit = 100;
		mArrayLimitSymbol = "...";
		mVisitedObjects = new HashMap<>();
		mDoNotVisitTypes = new HashSet<>();
		mDoNotVisitFields = new HashSet<>();
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


	public ObjectStringifier doNotVisit(Class aType)
	{
		mDoNotVisitTypes.add(aType);
		return this;
	}


	public ObjectStringifier doNotVisit(Object aObject)
	{
		mDoNotVisitObjects.add(aObject);
		return this;
	}


	public ObjectStringifier doNotVisit(String aFieldName)
	{
		mDoNotVisitFields.add(aFieldName);
		return this;
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
			mPrintStream = ps;
			printObject(aObject, aObject.getClass(), 0, true);
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
		mPrintStream = aPrintStream;
		printObject(aObject, aObject.getClass(), 0, true);
		println();
	}


	private void printObject(Object aObject, Class aClass, int aLevel, boolean aLast)
	{
		String indent = Strings.repeat(INTENT, INTENT.length() * aLevel);

		if (printValue(aObject, aClass, mVisitedObjects, indent))
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

			println(indent + typeName);

			if (aClass.getSuperclass() != Object.class)
			{
				printObject(aObject, aClass.getSuperclass(), aLevel + 1, false);
				println();
			}

			printObjectClass(aObject, aClass, aLevel + 1, aLast);

			printArray(aObject, aClass.getComponentType(), indent, aLevel, mVisitedObjects, aLast);
		}
		else
		{
			if (true)
			{
				String typeName = getTypeName(aClass);
				println(indent + typeName + "{");
			}
			else
			{
				println(indent + "{");
			}

			if (aClass.getSuperclass() != Object.class)
			{
				printObject(aObject, aClass.getSuperclass(), aLevel + 1, false);
				println();
			}

			printObjectClass(aObject, aClass, aLevel + 1, aLast);

			print(indent + "} ");
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


	private void printObjectClass(Object aObject, Class aClass, int aLevel, boolean aLast)
	{
		int level = aLevel;
		String indent = Strings.repeat(INTENT, INTENT.length() * aLevel);

		if (printValue(aObject, aClass, mVisitedObjects, indent))
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
				print(indent + typeName + " " + field.getName() + " = ");
			}
			else
			{
				print(indent + field.getName() + " = ");
			}

			if (mDoNotVisitFields.contains(field.getName()))
			{
				print("<<>>");
			}
			else if (type.isArray())
			{
				printArray(value, type.getComponentType(), indent, level, mVisitedObjects, true);
				if (!last || mStatementTerminatorOnLast)
				{
					print(mStatementTerminator);
				}
			}
			else if (printValue(value, type, mVisitedObjects, ""))
			{
				if (!last || mStatementTerminatorOnLast)
				{
					print(mStatementTerminator);
				}
			}
			else
			{
				Integer ref = mVisitedObjects.get(value);
				if (ref != null)
				{
					printRef(ref, value);
				}
				else
				{
					mVisitedObjects.put(value, mVisitedObjects.size() + 1);

					println();
					printObject(value, value.getClass(), aLevel + 1, last);
					println();
				}
			}
			println();
		}
	}


	private void printRef(Integer aRef, Object aValue)
	{
		print("#ref" + aRef + "[" + getTypeName(aValue.getClass()) + "]");
	}


	private void printArray(Object aValue, Class aComponentType, String aIndent, int aLevel, HashMap<Object, Integer> aVisitedObjects, boolean aLast)
	{
		if (aValue == null)
		{
			println("null");
			return;
		}

		if (Number.class.isAssignableFrom(aComponentType) || aComponentType.isPrimitive())
		{
			println("{");
			print(aIndent + INTENT);
			for (int i = 0; i < Array.getLength(aValue); i++)
			{
				if (i > 0)
				{
					print(", ");
				}
				if (i >= mArrayLimit)
				{
					print(mArrayLimitSymbol);
					break;
				}
				print(Array.get(aValue, i));
			}
			println();
			print(aIndent + "} ");
		}
		else
		{
			print("{");
			println(aIndent + INTENT);
			int len = Array.getLength(aValue);
			for (int i = 0; i < len; i++)
			{
				boolean last = i == len - 1;
				Object value = Array.get(aValue, i);
				if (value == null)
				{
					print(aIndent + INTENT);
					print("null");
					println(last ? "" : ",");
				}
				else if (value.getClass().isArray())
				{
					print(aIndent + INTENT);
					printArray(value, value.getClass().getComponentType(), aIndent + INTENT, aLevel + 1, aVisitedObjects, last);
					println(last ? "" : ",");
				}
				else
				{
					printObject(value, value.getClass(), aLevel + 1, last);
					println(last ? "" : ",");
				}
				if (i >= mArrayLimit)
				{
					println(mArrayLimitSymbol);
					break;
				}
			}
			print(aIndent + "} ");
		}
	}


	private boolean printValue(Object aValue, Class aType, HashMap<Object, Integer> aVisitedObjects, String aIndent)
	{
		if (mDoNotVisitTypes.contains(aType))
		{
			print(aIndent + "<<" + aType + ">> ");
		}
		else if (aValue == null || aType == null || aType.isPrimitive() || Number.class.isAssignableFrom(aType) || Enum.class.isAssignableFrom(aType) || aType == Boolean.class || aType == Character.class)
		{
			print(aIndent + aValue);
		}
		else if (aType == String.class)
		{
			print(aIndent + "\"" + aValue.toString().replace("\"","\\\"").replace("\t","\\\t").replace("\n","\\\n").replace("\r","\\\r") + "\"");
		}
		else
		{
//			Integer ref = aVisitedObjects.get(aValue);
//			if (ref != null)
//			{
//				printRef(ref, aValue);
//			}
//			else
				if (mShortKnownTypes)
			{
				if (java.util.Date.class.isAssignableFrom(aType))
				{
					print(aIndent + (mSimpleClassNames ? "Date" : "java.util.Date") + "(\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date)aValue) + "\")");
				}
				else if (java.util.Calendar.class.isAssignableFrom(aType))
				{
					print(aIndent + (mSimpleClassNames ? "Calendar" : "java.util.Calendar") + "(\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(((java.util.Calendar)aValue).getTimeInMillis())) + "\")");
				}
				else if (Calendar.class.isAssignableFrom(aType))
				{
					print(aIndent + (mSimpleClassNames ? "Calendar" : "org.terifan.util.Calendar") + "(\"" + ((Calendar)aValue).format("yyyy-MM-dd HH:mm:ss.SSS") + "\")");
				}
				else if (UUID.class.isAssignableFrom(aType))
				{
					print(aIndent + (mSimpleClassNames ? "UUID" : "java.util.UUID") + "(\"" + aValue + "\")");
				}
				else if (List.class.isAssignableFrom(aType))
				{
					print(aIndent);
					printArray(((List)aValue).toArray(), Object.class, aIndent + INTENT, 1, aVisitedObjects, true);
				}
				else if (Set.class.isAssignableFrom(aType))
				{
					print(aIndent);
					printArray(((Set)aValue).toArray(), Object.class, aIndent + INTENT, 1, aVisitedObjects, true);
				}
				else if (Map.class.isAssignableFrom(aType))
				{
					print(aIndent);
					printArray(((Map)aValue).keySet().toArray(), Object.class, aIndent + INTENT, 1, aVisitedObjects, true);
					printArray(((Map)aValue).values().toArray(), Object.class, aIndent + INTENT, 1, aVisitedObjects, true);
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
		catch (Exception | Error e)
		{
			return null;
		}
	}


	private void print(String aText)
	{
		mPrintStream.print(aText);
	}


	private void println(String aText)
	{
		mPrintStream.println(aText);
	}


	private void println()
	{
		mPrintStream.println();
	}
}

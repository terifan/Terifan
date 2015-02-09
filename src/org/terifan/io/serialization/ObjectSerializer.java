package org.terifan.io.serialization;

import java.io.IOException;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.terifan.util.Result;


/**
 * Supported complex Java objects: java.util.map, java.util.set, java.util.list, java.util.date, java.util.calendar
 */
public class ObjectSerializer
{
	private Writer mWriter;
	private Reader mReader;


	public ObjectSerializer(Writer aWriter) throws IOException
	{
		mWriter = aWriter;
	}


	public ObjectSerializer(Reader aReader) throws IOException
	{
		mReader = aReader;
	}


	public void serialize(Object aObject) throws IOException
	{
		mWriter.startOutput();

		serializeObject(aObject, new HashSet<>());

		mWriter.endOutput();
	}


	private void serializeObject(Object aObject, HashSet<Object> aVisitedObjects) throws IOException
	{
		if (!aVisitedObjects.add(aObject))
		{
			return;
		}

		ArrayList<Property> entries = getEntries(aObject);

		mWriter.startObject(aObject, getTypeName(aObject.getClass(), false), entries.size());

		boolean first = true;

		for (Property entry : entries)
		{
			if (!first)
			{
				mWriter.nextProperty();
			}
			first = false;

			String name = entry.getName();
			Object value = entry.get();
			boolean primitive = entry.isPrimitive();
			String typeName = getTypeName(entry.getType(), primitive);

			mWriter.startProperty(entry, name, typeName);

			serializeValue(value, primitive, aVisitedObjects);

			mWriter.endProperty();
		}

		mWriter.endObject();
	}


	private void serializeValue(Object aObject, boolean aPrimitives, HashSet<Object> aVisitedObjects) throws IOException
	{
		if (aObject == null)
		{
			mWriter.writeNull();
			return;
		}

		if (aVisitedObjects.contains(aObject))
		{
			mWriter.writeReference(aObject);
			return;
		}

		Class valueType = aObject.getClass();

		if (valueType.isArray())
		{
			serializeArray(aObject, "array", aVisitedObjects);
		}
		else if (aPrimitives
			|| valueType.isPrimitive()
			|| valueType == Boolean.class
			|| valueType == Byte.class
			|| valueType == Short.class
			|| valueType == Character.class
			|| valueType == Integer.class
			|| valueType == Long.class
			|| valueType == Float.class
			|| valueType == Double.class
			|| valueType == String.class)
		{
			mWriter.writePrimitive(aObject, getTypeName(valueType, aPrimitives));
		}
		else if (Date.class.isAssignableFrom(valueType))
		{
			mWriter.writePrimitive(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date)aObject), "DateTime");
		}
		else if (Calendar.class.isAssignableFrom(valueType))
		{
			mWriter.writePrimitive(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(((Calendar)aObject).getTime()), "DateTime");
		}
		else if (List.class.isAssignableFrom(valueType))
		{
			serializeArray(((List)aObject).toArray(), "list", aVisitedObjects);
		}
		else if (Set.class.isAssignableFrom(valueType))
		{
			serializeArray(((Set)aObject).toArray(), "set", aVisitedObjects);
		}
		else if (Map.class.isAssignableFrom(valueType))
		{
			serializeMap((Map)aObject, aVisitedObjects);
		}
		else
		{
			serializeObject(aObject, new HashSet<>(aVisitedObjects));
		}
	}


	private void serializeArray(Object aObject, String aArrayType, HashSet<Object> aVisitedObjects) throws IOException
	{
		Class valueType = aObject.getClass();

		int depth = 1;
		Class componentType = valueType.getComponentType();
		while (componentType.isArray())
		{
			componentType = componentType.getComponentType();
			depth++;
		}

		boolean primitives = componentType.isPrimitive();

		Result<Boolean> hasNulls = new Result<>();
		Result<Class> type = new Result<>();
		scanList(aObject, hasNulls, type);

		int len = Array.getLength(aObject);

		mWriter.startArray(aObject, depth, len, getTypeName(type.get(), primitives), hasNulls.get(), aArrayType);

		for (int i = 0; i < len; i++)
		{
			Object value = Array.get(aObject, i);

			serializeValue(value, primitives, new HashSet<>(aVisitedObjects));

			if (i < len - 1)
			{
				mWriter.nextElement();
			}
		}

		mWriter.endArray();
	}


	private void serializeMap(Map aMap, HashSet<Object> aVisitedObjects) throws IOException
	{
		Object[] items = new Object[2 * aMap.size()];
		int i = 0;
		for (Object entry : aMap.entrySet())
		{
			items[i++] = ((Entry)entry).getKey();
			items[i++] = ((Entry)entry).getValue();
		}

		serializeArray(items, "map", new HashSet<>(aVisitedObjects));
	}


	private ArrayList<Property> getEntries(Object aObject) throws IOException
	{
		ArrayList<Property> fields = new ArrayList<>();
		HashSet<String> done = new HashSet<>();
		Class cls = aObject.getClass();

		while (cls != Object.class)
		{
			for (Field field : cls.getDeclaredFields())
			{
				if ((field.getModifiers() & (Modifier.FINAL | Modifier.TRANSIENT | Modifier.STATIC)) == 0 && done.add(field.getName()))
				{
					fields.add(new FieldProperty(aObject, field));
				}
			}
			cls = cls.getSuperclass();
		}

		return fields;
	}


	private void scanList(Object aArray, Result<Boolean> aHasNulls, Result<Class> aSingleType)
	{
		Class type = null;
		boolean singleType = true;
		boolean hasNulls = false;

		for (int i = 0; i < Array.getLength(aArray); i++)
		{
			Object value = Array.get(aArray, i);

			if (value == null)
			{
				hasNulls = true;
			}
			else if (singleType)
			{
				Class newType = value.getClass();

				if (type == null)
				{
					type = newType;
				}
				else if (newType != type)
				{
					singleType = false;
					type = null;
				}
			}
		}

		aHasNulls.set(hasNulls);
		aSingleType.set(type);
	}


	private String getTypeName(Class aType, boolean aPrimitives)
	{
		if (aType == null)
		{
			return "";
		}

		while (aType.isArray())
		{
			aType = aType.getComponentType();
		}

		if (aPrimitives)
		{
			if (aType == Boolean.class) return "boolean";
			if (aType == Byte.class) return "byte";
			if (aType == Short.class) return "short";
			if (aType == Character.class) return "char";
			if (aType == Integer.class) return "int";
			if (aType == Long.class) return "long";
			if (aType == Float.class) return "float";
			if (aType == Double.class) return "double";
		}
		else
		{
			if (aType == Boolean.class) return "Boolean";
			if (aType == Byte.class) return "Byte";
			if (aType == Short.class) return "Short";
			if (aType == Character.class) return "Charachter";
			if (aType == Integer.class) return "Integer";
			if (aType == Long.class) return "Long";
			if (aType == Float.class) return "Float";
			if (aType == Double.class) return "Double";
			if (aType == String.class) return "String";
		}

		return aType.getTypeName();
	}
	

	@Retention(RUNTIME)
	@Target({FIELD,TYPE})
	public @interface Serialize
	{
		String value() default "";
	}
}
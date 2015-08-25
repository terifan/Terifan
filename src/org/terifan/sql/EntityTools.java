package org.terifan.sql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.terifan.util.Calendar;
import org.terifan.util.Strings;


public class EntityTools
{
	public static <T> void populateEntity(T aEntity, ResultSet aResultSet) throws SQLException, SecurityException
	{
		for (Field field : getDeclaredFields(aEntity.getClass()))
		{
			String name = field.getName();

			Column column = field.getAnnotation(Column.class);
            PrimaryKey key = field.getAnnotation(PrimaryKey.class);

            if(column == null && key == null)
            {
                continue;
            }

			if (column != null && !Strings.isEmptyOrNull(column.name()))
			{
				name = column.name();
			}

			if (key != null && !Strings.isEmptyOrNull(key.name()))
			{
				name = key.name();
			}

			if (name != null)
			{
				try
				{
					field.setAccessible(true);

					Object value = aResultSet.getObject(name);

					value = convertValue(field.getType(), value);

					field.set(aEntity, value);
				}
				catch (Exception e)
				{
					Object value = aResultSet == null || Strings.isEmptyOrNull(name) ? "<empty column name>" : aResultSet.getObject(name);
					throw new SQLException("Problem setting item property " + name + " via field " + field + " to value " + value, e);
				}
			}
		}

		for (Method method : aEntity.getClass().getDeclaredMethods())
		{
			if (method.getName().startsWith("set"))
			{
				String name = null;

				Column column = method.getAnnotation(Column.class);
				if (column != null)
				{
					name = column.name();
				}

				PrimaryKey key = method.getAnnotation(PrimaryKey.class);
				if (key != null)
				{
					name = key.name();
				}

				if (name != null)
				{
					try
					{
						Object value = aResultSet.getObject(name);

						value = convertValue(method.getParameterTypes()[0], value);

						method.setAccessible(true);
						method.invoke(aEntity, value);
					}
					catch (Exception e)
					{
						throw new SQLException("Problem setting item property " + name + " via method " + method + " to value " + aResultSet.getObject(name), e);
					}
				}
			}
		}
	}


	public static <T> T convertValue(Class<T> aType, Object aValue) throws IllegalArgumentException
	{
		if (UUID.class.isAssignableFrom(aType))
		{
			if (aValue instanceof String)
			{
				aValue = UUID.fromString(aValue.toString());
			}
			else if (aValue != null)
			{
				throw new IllegalArgumentException("Failed to convert database field " + aValue.getClass() + " to java field " + aType);
			}
		}
		else if (Boolean.class.isAssignableFrom(aType) || Boolean.TYPE.isAssignableFrom(aType))
		{
			if (aValue instanceof Boolean)
			{
			}
			else if (aValue instanceof Integer)
			{
				aValue = ((int)(Integer)aValue) != 0;
			}
			else
			{
				throw new IllegalArgumentException("Failed to convert database field " + aValue.getClass() + " to java field " + aType);
			}
		}
		else if (Calendar.class.isAssignableFrom(aType))
		{
			if (aValue instanceof Date)
			{
				aValue = new Calendar(((Date)aValue).getTime());
			}
			else if (aValue instanceof String)
			{
				aValue = new Calendar((String)aValue);
			}
			else if (aValue != null)
			{
				throw new IllegalArgumentException("Failed to convert database field " + aValue.getClass() + " to java field " + aType);
			}
		}
		else if (Enum.class.isAssignableFrom(aType))
		{
			if (aValue instanceof String)
			{
				boolean found = false;

				for (Enum e : ((Class<Enum>)aType).getEnumConstants())
				{
					if (e.name().equals(aValue))
					{
						aValue = e;
						found = true;
						break;
					}
				}

				if (!found)
				{
					throw new IllegalArgumentException("Enum name not found for '" + aValue + "' in java type " + aType + ". Annotation enumName missing in Entity?");
				}
			}
			else if (aValue instanceof Integer || aValue != null && aValue.getClass() == Integer.TYPE)
			{
				aValue = getEnumConstant((Class<Enum>)aType, (Integer)aValue);
			}
			else if (aValue != null)
			{
				throw new IllegalArgumentException("Failed to convert database field " + aValue.getClass() + " to java field " + aType);
			}
		}
		else if (Integer.class.isAssignableFrom(aType) || Integer.TYPE.isAssignableFrom(aType))
		{
			if (aValue != null)
			{
				aValue = Integer.parseInt(aValue.toString());
			}
		}
		else if (Double.class.isAssignableFrom(aType) || Double.TYPE.isAssignableFrom(aType))
		{
			if (aValue != null)
			{
				aValue = Double.parseDouble(aValue.toString());
			}
		}
		else if (String.class.isAssignableFrom(aType))
		{
			if (aValue instanceof Clob)
			{
				try
				{
					Clob clob = (Clob)aValue;
					aValue = (T)clob.getSubString(1, (int)clob.length());
				}
				catch (SQLException e)
				{
					throw new IllegalStateException(e);
				}
			}
			else if (aValue != null)
			{
				aValue = aValue.toString();
			}
		}

//		Log.out.println("EntityTools:Convert: " + aType+" => "+input + " => " + aValue);

		return (T)aValue;
	}


	private static Object getEnumConstant(Class<Enum> aType, int aValue) throws IllegalArgumentException
	{
		boolean replacements = false;
		Enum[] enumConstants = aType.getEnumConstants();

		for (Enum ec : enumConstants)
		{
			EnumValue annotation = getEnumAnnotation(ec);

			if (annotation != null)
			{
				replacements = true;

				if (annotation.value() == aValue)
				{
					return ec;
				}
			}
		}

		if (replacements)
		{
			throw new IllegalArgumentException("Enum value not found: " + aValue + " in enum:" + aType);
		}

		return enumConstants[aValue - 1];
	}


	private static EnumValue getEnumAnnotation(Enum aEnum)
	{
		EnumValue annotation = null;
		try
		{
			Field field = aEnum.getClass().getField(aEnum.name());
			if (field.getType() == aEnum.getClass() && field.getAnnotations().length > 0)
			{
				annotation = field.getAnnotation(EnumValue.class);
			}
		}catch (NoSuchFieldException | SecurityException e)
		{
		}
		return annotation;
	}


	public static ArrayList<String> getColumns(AbstractEntity aEntity, boolean aIncludeKeys, boolean aIncludeValues, boolean aIncludeGenerated)
	{
		if (aEntity == null)
		{
			throw new IllegalArgumentException("Entity is null");
		}

		return getColumns(aEntity.getClass(), aIncludeKeys, aIncludeValues, aIncludeGenerated);
	}


	public static ArrayList<String> getColumns(Class aType, boolean aIncludeKeys, boolean aIncludeValues, boolean aIncludeGenerated)
	{
		ArrayList<String> list = new ArrayList<>();

		for (Field field : getDeclaredFields(aType))
		{
			String name = null;

			if (aIncludeValues)
			{
				Column column = field.getAnnotation(Column.class);
				if (column != null)
				{
					if (!aIncludeGenerated && column.generated())
					{
						continue;
					}
					if (Strings.isEmptyOrNull(column.name()))
					{
						name = field.getName();
					}
					else
					{
						name = column.name();
					}
				}
			}

			if (aIncludeKeys)
			{
				PrimaryKey key = field.getAnnotation(PrimaryKey.class);

				if (key != null)
				{
//					if (!aIncludeGenerated && key.generated())
//					{
//						continue;
//					}
					if (Strings.isEmptyOrNull(key.name()))
					{
						name = field.getName();
					}
					else
					{
						name = key.name();
					}
				}
			}

			if (name != null)
			{
				list.add(name);
			}
		}

		for (Method method : aType.getDeclaredMethods())
		{
			if (method.getName().startsWith("set") || method.getName().startsWith("get"))
			{
				String name = null;

				if (aIncludeValues)
				{
					Column column = method.getAnnotation(Column.class);
					if (column != null)
					{
						name = column.name();
					}
				}

				if (aIncludeKeys)
				{
					PrimaryKey key = method.getAnnotation(PrimaryKey.class);
					if (key != null)
					{
						name = key.name();
					}
				}

				if (name != null)
				{
					list.add(name);
				}
			}
		}

		return list;
	}


	public static Object getColumnValue(AbstractEntity aEntity, String aColumnName)
	{
		try
		{
			for (Method method : aEntity.getClass().getDeclaredMethods())
			{
				if (method.getName().startsWith("get"))
				{
					Column column = method.getAnnotation(Column.class);
					PrimaryKey key = method.getAnnotation(PrimaryKey.class);

					if (column != null && column.name().equals(aColumnName) || key != null && key.name().equals(aColumnName))
					{
						return method.invoke(aEntity);
					}
				}
			}

			for (Field field : getDeclaredFields(aEntity.getClass()))
			{
				Column column = field.getAnnotation(Column.class);
				PrimaryKey key = field.getAnnotation(PrimaryKey.class);

				if (column != null && (column.name().equals(aColumnName) || field.getName().equals(aColumnName)) || key != null && (key.name().equals(aColumnName) || field.getName().equals(aColumnName)))
				{
					field.setAccessible(true);

					return field.get(aEntity);
				}
			}

			throw new IllegalArgumentException("Column not found: " + aColumnName);
		}
		catch (SecurityException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static void setColumnValue(AbstractEntity aEntity, String aColumnName, Object aValue)
	{
		try
		{
			for (Method method : aEntity.getClass().getDeclaredMethods())
			{
				if (method.getName().startsWith("set"))
				{
					Column column = method.getAnnotation(Column.class);
					PrimaryKey key = method.getAnnotation(PrimaryKey.class);

					if (column != null && column.name().equals(aColumnName) || key != null && key.name().equals(aColumnName))
					{
						method.invoke(aEntity, aValue);
						return;
					}
				}
			}

			for (Field field : getDeclaredFields(aEntity.getClass()))
			{
				Column column = field.getAnnotation(Column.class);
				PrimaryKey key = field.getAnnotation(PrimaryKey.class);

				if (column != null && column.name().equals(aColumnName) || key != null && key.name().equals(aColumnName))
				{
					field.set(aEntity, aValue);
					return;
				}
			}

			throw new IllegalArgumentException("Column not found: " + aColumnName);
		}
		catch (SecurityException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static Column getColumn(AbstractEntity aEntity, String aColumnName)
	{
		try
		{
			for (Method method : aEntity.getClass().getDeclaredMethods())
			{
				if (method.getName().startsWith("get"))
				{
					Column column = method.getAnnotation(Column.class);

					if (column != null && column.name().equals(aColumnName))
					{
						return column;
					}
				}
			}

			for (Field field : getDeclaredFields(aEntity.getClass()))
			{
				Column column = field.getAnnotation(Column.class);

				if (column != null && (column.name().equals(aColumnName) || field.getName().equals(aColumnName)))
				{
					return column;
				}
			}

			throw new IllegalArgumentException("Column not found: " + aColumnName);
		}
		catch (SecurityException | IllegalArgumentException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static Object javaToResultSet(AbstractEntity aEntity, String aColumnName, Object aValue)
	{
		Column column = getColumn(aEntity, aColumnName);

		if (aValue != null)
		{
			if (aValue instanceof java.util.Date)
			{
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((java.util.Date)aValue);
			}
			if (aValue instanceof Calendar)
			{
				return aValue.toString();
			}
			if (aValue instanceof UUID)
			{
				return aValue.toString();
			}
			if (Enum.class.isAssignableFrom(aValue.getClass()))
			{
				if (column.enumType() == EnumType.NAME)
				{
					return ((Enum)aValue).name();
				}
				else
				{
					EnumValue annotation = getEnumAnnotation((Enum)aValue);
					if (annotation != null)
					{
						return annotation.value();
					}
					else
					{
						return ((Enum)aValue).ordinal() + 1;
					}
				}
			}
		}
		return aValue;
	}


	private static ArrayList<Field> getDeclaredFields(Class<?> aType)
	{
		return getDeclaredFields(aType, new ArrayList<>());
	}


	private static ArrayList<Field> getDeclaredFields(Class<?> aType, ArrayList<Field> aOutput)
	{
		aOutput.addAll(Arrays.asList(aType.getDeclaredFields()));

		Class<?> sup = aType.getSuperclass();

		if (sup != Object.class)
		{
			getDeclaredFields(sup, aOutput);
		}

		return aOutput;
	}
}

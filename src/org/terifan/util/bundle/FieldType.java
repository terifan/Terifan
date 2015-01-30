package org.terifan.util.bundle;

import java.util.Date;
import java.util.List;


enum FieldType
{
	TERMINATOR(null,null),
	BOOLEAN(Boolean.class, Boolean.TYPE),
	BYTE(Byte.class, Byte.TYPE),
	SHORT(Short.class, Short.TYPE),
	CHAR(Character.class, Character.TYPE),
	INT(Integer.class, Integer.TYPE),
	LONG(Long.class, Long.TYPE),
	FLOAT(Float.class, Float.TYPE),
	DOUBLE(Double.class, Double.TYPE),
	STRING(String.class, String.class),
	BUNDLE(Bundle.class, Bundle.class),
	DATE(Date.class, Date.class),
	UNDEFINED(null,null);

	private final Class mComponentType;
	private final Class mNumberType;


	private FieldType(Class aComponentType, Class aNumberType)
	{
		mComponentType = aComponentType;
		mNumberType = aNumberType;
	}


	public Class getNumberType()
	{
		return mNumberType;
	}


	public Class getComponentType()
	{
		return mComponentType;
	}


	static FieldType valueOf(Object aObject)
	{
		if (aObject == null)
		{
			return null;
		}
		Class cls = aObject.getClass();
		if (List.class.isAssignableFrom(cls))
		{
			cls = null;
			for (Object o : (List)aObject)
			{
				if (o != null)
				{
					cls = o.getClass();
					break;
				}
			}
			if (cls == null)
			{
				return UNDEFINED;
			}
		}
		if (cls.isArray())
		{
			cls = cls.getComponentType();
		}
		for (FieldType fieldType : values())
		{
			if (fieldType.mComponentType != null && (fieldType.mComponentType.isAssignableFrom(cls) || fieldType.mNumberType.isAssignableFrom(cls)))
			{
				return fieldType;
			}
		}
		throw new IllegalArgumentException("Unsupported type: " + cls);
	}


	public String getJavaName()
	{
		if (this == UNDEFINED)
		{
			return name();
		}
		String name = mComponentType.getSimpleName();
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
}

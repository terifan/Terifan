package deprecated.org.terifan.io.serialization;

import java.io.IOException;
import java.lang.reflect.Field;
import org.terifan.util.Strings;


class FieldProperty implements Property
{
	private Field mField;
	private Object mOwner;
	private String mName;


	public FieldProperty(Object aOwner, Field aField)
	{
		mField = aField;
		mOwner = aOwner;
		mField.setAccessible(true);

		ObjectSerializer.Serialize annotation = mField.getAnnotation(ObjectSerializer.Serialize.class);
		if (annotation != null)
		{
			mName = annotation.value();
		}
		if (Strings.isEmptyOrNull(mName))
		{
			mName = mField.getName();
		}
	}


	@Override
	public String getName()
	{
		return mName;
	}


	@Override
	public Object get() throws IOException
	{
		try
		{
			return mField.get(mOwner);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new IOException(e);
		}
	}


	@Override
	public boolean isPrimitive()
	{
		return mField.getType().isPrimitive();
	}


	@Override
	public Class getType()
	{
		return mField.getType();
	}
}

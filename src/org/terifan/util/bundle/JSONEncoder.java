package org.terifan.util.bundle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.List;


public class JSONEncoder
{
	private SimpleDateFormat mDateFormatter;


	public void marshal(Bundle aBundle, File aOutput) throws IOException
	{
		try (FileWriter fw = new FileWriter(aOutput))
		{
			marshal(aBundle, fw);
		}
	}


	/**
	 * Returns the provided Bundle as a JSON string.
	 *
	 * @return
	 *   the Bundle as a JSON string
	 */
	public String marshal(Bundle aBundle) throws IOException
	{
		return marshal(aBundle, new StringBuilder()).toString();
	}


	/**
	 * Returns the provided Bundle as a JSON string.
	 *
	 * @return
	 *   the Bundle as a JSON string
	 */
	public Appendable marshal(Bundle aBundle, Appendable aAppendable) throws IOException
	{
		writeBundle(aBundle, aAppendable);
		return aAppendable;
	}


	private void writeBundle(Bundle aBundle, Appendable aAppendable) throws IOException
	{
		aAppendable.append("{");

		boolean first = true;

		for (String key : aBundle.keySet())
		{
			if (key.contains("\""))
			{
				throw new IOException("Name contains illegal character: " + key);
			}

			Object value = aBundle.get(key);
			FieldType fieldType = FieldType.valueOf(value);

			if (!first)
			{
				aAppendable.append(", ");
			}
			first = false;

			aAppendable.append("\""+key+"\":");

			if (value == null)
			{
				aAppendable.append("null");
			}
			else
			{
				Class<? extends Object> cls = value.getClass();

				if (cls.isArray() || List.class.isAssignableFrom(cls))
				{
					if (List.class.isAssignableFrom(cls))
					{
						value = ((List)value).toArray();
					}
					aAppendable.append("[");
					for (int i = 0, len = Array.getLength(value); i < len; i++)
					{
						if (i > 0)
						{
							aAppendable.append(", ");
						}
						writeValue(Array.get(value, i), aAppendable, fieldType);
					}
					aAppendable.append("]");
				}
				else
				{
					writeValue(value, aAppendable, fieldType);
				}
			}
		}

		aAppendable.append("}");
	}


	private void writeValue(Object aValue, Appendable aAppendable, FieldType aFieldType) throws IOException
	{
		if (aValue == null)
		{
			aAppendable.append("null");
			return;
		}

		switch (aFieldType)
		{
			case BUNDLE:
				writeBundle((Bundle)aValue, aAppendable);
				break;
			case STRING:
				aAppendable.append("\"" + escapeString(aValue.toString()) + "\"");
				break;
			case DATE:
				if (mDateFormatter == null)
				{
					mDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				aAppendable.append("\"" + mDateFormatter.format(aValue) + "\"");
				break;
			case CHAR:
				aAppendable.append("\"" + (int)(Character)aValue + "\"");
				break;
			default:
				aAppendable.append(aValue.toString());
				break;
		}
	}


	private String escapeString(String s)
	{
		return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("/", "\\/").replace("\b", "\\\b").replace("\f", "\\\f").replace("\n", "\\\n").replace("\r", "\\\r").replace("\t", "\\\t");
	}
}

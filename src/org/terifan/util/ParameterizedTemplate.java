package org.terifan.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A ParameterizedTemplate is used to insert parameter values into a String, e.g "hello ${name}". The template is "compiled" making the
 * process of formatting very efficient.
 */
public class ParameterizedTemplate<V>
{
	protected final char START_SYMBOL = '$';
	protected final char LEFT_BRACE = '{';
	protected final char RIGHT_BRACE = '}';

	private Object[] mCommands;


	/**
	 * Constructs a new empty ParameterizedTemplate.
	 *
	 * e.g. new ParameterizedString().setTemplate("Name: ${name}\nSize: ${size}\n").format(map);
	 */
	public ParameterizedTemplate()
	{
	}


	/**
	 * Constructs a new ParameterizedTemplate with a template.
	 *
	 * e.g. new ParameterizedString("Name: ${name}\nSize: ${size}\n").format(map);
	 */
	public ParameterizedTemplate(String aTemplate)
	{
		this();
		setTemplate(aTemplate);
	}


	/**
	 * Sets the template to use.
	 */
	public ParameterizedTemplate setTemplate(String aTemplate)
	{
		ArrayList<Object> commands = new ArrayList<>();

		char[] chars = aTemplate.toCharArray();
		int start = 0;

		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == START_SYMBOL && i + 1 < chars.length && chars[i + 1] == LEFT_BRACE)
			{
				int j = -1;
				for (int k = i; k < chars.length; k++)
				{
					if (chars[k] == RIGHT_BRACE)
					{
						j = k;
						break;
					}
				}

				if (j == -1)
				{
					throw new IllegalArgumentException("Tag not terminated properly at offset " + i);
				}

				if (start < i)
				{
					commands.add(aTemplate.substring(start, i));
				}

				commands.add(new Key(new String(chars, i + 2, j - (i + 2))));
				i = j;
				start = i + 1;
			}
		}

		if (start < chars.length)
		{
			commands.add(aTemplate.substring(start, chars.length));
		}

		mCommands = commands.toArray();

		return this;
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public String format(Map<String,V> aParameters) throws IOException
	{
		return format(aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public String format(Map<String,V> aParameters, ValueFormatter<V> aValueFunction) throws IOException
	{
		StringWriter sw = new StringWriter();
		format(sw, aParameters, aValueFunction);
		return sw.toString();
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Map<String,V> aParameters) throws IOException
	{
		format(aAppendable, aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Map<String,V> aParameters, ValueFormatter<V> aValueFunction) throws IOException
	{
		for (Object o : mCommands)
		{
			if (o instanceof String)
			{
				aAppendable.append((String)o);
			}
			else
			{
				String key = ((Key)o).key;
				V value = aParameters.get(key);
				if (aValueFunction == null)
				{
					aAppendable.append(value.toString());
				}
				else
				{
					aAppendable.append(aValueFunction.value(key, value));
				}
			}
		}
	}


	protected static class Key
	{
		String key;

		public Key(String aKey)
		{
			key = aKey;
		}
	}


	@FunctionalInterface
	public interface ValueFormatter<V>
	{
		String value(String aKey, V aValue);
	}


	public static void main(String ... args)
	{
		try
		{
			Map<String,Object> map = new HashMap<>();
			map.put("name", "Stig");
			map.put("size", 7);

			System.out.println(new ParameterizedTemplate("${name}${size}").format(map));
			System.out.println(new ParameterizedTemplate(">${name}:${size}<").format(map));
			System.out.println(new ParameterizedTemplate("Name: ${name}\nSize: ${size}").format(map, (k,v)->v.toString().toUpperCase()));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;


/**
 * A ParameterizedTemplate is used to insert parameter values into a String, e.g "hello ${name}". The template is "compiled" making the
 * process of formatting very efficient when reused.
 *
 * e.g.:  <code>
 * Map<String,Object> map = new HashMap<>(); map.put("name", "jones"); map.put("size", 7);
 *
 * String result = new ParameterizedTemplate("Name: ${name}\nSize: ${size}\n").format(map::get);
 * </code>
 *
 * Using the simplified parameterised builder:
 *
 * e.g.:  <code>
 * String result = new ParameterizedTemplate("Name: ${name}\nSize: ${size}\n").put("name", "Olle").put("size", 8).format();
 * </code>
 */
public final class ParamsString<V>
{
	private final static char START_SYMBOL_1 = '$';
	private final static char START_SYMBOL_2 = '#';
	private final static char LEFT_BRACE = '{';
	private final static char RIGHT_BRACE = '}';

	private Object[] mCommands;
	private boolean mIgnoreMissingKeys;


	/**
	 * Constructs a new empty ParameterizedTemplate.
	 *
	 * e.g. new ParameterizedString().setTemplate("Name: ${name}\nSize: ${size}\n").format(map);
	 */
	public ParamsString()
	{
	}


	/**
	 * Constructs a new ParameterizedTemplate with a template.
	 *
	 * e.g. new ParameterizedString("Name: ${name}\nSize: ${size}\n").format(map);
	 */
	public ParamsString(String aTemplate)
	{
		this();
		setTemplate(aTemplate);
	}


	public ParamsString<V> setIgnoreMissingKeys(boolean aIgnoreMissingKeys)
	{
		mIgnoreMissingKeys = aIgnoreMissingKeys;
		return this;
	}


	public boolean isIgnoreMissingKeys()
	{
		return mIgnoreMissingKeys;
	}


	/**
	 * Sets the template to use.
	 */
	public ParamsString setTemplate(String aTemplate)
	{
		ArrayList<Object> commands = new ArrayList<>();

		char[] chars = aTemplate.toCharArray();
		int start = 0;

		for (int i = 0; i < chars.length; i++)
		{
			if ((chars[i] == START_SYMBOL_1 || chars[i] == START_SYMBOL_2) && i + 1 < chars.length && chars[i + 1] == LEFT_BRACE)
			{
				char command = chars[i];

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

				commands.add(new Command(command, new String(chars, i + 2, j - (i + 2))));
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
	public String format(Function<String, V> aParameters)
	{
		return format(aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public String format(Function<String, V> aParameters, Function<String, V> aValueFunction)
	{
		StringWriter sw = new StringWriter();
		format(sw, aParameters, aValueFunction);
		return sw.toString();
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Function<String, V> aParameters)
	{
		format(aAppendable, aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Function<String, V> aParameters1, Function<String, V> aParameters2)
	{
		for (Object o : mCommands)
		{
			try
			{
				if (o instanceof String)
				{
					aAppendable.append((String)o);
				}
				else
				{
					Command command = (Command)o;
					String key = command.key;
					V value = command.symbol == '$' ? aParameters1.apply(key) : aParameters2.apply(key);

					if (value != null)
					{
						aAppendable.append(value.toString());
					}
					else if (!mIgnoreMissingKeys)
					{
						throw new IllegalArgumentException("Key has no value: " + key);
					}
				}
			}
			catch (IOException e)
			{
				throw new IllegalStateException(e);
			}
		}
	}


	private static class Command
	{
		char symbol;
		String key;


		public Command(char aSymbol, String aKey)
		{
			symbol = aSymbol;
			key = aKey;
		}
	}


	/**
	 * Creates a builder to easily format a parameterised String.
	 * <p>
	 * <code>
	 * String result = ParameterizedTemplate.builder("${value}").put("value", 7).format();
	 * </code>
	 * </p>
	 */
	public static ParameterizedTemplateBuilder build(String aTemplate)
	{
		return new ParameterizedTemplateBuilder(new ParamsString(aTemplate));
	}


	public static class ParameterizedTemplateBuilder
	{
		private final ParamsString mTemplate;
		private final HashMap<String, Object> mValues;


		ParameterizedTemplateBuilder(ParamsString aTemplate)
		{
			mTemplate = aTemplate;
			mValues = new HashMap<>();
		}


		public ParameterizedTemplateBuilder setIgnoreMissingKeys(boolean aIgnoreMissingKeys)
		{
			mTemplate.setIgnoreMissingKeys(aIgnoreMissingKeys);
			return this;
		}


		public ParameterizedTemplateBuilder put(String aKey, Object aValue)
		{
			mValues.put(aKey, aValue);
			return this;
		}


		public String format()
		{
			return mTemplate.format(mValues::get);
		}
	}


	/**
	 * Process the parameterised String without building a template.
	 * <p>
	 * <code>
	 * HashMap<String,Object> map = new HashMap<>(); map.put("value", 7);
	 *
	 * String result = ParameterizedTemplate.format("${value}").format(map::get);
	 * </code>
	 * </p>
	 */
	public static String replace(String aTemplate, Function<String, Object> aParameters)
	{
		StringBuilder buffer = new StringBuilder(aTemplate);

		for (int i; (i = buffer.indexOf("${")) != -1;)
		{
			int j = buffer.indexOf("}", i);

			if (j == -1) // abort, something is wrong
			{
				break;
			}

			Object value = aParameters.apply(buffer.substring(i + 2, j));

			buffer.replace(i, j + 1, value == null ? "" : value.toString());
		}

		return buffer.toString();
	}


	public static void main(String... args)
	{
		try
		{
			HashMap<String, Object> map = new HashMap<>();
			map.put("name", "Stig");
			map.put("size", 7);

			System.out.println(new ParamsString("${name}${size}").format(map::get));
			System.out.println(new ParamsString("{${name}:${size}}").format(map::get));

			System.out.println("-----");

			System.out.println(ParamsString.build("${name}${size}").put("name", "Olle").put("size", 8).format());

			System.out.println("-----");

			System.out.println(ParamsString.replace("${name}${size}", map::get));
			System.out.println(ParamsString.replace("{${name}:${size}}", map::get));
			System.out.println(ParamsString.replace("xxxxxx ${name yyyyy", map::get));

			System.out.println(new ParamsString<>("${name}${size}").format(f ->
			{
				switch (f)
				{
					case "name": return "Stig";
					case "size": return 7;
				}
				throw new IllegalArgumentException();
			}));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

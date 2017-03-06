package org.terifan.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.function.Function;


/**
 * A ParameterizedTemplate is used to insert parameter values into a String, e.g "hello ${name}". The template is "compiled" making the
 * process of formatting very efficient when reused.
 */
public class ParameterizedTemplate<V>
{
	private final static char START_SYMBOL_1 = '$';
	private final static char START_SYMBOL_2 = '#';
	private final static char LEFT_BRACE = '{';
	private final static char RIGHT_BRACE = '}';

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
	public String format(Function<String,V> aParameters) throws IOException
	{
		return format(aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public String format(Function<String,V> aParameters, Function<String,V> aValueFunction) throws IOException
	{
		StringWriter sw = new StringWriter();
		format(sw, aParameters, aValueFunction);
		return sw.toString();
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Function<String,V> aParameters) throws IOException
	{
		format(aAppendable, aParameters, null);
	}


	/**
	 * Formats the template using the parameters provided.
	 */
	public void format(Appendable aAppendable, Function<String,V> aParameters1, Function<String,V> aParameters2) throws IOException
	{
		for (Object o : mCommands)
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
				else
				{
					aAppendable.append(command.symbol + "" + LEFT_BRACE + key + RIGHT_BRACE);
				}
			}
		}
	}


	protected static class Command
	{
		char symbol;
		String key;

		public Command(char aSymbol, String aKey)
		{
			symbol = aSymbol;
			key = aKey;
		}
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			Map<String,Object> map = new HashMap<>();
//			map.put("name", "Stig");
//			map.put("size", 7);
//
//			System.out.println(new ParameterizedTemplate("${name}${size}").format(map::get));
//			System.out.println(new ParameterizedTemplate(">${name}:${size}<").format(map::get));
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

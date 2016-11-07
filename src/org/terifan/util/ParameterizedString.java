package org.terifan.util;

import java.util.HashMap;
import org.terifan.util.log.Log;


/**
 * Constructs a formatted string from a template and parameters.
 *
 * new FormattedString("hello ${name}").put("name", "Patrik").toString();
 */
public class ParameterizedString
{
	private final static String TAG = ParameterizedString.class.getName();

	private final static char START_SYMBOL = '$';
	private final static char LEFT_BRACE = '{';
	private final static char RIGHT_BRACE = '}';

	private String mFormat;
	private HashMap<String, Object> mParameters;


	public ParameterizedString()
	{
		mParameters = new HashMap<>();
	}


	public ParameterizedString(String aFormat)
	{
		this();

		setFormat(aFormat);
	}


	public ParameterizedString setFormat(String aFormat)
	{
		mFormat = aFormat;
		return this;
	}


	public ParameterizedString put(String aName, Object aValue)
	{
		mParameters.put(aName, aValue);
		return this;
	}


	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		char[] chars = mFormat.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

			if (c == START_SYMBOL && i + 1 < chars.length && chars[i + 1] == LEFT_BRACE)
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

				if (j != -1)
				{
					String key = new String(chars, i + 2, j - (i + 2));
					String value = Strings.replaceNull(mParameters.get(key), key);
					result.append(value);
					i = j;

					continue;
				}
			}

			result.append(c);
		}

		return result.toString();
	}


	public static void main(String ... args)
	{
		try
		{
			Log.out.println(new ParameterizedString("hello ${name} $").put("name", "Patrik").put("family", "Olsson").toString());
			Log.out.println(new ParameterizedString("hello ${name} ${").put("name", "Patrik").put("family", "Olsson").toString());
			Log.out.println(new ParameterizedString("hello ${name} ${}").put("name", "Patrik").put("family", "Olsson").toString());
			Log.out.println(new ParameterizedString("hello ${name} ${x}").put("name", "Patrik").put("family", "Olsson").toString());
			Log.out.println(new ParameterizedString("hello ${name} ${family} ${name}!").put("name", "Patrik").put("family", "Olsson").toString());
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

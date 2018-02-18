package org.terifan.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


public class Strings
{
	/**
	 * Matches String that are not null, not empty and match the regular expression.
	 */
	public static boolean matches(String aString, String aRegex)
	{
		return aString != null && !aString.isEmpty() && aString.matches(aRegex);
	}


	/**
	 * Matches String that are either null, empty or match the regular expression.
	 */
	public static boolean emptyOrMatches(String aString, String aRegex)
	{
		return aString == null || aString.equals("") || aString.matches(aRegex);
	}


	public static String nullToEmpty(Object aString)
	{
		return aString == null ? "" : aString.toString();
	}


	public static String toString(byte[] aString)
	{
		return aString == null ? "" : new String(aString);
	}


	public static String nullToEmpty(char[] aString)
	{
		return aString == null ? "" : new String(aString);
	}


	public static boolean isNumeric(String aString)
	{
		for (int i = 0, sz = aString.length(); i < sz; i++)
		{
			if (!Character.isDigit(aString.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if the String provided is null or empty.
	 *
	 * @param aString
	 *   a String to test
	 * @return
	 *   true if null or empty
	 */
	public static boolean isEmptyOrNull(String aString)
	{
		return aString == null || aString.isEmpty();
	}


	/**
	 * Check if the String provided is not null or empty.
	 *
	 * @param aString
	 *   a String to test
	 * @return
	 *   true if not null or empty
	 */
	public static boolean isNotEmptyOrNull(String aString)
	{
		return aString != null && !aString.isEmpty();
	}


	/**
	 * Compares two words for similarity and return true if the words have less
	 * differences than the maximum threshold specified.
	 *
	 * @param aTemplate
	 *   the word to compare against
	 * @param aCompareWith
	 *   the word to compare with
	 * @param aCaseSensitive
	 *   true if the comparison should be case senastive
	 * @param aTrimCompare
	 *   differences beyond the end of the template will not be counted
	 * @return
	 *   true if the words match
	 */
	public static int compareWords(String aTemplate, String aCompareWith, boolean aCaseSensitive, boolean aTrimCompare)
	{
		if (!aCaseSensitive)
		{
			aTemplate = aTemplate.toLowerCase();
			aCompareWith = aCompareWith.toLowerCase();
		}

		return compareWords(aTemplate, aCompareWith, 0, 0, Integer.MAX_VALUE, 0, aTrimCompare);
	}


	/**
	 * Compares two words for similarity and return number of differences.
	 *
	 * @param aTemplate
	 *   the word to compare against
	 * @param aCompareWith
	 *   the word to compare with
	 * @param aMaxErrors
	 *   number of mismatching characters permitted before the test is aborted
	 * @param aCaseSensitive
	 *   true if the comparison should be case sensitive
	 * @param aTrimCompare
	 *   differences beyond the end of the template will not be counted
	 * @return
	 *   number of different characters
	 */
	public static boolean compareWords(String aTemplate, String aCompareWith, int aMaxErrors, boolean aCaseSensitive, boolean aTrimCompare)
	{
		if (!aCaseSensitive)
		{
			aTemplate = aTemplate.toLowerCase();
			aCompareWith = aCompareWith.toLowerCase();
		}

		int err = compareWords(aTemplate, aCompareWith, 0, 0, aMaxErrors, 0, aTrimCompare);

		return err <= aMaxErrors;
	}


	private static int compareWords(String aTemplate, String aCompare, int aTemplateOffset, int aCompareOffset, int aMaxErrors, int aAccumulatedErrors, boolean aTrimCompare)
	{
		for (; aTemplateOffset < aTemplate.length() && aCompareOffset < aCompare.length(); aTemplateOffset++, aCompareOffset++)
		{
			if (aTemplate.charAt(aTemplateOffset) != aCompare.charAt(aCompareOffset))
			{
				aAccumulatedErrors++;

				if (aAccumulatedErrors > aMaxErrors)
				{
					return aAccumulatedErrors;
				}

				int e1 = compareWords(aTemplate, aCompare, aTemplateOffset + 1, aCompareOffset, aMaxErrors, aAccumulatedErrors, aTrimCompare);
				int e2 = compareWords(aTemplate, aCompare, aTemplateOffset, aCompareOffset + 1, aMaxErrors, aAccumulatedErrors, aTrimCompare);
				int e3 = compareWords(aTemplate, aCompare, aTemplateOffset + 1, aCompareOffset + 1, aMaxErrors, aAccumulatedErrors, aTrimCompare);

				return Math.min(e1, Math.min(e2, e3));
			}
		}

		if (aTrimCompare && aTemplateOffset == aTemplate.length())
		{
			return aAccumulatedErrors;
		}

		return aAccumulatedErrors + Math.abs((aTemplate.length() - aTemplateOffset) - (aCompare.length() - aCompareOffset));
	}


	public static Integer toInteger(String aString, Integer aDefaultValue)
	{
		if (isEmptyOrNull(aString))
		{
			return aDefaultValue;
		}
		try
		{
			return Integer.valueOf(aString);
		}
		catch (Throwable e)
		{
			return aDefaultValue;
		}
	}


	public static Long toLong(String aString, Long aDefaultValue)
	{
		if (isEmptyOrNull(aString))
		{
			return aDefaultValue;
		}
		try
		{
			return Long.valueOf(aString);
		}
		catch (Throwable e)
		{
			return aDefaultValue;
		}
	}


	public static Double toDouble(String aString, Double aDefaultValue)
	{
		if (isEmptyOrNull(aString))
		{
			return aDefaultValue;
		}
		try
		{
			return Double.valueOf(aString);
		}
		catch (Throwable e)
		{
			return aDefaultValue;
		}
	}


	public static String repeat(char aCharacter, int aLength)
	{
		char [] buf = new char[aLength];
		Arrays.fill(buf, aCharacter);

		return new String(buf);
	}


	public static String repeat(String aWord, int aLength)
	{
		char [] buf = new char[aLength];
		char [] src = aWord.toCharArray();
		for (int i = 0; i < aLength; )
		{
			for (int j = 0; i < aLength && j < src.length; j++, i++)
			{
				buf[i] = src[j];
			}
		}

		return new String(buf);
	}


	/**
	 * Converts the provided Object to a String. If the value is null then value
	 * returned is null.
	 *
	 * @param aValue
	 *   a value to convert.
	 * @return
	 *   a String (which can be null).
	 */
	public static String asString(Object aValue)
	{
		return aValue == null ? null : aValue.toString();
	}


	/**
	 * Return a comma separated list of all items in the list.
	 * @param aList
	 *   a list of items
	 * @return
	 *   a String of all items
	 */
	public static String listToString(Collection aList)
	{
		StringBuilder sb = new StringBuilder();
		for (Object item : aList)
		{
			if (sb.length() > 0)
			{
				sb.append(',');
			}
			sb.append(item);
		}
		return sb.toString();
	}


	public static String listToString(Object... aList)
	{
		StringBuilder sb = new StringBuilder();
		for (Object item : aList)
		{
			if (sb.length() > 0)
			{
				sb.append(',');
			}
			sb.append(item);
		}
		return sb.toString();
	}


	public static String emptyToNull(String aString)
	{
		return aString == null || aString.isEmpty() ? null : aString;
	}


	/**
	 * Join two strings inserting the separator between ensuring the separator only exists once.
	 *
	 * E.g: join("c:/files/", "/", "/my_file.txt") returns "c:/files/my_file.txt".
	 *
	 * @return
	 *   strings join with the separator in-between.
	 */
	public static String concat(String aHead, String aSeparator, String aTail)
	{
		if (!aSeparator.isEmpty())
		{
			while (aHead.endsWith(aSeparator))
			{
				aHead = aHead.substring(0, aHead.length()-1);
			}
			while (aTail.startsWith(aSeparator))
			{
				aTail = aTail.substring(1);
			}
		}
		return aHead + (!aHead.isEmpty() && !aTail.isEmpty() ? aSeparator : "") + aTail;
	}


	/**
	 * Joins the string excluding any empty or null parts.
	 *
	 * @param aSeparator
	 *   separator between parts
	 * @param aStrings
	 *   parts to join
	 * @return
	 *   the joined strings
	 */
	public static String join(String aSeparator, String... aStrings)
	{
		return join(aSeparator, false, aStrings);
	}


	/**
	 * Joins the string excluding any null parts.
	 *
	 * @param aSeparator
	 *   separator between parts
	 * @param aIncludeEmptyParts
	 *   true if empty parts should be included
	 * @param aStrings
	 *   parts to join
	 * @return
	 *   the joined strings
	 */
	public static String join(String aSeparator, boolean aIncludeEmptyParts, String... aStrings)
	{
		if (aStrings == null)
		{
			return null;
		}
		if (aStrings.length == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : aStrings)
		{
			if (s != null && (aIncludeEmptyParts || !s.isEmpty()))
			{
				if (sb.length() > 0)
				{
					sb.append(aSeparator);
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}


	public static String replaceNull(Object aString, String aReplacedWith)
	{
		return aString == null ? aReplacedWith : aString.toString();
	}


	public static String replaceEmptyOrNull(String aString, String aReplacedWith)
	{
		return isEmptyOrNull(aString) ? aReplacedWith : aString;
	}


	public static String toTimeString(long aMillis)
	{
		return String.format("%d:%02d:%02d.%03d", aMillis/60/60/1000, (aMillis/60/1000)%60, (aMillis/1000)%60, aMillis%1000);
	}


	public static String replaceParams(String aText, Map<String,Object> aParams)
	{
		return replaceParams(aText, e->"" + aParams.get(e));
	}


	public static String replaceParams(String aText, StringLookup aParamProvider)
	{
		return replaceParams("${", "}", aText, aParamProvider);
	}


	public static String replaceParams(String aKeywordPrefix, String aKeywordSuffix, String aText, StringLookup aParamProvider)
	{
		StringBuilder text = new StringBuilder(aText.length());
		int prefixLength = aKeywordPrefix.length();
		int suffixLength = aKeywordSuffix.length();

		for (int i = 0, sz = aText.length(); i < sz; i++)
		{
			if (aText.startsWith(aKeywordPrefix, i))
			{
				int j = aText.indexOf(aKeywordSuffix, i + prefixLength);

				if (j != -1)
				{
					String name = aText.substring(i + prefixLength, j);
					Object header = aParamProvider.get(name);
					if (header != null)
					{
						text.append(header);
						i = j + suffixLength - 1;
						continue;
					}
				}
			}

			text.append(aText.charAt(i));
		}

		return text.toString();
	}


	public static String removeStart(String aString, String aPrefix)
	{
		while (aString.startsWith(aPrefix))
		{
			aString = aString.substring(aPrefix.length());
		}

		return aString;
	}


	public static String removeEnd(String aString, String aSuffix)
	{
		while (aString.endsWith(aSuffix))
		{
			aString = aString.substring(0, aString.length() - aSuffix.length());
		}

		return aString;
	}


	public static int indexOf(String aString, String... aTokens)
	{
		int i = -1;

		for (String s : aTokens)
		{
			int j = aString.indexOf(s);

			if (j != -1 && (j < i || i == -1))
			{
				i = j;
			}
		}

		return i;
	}


	@FunctionalInterface
	public interface StringLookup
	{
		String get(String aName);
	}


	/**
	 * Removes all characters not being either letter, digits or underscore from the string.
	 */
	public static String sanitizeString(String aString)
	{
		StringBuilder sb = new StringBuilder(aString.length());

		for (int i = 0; i < aString.length(); i++)
		{
			char c = aString.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '_')
			{
				sb.append(c);
			}
		}

		return sb.toString();
	}
}
package org.terifan.util;

import java.util.Arrays;


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


	public static String emptyToNull(String aString)
	{
		return aString == null || aString.isEmpty() ? null : aString;
	}
}
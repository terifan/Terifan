package org.terifan.util;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


public class Strings
{
	public static String toString(byte[] aString)
	{
		return aString == null ? "" : new String(aString);
	}


	public static String toString(Object aObject)
	{
		return aObject == null ? "" : aObject.toString();
	}


	public static String nullToEmpty(Object aObject)
	{
		return aObject == null ? "" : aObject.toString();
	}


	public static String nullToEmpty(char[] aString)
	{
		return aString == null ? "" : new String(aString);
	}


	/**
	 * Check if the String provided is null or empty.
	 *
	 * @param aString a String to test
	 * @return true if null or empty
	 */
	public static boolean isEmptyOrNull(Object aString)
	{
		return aString == null || aString.toString().isEmpty();
	}


	/**
	 * Check if the String provided is not null or empty.
	 *
	 * @param aString a String to test
	 * @return true if not null or empty
	 */
	public static boolean isNotEmptyOrNull(Object aString)
	{
		return aString != null && !aString.toString().isEmpty();
	}


	/**
	 * Compares two words for similarity and return true if the words have less differences than the maximum threshold specified.
	 *
	 * @param aTemplate the word to compare against
	 * @param aCompareWith the word to compare with
	 * @param aCaseSensitive true if the comparison should be case senastive
	 * @param aTrimCompare differences beyond the end of the template will not be counted
	 * @return true if the words match
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
	 * @param aTemplate the word to compare against
	 * @param aCompareWith the word to compare with
	 * @param aMaxErrors number of mismatching characters permitted before the test is aborted
	 * @param aCaseSensitive true if the comparison should be case sensitive
	 * @param aTrimCompare differences beyond the end of the template will not be counted
	 * @return number of different characters
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


	public static String repeat(char aCharacter, int aLength)
	{
		char[] buf = new char[aLength];
		Arrays.fill(buf, aCharacter);

		return new String(buf);
	}


	public static String repeat(String aWord, int aLength)
	{
		char[] buf = new char[aLength];
		char[] src = aWord.toCharArray();
		for (int i = 0; i < aLength;)
		{
			for (int j = 0; i < aLength && j < src.length; j++, i++)
			{
				buf[i] = src[j];
			}
		}

		return new String(buf);
	}


	/**
	 * Return a comma separated list of all items in the list.
	 *
	 * @param aList a list of items
	 * @return a String of all items
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


	public static String arrayToString(Object aArray)
	{
		if (aArray != null && aArray.getClass().isArray())
		{
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < Array.getLength(aArray); i++)
			{
				if (i > 0)
				{
					sb.append(',');
				}
				sb.append(Array.get(aArray, i));
			}
			return sb.append("]").toString();
		}

		return toString(aArray);
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
	 * @return strings join with the separator in-between.
	 */
	public static String concat(String aHead, String aSeparator, String aTail)
	{
		if (!aSeparator.isEmpty())
		{
			while (aHead.endsWith(aSeparator))
			{
				aHead = aHead.substring(0, aHead.length() - 1);
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
	 * @param aSeparator separator between parts
	 * @param aStrings parts to join
	 * @return the joined strings
	 */
	public static String join(String aSeparator, Object... aStrings)
	{
		return joinIterable(aSeparator, false, Arrays.asList(aStrings));
	}


	public static String join(String aSeparator, boolean aIncludeEmptyParts, String... aStrings)
	{
		return join(aSeparator, aIncludeEmptyParts, Arrays.asList(aStrings));
	}


	/**
	 * Joins the string excluding any null parts.
	 *
	 * @param aSeparator separator between parts
	 * @param aIncludeEmptyParts true if empty parts should be included
	 * @param aStrings parts to join
	 * @return the joined strings
	 */
	public static String joinIterable(String aSeparator, boolean aIncludeEmptyParts, Iterable<? extends Object> aStrings)
	{
		if (aStrings == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Object s : aStrings)
		{
			if (s != null && (aIncludeEmptyParts || !s.toString().isEmpty()))
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


	public static String join(String aSeparator, int aFirstIndex, int aLastIndex, FunctionEx<Integer, Object> aProducer)
	{
		try
		{
			StringBuilder err = new StringBuilder();

			for (int i = aFirstIndex; i <= aLastIndex; i++)
			{
				if (err.length() > 0)
				{
					err.append(aSeparator);
				}
				err.append(aProducer.apply(i));
			}

			return err.toString();
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static String replaceNull(Object aString, String aReplacedWith)
	{
		return aString == null ? aReplacedWith : aString.toString();
	}


	public static String replaceEmptyOrNull(Object aString, String aReplacedWith)
	{
		return isEmptyOrNull(aString) ? aReplacedWith : aString.toString();
	}


	public static String replaceParams(String aText, Map<String, Object> aParams)
	{
		return replaceParams(aText, e -> "" + aParams.get(e));
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


	public static boolean equalsIgnoreCase(Object aA, Object aB)
	{
		return aA != null && aB != null && aA.toString().equalsIgnoreCase(aB.toString());
	}


	@FunctionalInterface
	public interface StringLookup
	{
		String get(String aName);
	}


	@FunctionalInterface
	public interface FunctionEx<T, R>
	{
		R apply(T aParam) throws Exception;
	}


	/**
	 * Replaces non-ASCII characters in the string provided with normalized versions if possible.
	 */
	public static String normalizeString(String aString)
	{
		try
		{
			if (aString == null || aString.isEmpty())
			{
				return aString;
			}

			char[] chars = aString.toCharArray();

			for (int i = 0; i < chars.length; i++)
			{
				if (chars[i] > 255)
				{
					chars[i] = Normalizer.normalize(Character.toString(chars[i]), Normalizer.Form.NFD).charAt(0);
				}

				if (chars[i] == (char)322)
				{
					chars[i] = 'l';
				}
			}

			// change direction of apostrophe eg. è to é
			return Normalizer.normalize(Normalizer.normalize(new String(chars), Normalizer.Form.NFD).replace((char)768, (char)769), Normalizer.Form.NFC);
		}
		catch (Throwable e)
		{
			return aString;
		}
	}
}

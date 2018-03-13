package org.terifan.util;


public class Assert
{
	/**
	 * Throws exception if value is true.
	 */
	public static void fail(boolean aValue)
	{
		fail(aValue, "Test failed");
	}


	/**
	 * Throws exception if value is true.
	 */
	public static void fail(boolean aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue)
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value is false.
	 */
	public static void assertTrue(boolean aValue)
	{
		assertTrue(aValue, "Value not verified");
	}


	/**
	 * Throws exception if value is false.
	 */
	public static void assertTrue(boolean aValue, String aErrorMessage, Object... aArguments)
	{
		if (!aValue)
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value is false.
	 */
	public static void assertEquals(Object aValue0, Object aValue1)
	{
		assertEquals(aValue0, aValue1, "Values aren't equal");
	}


	/**
	 * Throws exception if value is false.
	 */
	public static void assertEquals(Object aValue0, Object aValue1, String aErrorMessage, Object... aArguments)
	{
		if ((aValue0 == null && aValue1 != null) || aValue0 != null && !aValue0.equals(aValue1))
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value is null.
	 */
	public static void assertNotNull(Object aValue)
	{
		assertNotNull(aValue, "Value is null");
	}


	/**
	 * Throws exception if value is null.
	 */
	public static void assertNotNull(Object aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null)
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value is empty or null.
	 */
	public static void assertNotEmptyOrNull(Object aValue)
	{
		assertNotEmptyOrNull(aValue, "Value is empty or null");
	}


	/**
	 * Throws exception if value is empty or null.
	 */
	public static void assertNotEmptyOrNull(Object aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null || aValue.toString().isEmpty())
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatchesOrNull(Object aValue, String aPattern)
	{
		assertMatchesOrNull(aValue, aPattern, "Value don't match pattern");
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatchesOrNull(Object aValue, String aPattern, String aErrorMessage, Object... aArguments)
	{
		if (aValue != null && !aValue.toString().matches(aPattern))
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatches(Object aValue, String aPattern)
	{
		assertMatches(aValue, aPattern, "Value don't match pattern");
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatches(Object aValue, String aPattern, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null || !aValue.toString().matches(aPattern))
		{
			throw new IllegalArgumentException(String.format(aErrorMessage, aArguments));
		}
	}
}
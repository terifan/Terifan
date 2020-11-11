package org.terifan.util;


public class Assert
{
	/**
	 * Throws exception if value is true.
	 */
	public static void fail(boolean aValue)
	{
		fail(aValue, "%s", "Test failed");
	}


	/**
	 * Throws exception if value is true.
	 */
	public static void fail(boolean aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value isn't true.
	 */
	public static void assertTrue(boolean aValue)
	{
		assertTrue(aValue, "%s", "Value not true");
	}


	/**
	 * Throws exception if value isn't true.
	 */
	public static void assertTrue(boolean aValue, String aErrorMessage, Object... aArguments)
	{
		if (!aValue)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value isn't false.
	 */
	public static void assertFalse(boolean aValue)
	{
		assertFalse(aValue, "%s", "Value not false");
	}


	/**
	 * Throws exception if value isn't false.
	 */
	public static void assertFalse(boolean aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if values aren't equal.
	 */
	public static void assertEquals(Object aActual, Object aExpected)
	{
		assertEquals(aActual, aExpected, "Values not equal: actual: %s, expected: %s", aActual, aExpected);
	}


	/**
	 * Throws exception if values aren't equal.
	 */
	public static void assertEquals(Object aActual, Object aExpected, String aErrorMessage, Object... aArguments)
	{
		if ((aActual == null && aExpected != null) || aActual != null && !aActual.equals(aExpected))
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value is null.
	 */
	public static void assertNotNull(Object aValue)
	{
		assertNotNull(aValue, "%s", "Value is null");
	}


	/**
	 * Throws exception if value is null.
	 */
	public static void assertNotNull(Object aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value isn't null.
	 */
	public static void assertNull(Object aValue)
	{
		assertNull(aValue, "%s", "Value is null");
	}


	/**
	 * Throws exception if value isn't null.
	 */
	public static void assertNull(Object aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue != null)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value is empty or null.
	 */
	public static void assertNotEmptyOrNull(Object aValue)
	{
		assertNotEmptyOrNull(aValue, "%s", "Value is empty or null");
	}


	/**
	 * Throws exception if value is empty or null.
	 */
	public static void assertNotEmptyOrNull(Object aValue, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null || aValue.toString().isEmpty())
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatchesOrNull(Object aValue, String aPattern)
	{
		assertMatchesOrNull(aValue, aPattern, "%s", "Value don't match pattern");
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatchesOrNull(Object aValue, String aPattern, String aErrorMessage, Object... aArguments)
	{
		if (aValue != null && !aValue.toString().matches(aPattern))
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatches(Object aValue, String aPattern)
	{
		assertMatches(aValue, aPattern, "%s", "Value don't match pattern");
	}


	/**
	 * Throws exception if value don't match regular expression.
	 */
	public static void assertMatches(Object aValue, String aPattern, String aErrorMessage, Object... aArguments)
	{
		if (aValue == null || !aValue.toString().matches(aPattern))
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if the values are the same.
	 */
	public static void assertSame(Object aExpected, Object aActual)
	{
		assertSame(aExpected, aActual, "%s", "Provided objects are not the same.");
	}


	/**
	 * Throws exception if the values are the same.
	 */
	public static void assertSame(Object aExpected, Object aActual, String aErrorMessage, Object... aArguments)
	{
		if (aExpected != aActual)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}


	/**
	 * Throws exception if the values aren't the same.
	 */
	public static void assertNotSame(Object aExpected, Object aActual)
	{
		assertNotSame(aExpected, aActual, "%s", "Provided objects are the same.");
	}


	/**
	 * Throws exception if the values aren't the same.
	 */
	public static void assertNotSame(Object aExpected, Object aActual, String aErrorMessage, Object... aArguments)
	{
		if (aExpected == aActual)
		{
			throw new AssertionException(aErrorMessage, aArguments);
		}
	}
}

package org.terifan.util;

import java.util.Arrays;


public class Assert
{
	public static void assertNotNull(Object aValue)
	{
		assertNotNull(aValue, "Provided value is null.");
	}


	public static void assertNotNull(Object aValue, String aMessage)
	{
		if (aValue == null)
		{
			throw new IllegalArgumentException(aMessage);
		}
	}


	public static void assertNotNullOrEmpty(String aValue)
	{
		assertNotNullOrEmpty(aValue, "Provided value is null or empty.");
	}


	public static void assertNotNullOrEmpty(String aValue, String aMessage)
	{
		if (aValue == null || aValue.isEmpty())
		{
			throw new IllegalArgumentException(aMessage);
		}
	}


	public static void assertTrue(boolean aValue)
	{
		assertTrue(aValue, "Prrovided value is false.");
	}


	public static void assertTrue(boolean aValue, String aMessage)
	{
		if (!aValue)
		{
			throw new IllegalArgumentException(aMessage);
		}
	}


	public static void assertFalse(boolean aValue)
	{
		assertFalse(aValue, "Provided value is true.");
	}


	public static void assertFalse(boolean aValue, String aMessage)
	{
		if (aValue)
		{
			throw new IllegalArgumentException(aMessage);
		}
	}


	public static void assertEquals(Object aExpected, Object aActual)
	{
		assertEquals(aExpected, aActual, "Provided objects are not equal.");
	}


	public static void assertEquals(Object aExpected, Object aActual, String aMessage)
	{
		if ((aExpected == null ^ aActual == null) || aExpected != null && !aExpected.equals(aActual))
		{
			throw new IllegalArgumentException(aMessage);
		}
	}


	public static void assertEquals(byte[] aExpected, byte[] aActual)
	{
		assertEquals(aExpected, aActual, "Provided arrays don't match.");
	}


	public static void assertEquals(byte[] aExpected, byte[] aActual, String aMessage)
	{
		if (!Arrays.equals(aExpected, aActual))
		{
			throw new IllegalArgumentException(aMessage);
		}
	}
}

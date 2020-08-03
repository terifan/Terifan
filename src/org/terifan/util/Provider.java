package org.terifan.util;


@FunctionalInterface
public interface Provider<T,R>
{
	R apply(T aValue) throws Exception;

	default R applySafe(T aValue)
	{
		try
		{
			return apply(aValue);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}
}

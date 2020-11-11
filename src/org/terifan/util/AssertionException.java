package org.terifan.util;


public class AssertionException extends IllegalArgumentException
{
	private final static long serialVersionUID = 1L;


	public AssertionException(String aErrorMessage, Object[] aArguments)
	{
		super(String.format(aErrorMessage, aArguments));
	}
}

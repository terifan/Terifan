package org.terifan.security;


public class InvalidKeyException extends RuntimeException
{
	public InvalidKeyException()
	{
	}

	public InvalidKeyException(String aMessage)
	{
		super(aMessage);
	}
}
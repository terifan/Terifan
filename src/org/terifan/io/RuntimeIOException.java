package org.terifan.io;

import java.io.IOException;


public class RuntimeIOException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public RuntimeIOException(IOException aCause)
	{
		super(aCause);
	}
}

package org.terifan.security;

import java.util.Arrays;


public final class SecretKey
{
	private transient byte[] mKeyBytes;


	public SecretKey(byte[] aKeyBytes)
	{
		this(aKeyBytes, 0, aKeyBytes.length);
	}


	public SecretKey(byte[] aKeyBytes, int aOffset, int aLength)
	{
		mKeyBytes = Arrays.copyOfRange(aKeyBytes, aOffset, aOffset + aLength);
	}


	byte[] bytes()
	{
		return mKeyBytes;
	}


	/**
	 * Copies the key bytes to the output buffer provided.
	 *
	 * @return
	 *   the output buffer
	 */
	public byte[] bytes(byte[] aOutputBuffer)
	{
		System.arraycopy(mKeyBytes, 0, aOutputBuffer, 0, mKeyBytes.length);
		return aOutputBuffer;
	}


	public void reset()
	{
		Arrays.fill(mKeyBytes, (byte)0);
	}
}
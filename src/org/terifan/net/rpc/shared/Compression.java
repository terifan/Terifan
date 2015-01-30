package org.terifan.net.rpc.shared;

import java.util.zip.Deflater;


/**
 * Controls the compression method used in a RPCConnection.
 */
public enum Compression
{
	/**
	 * Compression will be disabled.
	 */
	NO_COMPRESSION(Deflater.NO_COMPRESSION),
	/**
	 * Most efficient compression will be used.
	 */
	BEST_COMPRESSION(Deflater.BEST_COMPRESSION),
	/**
	 * Fastest compression will be used.
	 */
	BEST_SPEED(Deflater.BEST_SPEED);

	
	private final int mAlgorithm;


	Compression(int aAlgorithm)
	{
		mAlgorithm = aAlgorithm;
	}


	public int getAlgorithm()
	{
		return mAlgorithm;
	}
}

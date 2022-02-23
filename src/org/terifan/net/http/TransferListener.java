package org.terifan.net.http;

public interface TransferListener
{
	/**
	 * @param aByteCount
	 *   number of bytes to send or -1 if unknown
	 */
	void prepareSending(long aByteCount);

	void sending(long aByteCount);

	/**
	 * @param aByteCount
	 *   number of bytes to receive or -1 if unknown
	 */
	void prepareReceiving(long aByteCount);

	void receiving(long aByteCount);
}

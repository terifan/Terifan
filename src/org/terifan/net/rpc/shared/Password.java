package org.terifan.net.rpc.shared;

import org.terifan.security.HMAC;
import org.terifan.security.PBKDF2;
import org.terifan.security.SHA256;
import org.terifan.util.Convert;


/**
 * Helper class used to expand an user's clear-text password into an expanded
 * password used by the RPC Authenticator to verify user access.
 */
public final class Password
{
	private Password()
	{
	}


	/**
	 * Produces a password using PBKDF2 function from the PKCS#5 v2.0
	 * Password-Based Cryptography Standard. The password is expanded using
	 * 16384 iterations of SHA-256. The password is treated as an array of 16
	 * bit characters.
	 *
	 * @param aSalt
	 *   a 16 byte value
	 * @param aPassword
	 *   a user's clear-text password
	 * @return
	 *   a 16 byte expanded password
	 */
	public static byte [] expandPassword(byte [] aSalt, String aPassword)
	{
		if (aSalt == null || aSalt.length != 16)
		{
			throw new IllegalArgumentException("The salt value must be 16 bytes long.");
		}
		if (aPassword == null || aPassword.isEmpty())
		{
			throw new IllegalArgumentException("Password is empty.");
		}

		byte [] tmp = Convert.toBytes(aPassword.toCharArray());

		return PBKDF2.generateKeyBytes(new HMAC(new SHA256(), tmp), aSalt, 16384, 16);
	}
}

package org.terifan.net.rpc.server;

import java.io.IOException;
import java.lang.reflect.Method;


/**
 * An Authenticator is used by the RPC server to authenticate client requests.
 *
 * Passwords must be expanded using the helper class Password. The result of
 * the expandPassword can be stored together with the salt value in a database.
 * The getUserSalt and getUserPassword should return the salt and expanded
 * password.
 *
 * <pre>
 * String password = the actual password assigned to a user
 * byte [] salt = new byte[16];
 * new SecureRandom().nextBytes(salt);
 * byte [] expandedPassword = Password.expandPassword(salt, password);
 * </pre>
 */
public interface Authenticator
{
	/**
	 * Return the salt value that protects the user password from attacks.
	 *
	 * @param aSession
	 *   the Session requesting the salt value. The name of the user can be found in the session.
	 * @return
	 *   a 16 bytes long array containing the salt value
	 */
	public byte [] getUserSalt(Session aSession) throws IOException;

	/**
	 * Return the expanded 16 byte password assigned to a user.
	 *
	 * @param aSession
	 *   the Session requesting the password. The name of the user can be found in the session.
	 * @return
	 *   the expanded password of the user.
	 */
	public byte [] getUserPassword(Session aSession) throws IOException;

	/**
	 * Return true if an user is allowed to invoke a method.
	 *
	 * @param aSession
	 *   the Session invoking the method. The name of the user can be found in the session.
	 * @param aService
	 *   the name of the service
	 * @param aMethod
	 *   the Method being invoked
	 * @return
	 *   true if invocation is permitted.
	 */
	public boolean permitInvocation(Session aSession, AbstractRemoteService aService, Method aMethod) throws IOException;
}

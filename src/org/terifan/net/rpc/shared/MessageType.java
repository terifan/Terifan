package org.terifan.net.rpc.shared;


public enum MessageType
{
	/**
	 * Client requests authorization to access server.
	 */
	AUTHORIZE,
	/**
	 * Server issues a challenge to the client. Response to a client
	 * authorization, session timeout, server restart or failure to authorize
	 * (bad password).
	 */
	CHALLANGE,
	/**
	 * Client response to a server challenge.
	 */
	CHALLANGE_RESPONSE,
	/**
	 * Server responding to successful challenge response from client.
	 */
	AUTHORIZED,
	/**
	 * Message sent from the server to the client. Only available after session
	 * is authorized.
	 */
	CLIENT_MESSAGE,
	/**
	 * Message sent from the server to the client. Only available after session
	 * is authorized.
	 */
	SERVER_MESSAGE,
	/**
	 * Client gracefully disconnecting from server or server gracefully
	 * requesting client to authorize. The request has been carried out and
	 * the response contain the method reply. Disregarding who initiated it,
	 * the session on the server is lost.
	 */
	DISCONNECT,
	/**
	 * Server failed to decode the client message.
	 */
	ERROR,
	/**
	 * Server responded with a bogus message forcing client to perform a new login.
	 */
	UNAUTHORIZED;
}

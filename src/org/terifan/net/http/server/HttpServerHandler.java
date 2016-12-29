package org.terifan.net.http.server;

import java.io.IOException;


public interface HttpServerHandler
{
	public void service(HttpServerRequest aRequest, HttpServerResponse aResponse) throws IOException;
}

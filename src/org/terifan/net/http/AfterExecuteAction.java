package org.terifan.net.http;


@FunctionalInterface
public interface AfterExecuteAction
{
	void execute(HttpRequest aRequest, HttpResponse aResponse);
}

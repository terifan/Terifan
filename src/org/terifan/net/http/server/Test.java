package org.terifan.net.http.server;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			SimpleHttpServer http = new SimpleHttpServer(80, null);

			http.setRequestHandler((req, resp) ->
			{
				resp.setContent("hello world".getBytes());
			});

			http.start(false);

			Thread.sleep(10000);

			http.shutdown(false);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

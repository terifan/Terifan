package org.terifan.net.http.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class HttpResponseFactory
{
	public static byte [] createSoapNoMethodResponse()
	{
		return createSoapdResponse("", "<faultcode>SOAP-ENV:Client</faultcode><faultstring>There was an error in the incoming SOAP request packet: Client, NoMethod</faultstring><faultactor></faultactor>");
	}


	public static byte [] createSoapdResponse(String aBodyXml)
	{
		return createSoapdResponse(null, aBodyXml);
	}


	public static byte [] createSoapdResponse(String aHeaderXml, String aBodyXml)
	{
		ByteArrayOutputStream ba = new ByteArrayOutputStream();

		try (PrintStream ps = new PrintStream(ba))
		{
			ps.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			ps.println("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">");

			if (aHeaderXml != null)
			{
				ps.println("<SOAP-ENV:Header>");
				ps.println(aHeaderXml);
				ps.println("</SOAP-ENV:Header>");
			}
			if (aBodyXml != null)
			{
				ps.println("<SOAP-ENV:Body>");
				ps.println(aBodyXml);
				ps.println("</SOAP-ENV:Body>");
			}
			ps.println("</SOAP-ENV:Envelope>");
		}

		return ba.toByteArray();
	}
}

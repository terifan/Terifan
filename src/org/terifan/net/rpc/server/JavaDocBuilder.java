package org.terifan.net.rpc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.TreeSet;
import org.terifan.io.Streams;
import org.terifan.net.rpc.server.AbstractRemoteService.RemoteMethod;
import org.terifan.net.rpc.shared.ServiceDescription;
import org.terifan.xml.XmlDocument;
import org.terifan.xml.XmlElement;


public class JavaDocBuilder extends AbstractBuilder
{
	public void generate(PrintStream out, AbstractRemoteService ... aServices) throws IOException
	{
		XmlDocument template = new XmlDocument(new ByteArrayInputStream(Streams.fetch(JavaDocBuilder.class.getResourceAsStream("JavaDocTemplate.xsl"))));

		XmlDocument doc = new XmlDocument();
		XmlElement root = doc.appendElement("doc");

		for (AbstractRemoteService s : aServices)
		{
			TreeSet<MethodWrapper> sortedSet = new TreeSet<MethodWrapper>();

			for (Method method : s.getClass().getDeclaredMethods())
			{
				if (method.getAnnotation(RemoteMethod.class) != null)
				{
					MethodWrapper meth = new MethodWrapper(method);
					sortedSet.add(meth);
				}
			}

			ServiceDescription sd = s.getClass().getAnnotation(ServiceDescription.class);
			String desc = sd == null ? "" : sd.value();

			XmlElement service = root.appendElement("service");
			service.appendTextNode("description", desc);
			service.setAttribute("name", s.getName());

			for (MethodWrapper m : sortedSet)
			{
				XmlElement method = service.appendElement("method");
				method.appendTextNode("signature", s.getName()+"."+m.mSignature);
				method.appendTextNode("name", m.mName);
				method.appendTextNode("description", m.mDescription);
				method.appendTextNode("returnDescription", m.mReturnDescription);
				method.appendTextNode("returnType", m.mReturnType);

				for (ParamWrapper p : m.mParams)
				{
					XmlElement param = method.appendElement("param");
					param.appendTextNode("type", p.mType);
					param.appendTextNode("name", p.mName);
					param.appendTextNode("description", p.mDescription);
				}
			}
		}

		String temp = doc.transform(template).toXmlString();

		// Java XSLT implementation can't write DOCTYPE!
		// Dont work: <xsl:output method="html" indent="no" omit-xml-declaration="yes" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd" standalone="yes"/>

		temp = "<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>\n" + temp;

		out.write(temp.getBytes());
	}
}
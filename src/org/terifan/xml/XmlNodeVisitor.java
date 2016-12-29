package org.terifan.xml;


public interface XmlNodeVisitor
{
	public boolean match(XmlNode aNode);

	public Object entering(XmlNode aNode);

	public Object leaving(XmlNode aNode);
}

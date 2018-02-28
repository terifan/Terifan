package org.terifan.xml;


public interface XmlNodeVisitor
{
	public boolean match(XmlNode aNode);

	public Object entering(XmlElement aNode);

	public Object leaving(XmlElement aNode);
}

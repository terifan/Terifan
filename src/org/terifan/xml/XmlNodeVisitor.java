package org.terifan.xml;


@FunctionalInterface
public interface XmlNodeVisitor
{
	default public boolean match(XmlElement aNode)
	{
		return true;
	}

	default public Object entering(XmlElement aNode)
	{
		return null;
	}

	default public Object leaving(XmlElement aNode)
	{
		return null;
	}

	default public Object attribute(XmlElement aNode, String aName, String aValue)
	{
		return null;
	}

	public Object process(XmlNode aNode);
}

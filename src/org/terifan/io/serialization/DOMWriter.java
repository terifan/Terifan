package org.terifan.io.serialization;

import java.lang.reflect.Field;
import org.terifan.xml.XmlElement;
import org.terifan.xml.XmlNode;


public class DOMWriter implements Writer
{
	private XmlNode mNode;


	public DOMWriter(XmlNode aNode)
	{
		mNode = aNode;
	}


	@Override
	public void startOutput()
	{
	}


	@Override
	public void endOutput()
	{
	}


	@Override
	public void startObject(Object aObject, String aTypeName, int aFieldCount)
	{
		XmlElement element = mNode.appendElement("object");
		element.setAttribute("type", aTypeName);
		element.setAttribute("fields", "" + aFieldCount);
		mNode = element;
	}


	@Override
	public void endObject()
	{
		mNode = mNode.getParent();
	}


	@Override
	public void startProperty(Property aProperty, String aName, String aTypeName)
	{
		XmlElement element = mNode.appendElement("field");
		element.setAttribute("name", aName);
		element.setAttribute("type", aTypeName);
		mNode = element;
	}


	@Override
	public void endProperty()
	{
		mNode = mNode.getParent();
	}


	@Override
	public void nextProperty()
	{
	}


	@Override
	public void writeNull()
	{
		mNode.appendElement("null");
	}


	@Override
	public void writePrimitive(Object aPrimitive, String aTypeName)
	{
		XmlElement element = mNode.appendElement("primitive");
		element.setAttribute("type", aTypeName);
		element.setText("" + aPrimitive);
	}


	@Override
	public void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType)
	{
		XmlElement element = mNode.appendElement("array");
		element.setAttribute("depth", "" + aDepth);
		element.setAttribute("length", "" + aLength);
		element.setAttribute("nulls", "" + aNulls);
		element.setAttribute("type", aTypeName);
		element.setAttribute("arrayType", aArrayType);
		mNode = element;
	}


	@Override
	public void endArray()
	{
		mNode = mNode.getParent();
	}


	@Override
	public void nextElement()
	{
	}


	@Override
	public void writeReference(Object aObject)
	{
		mNode.appendElement("reference");
	}
}

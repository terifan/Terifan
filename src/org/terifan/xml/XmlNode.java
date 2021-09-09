package org.terifan.xml;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class XmlNode
{
	protected Node mNode;


	public XmlNode(Node aNode)
	{
		if (aNode == null)
		{
			throw new IllegalArgumentException("Provided node is null.");
		}

		mNode = aNode;
	}


	public XmlNode getParent()
	{
		if (mNode.getParentNode() == null)
		{
			if (mNode.getOwnerDocument() == null)
			{
				return null;
			}
			else
			{
				return new XmlDocument(mNode.getOwnerDocument());
			}
		}
		else
		{
			return new XmlNode(mNode.getParentNode());
		}
	}


    public XmlElement getElement(XPath aXPath)
    {
		XmlNode node = getNode(aXPath);
		if (node == null)
		{
			return null;
		}
		if (node instanceof XmlElement)
		{
			return (XmlElement)node;
		}
		return new XmlElement(node);
	}


    public XmlNode getNode(XPath aXPath)
    {
        try
        {
			Node node = (Node)aXPath.getDOMExpression().evaluate(mNode, XPathConstants.NODE);
			if (node == null)
			{
				return null;
			}
            return new XmlNode(node);
        }
        catch (XPathExpressionException e)
        {
            throw new XmlException(e);
        }
    }


    public XmlNodeList getList(XPath aXPath)
    {
        try
        {
			NodeList nodeList = (NodeList)aXPath.getDOMExpression().evaluate(mNode, XPathConstants.NODESET);
			if (nodeList == null)
			{
				return new XmlNodeList();
			}
            return new XmlNodeList(nodeList);
        }
        catch (XPathExpressionException e)
        {
            throw new XmlException(e);
        }
    }


	/**
	 * Return an array of elements matching the XPath. This method will always
	 * return an array.
	 */
    public XmlElement [] getElements(XPath aXPath)
    {
		try
		{
			NodeList nodeList = (NodeList)aXPath.getDOMExpression().evaluate(mNode, XPathConstants.NODESET);

			if (nodeList == null)
			{
				return new XmlElement[0];
			}

			ArrayList<XmlElement> elements = new ArrayList<>();

			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node instanceof Element)
				{
					elements.add(new XmlElement(node));
				}
			}

			return elements.toArray(new XmlElement[0]);
        }
        catch (XPathExpressionException e)
        {
            throw new XmlException(e);
        }
    }


	/**
	 * Return an array of elements matching the XPath. This method will always
	 * return an array.
	 */
    public XmlElement [] getElements(String aPath)
    {
		return getElements(new XPath(aPath));
    }


    public XmlElement [] getChildElements()
    {
		NodeList nodeList = mNode.getChildNodes();
		if (nodeList == null)
		{
			return new XmlElement[0];
		}
		XmlElement [] elements = new XmlElement[nodeList.getLength()];
		int size = 0;
		for (int i = 0; i < elements.length; i++)
		{
			Node node = nodeList.item(i);
			if (node instanceof Element)
			{
				elements[size++] = new XmlElement(node);
			}
		}
		return Arrays.copyOfRange(elements, 0, size);
    }


    public XmlNode [] getChildNodes()
    {
		NodeList nodeList = mNode.getChildNodes();
		if (nodeList == null)
		{
			return new XmlElement[0];
		}
		XmlNode [] nodes = new XmlNode[nodeList.getLength()];
		for (int i = 0; i < nodes.length; i++)
		{
			Node node = nodeList.item(i);
			if (node instanceof Element)
			{
				nodes[i] = new XmlElement(node);
			}
			else if (node instanceof ProcessingInstruction)
			{
				nodes[i] = new XmlProcessingInstruction(node);
			}
			else
			{
				nodes[i] = new XmlNode(node);
			}
		}
		return nodes;
    }


    public String getText(XPath aXPath, String aDefaultValue)
    {
		String s = getText(aXPath);
		if (s == null)
		{
			return aDefaultValue;
		}
		return s;
	}


    public String getText(XPath aXPath)
    {
        try
        {
            return (String)aXPath.getDOMExpression().evaluate(mNode, XPathConstants.STRING);
        }
        catch (XPathExpressionException e)
        {
            throw new XmlException(e);
        }
    }


    public String [] getTextArray(XPath aXPath)
    {
        try
        {
            XmlNodeList list = new XmlNodeList((NodeList)aXPath.getDOMExpression().evaluate(mNode, XPathConstants.NODESET));

			String [] text = new String[list.size()];

			for (int i = 0; i < list.size(); i++)
			{
				text[i] = list.get(i).getValue();
			}

			return text;
        }
        catch (XPathExpressionException e)
        {
            throw new XmlException(e);
        }
	}


    public XmlElement getElement(String aPath)
    {
		XmlNode node = getNode(aPath);
		if (node == null)
		{
			return null;
		}
		return new XmlElement(node);
	}


    public XmlNode getNode(String aPath)
    {
		assertNodePath(aPath);

		if (aPath.startsWith("/"))
		{
			return new XmlNode(getOwner()).getNode(aPath.substring(1));
		}

		Node node = mNode;
		String [] paths = aPath.split("/");
		for (int j = 0; j < paths.length; j++)
		{
			String path = paths[j];
			boolean last = paths.length-1 == j;
			boolean found = false;
			NodeList list = node.getChildNodes();
			for (int i = 0, sz = list.getLength(); i < sz; i++)
			{
				if (path.startsWith("@"))
				{
					if (!last)
					{
						throw new XmlException("Attributes must be the last path element: path: " + aPath+", element: "+path);
					}
					return new XmlNode(((Element)node).getAttributeNode(path.substring(1)));
				}
				Node child = list.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE && getNodeName(child).equals(path))
				{
					if (last)
					{
						return new XmlNode(child);
					}
					node = child;
					found = true;
					break;
				}
			}
			if (!found)
			{
				return null;
			}
		}
		return null;
    }


    public String getText(String aPath, String aDefaultValue)
    {
		String s = getText(aPath);
		if (s == null)
		{
			return aDefaultValue;
		}
		return s;
	}


    public String getText(String aPath)
    {
		assertNodePath(aPath);

		XmlNode node = getNode(aPath);
		if (node == null)
		{
			return null;
		}
		return node.getValue();
    }


    public XmlNodeList getList(String aPath)
    {
		assertNodePath(aPath);

		if (aPath.startsWith("/"))
		{
			return new XmlNode(getOwner()).getList(aPath.substring(1));
		}

		XmlNodeList list = new XmlNodeList();

		getList(aPath, list);

		return list;
	}



    public String [] getTextArray(String aPath)
    {
		assertNodePath(aPath);

		if (aPath.startsWith("/"))
		{
			return new XmlNode(getOwner()).getTextArray(aPath.substring(1));
		}

		XmlNodeList list = new XmlNodeList();

		getList(aPath, list);

		String [] text = new String[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			text[i] = list.get(i).getValue();
		}

		return text;
	}


    private void getList(String aPath, XmlNodeList aList)
    {
		int index = aPath.indexOf('/');
		boolean last = index == -1;
		String path = last ? aPath : aPath.substring(0, index);
		String remaining = aPath.substring(index+1);

		if (path.startsWith("@"))
		{
			if (!last)
			{
				throw new XmlException("Attributes must be the last path element: path: " + aPath+", element: "+path);
			}
			aList.add(new XmlNode(((Element)mNode).getAttributeNode(path.substring(1))));
			return;
		}

		NodeList list = mNode.getChildNodes();
		for (int i = 0, sz = list.getLength(); i < sz; i++)
		{
			XmlNode child = new XmlNode(list.item(i));
			if (child.getName().equals(path))
			{
				if (last)
				{
					aList.add(child);
				}
				else
				{
					child.getList(remaining, aList);
				}
			}
		}
    }


	private void assertNodePath(String aPath)
	{
		boolean fail = false;
		boolean atFound = false;
		char d = 0;
		for (int i = 0, sz = aPath.length(); !fail && i < sz; i++)
		{
			char c = aPath.charAt(i);
			if (c == '[' || c == ']' || c == '\'' || c == '\"' || c == '*' || c == '=' || (c == '/' && d == '/'))
			{
				fail = true;
			}
			if (atFound && c == '/')
			{
				fail = true;
			}
			if (c == '@')
			{
				if (atFound)
				{
					fail = true;
				}
				atFound = true;
			}
			d = c;
		}
		if (fail)
		{
			throw new IllegalArgumentException("aPath must be a literal node name path. (You may want to use an XPath query?): path: " + aPath);
		}
	}


	public String getName()
	{
		return getNodeName(mNode);
	}


	public String getValue()
	{
//		StringBuilder sb = new StringBuilder();
//		NodeList list = mNode.getChildNodes();
//		for (int i = 0; i < list.getLength(); i++)
//		{
//			sb.append(list.item(i).getNodeValue());
//		}
//		return sb.toString();

		return mNode.getTextContent();
	}


	public XmlElement toElement()
	{
		return new XmlElement(mNode);
	}


	public XmlElement appendElement(String aName)
	{
		XmlElement node = getDocument().createElement(aName);
		mNode.appendChild(node.mNode);
		return node;
	}


	public XmlNode appendChild(XmlNode aNode)
	{
		mNode.appendChild(aNode.mNode);
		return aNode;
	}


	public XmlDocument getDocument()
	{
		return new XmlDocument(getOwner());
	}


	@Override
	public String toString()
	{
		return "XmlNode{" + (mNode == null ? "null" : mNode.getNodeName()) + "}";
	}


	/**
	 * Append text node.
	 *
	 * Note: this method will escape illegal characters!
	 */
	public XmlNode appendTextNode(String aNodeName, Object aText)
	{
		if (aText == null)
		{
			throw new IllegalArgumentException("Provided text is null.");
		}

		Element node = getOwner().createElement(aNodeName);
		node.setTextContent(aText.toString());
		mNode.appendChild(node);
		return this;
	}


	public void writeTo(File aFile)
	{
		try
		{
			newTransformer().transform(new DOMSource(mNode), new StreamResult(aFile));
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public void writeTo(Writer aWriter)
	{
		try
		{
			newTransformer().transform(new DOMSource(mNode), new StreamResult(aWriter));
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public void writeTo(OutputStream aOutputStream)
	{
		try
		{
			newTransformer().transform(new DOMSource(mNode), new StreamResult(aOutputStream));
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public String toXmlString()
	{
		try
		{
			CharArrayWriter cw = new CharArrayWriter();
			newTransformer().transform(new DOMSource(mNode), new StreamResult(cw));
			return cw.toString().trim();
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/**
	 * Return the document as a UTF-8 encoded byte array.
	 */
	public byte[] toByteArray()
	{
		try
		{
			return toXmlString().getBytes("utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public Node getInternalNode()
	{
		return mNode;
	}


	public String[] getAttributes()
	{
		NamedNodeMap attributes = mNode.getAttributes();
		if (attributes == null)
		{
			return new String[0];
		}

		String[] names = new String[attributes.getLength()];
		for (int i = 0; i < attributes.getLength(); i++)
		{
			names[i] = attributes.item(i).getNodeName();
		}

		return names;
	}


	public Object visit(XmlNodeVisitor aVisitor)
	{
		NodeList list = mNode.getChildNodes();

		for (int i = 0; i < list.getLength(); i++)
		{
			XmlNode node;
			Node n = list.item(i);

			if (n instanceof Element)
			{
				node = new XmlElement(n);
			}
			else if (n instanceof Text)
			{
				node = new XmlText(n);
			}
			else
			{
				node = new XmlNode(n);
			}

			if (node instanceof XmlElement)
			{
				XmlElement el = (XmlElement)node;

				if (aVisitor.match(el))
				{
					Object o = aVisitor.entering(el);

					if (o != null)
					{
						return o;
					}

					o = aVisitor.process(el);

					if (o != null)
					{
						return o;
					}

					for (String attr : el.getAttributes())
					{
						o = aVisitor.attribute(el, attr, el.getAttribute(attr));

						if (o != null)
						{
							return o;
						}
					}

					o = node.visit(aVisitor);

					if (o != null)
					{
						return o;
					}

					o = aVisitor.leaving(el);

					if (o != null)
					{
						return o;
					}
				}
			}
			else
			{
				Object o = aVisitor.process(node);

				if (o != null)
				{
					return o;
				}
			}
		}

		return null;
	}


	private static String getNodeName(Node aNode)
	{
		String s = aNode.getLocalName();
		if (s == null)
		{
			s = aNode.getNodeName();
		}
		return s;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof XmlNode)
		{
			return mNode.equals(((XmlNode)obj).mNode);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		return mNode.hashCode();
	}


	private Document getOwner()
	{
		return (Document)((mNode instanceof Document) ? mNode : mNode.getOwnerDocument());
	}


	static Transformer newTransformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError
	{
		return newTransformer(null);
	}


	static Transformer newTransformer(XmlDocument aTemplate) throws TransformerConfigurationException, TransformerFactoryConfigurationError
	{
		Transformer transformer;
		if (aTemplate == null)
		{
			transformer = TransformerFactory.newInstance().newTransformer();
		}
		else
		{
			transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(aTemplate.getInternalNode()));
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
		transformer.setErrorListener(new ErrorListener() {
			@Override
			public void warning(TransformerException aException) throws TransformerException
			{
				throw new IllegalStateException("XSLT warning while transforming document.", aException);
			}
			@Override
			public void error(TransformerException aException) throws TransformerException
			{
				throw new IllegalStateException("XSLT error while transforming document.", aException);
			}
			@Override
			public void fatalError(TransformerException aException) throws TransformerException
			{
				throw new IllegalStateException("XSLT fatal error while transforming document.", aException);
			}
		});
		return transformer;
	}
}
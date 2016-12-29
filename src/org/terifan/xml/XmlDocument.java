package org.terifan.xml;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.terifan.util.log.Log;
import static org.terifan.xml.XmlNode.newTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlDocument extends XmlNode
{
    public XmlDocument()
    {
		super(newDocument(false));
    }


    public XmlDocument(boolean aNamespaceAware)
    {
		super(newDocument(aNamespaceAware));
    }


    public XmlDocument(String aXmlContent)
    {
		super(parse(false, aXmlContent));
    }


    public XmlDocument(boolean aNamespaceAware, String aXmlContent)
    {
		super(parse(aNamespaceAware, aXmlContent));
    }


    public XmlDocument(File aXmlFile)
    {
		super(parse(false, aXmlFile));
    }


    public XmlDocument(boolean aNamespaceAware, File aXmlFile)
    {
		super(parse(aNamespaceAware, aXmlFile));
    }


    public XmlDocument(Reader aXmlStream)
    {
		super(parse(false, aXmlStream));
    }


    public XmlDocument(boolean aNamespaceAware, Reader aXmlStream)
    {
		super(parse(aNamespaceAware, aXmlStream));
    }


    public XmlDocument(InputStream aXmlStream)
    {
		super(parse(false, aXmlStream));
    }


    public XmlDocument(boolean aNamespaceAware, InputStream aXmlStream)
    {
		super(parse(aNamespaceAware, aXmlStream));
    }


    public XmlDocument(Document aXmlDocument)
    {
		super(aXmlDocument);
    }


    public XmlDocument(Element aXmlElement)
    {
		super(parse(false, aXmlElement));
    }


    public XmlDocument(boolean aNamespaceAware, Element aXmlElement)
    {
		super(parse(aNamespaceAware, aXmlElement));
    }


    public XmlDocument(URL aXmlURL)
    {
		super(parse(false, aXmlURL));
    }


    public XmlDocument(boolean aNamespaceAware, URL aXmlURL)
    {
		super(parse(aNamespaceAware, aXmlURL));
    }


    public static Document parse(boolean aNamespaceAware, Object aXmlDocument)
    {
		if (aXmlDocument == null)
		{
			throw new XmlException("Provided argument is null.");
		}
        try
        {
			try
			{
				if (aXmlDocument instanceof String)
				{
					return newBuilder(aNamespaceAware).parse(new InputSource(new StringReader((String)aXmlDocument)));
				}
				else if (aXmlDocument instanceof File)
				{
					return newBuilder(aNamespaceAware).parse((File)aXmlDocument);
				}
				else if (aXmlDocument instanceof Reader)
				{
					return newBuilder(aNamespaceAware).parse(new InputSource((Reader)aXmlDocument));
				}
				else if (aXmlDocument instanceof InputStream)
				{
					return newBuilder(aNamespaceAware).parse((InputStream)aXmlDocument);
				}
				else if (aXmlDocument instanceof URL)
				{
					return newBuilder(aNamespaceAware).parse((String)aXmlDocument.toString());
				}
				else
				{
					throw new IllegalArgumentException("Unsupported type: " + aXmlDocument);
				}
			}
			finally
			{
				if (aXmlDocument instanceof Closeable)
				{
					((Closeable)aXmlDocument).close();
				}
			}
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new XmlException(e);
        }
    }


    public XmlDocument transform(XmlDocument aTemplate)
    {
		try
		{
			XmlDocument result = new XmlDocument();
			newTransformer(aTemplate).transform(new DOMSource(mNode), new DOMResult(result.getInternalNode()));
			return result;
		}
		catch (TransformerException e)
		{
            throw new XmlException(e);
		}
    }


    public void transform(XmlDocument aTemplate, OutputStream aOutput) throws IOException
    {
		try
		{
			newTransformer(aTemplate).transform(new DOMSource(mNode), new StreamResult(aOutput));
		}
		catch (TransformerException e)
		{
            throw new XmlException(e);
		}
    }


    public void transform(XmlDocument aTemplate, Writer aOutput) throws IOException
    {
		try
		{
			newTransformer(aTemplate).transform(new DOMSource(mNode), new StreamResult(aOutput));
		}
		catch (TransformerException e)
		{
            throw new XmlException(e);
		}
    }


    public void transform(XmlDocument aTemplate, File aOutput) throws IOException
    {
		try
		{
			newTransformer(aTemplate).transform(new DOMSource(mNode), new StreamResult(aOutput));
		}
		catch (TransformerException e)
		{
            throw new XmlException(e);
		}
    }


	private static DocumentBuilder newBuilder(boolean aNamespaceAware) throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(aNamespaceAware);

		return factory.newDocumentBuilder();
	}


	private static Document newDocument(boolean aNamespaceAware)
	{
        try
        {
            return newBuilder(aNamespaceAware).newDocument();
        }
        catch (ParserConfigurationException e)
        {
            throw new XmlException(e);
        }
	}


	public XmlElement createElement(String aName)
	{
		return new XmlElement(((Document)mNode).createElement(aName));
	}


	public XmlProcessingInstruction getProcessingInstruction(String aName, String aData)
	{
		Document doc = (Document)mNode;
		ProcessingInstruction pi = doc.createProcessingInstruction(aName, aData);
		doc.insertBefore(pi, doc.getDocumentElement());
		return new XmlProcessingInstruction(pi);
	}


	public XmlElement getFirstElement()
	{
		NodeList nodeList = mNode.getChildNodes();

		if (nodeList != null)
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node instanceof Element)
				{
					return new XmlElement(node);
				}
			}
		}

		return null;
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			Log.out.println(new XmlDocument().appendElement("test").appendTextNode("v", "x").toXmlString());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}
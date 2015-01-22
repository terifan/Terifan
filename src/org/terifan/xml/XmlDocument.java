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
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
		super(newDocument());
    }


    public XmlDocument(String aXmlContent)
    {
		super(parse(aXmlContent));
    }


    public XmlDocument(File aXmlFile)
    {
		super(parse(aXmlFile));
    }


    public XmlDocument(Reader aXmlStream)
    {
		super(parse(aXmlStream));
    }


    public XmlDocument(InputStream aXmlStream)
    {
		super(parse(aXmlStream));
    }


    public XmlDocument(Document aXmlDocument)
    {
		super(aXmlDocument);
    }


    public XmlDocument(Element aXmlElement)
    {
		super(parse(aXmlElement));
    }


    public XmlDocument(URL aXmlURL)
    {
		super(parse(aXmlURL));
    }


    public static Document parse(final Object aXmlDocument)
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
					return newBuilder().parse(new InputSource(new StringReader((String)aXmlDocument)));
				}
				else if (aXmlDocument instanceof File)
				{
					return newBuilder().parse((File)aXmlDocument);
				}
				else if (aXmlDocument instanceof Reader)
				{
					return newBuilder().parse(new InputSource((Reader)aXmlDocument));
				}
				else if (aXmlDocument instanceof InputStream)
				{
					return newBuilder().parse((InputStream)aXmlDocument);
				}
				else if (aXmlDocument instanceof URL)
				{
					return newBuilder().parse((String)aXmlDocument.toString());
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
        catch (ParserConfigurationException e)
        {
            throw new XmlException(e);
        }
        catch (SAXException e)
        {
            throw new XmlException(e);
        }
        catch (IOException e)
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
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(aTemplate.getInternalNode()));
			transformer.transform(new DOMSource(mNode), new StreamResult(aOutput));
		}
		catch (TransformerException e)
		{
            throw new XmlException(e);
		}
    }


	private static DocumentBuilder newBuilder() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		return documentBuilder;
	}


	private static Transformer newTransformer(XmlDocument aTemplate) throws TransformerConfigurationException, TransformerFactoryConfigurationError
	{
		Transformer transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(aTemplate.getInternalNode()));
		transformer.setErrorListener(new ErrorListener() {
			@Override
			public void warning(TransformerException aException) throws TransformerException
			{
				throw new RuntimeException("XSLT warning while transforming document.", aException);
			}
			@Override
			public void error(TransformerException aException) throws TransformerException
			{
				throw new RuntimeException("XSLT error while transforming document.", aException);
			}
			@Override
			public void fatalError(TransformerException aException) throws TransformerException
			{
				throw new RuntimeException("XSLT fatal error while transforming document.", aException);
			}
		});
		return transformer;
	}


	private static Document newDocument()
	{
        try
        {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder documentBuilder = factory.newDocumentBuilder();

            return documentBuilder.newDocument();
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
}
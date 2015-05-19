package org.terifan.util.bundle;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.terifan.io.Streams;
import org.terifan.util.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class TestXml
{
	public static void main(String ... args)
	{
		try
		{
			Bundle bundle = new Bundle();

			byte[] xmlData = Streams.fetch(TestXml.class.getResource("edoc.xml"));
//			byte[] xmlData = Streams.fetch(TestXml.class.getResource("mds1.xml"));
//			byte[] xmlData = Streams.fetch(TestXml.class.getResource("mds2.xml"));

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlData));
			list(doc, bundle);

			byte[] binData = new BinaryEncoder().marshal(bundle);

			String bunData = new BUNEncoder().marshal(bundle, true);

			Log.out.println(new BUNEncoder().marshal(bundle));

//			Debug.hexDump(binData);

			ByteArrayOutputStream zipBin = new ByteArrayOutputStream();
			try (DeflaterOutputStream dos = new DeflaterOutputStream(zipBin))
			{
				dos.write(binData);
			}

			ByteArrayOutputStream zipBun = new ByteArrayOutputStream();
			try (DeflaterOutputStream dos = new DeflaterOutputStream(zipBun))
			{
				dos.write(bunData.getBytes());
			}

			ByteArrayOutputStream zipXml = new ByteArrayOutputStream();
			try (DeflaterOutputStream dos = new DeflaterOutputStream(zipXml))
			{
				dos.write(xmlData);
			}

			Log.out.println();
			Log.out.println("xml: " + xmlData.length);
			Log.out.println("bun: " + bunData.length());
			Log.out.println("bin: " + binData.length);
			Log.out.println("zip-xml: " + zipXml.size());
			Log.out.println("zip-bun: " + zipBun.size());
			Log.out.println("zip-bin: " + zipBin.size());
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static void list(Node aNode, Bundle aBundle)
	{
		NodeList list = aNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				NodeList children = node.getChildNodes();
				if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
				{
					String value = children.item(0).getNodeValue();
					Object output = value;

					try
					{
						output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
					}
					catch (Exception e)
					{
					}
					if (output == value)
					{
						try
						{
							output = new SimpleDateFormat("yyyy-MM-dd").parse(value);
						}
						catch (Exception e)
						{
						}
					}
					if (output == value)
					{
						try
						{
							output = Double.parseDouble(value);
							output = Long.parseLong(value);
							output = Integer.parseInt(value);
						}
						catch (Exception e)
						{
						}
					}

					String name = node.getNodeName();

					if ((output instanceof String) && output.toString().length() > 20) // (name.equals("data") || name.equals("value")) &&
					{
						if (output.toString().matches("[A-Za-z0-9+/]*"))
						{
							try
							{
								output = Base64.decode(output.toString());
							}
							catch (Exception e)
							{
							}
						}
					}

					if (aBundle.containsKey(name))
					{
						Object a = aBundle.get(name);
						if (a instanceof ArrayList)
						{
							((ArrayList)a).add(output);
						}
						else
						{
							ArrayList tmp = new ArrayList();
							tmp.add(a);
							tmp.add(output);
							aBundle.put(name, tmp);
						}
					}
					else
					{
						aBundle.put(name, output);
					}
				}
				else if (children.getLength() > 0)
				{
					Bundle bundle = new Bundle();
					list(node, bundle);

					String name = node.getNodeName();

					if (aBundle.containsKey(name))
					{
						Object a = aBundle.get(name);
						if (a instanceof ArrayList)
						{
							((ArrayList)a).add(bundle);
						}
						else
						{
							ArrayList tmp = new ArrayList();
							tmp.add(a);
							tmp.add(bundle);
							aBundle.put(name, tmp);
						}
					}
					else
					{
						aBundle.put(name, bundle);
					}
				}
			}
		}
	}
}

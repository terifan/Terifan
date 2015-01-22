package org.terifan.xml;

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


public class SimpleNamespaceContext implements NamespaceContext
{
	private HashMap<String,String> mContexts;


	public SimpleNamespaceContext()
	{
		mContexts = new HashMap<>();
	}


	public SimpleNamespaceContext(String aDefault)
	{
		mContexts = new HashMap<>();
	}
	
	
	public SimpleNamespaceContext add(String aName, String aContext)
	{
		mContexts.put(aName, aContext);
		return this;
	}
	
	
	public SimpleNamespaceContext addDefault(String aContext)
	{
		mContexts.put("", aContext);
		return this;
	}
	
	
	@Override
	public String getNamespaceURI(String aPrefix)
	{
		if (aPrefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
		{
			return mContexts.get("");
		}

		String ctx = mContexts.get(aPrefix);

		if (ctx == null)
		{
			return XMLConstants.NULL_NS_URI;
		}

		return ctx;
	}


	@Override
	public String getPrefix(String aNamespaceURI)
	{
		return null;
	}


	@Override
	public Iterator getPrefixes(String aNamespaceURI)
	{
		return null;
	}
}

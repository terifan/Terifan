package org.terifan.io.serialization;

import java.io.OutputStream;
import java.lang.reflect.Field;
import org.terifan.util.log.Log;


public class XMLWriter implements Writer
{
	private int mIndent;
	private OutputStream mOutputStream;


	public XMLWriter(OutputStream aOutputStream)
	{
		mOutputStream = aOutputStream;
	}


	@Override
	public void startOutput()
	{
		print("<?xml version='1.0' encoding='utf-8'?>");
	}


	@Override
	public void endOutput()
	{
	}


	@Override
	public void startObject(Object aObject, String aTypeName, int aFieldCount)
	{
		print("<object fields='" + aFieldCount + "' type='" + aTypeName + "'>");
		mIndent++;
	}


	@Override
	public void endObject()
	{
		mIndent--;
		print("</object>");
	}


	@Override
	public void startProperty(Property aProperty, String aName, String aTypeName)
	{
		print("<field name='"+aName+"' type='" + aTypeName + "'>");
		mIndent++;
	}


	@Override
	public void endProperty()
	{
		mIndent--;
		print("</field>");
	}


	@Override
	public void nextProperty()
	{
	}


	@Override
	public void writeNull()
	{
		print("<null/>");
	}


	@Override
	public void writePrimitive(Object aPrimitive, String aTypeName)
	{
		String value = "" + aPrimitive;
		value = value.replace("&", "&amp;");
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		print("<primitive type='" + aTypeName + "'>" + value + "</primitive>");
	}


	@Override
	public void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType)
	{
		print("<array arrayType='" + aArrayType + "' depth='"+aDepth+"' length='"+aLength+"' nulls='"+aNulls+"' type='"+aTypeName+"'>");
		mIndent++;
	}


	@Override
	public void endArray()
	{
		mIndent--;
		print("</array>");
	}


	@Override
	public void nextElement()
	{
	}


	@Override
	public void writeReference(Object aObject)
	{
		print("<reference/>");
	}


	private void print(String aString)
	{
		try
		{
			for (int i = 0; i < mIndent; i++)
			{
				Log.out.print("   ");
				mOutputStream.write('\t');
			}
			Log.out.println(aString);
			mOutputStream.write(aString.getBytes("utf-8"));
			mOutputStream.write('\r');
			mOutputStream.write('\n');
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}
	}
}
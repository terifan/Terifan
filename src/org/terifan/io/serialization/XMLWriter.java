package org.terifan.io.serialization;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.terifan.io.serialization.ObjectSerializer.Serialize;
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
		print("<?xml version='1.0' encoding='iso-8859-1'?>");
	}


	@Override
	public void endOutput()
	{
	}


	@Override
	public void startObject(Object aObject, String aTypeName, int aFieldCount)
	{
		print("<object type='" + aTypeName + "' fields='" + aFieldCount + "'>");
		mIndent++;
	}


	@Override
	public void endObject()
	{
		mIndent--;
		print("</object>");
	}


	@Override
	public void startField(Field aField, String aName, String aTypeName)
	{
		print("<field name='"+aName+"' type='" + aTypeName + "'>");
		mIndent++;
	}


	@Override
	public void endField()
	{
		mIndent--;
		print("</field>");
	}


	@Override
	public void nextField()
	{
	}


	@Override
	public void writePrimitive(Object aPrimitive, String aTypeName)
	{
		if (aPrimitive == null)
		{
			print("<null/>");
		}
		else
		{
			print("<primitive type='" + aTypeName + "'>" + aPrimitive + "</primitive>");
		}
	}


	@Override
	public void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType)
	{
		print("<array depth='"+aDepth+"' length='"+aLength+"' nulls='"+aNulls+"' type='"+aTypeName+"' arrayType='" + aArrayType + "'>");
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
			mOutputStream.write(aString.getBytes());
			mOutputStream.write('\r');
			mOutputStream.write('\n');
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}
	}
}

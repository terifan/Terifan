package org.terifan.io.serialization;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import org.terifan.io.serialization.ObjectSerializer.Serialize;
import org.terifan.util.log.Log;


public class JSONWriter implements Writer
{
	private int mIndent;
	private OutputStream mOutputStream;
	private boolean mWasNewLine;
	private boolean mFirstLine;
	private ArrayDeque<String> mStack;


	public JSONWriter(OutputStream aOutputStream)
	{
		mStack = new ArrayDeque<>();
		mOutputStream = aOutputStream;
		mFirstLine = true;
	}


	@Override
	public void startOutput()
	{
	}


	@Override
	public void endOutput()
	{
		print("", true, false);
	}


	@Override
	public void startObject(Object aObject, String aTypeName, int aFieldCount)
	{
		print("{", false, true);
		mIndent++;
		mStack.push("object");
	}


	@Override
	public void endObject()
	{
		mIndent--;
		print("}", true, false);
		mStack.poll();
	}


	@Override
	public void startField(Field aField, String aName, String aTypeName)
	{
		print("\""+aName+"\": ", true, false);
		mStack.push("field");
	}


	@Override
	public void endField()
	{
		mStack.poll();
	}


	@Override
	public void nextField()
	{
		print(",", false, false);
	}


	@Override
	public void writePrimitive(Object aPrimitive, String aTypeName)
	{
		if (aPrimitive instanceof String || aPrimitive instanceof Character)
		{
			print("\"" + aPrimitive + "\"", false, false);
		}
		else
		{
			print("" + aPrimitive, false, false);
		}
	}


	@Override
	public void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType)
	{
		print("[", false, false);
		mIndent++;
		mStack.push(aDepth <= 1 ? "array" : "matrix");
	}


	@Override
	public void endArray()
	{
		mIndent--;
		print("]", false, false);
		mStack.poll();
	}


	@Override
	public void nextElement()
	{
		print(", ", false, false);
	}


	@Override
	public void writeReference(Object aObject)
	{
		print("null", false, false);
	}


	private void print(String aString, boolean aNewLineBefore, boolean aNewLineAfter)
	{
		try
		{
			if (!mFirstLine && !mWasNewLine && aNewLineBefore)
			{
				Log.out.println();
				mOutputStream.write('\r');
				mOutputStream.write('\n');
				mWasNewLine = true;
			}
			if (mWasNewLine)
			{
				for (int i = 0; i < mIndent; i++)
				{
					Log.out.print("\t");
					mOutputStream.write('\t');
				}
			}
			mWasNewLine = false;

			Log.out.print(aString);
			mOutputStream.write(aString.getBytes());

			if (aNewLineAfter)
			{
				Log.out.println();
				mOutputStream.write('\r');
				mOutputStream.write('\n');
				mWasNewLine = true;
			}
			mFirstLine = false;
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}
	}
}

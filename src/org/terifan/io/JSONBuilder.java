package org.terifan.io;

import java.io.StringWriter;
import java.util.ArrayDeque;


public class JSONBuilder
{
	private StringWriter mWriter;
	private ArrayDeque<Boolean> mState;
	private ArrayDeque<Boolean> mType;

	private final static boolean ARRAY = false;
	private final static boolean OBJECT = true;
	private final static boolean FIRST = true;
	private final static boolean NOT_FIRST = false;


	public JSONBuilder()
	{
		mState = new ArrayDeque();
		mType = new ArrayDeque();
		mWriter = new StringWriter();
	}


	public JSONBuilder object()
	{
		try
		{
			comma();
			mType.push(OBJECT);
			mState.push(FIRST);
			mWriter.append("{");
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	public JSONBuilder endObject()
	{
		try
		{
			if (mType.pop() != OBJECT)
			{
				throw new IllegalStateException();
			}
			mState.pop();
			mWriter.append("}");
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	public JSONBuilder array()
	{
		try
		{
			comma();
			mType.push(ARRAY);
			mState.push(FIRST);
			mWriter.append("[");
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	public JSONBuilder endArray()
	{
		try
		{
			if (mType.pop() != ARRAY)
			{
				throw new IllegalStateException();
			}
			mState.pop();
			mWriter.append("]");
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	public JSONBuilder key(String aKey)
	{
		try
		{
			comma();
			mWriter.append("\"" + escape(aKey) + "\":");
			mState.pop();
			mState.push(FIRST);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	private static String escape(String aKey)
	{
		return aKey.replace("\"", "\\\"").replace("\n", "\\\n").replace("\t", "\\\t").replace("\r", "\\\r");
	}


	public JSONBuilder value(Object aValue)
	{
		try
		{
			comma();

			if (aValue == null)
			{
				mWriter.append("null");
			}
			else if (aValue instanceof Boolean)
			{
				mWriter.append(aValue.toString());
			}
			else if (aValue instanceof Number)
			{
				mWriter.append(aValue.toString());
			}
			else if (aValue instanceof JSONBuilder)
			{
				mWriter.append(aValue.toString());
			}
			else
			{
				mWriter.append("\"" + escape(aValue.toString()) + "\"");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	private void comma()
	{
		if (!mState.isEmpty())
		{
			if (mState.peek() == NOT_FIRST)
			{
				mWriter.append(",");
			}
			mState.pop();
			mState.push(NOT_FIRST);
		}
	}


	public JSONBuilder put(String aKey, Object aObject)
	{
		try
		{
			key(aKey).value(aObject);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
		return this;
	}


	@Override
	public String toString()
	{
		return mWriter.toString();
	}
}

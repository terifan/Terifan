package org.terifan.util.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;
import org.terifan.io.BitInputStream;
import org.terifan.io.ByteBufferInputStream;


public class BinaryDecoder
{
	private HashSet<String> mKnownKeys;
	private TreeMap<Integer,String> mBundleKeys;
	private BitInputStream mInput;


	public BinaryDecoder()
	{
		mKnownKeys = new HashSet<>();
	}


	/**
	 * Add words to the internal dictionary. The same words must be added in identical order when encoding the message.
	 *
	 * @param aKeys
	 *   a list of Strings
	 * @return
	 *   this instance
	 */
	public BinaryDecoder addKeys(String... aKeys)
	{
		if (aKeys != null)
		{
			mKnownKeys.addAll(Arrays.asList(aKeys));
		}

		return this;
	}


	public Bundle unmarshal(byte[] aBuffer) throws IOException
	{
		return unmarshal(new ByteArrayInputStream(aBuffer));
	}


	public Bundle unmarshal(ByteBuffer aBuffer) throws IOException
	{
		return unmarshal(new ByteBufferInputStream(aBuffer));
	}


	public Bundle unmarshal(InputStream aInputStream) throws IOException
	{
		mInput = new BitInputStream(aInputStream);

		mBundleKeys = new TreeMap<>();

		for (String key : mKnownKeys)
		{
			mBundleKeys.put(mBundleKeys.size(), key);
		}
		for (;;)
		{
			int len = mInput.readVariableInt(7, 0, false);

			if (len == 0)
			{
				break;
			}

			mBundleKeys.put(mBundleKeys.size(), readString(len));
		}

		return readBundle(new Bundle());
	}


	private Bundle readBundle(Bundle aBundle) throws IOException
	{
		for (;;)
		{
			FieldType fieldType = FieldType.values()[(int)mInput.readBits(4)];

			if (fieldType == FieldType.TERMINATOR)
			{
				break;
			}

			String key = mBundleKeys.get((int)mInput.readBitsInRange(mBundleKeys.size() - 1));

			Object value;

			if (mInput.readBit() == 1)
			{
				value = readValue(fieldType);
			}
			else if (mInput.readBit() == 1)
			{
				value = readList(fieldType);
			}
			else if (fieldType == FieldType.BYTE)
			{
				value = new byte[mInput.readVariableInt(3, 0, false)];
				mInput.align();
				mInput.read((byte[])value);
			}
			else
			{
				ArrayList list = readList(fieldType);

				value = Array.newInstance(fieldType.getNumberType(), list.size());
				for (int j = 0; j < list.size(); j++)
				{
					Array.set(value, j, list.get(j));
				}
			}

			aBundle.put(key, value);
		}

		return aBundle;
	}


	private ArrayList readList(FieldType aFieldType) throws IOException
	{
		int flags = mInput.readVariableInt(3, 0, true);
		boolean hasNulls = flags < 0;
		int len = Math.abs(flags);

		ArrayList list = new ArrayList(len);

		for (int i = 0; i < len; i++)
		{
			Object value;

			if (hasNulls && mInput.readBit() == 1)
			{
				value = null;
			}
			else
			{
				value = readValue(aFieldType);
			}

			list.add(value);
		}

		return list;
	}


	private Object readValue(FieldType aFieldType) throws IOException
	{
		switch (aFieldType)
		{
			case BOOLEAN:
				return mInput.readBit() == 1;
			case BYTE:
				return (byte)mInput.read();
			case SHORT:
				return (short)mInput.readVariableInt(3, 0, true);
			case CHAR:
				return (char)mInput.readVariableInt(3, 0, false);
			case INT:
				return mInput.readVariableInt(3, 0, true);
			case LONG:
				return mInput.readVariableLong(7, 0, true);
			case FLOAT:
				return Float.intBitsToFloat(mInput.readVariableInt(7, 0, false));
			case DOUBLE:
				return Double.longBitsToDouble(mInput.readVariableLong(7, 0, false));
			case STRING:
				return readString(mInput.readVariableInt(3, 0, false));
			case DATE:
				return new Date(mInput.readVariableLong(7, 0, false));
			case BUNDLE:
				return readBundle(new Bundle());
			default:
				throw new UnsupportedOperationException("Unsupported field type: " + aFieldType);
		}
	}


	private String readString(int aLength) throws IOException
	{
		byte[] buf = new byte[aLength];

		mInput.align();

		if (mInput.read(buf) != buf.length)
		{
			throw new IOException("Unexpected end of stream");
		}

		return new String(buf, "utf-8");
	}
}

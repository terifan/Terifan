package org.terifan.util.bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import org.terifan.util.Convert;
import org.terifan.io.BitOutputStream;
import org.terifan.io.ByteBufferOutputStream;


public class BinaryEncoder
{
	private TreeMap<String,Integer> mKeys;
	private BitOutputStream mOutput;


	public BinaryEncoder()
	{
	}


	public byte[] marshal(Bundle aBundle) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshal(aBundle, baos);
		return baos.toByteArray();
	}


	public void marshal(Bundle aBundle, ByteBuffer aBuffer) throws IOException
	{
		marshal(aBundle, new ByteBufferOutputStream(aBuffer));
	}


	public void marshal(Bundle aBundle, OutputStream aOutputStream) throws IOException
	{
		mOutput = new BitOutputStream(aOutputStream);
		mKeys = new TreeMap<>();

		writeBundle(aBundle);

		mOutput.finish();
	}


	private void writeBundle(Bundle aBundle) throws IOException
	{
		String[] keys = writeBundleKeys(aBundle);

		for (String key : keys)
		{
			Object value = aBundle.get(key);
			FieldType fieldType = FieldType.valueOf(value);
			Class<? extends Object> cls = value.getClass();

//			mOutput.writeBits(fieldType.ordinal(), 4);
			mOutput.writeBits(fieldType.getSymbol(), fieldType.getSymbolLength());

			if (cls.isArray())
			{
				mOutput.writeBits(0b11, 2);
				if (fieldType == FieldType.BYTE)
				{
					mOutput.writeVariableInt(((byte[])value).length, 3, 4, false);
					mOutput.align();
					mOutput.write((byte[])value);
				}
				else
				{
					writeList(fieldType, value);
				}
			}
			else if (List.class.isAssignableFrom(cls))
			{
				mOutput.writeBits(0b10, 2);
				writeList(fieldType, ((List)value).toArray());
			}
			else
			{
				mOutput.writeBits(0b0, 1);
				writeValue(fieldType, value);
			}
		}
	}


	private String[] writeBundleKeys(Bundle aBundle) throws IOException
	{
		int initialKeyCount = mKeys.size();
		String[] keys = aBundle.keySet().toArray(new String[aBundle.size()]);
		ByteArrayOutputStream keyData = new ByteArrayOutputStream();

		mOutput.writeVariableInt(keys.length, 3, 0, false);

		for (String key : keys)
		{
			if (mKeys.containsKey(key))
			{
				mOutput.writeBit(0);
				mOutput.writeBitsInRange(mKeys.get(key), initialKeyCount);
			}
			else
			{
				byte[] buffer = Convert.encodeUTF8(key);
				keyData.write(buffer);

				mOutput.writeBit(1);
				mOutput.writeVariableInt(buffer.length, 3, 0, false);

				mKeys.put(key, mKeys.size());
			}
		}

		if (keyData.size() > 0)
		{
			mOutput.align();

			keyData.writeTo(mOutput);
		}

		return keys;
	}


	private void writeList(FieldType aFieldType, Object aArray) throws IOException
	{
		int length = Array.getLength(aArray);

		boolean hasNull = false;
		for (int i = 0; i < length; i++)
		{
			if (Array.get(aArray, i) == null)
			{
				hasNull = true;
				break;
			}
		}

		mOutput.writeVariableInt(hasNull ? -length : length, 3, 0, true);

		for (int i = 0; i < length; i++)
		{
			Object item = Array.get(aArray, i);

			if (hasNull)
			{
				mOutput.writeBit(item == null ? 1 : 0);
			}

			if (item != null)
			{
				writeValue(aFieldType, item);
			}
		}
	}


	private void writeValue(FieldType aFieldType, Object aValue) throws IOException
	{
		switch (aFieldType)
		{
			case BOOLEAN:
				mOutput.writeBit((Boolean)aValue ? 1 : 0);
				break;
			case BYTE:
				mOutput.write(0xff & (Byte)aValue);
				break;
			case SHORT:
				mOutput.writeVariableInt((Short)aValue, 3, 0, true);
				break;
			case CHAR:
				mOutput.writeVariableInt((Character)aValue, 3, 0, false);
				break;
			case INT:
				mOutput.writeVariableInt((Integer)aValue, 3, 4, true);
				break;
			case LONG:
				mOutput.writeVariableLong((Long)aValue, 7, 0, true);
				break;
			case FLOAT:
				mOutput.writeVariableInt(Float.floatToIntBits((Float)aValue), 7, 0, false);
				break;
			case DOUBLE:
				mOutput.writeVariableLong(Double.doubleToLongBits((Double)aValue), 7, 0, false);
				break;
			case STRING:
				byte[] buffer = Convert.encodeUTF8((String)aValue);
				mOutput.writeVariableInt(buffer.length, 3, 4, false);
				mOutput.align();
				mOutput.write(buffer);
				break;
			case DATE:
				mOutput.writeVariableLong(((Date)aValue).getTime(), 7, 0, false);
				break;
			case BUNDLE:
				writeBundle((Bundle)aValue);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported field type: " + aFieldType);
		}
	}
}
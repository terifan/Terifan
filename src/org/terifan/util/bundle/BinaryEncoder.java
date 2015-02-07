package org.terifan.util.bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import org.terifan.util.Convert;
import org.terifan.io.BitOutputStream;
import org.terifan.io.ByteBufferOutputStream;


public class BinaryEncoder
{
	private TreeSet<String> mKnownKeys;
	private TreeMap<String,Integer> mBundleKeys;
	private BitOutputStream mOutput;


	public BinaryEncoder()
	{
		mKnownKeys = new TreeSet<>();
	}


	/**
	 * Add predetermined key names to minimize the size of the encoded data. The same keys must be provided when decoding the encoded message.
	 *
	 * Note: The list can include keys not found in the Bundle.
	 *
	 * @param aKeys
	 *   a list of key names
	 * @return
	 *   this instance
	 */
	public BinaryEncoder addKeys(String... aKeys)
	{
		if (aKeys != null)
		{
			mKnownKeys.addAll(Arrays.asList(aKeys));
		}

		return this;
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

		mBundleKeys = new TreeMap<>();

		for (String key : mKnownKeys)
		{
			mBundleKeys.put(key, mBundleKeys.size());
		}
		for (String key : collectKeys(aBundle, new TreeSet<>()))
		{
			if (!mBundleKeys.containsKey(key))
			{
				byte[] buffer = Convert.encodeUTF8(key);
				mOutput.writeVariableInt(buffer.length, 7, 0, false);
				mOutput.write(buffer);
				mBundleKeys.put(key, mBundleKeys.size());
			}
		}

		mOutput.writeVariableInt(0, 7, 0, false); // terminator

		writeBundle(aBundle);

		mOutput.finish();
	}


	private TreeSet<String> collectKeys(Bundle aBundle, TreeSet<String> aKeys) throws IOException
	{
		if (aBundle != null)
		{
			for (String key : aBundle)
			{
				Object value = aBundle.get(key);
				FieldType fieldType = FieldType.valueOf(value);

				if (fieldType != null)
				{
					aKeys.add(key);

					if (fieldType == FieldType.BUNDLE)
					{
						Class<? extends Object> cls = value.getClass();

						if (List.class.isAssignableFrom(cls))
						{
							for (Object item : (List)value)
							{
								collectKeys((Bundle)item, aKeys);
							}
						}
						else if (cls.isArray())
						{
							for (int i = 0; i < Array.getLength(value); i++)
							{
								collectKeys((Bundle)Array.get(value, i), aKeys);
							}
						}
						else
						{
							collectKeys((Bundle)value, aKeys);
						}
					}
				}
			}
		}

		return aKeys;
	}


	private void writeBundle(Bundle aBundle) throws IOException
	{
		for (String key : aBundle)
		{
			Object value = aBundle.get(key);
			FieldType fieldType = FieldType.valueOf(value);

			if (fieldType != null)
			{
//				Log.out.println("ENCODE: " + fieldType+" "+key);

				mOutput.writeBits(fieldType.ordinal(), 4);
				mOutput.writeBitsInRange(mBundleKeys.get(key), mBundleKeys.size());

				Class<? extends Object> cls = value.getClass();

				if (cls.isArray())
				{
					mOutput.writeBits(0b00, 2);
					if (fieldType == FieldType.BYTE)
					{
						mOutput.writeVariableInt(((byte[])value).length, 3, 0, false);
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
					mOutput.writeBits(0b01, 2);
					writeList(fieldType, ((List)value).toArray());
				}
				else
				{
					mOutput.writeBits(0b1, 1);
					writeValue(fieldType, value);
				}
			}
		}

		mOutput.writeBits(FieldType.TERMINATOR.ordinal(), 4);
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
				mOutput.writeVariableInt((Integer)aValue, 3, 0, true);
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
				writeString((String)aValue);
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


	private void writeString(String aValue) throws IOException
	{
		byte[] buffer = Convert.encodeUTF8(aValue);
		mOutput.writeVariableInt(buffer.length, 3, 0, false);
		mOutput.align();
		mOutput.write(buffer);
	}
}

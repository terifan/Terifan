package org.terifan.util.bundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import org.terifan.io.BitInputStream;
import org.terifan.io.ByteBufferInputStream;
import org.terifan.util.Convert;
import org.terifan.util.Debug;
import org.terifan.util.log.Log;


public class BinaryDecoder
{
	private TreeMap<Integer,String> mKeys;
	private BitInputStream mInput;


	public BinaryDecoder()
	{
	}


	public Bundle unmarshal(byte[] aBuffer) throws IOException
	{
		if (aBuffer == null || aBuffer.length == 0)
		{
			return null;
		}

		return unmarshal(new ByteArrayInputStream(aBuffer));
	}


	public Bundle unmarshal(ByteBuffer aBuffer) throws IOException
	{
		return unmarshal(new ByteBufferInputStream(aBuffer));
	}


	public Bundle unmarshal(InputStream aInputStream) throws IOException
	{
		mKeys = new TreeMap<>();
		mInput = new BitInputStream(aInputStream);

		return readBundle(new Bundle());
	}


	private Bundle readBundle(Bundle aBundle) throws IOException
	{
		String[] keys = readBundleKeys();

		for (String key : keys)
		{
//			FieldType fieldType = FieldType.values()[(int)mInput.readBits(4)];
			FieldType fieldType = decodeFieldType();
			Object value;

			if (fieldType == FieldType.NULL)
			{
				value = null;
			}
			else if (fieldType == FieldType.EMPTY_LIST)
			{
				value = new ArrayList();
			}
			else if (mInput.readBit() == 0)
			{
				value = readValue(fieldType);
			}
			else if (mInput.readBit() == 0)
			{
				value = readList(fieldType);
			}
			else if (fieldType == FieldType.BYTE)
			{
				value = new byte[mInput.readVariableInt(3, 4, false)];
				mInput.align();
				mInput.read((byte[])value);
			}
			else
			{
				ArrayList list = readList(fieldType);
				value = Array.newInstance(fieldType.getPrimitiveType(), list.size());
				for (int i = 0; i < list.size(); i++)
				{
					Array.set(value, i, list.get(i));
				}
			}

			aBundle.put(key, value);
		}

		return aBundle;
	}


	private FieldType decodeFieldType() throws IOException
	{
		switch ((int)mInput.readBits(2))
		{
			case 0b00: return FieldType.DECODER_ORDER[0];
			case 0b01: return FieldType.DECODER_ORDER[1];
			case 0b10: return FieldType.DECODER_ORDER[2];
			default:
				switch ((int)mInput.readBits(3))
				{
					case 0b000: return FieldType.DECODER_ORDER[3];
					case 0b001: return FieldType.DECODER_ORDER[4];
					case 0b010: return FieldType.DECODER_ORDER[5];
					case 0b011: return FieldType.DECODER_ORDER[6];
					case 0b100: return FieldType.DECODER_ORDER[7];
					case 0b101: return FieldType.DECODER_ORDER[8];
					case 0b110: return FieldType.DECODER_ORDER[9];
					default:
						switch (mInput.readBit())
						{
							case 0b0: return FieldType.DECODER_ORDER[10];
							default:
								switch (mInput.readBit())
								{
									case 0b0: return FieldType.DECODER_ORDER[11];
									default: return FieldType.DECODER_ORDER[12];
								}
						}
				}
		}
	}


	private String[] readBundleKeys() throws IOException
	{
		int keyCount = mInput.readVariableInt(3, 0, false);

		String[] keys = new String[keyCount];
		ArrayList<int[]> newKeys = new ArrayList<>();

		for (int i = 0; i < keyCount; i++)
		{
			if (mInput.readBit() == 0)
			{
				keys[i] = mKeys.get((int)mInput.readBitsInRange(mKeys.size()));
			}
			else
			{
				newKeys.add(new int[]{i, (int)mInput.readVariableInt(3, 0, false)});
			}
		}

		if (newKeys.size() > 0)
		{
			mInput.align();

			for (int[] entry : newKeys)
			{
				mKeys.put(mKeys.size(), keys[entry[0]] = readString(entry[1]));
			}
		}

		return keys;
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
				return mInput.readVariableInt(3, 4, true);
			case LONG:
				return mInput.readVariableLong(7, 0, true);
			case FLOAT:
				return Float.intBitsToFloat(mInput.readVariableInt(7, 0, false));
			case DOUBLE:
				return Double.longBitsToDouble(mInput.readVariableLong(7, 0, false));
			case STRING:
				int len = mInput.readVariableInt(3, 4, false);
				mInput.align();
				return readString(len);
			case DATE:
				return new Date(mInput.readVariableLong(7, 8, false));
			case BUNDLE:
				return readBundle(new Bundle());
			default:
				throw new IOException("Unsupported field type: " + aFieldType);
		}
	}


	private String readString(int aLength) throws IOException
	{
		byte[] buf = new byte[aLength];

		if (mInput.read(buf) != buf.length)
		{
			throw new IOException("Unexpected end of stream");
		}

		return Convert.decodeUTF8(buf, 0, aLength);
	}
}

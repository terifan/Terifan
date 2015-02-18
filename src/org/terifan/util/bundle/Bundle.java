package org.terifan.util.bundle;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.terifan.util.Convert;
import org.terifan.util.bundle.BundleExternalizable.Bundlable;
import org.terifan.util.log.Log;


public final class Bundle implements Cloneable, Externalizable, Iterable<String>
{
	private final static long serialVersionUID = 1L;

	private final Map<String, Object> mValues = new TreeMap<>();
	private boolean mStrict;


	/**
	 * Create a new empty Bundle.
	 */
	public Bundle()
	{
	}


	/**
	 * Copy the provided Bundle into a new instance.
	 */
	public Bundle(Bundle aOther)
	{
		putAll(aOther);
	}


	public Bundle setStrict(boolean aStrict)
	{
		mStrict = aStrict;
		return this;
	}


	public boolean isStrict()
	{
		return mStrict;
	}


	/**
	 * Removes all elements from the mapping of this Bundle.
	 */
	public Bundle clear()
	{
		mValues.clear();
		return this;
	}


	/**
	 * Clones the current Bundle.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		try
		{
			Bundle b = (Bundle)super.clone();
			b.putAll(this);
			return b;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}


	/**
	 * Returns true if the given key is contained in the mapping of this Bundle.
	 */
	public boolean containsKey(String aKey)
	{
		return mValues.containsKey(aKey);
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the given key exists.
	 */
	public Object get(String aKey)
	{
		return mValues.get(aKey);
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public Bundle getBundle(String aKey)
	{
		return getBundle(aKey, null);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public Bundle getBundle(String aKey, Bundle aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			return ((Bundle)o).setStrict(mStrict);
		}
		catch (ClassCastException e)
		{
			return (Bundle)typeWarning(aKey, o, "Bundle", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public Bundle[] getBundleArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (Bundle[])o;
		}
		catch (ClassCastException e)
		{
			return (Bundle[])typeWarning(aKey, o, "Bundle[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Bundle> getBundleArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Bundle>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Bundle>)typeWarning(aKey, o, "ArrayList<Bundle>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or false if no mapping of the desired type exists for the given key.
	 */
	public boolean getBoolean(String aKey)
	{
		return getBoolean(aKey, false);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public boolean getBoolean(String aKey, boolean aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict || o instanceof Boolean)
			{
				return (Boolean)o;
			}
			return ((Number)o).longValue() != 0;
		}
		catch (ClassCastException e)
		{
			return (Boolean)typeWarning(aKey, o, "Boolean", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public boolean[] getBooleanArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (boolean[])o;
		}
		catch (ClassCastException e)
		{
			return (boolean[])typeWarning(aKey, o, "boolean[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Boolean> getBooleanArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Boolean>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Boolean>)typeWarning(aKey, o, "ArrayList<Boolean>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public byte getByte(String aKey)
	{
		return getByte(aKey, (byte)0);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public byte getByte(String aKey, byte aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Byte)o;
			}
			return ((Number)o).byteValue();
		}
		catch (ClassCastException e)
		{
			return (Byte)typeWarning(aKey, o, "Byte", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public byte[] getByteArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (byte[])o;
		}
		catch (ClassCastException e)
		{
			return (byte[])typeWarning(aKey, o, "byte[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Byte> getByteArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Byte>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Byte>)typeWarning(aKey, o, "ArrayList<Byte>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public char getChar(String aKey)
	{
		return getChar(aKey, (char)0);
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public char getChar(String aKey, char aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict || o instanceof Character)
			{
				return (Character)o;
			}
			return (char)((Number)o).shortValue();
		}
		catch (ClassCastException e)
		{
			return (Character)typeWarning(aKey, o, "Character", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public char[] getCharArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (char[])o;
		}
		catch (ClassCastException e)
		{
			return (char[])typeWarning(aKey, o, "char[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Character> getCharArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Character>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Character>)typeWarning(aKey, o, "ArrayList<Character>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0.0 if no mapping of the desired type exists for the given key.
	 */
	public double getDouble(String aKey)
	{
		return getDouble(aKey, 0.0);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public double getDouble(String aKey, double aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Double)o;
			}
			return ((Number)o).doubleValue();
		}
		catch (ClassCastException e)
		{
			return (Double)typeWarning(aKey, o, "Double", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public double[] getDoubleArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (double[])o;
		}
		catch (ClassCastException e)
		{
			return (double[])typeWarning(aKey, o, "double[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Double> getDoubleArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Double>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Double>)typeWarning(aKey, o, "ArrayList<Double>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0.0f if no mapping of the desired type exists for the given key.
	 */
	public float getFloat(String aKey)
	{
		return getFloat(aKey, 0f);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public float getFloat(String aKey, float aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Float)o;
			}
			return ((Number)o).floatValue();
		}
		catch (ClassCastException e)
		{
			return (Float)typeWarning(aKey, o, "Float", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public float[] getFloatArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (float[])o;
		}
		catch (ClassCastException e)
		{
			return (float[])typeWarning(aKey, o, "float[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Float> getFloatArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Float>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Float>)typeWarning(aKey, o, "ArrayList<Float>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public int getInt(String aKey, int aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Integer)o;
			}
			return ((Number)o).intValue();
		}
		catch (ClassCastException e)
		{
			return (Integer)typeWarning(aKey, o, "Integer", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public int getInt(String aKey)
	{
		return getInt(aKey, 0);
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public int[] getIntArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			if (!mStrict)
			{
				return Convert.toInts(o);
			}
			return (int[])o;
		}
		catch (ClassCastException e)
		{
			return (int[])typeWarning(aKey, o, "int[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Integer> getIntArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Integer>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Integer>)typeWarning(aKey, o, "ArrayList<Integer>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public long getLong(String aKey)
	{
		return getLong(aKey, 0L);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public long getLong(String aKey, long aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Long)o;
			}
			return ((Number)o).longValue();
		}
		catch (ClassCastException e)
		{
			return (Long)typeWarning(aKey, o, "Long", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public long[] getLongArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (long[])o;
		}
		catch (ClassCastException e)
		{
			return (long[])typeWarning(aKey, o, "long[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Long> getLongArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Long>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Long>)typeWarning(aKey, o, "ArrayList<Long>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
	 */
	public short getShort(String aKey)
	{
		return getShort(aKey, (short)0);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public short getShort(String aKey, short aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (mStrict)
			{
				return (Short)o;
			}
			return ((Number)o).shortValue();
		}
		catch (ClassCastException e)
		{
			return (Short)typeWarning(aKey, o, "Short", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public short[] getShortArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (short[])o;
		}
		catch (ClassCastException e)
		{
			return (short[])typeWarning(aKey, o, "short[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Short> getShortArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Short>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Short>)typeWarning(aKey, o, "ArrayList<Short>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public String getString(String aKey)
	{
		return getString(aKey, null);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public String getString(String aKey, String aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			return o.toString();
		}
		catch (ClassCastException e)
		{
			return (String)typeWarning(aKey, o, "String", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public String[] getStringArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (String[])o;
		}
		catch (ClassCastException e)
		{
			return (String[])typeWarning(aKey, o, "String[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<String> getStringArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<String>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<String>)typeWarning(aKey, o, "ArrayList<String>", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public Date getDate(String aKey)
	{
		return getDate(aKey, null);
	}


	/**
	 * Returns the value associated with the given key, or aDefaultValue if no mapping of the desired type exists for the given key.
	 */
	public Date getDate(String aKey, Date aDefaultValue)
	{
		Object o = mValues.get(aKey);
		if (o == null)
		{
			return aDefaultValue;
		}
		try
		{
			if (!mStrict && !(o instanceof Date))
			{
				if (o instanceof Long)
				{
					return new Date((Long)o);
				}
				if (o instanceof String)
				{
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse((String)o);
				}
			}
			return (Date)o;
		}
		catch (ClassCastException | ParseException e)
		{
			return (Date)typeWarning(aKey, o, "Date", aDefaultValue, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public Date[] getDateArray(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (Date[])o;
		}
		catch (ClassCastException e)
		{
			return (Date[])typeWarning(aKey, o, "Date[]", null, e);
		}
	}


	/**
	 * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key or a null value
	 * is explicitly associated with the key.
	 */
	public ArrayList<Date> getDateArrayList(String aKey)
	{
		Object o = mValues.get(aKey);
		try
		{
			return (ArrayList<Date>)o;
		}
		catch (ClassCastException e)
		{
			return (ArrayList<Date>)typeWarning(aKey, o, "ArrayList<Date>", null, e);
		}
	}


	/**
	 * Returns true if the mapping of this Bundle is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return mValues.isEmpty();
	}


	/**
	 * Returns true if the mapping of this key is null, false otherwise.
	 */
	public boolean isNull(String aKey)
	{
		return mValues.get(aKey) == null;
	}


	/**
	 * Returns a Set containing the Strings used as keys in this Bundle.
	 */
	public Set<String> keySet()
	{
		return mValues.keySet();
	}


	/**
	 * Inserts all mappings from the given Bundle into this Bundle.
	 */
	public Bundle putAll(Bundle aOther)
	{
		for (String key : aOther)
		{
			put(key, aOther.mValues.get(key));
		}
		return this;
	}


	/**
	 * Inserts a Bundle value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putBundle(String aKey, Bundle aValue)
	{
		put(aKey, aValue);
		return this;
	}


	public Bundle putBundleArray(String aKey, Bundle... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	public Bundle putBundleArrayList(String aKey, ArrayList<Bundle> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a Boolean value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putBoolean(String aKey, boolean aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a boolean array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putBooleanArray(String aKey, boolean... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a boolean array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putBooleanArrayList(String aKey, ArrayList<Boolean> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a byte value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putByte(String aKey, int aValue)
	{
		return putByte(aKey, (byte)aValue);
	}


	/**
	 * Inserts a byte value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putByte(String aKey, byte aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a byte array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putByteArray(String aKey, byte... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a byte array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putByteArrayList(String aKey, ArrayList<Byte> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a char value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putChar(String aKey, char aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a char array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putCharArray(String aKey, char... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a char array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putCharArrayList(String aKey, ArrayList<Character> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a double value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDouble(String aKey, double aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a double array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDoubleArray(String aKey, double... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a double list value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDoubleArrayList(String aKey, ArrayList<Double> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a float value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putFloat(String aKey, float aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a float array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putFloatArray(String aKey, float... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a float array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putFloatArrayList(String aKey, ArrayList<Float> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts an int value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putInt(String aKey, int aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts an int array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putIntArray(String aKey, int... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts an ArrayList value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putIntArrayList(String aKey, ArrayList<Integer> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a long value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putLong(String aKey, long aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a long array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putLongArray(String aKey, long... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a long list value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putLongArrayList(String aKey, ArrayList<Long> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a short value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putShort(String aKey, int aValue)
	{
		return putShort(aKey, (short)aValue);
	}


	/**
	 * Inserts a short value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putShort(String aKey, short aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a short array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putShortArray(String aKey, short... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a short list value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putShortArrayList(String aKey, ArrayList<Short> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a String value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putString(String aKey, String aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a String array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putStringArray(String aKey, String... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts an ArrayList value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putStringArrayList(String aKey, ArrayList<String> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a Date value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDate(String aKey, Date aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts a Date array value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDateArray(String aKey, Date... aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Inserts an ArrayList value into the mapping of this Bundle, replacing any existing value for the given key.
	 */
	public Bundle putDateArrayList(String aKey, ArrayList<Date> aValue)
	{
		put(aKey, aValue);
		return this;
	}


	/**
	 * Removes any entry with the given key from the mapping of this Bundle.
	 */
	public Bundle remove(String aKey)
	{
		mValues.remove(aKey);
		return this;
	}


	/**
	 * Returns the number of mappings contained in this Bundle.
	 */
	public int size()
	{
		return mValues.size();
	}


	/**
	 * Returns a description of this bundle.
	 */
	@Override
	public String toString()
	{
		return mValues.toString();
	}


	/**
	 * Returns true if the provided object is a Bundle with the same keys and values as this.
	 */
	@Override
	public boolean equals(Object aOther)
	{
		if (aOther == this)
		{
			return true;
		}
		if (aOther instanceof Bundle)
		{
			Bundle o = (Bundle)aOther;

			for (String key : keySet())
			{
				Object tv = mValues.get(key);
				Object ov = o.mValues.get(key);

				if (tv != ov)
				{
//					if (tv == null && List.class.isAssignableFrom(ov.getClass()) && ((List)ov).isEmpty())
//					{
//						continue;
//					}
//					if (ov == null && List.class.isAssignableFrom(tv.getClass()) && ((List)tv).isEmpty())
//					{
//						continue;
//					}
					if (tv == null || ov == null)
					{
						return false;
					}

					if (tv.getClass().isArray())
					{
						int tl = Array.getLength(tv);
						int ol = Array.getLength(ov);

						if (tl != ol)
						{
							return false;
						}
						for (int i = 0; i < tl; i++)
						{
							Object tav = Array.get(tv, i);
							Object oav = Array.get(ov, i);
							if (tav != oav && (tav == null || !tav.equals(oav)))
							{
								return false;
							}
						}
					}
					else if (!tv.equals(ov))
					{
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}


	/**
	 * Returns a hashcode of this Bundle.
	 */
	@Override
	public int hashCode()
	{
		return mValues.hashCode();
	}


	@Override
	public Iterator<String> iterator()
	{
		return keySet().iterator();
	}


	public void put(String aKey, Object aValue)
	{
		if (aKey == null)
		{
			throw new IllegalArgumentException("Provided key is null.");
		}

		mValues.put(aKey, aValue);
	}


	/**
	 * Override this method to handle type casting errors.
	 *
	 * @return
	 *    default implementation return the default value provided
	 */
	protected Object typeWarning(String aKey, Object aValue, String aClassName, Object aDefaultValue, Exception aException)
	{
		Log.out.println("Attempt to cast generated internal exception: Key '" + aKey+ "' expected "+aClassName+" but value was a "+aValue.getClass().getSimpleName()+". The default value '"+aDefaultValue+"' was returned.");

		return aDefaultValue;
	}


	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int size = in.readInt();
		for (int i = 0; i < size; i++)
		{
			put(in.readUTF(), in.readObject());
		}
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(size());
		for (String key : this)
		{
			out.writeUTF(key);
			out.writeObject(mValues.get(key));
		}
	}


	public Bundle getObject(BundleExternalizable aObject) throws IOException
	{
		bundleToObject(aObject);
		aObject.readExternal(this);
		return this;
	}


	public <T extends BundleExternalizable> T getObject(Class<T> aType, String aName) throws IOException
	{
		try
		{
			T instance = aType.newInstance();
			Bundle bundle = getBundle(aName);
			if (bundle == null)
			{
				throw new IllegalArgumentException("Bundle not found: name: " + aName);
			}
			bundle.bundleToObject(instance);
			instance.readExternal(bundle);
			return instance;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IOException(e);
		}
	}


	public <T> T[] getObjectArray(Class aType, String aName) throws IOException
	{
		try
		{
			ArrayList<Bundle> bundleArray = getBundleArrayList(aName);
			if (bundleArray == null)
			{
				return null;
			}
			Object array = Array.newInstance(aType, bundleArray.size());
			for (int i = 0; i < bundleArray.size(); i++)
			{
				BundleExternalizable instance = (BundleExternalizable)aType.newInstance();
				Bundle bundle = bundleArray.get(i);
				bundle.bundleToObject(instance);
				instance.readExternal(bundle);
				Array.set(array, i, instance);
			}
			return (T[])array;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IOException(e);
		}
	}


	public <T extends BundleExternalizable> ArrayList<T> getObjectArrayList(Class<T> aType, String aName) throws IOException
	{
		ArrayList<Bundle> bundleArrayList = getBundleArrayList(aName);

		if (bundleArrayList == null)
		{
			return null;
		}

		try
		{
			ArrayList<T> list = new ArrayList<>();
			for (Bundle bundle : bundleArrayList)
			{
				T instance = aType.newInstance();
				bundle.bundleToObject(instance);
				instance.readExternal(bundle);
				list.add(instance);
			}
			return list;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IOException(e);
		}
	}


	public Bundle putObject(BundleExternalizable aObject) throws IOException
	{
		objectToBundle(aObject);
		aObject.writeExternal(this);
		return this;
	}


	public Bundle putObject(String aName, BundleExternalizable aObject) throws IOException
	{
		Bundle bundle = new Bundle();
		bundle.objectToBundle(aObject);
		aObject.writeExternal(bundle);
		putBundle(aName, bundle);
		return this;
	}


	public Bundle putObjectArray(String aName, BundleExternalizable[] aArray) throws IOException
	{
		if (aArray != null)
		{
			Bundle[] array = new Bundle[aArray.length];
			for (int i = 0; i < array.length; i++)
			{
				Bundle bundle = new Bundle();
				array[i] = bundle;

				BundleExternalizable item = aArray[i];

				if (item != null)
				{
					bundle.objectToBundle(item);
					item.writeExternal(bundle);
				}
			}
			putBundleArray(aName, array);
		}
		return this;
	}


	public Bundle putObjectArrayList(String aName, ArrayList<? extends BundleExternalizable> aList) throws IOException
	{
		ArrayList<Bundle> list = new ArrayList<>(aList.size());
		for (BundleExternalizable entry : aList)
		{
			Bundle bundle = new Bundle();
			list.add(bundle);
			bundle.objectToBundle(entry);
			entry.writeExternal(bundle);
		}
		putBundleArrayList(aName, list);
		return this;
	}


	private void bundleToObject(BundleExternalizable aObject)
	{
		for (Field field : aObject.getClass().getDeclaredFields())
		{
			try
			{
				field.setAccessible(true);
				if (field.getAnnotation(Bundlable.class) != null)
				{
					updateField(field, aObject);
				}
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("Error updating field: " + field, e);
			}
		}
	}


	private void objectToBundle(BundleExternalizable aObject) throws IOException
	{
		try
		{
			for (Field field : aObject.getClass().getDeclaredFields())
			{
				field.setAccessible(true);
				if (field.getAnnotation(Bundlable.class) != null)
				{
					putField(field, aObject);
				}
			}
		}
		catch (SecurityException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new IllegalArgumentException(e);
		}
	}


	private void updateField(Field aField, BundleExternalizable aObject) throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException
	{
		String fieldName = aField.getName();
		Object fieldValue = get(fieldName);
		Class fieldType = aField.getType();

		if (fieldValue == null)
		{
			if (containsKey(fieldName) && !fieldType.isPrimitive())
			{
				aField.set(aObject, fieldValue);
			}
		}
		else if (fieldValue instanceof Bundle)
		{
			BundleExternalizable instance = (BundleExternalizable)aField.get(aObject);
			if (instance == null)
			{
				instance = (BundleExternalizable)fieldType.newInstance();
			}
			Bundle bundle = (Bundle)fieldValue;
			instance.readExternal(bundle);
			bundle.bundleToObject(instance);
			aField.set(aObject, instance);
		}
		else if (fieldType.isArray())
		{
			aField.set(aObject, Convert.asArray(fieldType.getComponentType(), fieldValue));
		}
		else if (List.class.isAssignableFrom(fieldType))
		{
			aField.set(aObject, Convert.asList(getParameterizedType(aField, 0), fieldValue));
		}
		else if (aField.getType() == Integer.class || aField.getType() == Integer.TYPE)
		{
			aField.set(aObject, ((Number)fieldValue).intValue());
		}
		else
		{
			aField.set(aObject, fieldValue);
		}
	}


	private void putField(Field aField, Object aObject) throws IOException, ClassNotFoundException, IllegalAccessException
	{
		Object value = aField.get(aObject);
		Class fieldType = aField.getType();
		String name = aField.getName();

		if (isSupportedType(fieldType) 
			|| fieldType.isArray() && isSupportedType(fieldType.getComponentType()) 
			|| ArrayList.class.isAssignableFrom(fieldType) && isSupportedType(getParameterizedType(aField, 0)))
		{
			put(name, value);
		}
		else if (BundleExternalizable.class.isAssignableFrom(fieldType))
		{
			BundleExternalizable externalizable = (BundleExternalizable)value;
			externalizable.writeExternal(this);
			putObject(name, externalizable);
		}
		else if (fieldType.isArray() && BundleExternalizable.class.isAssignableFrom(fieldType.getComponentType()))
		{
			putObjectArray(name, (BundleExternalizable[])value);
		}
		else if (ArrayList.class.isAssignableFrom(fieldType) && BundleExternalizable.class.isAssignableFrom(getParameterizedType(aField, 0)))
		{
			putObjectArrayList(name, (ArrayList<BundleExternalizable>)value);
		}
		else
		{
			throw new IllegalArgumentException("Unsupported type: " + aField);
		}
	}


	private static boolean isSupportedType(Class aFieldType)
	{
		return aFieldType == Boolean.class || aFieldType == Boolean.TYPE || aFieldType == Short.class || aFieldType == Short.TYPE || aFieldType == Character.class || aFieldType == Character.TYPE || aFieldType == Integer.class || aFieldType == Integer.TYPE || aFieldType == Long.class || aFieldType == Long.TYPE || aFieldType == Float.class || aFieldType == Float.TYPE || aFieldType == Double.class || aFieldType == Double.TYPE || aFieldType == String.class || aFieldType == Date.class || aFieldType == Bundle.class;
	}


	private Class getParameterizedType(Field aField, int aIndex) throws ClassNotFoundException
	{
		ParameterizedType paramType = (ParameterizedType)aField.getGenericType();
		Class valueType = Class.forName(paramType.getActualTypeArguments()[aIndex].getTypeName());
		return valueType;
	}
}
package org.terifan.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.LongPredicate;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;


public final class LongArray implements Cloneable, Iterable<Long>
{
	private final static int GROWTH = 1000;

	private long[] mValues;
	private int mSize;


	public LongArray()
	{
		mValues = new long[0];
	}


	public LongArray(long[] aValues)
	{
		this(aValues == null ? new long[0] : aValues, aValues == null ? 0 : aValues.length);
	}


	public LongArray(long[] aValues, int aSize)
	{
		mValues = aValues;
		mSize = aSize;
	}
	
	
	public int indexOf(long aValue)
	{
		for (int i = 0; i < mSize; i++)
		{
			if (mValues[i] == aValue)
			{
				return i;
			}
		}

		return -1;
	}


	public LongArray removeValue(long aValue)
	{
		for (int dst = 0, src = 0; dst < mSize; )
		{
			if (mValues[src] == aValue)
			{
				src++;
				mSize--;
			}
			else
			{
				mValues[dst++] = mValues[src++];
			}
		}
		
		return this;
	}


	public LongArray removeIf(LongPredicate aPredicate)
	{
		for (int dst = 0, src = 0; dst < mSize; )
		{
			if (aPredicate.test(mValues[src]))
			{
				src++;
				mSize--;
			}
			else
			{
				mValues[dst++] = mValues[src++];
			}
		}
		
		return this;
	}
	
	
	public LongArray remove(int aIndex)
	{
		if (aIndex < mSize - 1)
		{
			System.arraycopy(mValues, aIndex + 1, mValues, aIndex, mSize - aIndex - 1);
		}

		mSize--;

		return this;
	}

	
	public long[] array()
	{
		return mValues;
	}


	public long get(int aIndex)
	{
		return mValues[aIndex];
	}
	
	
	public LongArray add(long aValue)
	{
		if (mSize == mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + GROWTH);
		}

		mValues[mSize++] = aValue;

		return this;
	}
	
	
	public LongArray add(long... aValues)
	{
		add(aValues, 0, aValues.length);
		return this;
	}
	
	
	public LongArray add(long[] aValues, int aOffset, int aLength)
	{
		if (mSize + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + aLength + GROWTH);
		}

		System.arraycopy(aValues, aOffset, mValues, mSize, aLength);
		
		mSize += aLength;

		return this;
	}
	
	
	public <T> LongArray addValues(T[] aArray, ToLongFunction<T> aFunction)
	{
		for (int i = 0; i < aArray.length; i++)
		{
			add(aFunction.applyAsLong(aArray[i]));
		}
		return this;
	}
	
	
	public LongArray set(int aIndex, long aValue)
	{
		if (aIndex >= mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, aIndex + GROWTH);
		}

		mValues[aIndex] = aValue;
		
		if (aIndex >= mSize)
		{
			mSize = aIndex + 1;
		}
		
		return this;
	}


	public LongArray set(int aIndex, long... aValues)
	{
		set(aIndex, aValues, 0, aValues.length);
		return this;
	}


	public LongArray set(int aIndex, long[] aValues, int aOffset, int aLength)
	{
		if (aIndex + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, aIndex + aLength);
			mSize = aIndex + aLength;
			System.arraycopy(aValues, aOffset, mValues, aIndex, aLength);
		}
		else
		{
			System.arraycopy(aValues, aOffset, mValues, aIndex, aLength);
			mSize = Math.max(mSize, aIndex + aLength);
		}

		return this;
	}
	
	
	public LongArray clear()
	{
		mSize = 0;
		mValues = new long[0];
		return this;
	}
	
	
	public LongArray trim()
	{
		mValues = Arrays.copyOfRange(mValues, 0, mSize);
		return this;
	}
	

	public int size()
	{
		return mSize;
	}
	
	
	public boolean isEmpty()
	{
		return mSize == 0;
	}


	@Override
	public Iterator<Long> iterator()
	{
		return new Iterator<Long>()
		{
			int mIndex = 0;

			@Override
			public boolean hasNext()
			{
				return mIndex < mSize;
			}

			@Override
			public Long next()
			{
				return mValues[mIndex++];
			}
		};
	}
	
	
	@Override
	public LongArray clone()
	{
		try
		{
			return (LongArray)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return new LongArray().add(mValues, 0, mSize);
		}
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mSize; i++)
		{
			if (i > 0)
			{
				sb.append(",");
			}
			sb.append(mValues[i]);
		}
		return sb.toString();
	}


	@Override
	public int hashCode()
	{
        long result = 1;
        for (long element : mValues)
		{
            result = 31 * result + element;
		}

        return (int)result;
	}


	@Override
	public boolean equals(Object aOther)
	{
        if (this == aOther)
		{
            return true;
		}
		if (!(aOther instanceof LongArray))
		{
			return false;
		}

		LongArray other = (LongArray)aOther;

        if (mSize != other.mSize)
		{
            return false;
		}

		long[] a = mValues;
		long[] b = other.mValues;

		for (int i = 0; i < mSize; i++)
		{
            if (a[i] != b[i])
			{
                return false;
			}
		}

        return true;
	}
	

	public boolean contains(long aValue)
	{
		return indexOf(aValue) != -1;
	}
	
	
	public LongStream stream()
	{
		return Arrays.stream(mValues, 0, mSize);
	}


	public <T> T find(Visitor<T> aConsumer)
	{
		for (int i = 0; i < mSize; i++)
		{
			T value = aConsumer.visit(mValues[i]);
			if (value != null)
			{
				return value;
			}			
		}

		return null;
	}
	
	
	@FunctionalInterface
	public interface Visitor<T>
	{
		T visit(long aValue);
	}
}

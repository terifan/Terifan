package org.terifan.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;


public final class IntArray implements Cloneable, Iterable<Integer>
{
	private final static int GROWTH = 1000;

	private int[] mValues;
	private int mSize;


	public IntArray()
	{
		mValues = new int[0];
	}


	public IntArray(int[] aValues)
	{
		this(aValues, aValues.length);
	}


	public IntArray(int[] aValues, int aSize)
	{
		mValues = aValues;
		mSize = aSize;
	}


	public int indexOf(int aValue)
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


	public IntArray removeValue(int aValue)
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


	public IntArray removeIf(IntPredicate aPredicate)
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


	public IntArray remove(int aIndex)
	{
		if (aIndex < mSize - 1)
		{
			System.arraycopy(mValues, aIndex + 1, mValues, aIndex, mSize - aIndex - 1);
		}

		mSize--;

		return this;
	}


	public int[] array()
	{
		return mValues;
	}


	public int get(int aIndex)
	{
		return mValues[aIndex];
	}


	public IntArray add(int aValue)
	{
		if (mSize == mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + GROWTH);
		}

		mValues[mSize++] = aValue;

		return this;
	}


	public IntArray add(int... aValues)
	{
		add(aValues, 0, aValues.length);
		return this;
	}


	public IntArray add(int[] aValues, int aOffset, int aLength)
	{
		if (mSize + aLength > mValues.length)
		{
			mValues = Arrays.copyOfRange(mValues, 0, mSize + aLength + GROWTH);
		}

		System.arraycopy(aValues, aOffset, mValues, mSize, aLength);

		mSize += aLength;

		return this;
	}


	public <T> IntArray addValues(T[] aArray, ToIntFunction<T> aFunction)
	{
		for (int i = 0; i < aArray.length; i++)
		{
			add(aFunction.applyAsInt(aArray[i]));
		}
		return this;
	}


	public IntArray set(int aIndex, int aValue)
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


	public IntArray set(int aIndex, int... aValues)
	{
		set(aIndex, aValues, 0, aValues.length);
		return this;
	}


	public IntArray set(int aIndex, int[] aValues, int aOffset, int aLength)
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


	public IntArray clear()
	{
		mSize = 0;
		mValues = new int[0];
		return this;
	}


	public IntArray trimToSize()
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
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			int mIndex = 0;

			@Override
			public boolean hasNext()
			{
				return mIndex < mSize;
			}

			@Override
			public Integer next()
			{
				return mValues[mIndex++];
			}
		};
	}


	@Override
	public IntArray clone()
	{
		try
		{
			return (IntArray)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return new IntArray().add(mValues, 0, mSize);
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
        int result = 1;
        for (int element : mValues)
		{
            result = 31 * result + element;
		}

        return result;
	}


	@Override
	public boolean equals(Object aOther)
	{
        if (this == aOther)
		{
            return true;
		}
		if (!(aOther instanceof IntArray))
		{
			return false;
		}

		IntArray other = (IntArray)aOther;

        if (mSize != other.mSize)
		{
            return false;
		}

		int[] a = mValues;
		int[] b = other.mValues;

		for (int i = 0; i < mSize; i++)
		{
            if (a[i] != b[i])
			{
                return false;
			}
		}

        return true;
	}


	public boolean contains(int aValue)
	{
		return indexOf(aValue) != -1;
	}


	public IntStream stream()
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
		T visit(int aValue);
	}
}

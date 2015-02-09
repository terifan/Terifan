package org.terifan.util;

import java.io.Serializable;


/**
 * The Tuple class is a union of tw values into a single Object. Useful for
 * keys in HashMaps where two distinct values make out the key.
 *
 * @param <T1>
 *   type of first value
 * @param <T2>
 *   type of second value
 */
public class Tuple<T1, T2> implements Serializable, Comparable<Tuple<T1, T2>>
{
	private T1 mFirst;
	private T2 mSecond;


	/**
	 * Creates a Tuple instance.
	 *
	 * @param aFirst
	 *   the first value.
	 * @param aSecond
	 *   the second value.
	 */
	public Tuple(T1 aFirst, T2 aSecond)
	{
		setFirst(aFirst);
		setSecond(aSecond);
	}


	/**
	 * Return a hashcode of this Tuple.
	 * @return
	 *   a hash code
	 */
	@Override
	public int hashCode()
	{
		return getFirst().hashCode() ^ getSecond().hashCode();
	}


	/**
	 * Compares this Tuple with the Tuple supplied.
	 *
	 * @param aObject
	 *   a Tuple object
	 * @return
	 *   true if both Tuples contain values that are equal.
	 */
	@Override
	public boolean equals(Object aObject)
	{
		if (aObject instanceof Tuple)
		{
			Tuple<T1, T2> t = (Tuple<T1, T2>)aObject;

			T1 f1 = getFirst();
			T1 f2 = t.getFirst();

			if (f1 == null && f2 == null)
			{
			}
			else if (f1 == null && f2 != null || f1 != null && f2 == null || !f1.equals(f2))
			{
				return false;
			}

			T2 s1 = getSecond();
			T2 s2 = t.getSecond();

			if (s1 == null && s2 == null)
			{
			}
			else if (s1 == null && s2 != null || s1 != null && s2 == null || !s1.equals(s2))
			{
				return false;
			}

			return true;
		}

		return false;
	}


	/**
	 * Gets the first value of this Tuple.
	 * @return
	 *   the value
	 */
	public T1 getFirst()
	{
		return mFirst;
	}


	/**
	 * Sets the first value of this Tuple.
	 * @param aFirst
	 *   the value
	 */
	public void setFirst(T1 aFirst)
	{
		mFirst = aFirst;
	}


	/**
	 * Gets the second value of this Tuple.
	 * @return
	 *   the value
	 */
	public T2 getSecond()
	{
		return mSecond;
	}


	/**
	 * Sets the second value of this Tuple.
	 * @param aSecond
	 *   the value
	 */
	public void setSecond(T2 aSecond)
	{
		mSecond = aSecond;
	}


	/**
	 * Returns a descriptive text of this Tuple.
	 * @return
	 */
	@Override
	public String toString()
	{
		return "Tuple{first=" + getFirst() + ", second=" + getSecond() + "}";
	}


	/**
	 * Compares this Tuple with the Tuplie supplied.<p>
	 *
	 * Note: The second value is compared only if the first values are equal.<p>
	 *
	 * Note: Values must implement the Comparable interface for this method to
	 * be useful.
	 * 
	 * @param aTuple
	 *   the Tuple to compare against.
	 * @return
	 *   Returns a negative number if this Tuple is "less than" the supplied
	 *   Tuple. 0 if they are equal and a positive number if this Tuple is
	 *   "greater than" the supplied Tuple.
	 */
	@Override
	public int compareTo(Tuple<T1, T2> aTuple)
	{
		int r;

		if (getFirst() == null && aTuple.getFirst() == null)
		{
			r = 0;
		}
		else if (getFirst() == null && aTuple.getFirst() != null)
		{
			r = -1;
		}
		else if (getFirst() != null && aTuple.getFirst() == null)
		{
			r = 1;
		}
		else
		{
			r = ((Comparable<T1>)getFirst()).compareTo(aTuple.getFirst());
		}

		if (r == 0)
		{
			if (getSecond() == null && aTuple.getSecond() == null)
			{
				r = 0;
			}
			else if (getSecond() == null && aTuple.getSecond() != null)
			{
				r = -1;
			}
			else if (getSecond() != null && aTuple.getSecond() == null)
			{
				r = 1;
			}
			else
			{
				r = ((Comparable<T2>)getSecond()).compareTo(aTuple.getSecond());
			}
		}

		return r;
	}
}
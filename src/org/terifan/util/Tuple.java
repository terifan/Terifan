package org.terifan.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;


/**
 * The Tuple class is a union of tw values into a single Object. Useful for
 * keys in HashMaps where two distinct values make out the key.
 *
 * @param <T>
 *   type of first value
 * @param <U>
 *   type of second value
 */
public class Tuple<T, U> implements Serializable, Comparable<Tuple<T, U>>
{
	private final static long serialVersionUID = 1L;

	private final Comparator<T> mTComparator = (T aO1, T aO2) -> ((Comparable<T>)aO1).compareTo(aO2);
	private final Comparator<U> mUComparator = (U aO1, U aO2) -> ((Comparable<U>)aO1).compareTo(aO2);

	public T first;
	public U second;


	/**
	 * Creates a Tuple instance.
	 *
	 * @param aFirst
	 *   the first value.
	 * @param aSecond
	 *   the second value.
	 */
	public Tuple(T aFirst, U aSecond)
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
		return Objects.hashCode(first) ^ Objects.hashCode(second);
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
		if (aObject == this)
		{
			return true;
		}

		if (aObject instanceof Tuple)
		{
			Tuple<T, U> t = (Tuple<T, U>)aObject;

			T f1 = first;
			T f2 = t.first;
			U s1 = second;
			U s2 = t.second;

			if (f1 == f2 && s1 == s2)
			{
				return true;
			}

			return f1 != null && s1 != null && f1.equals(f2) && s1.equals(s2);
		}

		return false;
	}


	/**
	 * Gets the first value of this Tuple.
	 * @return
	 *   the value
	 */
	public T getFirst()
	{
		return first;
	}


	/**
	 * Sets the first value of this Tuple.
	 * @param aFirst
	 *   the value
	 */
	public Tuple<T, U> setFirst(T aFirst)
	{
		first = aFirst;
		return this;
	}


	/**
	 * Gets the second value of this Tuple.
	 * @return
	 *   the value
	 */
	public U getSecond()
	{
		return second;
	}


	/**
	 * Sets the second value of this Tuple.
	 * @param aSecond
	 *   the value
	 */
	public Tuple<T, U> setSecond(U aSecond)
	{
		second = aSecond;
		return this;
	}


	/**
	 * Returns a descriptive text of this Tuple.
	 * @return
	 */
	@Override
	public String toString()
	{
		return "{" + getFirst() + ", " + getSecond() + "}";
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
	public int compareTo(Tuple<T, U> aTuple)
	{
		int i = Objects.compare(first, aTuple.first, mTComparator);

		if (i == 0)
		{
			return Objects.compare(second, aTuple.second, mUComparator);
		}

		return i;

//		int r;
//
//		if (getFirst() == null && aTuple.getFirst() == null)
//		{
//			r = 0;
//		}
//		else if (getFirst() == null && aTuple.getFirst() != null)
//		{
//			r = -1;
//		}
//		else if (getFirst() != null && aTuple.getFirst() == null)
//		{
//			r = 1;
//		}
//		else
//		{
//			r = ((Comparable<T>)getFirst()).compareTo(aTuple.getFirst());
//		}
//
//		if (r == 0)
//		{
//			if (getSecond() == null && aTuple.getSecond() == null)
//			{
//				r = 0;
//			}
//			else if (getSecond() == null && aTuple.getSecond() != null)
//			{
//				r = -1;
//			}
//			else if (getSecond() != null && aTuple.getSecond() == null)
//			{
//				r = 1;
//			}
//			else
//			{
//				r = ((Comparable<U>)getSecond()).compareTo(aTuple.getSecond());
//			}
//		}
//
//		return r;
	}
}
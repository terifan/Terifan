package org.terifan.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.function.BiFunction;


/**
 * This class store data in a two dimensional grid.
 *
 * @param <R> row key type
 * @param <C> column key type
 * @param <V> value type
 */
public class DataTable<R, C, V>
{
	private TreeSet<R> mRows;
	private TreeSet<C> mColumns;
	private HashMap<Tuple<R, C>, V> mValues;


	public DataTable()
	{
		mRows = new TreeSet<>();
		mColumns = new TreeSet<>();
		mValues = new HashMap<>();
	}


	public V get(R aRow, C aColumn)
	{
		return mValues.get(new Tuple<>(aRow, aColumn));
	}


	public void put(R aRow, C aColumn, V aValue)
	{
		mRows.add(aRow);
		mColumns.add(aColumn);
		mValues.put(new Tuple<>(aRow, aColumn), aValue);
	}


	public void put(R aRow, C aColumn, V aValue, BiFunction<V, V, V> aCombiner)
	{
		mRows.add(aRow);
		mColumns.add(aColumn);
		Tuple<R, C> key = new Tuple<>(aRow, aColumn);
		if (mValues.containsKey(key))
		{
			aValue = aCombiner.apply(mValues.get(key), aValue);
		}
		mValues.put(key, aValue);
	}


	public boolean contains(R aRow, C aColumn)
	{
		return mValues.containsKey(new Tuple<>(aRow, aColumn));
	}


	public TreeSet<R> getRows()
	{
		return mRows;
	}


	public TreeSet<C> getColumns()
	{
		return mColumns;
	}


	public V getMaximumValue(Comparator<V> aComparator)
	{
		V max = null;

		for (Entry<Tuple<R, C>, V> key : mValues.entrySet())
		{
			V v = key.getValue();
			if (max == null || aComparator.compare(max, v) > 0)
			{
				max = v;
			}
		}

		return max;
	}


	public V getMaximumRowValue(Comparator<V> aComparator, BiFunction<V, V, V> aCombiner)
	{
		V max = null;

		for (R row : mRows)
		{
			V maxRow = null;
			for (Entry<Tuple<R, C>, V> key : mValues.entrySet())
			{
				if (key.getKey().first.equals(row))
				{
					V v = key.getValue();
					if (maxRow == null)
					{
						maxRow = v;
					}
					else
					{
						maxRow = aCombiner.apply(maxRow, v);
					}
				}
			}
			if (max == null || aComparator.compare(maxRow, max) > 0)
			{
				max = maxRow;
			}
		}

		return max;
	}


	public V getMaximumColumnValue(Comparator<V> aComparator, BiFunction<V, V, V> aCombiner)
	{
		V max = null;

		for (C col : mColumns)
		{
			V maxCol = null;
			for (Entry<Tuple<R, C>, V> key : mValues.entrySet())
			{
				if (key.getKey().second.equals(col))
				{
					V v = key.getValue();
					if (maxCol == null)
					{
						maxCol = v;
					}
					else
					{
						maxCol = aCombiner.apply(maxCol, v);
					}
				}
			}
			if (max == null || aComparator.compare(maxCol, max) > 0)
			{
				max = maxCol;
			}
		}

		return max;
	}


	public V getSumRowValue(R aRow, BiFunction<V, V, V> aCombiner)
	{
		V sum = null;

		if (mRows.contains(aRow))
		{
			for (Entry<Tuple<R, C>, V> key : mValues.entrySet())
			{
				if (key.getKey().first.equals(aRow))
				{
					V v = key.getValue();
					if (sum == null)
					{
						sum = v;
					}
					else
					{
						sum = aCombiner.apply(sum, v);
					}
				}
			}
		}

		return sum;
	}


	public V getSumColumnValue(C aColumn, BiFunction<V, V, V> aCombiner)
	{
		V sum = null;

		if (mColumns.contains(aColumn))
		{
			for (Entry<Tuple<R, C>, V> key : mValues.entrySet())
			{
				if (key.getKey().second.equals(aColumn))
				{
					V v = key.getValue();
					if (sum == null)
					{
						sum = v;
					}
					else
					{
						sum = aCombiner.apply(sum, v);
					}
				}
			}
		}

		return sum;
	}


	public static void main(String... args)
	{
		try
		{
			DataTable<Integer, String, Integer> map = new DataTable<>();
			map.put(0, "a", 1);
			map.put(1, "b", 4);
			map.put(0, "c", 8);
			map.put(0, "d", 8);
			map.put(1, "d", 2);
			map.put(2, "c", 3);

			System.out.printf("%5s", "-");
			for (String c : map.getColumns())
			{
				System.out.printf("%5s", c);
			}
			System.out.printf("%5s", "sum");
			System.out.println();
			for (int r : map.getRows())
			{
				System.out.printf("%5d", r);
				for (String c : map.getColumns())
				{
					System.out.printf("%5s", map.get(r, c));
				}
				System.out.printf("%5s", map.getSumRowValue(r, (v0, v1) -> v0 + v1));
				System.out.println();
			}
			System.out.printf("%5s", "sum");
			for (String c : map.getColumns())
			{
				System.out.printf("%5s", map.getSumColumnValue(c, (v0, v1) -> v0 + v1));
			}
			System.out.println();
			System.out.println("max row: " + map.getMaximumRowValue(Integer::compare, (v0, v1) -> v0 + v1));
			System.out.println("max col: " + map.getMaximumColumnValue(Integer::compare, (v0, v1) -> v0 + v1));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

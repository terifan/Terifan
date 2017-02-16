package org.terifan.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.terifan.util.Calendar;
import org.terifan.util.log.Log;


/**
 * <p>A query utility enabling named query parameters to be be used. Named query parameters are tokens of the form :name in the query string.
 * A value is bound to the integer parameter :foo by calling <pre>setParameter("foo", foo);</pre> for example. A name may appear multiple
 * times in the query string.</p>
 * <p/>
 * Sample fetching a Query result to a list:
 * <code>Query query = EntityManager.createQuery("select * from table where id between :first and :last")
 *    .setParameter("first", 1)
 *    .setParameter("last", 7);
 *
 *    for (Item item : query.list(Item.class))
 *    {
 *        System.out.println(item.personName);
 *    }
 * }
 *
 * public static class Item extends AbstractEntity
 * {
 *    int id;
 *    @Column("person_name") String personName;
 * }
 * </code>
 */
public class Query implements AutoCloseable
{
	private EntityManager mEntityManager;
	private StringBuilder mStatement;
	private HashMap<String,String> mParameters;


	Query(EntityManager aEntityManager, String aStatement)
	{
		mEntityManager = aEntityManager;
		mStatement = new StringBuilder(aStatement);
		mParameters = new HashMap<>();
	}


	public Query append(String aStatement)
	{
		HashSet<String> keysUsed = new HashSet<>();

		replaceParameters(aStatement, keysUsed);

		boolean empty = true;
		for (String key : keysUsed)
		{
			if (mParameters.get(key) != null)
			{
				empty = false;
				break;
			}
		}

		if (!empty || keysUsed.isEmpty())
		{
			mStatement.append(aStatement);
		}

		return this;
	}


	@Override
	public void close() throws SQLException
	{
	}


	/**
	 * Bind a value to a named query parameter.
	 *
	 * @param aField
	 *   field name in the query, e.g. field name "value" in query "select * from table where id=:value"
	 * @param aValue
	 *   value
	 */
	public Query setParameter(String aField, Object aValue)
	{
//		Log.out.println("Setting parameter " + aField + " = " + aValue);

		String value;

		if (aValue != null && Collection.class.isAssignableFrom(aValue.getClass()))
		{
			value = "";
			for (Object o : (Collection)aValue)
			{
				if (!value.isEmpty())
				{
					value += ",";
				}
				value += convertValue(o);
			}
		}
		else
		{
			value = convertValue(aValue);
		}

		mParameters.put(aField, value);

		return this;
	}


	private String convertValue(Object aValue) throws IllegalArgumentException
	{
		if (aValue == null)
		{
			return null;
		}
		if (aValue instanceof Boolean)
		{
			return (Boolean)aValue ? "1" : "0";
		}
		if (aValue instanceof Byte || aValue instanceof Short || aValue instanceof Integer || aValue instanceof Long || aValue instanceof Float || aValue instanceof Double)
		{
			return aValue.toString();
		}

		if (aValue instanceof Character)
		{
			return "'" + (Character)aValue + "'";
		}
		if (aValue instanceof Date)
		{
			return "'" + new Calendar(((Date)aValue).getTime()).toString() + "'";
		}
		if (aValue instanceof Calendar)
		{
			return "'" + ((Calendar)aValue).toString() + "'";
		}
		if (aValue instanceof String)
		{
			return "'" + aValue.toString() + "'";
		}

		throw new IllegalArgumentException("Unsupported type: " + aValue.getClass());
	}


	public <T extends AbstractEntity> T executeSingle(Class<T> aType) throws SQLException
	{
		T item = mEntityManager.create(aType);

		if (executeSingle(item))
		{
			return item;
		}

		return null;
	}


	public boolean executeSingle(Object aEntity) throws SQLException
	{
		try (ResultSet resultSet = execute())
		{
			if (!resultSet.next())
			{
				return false;
			}

			EntityTools.populateEntity(aEntity, resultSet);

			return true;
		}
	}


	public <T> ArrayList<T> list(Class<T> aType) throws SQLException
	{
		ArrayList list = new ArrayList();

		try (ResultSet resultSet = execute())
		{
			while (resultSet.next())
			{
				T item = mEntityManager.create(aType);

				EntityTools.populateEntity(item, resultSet);

				list.add(item);
			}

			return list;
		}
	}


	public String compile()
	{
		String sql = replaceParameters(mStatement.toString(), null);

		Log.out.println("query: " + sql);

		return sql;
	}


	private String replaceParameters(String aStatement, HashSet<String> aKeysOut)
	{
		StringBuilder result = new StringBuilder();
		char[] chars = aStatement.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

			if (c == ':')
			{
				int j;
				for (j = i + 1; j < chars.length; j++)
				{
					if (!Character.isJavaIdentifierPart(chars[j]))
					{
						break;
					}
				}

				String key = new String(chars, i + 1, j - (i + 1));

				if (!mParameters.containsKey(key))
				{
					throw new IllegalArgumentException("Parameter '" + key + "' not set.");
				}

				String value = mParameters.get(key);

				result.append(value);

				if (aKeysOut != null)
				{
					aKeysOut.add(key);
				}

				i = j - 1;
			}
			else
			{
				result.append(c);
			}
		}

		return result.toString();
	}


	public ResultSet execute() throws SQLException
	{
		String queryString = compile();

		PooledConnection conn = null;

		try
		{
			conn = (PooledConnection)mEntityManager.claim();

			return new PooledResultSet(conn, conn.createStatement().executeQuery(queryString));
		}
		catch (Exception e)
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (Exception ee)
				{
					e.printStackTrace(Log.out);
				}
			}

			throw e;
		}
	}


	public ArrayList listColumn(String aColumnName) throws SQLException
	{
		ArrayList list = new ArrayList<>();

		try (ResultSet resultSet = execute())
		{
			while (resultSet.next())
			{
				list.add(resultSet.getObject(aColumnName));
			}
		}

		return list;
	}
}

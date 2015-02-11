package org.terifan.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.terifan.util.log.Log;


/**
 * <p>A query utility enabling named query parameters to be be used. Named query parameters are tokens of the form :name in the query string.
 * A value is bound to the integer parameter :foo by calling <pre>setParameter("foo", foo, Types.INTEGER);</pre> for example. A name may
 * appear multiple times in the query string.</p>
 * <p/>
 * Sample iterating a ResultSet produced via a Query:
 * <pre>try (Query query = new Query("select * from table where id between @first and @last"))
 * {
 *    query.setParameter("first", 1);
 *    query.setParameter("last", 7);
 *
 *    for (ResultSet rs : query.execute(connection))
 *    {
 *        System.out.println(rs.getInt("id"));
 *    }
 * }</pre>
 * <p/>
 * Sample fetching a Query result to a list:
 * <pre>try (Query query = new Query("select * from table where id between @first and @last"))
 * {
 *    query.setParameter("first", 1);
 *    query.setParameter("last", 7);
 *
 *    for (Item item : query.list(connection, Item.class))
 *    {
 *        System.out.println(item.getId());
 *    }
 * }
 *
 * class Item
 * {
 *    @Column(name="id") int id;
 * }
 * </pre>
 */
public class Query implements AutoCloseable
{
	private EntityManager em;
	private String mStatement;
	private HashMap<String,Object> mValues;
	private HashMap<String,Integer> mTypes;
	private ResultSetIterable mSqlResult;


	Query(EntityManager aEntityManager, String aStatement)
	{
		em = aEntityManager;
		mStatement = aStatement;
		mValues = new HashMap<>();
		mTypes = new HashMap<>();
	}


	public Query(String aStatement)
	{
		mStatement = aStatement;
		mValues = new HashMap<>();
		mTypes = new HashMap<>();
	}


	public Query append(String aStatement)
	{
		mStatement += aStatement;
		return this;
	}


	@Override
	public void close() throws SQLException
	{
		if (mSqlResult != null)
		{
			mSqlResult.close();
			mSqlResult = null;
		}
	}


	/**
	 * Bind a value to a named query parameter.
	 *
	 * @param aField
	 *   field name in the query, e.g. field name "value" in query "select * from table where id=@value"
	 * @param aValue
	 *   value
	 */
	public Query setParameter(String aField, Object aValue)
	{
		mValues.put(aField, aValue);
		return this;
	}


	/**
	 * Bind a value to a named query parameter.
	 *
	 * @param aField
	 *   field name in the query, e.g. field name "value" in query "select * from table where id=@value"
	 * @param aValue
	 *   value
	 * @param aType
	 *   parameter type, see java.sql.Types
	 * @see
	 *   java.sql.Types
	 */
	public Query setParameter(String aField, Object aValue, int aType)
	{
		mValues.put(aField, aValue);
		mTypes.put(aField, aType);
		return this;
	}


	@Deprecated
	public ResultSet executeQuery(Connection aConnection) throws SQLException
	{
		return execute(aConnection).iterator().next();
	}


	@Deprecated
	public ResultSetIterable execute(Connection aConnection) throws SQLException
	{
		String st = mStatement;
		ArrayList<Object> values = new ArrayList<>();
		ArrayList<Integer> types = new ArrayList<>();
		ArrayList<String> fields = new ArrayList<>();

//		Log.out.println(mStatement);

		for (;;)
		{
			int i = st.indexOf("@");

			if (i == -1)
			{
				break;
			}

			String field = trim(st.substring(i + 1));

			fields.add(field);
			values.add(mValues.get(field));
			types.add(mTypes.get(field));

			st = st.substring(0, i) + "?" + st.substring(i + 1 + field.length());
		}

//		Log.out.println(st);

		PreparedStatement statement = aConnection.prepareStatement(st);

		try
		{
			for (int i = 0; i < values.size(); i++)
			{
				Integer type = types.get(i);

//				Log.out.println("  " + fields.get(i) + " = " + values.get(i));

				if (type == null)
				{
					statement.setObject(1 + i, values.get(i));
				}
				else
				{
					statement.setObject(1 + i, values.get(i), type);
				}
			}

			mSqlResult = new ResultSetIterable(statement.executeQuery());

			return mSqlResult;
		}
		catch (SQLException e)
		{
			statement.close();
			throw e;
		}
	}


//	public <T> T executeSingle(Connection aConnection, Class<T> aType) throws SQLException
//	{
//		T e = createEntityInstance(aType);
//
//		if (executeSingle(aConnection, e))
//		{
//			return e;
//		}
//
//		return null;
//	}


	public <T extends AbstractEntity> T executeSingle(Class<T> aType) throws SQLException
	{
		T item = em.create(aType);

		if (executeSingle(item))
		{
			return item;
		}

		return null;
	}


	public boolean executeSingle(AbstractEntity aEntity) throws SQLException
	{
		try (Connection conn = em.claim())
		{
			ResultSet resultSet = executeQuery(conn);

			if (!resultSet.next())
			{
				return false;
			}

			EntityTools.populateEntity(aEntity, resultSet);

			return true;
		}
	}


//	public boolean executeSingle(Connection aConnection, Object aItem) throws SQLException
//	{
//		ResultSet resultSet = executeQuery(aConnection);
//
//		if (!resultSet.next())
//		{
//			return false;
//		}
//
//		assignValue(aItem, resultSet);
//
//		return true;
//	}


	public <T extends AbstractEntity> List<T> list(Class<T> aType) throws SQLException
	{
		try (Connection conn = em.claim())
		{
			ArrayList list = new ArrayList();

			for (ResultSet resultSet : execute(conn))
			{
				T item = em.create(aType);

				EntityTools.populateEntity(item, resultSet);

				list.add(item);
			}

			return list;
		}
	}


//	@Deprecated
//	public <T> List<T> list(Connection aConnection, Class<T> aType) throws SQLException
//	{
//		ArrayList list = new ArrayList();
//
//		for (ResultSet resultSet : execute(aConnection))
//		{
//			T item = createEntityInstance(aType);
//
//			EntityTools.populateEntity(item, resultSet);
//
//			list.add(item);
//		}
//
//		return list;
//	}


	private static <T> T createEntityInstance(Class<T> aType) throws SQLException
	{
		try
		{
			return aType.newInstance();
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			try
			{
				Constructor c = aType.getDeclaredConstructor();
				c.setAccessible(true);
				return (T)c.newInstance();
			}
			catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ee)
			{
				throw new SQLException("Problem creating an instance of " + aType + " (make sure there is a zero arguments constructor).", ee);
			}
		}
	}


	private static String trim(String aString)
	{
		for (int i = 0; i < aString.length(); i++)
		{
			if (!Character.isJavaIdentifierPart(aString.charAt(i)))
			{
				return aString.substring(0, i);
			}
		}

		return aString;
	}
}

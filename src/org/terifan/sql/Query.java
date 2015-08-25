package org.terifan.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.terifan.util.Strings;


/**
 * <p>A query utility enabling named query parameters to be be used. Named query parameters are tokens of the form :name in the query string.
 * A value is bound to the integer parameter :foo by calling <pre>setParameter("foo", foo, Types.INTEGER);</pre> for example. A name may
 * appear multiple times in the query string.</p>
 * <p/>
 * Sample fetching a Query result to a list:
 * <pre>Query query = EntityManager.createQuery("select * from table where id between :first and :last")
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


//	Query(String aStatement)
//	{
//		mStatement = aStatement;
//		mValues = new HashMap<>();
//		mTypes = new HashMap<>();
//	}


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
	 *   field name in the query, e.g. field name "value" in query "select * from table where id=:value"
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
	 *   field name in the query, e.g. field name "value" in query "select * from table where id=:value"
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


	private ResultSetIterable executeImpl(Connection aConnection) throws SQLException
	{
		String st = mStatement;
		ArrayList<Object> values = new ArrayList<>();
		ArrayList<Integer> types = new ArrayList<>();
//		ArrayList<String> fields = new ArrayList<>();

//		Log.out.println(mStatement);

		for (;;)
		{
			int i = st.indexOf(":");

			if (i == -1)
			{
				break;
			}

			String field = trim(st.substring(i + 1));

			if (Strings.isEmptyOrNull(field))
			{
				throw new IllegalArgumentException("Illegal parameter name in query: " + mStatement);
			}

//			fields.add(field);
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
				Object tmp = values.get(i);

				if (tmp instanceof java.util.Date)
				{
					tmp = new java.sql.Date(((java.util.Date)tmp).getTime());
				}

//				Log.out.println("  " + fields.get(i) + " = " + values.get(i));

				if (type == null)
				{
					statement.setObject(1 + i, tmp);
				}
				else
				{
					statement.setObject(1 + i, tmp, type);
				}
			}

			mSqlResult = new ResultSetIterable(statement, statement.executeQuery());

			return mSqlResult;
		}
		catch (SQLException e)
		{
			statement.close();
			throw e;
		}
	}


	public <T extends AbstractEntity> T executeSingle(Class<T> aType) throws SQLException
	{
		T item = em.create(aType);

		if (executeSingle(item))
		{
			return item;
		}

		return null;
	}


	public boolean executeSingle(Object aEntity) throws SQLException
	{
		try (Connection conn = em.claim())
		{
			try
			{
				ResultSet resultSet = executeImpl(conn).iterator().next();

				if (!resultSet.next())
				{
					return false;
				}

				EntityTools.populateEntity(aEntity, resultSet);

				return true;
			}
			finally
			{
				close();
			}
		}
	}


	public <T> List<T> list(Class<T> aType) throws SQLException
	{
		try (Connection conn = em.claim())
		{
			try
			{
				ArrayList list = new ArrayList();

				for (ResultSet resultSet : executeImpl(conn))
				{
					T item = em.create(aType);

					EntityTools.populateEntity(item, resultSet);

					list.add(item);
				}

				return list;
			}
			finally
			{
				close();
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

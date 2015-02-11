package org.terifan.sql;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.terifan.util.Calendar;


/**
 *
 * <code>
 * id = new SqlStatement("t_log")
 *   .key("id", id, true)
 *   .put("datetime", new Date(0))
 *   .put("file_id", fileId)
 *   .executeInsertUpdate(aConnection);
 * </code>
 */
public class SqlStatement
{
	private String mTable;
	private LinkedHashMap<String,Object> mValues;
	private LinkedHashMap<String,Object> mKeys;
	private String mIdentityColumn;
	private int insertedId;
	private boolean identityColumn;


	public SqlStatement(String aTable)
	{
		mTable = aTable;
		mValues = new LinkedHashMap<>();
		mKeys = new LinkedHashMap<>();
		identityColumn = true;
	}


	public SqlStatement setIdentityColumn(boolean identityColumn)
	{
		this.identityColumn = identityColumn;
		return this;
	}


	public int getInsertedId()
	{
		return insertedId;
	}


	/**
	 * Put column name and value.
	 */
	public SqlStatement put(String aColumn, Object aValue)
	{
		put(aColumn, aValue, false, false);
		return this;
	}


	/**
	 * Put column name and value and specify if column is primary key.
	 */
	public SqlStatement key(String aColumn, Object aValue)
	{
		put(aColumn, aValue, true, false);
		return this;
	}


	/**
	 * Put column name and value and specify if column is primary key and identity column.
	 */
	public SqlStatement key(String aColumn, Object aValue, boolean aIdentityColumn)
	{
		put(aColumn, aValue, true, aIdentityColumn);
		return this;
	}


	private void put(String aColumn, Object aValue, boolean aPrimaryKey, boolean aIdentityColumn)
	{
		if (aIdentityColumn)
		{
			if (mIdentityColumn != null)
			{
				throw new IllegalArgumentException();
			}
			mIdentityColumn = aColumn;
		}
		if (aPrimaryKey)
		{
			if (mKeys.put(aColumn, aValue) != null)
			{
				throw new IllegalArgumentException();
			}
		}
		else
		{
			if (mValues.put(aColumn, aValue) != null)
			{
				throw new IllegalArgumentException();
			}
		}
	}


	public boolean executeInsertUpdate(Connection aConnection) throws SQLException
	{
		if (executeUpdate(aConnection) > 0)
		{
			return false;
		}

		executeInsert(aConnection);

		return true;
	}


	public int executeUpdate(Connection aConnection) throws SQLException
	{
		StringBuilder where = new StringBuilder();
		StringBuilder columns = new StringBuilder();
		for (String columnName : mValues.keySet())
		{
			if (columns.length() > 0)
			{
				columns.append(",");
			}
			columns.append("[").append(columnName).append("]=?");
		}
		for (String keyName : mKeys.keySet())
		{
			if (where.length() > 0)
			{
				where.append(" and ");
			}
			where.append("[").append(keyName).append("]=?");
		}

		String sql = "update " + mTable + " set " + columns + " where " + where;

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			for (Object value : mKeys.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			return statement.executeUpdate();
		}
	}


	public int executeInsert(Connection aConnection) throws SQLException
	{
		StringBuilder values = new StringBuilder();
		StringBuilder columns = new StringBuilder();
		for (String column : mValues.keySet())
		{
			if (columns.length() > 0)
			{
				columns.append(",");
				values.append(",");
			}
			columns.append("[").append(column).append("]");
			values.append("?");
		}

		if (!identityColumn)
		{
			for (String column : mKeys.keySet())
			{
				if (columns.length() > 0)
				{
					columns.append(",");
					values.append(",");
				}
				columns.append("[").append(column).append("]");
				values.append("?");
			}
		}

		String sql = "insert " + mTable + " (" + columns + ") values(" + values + ")";

		if (identityColumn)
		{
			sql += "; select @@identity as id";
		}

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}
			if (!identityColumn)
			{
				for (Object value : mKeys.values())
				{
					statement.setObject(++i, javaToResultSet(value));
				}
			}

			if (identityColumn)
			{
				try (ResultSet resultSet = statement.executeQuery())
				{
					if (resultSet.next())
					{
						insertedId = resultSet.getInt("id");

						return insertedId;
					}
				}
			}
			else
			{
				statement.executeUpdate();
				insertedId = -1;
			}
		}

		throw new SQLException();
	}


	void executeUpdate2(Connection aConnection, AbstractEntity aEntity) throws SQLException
	{
		StringBuilder where = new StringBuilder();
		StringBuilder columns = new StringBuilder();
		for (String columnName : mValues.keySet())
		{
			if (columns.length() > 0)
			{
				columns.append(",");
			}
			columns.append("[").append(columnName).append("]=?");
		}
		for (String keyName : mKeys.keySet())
		{
			if (where.length() > 0)
			{
				where.append(" and ");
			}
			where.append("[").append(keyName).append("]=?");
		}

		String sql = "update " + mTable + " set " + columns + " where " + where + "; select * from " + mTable + " where " + where;

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			// update params
			for (Object value : mKeys.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			// select params
			for (Object value : mKeys.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			try (ResultSet resultSet = statement.executeQuery())
			{
				if (!resultSet.next())
				{
					throw new SQLException("Updated record not found");
				}

				EntityTools.populateEntity(aEntity, resultSet);
			}
		}
	}


	void executeInsert2(Connection aConnection, AbstractEntity aEntity) throws SQLException
	{
		if (!identityColumn)
		{
			throw new IllegalStateException();
		}

		StringBuilder values = new StringBuilder();
		StringBuilder columns = new StringBuilder();
		for (String column : mValues.keySet())
		{
			if (columns.length() > 0)
			{
				columns.append(",");
				values.append(",");
			}
			columns.append("[").append(column).append("]");
			values.append("?");
		}

		String sql = "insert " + mTable + " (" + columns + ") values(" + values + "); select * from " + mTable + " where id=@@identity";

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			try (ResultSet resultSet = statement.executeQuery())
			{
				if (!resultSet.next())
				{
					throw new SQLException("Inserted record not found");
				}

				EntityTools.populateEntity(aEntity, resultSet);
			}
		}
	}


	public int executeDelete(Connection aConnection) throws SQLException
	{
		if (!identityColumn)
		{
			throw new IllegalStateException();
		}

		if (!mValues.isEmpty())
		{
			throw new IllegalArgumentException();
		}

		StringBuilder where = new StringBuilder();

		for (String column : mValues.keySet())
		{
			if (where.length() > 0)
			{
				where.append(" and ");
			}
			where.append("[").append(column).append("]=?");
		}
		for (String column : mKeys.keySet())
		{
			if (where.length() > 0)
			{
				where.append(" and ");
			}
			where.append("[").append(column).append("]=?");
		}

		String sql = "delete " + mTable + " where " + where;

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}
			for (Object value : mKeys.values())
			{
				statement.setObject(++i, javaToResultSet(value));
			}

			return statement.executeUpdate();
		}
	}


	private Object javaToResultSet(Object aValue)
	{
		if (aValue != null)
		{
			if (aValue instanceof java.util.Date)
			{
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((java.util.Date)aValue);
			}
			if (aValue instanceof Calendar)
			{
				return aValue.toString();
			}
			if (aValue instanceof UUID)
			{
				return aValue.toString();
			}
			if (Enum.class.isAssignableFrom(aValue.getClass()))
			{
				return ((Enum)aValue).ordinal() + 1;
			}
		}
		return aValue;
	}


	public static void main(String ... args)
	{
		try
		{
			SqlStatement insert = new SqlStatement("t_log")
				.key("id", 0, true)
				.put("datetime", new Date(0))
				.put("str", "hello world");

			SqlStatement update = new SqlStatement("t_log")
				.key("id", 7)
				.key("str", "test", true)
				.put("datetime", new Date(0))
				.put("str", "hello world");

			SqlStatement delete = new SqlStatement("t_log")
				.key("id", 7)
				.key("str", "test");

//			Log.out.println(insert.executeInsert(null));
//			Log.out.println(update.executeInsertUpdate(null));
//			Log.out.println(delete.executeDelete(null));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

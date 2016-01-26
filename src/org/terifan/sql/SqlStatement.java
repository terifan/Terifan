package org.terifan.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.terifan.util.Calendar;
import org.terifan.util.log.Log;


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
@Deprecated
class SqlStatement
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
			Object oldValue = mValues.put(aColumn, aValue);

			if (oldValue != null)
			{
				throw new IllegalArgumentException("Value in column '" + aColumn + "' replaced '" + oldValue + "' with '" + aValue + "'");
			}
		}
	}


	@Deprecated
	public boolean executeInsertUpdate(Connection aConnection) throws SQLException
	{
		if (executeUpdate(aConnection) > 0)
		{
			return false;
		}

		executeInsert(aConnection);

		return true;
	}


	@Deprecated
	public int executeUpdate(Connection aConnection) throws SQLException
	{
		StringBuilder columnsString = new StringBuilder();
		for (String columnName : mValues.keySet())
		{
			if (columnsString.length() > 0)
			{
				columnsString.append(",");
			}
			columnsString.append("[").append(columnName).append("]=?");
		}

		StringBuilder where = new StringBuilder();
		for (String keyName : mKeys.keySet())
		{
			if (where.length() > 0)
			{
				where.append(" and ");
			}
			where.append("[").append(keyName).append("]=?");
		}

		String sql = "update " + mTable + " set " + columnsString + " where " + where;

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


	@Deprecated
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
		int j = 0;
		Column[] valueColumns = new Column[mValues.size()];
		StringBuilder columnString = new StringBuilder();
		for (String columnName : mValues.keySet())
		{
			if (columnString.length() > 0)
			{
				columnString.append(",");
			}
			columnString.append("[").append(columnName).append("]=?");
			valueColumns[j++] = EntityTools.getColumn(aEntity, columnName);
		}

		StringBuilder whereString = new StringBuilder();
		for (String keyName : mKeys.keySet())
		{
			if (whereString.length() > 0)
			{
				whereString.append(" and ");
			}
			whereString.append("[").append(keyName).append("]=?");
		}

		if (whereString.length() == 0)
		{
			throw new IllegalStateException("No keys defined");
		}

		String sql = "update " + mTable + " set " + columnString + " where " + whereString + "; select * from " + mTable + " where " + whereString;

		if (aConnection == null)
		{
			throw new IllegalArgumentException("aConnection is null - " + sql);
		}

		try (PreparedStatement statement = aConnection.prepareStatement(sql))
		{
			int i = 0;
			for (Object value : mValues.values())
			{
				statement.setObject(i + 1, javaToResultSet(value, valueColumns[i]));
				i++;
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
			catch (SQLException e)
			{
				throw new SQLException("Error with expression: " + sql, e);
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
			Column col = EntityTools.getColumn(aEntity, column);
			if (mValues.get(column) == null && col.producer() != Column.NO_PRODUCER.class)
			{
				try
				{
					Object value = col.producer().newInstance().produce(aEntity, col);

//					Log.out.println("Producing value '" + value + "' for column '" + column + "'");

					mValues.put(column, value);
				}
				catch (InstantiationException | IllegalAccessException e)
				{
				}
			}

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


	@Deprecated
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


	private Object javaToResultSet(Object aValue, Column aColumn)
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
				if (aColumn.enumType() == EnumType.NAME)
				{
					return ((Enum)aValue).name();
				}
				else
				{
					return ((Enum)aValue).ordinal() + 1;
				}
			}
		}
		return aValue;
	}
}

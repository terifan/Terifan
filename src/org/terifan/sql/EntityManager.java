package org.terifan.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;


public class EntityManager implements AutoCloseable
{
	private ConnectionPool mConnectionPool;
	private Connection mConnection;


	public EntityManager(ConnectionPool aConnectionPool)
	{
		mConnectionPool = aConnectionPool;
	}


	public EntityManager(Connection aConnection)
	{
		mConnection = aConnection;
	}


	public Connection claim()
	{
		if (mConnection != null)
		{
			return mConnection;
		}
		return mConnectionPool.claim();
	}


	public Query createQuery(String aStatement)
	{
		return new Query(this, aStatement);
	}


	@Override
	public void close() throws SQLException
	{
		if (mConnection != null)
		{
			mConnection.close();
			mConnection = null;
		}
		if (mConnectionPool != null)
		{
			mConnectionPool.shutdown();
			mConnectionPool = null;
		}
	}


	public <E extends AbstractEntity> E create(Class<E> aType)
	{
		try
		{
			E instance = aType.getConstructor().newInstance();

//			E instance = aType.newInstance();

			instance.bind(this);

			return instance;
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException("Ensure that " + aType + " has an empty constructor", e);
		}
		catch (InstantiationException | IllegalAccessException | IllegalStateException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public <E extends AbstractEntity> E create(Class<E> aType, Object aPrimaryKey) throws SQLException
	{
		try
		{
			E entity = load(aType, aPrimaryKey);

			if (entity != null)
			{
				return entity;
			}

			return aType.getConstructor(EntityManager.class).newInstance(this);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public <E extends AbstractEntity> E load(Class<E> aType, Object... aPrimaryKey) throws SQLException
	{
		String table = getTable(aType);

		Query query = createQuery("select * from " + table + " where ");

		int keyIndex = 0;

		for (String keyName : EntityTools.getColumns(aType, true, false, false))
		{
			query.append("[" + keyName + "] = :" + keyName);

			query.setParameter(keyName, aPrimaryKey[keyIndex++]);
		}

		return query.executeSingle(aType);
	}


	public boolean save(AbstractEntity aEntity) throws SQLException
	{
		String table = getTable(aEntity.getClass());

		SqlStatement statement = new SqlStatement(table);

		boolean hasKeys = true;

		for (String keyName : EntityTools.getColumns(aEntity, true, false, false))
		{
			Object value = EntityTools.getColumnValue(aEntity, keyName);
			hasKeys &= value != null;
			statement.key(keyName, value);
		}
		for (String columnName : EntityTools.getColumns(aEntity, false, true, false))
		{
			statement.put(columnName, EntityTools.javaToResultSet(aEntity, columnName, EntityTools.getColumnValue(aEntity, columnName)));
		}

		try (Connection conn = claim())
		{
			if (hasKeys)
			{
				statement.executeUpdate2(conn, aEntity);

				return false;
			}
			else
			{
				statement.executeInsert2(conn, aEntity);

				return true;
			}
		}
	}


	public boolean remove(AbstractEntity aEntity) throws SQLException
	{
		String table = getTable(aEntity.getClass());

		SqlStatement statement = new SqlStatement(table);

		for (String keyName : EntityTools.getColumns(aEntity, true, false, true))
		{
			statement.key(keyName, EntityTools.getColumnValue(aEntity, keyName));
		}

		try (Connection conn = claim())
		{
			return statement.executeDelete(conn) > 0;
		}
	}


	public boolean refresh(AbstractEntity aEntity) throws SQLException
	{
		String table = getTable(aEntity.getClass());

		Query query = createQuery("select * from " + table + " where ");

		int keyIndex = 0;

		for (String name : EntityTools.getColumns(aEntity, true, false, true))
		{
			query.append("[" + name + "] = :" + name);

			query.setParameter(name, EntityTools.getColumnValue(aEntity, name));
		}

		return query.executeSingle(aEntity);
	}


	private String getTable(Class aClass) throws IllegalArgumentException
	{
		Table ann = (Table)aClass.getAnnotation(Table.class);

		if (ann == null)
		{
			throw new IllegalArgumentException("Entity '" + aClass.getSimpleName() + "' is missing Table annotation.");
		}

		String table = ann.catalog();

		if (table.isEmpty())
		{
			throw new IllegalArgumentException("Entity '" + aClass.getSimpleName() + "' is missing catalog property in Table annotation.");
		}

		return table;
	}
}

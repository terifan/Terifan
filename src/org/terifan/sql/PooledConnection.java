package org.terifan.sql;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


public class PooledConnection implements Connection
{
	private final static long IDLE_TEST_PERIOD = 10*1000;

	private final ConnectionPool mConnectionPool;
	private Connection mConnection;
	private long mLastAliveTime;


	public PooledConnection(ConnectionPool aConnectionPool, Connection aConnection)
	{
		mConnectionPool = aConnectionPool;
		mConnection = aConnection;

		mLastAliveTime = System.currentTimeMillis();
	}


	public Connection getConnection() throws SQLException
	{
		// create a new connection if the connection was erroneously closed
		if (mConnectionPool != null && mConnection.isClosed())
		{
			mConnection = mConnectionPool.claim().mConnection;
		}

		return mConnection;
	}


	@Override
	public void close() throws SQLException
	{
		mConnectionPool.release(this);
	}


	@Override
	public boolean isValid(int aTimeoutSeconds) throws SQLException
	{
		if (System.currentTimeMillis() - mLastAliveTime > IDLE_TEST_PERIOD)
		{
			if (mConnection.isClosed())
			{
				return false;
			}

			try (Statement statement = getConnection().createStatement())
			{
				statement.setQueryTimeout(aTimeoutSeconds);
				statement.execute("select 1");
			}
			catch (Exception e)
			{
				return false;
			}

			mLastAliveTime = System.currentTimeMillis();
		}

		return true;
	}


	// ---------- overrides


	@Override
	public void setAutoCommit(boolean aState) throws SQLException
	{
		getConnection().setAutoCommit(aState);
	}


	@Override
	public void commit() throws SQLException
	{
		getConnection().commit();
	}


	@Override
	public void rollback() throws SQLException
	{
		getConnection().rollback();
	}


	@Override
	public Statement createStatement() throws SQLException
	{
		return getConnection().createStatement();
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement) throws SQLException
	{
		return getConnection().prepareStatement(aStatement);
	}


	@Override
	public CallableStatement prepareCall(String aStatement) throws SQLException
	{
		return getConnection().prepareCall(aStatement);
	}


	@Override
	public String nativeSQL(String aStatement) throws SQLException
	{
		return getConnection().nativeSQL(aStatement);
	}


	@Override
	public boolean getAutoCommit() throws SQLException
	{
		return getConnection().getAutoCommit();
	}


	@Override
	public boolean isClosed() throws SQLException
	{
		return getConnection().isClosed();
	}


	@Override
	public DatabaseMetaData getMetaData() throws SQLException
	{
		return getConnection().getMetaData();
	}


	@Override
	public void setReadOnly(boolean readOnly) throws SQLException
	{
		getConnection().setReadOnly(readOnly);
	}


	@Override
	public boolean isReadOnly() throws SQLException
	{
		return getConnection().isReadOnly();
	}


	@Override
	public void setCatalog(String aCatalog) throws SQLException
	{
		getConnection().setCatalog(aCatalog);
	}


	@Override
	public String getCatalog() throws SQLException
	{
		return getConnection().getCatalog();
	}


	@Override
	public void setTransactionIsolation(int aLevel) throws SQLException
	{
		getConnection().setTransactionIsolation(aLevel);
	}


	@Override
	public int getTransactionIsolation() throws SQLException
	{
		return getConnection().getTransactionIsolation();
	}


	@Override
	public SQLWarning getWarnings() throws SQLException
	{
		return getConnection().getWarnings();
	}


	@Override
	public void clearWarnings() throws SQLException
	{
		getConnection().clearWarnings();
	}


	@Override
	public Statement createStatement(int aResultSetType, int aResultSetConcurrency) throws SQLException
	{
		return getConnection().createStatement(aResultSetType, aResultSetConcurrency);
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement, int aResultSetType, int aResultSetConcurrency) throws SQLException
	{
		return getConnection().prepareStatement(aStatement, aResultSetType, aResultSetConcurrency);
	}


	@Override
	public CallableStatement prepareCall(String aStatement, int aResultSetType, int aResultSetConcurrency) throws SQLException
	{
		return getConnection().prepareCall(aStatement, aResultSetType, aResultSetConcurrency);
	}


	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		return getConnection().getTypeMap();
	}


	@Override
	public void setTypeMap(Map<String, Class<?>> aMap) throws SQLException
	{
		getConnection().setTypeMap(aMap);
	}


	@Override
	public void setHoldability(int aHoldability) throws SQLException
	{
		getConnection().setHoldability(aHoldability);
	}


	@Override
	public int getHoldability() throws SQLException
	{
		return getConnection().getHoldability();
	}


	@Override
	public Savepoint setSavepoint() throws SQLException
	{
		return getConnection().setSavepoint();
	}


	@Override
	public Savepoint setSavepoint(String aName) throws SQLException
	{
		return getConnection().setSavepoint(aName);
	}


	@Override
	public void rollback(Savepoint aSavepoint) throws SQLException
	{
		getConnection().rollback(aSavepoint);
	}


	@Override
	public void releaseSavepoint(Savepoint aSavepoint) throws SQLException
	{
		getConnection().releaseSavepoint(aSavepoint);
	}


	@Override
	public Statement createStatement(int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability) throws SQLException
	{
		return getConnection().createStatement(aResultSetType, aResultSetConcurrency, aResultSetHoldability);
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement, int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability) throws SQLException
	{
		return getConnection().prepareStatement(aStatement, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
	}


	@Override
	public CallableStatement prepareCall(String aStatement, int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability) throws SQLException
	{
		return getConnection().prepareCall(aStatement, aResultSetType, aResultSetConcurrency, aResultSetHoldability);
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement, int aAutoGeneratedKeys) throws SQLException
	{
		return getConnection().prepareStatement(aStatement, aAutoGeneratedKeys);
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement, int[] aColumnIndexes) throws SQLException
	{
		return getConnection().prepareStatement(aStatement, aColumnIndexes);
	}


	@Override
	public PreparedStatement prepareStatement(String aStatement, String[] aColumnNames) throws SQLException
	{
		return getConnection().prepareStatement(aStatement, aColumnNames);
	}


	@Override
	public Clob createClob() throws SQLException
	{
		return getConnection().createClob();
	}


	@Override
	public Blob createBlob() throws SQLException
	{
		return getConnection().createBlob();
	}


	@Override
	public NClob createNClob() throws SQLException
	{
		return getConnection().createNClob();
	}


	@Override
	public SQLXML createSQLXML() throws SQLException
	{
		return getConnection().createSQLXML();
	}


	@Override
	public void setClientInfo(String aName, String aValue) throws SQLClientInfoException
	{
		try
		{
			getConnection().setClientInfo(aName, aValue);
		}
		catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}


	@Override
	public void setClientInfo(Properties aProperties) throws SQLClientInfoException
	{
		try
		{
			getConnection().setClientInfo(aProperties);
		}
		catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}


	@Override
	public String getClientInfo(String aName) throws SQLException
	{
		return getConnection().getClientInfo(aName);
	}


	@Override
	public Properties getClientInfo() throws SQLException
	{
		return getConnection().getClientInfo();
	}


	@Override
	public Array createArrayOf(String aTypeName, Object[] aElements) throws SQLException
	{
		return getConnection().createArrayOf(aTypeName, aElements);
	}


	@Override
	public Struct createStruct(String aTypeName, Object[] aAttributes) throws SQLException
	{
		return getConnection().createStruct(aTypeName, aAttributes);
	}


	@Override
	public void setSchema(String aSchema) throws SQLException
	{
		getConnection().setSchema(aSchema);
	}


	@Override
	public String getSchema() throws SQLException
	{
		return getConnection().getSchema();
	}


	@Override
	public void abort(Executor aExecutor) throws SQLException
	{
		getConnection().abort(aExecutor);
	}


	@Override
	public void setNetworkTimeout(Executor aExecutor, int aMilliseconds) throws SQLException
	{
		getConnection().setNetworkTimeout(aExecutor, aMilliseconds);
	}


	@Override
	public int getNetworkTimeout() throws SQLException
	{
		return getConnection().getNetworkTimeout();
	}


	@Override
	public boolean isWrapperFor(Class<?> aIface) throws SQLException
	{
		return getConnection().isWrapperFor(aIface);
	}


	@Override
	public <T> T unwrap(Class<T> aIface) throws SQLException
	{
		return getConnection().unwrap(aIface);
	}


	@Override
	public String toString()
	{
		try
		{
			return getConnection().toString();
		}
		catch (Exception e)
		{
			return "" + e.toString();
		}
	}
}

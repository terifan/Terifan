package org.terifan.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.terifan.util.Pool;
import org.terifan.util.Strings;


public class ConnectionPool extends Pool<PooledConnection>
{
	private final Driver mDriver;
	private final String mHost;
	private final String mLogin;
	private final String mPassword;
	private final String mCatalog;


	public ConnectionPool(String aDriver, String aHost, String aLogin, String aPassword, String aCatalog)
	{
		super(3, 10);

		if (Strings.isEmptyOrNull(aDriver) || Strings.isEmptyOrNull(aHost) || Strings.isEmptyOrNull(aLogin) || Strings.isEmptyOrNull(aPassword) || Strings.isEmptyOrNull(aCatalog))
		{
			throw new IllegalArgumentException("Bad input " + aDriver + ", " + aHost + ", " + aLogin + ", " + aPassword + ", " + aCatalog);
		}

		try
		{
			mDriver = (Driver)Class.forName(aDriver).newInstance();

			DriverManager.registerDriver(mDriver);
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e)
		{
			throw new IllegalArgumentException(e);
		}

		mHost = aHost;
		mLogin = aLogin;
		mPassword = aPassword;
		mCatalog = aCatalog;
	}


	public void shutdown() throws SQLException
	{
		clear();

		if (mDriver != null)
		{
			DriverManager.deregisterDriver(mDriver);
		}
	}


	@Override
	protected PooledConnection create()
	{
		RuntimeException exception = null;

		for (int i = 0; i < 10; i++)
		{
			try
			{
				DriverManager.setLoginTimeout(10);
				Connection connection = DriverManager.getConnection(mHost, mLogin, mPassword);
				connection.setCatalog(mCatalog);

				return new PooledConnection(this, connection);
			}
			catch (SQLException e)
			{
				exception = new RuntimeException(e);

				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException ee)
				{
				}
			}
		}

		throw exception;
	}


	@Override
	protected void destroy(PooledConnection aConnection)
	{
		try
		{
			Connection conn = aConnection.getConnection();

			if (conn != null)
			{
				conn.close();
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	protected boolean prepare(PooledConnection aConnection)
	{
		try
		{
			if (aConnection.getConnection().isClosed())
			{
				return false;
			}

			Connection conn = aConnection.getConnection();

			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.clearWarnings();
			}

			return aConnection.isValid(5);
		}
		catch (SQLException e)
		{
			return false;
		}
	}


	@Override
	protected boolean reset(PooledConnection aConnection)
	{
		try
		{
			return aConnection.getWarnings() == null;
		}
		catch (SQLException e)
		{
			return false;
		}
	}
}

package org.terifan.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import org.terifan.util.Pool;
import org.terifan.util.Strings;
import org.terifan.util.log.Log;


public class ConnectionPool extends Pool<PooledConnection>
{
	private final Driver mDriver;
	private final String mHost;
	private final String mLogin;
	private final String mPassword;
	private final String mCatalog;
	private PrintStream mLog;
	private Timer mCleanUpTimer;


	public ConnectionPool(String aDriver, String aHost, String aLogin, String aPassword, String aCatalog)
	{
		super(10, 15 * 60);

		if (Strings.isEmptyOrNull(aDriver) || Strings.isEmptyOrNull(aHost) || Strings.isEmptyOrNull(aLogin) || Strings.isEmptyOrNull(aPassword) || Strings.isEmptyOrNull(aCatalog))
		{
			throw new IllegalArgumentException("Bad input " + aDriver + ", " + aHost + ", " + aLogin + ", " + aPassword + ", " + aCatalog);
		}

		super.setYoungFirst(true);

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

		mCleanUpTimer = new Timer(true);
		mCleanUpTimer.schedule(mCleanUpTimerTask, 60000, 60000);
	}


	private TimerTask mCleanUpTimerTask = new TimerTask()
	{
		@Override
		public void run()
		{
			cleanUp();
		}
	};


	public void setLog(PrintStream aLog)
	{
		mLog = aLog;
	}


	public void shutdown() throws SQLException
	{
		try
		{
			mCleanUpTimer.cancel();
		}
		catch (Exception e)
		{
			mLog.println("ConnectionPool: Error: " + Log.getStackTraceStringFlatten(e));

			e.printStackTrace(Log.out);
		}

		clear();

		if (mDriver != null)
		{
			DriverManager.deregisterDriver(mDriver);
		}
	}


	@Override
	protected PooledConnection create()
	{
		if (mLog != null) mLog.println("ConnectionPool: Creating connection");

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
				if (mLog != null) mLog.println("ConnectionPool: Error: " + Log.getStackTraceStringFlatten(e));

				Log.out.println("Error connecting to " + mCatalog+", "+mHost + ", " + mLogin);

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
	protected void destroy(PooledConnection aPooledConnection)
	{
		try
		{
			Connection conn = aPooledConnection.getConnection();

			if (conn != null)
			{
				if (mLog != null) mLog.println("ConnectionPool: Closing connection");

				conn.close();
			}
		}
		catch (SQLException e)
		{
			if (mLog != null) mLog.println("ConnectionPool: Error: " + Log.getStackTraceStringFlatten(e));

			throw new RuntimeException(e);
		}
	}


	@Override
	protected boolean prepare(PooledConnection aPooledConnection)
	{
		try
		{
			if (aPooledConnection.getConnection().isClosed())
			{
				return false;
			}

			Connection conn = aPooledConnection.getConnection();

			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.clearWarnings();
			}

			return aPooledConnection.isValid(5);
		}
		catch (SQLException e)
		{
			mLog.println("ConnectionPool: Error: " + Log.getStackTraceStringFlatten(e));

			return false;
		}
	}


	@Override
	protected boolean reset(PooledConnection aConnection)
	{
		try
		{
			if (aConnection.isClosed()) // uggly code because of validation bug in netbeans
			{
				return false;
			}
			return aConnection.getWarnings() == null;
		}
		catch (Throwable e)
		{
			mLog.println("ConnectionPool: Error: " + Log.getStackTraceStringFlatten(e));

			return false;
		}
	}
}

package org.terifan.util.log;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import org.terifan.util.Calendar;
import static org.terifan.util.CommonTools.close;


// TODO: batch insert log rows, row count parameter, timeout parameter
// TODO: cache database connection, timeout parameter
// TODO: method for creating connection
// TODO: method for creating insert statement
public class DatabaseTarget implements LogTarget
{
	private String mDriver;
	private String mProtocol;
	private String mHost;
	private String mFile;
	private String mUser;
	private String mPassword;
	private String mTable;


	public DatabaseTarget(String aDriver, String aProtocol, String aHost, String aFile, String aUser, String aPassword, String aTable)
	{
		mDriver = aDriver;
		mProtocol = aProtocol;
		mHost = aHost;
		mFile = aFile;
		mUser = aUser;
		mPassword = aPassword;
		mTable = aTable;
	}


	@Override
	public void writeLogEntry(Date aDateTime, LogLevel aLogLevel, String aTag, String aMessage, Throwable aThrowable)
	{
		try
		{
			Connection connection = null;
			PreparedStatement statement = null;
			try
			{
				Class.forName(mDriver);

				connection = DriverManager.getConnection(mProtocol + "://" + mHost + "/" + mFile, mUser, mPassword);

				statement = connection.prepareStatement("insert " + mTable + " (datetime,tag,level,message,exception) values(?,?,?,?,?)");
				statement.setString(1, Calendar.format(aDateTime.getTime()));
				statement.setString(2, aTag);
				statement.setString(3, aLogLevel.name());
				statement.setString(4, aMessage);
				statement.setString(5, aThrowable == null ? null : Log.getStackTraceString(aThrowable));
				statement.executeUpdate();
			}
			finally
			{
				close(statement, connection);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.err);
		}
	}
}

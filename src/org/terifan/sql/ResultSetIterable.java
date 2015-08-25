package org.terifan.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;


public class ResultSetIterable implements Iterable<ResultSet>, AutoCloseable
{
	private ResultSet mResultSet;
	private Statement mStatement;


	ResultSetIterable(Statement aStatement, ResultSet aResultSet)
	{
		mResultSet = aResultSet;
		mStatement = aStatement;
	}


	@Override
	public void close() throws SQLException
	{
		if (mResultSet != null)
		{
			mResultSet.close();
			mResultSet = null;
		}
		if (mStatement != null)
		{
			mStatement.close();
			mStatement = null;
		}
	}


	@Override
	public Iterator<ResultSet> iterator()
	{
		return new Iterator<ResultSet>()
		{
			@Override
			public boolean hasNext()
			{
				try
				{
					boolean b = mResultSet.next();
					if (!b)
					{
						close();
					}
					return b;
				}
				catch (SQLException e)
				{
					throw new IllegalStateException(e);
				}
			}


			@Override
			public ResultSet next()
			{
				return mResultSet;
			}


			@Override
			public void remove()
			{
				throw new UnsupportedOperationException("Not supported.");
			}
		};
	}
}

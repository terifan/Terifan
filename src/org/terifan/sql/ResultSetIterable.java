package org.terifan.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;


public class ResultSetIterable implements Iterable<ResultSet>, AutoCloseable
{
	private ResultSet mResultSet;


	ResultSetIterable(ResultSet aResultSet)
	{
		mResultSet = aResultSet;
	}


	@Override
	public void close() throws SQLException
	{
		if (mResultSet != null)
		{
			mResultSet.close();
			mResultSet = null;
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

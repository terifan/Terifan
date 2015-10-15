package org.terifan.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.terifan.util.log.Log;


public class PooledResultSet implements ResultSet, AutoCloseable
{
	private final static boolean VERBOSE = true;

	private ResultSet mResultSet;
	private PooledConnection mConnection;


	public PooledResultSet(PooledConnection aConnection, ResultSet aResultSet)
	{
		mResultSet = aResultSet;
		mConnection = aConnection;

		if (VERBOSE) Log.out.println("PooledResultSet: created instance");
	}


	@Override
	public void close() throws SQLException
	{
		SQLException ex = null;

		try
		{
			Statement statement = mResultSet.getStatement();
			if (statement != null)
			{
				if (VERBOSE) Log.out.println("PooledResultSet: closing statement");
				statement.close();
			}
			else
			{
				Log.out.println("PooledResultSet: warning: ResultSet statement was null.");
			}
		}
		catch (SQLException e)
		{
			ex = e;
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}

		try
		{
			if (VERBOSE) Log.out.println("PooledResultSet: closing connection");
			mConnection.close();
		}
		catch (SQLException e)
		{
			ex = e;
		}
		catch (Exception e)
		{
			e.printStackTrace(Log.out);
		}

		if (ex != null)
		{
			throw ex;
		}
	}





	@Override
	public boolean next() throws SQLException
	{
		return mResultSet.next();
	}


	@Override
	public boolean wasNull() throws SQLException
	{
		return mResultSet.wasNull();
	}


	@Override
	public String getString(int aColumnIndex) throws SQLException
	{
		return mResultSet.getString(aColumnIndex);
	}


	@Override
	public boolean getBoolean(int aColumnIndex) throws SQLException
	{
		return mResultSet.getBoolean(aColumnIndex);
	}


	@Override
	public byte getByte(int aColumnIndex) throws SQLException
	{
		return mResultSet.getByte(aColumnIndex);
	}


	@Override
	public short getShort(int aColumnIndex) throws SQLException
	{
		return mResultSet.getShort(aColumnIndex);
	}


	@Override
	public int getInt(int aColumnIndex) throws SQLException
	{
		return mResultSet.getInt(aColumnIndex);
	}


	@Override
	public long getLong(int aColumnIndex) throws SQLException
	{
		return mResultSet.getLong(aColumnIndex);
	}


	@Override
	public float getFloat(int aColumnIndex) throws SQLException
	{
		return mResultSet.getFloat(aColumnIndex);
	}


	@Override
	public double getDouble(int aColumnIndex) throws SQLException
	{
		return mResultSet.getDouble(aColumnIndex);
	}


	@Override
	public BigDecimal getBigDecimal(int aColumnIndex, int aScale) throws SQLException
	{
		return mResultSet.getBigDecimal(aColumnIndex, aScale);
	}


	@Override
	public byte[] getBytes(int aColumnIndex) throws SQLException
	{
		return mResultSet.getBytes(aColumnIndex);
	}


	@Override
	public Date getDate(int aColumnIndex) throws SQLException
	{
		return mResultSet.getDate(aColumnIndex);
	}


	@Override
	public Time getTime(int aColumnIndex) throws SQLException
	{
		return mResultSet.getTime(aColumnIndex);
	}


	@Override
	public Timestamp getTimestamp(int aColumnIndex) throws SQLException
	{
		return mResultSet.getTimestamp(aColumnIndex);
	}


	@Override
	public InputStream getAsciiStream(int aColumnIndex) throws SQLException
	{
		return mResultSet.getAsciiStream(aColumnIndex);
	}


	@Override
	public InputStream getUnicodeStream(int aColumnIndex) throws SQLException
	{
		return mResultSet.getUnicodeStream(aColumnIndex);
	}


	@Override
	public InputStream getBinaryStream(int aColumnIndex) throws SQLException
	{
		return mResultSet.getBinaryStream(aColumnIndex);
	}


	@Override
	public String getString(String aColumnLabel) throws SQLException
	{
		return mResultSet.getString(aColumnLabel);
	}


	@Override
	public boolean getBoolean(String aColumnLabel) throws SQLException
	{
		return mResultSet.getBoolean(aColumnLabel);
	}


	@Override
	public byte getByte(String aColumnLabel) throws SQLException
	{
		return mResultSet.getByte(aColumnLabel);
	}


	@Override
	public short getShort(String aColumnLabel) throws SQLException
	{
		return mResultSet.getShort(aColumnLabel);
	}


	@Override
	public int getInt(String aColumnLabel) throws SQLException
	{
		return mResultSet.getInt(aColumnLabel);
	}


	@Override
	public long getLong(String aColumnLabel) throws SQLException
	{
		return mResultSet.getLong(aColumnLabel);
	}


	@Override
	public float getFloat(String aColumnLabel) throws SQLException
	{
		return mResultSet.getFloat(aColumnLabel);
	}


	@Override
	public double getDouble(String aColumnLabel) throws SQLException
	{
		return mResultSet.getDouble(aColumnLabel);
	}


	@Override
	public BigDecimal getBigDecimal(String aColumnLabel, int aScale) throws SQLException
	{
		return mResultSet.getBigDecimal(aColumnLabel, aScale);
	}


	@Override
	public byte[] getBytes(String aColumnLabel) throws SQLException
	{
		return mResultSet.getBytes(aColumnLabel);
	}


	@Override
	public Date getDate(String aColumnLabel) throws SQLException
	{
		return mResultSet.getDate(aColumnLabel);
	}


	@Override
	public Time getTime(String aColumnLabel) throws SQLException
	{
		return mResultSet.getTime(aColumnLabel);
	}


	@Override
	public Timestamp getTimestamp(String aColumnLabel) throws SQLException
	{
		return mResultSet.getTimestamp(aColumnLabel);
	}


	@Override
	public InputStream getAsciiStream(String aColumnLabel) throws SQLException
	{
		return mResultSet.getAsciiStream(aColumnLabel);
	}


	@Override
	public InputStream getUnicodeStream(String aColumnLabel) throws SQLException
	{
		return mResultSet.getUnicodeStream(aColumnLabel);
	}


	@Override
	public InputStream getBinaryStream(String aColumnLabel) throws SQLException
	{
		return mResultSet.getBinaryStream(aColumnLabel);
	}


	@Override
	public SQLWarning getWarnings() throws SQLException
	{
		return mResultSet.getWarnings();
	}


	@Override
	public void clearWarnings() throws SQLException
	{
		mResultSet.clearWarnings();
	}


	@Override
	public String getCursorName() throws SQLException
	{
		return mResultSet.getCursorName();
	}


	@Override
	public ResultSetMetaData getMetaData() throws SQLException
	{
		return mResultSet.getMetaData();
	}


	@Override
	public Object getObject(int aColumnIndex) throws SQLException
	{
		return mResultSet.getObject(aColumnIndex);
	}


	@Override
	public Object getObject(String aColumnLabel) throws SQLException
	{
		return mResultSet.getObject(aColumnLabel);
	}


	@Override
	public int findColumn(String aColumnLabel) throws SQLException
	{
		return mResultSet.findColumn(aColumnLabel);
	}


	@Override
	public Reader getCharacterStream(int aColumnIndex) throws SQLException
	{
		return mResultSet.getCharacterStream(aColumnIndex);
	}


	@Override
	public Reader getCharacterStream(String aColumnLabel) throws SQLException
	{
		return mResultSet.getCharacterStream(aColumnLabel);
	}


	@Override
	public BigDecimal getBigDecimal(int aColumnIndex) throws SQLException
	{
		return mResultSet.getBigDecimal(aColumnIndex);
	}


	@Override
	public BigDecimal getBigDecimal(String aColumnLabel) throws SQLException
	{
		return mResultSet.getBigDecimal(aColumnLabel);
	}


	@Override
	public boolean isBeforeFirst() throws SQLException
	{
		return mResultSet.isBeforeFirst();
	}


	@Override
	public boolean isAfterLast() throws SQLException
	{
		return mResultSet.isAfterLast();
	}


	@Override
	public boolean isFirst() throws SQLException
	{
		return mResultSet.isFirst();
	}


	@Override
	public boolean isLast() throws SQLException
	{
		return mResultSet.isLast();
	}


	@Override
	public void beforeFirst() throws SQLException
	{
		mResultSet.beforeFirst();
	}


	@Override
	public void afterLast() throws SQLException
	{
		mResultSet.afterLast();
	}


	@Override
	public boolean first() throws SQLException
	{
		return mResultSet.first();
	}


	@Override
	public boolean last() throws SQLException
	{
		return mResultSet.last();
	}


	@Override
	public int getRow() throws SQLException
	{
		return mResultSet.getRow();
	}


	@Override
	public boolean absolute(int aRow) throws SQLException
	{
		return mResultSet.absolute(aRow);
	}


	@Override
	public boolean relative(int aRows) throws SQLException
	{
		return mResultSet.relative(aRows);
	}


	@Override
	public boolean previous() throws SQLException
	{
		return mResultSet.previous();
	}


	@Override
	public void setFetchDirection(int aDirection) throws SQLException
	{
		mResultSet.setFetchDirection(aDirection);
	}


	@Override
	public int getFetchDirection() throws SQLException
	{
		return mResultSet.getFetchDirection();
	}


	@Override
	public void setFetchSize(int aRows) throws SQLException
	{
		mResultSet.setFetchSize(aRows);
	}


	@Override
	public int getFetchSize() throws SQLException
	{
		return mResultSet.getFetchSize();
	}


	@Override
	public int getType() throws SQLException
	{
		return mResultSet.getType();
	}


	@Override
	public int getConcurrency() throws SQLException
	{
		return mResultSet.getConcurrency();
	}


	@Override
	public boolean rowUpdated() throws SQLException
	{
		return mResultSet.rowUpdated();
	}


	@Override
	public boolean rowInserted() throws SQLException
	{
		return mResultSet.rowInserted();
	}


	@Override
	public boolean rowDeleted() throws SQLException
	{
		return mResultSet.rowDeleted();
	}


	@Override
	public void updateNull(int aColumnIndex) throws SQLException
	{
		mResultSet.updateNull(aColumnIndex);
	}


	@Override
	public void updateBoolean(int aColumnIndex, boolean aX) throws SQLException
	{
		mResultSet.updateBoolean(aColumnIndex, aX);
	}


	@Override
	public void updateByte(int aColumnIndex, byte aX) throws SQLException
	{
		mResultSet.updateByte(aColumnIndex, aX);
	}


	@Override
	public void updateShort(int aColumnIndex, short aX) throws SQLException
	{
		mResultSet.updateShort(aColumnIndex, aX);
	}


	@Override
	public void updateInt(int aColumnIndex, int aX) throws SQLException
	{
		mResultSet.updateInt(aColumnIndex, aX);
	}


	@Override
	public void updateLong(int aColumnIndex, long aX) throws SQLException
	{
		mResultSet.updateLong(aColumnIndex, aX);
	}


	@Override
	public void updateFloat(int aColumnIndex, float aX) throws SQLException
	{
		mResultSet.updateFloat(aColumnIndex, aX);
	}


	@Override
	public void updateDouble(int aColumnIndex, double aX) throws SQLException
	{
		mResultSet.updateDouble(aColumnIndex, aX);
	}


	@Override
	public void updateBigDecimal(int aColumnIndex, BigDecimal aX) throws SQLException
	{
		mResultSet.updateBigDecimal(aColumnIndex, aX);
	}


	@Override
	public void updateString(int aColumnIndex, String aX) throws SQLException
	{
		mResultSet.updateString(aColumnIndex, aX);
	}


	@Override
	public void updateBytes(int aColumnIndex, byte[] aX) throws SQLException
	{
		mResultSet.updateBytes(aColumnIndex, aX);
	}


	@Override
	public void updateDate(int aColumnIndex, Date aX) throws SQLException
	{
		mResultSet.updateDate(aColumnIndex, aX);
	}


	@Override
	public void updateTime(int aColumnIndex, Time aX) throws SQLException
	{
		mResultSet.updateTime(aColumnIndex, aX);
	}


	@Override
	public void updateTimestamp(int aColumnIndex, Timestamp aX) throws SQLException
	{
		mResultSet.updateTimestamp(aColumnIndex, aX);
	}


	@Override
	public void updateAsciiStream(int aColumnIndex, InputStream aX, int aLength) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateBinaryStream(int aColumnIndex, InputStream aX, int aLength) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateCharacterStream(int aColumnIndex, Reader aX, int aLength) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateObject(int aColumnIndex, Object aX, int aScaleOrLength) throws SQLException
	{
		mResultSet.updateObject(aColumnIndex, aX, aScaleOrLength);
	}


	@Override
	public void updateObject(int aColumnIndex, Object aX) throws SQLException
	{
		mResultSet.updateObject(aColumnIndex, aX);
	}


	@Override
	public void updateNull(String aColumnLabel) throws SQLException
	{
		mResultSet.updateNull(aColumnLabel);
	}


	@Override
	public void updateBoolean(String aColumnLabel, boolean aX) throws SQLException
	{
		mResultSet.updateBoolean(aColumnLabel, aX);
	}


	@Override
	public void updateByte(String aColumnLabel, byte aX) throws SQLException
	{
		mResultSet.updateByte(aColumnLabel, aX);
	}


	@Override
	public void updateShort(String aColumnLabel, short aX) throws SQLException
	{
		mResultSet.updateShort(aColumnLabel, aX);
	}


	@Override
	public void updateInt(String aColumnLabel, int aX) throws SQLException
	{
		mResultSet.updateInt(aColumnLabel, aX);
	}


	@Override
	public void updateLong(String aColumnLabel, long aX) throws SQLException
	{
		mResultSet.updateLong(aColumnLabel, aX);
	}


	@Override
	public void updateFloat(String aColumnLabel, float aX) throws SQLException
	{
		mResultSet.updateFloat(aColumnLabel, aX);
	}


	@Override
	public void updateDouble(String aColumnLabel, double aX) throws SQLException
	{
		mResultSet.updateDouble(aColumnLabel, aX);
	}


	@Override
	public void updateBigDecimal(String aColumnLabel, BigDecimal aX) throws SQLException
	{
		mResultSet.updateBigDecimal(aColumnLabel, aX);
	}


	@Override
	public void updateString(String aColumnLabel, String aX) throws SQLException
	{
		mResultSet.updateString(aColumnLabel, aX);
	}


	@Override
	public void updateBytes(String aColumnLabel, byte[] aX) throws SQLException
	{
		mResultSet.updateBytes(aColumnLabel, aX);
	}


	@Override
	public void updateDate(String aColumnLabel, Date aX) throws SQLException
	{
		mResultSet.updateDate(aColumnLabel, aX);
	}


	@Override
	public void updateTime(String aColumnLabel, Time aX) throws SQLException
	{
		mResultSet.updateTime(aColumnLabel, aX);
	}


	@Override
	public void updateTimestamp(String aColumnLabel, Timestamp aX) throws SQLException
	{
		mResultSet.updateTimestamp(aColumnLabel, aX);
	}


	@Override
	public void updateAsciiStream(String aColumnLabel, InputStream aX, int aLength) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnLabel, aX, aLength);
	}


	@Override
	public void updateBinaryStream(String aColumnLabel, InputStream aX, int aLength) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnLabel, aX, aLength);
	}


	@Override
	public void updateCharacterStream(String aColumnLabel, Reader aReader, int aLength) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnLabel, aReader, aLength);
	}


	@Override
	public void updateObject(String aColumnLabel, Object aX, int aScaleOrLength) throws SQLException
	{
		mResultSet.updateObject(aColumnLabel, aX, aScaleOrLength);
	}


	@Override
	public void updateObject(String aColumnLabel, Object aX) throws SQLException
	{
		mResultSet.updateObject(aColumnLabel, aX);
	}


	@Override
	public void insertRow() throws SQLException
	{
		mResultSet.insertRow();
	}


	@Override
	public void updateRow() throws SQLException
	{
		mResultSet.updateRow();
	}


	@Override
	public void deleteRow() throws SQLException
	{
		mResultSet.deleteRow();
	}


	@Override
	public void refreshRow() throws SQLException
	{
		mResultSet.refreshRow();
	}


	@Override
	public void cancelRowUpdates() throws SQLException
	{
		mResultSet.cancelRowUpdates();
	}


	@Override
	public void moveToInsertRow() throws SQLException
	{
		mResultSet.moveToInsertRow();
	}


	@Override
	public void moveToCurrentRow() throws SQLException
	{
		mResultSet.moveToCurrentRow();
	}


	@Override
	public Statement getStatement() throws SQLException
	{
		return mResultSet.getStatement();
	}


	@Override
	public Object getObject(int aColumnIndex, Map<String, Class<?>> aMap) throws SQLException
	{
		return mResultSet.getObject(aColumnIndex, aMap);
	}


	@Override
	public Ref getRef(int aColumnIndex) throws SQLException
	{
		return mResultSet.getRef(aColumnIndex);
	}


	@Override
	public Blob getBlob(int aColumnIndex) throws SQLException
	{
		return mResultSet.getBlob(aColumnIndex);
	}


	@Override
	public Clob getClob(int aColumnIndex) throws SQLException
	{
		return mResultSet.getClob(aColumnIndex);
	}


	@Override
	public Array getArray(int aColumnIndex) throws SQLException
	{
		return mResultSet.getArray(aColumnIndex);
	}


	@Override
	public Object getObject(String aColumnLabel, Map<String, Class<?>> aMap) throws SQLException
	{
		return mResultSet.getObject(aColumnLabel, aMap);
	}


	@Override
	public Ref getRef(String aColumnLabel) throws SQLException
	{
		return mResultSet.getRef(aColumnLabel);
	}


	@Override
	public Blob getBlob(String aColumnLabel) throws SQLException
	{
		return mResultSet.getBlob(aColumnLabel);
	}


	@Override
	public Clob getClob(String aColumnLabel) throws SQLException
	{
		return mResultSet.getClob(aColumnLabel);
	}


	@Override
	public Array getArray(String aColumnLabel) throws SQLException
	{
		return mResultSet.getArray(aColumnLabel);
	}


	@Override
	public Date getDate(int aColumnIndex, Calendar aCal) throws SQLException
	{
		return mResultSet.getDate(aColumnIndex, aCal);
	}


	@Override
	public Date getDate(String aColumnLabel, Calendar aCal) throws SQLException
	{
		return mResultSet.getDate(aColumnLabel, aCal);
	}


	@Override
	public Time getTime(int aColumnIndex, Calendar aCal) throws SQLException
	{
		return mResultSet.getTime(aColumnIndex, aCal);
	}


	@Override
	public Time getTime(String aColumnLabel, Calendar aCal) throws SQLException
	{
		return mResultSet.getTime(aColumnLabel, aCal);
	}


	@Override
	public Timestamp getTimestamp(int aColumnIndex, Calendar aCal) throws SQLException
	{
		return mResultSet.getTimestamp(aColumnIndex, aCal);
	}


	@Override
	public Timestamp getTimestamp(String aColumnLabel, Calendar aCal) throws SQLException
	{
		return mResultSet.getTimestamp(aColumnLabel, aCal);
	}


	@Override
	public URL getURL(int aColumnIndex) throws SQLException
	{
		return mResultSet.getURL(aColumnIndex);
	}


	@Override
	public URL getURL(String aColumnLabel) throws SQLException
	{
		return mResultSet.getURL(aColumnLabel);
	}


	@Override
	public void updateRef(int aColumnIndex, Ref aX) throws SQLException
	{
		mResultSet.updateRef(aColumnIndex, aX);
	}


	@Override
	public void updateRef(String aColumnLabel, Ref aX) throws SQLException
	{
		mResultSet.updateRef(aColumnLabel, aX);
	}


	@Override
	public void updateBlob(int aColumnIndex, Blob aX) throws SQLException
	{
		mResultSet.updateBlob(aColumnIndex, aX);
	}


	@Override
	public void updateBlob(String aColumnLabel, Blob aX) throws SQLException
	{
		mResultSet.updateBlob(aColumnLabel, aX);
	}


	@Override
	public void updateClob(int aColumnIndex, Clob aX) throws SQLException
	{
		mResultSet.updateClob(aColumnIndex, aX);
	}


	@Override
	public void updateClob(String aColumnLabel, Clob aX) throws SQLException
	{
		mResultSet.updateClob(aColumnLabel, aX);
	}


	@Override
	public void updateArray(int aColumnIndex, Array aX) throws SQLException
	{
		mResultSet.updateArray(aColumnIndex, aX);
	}


	@Override
	public void updateArray(String aColumnLabel, Array aX) throws SQLException
	{
		mResultSet.updateArray(aColumnLabel, aX);
	}


	@Override
	public RowId getRowId(int aColumnIndex) throws SQLException
	{
		return mResultSet.getRowId(aColumnIndex);
	}


	@Override
	public RowId getRowId(String aColumnLabel) throws SQLException
	{
		return mResultSet.getRowId(aColumnLabel);
	}


	@Override
	public void updateRowId(int aColumnIndex, RowId aX) throws SQLException
	{
		mResultSet.updateRowId(aColumnIndex, aX);
	}


	@Override
	public void updateRowId(String aColumnLabel, RowId aX) throws SQLException
	{
		mResultSet.updateRowId(aColumnLabel, aX);
	}


	@Override
	public int getHoldability() throws SQLException
	{
		return mResultSet.getHoldability();
	}


	@Override
	public boolean isClosed() throws SQLException
	{
		return mResultSet.isClosed();
	}


	@Override
	public void updateNString(int aColumnIndex, String anString) throws SQLException
	{
		mResultSet.updateNString(aColumnIndex, anString);
	}


	@Override
	public void updateNString(String aColumnLabel, String anString) throws SQLException
	{
		mResultSet.updateNString(aColumnLabel, anString);
	}


	@Override
	public void updateNClob(int aColumnIndex, NClob anClob) throws SQLException
	{
		mResultSet.updateNClob(aColumnIndex, anClob);
	}


	@Override
	public void updateNClob(String aColumnLabel, NClob anClob) throws SQLException
	{
		mResultSet.updateNClob(aColumnLabel, anClob);
	}


	@Override
	public NClob getNClob(int aColumnIndex) throws SQLException
	{
		return mResultSet.getNClob(aColumnIndex);
	}


	@Override
	public NClob getNClob(String aColumnLabel) throws SQLException
	{
		return mResultSet.getNClob(aColumnLabel);
	}


	@Override
	public SQLXML getSQLXML(int aColumnIndex) throws SQLException
	{
		return mResultSet.getSQLXML(aColumnIndex);
	}


	@Override
	public SQLXML getSQLXML(String aColumnLabel) throws SQLException
	{
		return mResultSet.getSQLXML(aColumnLabel);
	}


	@Override
	public void updateSQLXML(int aColumnIndex, SQLXML aXmlObject) throws SQLException
	{
		mResultSet.updateSQLXML(aColumnIndex, aXmlObject);
	}


	@Override
	public void updateSQLXML(String aColumnLabel, SQLXML aXmlObject) throws SQLException
	{
		mResultSet.updateSQLXML(aColumnLabel, aXmlObject);
	}


	@Override
	public String getNString(int aColumnIndex) throws SQLException
	{
		return mResultSet.getNString(aColumnIndex);
	}


	@Override
	public String getNString(String aColumnLabel) throws SQLException
	{
		return mResultSet.getNString(aColumnLabel);
	}


	@Override
	public Reader getNCharacterStream(int aColumnIndex) throws SQLException
	{
		return mResultSet.getNCharacterStream(aColumnIndex);
	}


	@Override
	public Reader getNCharacterStream(String aColumnLabel) throws SQLException
	{
		return mResultSet.getNCharacterStream(aColumnLabel);
	}


	@Override
	public void updateNCharacterStream(int aColumnIndex, Reader aX, long aLength) throws SQLException
	{
		mResultSet.updateNCharacterStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateNCharacterStream(String aColumnLabel, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateNCharacterStream(aColumnLabel, aReader, aLength);
	}


	@Override
	public void updateAsciiStream(int aColumnIndex, InputStream aX, long aLength) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateBinaryStream(int aColumnIndex, InputStream aX, long aLength) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateCharacterStream(int aColumnIndex, Reader aX, long aLength) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnIndex, aX, aLength);
	}


	@Override
	public void updateAsciiStream(String aColumnLabel, InputStream aX, long aLength) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnLabel, aX, aLength);
	}


	@Override
	public void updateBinaryStream(String aColumnLabel, InputStream aX, long aLength) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnLabel, aX, aLength);
	}


	@Override
	public void updateCharacterStream(String aColumnLabel, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnLabel, aReader, aLength);
	}


	@Override
	public void updateBlob(int aColumnIndex, InputStream aInputStream, long aLength) throws SQLException
	{
		mResultSet.updateBlob(aColumnIndex, aInputStream, aLength);
	}


	@Override
	public void updateBlob(String aColumnLabel, InputStream aInputStream, long aLength) throws SQLException
	{
		mResultSet.updateBlob(aColumnLabel, aInputStream, aLength);
	}


	@Override
	public void updateClob(int aColumnIndex, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateClob(aColumnIndex, aReader, aLength);
	}


	@Override
	public void updateClob(String aColumnLabel, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateClob(aColumnLabel, aReader, aLength);
	}


	@Override
	public void updateNClob(int aColumnIndex, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateNClob(aColumnIndex, aReader, aLength);
	}


	@Override
	public void updateNClob(String aColumnLabel, Reader aReader, long aLength) throws SQLException
	{
		mResultSet.updateNClob(aColumnLabel, aReader, aLength);
	}


	@Override
	public void updateNCharacterStream(int aColumnIndex, Reader aX) throws SQLException
	{
		mResultSet.updateNCharacterStream(aColumnIndex, aX);
	}


	@Override
	public void updateNCharacterStream(String aColumnLabel, Reader aReader) throws SQLException
	{
		mResultSet.updateNCharacterStream(aColumnLabel, aReader);
	}


	@Override
	public void updateAsciiStream(int aColumnIndex, InputStream aX) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnIndex, aX);
	}


	@Override
	public void updateBinaryStream(int aColumnIndex, InputStream aX) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnIndex, aX);
	}


	@Override
	public void updateCharacterStream(int aColumnIndex, Reader aX) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnIndex, aX);
	}


	@Override
	public void updateAsciiStream(String aColumnLabel, InputStream aX) throws SQLException
	{
		mResultSet.updateAsciiStream(aColumnLabel, aX);
	}


	@Override
	public void updateBinaryStream(String aColumnLabel, InputStream aX) throws SQLException
	{
		mResultSet.updateBinaryStream(aColumnLabel, aX);
	}


	@Override
	public void updateCharacterStream(String aColumnLabel, Reader aReader) throws SQLException
	{
		mResultSet.updateCharacterStream(aColumnLabel, aReader);
	}


	@Override
	public void updateBlob(int aColumnIndex, InputStream aInputStream) throws SQLException
	{
		mResultSet.updateBlob(aColumnIndex, aInputStream);
	}


	@Override
	public void updateBlob(String aColumnLabel, InputStream aInputStream) throws SQLException
	{
		mResultSet.updateBlob(aColumnLabel, aInputStream);
	}


	@Override
	public void updateClob(int aColumnIndex, Reader aReader) throws SQLException
	{
		mResultSet.updateClob(aColumnIndex, aReader);
	}


	@Override
	public void updateClob(String aColumnLabel, Reader aReader) throws SQLException
	{
		mResultSet.updateClob(aColumnLabel, aReader);
	}


	@Override
	public void updateNClob(int aColumnIndex, Reader aReader) throws SQLException
	{
		mResultSet.updateNClob(aColumnIndex, aReader);
	}


	@Override
	public void updateNClob(String aColumnLabel, Reader aReader) throws SQLException
	{
		mResultSet.updateNClob(aColumnLabel, aReader);
	}


	@Override
	public <T> T getObject(int aColumnIndex, Class<T> aType) throws SQLException
	{
		return mResultSet.getObject(aColumnIndex, aType);
	}


	@Override
	public <T> T getObject(String aColumnLabel, Class<T> aType) throws SQLException
	{
		return mResultSet.getObject(aColumnLabel, aType);
	}


	@Override
	public void updateObject(int aColumnIndex, Object aX, SQLType aTargetSqlType, int aScaleOrLength) throws SQLException
	{
		mResultSet.updateObject(aColumnIndex, aX, aTargetSqlType, aScaleOrLength);
	}


	@Override
	public void updateObject(String aColumnLabel, Object aX, SQLType aTargetSqlType, int aScaleOrLength) throws SQLException
	{
		mResultSet.updateObject(aColumnLabel, aX, aTargetSqlType, aScaleOrLength);
	}


	@Override
	public void updateObject(int aColumnIndex, Object aX, SQLType aTargetSqlType) throws SQLException
	{
		mResultSet.updateObject(aColumnIndex, aX, aTargetSqlType);
	}


	@Override
	public void updateObject(String aColumnLabel, Object aX, SQLType aTargetSqlType) throws SQLException
	{
		mResultSet.updateObject(aColumnLabel, aX, aTargetSqlType);
	}


	@Override
	public boolean isWrapperFor(Class<?> aIface) throws SQLException
	{
		return mResultSet.isWrapperFor(aIface);
	}


	@Override
	public <T> T unwrap(Class<T> aIface) throws SQLException
	{
		return mResultSet.unwrap(aIface);
	}
}

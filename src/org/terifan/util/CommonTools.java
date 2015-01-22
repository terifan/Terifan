package org.terifan.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.UIManager;


/**
 * This is intended to contain common tools that can be imported by other
 * classes.
 */
public final class CommonTools
{
    private CommonTools()
    {
    }


    /**
     * Closes all objects provided assuming they are either implementors of the
     * Closable interface or Connections, Statements or ResultSets.<p>
     *
     * Note: this method will attempt to close all objects provided and in the
     * event of an exception, the exception will be thrown upon completion. Only
     * a single exception can be thrown.
     *
     * @param aObjects
     *    one or more objects to close.
     * @throws RuntimeException
     *    if one or more exceptions occur.
     */
	public static void close(Object ... aObjects)
	{
		RuntimeException error = null;

		for (int i = 0; i < aObjects.length; i++)
		{
			Object o = aObjects[i];
			try
			{
				if (o instanceof AutoCloseable)
				{
					((AutoCloseable)o).close();
				}
                else if (o instanceof Connection)
				{
					((Connection)o).close();
				}
				else if (o instanceof Statement)
				{
					((Statement)o).close();
				}
				else if (o instanceof ResultSet)
				{
					((ResultSet)o).close();
				}
				else if (o != null)
				{
					error = new RuntimeException("Unsupported object to close: index: " + i + ", class: " + o.getClass().getName());
				}
			}
			catch (RuntimeException e)
			{
				error = e;
			}
			catch (Throwable e)
			{
				error = new RuntimeException(e);
			}
		}

		if (error != null)
		{
			throw error;
		}
	}


	/**
	 * Set the current Swing UI to the SystemLookAndFeel.
	 */
	public static void setSystemLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}
	}
}
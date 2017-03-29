package org.terifan.util;

import java.io.File;


public final class SystemPaths
{
	/**
	 * Creates a directory in environments temp directory. Directory will be named as provided application name and suffix "TemporaryDirectory".
	 *
	 * @param aApplicationName
	 *   the name of the application running.
	 * @return
	 *   the temporary directory
	 */
	public static File getTempDirectory(String aApplicationName)
	{
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		if (!tempDir.exists())
		{
			throw new IllegalStateException("No temporary directory exists in this environment.");
		}

		tempDir = new File(tempDir.getAbsolutePath(), aApplicationName + "TemporaryDirectory");
		tempDir.mkdir();

		if (!tempDir.exists())
		{
			throw new IllegalStateException("Failed to create temporary directory at " + tempDir.getAbsolutePath());
		}

		return tempDir;
	}


	/**
	 * Creates a directory in environments user directory. Directory will be named as provided application name.
	 *
	 * @param aApplicationName
	 *   the name of the application running.
	 * @return
	 *   the application directory
	 */
	public static File getAppDirectory(String aApplicationName)
	{
		File tempDir = new File(System.getProperty("user.home"));
		if (!tempDir.exists())
		{
			throw new IllegalStateException("User diretory not found in this environment.");
		}

		tempDir = new File(tempDir.getAbsolutePath(), aApplicationName);
		tempDir.mkdir();

		if (!tempDir.exists())
		{
			throw new IllegalStateException("Failed to create application directory at " + tempDir.getAbsolutePath());
		}

		return tempDir;
	}
}

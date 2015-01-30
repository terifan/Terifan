package org.terifan.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;


/**
 * Utility class that simplifies stream handling. The transfer methods accept:
 * <p>
 * Input:
 * <ol>
 * <li>java.io.InputStream</li>
 * <li>java.io.File</li>
 * <li>java.lang.String (file path)</li>
 * <li>java.lang.CharSequence</li>
 * <li>java.net.URL</li>
 * <li>byte array</li>
 * <li>java.nio.ByteBuffer</li>
 * </ol>
 *
 * Output:
 * <ol>
 * <li>java.io.OutputStream</li>
 * <li>java.io.File</li>
 * <li>java.lang.String (file path)</li>
 * <li>java.net.URL</li>
 * <li>java.nio.ByteBuffer</li>
 * </ol>
 *
 * This sample will copy a file:
 *
 * <pre>
 * Streams.transfer("myfile.txt", "copy of myfile.txt"));
 * </pre>
 * </p>
 */
public final class Streams
{
	private Streams()
	{
	}


	/**
	 * Transfers bytes from the InputStream to the OutputStreams until end of stream is reached. Streams are closed when the transfer has
	 * completed.
	 *
	 * @param aInput an input to read from
	 * @param aOutput one or more outputs to write to
	 * @return number of bytes actually transfered.
	 */
	public static byte[] fetch(Object aInput) throws IOException
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transfer(aInput, output);
		return output.toByteArray();
	}


	/**
	 * Transfers bytes from the InputStream to the OutputStreams until end of stream is reached.
	 *
	 * @param aCloseInput if true, calls the closeInput method when transfer is complete.
	 * @param aCloseOutput if true, calls the closeOutput method when transfer is complete.
	 * @return number of bytes actually transfered.
	 */
	public static long transfer(Object aInput, Object aOutput) throws IOException
	{
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try
		{
			inputStream = createInputStream(aInput);
			outputStream = createOutputStream(aOutput);

			long total = 0;
			byte[] buffer = new byte[4096];

			for (;;)
			{
				int len = inputStream.read(buffer);

				if (len <= 0)
				{
					break;
				}

				outputStream.write(buffer, 0, len);

				total += len;
			}

			return total;
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (Throwable e)
				{
				}
			}
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (Throwable e)
				{
				}
			}
		}
	}


	private static InputStream createInputStream(Object aInput) throws IOException
	{
		if (aInput instanceof InputStream)
		{
			return (InputStream)aInput;
		}
		if (aInput instanceof URL)
		{
			return ((URL)aInput).openStream();
		}
		if (aInput instanceof File)
		{
			return new BufferedInputStream(new FileInputStream((File)aInput));
		}
		if (aInput instanceof String)
		{
			return new FileInputStream((String)aInput);
		}
		if (aInput instanceof CharSequence)
		{
			return new ByteArrayInputStream(((CharSequence)aInput).toString().getBytes());
		}
		if (aInput instanceof RandomAccessFile)
		{
			return new MyRandomAccessFileInputStream((RandomAccessFile)aInput);
		}
		if (aInput instanceof ByteBuffer)
		{
			return new ByteBufferInputStream((ByteBuffer)aInput);
		}
		if (aInput instanceof byte[])
		{
			return new ByteArrayInputStream((byte[])aInput);
		}

		throw new IOException("Unsupported input type: " + (aInput == null ? "" : aInput.getClass()));
	}


	private static OutputStream createOutputStream(Object aOutput) throws IOException
	{
		if (aOutput instanceof OutputStream)
		{
			return (OutputStream)aOutput;
		}
		if (aOutput instanceof File)
		{
			return new BufferedOutputStream(new FileOutputStream((File)aOutput));
		}
		if (aOutput instanceof String)
		{
			return new FileOutputStream((String)aOutput);
		}
		if (aOutput instanceof ByteBuffer)
		{
			return new ByteBufferOutputStream((ByteBuffer)aOutput);
		}
		if (aOutput instanceof byte[])
		{
			return new MyByteArrayOutputStream((byte[])aOutput);
		}

		throw new IOException("Unsupported output type: " + (aOutput == null ? "" : aOutput.getClass()));
	}


	private static class MyByteArrayOutputStream extends OutputStream
	{
		private byte[] mBuffer;
		private int mOffset;


		public MyByteArrayOutputStream(byte[] aBuffer)
		{
			mBuffer = aBuffer;
		}


		@Override
		public void write(int b) throws IOException
		{
			mBuffer[mOffset++] = (byte)b;
		}
	}


	private static class MyRandomAccessFileInputStream extends InputStream
	{
		private RandomAccessFile mFile;


		public MyRandomAccessFileInputStream(RandomAccessFile aFile)
		{
			mFile = aFile;
		}


		@Override
		public int read(byte[] b, int off, int len) throws IOException
		{
			return mFile.read(b, off, len);
		}


		@Override
		public int read() throws IOException
		{
			return mFile.read();
		}


		@Override
		public void close() throws IOException
		{
			mFile.close();
		}
	}
}

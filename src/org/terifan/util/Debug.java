package org.terifan.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import org.terifan.util.log.Log;


public class Debug
{
	public static void hexDump(Buffer aBuffer)
	{
		hexDump((byte[])aBuffer.array(), 0, aBuffer.limit());
	}


	public static void hexDump(byte [] aBuffer)
	{
		hexDump(aBuffer, 0, aBuffer.length);
	}


	public static void hexDump(int aWidth, byte [] aBuffer)
	{
		hexDump(aWidth, aBuffer, 0, aBuffer.length);
	}


	public static void hexDump(byte [] aBuffer, int aLength)
	{
		hexDump(aBuffer, 0, aLength);
	}


	public static void hexDump(byte [] aBuffer, int aOffset, int aLength)
	{
		hexDump(new ByteArrayInputStream(aBuffer, aOffset, aLength), aLength);
	}


	public static void hexDump(int aWidth, byte [] aBuffer, int aOffset, int aLength)
	{
		hexDump(aWidth, new ByteArrayInputStream(aBuffer, aOffset, aLength), aLength);
	}


	public static void hexDump(InputStream aInputStream, int aLength)
	{
		hexDump(32, aInputStream, aLength);
	}


	public static void hexDump(int aWidth, InputStream aInputStream, int aLength)
	{
		try
		{
			StringBuilder binText = new StringBuilder("");
			StringBuilder hexText = new StringBuilder("");

			for (int row = 0; row == 0 || aLength > 0; row++)
			{
				hexText.append(String.format("%04d: ", row * aWidth));

				int padding = 3 * aWidth + aWidth / 8;

				for (int i = 0; i < aWidth && aLength > 0; i++)
				{
					int c = aInputStream.read();

					if (c == -1)
					{
						aLength = 0;
						break;
					}

					hexText.append(String.format("%02x ", c));
					binText.append(Character.isISOControl(c) ? '.' : (char)c);
					padding -= 3;
					aLength--;

					if ((i & 7) == 7)
					{
						hexText.append(" ");
						padding--;
					}
				}

				for (int i = 0; i < padding; i++)
				{
					hexText.append(" ");
				}

				Log.out.println(hexText.append(binText).toString());

				binText.setLength(0);
				hexText.setLength(0);
			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
package org.terifan.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;


public class HexDumpBase
{
	private PrintStream mTo;
	private int mWidth;
	private int mLimit;


	HexDumpBase()
	{
		mLimit = 100_000;
		mWidth = 80;
	}


	public HexDumpBase limit(int aLimit)
	{
		mLimit = aLimit;
		return this;
	}


	public HexDumpBase width(int aWidth)
	{
		mWidth = aWidth;
		return this;
	}


	public HexDumpBase to(OutputStream aTo)
	{
		mTo = new PrintStream(aTo);
		return this;
	}


	public HexDumpBase to(Writer aTo)
	{
		mTo = new PrintStream(new OutputStream()
		{
			@Override
			public void write(int aByte) throws IOException
			{
				aTo.write(aByte);
			}
		});
		return this;
	}


	public void dump(Object aData)
	{
		if (aData instanceof InputStream)
		{
			dumpImpl((InputStream)aData);
		}
		else if (aData instanceof byte[])
		{
			dumpImpl(new ByteArrayInputStream((byte[])aData));
		}
	}


	private void dumpImpl(InputStream aInputStream)
	{
		try
		{
			StringBuilder binText = new StringBuilder("");
			StringBuilder hexText = new StringBuilder("");

			for (int row = 0, remaining = mLimit; row == 0 || remaining > 0; row++)
			{
				hexText.append(String.format("%04d: ", row * mWidth));

				int padding = 3 * mWidth + mWidth / 8;
				int col = 0;

				for (; col < mWidth && remaining > 0; col++)
				{
					int c = aInputStream.read();

					if (c == -1)
					{
						remaining = 0;
						break;
					}

					hexText.append(String.format("%02x ", c));
					binText.append(Character.isISOControl(c) ? '.' : (char)c);
					padding -= 3;
					remaining--;

					if ((col & 7) == 7)
					{
						hexText.append(" ");
						padding--;
					}
				}

				if (col > 0)
				{
					for (int i = 0; i < padding; i++)
					{
						hexText.append(" ");
					}

					mTo.println(hexText.append(binText).toString());
				}

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

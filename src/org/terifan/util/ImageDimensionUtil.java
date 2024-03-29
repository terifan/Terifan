package org.terifan.util;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageDimensionUtil
{
	public static Dimension getImageDimension(byte[] aBuffer) throws IOException
	{
		return getImageDimension(new ByteArrayInputStream(aBuffer));
	}


	public static Dimension getImageDimension(InputStream aInputStream) throws IOException
	{
		DataInput buffer = new DataInputStream(aInputStream);

		switch (0xFFFF & buffer.readShort())
		{
			case 0xFFD8:
				return getImageDimensionsJPEG(buffer);
			case 0x8950:
				return getImageDimensionsPNG(buffer);
			case 0x4749:
				return getImageDimensionsGIF(buffer);
			case 0x4241:
			case 0x424d:
			case 0x4349:
			case 0x4350:
			case 0x4943:
			case 0x5054:
				return getImageDimensionsBMP(buffer);
		}

		return null;
	}


	private static Dimension getImageDimensionsJPEG(DataInput aBuffer) throws IOException
	{
		for (;;)
		{
			int nextSegment = 0xFFFF & aBuffer.readShort();

			// deal with badly coded files, skip till next segment
			while ((nextSegment & 0xFF00) != 0xFF00)
			{
				nextSegment = ((0xFF & nextSegment) << 8) | (0xFF & aBuffer.readByte());
			}

			switch (nextSegment)
			{
				case 0xFFC0:
				case 0xFFC1:
				case 0xFFC2:
				case 0xFFC9:
				case 0xFFCA:
					aBuffer.skipBytes(3);

					int h = 0xFFFF & aBuffer.readShort();
					int w = 0xFFFF & aBuffer.readShort();

					return new Dimension(w, h);
				default:
					aBuffer.skipBytes((0xFFFF & aBuffer.readShort()) - 2);
			}
		}
	}


	private static Dimension getImageDimensionsPNG(DataInput aBuffer) throws IOException
	{
		if (aBuffer.readShort() != (short)0x4E47)
		{
			return null;
		}

		aBuffer.skipBytes(8);

		if (aBuffer.readInt() != 0x49484452)
		{
			return null;
		}

		int w = aBuffer.readInt();
		int h = aBuffer.readInt();

		return new Dimension(w, h);
	}


	private static Dimension getImageDimensionsGIF(DataInput aBuffer) throws IOException
	{
		if (aBuffer.readShort() != (short)0x4638)
		{
			return null;
		}

		aBuffer.skipBytes(2);

		int w = 0xFFFF & Short.reverseBytes(aBuffer.readShort());
		int h = 0xFFFF & Short.reverseBytes(aBuffer.readShort());

		return new Dimension(w, h);
	}


	private static Dimension getImageDimensionsBMP(DataInput aBuffer) throws IOException
	{
		aBuffer.skipBytes(12);

		if (Integer.reverseBytes(aBuffer.readInt()) != 40)
		{
			return null;
		}

		int w = Integer.reverseBytes(aBuffer.readInt());
		int h = Integer.reverseBytes(aBuffer.readInt());

		return new Dimension(w, h);
	}
}

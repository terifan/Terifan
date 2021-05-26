package org.terifan.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.zip.InflaterInputStream;
import org.terifan.io.Streams;



public class MemoryStruct
{
	public MemoryStruct(String aDefinition)
	{
	}


	public String getString(Path aPath)
	{
		return "";
	}


	public long getLong(Path aPath)
	{
		return 0;
	}


//	private static void putLong(byte[] aData, int aOffset, long aValue)
//	{
//		aData[aOffset++] = (byte)(aValue >>> 56);
//		aData[aOffset++] = (byte)(aValue >>> 48);
//		aData[aOffset++] = (byte)(aValue >>> 40);
//		aData[aOffset++] = (byte)(aValue >>> 32);
//		aData[aOffset++] = (byte)(aValue >>> 24);
//		aData[aOffset++] = (byte)(aValue >>> 16);
//		aData[aOffset++] = (byte)(aValue >>>  8);
//		aData[aOffset++] = (byte)(aValue >>>  0);
//	}


	public static class Path
	{
		Object[] path;
		public static Path of(Object... aPath)
		{
			Path p = new Path();
			p.path = aPath;
			return p;
		}
	}


	public static void main(String ... args)
	{
		try
		{
			try (DataInputStream in = new DataInputStream(new InflaterInputStream(new FileInputStream("C:\\Users\\patrik\\AppData\\Local\\Temp\\UnitLocationServlet-2"))))
			{
				System.out.println(in.readInt());
			}

//			MemoryStruct struct = new MemoryStruct("{deviceId=string:20,id=int32,locations={time=int64,latitude=float64,longitude=float64}[10]}[100]");
//
//			struct.getLong(Path.of(10, "locations", 4, "time"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

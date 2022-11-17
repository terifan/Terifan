package org.terifan.util;

import java.io.OutputStream;
import java.io.Writer;


public class HexDump
{
	public static void dump(Object aData)
	{
		new HexDumpBase().to(System.out).dump(aData);
	}


	public static HexDumpBase to(OutputStream aTo)
	{
		return new HexDumpBase().to(aTo);
	}


	public static HexDumpBase to(Writer aTo)
	{
		return new HexDumpBase().to(aTo);
	}


	public static HexDumpBase limit(int aLimit)
	{
		return new HexDumpBase().limit(aLimit);
	}


	public static HexDumpBase width(int aWidth)
	{
		return new HexDumpBase().width(aWidth);
	}
}
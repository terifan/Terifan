package org.terifan.net.http.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;


public class HttpHeaderParser
{
	public static LinkedHashMap<String, String> readHeader(InputStream aInputStream) throws IOException
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		byte[] header = readInput(aInputStream, 0x0D0A0D0A, 0xFFFFFFFF);

		if (header.length == 0)
		{
			return map;
		}

		String[] lines = new String(header).split("\r\n");

		int i0 = lines[0].indexOf(" ");
		int i1 = lines[0].lastIndexOf(" ");
		map.put("#command", lines[0].substring(0, i0));
		map.put("#path", lines[0].substring(i0 + 1, i1));
		map.put("#protocol", lines[0].substring(i1 + 1));

		for (int i = 1; i < lines.length; i++)
		{
			String param = lines[i];

			String key;
			String value;

			int p = param.indexOf(":");
			if (p == -1)
			{
				key = param;
				value = "";
			}
			else
			{
				key = param.substring(0, p);
				value = param.substring(p + 1).trim();
			}

			map.put(key, value);
		}

		System.out.println(map);

//		String port = getPort(map) != 80 ? ":" + getPort(map) : "";
//		byte[] host = ("http://"+getHost(map) + port).getBytes();
//		for (int i = 0; i < 10; i++)
//		{
//			boolean found = true;
//			for (int j = 0; j < host.length; j++)
//			{
//				if (host[j] != header[i+j])
//				{
//					found = false;
//					break;
//				}
//			}
//			if (found)
//			{
//				System.arraycopy(header, i+host.length, header, i, header.length-i-host.length);
//				header = Arrays.copyOfRange(header, 0, header.length-host.length);
//				break;
//			}
//		}
		return map;
	}


	private static byte[] readInput(InputStream aInputStream, int aTerminator, int aMask) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (int c, d = 0; (c = aInputStream.read()) != -1;)
		{
			baos.write(c);

			d = (d << 8) + c;

			if ((d & aMask) == aTerminator)
			{
				break;
			}
		}

		return baos.toByteArray();
	}

//	private static int getPort(HashMap<String,String> aParams) throws IOException
//	{
//		String host = aParams.getOrDefault("Host", "");
//		if (host.contains(":"))
//		{
//			return Integer.parseInt(host.substring(host.indexOf(":") + 1));
//		}
//		return 80;
//	}
}

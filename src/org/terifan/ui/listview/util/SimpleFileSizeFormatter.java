package org.terifan.ui.listview.util;

import org.terifan.ui.listview.util.Formatter;

public class SimpleFileSizeFormatter implements Formatter<Long>
{
	@Override
	public String format(Long aValue)
	{
		if (aValue == null)
		{
			return "";
		}

		long v = (aValue + 1023) / 1024;
		String s = "";

		if (v == 0)
		{
			s = "0 ";
		}
		else
		{
			while (v > 0)
			{
				int n = (int) (v % 1000);
				s = n + " " + s;

				v /= 1000;

				if (v > 0)
				{
					if (n < 10)
					{
						s = "00" + s;
					}
					else if (n < 100)
					{
						s = "0" + s;
					}
				}
			}
		}

		return s + "KB";
	}
}

package org.terifan.ui.listview.util;


public class SimpleTimeFormatter implements Formatter<Long>
{
	@Override
	public String format(Long aValue)
	{
		if (aValue == null)
		{
			return "";
		}
		
		aValue /= 1000;

		return (aValue / 60 / 60) + "h " + ((aValue / 60) % 60) + "m " + (aValue % 60) + "s";
	}
}

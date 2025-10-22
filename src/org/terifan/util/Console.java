package org.terifan.util;


public class Console
{
	private final static Console instance = new Console();
	private final static String RESET = ConsoleColors.RESET.toString();
	private final static String LABEL = ConsoleColors.BLACK_BOLD.toString();
	private final static String PARAM = ConsoleColors.CYAN_BOLD.toString();


	public static Console println(String aPattern, Object... aParams)
	{
		print(aPattern, aParams);
		System.out.println();
		return instance;
	}


	public static Console print(String aPattern, Object... aParams)
	{
		for (int i = 0, p = 0;;)
		{
			int j = aPattern.indexOf("{", i);
			int k = aPattern.indexOf("}", j + 1);

			if (j == -1 || k == -1)
			{
				System.out.print(aPattern.substring(i));
				break;
			}

			System.out.print(aPattern.substring(i, j));

			if (k == j + 1)
			{
				System.out.print(PARAM + aParams[p++] + RESET);
			}
			else if (aPattern.charAt(j + 1) == '%')
			{
				System.out.print(PARAM + aPattern.substring(j + 1, k).formatted(aParams[p++]) + RESET);
			}
			else
			{
				System.out.print(LABEL + aPattern.substring(j + 1, k) + RESET);
			}

			i = k + 1;
		}

		return instance;
	}
}

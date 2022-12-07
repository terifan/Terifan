package org.terifan.console;

import static org.terifan.console.SimpleConsoleWindow.BLACK;
import static org.terifan.console.SimpleConsoleWindow.GREEN;
import static org.terifan.console.SimpleConsoleWindow.RED;
import static org.terifan.console.SimpleConsoleWindow.BLUE;
import static org.terifan.console.SimpleConsoleWindow.CYAN;
import static org.terifan.console.SimpleConsoleWindow.MAGENTA;
import static org.terifan.console.SimpleConsoleWindow.YELLOW;
import static org.terifan.console.SimpleConsoleWindow.GRAY;
import org.terifan.ui.Utilities;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			Utilities.setSystemLookAndFeel();

			try (SimpleConsoleWindow console = new SimpleConsoleWindow().setAllowForceShutdown(true))
			{
				for (int j = 0, n = 0; j < 100; j++)
				{
					for (int i = 0; i < 10; i++, n++)
					{
						Object text = i == 9 ? new Exception("line " + n) : "line %d";

						TextStyle style = i == 9 ? RED : i == 3 ? GREEN : i == 4 ? BLUE : i == 5 ? CYAN : i == 6 ? MAGENTA : i == 7 ? YELLOW : i == 8 ? GRAY : BLACK;

						console.append("All", style, text, n);
						if (i == 9)
						{
							console.append("Error", style, text, n);
						}
						else if (i > 5)
						{
							console.append("Network", style, text, n);
						}
						else
						{
							console.append("General", style, text, n);
						}
						Thread.sleep(50);
					}

//					if (console.isCancelled())
//					{
//						console.append("X", BLUE, "closing");
//						System.out.println("was cancelled");
//						break;
//					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

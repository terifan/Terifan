package org.terifan.console;

import static org.terifan.console.ConsoleOutputWindow.BLACK;
import static org.terifan.console.ConsoleOutputWindow.GREEN;
import static org.terifan.console.ConsoleOutputWindow.RED;
import static org.terifan.console.ConsoleOutputWindow.BLUE;
import static org.terifan.console.ConsoleOutputWindow.CYAN;
import static org.terifan.console.ConsoleOutputWindow.MAGENTA;
import static org.terifan.console.ConsoleOutputWindow.YELLOW;
import static org.terifan.console.ConsoleOutputWindow.GRAY;
import org.terifan.ui.Utilities;


public class Test
{
	public static void main(String... args)
	{
		try
		{
			Utilities.setSystemLookAndFeel();

			try (ConsoleOutputWindow console = new ConsoleOutputWindow())
			{
				for (int j = 0, n = 0; j < 1000; j++)
				{
					for (int i = 0; i < 10; i++, n++)
					{
						Object text = i == 9 ? new Exception("line " + n) : "line " + n;

						TextStyle style = i == 9 ? RED : i == 3 ? GREEN : i == 4 ? BLUE : i == 5 ? CYAN : i == 6 ? MAGENTA : i == 7 ? YELLOW : i == 8 ? GRAY : BLACK;

						console.append("All", style, text);
						if (i == 9)
						{
							console.append("Error", style, text);
						}
						else if (i > 5)
						{
							console.append("Network", style, text);
						}
						else
						{
							console.append("General", style, text);
						}
						Thread.sleep(50);
					}

					if (console.isCancelled())
					{
						System.out.println("was cancelled");
						break;
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

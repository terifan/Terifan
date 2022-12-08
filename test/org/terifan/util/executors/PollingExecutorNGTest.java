package org.terifan.util.executors;

import org.terifan.util.ListSupplier;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.terifan.util.executors.PollingExecutor.Handler;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class PollingExecutorNGTest
{
	@Test
	public void testExecute1()
	{
		PollingExecutor executor = new PollingExecutor(8);

		List<String> elements = Arrays.asList("a","b","c","d","e","f","g","h");
		Supplier<String> supplier = new ListSupplier<>(elements);

		StringBuffer sb = new StringBuffer();
		Handler<String> handler = sb::append;

		executor.execute(supplier, handler);

		assertEquals(sb.length(), elements.size());
		System.out.println(sb);
	}


	@Test
	public void testExecuteRange()
	{
		PollingExecutor executor = new PollingExecutor(8);

		StringBuffer sb = new StringBuffer();
		Handler<Integer> handler = sb::append;

		executor.executeRange(0, 8, handler);

		assertEquals(sb.length(), 8);
	}


	@Test
	public void testExecute2()
	{
		PollingExecutor executor = new PollingExecutor(8);

		Supplier<Integer> elements = new Supplier()
		{
			int i;
			Random rnd = new Random(1);

			@Override
			public synchronized Integer get()
			{
				if (i++ < 8)
				{
					return rnd.nextInt(1000);
				}
				return null;
			}
		};

		StringBuffer sb = new StringBuffer();

		Handler<Integer> handler = aParameter ->
		{
			try
			{
				Thread.sleep(aParameter);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace(System.out);
			}
			sb.append(aParameter + "\n");
		};

		executor.execute(elements, handler);

		System.out.println(sb);
	}
}

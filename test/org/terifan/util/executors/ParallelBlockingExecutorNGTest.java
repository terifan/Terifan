package org.terifan.util.executors;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.terifan.util.executors.ParallelBlockingExecutor.Handler;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class ParallelBlockingExecutorNGTest
{
	@Test
	public void testExecute1()
	{
		ParallelBlockingExecutor executor = new ParallelBlockingExecutor(8);

		List<String> elements = Arrays.asList("a","b","c","d","e","f","g","h");

		StringBuffer sb = new StringBuffer();

		Handler<String> handler = new Handler<String>()
		{
			@Override
			public void run(String aParameter) throws Exception
			{
				sb.append(aParameter);
			}
		};

		executor.execute(elements, handler);

		assertEquals(sb.length(), elements.size());
	}


	@Test
	public void testExecute2()
	{
		ParallelBlockingExecutor executor = new ParallelBlockingExecutor(8);

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

		Handler<Integer> handler = new Handler<Integer>()
		{
			@Override
			public void run(Integer aParameter) throws Exception
			{
				try
				{
					Thread.sleep(aParameter);
				}
				catch (Exception e)
				{
					e.printStackTrace(System.out);
				}
				sb.append(aParameter + "\n");
			}
		};

		executor.execute(elements, handler);

		System.out.println(sb);
	}
}

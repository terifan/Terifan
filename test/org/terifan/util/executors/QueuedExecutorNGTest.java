package org.terifan.util.executors;

import org.terifan.util.executors.QueuedExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class QueuedExecutorNGTest
{
	@Test
	public void testSomeMethod()
	{
		try
		{
			AtomicInteger count = new AtomicInteger();

			QueuedExecutor<String> executor = new QueuedExecutor<>()
				.setInitializer(() -> System.out.println("init"))
				.setDestroyer(() -> System.out.println("dest"))
				.setHandler(aTask -> count.incrementAndGet());

			for (int i = 0; i < 10; i++)
			{
				executor.schedule("task " + i);
			}

			executor.shutdown();

			assertEquals(count.get(), 10);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

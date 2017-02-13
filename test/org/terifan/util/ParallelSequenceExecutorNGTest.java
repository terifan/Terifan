package org.terifan.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class ParallelSequenceExecutorNGTest
{
	public ParallelSequenceExecutorNGTest()
	{
	}


	@Test(invocationTimeOut = 1000)
	public void testExecute()
	{
		ParallelSequenceExecutor executor = new ParallelSequenceExecutor(8);

		SampleObject sample = new SampleObject();

		int n = 100;

		executor.execute(n, sample::sampleMethod1);

		assertEquals(sample.finishedCalls.size(), n);
	}


	@Test(invocationTimeOut = 1000)
	public void testExecute2()
	{
		ParallelSequenceExecutor executor = new ParallelSequenceExecutor(8);

		SampleObject sample = new SampleObject();
		
		String[] elements = new String[]{"a","b","c","d"};

		executor.execute(elements, sample::sampleMethod2);

		assertEquals(sample.finishedCalls.size(), elements.length);
	}


	@Test(invocationTimeOut = 1000)
	public void testExecute3()
	{
		ParallelSequenceExecutor executor = new ParallelSequenceExecutor(0.5f);

		SampleObject sample = new SampleObject();

		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				executor.cancel();
			}
		}.start();

		executor.execute(Integer.MAX_VALUE, sample::sampleMethod3);
	}


	private static class SampleObject
	{
		Set<Integer> finishedCalls;

		public SampleObject()
		{
			finishedCalls = Collections.synchronizedSet(new HashSet<>());
		}

		public void sampleMethod1(Integer e)
		{
			finishedCalls.add(e);
		}

		public void sampleMethod2(String e)
		{
			finishedCalls.add(e.hashCode());
		}

		public void sampleMethod3(Integer e)
		{
			// do nothing
		}
	}
}

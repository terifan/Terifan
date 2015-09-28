package org.terifan.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import org.terifan.util.log.Log;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class SparseDoubleArrayNGTest
{
	
	public SparseDoubleArrayNGTest()
	{
	}


	@Test
	public void test1()
	{
		SparseDoubleArray array = new SparseDoubleArray();
		
		for (int i = 0; i < 10000; i++)
		{
			array.set(i, i);
		}

		for (int i = 0; i < 10000; i++)
		{
			assertEquals((double)i, array.get(i, 0));
		}
	}


	@Test
	public void test2()
	{
		SparseDoubleArray array = new SparseDoubleArray();
		
		Random r = new Random(1);
		HashMap<Integer,Double> done = new HashMap<>();
		for (int i = 0; i < 10000; i++)
		{
			done.put(r.nextInt(Integer.MAX_VALUE), r.nextDouble());
		}

		for (Entry<Integer,Double> entry : done.entrySet())
		{
			array.set(entry.getKey(), entry.getValue());
		}

		for (Entry<Integer,Double> entry : done.entrySet())
		{
			assertEquals(entry.getValue(), array.get(entry.getKey(), 0));
		}
	}
}

package org.terifan.util;

import java.util.Arrays;
import org.terifan.util.log.Log;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class IntArrayNGTest
{
	
	public IntArrayNGTest()
	{
	}


	@Test
	public void testIndexOf()
	{
	}


	@Test
	public void testRemoveValue_int()
	{
		int[] input = {1,2,3,4,2,1,2,3,4,1,1};
		int[] output = new IntArray(input).removeValue(1).trimToSize().array();
		int[] expected = {2,3,4,2,2,3,4};

		assertEquals(output, expected);
	}


	@Test
	public void testRemoveValue_Predicate()
	{
		int[] input = {1,2,3,4,2,1,2,3,4,1,1};
		int[] output = new IntArray(input).removeIf(i->i==1).trimToSize().array();
		int[] expected = {2,3,4,2,2,3,4};

		for (int i : output) Log.out.println(i);
		
		assertEquals(output, expected);
	}


	@Test
	public void testArray()
	{
	}


	@Test
	public void testGet()
	{
	}


	@Test
	public void testAdd_int()
	{
	}


	@Test
	public void testAdd_intArr()
	{
	}


	@Test
	public void testAdd_3args()
	{
	}


	@Test
	public void testSet_int_int()
	{
	}


	@Test
	public void testSet_int_intArr()
	{
	}


	@Test
	public void testSet_4args()
	{
	}


	@Test
	public void testClear()
	{
	}


	@Test
	public void testTrimToSize()
	{
	}


	@Test
	public void testSize()
	{
	}


	@Test
	public void testIsEmpty()
	{
	}


	@Test
	public void testIterator()
	{
	}


	@Test
	public void testClone()
	{
	}


	@Test
	public void testToString()
	{
	}


	@Test
	public void testContains()
	{
	}
}

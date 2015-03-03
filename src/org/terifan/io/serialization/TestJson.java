package org.terifan.io.serialization;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.terifan.util.log.Log;


public class TestJson
{
	public static void main(String ... args)
	{
		try
		{
			Factory factory = new Factory(Person.class);
			String json = "{"
				+ "year:2000, "
				+ "name:Stig, "
				+ "body:{height:182}, "
				+ "email:["
					+ "{address:'sss'}, "
					+ "{address:'ttt'}"
				+ "], "
				+ "singleArray:[1,2,3], "
				+ "doubleArray:[[4,5,6],[7,8]],"
				+ "trippleArray:[[[4,5,6],[7,8]],[[9]]],"
//				+ "listInt:[10,11,12],"
//				+ "listSingleArray:[[13,14],[15,16]]"
				+ "mapValue:{a:1, b:2, c:3}"
				+ "}";
			new JSONReader().unmarshal(factory, new ByteArrayInputStream(json.getBytes()));
			Log.out.println(factory.getOutput());
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	static class Person
	{
		long year;
		String name;
		ArrayList<Email> email;
		Body body;
		int[] singleArray;
		int[][] doubleArray;
		int[][][] trippleArray;
		ArrayList<int[]> listSingleArray;
		ArrayList<int[][]> listDoubleArray;
		HashMap<String,Integer> mapValue;
		HashMap<String,int[]> mapSingleArray;

		@Override
		public String toString()
		{
			return "{"
				+ "year="+year+", "
				+ "name="+name+", "
				+ "body="+body+", "
				+ "email="+email+", "
				+ "singleArray="+Arrays.toString(singleArray)+", "
				+ "doubleArray=["+Arrays.toString(doubleArray[0])+", "+Arrays.toString(doubleArray[1])+"], "
				+ "trippleArray=[["+Arrays.toString(trippleArray[0][0])+", "+Arrays.toString(trippleArray[0][1])+"],["+Arrays.toString(trippleArray[1][0])+"]], "
				+ "mapValue=" + mapValue
				+ "}";
		}
	}
	static class Body
	{
		int height;

		@Override
		public String toString()
		{
			return "{height="+height+"}";
		}
	}
	static class Email
	{
		String address;

		@Override
		public String toString()
		{
			return "{address="+address+"}";
		}
	}
}
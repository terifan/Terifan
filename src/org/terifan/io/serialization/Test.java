package org.terifan.io.serialization;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.terifan.io.serialization.ObjectSerializer.Serialize;
import org.terifan.util.Calendar;
import org.terifan.xml.XmlDocument;


public class Test
{
	public static void main(String ... args)
	{
		new Test().test1();
	}


	private void test1()
	{
		try
		{
			MyObject obj = new MyObject();

			try (FileOutputStream fos = new FileOutputStream("d:/object.xml"))
			{
				ObjectSerializer serializer = new ObjectSerializer(new XMLWriter(fos));
				serializer.serialize(obj);
			}

			try (FileOutputStream fos = new FileOutputStream("d:/object_dom.xml"))
			{
				XmlDocument doc = new XmlDocument();
				ObjectSerializer serializer = new ObjectSerializer(new DOMWriter(doc));
				serializer.serialize(obj);
				doc.writeTo(fos);
			}

			try (FileOutputStream fos = new FileOutputStream("d:/object.json"))
			{
				ObjectSerializer serializer = new ObjectSerializer(new JSONWriter(fos));
				serializer.serialize(obj);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private Calendar calendarInstance = new Calendar();


	private class MyParentObject
	{
		Date date = new Date();
		Calendar parentCalendar = calendarInstance;
		int hiddenInt = 1;
	}


	private class MyObject extends MyParentObject
	{
		Calendar childCalendar = calendarInstance;
		int _int = 7;
		Integer integer = 17;
		char _char = 'x';
		char[] _chars = "hej".toCharArray();
		byte[] byteArrray1 = {1,2,3};
		Byte[] byteObjectArrray1 = {1,null,3};
		byte[][] byteArrray2 = {{4,5,6},{7,8,9}};
		byte[][] byteArrray2X = {{4,5,6},{7,8,9}};
		byte[][][] byteArrray3 = {{{10,11,12},{7,8,9}},{null,{13,14,15}}};
		String string = "<&>";
		String stringX = "hej";
		String[] stringArray = {"hej",null,"apa"};
		Object[] objectArray = {"hej",7,new Calendar()};
		ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList("hello",null,"world"));
		HashMap<Integer,ArrayList<String>> intStringHashMapArrayList = new HashMap<>();
		int hiddenInt = 2;
		MySecondObject mySecondObject = new MySecondObject();
		GregorianCalendar greg = new GregorianCalendar();
		String _null = null;
		MyObject loopReference = this;


		public MyObject()
		{
			intStringHashMapArrayList.put(7, new ArrayList<>(Arrays.asList("hello",null,"world")));
			intStringHashMapArrayList.put(3, new ArrayList<>(Arrays.asList("test")));
		}
	}


	private class MySecondObject
	{
		@Serialize("int") int _int = 684;
	}
}

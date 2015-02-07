package org.terifan.io.serialization;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.terifan.util.log.Log;


public class TestJson
{
	public static void main(String ... args)
	{
		try
		{
			Factory factory = new Factory();
			new JSONReader().unmarshal(factory, new ByteArrayInputStream("{year:2000, name:Stig, email:[{address:'ssss'},{address:'tttt'}]}".getBytes()));
			Log.out.println(factory.get());
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

//	static class Person
//	{
//		long year;
//		String name;
//		List email;
//
//		@Override
//		public String toString()
//		{
//			return year+" "+name+" "+email;
//		}
//	}
//	static class Email
//	{
//		String address;
//
//		@Override
//		public String toString()
//		{
//			return address;
//		}
//	}
}

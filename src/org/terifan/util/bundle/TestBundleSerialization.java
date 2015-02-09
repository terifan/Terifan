package org.terifan.util.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.terifan.util.log.Log;


public class TestBundleSerialization
{
	public static void main(String ... args)
	{
		try
		{
			Person person = new Person();
			person.year = 2000;
			person.name = "Stig";
			person.email = new ArrayList<>(Arrays.asList(new Email("ss"), new Email("tt")));
			person.body = new Body(182, 86);
			person.email2 = new Email[]{new Email("xx"), new Email("yy")};

			Bundle bundle = new Bundle();
			bundle.putObject("person", person);

			String json = new JSONEncoder().marshal(bundle);
			Log.out.println(json);

			Bundle decodedBundle = new JSONDecoder().unmarshal(json);
			Person decodedPerson = decodedBundle.getObject(Person.class, "person");

			Log.out.println(new JSONEncoder().marshal(new Bundle().putObject("person", decodedPerson)));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	static class Person implements BundleExternalizable
	{
		long year;
		String name;
		ArrayList<Email> email;
		Body body;
		Email[] email2;
		@Override
		public void readExternal(Bundle aBundle) throws IOException
		{
			year = aBundle.getLong("year");
			name = aBundle.getString("name");
			email = aBundle.getObjectArrayList(Email.class, "email");
			body = aBundle.getObject(Body.class, "body");
			email2 = aBundle.getObjectArray(Email.class, "email2");
		}
		@Override
		public void writeExternal(Bundle aBundle) throws IOException
		{
			aBundle.putLong("year", year);
			aBundle.putString("name", name);
			aBundle.putObject("body", body);
			aBundle.putObjectArrayList("email", email);
			aBundle.putObjectArray("email2", email2);
		}
	}

	static class Body implements BundleExternalizable
	{
		int height;
		float weight;
		public Body()
		{
		}
		public Body(int aHeight, float aWeight)
		{
			this.height = aHeight;
			this.weight = aWeight;
		}
		@Override
		public void readExternal(Bundle aBundle) throws IOException
		{
			height = aBundle.getAsInt("height", 0);
			weight = aBundle.getAsFloat("weight", 0);
		}
		@Override
		public void writeExternal(Bundle aBundle) throws IOException
		{
			aBundle.putInt("height", height);
			aBundle.putFloat("weight", weight);
		}
	}
	
	static class Email implements BundleExternalizable
	{
		String address;
		public Email()
		{
		}
		public Email(String aAddress)
		{
			this.address = aAddress;
		}
		@Override
		public void readExternal(Bundle aBundle) throws IOException
		{
			address = aBundle.getString("address", "");
		}
		@Override
		public void writeExternal(Bundle aBundle) throws IOException
		{
			aBundle.putString("address", address);
		}
	}
}

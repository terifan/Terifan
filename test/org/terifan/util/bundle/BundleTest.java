package org.terifan.util.bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;


public class BundleTest
{
	public BundleTest()
	{
	}


	@Test
	public void testExternalizable() throws IOException, ClassNotFoundException
	{
		Bundle bundle = createSimpleBundle();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			bundle.writeExternal(oos);
		}

		Bundle unbundled = new Bundle();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		try (ObjectInputStream ois = new ObjectInputStream(bais))
		{
			unbundled.readExternal(ois);
		}

		assertEquals(bundle, unbundled);
	}


	private static Bundle createSimpleBundle()
	{
		Bundle bundle = new Bundle()
			.putBoolean("boolean", true)
			.putByte("byte", 97)
			.putShort("short", 1000)
			.putChar("char", (char)97)
			.putInt("int", 64646464)
			.putLong("long", 6464646464646464646L)
			.putFloat("float", 3.14f)
			.putDouble("double", 7)
			.putDate("date", new Date())
			.putString("string", "string")
			.putString("null", null)
			.putBundle("bundle", new Bundle()
				.putByte("a", 1)
				.putByte("b", 2)
			)
			.putIntArray("ints", 1,2,3)
			.putByteArray("bytes", (byte)1,(byte)2,(byte)3)
			.putIntArrayList("intList", new ArrayList<>(Arrays.asList(1,2,3)));
		return bundle;
	}
}
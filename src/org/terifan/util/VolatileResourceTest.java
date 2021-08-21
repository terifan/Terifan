package org.terifan.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;


public class VolatileResourceTest
{
	public static void main(String ... args)
	{
		try
		{
			for (int i = 0; i < 100; i++)
			{
				test();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static class XX extends HashMap<String, Integer>
	{
	}


	public static void test()
	{
		try
		{
			VolatileResourceSupplier<XX> supplier = new VolatileResourceSupplier<XX>()
			{
				byte[] mBuffer;

				@Override
				public XX create() throws Exception
				{
					System.out.println("create");
					if (mBuffer == null)
					{
						return new XX();
					}
					return (XX)new ObjectInputStream(new ByteArrayInputStream(mBuffer)).readObject();
				}

				@Override
				public void release(XX aInstance) throws Exception
				{
					System.out.println("destroy");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try (ObjectOutputStream oos = new ObjectOutputStream(baos))
					{
						oos.writeObject(aInstance);
					}
					mBuffer = baos.toByteArray();
				}
			};
			supplier.setReleaseDelay(500);

			try (FixedThreadExecutor ex = new FixedThreadExecutor(8))
			{
				for (int i = 0; i < 100; i++)
				{
					int _i = i;
					ex.submit(() ->
					{
						try (VolatileResource<XX> res = supplier.lock())
						{
							res.get().put(Character.toString('n' + new Random().nextInt(13)), _i);
						}
					});
				}
			}

			Thread.sleep(1000);

			try (FixedThreadExecutor ex = new FixedThreadExecutor(8))
			{
				for (int i = 0; i < 100; i++)
				{
					int _i = i;
					ex.submit(() ->
					{
						try (VolatileResource<XX> res = supplier.lock())
						{
							res.get().put(Character.toString('a' + new Random().nextInt(13)), _i);
						}
					});
				}
			}

			try (VolatileResource<XX> lock = supplier.aquire())
			{
				System.out.println(lock.get());
			}

//			Thread.sleep(3000);

			supplier.close();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

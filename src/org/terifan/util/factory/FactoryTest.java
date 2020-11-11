package org.terifan.util.factory;

import java.util.Random;
import static org.terifan.util.Assert.assertEquals;
import static org.terifan.util.Assert.assertTrue;
import static org.terifan.util.Assert.assertNotSame;
import static org.terifan.util.Assert.assertSame;


public class FactoryTest
{
	public static void main(String ... args)
	{
		try
		{
			testUnbound();
			testBoundType();
			testBoundTypeToSupplier();
			testBoundTypeToSupplierSingleton();
			testBoundTypeToSingleton();
			testBoundTypeToSingleInstance();
			testBoundTypeToProducer();
			testDualFactoryBoundTypeToSingleton();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static void testUnbound()
	{
		Factory factory = new Factory();
		A instance = factory.newInstance(A.class);
		assertTrue(instance instanceof A);
	}


	private static void testBoundType()
	{
		Factory factory = new Factory();
		factory.bind(I.class).to(A.class);
		I instance1 = factory.newInstance(I.class);
		I instance2 = factory.newInstance(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertNotSame(instance1, instance2);
	}


	private static void testBoundTypeToSupplier()
	{
		Factory factory = new Factory();
		factory.bind(I.class).toSupplier(B::new);
		I instance1 = factory.newInstance(I.class);
		I instance2 = factory.newInstance(I.class);
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertNotSame(instance1, instance2);
	}


	private static void testBoundTypeToSupplierSingleton()
	{
		Factory factory = new Factory();
		factory.bind(I.class).toSupplier(B::new).asSingleton();
		I instance1 = factory.newInstance(I.class);
		I instance2 = factory.newInstance(I.class);
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertSame(instance1, instance2);
	}


	private static void testBoundTypeToSingleton()
	{
		Factory factory = new Factory();
		factory.bind(I.class).to(A.class).asSingleton();
		I instance1 = factory.newInstance(I.class);
		I instance2 = factory.newInstance(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(instance1, instance2);
	}


	private static void testBoundTypeToSingleInstance()
	{
		Factory factory = new Factory();
		A constant = new A();
		factory.bind(I.class).toInstance(constant);
		I instance1 = factory.newInstance(I.class);
		I instance2 = factory.newInstance(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(constant, instance1);
		assertSame(constant, instance2);
	}


	private static void testBoundTypeToProducer()
	{
		Factory factory = new Factory();
		factory.bind(K.class).with(Integer.class).toProducer(L::new);
		K instance1 = factory.with(5).newInstance(K.class);
		assertTrue(instance1 instanceof L);
		assertEquals(instance1.get(), 5);
	}


	private static void testDualFactoryBoundTypeToSingleton()
	{
		Factory factory1 = new Factory();
		Factory factory2 = new Factory();
		factory1.bind(I.class).to(A.class).asSingleton();
		factory2.bind(I.class).to(A.class).asSingleton();
		I instance1 = factory1.newInstance(I.class);
		I instance2 = factory1.newInstance(I.class);
		I instance3 = factory2.newInstance(I.class);
		I instance4 = factory2.newInstance(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(instance1, instance2);
		assertTrue(instance3 instanceof A);
		assertTrue(instance4 instanceof A);
		assertSame(instance3, instance4);
		assertNotSame(instance1, instance3);
	}


	static interface I
	{
	}


	static class A implements I
	{
		long v;

		public A()
		{
			v = new Random().nextLong();
		}

		@Override
		public String toString()
		{
			return "A{" + "v=" + v + '}';
		}
	}


	static class B implements I
	{
		long v;

		public B()
		{
			v = new Random().nextLong();
		}

		@Override
		public String toString()
		{
			return "B{" + "v=" + v + '}';
		}
	}


	static interface K
	{
		int get();
	}


	static class L implements K
	{
		int v;

		public L(int aLabel)
		{
			v = aLabel;
		}


		@Override
		public int get()
		{
			return v;
		}


		@Override
		public String toString()
		{
			return "L{" + "v=" + v + '}';
		}
	}
}
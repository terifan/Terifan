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
			testType();
			testTypeToSupplier();
			testTypeToSupplierSingleton();
			testTypeToSingleton();
			testTypeToSingleInstance();
			testTypeToProducer();
			testDualFactoryTypeToSingleton();

			testNamedType();
			testNamedTypeToSupplier();
			testNamedTypeToSupplierSingleton();
			testNamedTypeToSingleton();
			testNamedTypeToSingleInstance();
			testNamedTypeToProducer();

			testNamedConstant();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static void testUnbound()
	{
		Factory factory = new Factory();
		A instance = factory.get(A.class);
		assertTrue(instance instanceof A);
	}


	private static void testType()
	{
		Factory factory = new Factory();
		factory.bind(I.class).to(A.class);
		I instance1 = factory.get(I.class);
		I instance2 = factory.get(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertNotSame(instance1, instance2);
	}


	private static void testTypeToSupplier()
	{
		Factory factory = new Factory();
		factory.bind(I.class).toSupplier(B::new);
		I instance1 = factory.get(I.class);
		I instance2 = factory.get(I.class);
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertNotSame(instance1, instance2);
	}


	private static void testTypeToSupplierSingleton()
	{
		Factory factory = new Factory();
		factory.bind(I.class).toSupplier(B::new).asSingleton();
		I instance1 = factory.get(I.class);
		I instance2 = factory.get(I.class);
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertSame(instance1, instance2);
	}


	private static void testTypeToSingleton()
	{
		Factory factory = new Factory();
		factory.bind(I.class).to(A.class).asSingleton();
		I instance1 = factory.get(I.class);
		I instance2 = factory.get(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(instance1, instance2);
	}


	private static void testTypeToSingleInstance()
	{
		Factory factory = new Factory();
		A constant = new A();
		factory.bind(I.class).toInstance(constant);
		I instance1 = factory.get(I.class);
		I instance2 = factory.get(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(constant, instance1);
		assertSame(constant, instance2);
	}


	private static void testTypeToProducer()
	{
		Factory factory = new Factory();
		factory.bind(K.class).with(Integer.class).toProducer(L::new);
		K instance1 = factory.with(5).get(K.class);
		assertTrue(instance1 instanceof L);
		assertEquals(instance1.get(), 5);
	}


	private static void testDualFactoryTypeToSingleton()
	{
		Factory factory1 = new Factory();
		Factory factory2 = new Factory();
		factory1.bind(I.class).to(A.class).asSingleton();
		factory2.bind(I.class).to(A.class).asSingleton();
		I instance1 = factory1.get(I.class);
		I instance2 = factory1.get(I.class);
		I instance3 = factory2.get(I.class);
		I instance4 = factory2.get(I.class);
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(instance1, instance2);
		assertTrue(instance3 instanceof A);
		assertTrue(instance4 instanceof A);
		assertSame(instance3, instance4);
		assertNotSame(instance1, instance3);
	}


	private static void testNamedType()
	{
		Factory factory = new Factory();
		factory.bindNamed("a").to(A.class);
		I instance1 = factory.named("a");
		I instance2 = factory.named("a");
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertNotSame(instance1, instance2);
	}


	private static void testNamedTypeToSupplier()
	{
		Factory factory = new Factory();
		factory.bindNamed("b").toSupplier(B::new);
		I instance1 = factory.named("b");
		I instance2 = factory.named("b");
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertNotSame(instance1, instance2);
	}


	private static void testNamedTypeToSupplierSingleton()
	{
		Factory factory = new Factory();
		factory.bindNamed("b").toSupplier(B::new).asSingleton();
		I instance1 = factory.named("b");
		I instance2 = factory.named("b");
		assertTrue(instance1 instanceof B);
		assertTrue(instance2 instanceof B);
		assertSame(instance1, instance2);
	}


	private static void testNamedTypeToSingleton()
	{
		Factory factory = new Factory();
		factory.bindNamed("a").to(A.class).asSingleton();
		I instance1 = factory.named("a");
		I instance2 = factory.named("a");
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(instance1, instance2);
	}


	private static void testNamedTypeToSingleInstance()
	{
		Factory factory = new Factory();
		A constant = new A();
		factory.bindNamed("a").toInstance(constant);
		I instance1 = factory.named("a");
		I instance2 = factory.named("a");
		assertTrue(instance1 instanceof A);
		assertTrue(instance2 instanceof A);
		assertSame(constant, instance1);
		assertSame(constant, instance2);
	}


	private static void testNamedTypeToProducer()
	{
		Factory factory = new Factory();
		factory.bindNamed("k").with(Integer.class).toProducer(L::new);
		K instance1 = factory.with(5).get("k");
		assertTrue(instance1 instanceof L);
		assertEquals(instance1.get(), 5);
	}


	private static void testNamedConstant()
	{
		Factory factory = new Factory();
		factory.bindNamed("path").toInstance("c:\\windows");
		factory.bindNamed("value").toInstance(7.0);
		String path = factory.named("path");
		double value = factory.named("value");
		assertEquals(path, "c:\\windows");
		assertEquals(value, 7.0);
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

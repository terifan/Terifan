package org.terifan.injector;

import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class InjectNGTest
{
	public InjectNGTest()
	{
	}


	@Test
	public void testBind()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class);

		injector.bind(String.class).named("value").toInstance("string-value");

		Fruit fruit1 = injector.getInstance(Fruit.class);
		Fruit fruit2 = injector.getInstance(Fruit.class);

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertNotSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Fruit);
		assertTrue(fruit2 instanceof Fruit);

		assertEquals(fruit1.mFruitProperty1.mValue, "string-value");
		assertEquals(fruit1.mFruitProperty2.mValue, "string-value");
		assertEquals(fruit1.mFruitProperty3.mValue, "string-value");
		assertEquals(fruit2.mFruitProperty1.mValue, "string-value");
		assertEquals(fruit2.mFruitProperty2.mValue, "string-value");
		assertEquals(fruit2.mFruitProperty3.mValue, "string-value");
	}


	@Test
	public void testBindSelf()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).to(Apple.class);

		Apple fruit1 = (Apple)injector.getInstance(Fruit.class);
		Apple fruit2 = (Apple)injector.getInstance(Fruit.class);

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertNotSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
		assertTrue(fruit2 instanceof Apple);
	}


	@Test
	public void testBindToSingleton()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).to(Apple.class).asSingleton();

		Fruit fruit1 = injector.getInstance(Fruit.class);
		Fruit fruit2 = injector.getInstance(Fruit.class);

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
	}


	@Test
	public void testBindToIn()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).to(Apple.class).in(Apple.class);

		assertTrue(true);
	}


	@Test
	public void testBindToInSingleton()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).to(Apple.class).in(Apple.class).asSingleton();

		assertTrue(true);
	}


	@Test
	public void testBindInstance()
	{
		Injector injector = new Injector();

		Apple expected = new Apple(null,null);

		injector.bind(Fruit.class).toInstance(expected);

		Fruit fruit1 = injector.getInstance(Fruit.class);
		Fruit fruit2 = injector.getInstance(Fruit.class);

		assertSame(fruit1, expected);
		assertSame(fruit2, expected);
	}


	@Test
	public void testBindProvider()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).toProvider(()->new Apple(null,null));

		Fruit fruit1 = injector.getInstance(Fruit.class);
		Fruit fruit2 = injector.getInstance(Fruit.class);

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertNotSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
	}


	@Test
	public void testBindNamedTo()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).named("Braeburn").to(Apple.class);

		Fruit fruit1 = injector.getNamedInstance(Fruit.class, "Braeburn");
		Fruit fruit2 = injector.getNamedInstance(Fruit.class, "Braeburn");

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertNotSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
	}


	@Test
	public void testBindNamedToSingleton()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).named("Braeburn").to(Apple.class).asSingleton();

		Fruit fruit1 = injector.getNamedInstance(Fruit.class, "Braeburn");
		Fruit fruit2 = injector.getNamedInstance(Fruit.class, "Braeburn");

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
		assertTrue(fruit2 instanceof Apple);
	}


	@Test
	public void testBindNamedToProvider()
	{
		Injector injector = new Injector();

		injector.bind(Fruit.class).named("Braeburn").toProvider(()->new Apple(null,null));

		Fruit fruit1 = injector.getNamedInstance(Fruit.class, "Braeburn");
		Fruit fruit2 = injector.getNamedInstance(Fruit.class, "Braeburn");

		assertNotNull(fruit1);
		assertNotNull(fruit2);
		assertNotSame(fruit1, fruit2);
		assertTrue(fruit1 instanceof Apple);
		assertTrue(fruit2 instanceof Apple);
	}


	@Test
	public void testPostConstruct()
	{
		Injector injector = new Injector();

		injector.bind(PostConstructSample.class);

		PostConstructSample instance = injector.getInstance(PostConstructSample.class);

		assertTrue(instance.mPostConstructWasRun);
	}


	@Test
	public void testOptionalNamed()
	{
		Injector injector = new Injector();

		injector.bind(OptionalNamedSample.class);

		OptionalNamedSample instance = injector.getInstance(OptionalNamedSample.class);

		assertNull(instance.mValue);
	}


	@Test(expectedExceptions = InjectionException.class, expectedExceptionsMessageRegExp = "Named instance of.*value")
	public void testMandatoryNamed()
	{
		Injector injector = new Injector();

		injector.bind(MandatoryNamedSample.class);

		injector.getInstance(MandatoryNamedSample.class);

		fail();
	}


	static class Fruit
	{
		@Inject FruitProperty mFruitProperty1;
		FruitProperty mFruitProperty2;
		FruitProperty mFruitProperty3;
		@Inject(name = "color", optional = true) FruitProperty mFruitProperty4;

		@Inject
		public Fruit(FruitProperty aFruitProperty2)
		{
			mFruitProperty2 = aFruitProperty2;
		}

		@Inject
		public void initFruit(FruitProperty aFruitProperty3)
		{
			mFruitProperty3 = aFruitProperty3;
		}
	}


	static class Apple extends Fruit
	{
		@Inject AppleProperty mAppleProperty1;
		AppleProperty mAppleProperty2;
		AppleProperty mAppleProperty3;

		@Inject
		public Apple(AppleProperty aAppleProperty2, FruitProperty aFruitProperty2)
		{
			super(aFruitProperty2);

			mAppleProperty2 = aAppleProperty2;
		}

		@Inject
		public void initApple(AppleProperty aAppleProperty3)
		{
			mAppleProperty3 = aAppleProperty3;
		}
	}


	static class FruitProperty
	{
		@Inject(name = "value", optional = true) String mValue;
	}


	static class AppleProperty
	{
	}


	static class PostConstructSample
	{
		boolean mPostConstructWasRun;

		@PostConstruct
		public void init()
		{
			mPostConstructWasRun = true;
		}
	}


	static class OptionalNamedSample
	{
		@Inject(name = "value", optional = true) String mValue;
	}


	static class MandatoryNamedSample
	{
		@Inject(name = "value") String mValue;
	}
}

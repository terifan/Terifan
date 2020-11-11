package org.terifan.util.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Supplier;


public class Factory
{
	private final HashMap<Class, Supplier> mSuppliers = new HashMap<>();
	private final HashMap<Class, Class> mBoundTypes = new HashMap<>();
	private final HashMap<Class, Object> mSingletons = new HashMap<>();
	private final HashSet<Class> mFutureSingletons = new HashSet<>();
	private final HashMap<Class, HashMap<Class, Function>> mProducers = new HashMap<>();


	public Factory()
	{
		init();
	}


	/**
	 * This method is called by the constructor, override it to initialize this instance.
	 */
	protected void init()
	{
	}


	public <T> FactoryBind<T> bind(Class<T> aFrom)
	{
		mBoundTypes.put(aFrom, aFrom);
		return new FactoryBind<>(aFrom);
	}


	public Producer with(Object aParameter)
	{
		if (aParameter == null)
		{
			throw new IllegalArgumentException("Provided parameter is null.");
		}

		HashMap<Class, Function> map = mProducers.get(aParameter.getClass());

		if (map == null)
		{
			throw new IllegalArgumentException("Parameter type is not bound: " + aParameter.getClass());
		}

		return new Producer(map, aParameter);
	}


	public <T> T newInstance(Class<T> aType)
	{
		T instance = (T)mSingletons.get(aType);

		if (instance == null)
		{
			instance = (T)mSuppliers.getOrDefault(aType, () ->
			{
				try
				{
					return (T)mBoundTypes.getOrDefault(aType, aType).newInstance();
				}
				catch (IllegalAccessException | InstantiationException e)
				{
					throw new IllegalStateException(e);
				}
			}).get();

			if (mFutureSingletons.remove(aType))
			{
				mSingletons.put(aType, instance);
			}
		}

		return instance;
	}


	public class FactoryBind<T>
	{
		private Class<T> mFromType;


		FactoryBind(Class<T> aType)
		{
			mFromType = aType;
		}


		public <T> FactoryBindTo to(Class aTo)
		{
			mBoundTypes.put(mFromType, aTo);
			return new FactoryBindTo<>(mFromType);
		}


		public FactoryBindTo<T> toSupplier(Supplier aSupplier)
		{
			mSuppliers.put(mFromType, aSupplier);
			return new FactoryBindTo<>(mFromType);
		}


		public void toInstance(T aInstance)
		{
			mSingletons.put(mFromType, aInstance);
		}


		public void asSingleton()
		{
			mFutureSingletons.add(mFromType);
		}


		public <U> FactoryProducer<T, U> with(Class<U> aParameterType)
		{
			return new FactoryProducer<>(mFromType, aParameterType);
		}
	}


	public class FactoryProducer<T, U>
	{
		private Class<T> mFromType;
		private Class<U> mParameterType;


		FactoryProducer(Class<T> aFromType, Class<U> aParameterType)
		{
			mFromType = aFromType;
			mParameterType = aParameterType;
		}


		public void toProducer(Function<U, T> aFunction)
		{
			HashMap<Class, Function> map = mProducers.computeIfAbsent(mParameterType, k -> new HashMap<>());
			map.put(mFromType, aFunction);
		}
	}


	public class Producer
	{
		private HashMap<Class, Function> mMap;
		private Object mParameter;


		Producer(HashMap<Class, Function> aMap, Object aParameter)
		{
			mMap = aMap;
			mParameter = aParameter;
		}


		public <T> T newInstance(Class<T> aType)
		{
			Function fn = mMap.get(aType);

			if (fn == null)
			{
				throw new IllegalArgumentException("Type is not bound to a producer: " + aType);
			}

			T instance = (T)fn.apply(mParameter);

			return instance;
		}
	}


	public class FactoryBindTo<T>
	{
		private Class<T> mFromType;


		FactoryBindTo(Class<T> aType)
		{
			mFromType = aType;
		}


		public void asSingleton()
		{
			mFutureSingletons.add(mFromType);
		}
	}
}

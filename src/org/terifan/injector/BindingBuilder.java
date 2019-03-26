package org.terifan.injector;

import org.terifan.util.Tuple;


public class BindingBuilder<T> extends Factory<T>
{
	private Injector mInjector;
	private Class<T> mFromClass;


	BindingBuilder(Injector aInjector, Class aFromClass)
	{
		mInjector = aInjector;
		mFromClass = aFromClass;
	}


	public void asSingleton()
	{
		mInjector.mBindings.put(mFromClass, new BindingBuilderSingleton(mInjector, mFromClass));
	}


	public <T> BindingBuilderTo to(Class<T> aToClass)
	{
		BindingBuilderTo to = new BindingBuilderTo(mInjector, mFromClass, aToClass, null);
		mInjector.mBindings.put(mFromClass, to);
		return to;
	}


	public void toInstance(Object aInstance)
	{
		BindingBuilderInstance to = new BindingBuilderInstance(mInjector, aInstance);
		mInjector.mBindings.put(mFromClass, to);
	}


	public void toProvider(Provider aProvider)
	{
		BindingBuilderProvider to = new BindingBuilderProvider(mInjector, aProvider);
		mInjector.mBindings.put(mFromClass, to);
	}


	public BindingBuilderNamed named(String aName)
	{
		BindingBuilderNamed builder = new BindingBuilderNamed(mInjector, mFromClass, aName);
		mInjector.mNamedBindings.put(new Tuple<>(mFromClass, aName), builder);
		return builder;
	}


	@Override
	T getInstance() throws IllegalAccessException, InstantiationException
	{
		return (T)mInjector.createInstance(mFromClass);
	}


//	T getInstance()
//	{
//		try
//		{
//			if (mSingletons.containsKey(mClass))
//			{
//				synchronized (this)
//				{
//					Object tmp = mSingletons.get(mClass);
//					if (tmp instanceof Class)
//					{
//						tmp = (T)((Class)tmp).newInstance();
//						mSingletons.put(mClass, tmp);
//					}
//					return (T)tmp;
//				}
//			}
//
//			Class newType = mTypeMappings.getOrDefault(mClass, mClass);
//
//			if (newType == null)
//			{
//				throw new IllegalArgumentException(mClass + " not registered");
//			}
//
//			T instance = null;
//
//			for (Constructor constructor : newType.getConstructors())
//			{
//				Inject annotation = (Inject)constructor.getAnnotation(Inject.class);
//
//				if (annotation != null)
//				{
//					Object[] values = mInjector.createMappedValues(annotation, constructor.getParameterTypes(), constructor.getParameterAnnotations());
//					instance = (T)constructor.newInstance(values);
//					break;
//				}
//			}
//
//			if (instance == null)
//			{
//				if (mDefaultMappings.containsKey(mClass))
//				{
//					instance = (T)mDefaultMappings.get(mClass).get();
//				}
//				else
//				{
//					try
//					{
//						instance = (T)newType.newInstance();
//					}
//					catch (IllegalAccessException | InstantiationException e)
//					{
//						// ignore
//					}
//				}
//			}
//
//			if (instance != null)
//			{
//				mInjector.injectMembers(instance);
//			}
//
//			return instance;
//		}
//		catch (SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e)
//		{
//			throw new IllegalStateException(e);
//		}
//	}
}

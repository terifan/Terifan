package org.terifan.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;


public class InjectorOLD
{
	private HashMap<Class, Object> mSingletons;
	private HashMap<Class, Class> mTypeMappings;
	private HashMap<Class, HashMap<String, Supplier<Object>>> mNamedMappings;
	private HashMap<Class, Supplier<Object>> mDefaultMappings;
	private HashMap<Class, Function<String, Object>> mLookupMappings;


	public InjectorOLD()
	{
		mSingletons = new HashMap<>();
		mTypeMappings = new HashMap<>();
		mNamedMappings = new HashMap<>();
		mDefaultMappings = new HashMap<>();
		mLookupMappings = new HashMap<>();
	}


	/**
	 * Map a single instance of an Object to a type.
	 *
	 * If the instance is a Class object then a new instance is created using that type on the first request and replacing the mapping of
	 * that type result in all future requests to return this single instance.
	 *
	 * @param aFrom
	 *   type to map from
	 * @param aTo
	 *   instance or class to map to
	 */
	public void bindSingleton(Class aFrom, Object aTo)
	{
		mSingletons.put(aFrom, aTo);
	}


	public void bind(Class aFrom, Class aTo)
	{
		mTypeMappings.put(aFrom, aTo);
	}


	public void bindSupplier(Class aFrom, Supplier<Object> aTo)
	{
		mDefaultMappings.put(aFrom, aTo);
	}


	public void bindNamedSupplier(Class aFrom, String aName, Supplier<Object> aTo)
	{
		mNamedMappings.computeIfAbsent(aFrom, e -> new HashMap<>()).put(aName, aTo);
	}


	public void bindNamedSupplier(Class aFrom, Function<String, Object> aTo)
	{
		mLookupMappings.put(aFrom, aTo);
	}


	/**
	 * Returns the provider used to obtain instances for the given type.
	 */
	public <T>Provider<T> getProvider(Class<T> aType)
	{
		return ()->{
			try
			{
				return getInstance(aType);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception | Error e)
			{
				throw new IllegalStateException(e);
			}
		};
	}


	/**
	 * Returns the appropriate instance for the given injection type; equivalent to getProvider(type).get().
	 */
	public <T> T getInstance(Class<T> aType)
	{
		if (aType == null)
		{
			throw new IllegalArgumentException("Provided argument is null.");
		}

		try
		{
			if (mSingletons.containsKey(aType))
			{
				synchronized (this)
				{
					Object tmp = mSingletons.get(aType);
					if (tmp instanceof Class)
					{
						tmp = (T)((Class)tmp).newInstance();
						mSingletons.put(aType, tmp);
					}
					return (T)tmp;
				}
			}

			Class newType = mTypeMappings.getOrDefault(aType, aType);

			if (newType == null)
			{
				throw new IllegalArgumentException(aType + " not registered");
			}

			T instance = null;

			for (Constructor constructor : newType.getConstructors())
			{
				Inject annotation = (Inject)constructor.getAnnotation(Inject.class);

				if (annotation != null)
				{
					Object[] values = createMappedValues(annotation, constructor.getParameterTypes(), constructor.getParameterAnnotations());
					instance = (T)constructor.newInstance(values);
					break;
				}
			}

			if (instance == null)
			{
				if (mDefaultMappings.containsKey(aType))
				{
					instance = (T)mDefaultMappings.get(aType).get();
				}
				else
				{
					try
					{
						instance = (T)newType.newInstance();
					}
					catch (IllegalAccessException | InstantiationException e)
					{
						// ignore
					}
				}
			}

			if (instance != null)
			{
				injectMembers(instance);
			}

			return instance;
		}
		catch (SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	/**
	 * Injects dependencies into the fields and methods of instance.
	 */
	public void injectMembers(Object aInstance)
	{
		try
		{
			for (Field field : aInstance.getClass().getDeclaredFields())
			{
				if (field.getAnnotation(Inject.class) != null)
				{
					field.setAccessible(true);

					Inject annotation = field.getAnnotation(Inject.class);

					if (annotation.value().isEmpty())
					{
						Class mappedType = mTypeMappings.get(field.getType());

						if (mappedType != null)
						{
							field.set(aInstance, getInstance(mappedType));
						}
						else if (!field.getType().isPrimitive())
						{
							field.set(aInstance, getInstance(field.getType()));
						}
					}
					else
					{
						Object mappedType = getSuppliedInstance(field.getType(), annotation.value());

						while (mappedType instanceof Class)
						{
							mappedType = getInstance((Class)mappedType);
						}

						field.set(aInstance, mappedType);
					}
				}
			}
			for (Method method : aInstance.getClass().getDeclaredMethods())
			{
				Inject annotation = method.getAnnotation(Inject.class);

				if (annotation != null)
				{
					method.setAccessible(true);
					method.invoke(aInstance, createMappedValues(annotation, method.getParameterTypes(), method.getParameterAnnotations()));
				}
			}
		}
		catch (IllegalAccessException | SecurityException | InvocationTargetException e)
		{
			throw new IllegalArgumentException(e);
		}
	}


	private Object getSuppliedInstance(Class aType, String aName)
	{
		Supplier<Object> supplier = mNamedMappings.get(aType).get(aName);

		Object mappedType;

		if (supplier != null)
		{
			mappedType = supplier.get();
		}
		else
		{
			Function<String, Object> function = mLookupMappings.get(aType);
			if (function != null)
			{
				mappedType = function.apply(aName);
			}
			else
			{
				mappedType = null;
			}
		}

		return mappedType;
	}


	private Object[] createMappedValues(Inject aInjectAnnotation, Class[] aParamTypes, Annotation[][] aAnnotations)
	{
		Object[] values = new Object[aParamTypes.length];
		for (int i = 0; i < aParamTypes.length; i++)
		{
			Class paramType = aParamTypes[i];

			String name = aInjectAnnotation.value();

			for (Annotation ann : aAnnotations[i])
			{
				if (ann instanceof Named)
				{
					name = ((Named)ann).value();
				}
			}

			if (name.isEmpty())
			{
				Class mappedType = mTypeMappings.get(paramType);

				if (mappedType != null)
				{
					values[i] = getInstance(mappedType);
				}
				else if (!paramType.isPrimitive())
				{
					values[i] = getInstance(paramType);
				}
			}
			else
			{
				Object mappedType = getSuppliedInstance(paramType, name);

				if (mappedType instanceof Class)
				{
					values[i] = getInstance((Class)mappedType);
				}
				else
				{
					values[i] = mappedType;
				}
			}
		}
		return values;
	}
}

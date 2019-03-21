package org.terifan.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;


public class Factory
{
	private HashMap<Class, Object> mSingletons;
	private HashMap<Class, Class> mTypeMappings;
	private HashMap<Class, HashMap<String, Supplier<Object>>> mNamedMappings;
	private HashMap<Class, Supplier<Object>> mDefaultMappings;
	private HashMap<Class, Function<String, Object>> mLookupMappings;


	public Factory()
	{
		mSingletons = new HashMap<>();
		mTypeMappings = new HashMap<>();
		mNamedMappings = new HashMap<>();
		mDefaultMappings = new HashMap<>();
		mLookupMappings = new HashMap<>();
	}


	public void addSingleton(Class aFrom, Object aTo)
	{
		mSingletons.put(aFrom, aTo);
	}


	public void addTypeMapping(Class aFrom, Class aTo)
	{
		mTypeMappings.put(aFrom, aTo);
	}


	public void addDefaultSupplier(Class aFrom, Supplier<Object> aTo)
	{
		mDefaultMappings.put(aFrom, aTo);
	}


	public void addNamedSupplier(Class aFrom, String aName, Supplier<Object> aTo)
	{
		mNamedMappings.computeIfAbsent(aFrom, e -> new HashMap<>()).put(aName, aTo);
	}


	public void addNamedSupplier(Class aFrom, Function<String, Object> aTo)
	{
		mLookupMappings.put(aFrom, aTo);
	}


	public <T> T newInstance(Class<T> aType)
	{
		if (aType == null)
		{
			throw new IllegalArgumentException("Provided argument is null.");
		}

		if (mSingletons.containsKey(aType))
		{
			return (T)mSingletons.get(aType);
		}

		try
		{
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
				prepareInstance(instance);
			}

			return instance;
		}
		catch (SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public void prepareInstance(Object aInstance)
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
							field.set(aInstance, newInstance(mappedType));
						}
						else if (!field.getType().isPrimitive())
						{
							field.set(aInstance, newInstance(field.getType()));
						}
					}
					else
					{
						Object mappedType = getSuppliedInstance(field.getType(), annotation.value());

						while (mappedType instanceof Class)
						{
							mappedType = newInstance((Class)mappedType);
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
					values[i] = newInstance(mappedType);
				}
				else if (!paramType.isPrimitive())
				{
					values[i] = newInstance(paramType);
				}
			}
			else
			{
				Object mappedType = getSuppliedInstance(paramType, name);

				if (mappedType instanceof Class)
				{
					values[i] = newInstance((Class)mappedType);
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

package org.terifan.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.terifan.util.Tuple;


public class Injector
{
	HashMap<Class, Factory> mBindings;
	HashMap<Tuple<Class, String>, Factory> mNamedBindings;


	public Injector()
	{
		mBindings = new HashMap<>();
		mNamedBindings = new HashMap<>();
	}


	public <T> BindingBuilder bind(Class<T> aType)
	{
		BindingBuilder bb = new BindingBuilder(this, aType);
		mBindings.put(aType, bb);
		return bb;
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
			Factory binding = mBindings.get(aType);

			if (binding != null)
			{
				return (T)binding.getInstance();
			}

			return (T)createInstance(aType);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception | Error e)
		{
			throw new IllegalStateException(e);
		}
	}


	/**
	 * Returns the appropriate instance for the given injection type; equivalent to getProvider(type).get().
	 */
	public <T> T getNamedInstance(Class<T> aType, String aName)
	{
		if (aType == null)
		{
			throw new IllegalArgumentException("Provided argument is null.");
		}

		try
		{
			Tuple<Class, String> key = new Tuple<>(aType, aName);

			Factory instance = mNamedBindings.get(key);

			if (instance == null)
			{
				return null;
			}

			return (T)instance.getInstance();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception | Error e)
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

					Object mappedType;

					if (annotation.value().isEmpty())
					{
						mappedType = getInstance(field.getType());
					}
					else
					{
						mappedType = getNamedInstance(field.getType(), annotation.value());

						if (mappedType == null)
						{
							throw new InjectionException("Failed to inject named member into " + aInstance.getClass() + ", not found: " + annotation);
						}
					}

					field.set(aInstance, mappedType);
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
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}


	Object createInstance(Class aType)
	{
		Object instance = null;

		for (Constructor constructor : aType.getConstructors())
		{
			Inject annotation = (Inject)constructor.getAnnotation(Inject.class);

			if (annotation != null)
			{
				try
				{
					instance = constructor.newInstance(createMappedValues(annotation, constructor.getParameterTypes(), constructor.getParameterAnnotations()));
					break;
				}
				catch (Exception | Error e)
				{
					e.printStackTrace(System.out);
				}
			}
		}

		if (instance == null)
		{
			try
			{
				instance = aType.newInstance();
			}
			catch (Exception | Error e)
			{
				// ignore
				e.printStackTrace(System.out);
			}
		}

		if (instance != null)
		{
			injectMembers(instance);
		}

		return instance;
	}


	Object[] createMappedValues(Inject aInjectAnnotation, Class[] aParamTypes, Annotation[][] aAnnotations)
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
				values[i] = getInstance(paramType);
			}
			else
			{
				values[i] = getNamedInstance(paramType, name);
			}
		}

		return values;
	}
}

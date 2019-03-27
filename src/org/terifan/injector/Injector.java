package org.terifan.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.terifan.util.Tuple;


public class Injector
{
	private boolean mStrict;
	HashMap<Class, Factory> mBindings;
	HashMap<Tuple<Class, String>, Factory> mNamedBindings;


	public Injector()
	{
		mBindings = new HashMap<>();
		mNamedBindings = new HashMap<>();
	}


	public boolean isStrict()
	{
		return mStrict;
	}


	/**
	 * Will throw an exception if an unbound instance is created from an injection. Default is false, which allow unbound objects to be
	 * created by the injector.
	 */
	public Injector setStrict(boolean aStrict)
	{
		mStrict = aStrict;
		return this;
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
			if (mStrict)
			{
				throw new IllegalArgumentException("Type not bound: " + aType);
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
		visit(aInstance, mInjectVisitor);
	}


	private final Visitor mPostConstructVisitor = new Visitor()
	{
		@Override
		public void visitMethod(Object aInstance, Class aType, Method aMethod) throws Exception
		{
			if (aMethod.getAnnotation(PostConstruct.class) != null)
			{
				logPostConstruct(aType, aMethod);

				aMethod.invoke(aInstance);
			}
		}
	};


	private final Visitor mInjectVisitor = new Visitor()
	{
		@Override
		public void visitField(Object aInstance, Class aType, Field aField) throws IllegalAccessException, SecurityException
		{
			if (aField.getAnnotation(Inject.class) != null)
			{
				aField.setAccessible(true);

				Inject annotation = aField.getAnnotation(Inject.class);

				Object mappedType;

				if (getName(annotation).isEmpty())
				{
					mappedType = getInstance(aField.getType());
				}
				else
				{
					mappedType = getNamedInstance(aField.getType(), getName(annotation));

					if (mappedType == null && !annotation.optional())
					{
						throw new InjectionException("Named instance of " + aType + " not found: " + getName(annotation));
					}
				}

				if (mappedType != null || !annotation.optional())
				{
					logInjection(aInstance, aField, mappedType, annotation);

					aField.set(aInstance, mappedType);
				}
			}
		}


		@Override
		public void visitMethod(Object aInstance, Class aType, Method aMethod) throws IllegalAccessException, InvocationTargetException
		{
			Inject annotation = aMethod.getAnnotation(Inject.class);

			if (annotation != null)
			{
				aMethod.setAccessible(true);
				aMethod.invoke(aInstance, createMappedValues(annotation, aMethod.getParameterTypes(), aMethod.getParameterAnnotations()));
			}
		}
	};


	private void visit(Object aInstance, Visitor aVisitor)
	{
		try
		{
			Class<?> type = aInstance.getClass();

			for (;;)
			{
				if (type == Object.class)
				{
					return;
				}

				aVisitor.visitClass(aInstance, type);

				for (Field field : type.getDeclaredFields())
				{
					aVisitor.visitField(aInstance, type, field);
				}

				for (Method method : type.getDeclaredMethods())
				{
					aVisitor.visitMethod(aInstance, type, method);
				}

				type = type.getSuperclass();
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception | Error e)
		{
			throw new IllegalArgumentException(e);
		}
	}


	Object createInstance(Class aType) throws InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Object instance = null;

		for (Constructor constructor : aType.getConstructors())
		{
			Inject annotation = (Inject)constructor.getAnnotation(Inject.class);

			if (annotation != null)
			{
				logCreation(aType, constructor);
				instance = constructor.newInstance(createMappedValues(annotation, constructor.getParameterTypes(), constructor.getParameterAnnotations()));
				break;
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

			visit(instance, mPostConstructVisitor);
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


	private void logInjection(Object aInstance, Field aField, Object aMappedType, Inject aAnnotation)
	{
		if (getName(aAnnotation).isEmpty())
		{
			System.out.printf("Injecting [%s] instance into [%s] instance field [%s]%n", aMappedType.getClass().getSimpleName(), aInstance.getClass().getSimpleName(), aField.getName());
		}
		else
		{
			System.out.printf("Injecting [%s] instance named [%s] into [%s] instance field [%s]%n", aMappedType.getClass().getSimpleName(), getName(aAnnotation), aInstance.getClass().getSimpleName(), aField.getName());
		}
	}


	private void logCreation(Class aType, Constructor aConstructor)
	{
		StringBuilder sb = new StringBuilder();
		for (Class cls : aConstructor.getParameterTypes())
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			sb.append(cls.getSimpleName());
		}
		System.out.printf("Creating instance of [%s] using constructor [%s]%n", aType.getSimpleName(), sb);
	}


	private void logPostConstruct(Class aType, Method aMethod)
	{
		System.out.printf("Invoking PostConstruct method [%s] in instance of [%s]%n", aMethod.getName(), aType.getSimpleName());
	}


	private String getName(Inject aAnnotation)
	{
		return aAnnotation.name().isEmpty() ? aAnnotation.value() : aAnnotation.name();
	}
}

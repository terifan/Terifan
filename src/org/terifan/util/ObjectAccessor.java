package org.terifan.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;


public class ObjectAccessor
{
	final static HashMap<Class, ObjectAccessor> CACHE = new HashMap<>();

	private final Class mType;
	private final Map<String, Field> mFields;
	private final Map<String, Method> mMethods;
	private final Map<String, Method> mMirrorMethods;


	public ObjectAccessor(Class aType)
	{
		mType = aType;
		mFields = new TreeMap<>();
		mMethods = new HashMap<>();
		mMirrorMethods = new HashMap<>();

		getDeclaredFields(aType);
		getDeclaredMethods(aType);

		for (Method method : mMethods.values())
		{
			String name = method.getName();

			if (startsWith(name, "get") || startsWith(name, "set") || startsWith(name, "is"))
			{
				String suffix = getMethodName(method);

				if (name.startsWith("set"))
				{
					mMirrorMethods.put("get" + suffix, method);
				}
				else
				{
					mMirrorMethods.put("set" + suffix, method);
				}
			}
		}
	}


	public Object getFieldValue(Object aObject, String aFieldName) throws IllegalAccessException
	{
		return getFieldValue(aObject, aFieldName, Object.class);
	}


	public <E> E getFieldValue(Object aObject, String aFieldName, Class<E> aType) throws IllegalAccessException
	{
		Field field = getField(aFieldName);
		field.setAccessible(true);
		return (E)field.get(aObject);
	}


	public ObjectAccessor setFieldValue(Object aObject, String aFieldName, Object aValue) throws IllegalAccessException
	{
		Field field = getField(aFieldName);
		field.setAccessible(true);
		field.set(aObject, aValue);
		return this;
	}


	public Class getType()
	{
		return mType;
	}


	public Method getMethod(String aName)
	{
		return mMethods.get(aName);
	}


	public Set<String> getMethodNames()
	{
		return mMethods.keySet();
	}


	/**
	 * Gets the mirror method if one exists.
	 *
	 * @param aName the properly formatted name of a method who's mirror is requested, e.g. "getName" will return "setName" method,
	 * "isValid" will return "setValid", assuming these methods exists.
	 * @return the mirror method or null
	 */
	public Method getMirrorMethod(String aName)
	{
		return mMirrorMethods.get(aName);
	}


	public String getMirrorName(String aName)
	{
		return mMirrorMethods.get(aName).getName();
	}


	public Map<String, Method> getMirrorMethods()
	{
		return mMirrorMethods;
	}


	public Field getField(String aName)
	{
		return mFields.get(aName);
	}


	public Set<String> getFieldNames()
	{
		return mFields.keySet();
	}


	private static String getMethodName(Method aMethod)
	{
		String name = aMethod.getName();

		if (name.startsWith("is"))
		{
			return name.substring(2);
		}

		return name.substring(3);
	}


	private void getDeclaredMethods(Class<?> aType)
	{
		for (Method method : aType.getDeclaredMethods())
		{
			if (!mMethods.containsKey(method.getName()) || mMethods.get(method.getName()).getReturnType() == Object.class)
			{
				mMethods.put(method.getName(), method);
			}
		}

		Class<?> sup = aType.getSuperclass();

		if (sup != Object.class)
		{
			getDeclaredMethods(sup);
		}
	}


	private void getDeclaredFields(Class<?> aType)
	{
		for (Field field : aType.getDeclaredFields())
		{
			if (!mFields.containsKey(field.getName()))
			{
				mFields.put(field.getName(), field);
			}
		}

		Class<?> sup = aType.getSuperclass();

		if (sup != Object.class)
		{
			getDeclaredFields(sup);
		}
	}


	private boolean startsWith(String aName, String aPrefix)
	{
		return aName.startsWith(aPrefix) && aName.length() > aPrefix.length();
	}


	public <T> T invoke(Class<T> aReturnType, String aName, Object aObject, Object... aArguments)
	{
		try
		{
			if (aArguments != null && aArguments.length > 0)
			{
//				System.out.println(aReturnType+" "+aObject+" "+aName+" "+Array.getLength(aArguments));

				return (T)mMethods.get(aName).invoke(aObject, aArguments);
			}

			System.out.println(aReturnType+" "+aObject+" "+aName+" "+mMethods.get(aName));

			return (T)mMethods.get(aName).invoke(aObject);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Failed to invoke method " + aName, e);
		}
	}


	private Class[] toTypeList(Object[] aArguments)
	{
		Class[] types = new Class[aArguments.length];
		for (int i = 0; i < types.length; i++)
		{
			types[i] = aArguments[i] == null ? null : aArguments[i].getClass();
		}
		return types;
	}


	public Filter filter()
	{
		return new Filter(new ArrayList<>(mMethods.values()));
	}


	public class Filter
	{
		private final ArrayList<Method> mMethods;


		private Filter(ArrayList<Method> aMethods)
		{
			mMethods = aMethods;
		}


		public Filter withPrefix(String aStartsWith)
		{
			mMethods.removeIf(m -> !m.getName().startsWith(aStartsWith));
			return this;
		}


		public Filter withName(String aPattern)
		{
			mMethods.removeIf(m -> !m.getName().matches(aPattern));
			return this;
		}


		/**
		 * @param aModifiers keep methods with one or more modifiers, e.g. withAnyModifiers(Modifier.PUBLIC, Modifier.PROTECTED) return all
		 * public or protected methods.
		 * @return the Filter object instance with methods still matching the criteria specified
		 */
		public Filter withAnyModifiers(int... aModifiers)
		{
			int flags = combine(aModifiers);
			mMethods.removeIf(m -> (m.getModifiers() & flags) == 0);
			return this;
		}


		/**
		 * @param aModifiers keep methods with all modifiers, e.g. withAllModifiers(Modifier.PUBLIC, Modifier.STATIC) return all public and
		 * static methods.
		 * @return the Filter object instance with methods still matching the criteria specified
		 */
		public Filter withAllModifiers(int... aModifiers)
		{
			int flags = combine(aModifiers);
			mMethods.removeIf(m -> (m.getModifiers() & flags) != flags);
			return this;
		}


		public Filter withAnyAnnotation(Class... aAnnotation)
		{
			mMethods.removeIf(m -> {
				for (Class c : aAnnotation)
				{
					if (m.isAnnotationPresent(c))
					{
						return false;
					}
				}
				return true;
			});
			return this;
		}


		public Filter withAllAnnotations(Class... aAnnotation)
		{
			mMethods.removeIf(m -> {
				int n = 0;
				for (Class c : aAnnotation)
				{
					if (m.isAnnotationPresent(c))
					{
						n++;
					}
				}
				return n == aAnnotation.length;
			});
			return this;
		}


		/**
		 * @param aReturnType
		 *   the expected return type and if null will filter void methods
		 */
		public Filter withReturn(Class aReturnType)
		{
			mMethods.removeIf(m -> aReturnType == null ? m.getReturnType() != null : !aReturnType.isAssignableFrom(m.getReturnType()));
			return this;
		}


		public Filter withMirror()
		{
			mMethods.removeIf(m -> !mMirrorMethods.containsKey(m.getName()));
			return this;
		}


		public Filter withParamTypes(Object... aParameterTypes)
		{
			return withParams(toTypeList(aParameterTypes));
		}


		public Filter withParams(Class... aParameterTypes)
		{
			mMethods.removeIf(m ->
			{
				Class[] pt = aParameterTypes;
				Class<?>[] parameterTypes = m.getParameterTypes();
				if (parameterTypes.length != pt.length)
				{
					return true;
				}
				for (int i = 0; i < pt.length; i++)
				{
					if (pt[i] != null && !pt[i].isAssignableFrom(parameterTypes[i]))
					{
						return true;
					}
				}
				return false;
			});
			return this;
		}


		public <T> T invoke(Class<T> aReturnType, Object aObject, Object... aParameters)
		{
			List<Method> list = withReturn(mType).list();

			if (list.isEmpty())
			{
				throw new IllegalStateException("No matching method found.");
			}

			Method m = list.get(0);

			try
			{
				return (T)(aParameters.length == 0 ? m.invoke(aObject) : m.invoke(aObject, aParameters));
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				throw new IllegalArgumentException(e);
			}
		}


		public List<Method> list()
		{
			return mMethods;
		}


		public Stream<Method> stream()
		{
			return mMethods.stream();
		}


		private int combine(int[] aModifiers)
		{
			int flags = 0;
			for (int f : aModifiers)
			{
				flags |= f;
			}
			return flags;
		}
	}


	private static class Y
	{
		int a;
		public Y(int aA)
		{
			this.a = aA;
		}
		public void setA(int aA)
		{
			this.a = aA;
		}
		@Override
		public String toString()
		{
			return "y="+a;
		}
	}
	private static class X extends Y
	{
		int a;
		public X(int aA)
		{
			super(-aA);
			this.a = aA;
		}
		@Override
		public void setA(int aA)
		{
			this.a = aA;
		}
		@Override
		public String toString()
		{
			return "x="+a+","+super.toString();
		}
	}


	public static void main(String... args)
	{
		try
		{
			ObjectAccessor map = new ObjectAccessor(String.class);

//			System.out.println(map.filterMethods("put.*ArrayList", null, null, CharSequence.class, null));
//			System.out.println(map.filterMethods(null, null, CharSequence.class));
//			System.out.println(map.filter().withAnyModifiers(Modifier.PRIVATE).withName("put.*").withParams(String.class, null).list());
//			System.out.println(map.filter().withAnyModifiers(Modifier.STATIC, Modifier.PROTECTED).list());

			for (String field : map.getFieldNames())
			{
				System.out.println(field + " = " + map.getFieldValue("test", field));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

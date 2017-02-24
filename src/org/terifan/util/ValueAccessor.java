package org.terifan.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;


public class ValueAccessor
{
	public static Object get(String aKey, Function<String,Object> aMap) throws NoSuchFieldException, IllegalAccessException
	{
		int i = aKey.indexOf(".");
		int j = aKey.indexOf(".", i + 1);
		int k = aKey.indexOf(".", Math.max(i, j) + 1);

		Object item;

		if (i != -1)
		{
			item = aMap.apply(aKey.substring(0, i));
		}
		else
		{
			item = aMap.apply(null);
		}

		if (j != -1)
		{
			int index = Integer.parseInt(aKey.substring(i + 1, j));

			if (List.class.isAssignableFrom(item.getClass()))
			{
				item = ((List)item).get(index);
			}
			else
			{
				item = Array.get(item, index);
			}
		}

		String fieldName = aKey.substring(Math.max(i, j) + 1);
		if (fieldName.contains("."))
		{
			fieldName = fieldName.substring(0, fieldName.indexOf("."));
		}

		System.out.println(fieldName);

		Field field = item.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);

		Object value = field.get(item);

		if (k != -1)
		{
			int index = Integer.parseInt(aKey.substring(k + 1));

			if (List.class.isAssignableFrom(value.getClass()))
			{
				value = ((List)value).get(index);
			}
			else
			{
				value = Array.get(value, index);
			}
		}

		return value;
	}


	public static void main(String ... args)
	{
		try
		{
			HashMap<String,Object> map = new HashMap<>();
			map.put("product", new Product(1, "apple", "red", "round"));
			map.put("products", Arrays.asList(new Product(1, "pear", "green", "round"), new Product(1, "banana", "yellow", "long")));

			Product product = new Product(1, "apple", "red", "round");

			System.out.println(ValueAccessor.get("value", e->product));

			System.out.println(ValueAccessor.get("product.value", e->product));

			System.out.println(ValueAccessor.get("product.id", map::get));
			System.out.println(ValueAccessor.get("products.0.value", map::get));
			System.out.println(ValueAccessor.get("products.1.value", map::get));
			System.out.println(ValueAccessor.get("products.0.properties.0", map::get));
			System.out.println(ValueAccessor.get("products.0.properties.1", map::get));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	static class Product
	{
		int id;
		String value;
		String[] properties;


		public Product(int aId, String aValue, String... aProperties)
		{
			this.id = aId;
			this.value = aValue;
			this.properties = aProperties;
		}
	}
}

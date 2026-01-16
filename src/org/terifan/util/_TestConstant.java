package org.terifan.util;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;


public class _TestConstant
{
	private final static Constant<UUID> ID = Constant.supplier(() -> UUID.randomUUID());

	private final static Constant<int[]> VALS = Constant.supplier(() -> new int[]
		{
			rnd(), rnd(), rnd()
		});

	private final static List<Dimension> DIMS = Constant.list(3, x -> {System.out.println("#dim");return new Dimension((Integer)x, (Integer)x);});

	private final static Function<Integer, String> numbers = Constant.function(n -> System.nanoTime() + "***" + n + " " + rnd());

	private final static Set<String> cities = Set.of("London", "Madrid", "Paris");

	private final static Function<String, String> cityToCountry = Constant.function(cities, city -> System.nanoTime() + "***" + city);

	private final static Map<String, String> cityToCountryMap = Constant.map(cities, city -> System.nanoTime() + "***" + city);


	static class Person
	{
		private final static Constant<String> mCountry = Constant.of();

		public static String getCountry()
		{
			return mCountry.orElseSet(() -> "Sweden");
		}
	}


	private static int rnd()
	{
		System.out.println("#rnd");
		return new Random().nextInt();
	}


	public static void main(String... args)
	{
		try
		{
//			int[] i = getValues();
//			int[] j = getValues();
//			int[] k = getValues();

			System.out.println(ID.get());
//			System.out.println(Arrays.toString(i));
			System.out.println(DIMS.get(2));
			System.out.println(DIMS.get(3));
			System.out.println(cityToCountry.apply("London"));
			System.out.println(cityToCountryMap.get("London"));
			System.out.println(numbers.apply(7));
			System.out.println(numbers.apply(7));
			System.out.println(VALS.get()[0]);

			System.out.println(cityToCountryMap.keySet());
			System.out.println(cityToCountryMap.size());
			System.out.println(""+cityToCountryMap.get("London"));
			System.out.println(""+cityToCountry.apply("London"));

			System.out.println(numbers.apply(6));
			System.out.println(numbers.apply(7));
			System.out.println(numbers.apply(6));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

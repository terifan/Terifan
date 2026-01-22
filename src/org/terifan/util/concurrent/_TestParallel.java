package org.terifan.util.concurrent;


public class _TestParallel
{
	public static void main(String... args)
	{
		try
		{
			Parallel.of(
				()->{System.out.println(1);},
				()->{System.out.println(2);},
				()->{System.out.println(3);},
				()->{System.out.println(4);},
				()->{System.out.println(5);},
				()->{System.out.println(6);},
				()->{System.out.println(7);},
				()->{System.out.println(8);},
				()->{System.out.println(9);}
			);

			Parallel.range(0, 10, 4).forEach((i,j) -> System.out.println(i));
			System.out.println("-----");
			Parallel.rangeClosed(0, 10, 5).forEach((i,j) -> System.out.println(i));
			System.out.println("-----");
			Parallel.range(0, 3).forEach((i) -> System.out.println(i));
			System.out.println("-----");
			Parallel.rangeClosed(0, 3).forEach((i) -> System.out.println(i));
			System.out.println("-----");
			Parallel.range(0, 10, 4).forEach((i, j) -> System.out.println(i + ", " + j));
			System.out.println("-----");
			Parallel.rangeClosed(0, 10, 4).forEach((i, j) -> System.out.println(i + ", " + j));
			System.out.println("-----");
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

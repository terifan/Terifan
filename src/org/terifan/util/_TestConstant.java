package org.terifan.util;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class _TestConstant
{
	private final static Constant<UUID> ID = Constant.supplier(() -> UUID.randomUUID());

//	private final static Constant<int[]> VALS = Constant.supplier(() -> new int[]
//	{
//		1, 2, 3
//	});

	private final static Constant<int[]> VALS = Constant.of();

	private final static List<Dimension> DIMS = Constant.list(3, x -> new Dimension((Integer)x, (Integer)x));

	private static Supplier<int[]> vals()
	{
		return () -> new int[]{1,2,3};
	}


	public static void main(String... args)
	{
		try
		{
			UUID id = ID.get();
			int[] i = VALS.orElseSet(vals());
			Dimension dim = DIMS.get(2);

			System.out.println(id);
			System.out.println(Arrays.toString(i));
			System.out.println(dim);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

package org.terifan.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class Parallel
{
//	private Parallel(int aFrom, int aTo, int aStep, boolean aInclusive)
//	{
//		if (aStep <= 0)
//		{
//			throw new IllegalArgumentException("Step must be positive.");
//		}
//
//		mFrom = aFrom;
//		mTo = aTo;
//		mStep = aStep;
//		mInclusive = aInclusive;
//	}


	private Parallel()
	{
	}


	public static ParallelSingle range(int aFrom, int aToExclusive)
	{
		return new ParallelSingle(aFrom, aToExclusive, false);
	}


	public static ParallelSingle rangeClosed(int aFrom, int aToInclusive)
	{
		return new ParallelSingle(aFrom, aToInclusive, true);
	}


	public static ParallelStep range(int aFrom, int aToExclusive, int aStep)
	{
		return new ParallelStep(aFrom, aToExclusive, aStep, false);
	}


	public static ParallelStep rangeClosed(int aFrom, int aToInclusive, int aStep)
	{
		return new ParallelStep(aFrom, aToInclusive, aStep, true);
	}


	/**
	 * 	Parallel.of(
	 *		()->{System.out.println(1);},
	 *		()->{System.out.println(2);},
	 *		()->{System.out.println(3);},
	 *	);
	 */
	public static void of(Runnable... aRunnables)
	{
		int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		ExecutorService executor = new ThreadPoolExecutor(cpu, cpu, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

		for (Runnable r : aRunnables)
		{
			executor.submit(r);
		}

		try
		{
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
		}
	}


	static class ParallelImpl
	{
		final int mFrom;
		final int mTo;
		final int mStep;
		final boolean mInclusive;

		ParallelImpl(int aFrom, int aTo, int aStep, boolean aInclusive)
		{
			if (aStep <= 0)
			{
				throw new IllegalArgumentException("Step must be positive.");
			}

			mFrom = aFrom;
			mTo = aTo;
			mStep = aStep;
			mInclusive = aInclusive;
		}
	}


	static class ParallelSingle extends ParallelImpl
	{
		ParallelSingle(int aFrom, int aTo, boolean aInclusive)
		{
			super(aFrom, aTo, 1, aInclusive);
		}


		/**
		 * Calls the Consumer with a value.
		 * <pre>
		 * Parallel.range(0, 10, 4).forEach(i->System.out.println(i));
		 *   "0"
		 *   "4"
		 *   "8"
		 *
		 * Parallel.rangeClosed(0, 10, 5).forEach(i->System.out.println(i));
		 *   "0"
		 *   "5"
		 *   "10"
		 *
		 * Parallel.range(0, 3).forEach(i->System.out.println(i));
		 *   "0"
		 *   "1"
		 *   "2"
		 *
		 * Parallel.rangeClosed(0, 3).forEach(i->System.out.println(i));
		 *   "0"
		 *   "1"
		 *   "2"
		 *   "3"
		 * </pre>
		 */
		public void forEach(Consumer<Integer> aConsumer)
		{
			int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

			ExecutorService executor = new ThreadPoolExecutor(cpu, cpu, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

			int to = mInclusive ? mTo + 1 : mTo;
			for (int i = mFrom; i < to; i += mStep)
			{
				int _i = Math.min(i, to);
				executor.submit(() -> aConsumer.accept(_i));
			}

			try
			{
				executor.shutdown();
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
			}
		}
	}


	static class ParallelStep extends ParallelImpl
	{
		ParallelStep(int aFrom, int aTo, int aStep, boolean aInclusive)
		{
			super(aFrom, aTo, aStep, aInclusive);
		}


		/**
		 * Calls the BiConsumer with a starting value and ending value equal to starting value plus step size.
		 * <pre>
		 * Parallel.range(0, 10, 4).forEach((i,j)->System.out.println(i+", "+j));
		 *   "0, 3"
		 *   "4, 7"
		 *   "8, 9"
		 *
		 * Parallel.rangeClosed(0, 10, 4).forEach((i,j)->System.out.println(i+", "+j));
		 *   "0, 4"
		 *   "4, 8"
		 *   "8, 10"
		 * </pre>
		 */
		public void forEach(BiConsumer<Integer, Integer> aConsumer)
		{
			int cpu = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

			ExecutorService executor = new ThreadPoolExecutor(cpu, cpu, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

			for (int i = mFrom; i < mTo; i += mStep)
			{
				int _i = i;
				int _j = Math.min(i + mStep, mTo) - (mInclusive ? 0 : 1);
				executor.submit(() -> aConsumer.accept(_i, _j));
			}

			try
			{
				executor.shutdown();
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
			}
		}
	}


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

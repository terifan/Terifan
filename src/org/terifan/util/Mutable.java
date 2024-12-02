package org.terifan.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * A container object which may or may not contain a value. If the container is empty {@code isEmpty()} returns {@code true}. If a non-null
 * value is present {@code isPresent()} returns {@code true}. If the container value is null {@code isNull()} returns {@code true}.
 * <p>
 * Contrary to the Optional-class this container treats null as a valid value.
 * <p>
 * Contrary to the Optional-class this container can have it's contained value updated using {@code set()} method.
 */
public class Mutable<T>
{
	private final static Mutable EMPTY = new Mutable();
	public T value;


	public Mutable()
	{
		this(null);
	}


	private Mutable(T aValue)
	{
		value = aValue;
	}


	public static <E> Mutable<E> empty()
	{
		return new Mutable<>((E)EMPTY);
	}


	public static <E> Mutable<E> of(E aValue)
	{
		return new Mutable<>(aValue);
	}


	public Mutable<T> emptyNull()
	{
		if (value == null)
		{
			value = (T)EMPTY;
		}
		return this;
	}


	public Mutable<T> or(Supplier<Mutable> aMutable)
	{
		return isPresent() ? this : aMutable.get();
	}


	public T orElseGet(Supplier<T> aSupplier)
	{
		return isPresent() ? value : aSupplier.get();
	}


	public Mutable<T> orElseThrow()
	{
		if (!isPresent())
		{
			throw new NoSuchElementException();
		}
		return this;
	}


	public Mutable<T> orElseThrow(Supplier<RuntimeException> aSupplier)
	{
		if (!isPresent())
		{
			throw aSupplier.get();
		}
		return this;
	}


	public Mutable<T> orSet(T aValue)
	{
		if (!isPresent())
		{
			value = aValue;
		}
		return this;
	}


	public T orElseGet(Predicate<T> aPredicate, Supplier<T> aSupplier)
	{
		return isPresent() && aPredicate.test(value) ? value : aSupplier.get();
	}


	public T orElse(T aValue)
	{
		return value == EMPTY ? aValue : value;
	}


	public Mutable<T> compareAndSet(T aCompare, T aSet)
	{
		if (Objects.equals(aCompare, value))
		{
			value = aSet;
		}
		return this;
	}


	public T get()
	{
		if (value == EMPTY)
		{
			throw new NoSuchElementException("No value present");
		}
		return value;
	}


	public boolean isPresent()
	{
		return value != null && value != EMPTY;
	}


	public boolean isEmpty()
	{
		return value == EMPTY;
	}


	public boolean isNull()
	{
		return value == null;
	}


	public Mutable ifPresent(Consumer<? super T> aAction)
	{
		if (isPresent())
		{
			aAction.accept(value);
		}
		return this;
	}


	public Mutable ifNotEmpty(Consumer<? super T> aAction)
	{
		if (!isEmpty())
		{
			aAction.accept(value);
		}
		return this;
	}


	public Mutable ifEmpty(Runnable aAction)
	{
		if (isEmpty())
		{
			aAction.run();
		}
		return this;
	}


	public Mutable<T> set(T aValue)
	{
		value = aValue;
		return this;
	}


	public Mutable<T> clear()
	{
		value = (T)EMPTY;
		return this;
	}


	public void print()
	{
		System.out.print(value);
	}


	public void println()
	{
		System.out.println(value);
	}


	@Override
	public boolean equals(Object aOther)
	{
		if (aOther == this)
		{
			return true;
		}
		if (aOther instanceof Mutable v)
		{
			return value.equals(v.value);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		return Objects.hashCode(value);
	}


	@Override
	public String toString()
	{
		return Objects.toString(value, "mutable:null");
	}


	public static void main(String ... args)
	{
		try
		{
			Mutable.of(0).compareAndSet(0, -1).println();

			Mutable.of(Long.MAX_VALUE).println();
			new Mutable<>(Long.MAX_VALUE).println();
			new Mutable<Long>().set(Long.MAX_VALUE).println();

			Mutable<Long> a = new Mutable<>(Long.MAX_VALUE);
			Mutable<Long> b = new Mutable<Long>().set(Long.MAX_VALUE);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

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
	private final static Mutable EMPTY = new Mutable(null);
	private T mValue;


	private Mutable(T aValue)
	{
		mValue = aValue;
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
		if (mValue == null)
		{
			mValue = (T)EMPTY;
		}
		return this;
	}


	public Mutable<T> or(Supplier<Mutable> aMutable)
	{
		return isPresent() ? this : aMutable.get();
	}


	public T orElseGet(Supplier<T> aSupplier)
	{
		return isPresent() ? mValue : aSupplier.get();
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
			mValue = aValue;
		}
		return this;
	}


	public T orElseGet(Predicate<T> aPredicate, Supplier<T> aSupplier)
	{
		return isPresent() && aPredicate.test(mValue) ? mValue : aSupplier.get();
	}


	public T orElse(T aValue)
	{
		return mValue == EMPTY ? aValue : mValue;
	}


	public T get()
	{
		if (mValue == EMPTY)
		{
			throw new NoSuchElementException("No value present");
		}
		return mValue;
	}


	public boolean isPresent()
	{
		return mValue != null && mValue != EMPTY;
	}


	public boolean isEmpty()
	{
		return mValue == EMPTY;
	}


	public boolean isNull()
	{
		return mValue == null;
	}


	public Mutable ifPresent(Consumer<? super T> aAction)
	{
		if (isPresent())
		{
			aAction.accept(mValue);
		}
		return this;
	}


	public Mutable ifNotEmpty(Consumer<? super T> aAction)
	{
		if (!isEmpty())
		{
			aAction.accept(mValue);
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
		mValue = aValue;
		return this;
	}


	public Mutable<T> clear()
	{
		mValue = (T)EMPTY;
		return this;
	}


	@Override
	public boolean equals(Object aOther)
	{
		if (aOther == this)
		{
			return true;
		}
		if (aOther instanceof Mutable)
		{
			return mValue.equals(((Mutable)aOther).mValue);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		return Objects.hashCode(mValue);
	}


	@Override
	public String toString()
	{
		return Objects.toString(mValue, "mutable:null");
	}


	public static void main(String[] args)
	{
		Mutable.of(1).ifPresent(System.out::println);
		Mutable.empty().set(2).ifNotEmpty(System.out::println);
		Mutable.empty().or(() -> Mutable.of(3)).ifNotEmpty(System.out::println);
		Mutable.empty().orSet(4).ifNotEmpty(System.out::println);
		System.out.println(Mutable.empty().orElse(5));
		System.out.println(Mutable.of(6).orElseGet(e -> e == 6, () -> 0));
		System.out.println(Mutable.empty().orElseGet(() -> 7));
		Mutable.of(null).emptyNull().ifEmpty(() -> System.out.println("undefined"));

//		Mutable.empty().orElseThrow(() -> new RuntimeException());
	}
}

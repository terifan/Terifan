package org.terifan.injector;

import java.util.function.Supplier;


public class BuilderRootNamed
{
	private final Builder mInjector;
	BuilderRootNamed(Builder aInjector, Class aType)
	{
		mInjector = aInjector;
	}
	public void asSingleton()
	{
	}
	public BuilderRootNamedTo to(Class aType)
	{
		return null;
	}
	public void toInstance(Object aInstance)
	{
	}
	public void toProvider(Supplier aSupplier)
	{
	}
	public BuilderRootNamedIn in(Class aType)
	{
		return null;
	}
}

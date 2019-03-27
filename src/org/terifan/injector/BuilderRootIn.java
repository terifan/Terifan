package org.terifan.injector;

import java.util.HashMap;
import java.util.function.Supplier;



public class BuilderRootIn
{
	private final Builder mInjector;
	BuilderRootIn(Builder aInjector, Class aType)
	{
		mInjector = aInjector;
	}
	public BuilderRootIn asSingleton()
	{
		return null;
	}
	public BuilderRootIn to(Class aType)
	{
		return null;
	}
	public BuilderRootIn toInstance(Object aInstance)
	{
		return null;
	}
	public BuilderRootIn toProvider(Supplier aSupplier)
	{
		return null;
	}
	public BuilderRootIn in(Class aType)
	{
		return null;
	}
}

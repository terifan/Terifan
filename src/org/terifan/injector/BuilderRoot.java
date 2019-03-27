package org.terifan.injector;

import java.util.HashMap;
import java.util.function.Supplier;
import org.terifan.util.Tuple;


public class BuilderRoot
{
	private HashMap<Class, BuilderRootTo> mBindings;
	private HashMap<Tuple<Class,String>, BuilderRootIn> mScopedBindings;
	private HashMap<Tuple<Class,String>, BuilderRootNamed> mScopedBindings2;

	private final Class mType;
	private boolean mSingleton;
	private final Builder mInjector;
	private Object mInstance;

	BuilderRoot(Builder aInjector, Class aType)
	{
		mBindings = new HashMap<>();
		mScopedBindings = new HashMap<>();
		mScopedBindings2 = new HashMap<>();
		mInjector = aInjector;
		mType = aType;
	}
	public void asSingleton()
	{
		mSingleton = true;
	}
	public BuilderRootTo to(Class aType)
	{
		BuilderRootTo binding = new BuilderRootTo(mInjector, null);
		mBindings.put(aType, binding);
		return binding;
	}
	public void toInstance(Object aInstance)
	{
	}
	public void toProvider(Supplier aSupplier)
	{
	}
	public BuilderRootIn in(Class aType)
	{
		BuilderRootIn binding = new BuilderRootIn(mInjector, aType);
		mScopedBindings.put(new Tuple<>(aType,null), binding);
		return binding;
	}
	public BuilderRootNamed named(String aName)
	{
		BuilderRootNamed binding = new BuilderRootNamed(mInjector, null);
		mScopedBindings2.put(new Tuple<>(null,aName), binding);
		return binding;
	}


	Object getInstance()
	{
		if (mInstance == null && mSingleton)
		{
			mInstance = mInjector.getInstance(mType);
		}
		return mInstance;
	}
}

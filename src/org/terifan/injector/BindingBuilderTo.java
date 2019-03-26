package org.terifan.injector;

import org.terifan.util.Tuple;


public class BindingBuilderTo<T> extends Factory<T>
{
	private Injector mInjector;
	private Class mFromClass;
	private Class<T> mToClass;
	private String mName;


	BindingBuilderTo(Injector aInjector, Class aFromClass, Class aToClass, String aName)
	{
		mInjector = aInjector;
		mToClass = aToClass;
		mFromClass = aFromClass;
		mName = aName;
	}


	public BindingBuilderScope in(Class aScope)
	{
		BindingBuilderScope builder = new BindingBuilderScope(mInjector, mFromClass, mToClass, mName, aScope);
		mInjector.mBindings.put(mFromClass, builder);
		return builder;
	}


	@Override
	T getInstance()
	{
		return mInjector.getInstance(mToClass);
	}


	public void asSingleton()
	{
		if (mName == null)
		{
			mInjector.mBindings.put(mFromClass, new BindingBuilderSingleton(mInjector, mToClass));
		}
		else
		{
			mInjector.mNamedBindings.put(new Tuple<>(mFromClass, mName), new BindingBuilderSingleton(mInjector, mToClass));
		}
	}
}

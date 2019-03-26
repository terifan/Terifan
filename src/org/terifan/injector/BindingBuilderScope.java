package org.terifan.injector;

import org.terifan.util.Tuple;



public class BindingBuilderScope<T> extends Factory<Object>
{
	private Injector mInjector;
	private Class mFromClass;
	private Class<T> mToClass;
	private String mName;
	private Class mScope;


	BindingBuilderScope(Injector aInjector, Class aFromClass, Class aToClass, String aName, Class aScope)
	{
		mInjector = aInjector;
		mFromClass = aFromClass;
		mToClass = aToClass;
		mName = aName;
		mScope = aScope;
	}


	public void asSingleton()
	{
		if (mName != null)
		{
			mInjector.mNamedBindings.put(new Tuple<>(mFromClass, mName), new BindingBuilderSingleton(mInjector, mToClass));
		}
		else
		{
			mInjector.mBindings.put(mFromClass, new BindingBuilderSingleton(mInjector, mToClass));
		}
	}


	@Override
	T getInstance() throws InstantiationException, IllegalAccessException
	{
		return mInjector.getInstance(mToClass);
	}
}

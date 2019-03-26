package org.terifan.injector;


public class BindingBuilderInstance<T> extends Factory<T>
{
	private Injector mInjector;
	private T mToInstance;


	BindingBuilderInstance(Injector aInjector, T aToInstance)
	{
		mInjector = aInjector;
		mToInstance = aToInstance;
	}


	@Override
	T getInstance()
	{
		return mToInstance;
	}
}

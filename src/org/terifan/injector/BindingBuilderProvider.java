package org.terifan.injector;


public class BindingBuilderProvider<T> extends Factory<T>
{
	private Injector mInjector;
	private Provider<T> mToProvider;


	BindingBuilderProvider(Injector aInjector, Provider<T> aToProvider)
	{
		mInjector = aInjector;
		mToProvider = aToProvider;
	}


	@Override
	T getInstance()
	{
		return mToProvider.get();
	}
}

package org.terifan.injector;


public class BindingBuilderSingleton<T> extends Factory<T>
{
	private Injector mInjector;
	private Class<T> mToClass;
	private T mToInstance;


	BindingBuilderSingleton(Injector aInjector, Class<T> aToClass)
	{
		mInjector = aInjector;
		mToClass = aToClass;
	}


	@Override
	public synchronized T getInstance() throws IllegalAccessException, InstantiationException
	{
		if (mToInstance == null)
		{
			mToInstance = mInjector.getInstance(mToClass);
		}

		return mToInstance;
	}
}

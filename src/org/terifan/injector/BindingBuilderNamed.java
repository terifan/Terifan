package org.terifan.injector;


public class BindingBuilderNamed<T> extends Factory<T>
{
	private Injector mInjector;
	private Class<T> mFromClass;
	private String mName;
	private Factory<T> mFactory;


	BindingBuilderNamed(Injector aInjector, Class aFromClass, String aName)
	{
		mInjector = aInjector;
		mFromClass = aFromClass;
		mName = aName;
		mFactory = new BindingBuilderProvider<>(mInjector, ()->mInjector.getInstance(mFromClass));
	}


	public void asSingleton()
	{
		mFactory = new BindingBuilderSingleton(mInjector, mFromClass);
	}


	public <T> BindingBuilderTo to(Class<T> aToClass)
	{
		mFactory = new BindingBuilderTo(mInjector, mFromClass, aToClass, mName);
		return (BindingBuilderTo)mFactory;
	}


	public void toInstance(Object aInstance)
	{
		mFactory = new BindingBuilderInstance(mInjector, aInstance);
	}


	public void toProvider(Provider aProvider)
	{
		mFactory = new BindingBuilderProvider(mInjector, aProvider);
	}


	@Override
	T getInstance() throws IllegalAccessException, InstantiationException
	{
		return mFactory.getInstance();
	}
}

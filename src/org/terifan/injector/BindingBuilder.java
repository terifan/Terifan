package org.terifan.injector;

import java.lang.reflect.InvocationTargetException;
import org.terifan.util.Tuple;


public class BindingBuilder<T> extends Factory<T>
{
	private Injector mInjector;
	private Class<T> mFromClass;


	BindingBuilder(Injector aInjector, Class aFromClass)
	{
		mInjector = aInjector;
		mFromClass = aFromClass;
	}


	public void asSingleton()
	{
		mInjector.mBindings.put(mFromClass, new BindingBuilderSingleton(mInjector, mFromClass));
	}


	public <T> BindingBuilderTo to(Class<T> aToClass)
	{
		BindingBuilderTo to = new BindingBuilderTo(mInjector, mFromClass, aToClass, null);
		mInjector.mBindings.put(mFromClass, to);
		return to;
	}


	public void toInstance(Object aInstance)
	{
		BindingBuilderInstance to = new BindingBuilderInstance(mInjector, aInstance);
		mInjector.mBindings.put(mFromClass, to);
	}


	public void toProvider(Provider aProvider)
	{
		BindingBuilderProvider to = new BindingBuilderProvider(mInjector, aProvider);
		mInjector.mBindings.put(mFromClass, to);
	}


	public BindingBuilderNamed named(String aName)
	{
		BindingBuilderNamed builder = new BindingBuilderNamed(mInjector, mFromClass, aName);
		mInjector.mNamedBindings.put(new Tuple<>(mFromClass, aName), builder);
		return builder;
	}


	@Override
	T getInstance() throws IllegalAccessException, InstantiationException, InvocationTargetException
	{
		return (T)mInjector.createInstance(mFromClass);
	}
}

package org.terifan.injector;


public abstract class Factory<T>
{
	abstract T getInstance() throws IllegalAccessException, InstantiationException;
}

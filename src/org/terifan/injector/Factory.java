package org.terifan.injector;

import java.lang.reflect.InvocationTargetException;


public abstract class Factory<T>
{
	abstract T getInstance() throws IllegalAccessException, InstantiationException, InvocationTargetException;
}

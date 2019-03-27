package org.terifan.injector;

import java.util.HashMap;


public class BuilderRootTo
{
	private final Builder mInjector;
	private final Class mType;
	BuilderRootTo(Builder aInjector, Class aType)
	{
		mInjector = aInjector;
		mType = aType;
	}

	public void asSingleton()
	{
	}
}

package org.terifan.injector;

import java.util.HashMap;


public class Builder
{
	private HashMap<Class, BuilderRoot> mBindings;


	public Builder()
	{
		mBindings = new HashMap<>();
	}


	public BuilderRoot bind(Class aType)
	{
		BuilderRoot binding = new BuilderRoot(this, aType);
		mBindings.put(aType, binding);
		return binding;
	}


	Object getInstance(Class aType)
	{
		return mBindings.get(aType).getInstance();
	}
}

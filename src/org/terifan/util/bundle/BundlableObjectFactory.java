package org.terifan.util.bundle;


public interface BundlableObjectFactory<T extends Bundlable>
{
	T newInstance();
}

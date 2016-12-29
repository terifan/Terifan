package org.terifan.util;


@FunctionalInterface
public interface OnCompletion<T>
{
	void onCompletion(T aItem);
}

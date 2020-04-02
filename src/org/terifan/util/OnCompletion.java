package org.terifan.util;

import java.util.concurrent.Future;


@FunctionalInterface
public interface OnCompletion
{
	void onCompletion(Future aItem);
}

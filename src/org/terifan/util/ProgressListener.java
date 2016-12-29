package org.terifan.util;


public interface ProgressListener<E>
{
	public void setWorkProgress(int aProgress, int aMaximum, E aItem);
}

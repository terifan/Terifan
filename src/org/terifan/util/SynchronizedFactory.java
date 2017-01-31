package org.terifan.util;

import java.util.HashMap;


public class SynchronizedFactory<K, V> extends HashMap<K, V>
{
	private static final long serialVersionUID = 1L;


	public V getOrCreate(K aKey, Creator<K, V> aCreator)
	{
		V item = get(aKey);

		if (item != null)
		{
			return item;
		}

		synchronized (this)
		{
			item = get(aKey);

			if (item != null)
			{
				return item;
			}

			item = aCreator.create(aKey);

			put(aKey, item);
		}

		return item;
	}


	@FunctionalInterface
	public interface Creator<K, V>
	{
		V create(K aKey);
	}
}

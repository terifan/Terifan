package org.terifan.sql;

import java.util.HashMap;



public abstract class AbstractEntity
{
	protected EntityManager em;
	HashMap<String,Object> mPersistedValues;


	protected void bind(EntityManager em)
	{
		this.em = em;
	}
}

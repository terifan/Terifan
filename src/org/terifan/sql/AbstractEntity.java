package org.terifan.sql;


public abstract class AbstractEntity
{
	protected EntityManager em;


	protected void bind(EntityManager em)
	{
		this.em = em;
	}
}

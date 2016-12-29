package org.terifan.sql;


public interface ValueProducer
{
	Object produce(Object aEntity, Column aColumn);
}

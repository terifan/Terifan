package deprecated.org.terifan.io.serialization;

import java.io.IOException;


public interface Property
{
	public String getName() throws IOException;


	public Object get() throws IOException;


	public boolean isPrimitive() throws IOException;


	public Class getType() throws IOException;
}

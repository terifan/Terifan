package deprecated.org.terifan.io.serialization;

import java.io.IOException;
import org.terifan.util.bundle.Bundle;


public class BundleProperty implements Property
{
	private Bundle mBundle;
	private final String mName;


	public BundleProperty(Bundle aOwner, String aName)
	{
		mName = aName;
	}


	@Override
	public String getName() throws IOException
	{
		return mName;
	}


	@Override
	public Object get() throws IOException
	{
		return mBundle.get(mName);
	}


	@Override
	public boolean isPrimitive() throws IOException
	{
		return false;
	}


	@Override
	public Class getType() throws IOException
	{
		return get().getClass();
	}
}

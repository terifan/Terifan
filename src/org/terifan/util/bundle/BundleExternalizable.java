package org.terifan.util.bundle;

import java.io.IOException;


public interface BundleExternalizable
{
	void readExternal(Bundle aBundle) throws IOException;

	void writeExternal(Bundle aBundle) throws IOException;
}

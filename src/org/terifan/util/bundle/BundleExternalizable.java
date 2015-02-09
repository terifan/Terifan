package org.terifan.util.bundle;

import java.io.IOException;


public interface BundleExternalizable
{
	default void readExternal(Bundle aBundle) throws IOException {};

	default void writeExternal(Bundle aBundle) throws IOException {};
}

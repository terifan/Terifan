package org.terifan.io.serialization;


public interface Writer
{
	void startOutput();
	void endOutput();

	void startObject(Object aObject, String aTypeName, int aPropertyCount);
	void endObject();

	void startProperty(Property aProperty, String aName, String aTypeName);
	void nextProperty();
	void endProperty();

	void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType);
	void nextElement();
	void endArray();

	void writeNull();
	void writePrimitive(Object aPrimitive, String aTypeName);
	void writeReference(Object aObject);
}

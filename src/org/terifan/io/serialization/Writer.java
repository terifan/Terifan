package org.terifan.io.serialization;

import java.lang.reflect.Field;


public interface Writer
{
	void startOutput();
	void endOutput();

	void startObject(Object aObject, String aTypeName, int aFieldCount);
	void endObject();

	void startField(Field aField, String aName, String aTypeName);
	void nextField();
	void endField();

	void startArray(Object aArray, int aDepth, int aLength, String aTypeName, boolean aNulls, String aArrayType);
	void nextElement();
	void endArray();

	void writePrimitive(Object aPrimitive, String aTypeName);
	void writeReference(Object aObject);
}

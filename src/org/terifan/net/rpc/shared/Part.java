package org.terifan.net.rpc.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.terifan.io.ByteArray;


public class Part implements Serializable
{
	private PartType mPartType;
	private String mServiceName;
	private String mMethodName;
	private String mPartId;
	private Object [] mParameters;


	private Part()
	{
	}


	public Part(String aServiceName, String aMethodName, String aPartId, PartType aPartType, Object ... aParameters)
	{
		if (aServiceName == null || aMethodName == null || aMethodName.isEmpty())
		{
			throw new IllegalArgumentException();
		}

		mServiceName = aServiceName;
		mMethodName = aMethodName;
		mPartId = aPartId;
		mPartType = aPartType;
		mParameters = aParameters;
	}


	public String getServiceName()
	{
		return mServiceName;
	}


	public String getMethodName()
	{
		return mMethodName;
	}


	public String getPartId()
	{
		return mPartId;
	}


	public Object getParameter(int aIndex)
	{
		return mParameters[aIndex];
	}


	public int getParameterCount()
	{
		return mParameters.length;
	}


	public Object [] getParameters()
	{
		return mParameters.clone();
	}


	public PartType getPartType()
	{
		return mPartType;
	}


	@Override
	public String toString()
	{
		return getServiceName()+"."+getMethodName()+Arrays.asList(mParameters);
	}


	public void encode(OutputStream aOutputStream) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try (DataOutputStream dos = new DataOutputStream(baos))
		{
			dos.write(mPartType.ordinal());
			dos.writeInt(-1); // placeholder for length value
			dos.writeUTF(mServiceName);
			dos.writeUTF(mMethodName);
			dos.writeUTF(mPartId);
			dos.write(mParameters.length);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			for (Object mParameter : mParameters)
			{
				oos.writeObject(mParameter);
			}
		}

		byte [] temp = baos.toByteArray();
		ByteArray.BE.putInt(temp, 1, temp.length);

		aOutputStream.write(temp);
	}


	public static Part decode(InputStream aInputStream) throws IOException
	{
		try
		{
			DataInputStream dis = new DataInputStream(aInputStream);

			PartType partType = PartType.values()[dis.read()];
			dis.skipBytes(4); // TODO: read and check part length
			String serviceName = dis.readUTF();
			String methodName = dis.readUTF();
			String partId = dis.readUTF();
			Object [] parameters = new Object[dis.read()];

			ObjectInputStream ois = new ObjectInputStream(dis);
			for (int i = 0; i < parameters.length; i++)
			{
				parameters[i] = ois.readObject();
			}

			return new Part(serviceName, methodName, partId, partType, parameters);
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(e);
		}
	}
}
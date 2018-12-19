package org.terifan.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import org.terifan.util.Convert;


public class MultiPartPackage
{
//	private ArrayList<Object> mSources;
//	private int mIndex;
//	private InputStream mInputStream;
//	private MessageDigest mMessageDigest;
//
//
//	public MultiPartPackage()
//	{
//		mSources = new ArrayList<>();
//
//		try
//		{
//			mMessageDigest = MessageDigest.getInstance("md5");
//		}
//		catch (NoSuchAlgorithmException e)
//		{
//			throw new IllegalArgumentException(e);
//		}
//	}
//
//
//	public MultiPartPackage(InputStream aInputStream)
//	{
//		mInputStream = aInputStream;
//
//		try
//		{
//			mMessageDigest = MessageDigest.getInstance("md5");
//		}
//		catch (NoSuchAlgorithmException e)
//		{
//			throw new IllegalArgumentException(e);
//		}
//	}
//
//
//	public void writeInt(int aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeLong(long aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeString(String aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeStream(InputStream aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeStream(File aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeObject(Object aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public void writeBytes(byte[] aObject)
//	{
//		mSources.add(aObject);
//	}
//
//
//	public InputStream finish()
//	{
//		return new InputStream()
//		{
//			InputStream mInputStream;
//
//			{
//				init();
//			}
//
//			@Override
//			public int read() throws IOException
//			{
//				int c = mInputStream.read();
//
//				if (c == -1)
//				{
//					init();
//					c = mInputStream.read();
//				}
//
//				if (c != -1)
//				{
//					mMessageDigest.update((byte)c);
//				}
//
//				return c;
//			}
//
//
//			private void init()
//			{
//				try
//				{
//					if (mIndex < mSources.size())
//					{
//						Object source = mSources.get(mIndex++);
//
//						if (source instanceof Integer)
//						{
//							source = Convert.toBytes((int)source);
//						}
//						else if (source instanceof Long)
//						{
//							source = Convert.toBytes((long)source);
//						}
//						else if (source instanceof String)
//						{
//							source = Convert.encodeUTF8((String)source);
//						}
//						else if (source instanceof MessageDigest)
//						{
//							source = ((MessageDigest)source).digest();
//						}
//						else
//						{
//							ByteArrayOutputStream baos = new ByteArrayOutputStream();
//							try (ObjectOutputStream oos = new ObjectOutputStream(baos))
//							{
//								oos.writeObject(source);
//							}
//							source = baos.toByteArray();
//						}
//
//						if (source instanceof byte[])
//						{
//							mInputStream = new ByteArrayInputStream((byte[])source);
//						}
//						else if (source instanceof File)
//						{
//							mInputStream = new BufferedInputStream(new FileInputStream((File)source));
//						}
//						else if (source instanceof InputStream)
//						{
//							mInputStream = (InputStream)source;
//						}
//						else
//						{
//							throw new IllegalArgumentException("Unsupported source: " + source.getClass());
//						}
//					}
//				}
//				catch (IOException e)
//				{
//					mInputStream = null;
//				}
//			}
//		};
//	}
//
//
//	public static void main(String ... args)
//	{
//		try
//		{
//			MultiPartPackage mpp = new MultiPartPackage();
//			mpp.writeInt(7);
//			mpp.writeLong(7777777777L);
//			mpp.writeString("hello");
//			mpp.writeString("world");
//			mpp.writeStream(new File("d:\\output.png"));
//			mpp.writeObject(new Date());
//			mpp.writeBytes("lalala".getBytes());
//
//			byte[] data = Streams.readAll(mpp.finish());
//			System.out.println(new String(data));
//
////			mpp = new MultiPartPackage(new ByteArrayInputStream(data));
////			mpp.readInt();
////			mpp.readLong();
////			mpp.readString();
////			mpp.readString();
////			mpp.readStream();
////			mpp.readObject(Date.class);
////			mpp.readBytes();
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}

package org.terifan.net.rpc.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.terifan.io.ByteArray;
import org.terifan.security.Cipher;
import org.terifan.security.CipherMode;
import org.terifan.security.SHA256;


/*
 * message          = [header][array of part][padding]
 * headers          = [public header][protected header]
 * public header    = [protocol version][content type][length][message index][session id]
 * protected header = [time][compression][part count][checksum]
 * part             = [part type][part length][service name][method name][parameter count][parameter 0]..[parameter n]
 *
 *
 * overhead:
 *   message headers  (1+1+2+4)+(8+1+1) = 18    -3
 *   part headers     1+4+2+2+1 = 10			-4
 *   session id       16						-8
 *   padding          0..15
 *   checksum         32						-16
 *                    -------
 *                avg 83
 */
public class Message implements Iterable<Part>
{
	private final static int PROTOCOL_VERSION = 1;
	private final static int CHECKSUM_SIZE = 32;
	public final static int PUBLIC_HEADER_SIZE = 1 + 1 + 2 + 4 + 16;
	private final static int PROTECTED_HEADER_SIZE = 8 + 1 + 1 + CHECKSUM_SIZE;
	private final static int PART_OFFSET = PUBLIC_HEADER_SIZE + PROTECTED_HEADER_SIZE;
	private final static int CIPHER_BLOCK_SIZE = 16;

	private UUID mSessionID;
	private List<Part> mParts;
	private MessageType mMessageType;
	private Compression mCompression;
	private int mMessageIndex;
	private int mLength;
	private long mTime;


	public Message(MessageType aMessageType, Part... aParts)
	{
		mMessageType = aMessageType;
		mParts = new ArrayList<>();
		mParts.addAll(Arrays.asList(aParts));
	}


	public static Message decodeHeader(byte[] aPublicHeader) throws IOException
	{
		ByteBuffer bb = ByteBuffer.wrap(aPublicHeader);

		if ((bb.get() & 0xff) != PROTOCOL_VERSION)
		{
			throw new IOException("Unsupported protocol version");
		}

		MessageType messageType = MessageType.values()[0xff & bb.get()];

		Message message = new Message(messageType);
		message.mMessageIndex = 0xffff & bb.getShort();
		message.mLength = bb.getInt();
		message.mSessionID = new UUID(bb.getLong(), bb.getLong());

		return message;
	}


	public int getMessageIndex()
	{
		return mMessageIndex;
	}


	public long getTime()
	{
		return mTime;
	}


	public void addPart(Part aPart)
	{
		mParts.add(aPart);
	}


	public Part getPart(int aIndex)
	{
		return mParts.get(aIndex);
	}


	public int getPartCount()
	{
		return mParts.size();
	}


	public MessageType getMessageType()
	{
		return mMessageType;
	}


	public void setMessageType(MessageType aMessageType)
	{
		mMessageType = aMessageType;
	}


	public void setSessionID(UUID aSessionID)
	{
		mSessionID = aSessionID;
	}


	public UUID getSessionID()
	{
		return mSessionID;
	}


	public int getLength()
	{
		return mLength;
	}


	public Compression getCompression()
	{
		return mCompression;
	}


	@Override
	public Iterator<Part> iterator()
	{
		return mParts.iterator();
	}


	public byte[] encode(int aMessageIndex, Cipher aCipher, Compression aCompression) throws IOException
	{
		if (aCompression == null)
		{
			throw new IllegalArgumentException("aCompression is null");
		}

		mMessageIndex = aMessageIndex;
		mTime = System.currentTimeMillis();

		// allocate space for headers
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.write(new byte[PUBLIC_HEADER_SIZE]);
		baos.write(new byte[PROTECTED_HEADER_SIZE]);

		OutputStream os;
		if (aCompression == Compression.NO_COMPRESSION)
		{
			os = baos;
		}
		else
		{
			os = new DeflaterOutputStream(baos, new Deflater(aCompression.getAlgorithm()));
		}

		// write all parts
		for (Part part : mParts)
		{
			part.encode(os);
		}
		os.close();

		// pad message
		if (aCipher != null)
		{
			int len = baos.size() - PUBLIC_HEADER_SIZE;
			if ((len % CIPHER_BLOCK_SIZE) != 0)
			{
				int padd = ((len + CIPHER_BLOCK_SIZE - 1) & ~(CIPHER_BLOCK_SIZE - 1)) - len;
				for (int i = 0; i < padd; i++)
				{
					baos.write((byte)(padd - i));
				}
			}
		}

		byte[] data = baos.toByteArray();
		ByteBuffer bb = ByteBuffer.wrap(data);

		// public header
		bb.put((byte)PROTOCOL_VERSION);
		bb.put((byte)mMessageType.ordinal());
		bb.putShort((short)mMessageIndex);
		bb.putInt(data.length);
		bb.putLong(mSessionID == null ? 0L : mSessionID.getMostSignificantBits());
		bb.putLong(mSessionID == null ? 0L : mSessionID.getLeastSignificantBits());

		// protected header
		bb.putLong(mTime);
		bb.put((byte)aCompression.ordinal());
		bb.put((byte)mParts.size());

		// checksum
		int co = PUBLIC_HEADER_SIZE + PROTECTED_HEADER_SIZE - CHECKSUM_SIZE;
		MessageDigest md = new SHA256();
		md.update(data, 0, co);
		md.update(data, co + CHECKSUM_SIZE, data.length - co - CHECKSUM_SIZE);
		bb.position(co);
		bb.put(md.digest());

		if (aCipher != null)
		{
			aCipher.init(Cipher.ENCRYPT, CipherMode.CBC, getIV(aCipher));
			aCipher.update(data, PUBLIC_HEADER_SIZE, data, PUBLIC_HEADER_SIZE, data.length - PUBLIC_HEADER_SIZE);
		}

		mLength = data.length;

		return data;
	}


	public void decode(Cipher aCipher, byte[] aMessage) throws IOException
	{
		if (aCipher != null && mMessageType != MessageType.CHALLANGE)
		{
			if (((aMessage.length - PUBLIC_HEADER_SIZE) % CIPHER_BLOCK_SIZE) != 0)
			{
				throw new IOException("Encoded message not a multiple of " + CIPHER_BLOCK_SIZE + " bytes long.");
			}

			aCipher.init(Cipher.DECRYPT, CipherMode.CBC, getIV(aCipher));
			aCipher.update(aMessage, PUBLIC_HEADER_SIZE, aMessage.length - PUBLIC_HEADER_SIZE);
		}

		int co = PUBLIC_HEADER_SIZE + PROTECTED_HEADER_SIZE - CHECKSUM_SIZE;
		MessageDigest md = new SHA256();
		md.update(aMessage, 0, co);
		md.update(aMessage, co + CHECKSUM_SIZE, aMessage.length - co - CHECKSUM_SIZE);
		byte[] checksum = md.digest();

		if (!ByteArray.equals(checksum, 0, aMessage, co, CHECKSUM_SIZE))
		{
			throw new IOException("Message checksum failed.");
		}

		ByteBuffer bb = ByteBuffer.wrap(aMessage);
		bb.position(PUBLIC_HEADER_SIZE);

		// protected header
		mTime = bb.getLong();
		mCompression = Compression.values()[bb.get()];
		int partCount = bb.get() & 0xff;

		InputStream is = new ByteArrayInputStream(aMessage, PART_OFFSET, mLength - PART_OFFSET);

		if (mCompression != Compression.NO_COMPRESSION)
		{
			is = new InflaterInputStream(is);
		}

		for (int i = 0; i < partCount; i++)
		{
			addPart(Part.decode(is));
		}
	}


	private byte[] getIV(Cipher aCipher)
	{
		byte[] out = new byte[CIPHER_BLOCK_SIZE];

		ByteArray.BE.putLong(out, 0, mSessionID.getMostSignificantBits());
		ByteArray.BE.putLong(out, 8, mSessionID.getLeastSignificantBits());

		out[0] ^= (byte)(mMessageIndex >>> 24);
		out[1] ^= (byte)(mMessageIndex >> 16);
		out[2] ^= (byte)(mMessageIndex >> 8);
		out[3] ^= (byte)(mMessageIndex);

		aCipher.init(Cipher.ENCRYPT, CipherMode.ECB);
		aCipher.update(out, 0, out.length);

		return out;
	}
}

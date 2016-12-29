package org.terifan.security;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;
import org.terifan.io.ByteArray;


public class Keccak extends MessageDigest
{
	private final static int [] PI = {0, 10, 20, 5, 15, 16, 1, 11, 21, 6, 7, 17, 2, 12, 22, 23, 8, 18, 3, 13, 14, 24, 9, 19, 4};
	private final static int [] CHIA = {1, 2, 3, 4, 0, 6, 7, 8, 9, 5, 11, 12, 13, 14, 10, 16, 17, 18, 19, 15, 21, 22, 23, 24, 20};
	private final static int [] CHIB = {2, 3, 4, 0, 1, 7, 8, 9, 5, 6, 12, 13, 14, 10, 11, 17, 18, 19, 15, 16, 22, 23, 24, 20, 21};
	private final static int[] RHO_OFFSETS =
	{
		0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43, 25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2,
		14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61,
		20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 0, 1, 62, 28, 27, 36, 44, 6, 55,
		20, 3, 10, 43, 25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18,
		39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28,
		36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43, 25, 39, 41, 45, 15,
		21, 8, 18, 2, 61, 56, 14, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15,
		21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8,
		25, 43, 62, 18, 39, 61, 20, 44, 0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43, 25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14, 3, 6,
		10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41,
		56, 8, 25, 43, 62, 18, 39, 61, 20, 44, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
	};
	private final static long[] ROUND_CONSTANTS =
	{
		1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L,
		136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L,
		-9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L,
		2147483649L, -9223372034707259384L, 1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L,
		-9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L,
		-9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L,
		-9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L, 1L, 32898L, -9223372036854742902L,
		-9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L,
		2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L,
		32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L, 1L, 32898L,
		-9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L,
		2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L,
		-9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L,
		-9223372034707259384L
	};

	private final static int ROUNDS = 24;
	private final static int PERMUTATION_SIZE_WORDS = 1600 / 64;
	private final static int MAX_RATE_BYTES = 1536 / 8;

	private transient final int mRate;
	private transient final int mHashLength;
	private transient byte[] mState = new byte[8 * PERMUTATION_SIZE_WORDS];
	private transient byte[] mDataQueue = new byte[MAX_RATE_BYTES];
	private transient int mBytesInQueue;
	private transient long[][] mStateAsWords = new long[2][PERMUTATION_SIZE_WORDS];


	public Keccak(int aHashLength)
	{
		super("keccak-" + aHashLength);

		switch (aHashLength)
		{
			case 224:
				mRate = 1152 / 8;
				break;
			case 256:
				mRate = 1088 / 8;
				break;
			case 384:
				mRate = 832 / 8;
				break;
			case 512:
				mRate = 576 / 8;
				break;
			default:
				throw new IllegalStateException();
		}

		mHashLength = aHashLength / 8;
	}


	@Override
	protected int engineGetDigestLength()
	{
		return mHashLength;
	}


	@Override
	protected void engineReset()
	{
		mBytesInQueue = 0;

		Arrays.fill(mState, (byte)0);
		Arrays.fill(mDataQueue, (byte)0);
		Arrays.fill(mStateAsWords[0], (byte)0);
		Arrays.fill(mStateAsWords[1], (byte)0);
	}


	@Override
	protected void engineUpdate(ByteBuffer input)
	{
		byte[] buf = new byte[input.remaining()];
		input.get(buf);
		engineUpdate(buf, 0, buf.length);
	}


	@Override
	protected void engineUpdate(byte input)
	{
		byte[] data = {input};
		engineUpdate(data, 0, 1);
	}


	@Override
	protected byte[] engineDigest()
	{
		byte[] hashval = new byte[mHashLength];
		finish(hashval);

		engineReset();

		return hashval;
	}


	@Override
	protected int engineDigest(byte[] buf, int offset, int len) throws DigestException
	{
		byte[] hashval = new byte[mHashLength];
		finish(hashval);
		System.arraycopy(hashval, 0, buf, offset, len);

		engineReset();

		return mHashLength;
	}


	@Override
	protected void engineUpdate(byte[] aBuffer, int aOffset, int aLength)
	{
		while (aLength > 0)
		{
			int len = Math.min(aLength, mRate - mBytesInQueue);
			System.arraycopy(aBuffer, aOffset, mDataQueue, mBytesInQueue, len);

			mBytesInQueue += len;
			aLength -= len;
			aOffset += len;

			if (mBytesInQueue == mRate)
			{
				processBlock();
				mBytesInQueue = 0;
			}
		}
	}


	private void fromBytesToWords()
	{
		for (int i = 0, j = 0; i < PERMUTATION_SIZE_WORDS; i++, j+=8)
		{
			mStateAsWords[0][i] = ByteArray.LE.getLong(mState, j);
		}
	}


	private void fromWordsToBytes()
	{
		for (int i = 0, j = 0; i < PERMUTATION_SIZE_WORDS; i++, j+=8)
		{
			ByteArray.LE.putLong(mState, j, mStateAsWords[0][i]);
		}
	}


	private void finish(byte[] output)
	{
		Arrays.fill(mDataQueue, mBytesInQueue, mRate, (byte)0);
		mDataQueue[mBytesInQueue] |= 1;
		mDataQueue[mRate - 1] |= 128;

		processBlock();

		System.arraycopy(mState, 0, output, 0, mHashLength);
	}


	private void processBlock()
	{
		for (int i = 0; i < mRate; i++)
		{
			mState[i] ^= mDataQueue[i];
		}

		fromBytesToWords();

		for (int round = 0, n = 0; round < ROUNDS; round++, n=1-n)
		{
			long[] state = mStateAsWords[n];
			long[] other = mStateAsWords[1-n];
			
			// theta
//			theta(state);
			long C0 = state[0] ^ state[5] ^ state[10] ^ state[15] ^ state[20];
			long C1 = state[1] ^ state[6] ^ state[11] ^ state[16] ^ state[21];
			long C2 = state[2] ^ state[7] ^ state[12] ^ state[17] ^ state[22];
			long C3 = state[3] ^ state[8] ^ state[13] ^ state[18] ^ state[23];
			long C4 = state[4] ^ state[9] ^ state[14] ^ state[19] ^ state[24];

			long D0 = ((C1 << 1) | (C1 >>> 63)) ^ C4;
			long D1 = ((C2 << 1) | (C2 >>> 63)) ^ C0;
			long D2 = ((C3 << 1) | (C3 >>> 63)) ^ C1;
			long D3 = ((C4 << 1) | (C4 >>> 63)) ^ C2;
			long D4 = ((C0 << 1) | (C0 >>> 63)) ^ C3;

			for (int i = 0; i < 25; i+=5)
			{
				state[i    ] ^= D0;
				state[i + 1] ^= D1;
				state[i + 2] ^= D2;
				state[i + 3] ^= D3;
				state[i + 4] ^= D4;
			}

			// rho
//			rho(state);
			for (int i = 0; i < 25; i++)
			{
				state[i] = Long.rotateLeft(state[i], RHO_OFFSETS[i]);
			}

			// pi
			for (int i = 0; i < 25; i++)
			{
				other[PI[i]] = state[i];
			}

			// chi
//			chi(other);
			for (int y = 0, i = 0; y < 5; y++)
			{
				long E0 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
				long E1 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
				long E2 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
				long E3 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
				long E4 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
				other[i-5] = E0;
				other[i-4] = E1;
				other[i-3] = E2;
				other[i-2] = E3;
				other[i-1] = E4;
			}

			mStateAsWords[1-n][0] ^= ROUND_CONSTANTS[round];
		}

		fromWordsToBytes();
	}


	private static void rho(long[] state)
	{
		for (int i = 0; i < 25; i++)
		{
			state[i] = Long.rotateLeft(state[i], RHO_OFFSETS[i]);
		}
	}


	private static void chi(long[] other)
	{
		for (int y = 0, i = 0; y < 5; y++)
		{
			long E0 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
			long E1 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
			long E2 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
			long E3 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
			long E4 = other[i] ^ ~other[CHIA[i]] & other[CHIB[i++]];
			other[i-5] = E0;
			other[i-4] = E1;
			other[i-3] = E2;
			other[i-2] = E3;
			other[i-1] = E4;
		}
	}


	private static void theta(long[] state)
	{
		long C0 = state[0] ^ state[5] ^ state[10] ^ state[15] ^ state[20];
		long C1 = state[1] ^ state[6] ^ state[11] ^ state[16] ^ state[21];
		long C2 = state[2] ^ state[7] ^ state[12] ^ state[17] ^ state[22];
		long C3 = state[3] ^ state[8] ^ state[13] ^ state[18] ^ state[23];
		long C4 = state[4] ^ state[9] ^ state[14] ^ state[19] ^ state[24];

		long D0 = ((C1 << 1) | (C1 >>> 63)) ^ C4;
		long D1 = ((C2 << 1) | (C2 >>> 63)) ^ C0;
		long D2 = ((C3 << 1) | (C3 >>> 63)) ^ C1;
		long D3 = ((C4 << 1) | (C4 >>> 63)) ^ C2;
		long D4 = ((C0 << 1) | (C0 >>> 63)) ^ C3;

		for (int i = 0; i < 25; i+=5)
		{
			state[i    ] ^= D0;
			state[i + 1] ^= D1;
			state[i + 2] ^= D2;
			state[i + 3] ^= D3;
			state[i + 4] ^= D4;
		}
	}


//	private static void init()
//	{
//		// Initialize round constants
//		long[] R = new long[ROUNDS];
//		for (int i = 0, val = 0x01; i < ROUNDS; i++)
//		{
//			R[i] = 0;
//			for (int j = 0; j < 7; j++)
//			{
//				int bitPosition = (1 << j) - 1;
//
//				if ((val & 0x01) != 0)
//				{
//					R[i] ^= 1L << bitPosition;
//				}
//
//				if ((val & 0x80) != 0)
//				{
//					val = (val << 1) ^ 0x71;
//				}
//				else
//				{
//					val <<= 1;
//				}
//			}
//		}
//
//		// Initialize rho offsets
//		RHO_OFFSETS[0] = 0;
//		for (int t = 0, x = 1, y = 0; t < 24; t++)
//		{
//			int i = (x % 5) + 5 * (y % 5);
//			RHO_OFFSETS[i] = ((t + 1) * (t + 2) / 2) & 63;
//			int newX = y % 5;
//			int newY = (2 * x + 3 * y) % 5;
//			x = newX;
//			y = newY;
//
//		}
//	}
}
package org.terifan.util;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.util.Iterator;


public class StreamTokenizer implements Iterator<char[]>
{
	private Reader mReader;
	private int mCurrentPosition;
	private int mNextPosition;
	private char mDelimiter;
	private char mNextDelimiter;
	private CharArrayWriter mBuffer;


	public StreamTokenizer(Reader aReader, char aDelimiter)
	{
		mReader = aReader;
		mDelimiter = aDelimiter;
		mBuffer = new CharArrayWriter();
	}


	public int getCurrentPosition()
	{
		return mCurrentPosition;
	}


	public int getNextPosition()
	{
		return mNextPosition;
	}


	public StreamTokenizer setDelimiter(char aDelimiter)
	{
		if (mCurrentPosition != mNextPosition && aDelimiter != mNextDelimiter)
		{
			throw new IllegalArgumentException("Delimiter change illegal: characters already consumed with previous delimiter: " + (int)mNextDelimiter + ", new: " + (int)aDelimiter);
		}

		mDelimiter = aDelimiter;

		return this;
	}


	public char getDelimiter()
	{
		return mDelimiter;
	}


	@Override
	public boolean hasNext()
	{
		return hasNext(mDelimiter);
	}


	public boolean hasNext(char aDelimiter)
	{
		if (mCurrentPosition != mNextPosition && aDelimiter != mNextDelimiter)
		{
			throw new IllegalArgumentException("Delimiter change illegal: characters already consumed with previous delimiter: " + (int)mNextDelimiter + ", new: " + (int)aDelimiter);
		}

		try
		{
			if (mNextPosition == mCurrentPosition)
			{
				for (int c; (c = mReader.read()) != -1;)
				{
					mNextPosition++;

					if (c == aDelimiter)
					{
						break;
					}

					mBuffer.write(c);
				}
			}

			mNextDelimiter = aDelimiter;

			return mNextPosition != mCurrentPosition;
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}


	@Override
	public char[] next()
	{
		return next(mDelimiter);
	}


	public char[] next(char aDelimiter)
	{
		if (!hasNext(aDelimiter))
		{
			return null;
		}
		char[] c = mBuffer.toCharArray();
		mBuffer.reset();
		mCurrentPosition = mNextPosition;
		return c;
	}


	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}


	public char[] remaining()
	{
		try
		{
			for (int c; (c = mReader.read()) != -1;)
			{
				mNextPosition++;
				mBuffer.write(c);
			}

			char[] c = mBuffer.toCharArray();
			mBuffer.reset();
			return c;
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}


	public String nextString()
	{
		return new String(next());
	}


	public String remainingString()
	{
		return new String(remaining());
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			StreamTokenizer t = new StreamTokenizer(new StringReader("hello,,world"), ',');
//			Log.out.println("#"+new String(t.next())+"#");
//			Log.out.println("#"+new String(t.next())+"#");
//			Log.out.println("#"+new String(t.next('r'))+"#");
//			Log.out.println("#"+new String(t.next())+"#");
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}
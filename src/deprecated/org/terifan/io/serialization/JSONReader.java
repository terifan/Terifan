package deprecated.org.terifan.io.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;


public class JSONReader
{
	private PushbackReader mReader;
	private Factory mFactory;


	public void unmarshal(Factory aFactory, InputStream aInputStream) throws IOException
	{
		mFactory = aFactory;
		mReader = new PushbackReader(new InputStreamReader(aInputStream), 2);

		if (read() != '{')
		{
			throw new IOException("Expected a start curly bracket as first character.");
		}

		readObject();
	}


	private void readObject() throws IOException
	{
		mFactory.startObject();

		for (boolean first = true;; first = false)
		{
			char c = readChar();

			if (c == '}')
			{
				mFactory.endObject();

				return;
			}
			if (first)
			{
				unread(c);
			}

			if (!first && c != ',' && c != ';')
			{
				throw new IOException("Expected comma sign between elements in object: found ascii " + c);
			}

			String key = readString((char)0);

			mFactory.startElement(key);
			
			c = readChar();

			if (c != '=' && c != ':')
			{
				throw new IOException("Expected colon sign between key and value: found ascii " + c);
			}

			readValue();
			
			mFactory.endElement();
		}
	}

	private void readValue() throws IOException
	{
		char c = readChar();

		if (c == '{')
		{
			readObject();
			return;
		}
		if (c == '[')
		{
			readArray();
			return;
		}
		if (c == '\"' || c == '\'')
		{
			mFactory.setValue(readString(c));
			return;
		}

		StringBuilder sb = new StringBuilder();

		for (;;)
		{
			if (c == '\\')
			{
				c = read();
			}
			if (c == '}' || c == ']' || c == ',' || c == ';')
			{
				unread(c);
				mFactory.setValue(processValue(sb.toString().trim()));
				return;
			}
			sb.append(c);

			c = read();
		}
	}


	private String readString(char aTerminator) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		for (boolean first = aTerminator == 0;; first = false)
		{
			char c = first ? readChar() : read();

			if (first && (c == '\'' || c == '\"'))
			{
				aTerminator = c;
				continue;
			}
			else if (c == '\\')
			{
				c = read();
			}
			else if (c == aTerminator)
			{
				return sb.toString();
			}
			if (aTerminator == 0)
			{
				if (c == ':' || c == '=')
				{
					unread(c);
					return sb.toString().trim();
				}
				if (c == '}' || c == ']' || c == ',' || c == ';')
				{
					throw new IOException("Exptected colon sign after key: key=" + c);
				}
			}
			sb.append(c);
		}
	}


	private void readArray() throws IOException
	{
		mFactory.startArray();
		
		for (boolean first = true; ; first = false)
		{
			char c = readChar();
			if (c == ']')
			{
				mFactory.endArray();

				return;
			}

			if (first)
			{
				unread(c);
			}
			else if (c != ',' && c != ';')
			{
				throw new IOException("Expected comma between elements in array.");
			}

			mFactory.startArrayElement();
			
			readValue();

			mFactory.endArrayElement();
		}
	}


	private char readChar() throws IOException
	{
		for (;;)
		{
			int c = read();

			if (c == -1)
			{
				throw new IOException();
			}

			if (!Character.isWhitespace((char)c))
			{
				return (char)c;
			}
		}
	}


	private Object processValue(String aString)
	{
		if (aString.equalsIgnoreCase("null"))
		{
			return null;
		}
		if (aString.equalsIgnoreCase("true"))
		{
			return Boolean.TRUE;
		}
		if (aString.equalsIgnoreCase("false"))
		{
			return Boolean.FALSE;
		}
		if (aString.matches("^[0-9]+$"))
		{
			return new Long(aString);
		}
		if (aString.matches("^[0-9.]+$"))
		{
			return new Double(aString);
		}

		return aString;
	}


	private char read() throws IOException
	{
		char c = (char)mReader.read();
//		Log.out.println("  read "+c);
		return c;
	}


	private void unread(char aChar) throws IOException
	{
//		Log.out.println("unread "+aChar);
		mReader.unread(aChar);
	}
}

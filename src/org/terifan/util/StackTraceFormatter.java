package org.terifan.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;


public class StackTraceFormatter
{
	public static String toString(Throwable aThrowable)
	{
		if (aThrowable == null)
		{
			return "null";
		}

		StringWriter w = new StringWriter();
		aThrowable.printStackTrace(new PrintWriter(w, true));

		return w.toString();
	}


	public static String toStringFlatten(Throwable aException)
	{
		return toString(aException).replace('\r',' ').replace('\n',' ').replace('\t',' ').replace("    "," ").replace("   "," ").replace("  "," ");
	}


	public static String serialize(StackTraceElement[] aElements)
	{
		StringBuilder sb = new StringBuilder();

		for (StackTraceElement st : aElements)
		{
			if (sb.length() > 0)
			{
				sb.append("\n");
			}
			if (st.getFileName() != null)
			{
				sb.append("\t");
			}
			sb.append(serialize(st));
		}

		return sb.toString();
	}


	public static String serialize(StackTraceElement aElement)
	{
		if (aElement.getFileName() == null)
		{
			return aElement.getClassName() + ": " + aElement.getMethodName(); // return the message line
		}

		return aElement.getClassName() + "." + aElement.getMethodName() + "(" + (aElement.getFileName() != null ? aElement.getFileName() : "unknown") + ":" + aElement.getLineNumber() + ")";
	}


	public static StackTraceElement[] parse(String aStackTrace) throws NumberFormatException
	{
		ArrayList<StackTraceElement> frames = new ArrayList<>();

		for (String s : aStackTrace.split("\n"))
		{
			frames.add(parseElement(s.trim()));
		}

		return frames.toArray(StackTraceElement[]::new);
	}


	public static StackTraceElement parseElement(String aElement) throws NumberFormatException
	{
		aElement = aElement.trim();

		int i0 = aElement.indexOf(":");
		int i1 = aElement.lastIndexOf(":");
		int i2 = aElement.lastIndexOf("(");

		if (i2 == -1 || i0 != -1 && i1 < i2)
		{
			String className = aElement.substring(0, i0);
			String message = (i2 == -1 ? aElement.substring(i0 + 1) : aElement.substring(i0 + 1, i2)).trim();

			return new StackTraceElement(className, message, null, -1);
		}

		String tmp = aElement.substring(0, i2);
		String methodName = aElement.substring(tmp.lastIndexOf(".") + 1, i2);
		String className = aElement.substring(0, tmp.lastIndexOf("."));
		String fileName = aElement.substring(i2 + 1, i1);
		String lineNumber = aElement.substring(i1 + 1, aElement.lastIndexOf(")"));

		return new StackTraceElement(className, methodName, fileName, Integer.parseInt(lineNumber));
	}


	public static void main(String ... args)
	{
		try
		{
			String s =
			"java.lang.IllegalStateException: error in subroutine\n" +
			"	test.TestLogging$Dummy.dummyMethod1(TestLogging.java:70)\n" +
			"	test.TestLogging.main(TestLogging.java:22)\n" +
			"java.lang.IllegalStateException: exception message\n" +
			"	test.TestLogging$Dummy.dummyMethod2(TestLogging.java:76)\n" +
			"	test.TestLogging$Dummy.dummyMethod1(TestLogging.java:66)\n" +
			"	test.TestLogging.main(TestLogging.java:22)";

//			s = toString(new Exception("test")).replace("\r", "").trim();

			StackTraceElement[] el = parse(s);

			String t = serialize(el);

			System.out.println(t);
			System.out.println();
			System.out.println(s.equals(t));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

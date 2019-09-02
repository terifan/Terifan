package org.terifan.ui;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.terifan.util.Calendar;


public class PrintTextArea extends JScrollPane
{
	private static final long serialVersionUID = 1L;

	private JTextPane mServiesDisplayOutput;
	private StyledDocument mDocument;


	public enum Style
	{
		BLUE,
		GREEN,
		YELLOW,
		RED,
		CYAN,
		MAGENTA,
		BLACK,
		BOLD_BLUE,
		BOLD_GREEN,
		BOLD_YELLOW,
		BOLD_RED,
		BOLD_CYAN,
		BOLD_MAGENTA,
		BOLD_BLACK
//		DEBUG,
//		VERBOSE,
//		INFO,
//		ERROR,
//		FATAL
	}


	public PrintTextArea()
	{
		mServiesDisplayOutput = new JTextPane();
		mServiesDisplayOutput.setEditable(false);

		super.setViewportView(mServiesDisplayOutput);

		mDocument = mServiesDisplayOutput.getStyledDocument();

		javax.swing.text.Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		javax.swing.text.Style regular = mDocument.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "Arial");

		javax.swing.text.Style s;

		s = mDocument.addStyle(Style.RED.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,0));

		s = mDocument.addStyle(Style.BOLD_RED.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.GREEN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,0));

		s = mDocument.addStyle(Style.BOLD_GREEN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.BLUE.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,200));

		s = mDocument.addStyle(Style.BOLD_BLUE.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.YELLOW.name(), regular);
		StyleConstants.setForeground(s, new Color(200,200,0));

		s = mDocument.addStyle(Style.BOLD_YELLOW.name(), regular);
		StyleConstants.setForeground(s, new Color(200,200,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.CYAN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,200));

		s = mDocument.addStyle(Style.BOLD_CYAN.name(), regular);
		StyleConstants.setForeground(s, new Color(0,200,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.MAGENTA.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,200));

		s = mDocument.addStyle(Style.BOLD_MAGENTA.name(), regular);
		StyleConstants.setForeground(s, new Color(200,0,200));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle(Style.BLACK.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,0));

		s = mDocument.addStyle(Style.BOLD_BLACK.name(), regular);
		StyleConstants.setForeground(s, new Color(0,0,0));
		StyleConstants.setBold(s, true);

		s = mDocument.addStyle("~System", regular);
		StyleConstants.setForeground(s, new Color(0,0,0));
		StyleConstants.setBackground(s, new Color(255,255,0));
	}


	public PrintTextArea println(Object aText, Object... aParams)
	{
		return println(Style.BLACK, aText, aParams);
	}


	public PrintTextArea println(Style aStyle, Object aText, Object... aParams)
	{
		try
		{
			javax.swing.text.Style style;

			if (aText instanceof Throwable)
			{
//				aText = StackTraceFormatter.toString((Throwable)aText);
				style = mDocument.getStyle("~System");
			}
			else
			{
				if (aParams.length > 0)
				{
					aText = String.format(aText.toString(), aParams);
				}

				style = mDocument.getStyle(aStyle == null ? Style.BLACK.name() : aStyle.name());
			}

			String text = Calendar.now() + "\t" + aText + "\n";

			synchronized (PrintTextArea.class)
			{
				JScrollBar scrollBar = super.getVerticalScrollBar();
				boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 100 >= scrollBar.getMaximum();
				int len = mDocument.getLength();
				if (len > 100000)
				{
					mDocument.remove(0, len - 100000);
					len = mDocument.getLength();
				}
				mDocument.insertString(len, text, style);
				if (scroll)
				{
					mServiesDisplayOutput.setCaretPosition(len + 1);
				}
			}
		}
		catch (Error | Exception e)
		{
			e.printStackTrace(System.out);
		}

		return this;
	}


	public PrintStream newPrintStream()
	{
		PrintStream ps = new PrintStream(new OutputStream()
		{
			ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();

			@Override
			public void write(int aB) throws IOException
			{
				if (aB == '\n' || aB == '\r')
				{
					if (mBuffer.size() > 0)
					{
						PrintTextArea.this.println(mBuffer.toString());
						mBuffer.reset();
					}
				}
				else
				{
					mBuffer.write(aB);
				}
			}


			@Override
			public void close() throws IOException
			{
				if (mBuffer.size() > 0)
				{
					PrintTextArea.this.println(mBuffer.toString());
					mBuffer.reset();
				}
			}
		});

		return ps;
	}


	public void reset()
	{
		mServiesDisplayOutput.setText("");
	}


	public static void main(String ... args)
	{
		try
		{
			PrintTextArea cow = new PrintTextArea();

			JFrame frame = new JFrame();
			frame.add(cow);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);

			try (PrintStream ps = cow.newPrintStream())
			{
				for (int i = 0; i < 10; i++)
				{
					ps.println(i);
					cow.println("line %d", i);
					cow.println(Style.RED, "line " + i);
					cow.println(Style.GREEN, "line %d", i);
					Thread.sleep(50);
				}
			}
		}
		catch (Error | Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}

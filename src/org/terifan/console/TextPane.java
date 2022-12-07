package org.terifan.console;

import java.awt.Color;
import java.awt.Cursor;
import java.text.SimpleDateFormat;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.terifan.util.StackTraceFormatter;


class TextPane
{
	private final static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private StyledDocument mDocument;
	private JScrollPane mScrollPane;
	private JTextPane mTextArea;
	private Style mDateTimeStyle;
	private Style mDefaultFont;


	TextPane()
	{
		mDefaultFont = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(mDefaultFont, "consolas");

		mTextArea = new JTextPane();
		mTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		mDocument = mTextArea.getStyledDocument();

		mScrollPane = new JScrollPane();
		mScrollPane.setViewportView(mTextArea);
		mScrollPane.setBorder(null);

		mDateTimeStyle = textStyleToStyle(new TextStyle("mDateTimeStyle", Color.GRAY, Color.WHITE, false));

		setEditable(false);
	}


	public JScrollPane getScrollPane()
	{
		return mScrollPane;
	}


	public JTextPane getTextArea()
	{
		return mTextArea;
	}


	public boolean isEditable()
	{
		return mTextArea.isEditable();
	}


	public TextPane setEditable(boolean aEditable)
	{
		mTextArea.setEditable(aEditable);
		return this;
	}


	synchronized void append(SimpleConsoleWindow aWindow, TextStyle aTextStyle, Object aText, Object[] aParameters)
	{
		try
		{
			if (aText instanceof Throwable)
			{
				aText = StackTraceFormatter.toString((Throwable)aText).trim();
			}
			if (aParameters != null)
			{
				for (int i = 0; i < aParameters.length; i++)
				{
					if (aParameters[i] instanceof Throwable)
					{
						aParameters[i] = StackTraceFormatter.toString((Throwable)aParameters[i]).trim();
					}
				}
			}

			JScrollBar scrollBar = mScrollPane.getVerticalScrollBar();
			boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 20 >= scrollBar.getMaximum();

			insertString(aWindow, DATETIME_FORMAT.format(System.currentTimeMillis()), mDateTimeStyle);
			insertString(aWindow, "\t" + String.format(aText == null ? "null" : aText.toString(), aParameters) + "\n", textStyleToStyle(aTextStyle));

			if (scroll)
			{
				mTextArea.setCaretPosition(mDocument.getLength());
			}
		}
		catch (Error | Exception e)
		{
			e.printStackTrace(System.out);
		}
	}


	private void insertString(SimpleConsoleWindow aWindow, String aString, AttributeSet aStyle) throws BadLocationException
	{
		int len = mDocument.getLength();

		if (len > aWindow.getTextLimit())
		{
			mDocument.remove(0, len - aWindow.getTextLimit());
			len = mDocument.getLength();
		}

		mDocument.insertString(len, aString, aStyle);
	}


	private Style textStyleToStyle(TextStyle aTextStyle)
	{
		Style style = mDocument.getStyle(aTextStyle.getName());
		if (style == null)
		{
			aTextStyle.apply(mDefaultFont, mDocument);
			style = mDocument.getStyle(aTextStyle.getName());
		}
		return style;
	}
}

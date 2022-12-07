package org.terifan.console;

import java.awt.Cursor;
import java.text.SimpleDateFormat;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import org.terifan.util.log.Log;


public class StyledTextPane
{
	private final static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private StyledDocument mDocument;
	private JScrollPane mScrollPane;
	private JTextPane mTextArea;


	StyledTextPane()
	{
		mTextArea = new JTextPane();
		mTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		mDocument = mTextArea.getStyledDocument();

		mScrollPane = new JScrollPane();
		mScrollPane.setViewportView(mTextArea);
		mScrollPane.setBorder(null);

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


	public StyledTextPane setEditable(boolean aEditable)
	{
		mTextArea.setEditable(aEditable);
		return this;
	}


	synchronized void append(ConsoleOutputWindow aWindow, TextStyle aTextStyle, Object aText)
	{
		try
		{
			if (aText instanceof Throwable)
			{
				aText = Log.getStackTraceString((Throwable)aText);
			}

			String text = DATETIME_FORMAT.format(System.currentTimeMillis()) + "\t" + aText + "\n";

			synchronized (aWindow)
			{
				JScrollBar scrollBar = mScrollPane.getVerticalScrollBar();

				boolean scroll = scrollBar.getValue() + scrollBar.getVisibleAmount() + 20 >= scrollBar.getMaximum();

				int len = mDocument.getLength();

				if (len > aWindow.getTextLimit())
				{
					mDocument.remove(0, len - aWindow.getTextLimit());
					len = mDocument.getLength();
				}

				Style style = mDocument.getStyle(aTextStyle.getName());
				if (style == null)
				{
					aTextStyle.apply(aWindow, mDocument);
					style = mDocument.getStyle(aTextStyle.getName());
				}

				mDocument.insertString(len, text, style);

				if (scroll)
				{
					mTextArea.setCaretPosition(mDocument.getLength());
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.out);
		}
	}
}

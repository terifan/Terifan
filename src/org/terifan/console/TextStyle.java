package org.terifan.console;

import java.awt.Color;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class TextStyle
{
	private String mName;
	private Color mBackground;
	private Color mForeground;
	private boolean mBold;


	public TextStyle(String aName, Color aForeground, Color aBackground, boolean aBold)
	{
		mName = aName;
		mBackground = aBackground;
		mForeground = aForeground;
		mBold = aBold;
	}


	public String getName()
	{
		return mName;
	}


	public Color getBackground()
	{
		return mBackground;
	}


	public Color getForeground()
	{
		return mForeground;
	}


	public boolean isBold()
	{
		return mBold;
	}


	void apply(SimpleConsoleWindow aWindow, StyledDocument aDocument)
	{
		Style regular = aDocument.addStyle("regular", aWindow.getDefaultFont());

		Style s = aDocument.addStyle(mName, regular);
		StyleConstants.setForeground(s, mForeground);
		StyleConstants.setBackground(s, mBackground);
		StyleConstants.setBold(s, mBold);
	}
}

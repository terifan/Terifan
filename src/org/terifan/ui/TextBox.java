package org.terifan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.border.Border;
import static org.terifan.ui.Anchor.CENTER;
import static org.terifan.ui.Anchor.EAST;
import static org.terifan.ui.Anchor.NORTH;
import static org.terifan.ui.Anchor.NORTH_EAST;
import static org.terifan.ui.Anchor.SOUTH;
import static org.terifan.ui.Anchor.SOUTH_EAST;
import org.terifan.util.log.Log;


public class TextBox implements Cloneable
{
	private final static Insets ZERO_INSETS = new Insets(0,0,0,0);
	private static FontRenderContext DEFAULT_RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, false);
	private static char[] DEFAULT_BREAK_CHARS = {' ', '.', ',', '-', '_'};

	private String mText;
	private Font mFont;
	private Color mForeground;
	private Color mBackground;
	private Color mHighlight;
	private Insets mMargins;
	private Insets mPadding;
	private Border mBorder;
	private Border mTextBorder;
	private Rectangle mBounds;
	private Anchor mAnchor;
	private int mLineSpacing;
	private int mMaxLineCount;
	private char[] mBreakChars;
	private FontRenderContext mFontRenderContext;

	private boolean mDirty;
	private ArrayList<String> mTextLines;
	private ArrayList<Rectangle> mTextBounds;


	public TextBox()
	{
		this("");
	}


	public TextBox(String aText)
	{
		mText = aText;
		mBounds = new Rectangle();
		mMargins = new Insets(0, 0, 0, 0);
		mPadding = new Insets(0, 0, 0, 0);
		mForeground = Color.BLACK;
		mAnchor = Anchor.NORTH_WEST;
		mFont = UIManager.getDefaults().getFont("TextField.font");

		mBreakChars = DEFAULT_BREAK_CHARS;
		mFontRenderContext = DEFAULT_RENDER_CONTEXT;

		mDirty = true;
	}


	public String getText()
	{
		return mText;
	}


	public TextBox setText(String aText)
	{
		if (aText == null)
		{
			throw new IllegalArgumentException("aText is null");
		}

		mText = aText;
		mDirty = true;
		return this;
	}


	public Font getFont()
	{
		return mFont;
	}


	public TextBox setFont(Font aFont)
	{
		if (aFont == null)
		{
			throw new IllegalArgumentException("aFont is null");
		}

		mFont = aFont;
		mDirty = true;
		return this;
	}


	public Color getForeground()
	{
		return mForeground;
	}


	public TextBox setForeground(Color aForeground)
	{
		if (aForeground == null)
		{
			throw new IllegalArgumentException("aForeground is null");
		}

		mForeground = aForeground;
		return this;
	}


	public Color getBackground()
	{
		return mBackground;
	}


	public TextBox setBackground(Color aBackground)
	{
		mBackground = aBackground;
		return this;
	}


	public Color getHighlight()
	{
		return mHighlight;
	}


	public TextBox setHighlight(Color aHighlight)
	{
		mHighlight = aHighlight;
		return this;
	}


	public Insets getMargins()
	{
		return mMargins;
	}


	public TextBox setMargins(Insets aMargins)
	{
		if (aMargins == null)
		{
			throw new IllegalArgumentException("aMargins is null");
		}

		return setMargins(aMargins.top, aMargins.left, aMargins.bottom, aMargins.right);
	}


	public TextBox setMargins(int aTop, int aLeft, int Bottom, int aRight)
	{
		mMargins.set(aTop, aLeft, Bottom, aRight);
		mDirty = true;
		return this;
	}


	public Insets getPadding()
	{
		return mPadding;
	}


	public TextBox setPadding(Insets aPadding)
	{
		if (aPadding == null)
		{
			throw new IllegalArgumentException("aPadding is null");
		}

		return setPadding(aPadding.top, aPadding.left, aPadding.bottom, aPadding.right);
	}


	public TextBox setPadding(int aTop, int aLeft, int Bottom, int aRight)
	{
		mPadding.set(aTop, aLeft, Bottom, aRight);
		mDirty = true;
		return this;
	}


	public Border getBorder()
	{
		return mBorder;
	}


	public TextBox setBorder(Border aBorder)
	{
		mBorder = aBorder;
		mDirty = true;
		return this;
	}


	public Border getTextBorder()
	{
		return mTextBorder;
	}


	public TextBox setTextBorder(Border aBorder)
	{
		mTextBorder = aBorder;
		mDirty = true;
		return this;
	}


	public Rectangle getBounds()
	{
		return mBounds;
	}


	public TextBox setBounds(Rectangle aBounds)
	{
		if (aBounds == null)
		{
			throw new IllegalArgumentException("aBounds is null");
		}

		mBounds = new Rectangle(aBounds);
		mDirty = true;
		return this;
	}


	public TextBox setBounds(int aOffsetX, int aOffsetY, int aWidth, int aHeight)
	{
		mBounds.setBounds(aOffsetX, aOffsetY, aWidth, aHeight);
		mDirty = true;
		return this;
	}


	public TextBox setDimensions(Dimension aDimension)
	{
		if (aDimension == null)
		{
			throw new IllegalArgumentException("aDimension is null");
		}

		mBounds.setSize(aDimension);
		mDirty = true;
		return this;
	}


	public TextBox setDimensions(int aWidth, int aHeight)
	{
		mBounds.setSize(aWidth, aHeight);
		mDirty = true;
		return this;
	}


	public Anchor getAnchor()
	{
		return mAnchor;
	}


	public TextBox setAnchor(Anchor aAnchor)
	{
		if (aAnchor == null)
		{
			throw new IllegalArgumentException("aAnchor is null");
		}

		mAnchor = aAnchor;
		mDirty = true;
		return this;
	}


	public int getLineSpacing()
	{
		return mLineSpacing;
	}


	public TextBox setLineSpacing(int aLineSpacing)
	{
		mLineSpacing = aLineSpacing;
		mDirty = true;
		return this;
	}


	public int getMaxLineCount()
	{
		return mMaxLineCount;
	}


	public TextBox setMaxLineCount(int aLineCount)
	{
		mMaxLineCount = aLineCount;
		mDirty = true;
		return this;
	}


	public void translate(int aDeltaX, int aDeltaY)
	{
		mBounds.translate(aDeltaX, aDeltaY);
		mDirty = true;
	}


	public int getWidth()
	{
		return mBounds.width;
	}


	public TextBox setWidth(int aWidth)
	{
		mBounds.width = aWidth;
		return this;
	}


	public int getHeight()
	{
		return mBounds.height;
	}


	public TextBox setHeight(int aHeight)
	{
		mBounds.height = aHeight;
		return this;
	}


	public int getX()
	{
		return mBounds.x;
	}


	public TextBox setX(int aOffsetX)
	{
		mBounds.x = aOffsetX;
		return this;
	}


	public int getY()
	{
		return mBounds.y;
	}


	public TextBox setY(int aOffsetY)
	{
		mBounds.y = aOffsetY;
		return this;
	}


	public TextBox pack()
	{
		setBounds(measure());
		mDirty = false;
		return this;
	}


	public TextBox setBreakChars(char[] aBreakChars)
	{
		if (aBreakChars == null)
		{
			aBreakChars = new char[0];
		}
		mBreakChars = aBreakChars.clone();
		return this;
	}


	public char[] getBreakChars()
	{
		return mBreakChars.clone();
	}


	public Rectangle measure()
	{
		if (mBounds.isEmpty())
		{
			mBounds.setBounds(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
		}

		if (mDirty)
		{
			layout();
		}

		if (mTextBounds.isEmpty())
		{
			return new Rectangle();
		}

		Rectangle bounds = new Rectangle(mTextBounds.get(0));

		for (int i = 1, sz = mTextBounds.size(); i < sz; i++)
		{
			bounds.add(mTextBounds.get(i));
		}

		if (mBorder != null)
		{
			Insets bi = mBorder.getBorderInsets(null);
			bounds.x -= bi.left;
			bounds.y -= bi.top;
			bounds.width += bi.left + bi.right;
			bounds.height += bi.top + bi.bottom;
		}

		if (mTextBorder != null)
		{
			Insets bi = mTextBorder.getBorderInsets(null);
			bounds.x -= bi.left;
			bounds.y -= bi.top;
			bounds.width += bi.left + bi.right;
			bounds.height += bi.top + bi.bottom;
		}

		bounds.x -= mMargins.left;
		bounds.y -= mMargins.top;
		bounds.width += mMargins.left + mMargins.right;
		bounds.height += mMargins.top + mMargins.bottom;

		return bounds;
	}


	@Override
	public TextBox clone()
	{
		try
		{
			TextBox textBox = (TextBox)super.clone();

			textBox.mAnchor = this.mAnchor;
			textBox.mBackground = this.mBackground;
			textBox.mBorder = this.mBorder;
			textBox.mBounds = (Rectangle)this.mBounds.clone();
			textBox.mBreakChars = this.mBreakChars == DEFAULT_BREAK_CHARS ? DEFAULT_BREAK_CHARS : this.mBreakChars.clone();
			textBox.mDirty = this.mDirty;
			textBox.mFont = this.mFont;
			textBox.mFontRenderContext = this.mFontRenderContext;
			textBox.mForeground = this.mForeground;
			textBox.mHighlight = this.mHighlight;
			textBox.mMaxLineCount = this.mMaxLineCount;
			textBox.mLineSpacing = this.mLineSpacing;
			textBox.mMargins = (Insets)mMargins.clone();
			textBox.mPadding = (Insets)mPadding.clone();
			textBox.mText = this.mText;
			textBox.mTextBorder = this.mTextBorder;
			textBox.mTextLines = this.mTextLines == null ? null : new ArrayList<>(this.mTextLines);

			return textBox;
		}
		catch (CloneNotSupportedException e)
		{
			throw new Error();
		}
	}


	public TextBox render(Graphics aGraphics)
	{
		return render(aGraphics, 0, 0);
	}


	public TextBox render(Graphics aGraphics, int aTranslateX, int aTranslateY)
	{
		if (mDirty)
		{
			layout();
		}

		aGraphics.translate(aTranslateX, aTranslateY);

		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		}

		int boxX = mBounds.x;
		int boxY = mBounds.y;
		int boxW = mBounds.width;
		int boxH = mBounds.height;

		if (mBorder != null)
		{
			mBorder.paintBorder(null, aGraphics, boxX, boxY, boxW, boxH);

			Insets bi = mBorder.getBorderInsets(null);
			boxX += bi.left;
			boxY += bi.top;
			boxW -= bi.left + bi.right;
			boxH -= bi.left + bi.bottom;
		}

		if (mBackground != null)
		{
			aGraphics.setColor(mBackground);
			aGraphics.fillRect(boxX, boxY, boxW, boxH);
		}

		Insets ti = mTextBorder != null ? mTextBorder.getBorderInsets(null) : ZERO_INSETS;
		LineMetrics lm = mFont.getLineMetrics("Adgj", mFontRenderContext);

		aGraphics.setColor(mForeground);
		aGraphics.setFont(mFont);
		
		for (int i = 0, sz = mTextBounds.size(); i < sz; i++)
		{
			Rectangle r = mTextBounds.get(i);

			if (mTextBorder != null)
			{
				mTextBorder.paintBorder(null, aGraphics, r.x - ti.left, r.y - ti.top, r.width + ti.left + ti.right, r.height + ti.top + ti.bottom);
			}

			drawSingleLine(aGraphics, mTextLines.get(i), lm, r.x, r.y, r.width, r.height);
		}

		aGraphics.translate(-aTranslateX, -aTranslateY);

		return this;
	}


	private synchronized void layout()
	{
		if (mText == null)
		{
			throw new IllegalStateException("Text is null");
		}

		layoutLines();
		layoutBounds();

		mDirty = false;
	}


	private void layoutBounds()
	{
		ArrayList<Rectangle> list = new ArrayList<>();

		int boxX = mBounds.x;
		int boxY = mBounds.y;
		int boxW = mBounds.width;
		int boxH = mBounds.height;

		if (mBorder != null)
		{
			Insets bi = mBorder.getBorderInsets(null);
			boxX += bi.left;
			boxY += bi.top;
			boxW -= bi.left + bi.right;
			boxH -= bi.left + bi.bottom;
		}

		boxX += mMargins.left;
		boxY += mMargins.top;
		boxW -= mMargins.left + mMargins.right;
		boxH -= mMargins.top + mMargins.bottom;

		int extraLineHeight = 0;
		if (mTextBorder != null)
		{
			Insets bi = mTextBorder.getBorderInsets(null);
			boxX += bi.left;
			boxY += bi.top;
			boxW -= bi.left + bi.right;
			boxH -= bi.left + bi.bottom;
			extraLineHeight = bi.top + bi.bottom;
		}

		LineMetrics lm = mFont.getLineMetrics("Adgj", mFontRenderContext);
		int lineHeight = (int)lm.getHeight() + mPadding.top + mPadding.bottom;

		if (boxH < lineHeight)
		{
			boxH = lineHeight;
		}

		int lineHeightExtra = lineHeight + mLineSpacing + extraLineHeight;
		int boxHeightExtra = boxH + mLineSpacing + extraLineHeight;
		
		int lineY = boxY;
		int lineCount = Math.min(Math.min(mTextLines.size(), mMaxLineCount > 0 ? mMaxLineCount : Integer.MAX_VALUE), boxHeightExtra / lineHeightExtra);
		
		switch (mAnchor)
		{
			case SOUTH_EAST:
			case SOUTH:
			case SOUTH_WEST:
				lineY += Math.max(0, boxHeightExtra - lineCount * lineHeightExtra);
				break;
			case CENTER:
			case WEST:
			case EAST:
				lineY += Math.max(0, (boxHeightExtra - lineCount * lineHeightExtra) / 2);
				break;
		}

		for (int i = 0; i < lineCount; i++)
		{
			String str = mTextLines.get(i);

			int lineX = boxX;
			int lineW = getStringLength(str, mFont) + mPadding.left + mPadding.right;

			switch (mAnchor)
			{
				case NORTH:
				case CENTER:
				case SOUTH:
					lineX += (boxW - lineW) / 2;
					break;
				case NORTH_EAST:
				case EAST:
				case SOUTH_EAST:
					lineX += boxW - lineW;
					break;
			}

			list.add(new Rectangle(lineX, lineY, lineW, lineHeight));

			lineY += lineHeightExtra;
		}

		mTextBounds = list;
	}


	private void layoutLines()
	{
		ArrayList<String> list = new ArrayList<>();

		int boxW = mBounds.width - mMargins.left - mMargins.right;

		if (mBorder != null)
		{
			Insets bi = mBorder.getBorderInsets(null);
			boxW -= bi.left + bi.right;
		}
		if (mTextBorder != null)
		{
			Insets bi = mTextBorder.getBorderInsets(null);
			boxW -= bi.left + bi.right;
		}
		boxW -= mPadding.left + mPadding.right;

		if (boxW > 0)
		{
			for (String str : mText.split("\n"))
			{
				do
				{
					int w = getStringLength(str, mFont);
					String tmp;

					if (w > boxW)
					{
						int offset = Math.max(findStringLimit(str, boxW), 1);
						int temp = offset;

						outer: for (; temp > 1; temp--)
						{
							char c = str.charAt(temp - 1);
							for (char d : mBreakChars)
							{
								if (c == d)
								{
									break outer;
								}
							}
						}

						if (temp > 1)
						{
							offset = temp;
						}

						tmp = str.substring(0, offset);
						str = str.substring(offset).trim();
					}
					else
					{
						tmp = str;
						str = "";
					}

					list.add(tmp.trim());

					if (mMaxLineCount > 0 && list.size() >= mMaxLineCount)
					{
						break;
					}
				}
				while (str.length() > 0);

				if (mMaxLineCount > 0 && list.size() >= mMaxLineCount)
				{
					break;
				}
			}
		}

		mTextLines = list;
	}


	private int findStringLimit(String aString, int aWidth)
	{
		int min = 0;
		int max = aString.length();

		while (Math.abs(min - max) > 1)
		{
			int mid = (max + min) / 2;

			int w = getStringLength(aString.substring(0, mid), mFont);

			if (w > aWidth)
			{
				max = mid;
			}
			else
			{
				min = mid;
			}
		}

		return min;
	}


	private int getStringLength(String aString, Font aFont)
	{
		return (int)Math.ceil(aFont.getStringBounds(aString, mFontRenderContext).getWidth());
	}


	private void drawSingleLine(Graphics aGraphics, String aText, LineMetrics aLineMetrics, int aOffsetX, int aOffsetY, int aWidth, int aHeight)
	{
		if (mHighlight != null)
		{
			aGraphics.setColor(mHighlight);
			aGraphics.fillRect(aOffsetX, aOffsetY, aWidth, aHeight);
			aGraphics.setColor(mForeground);
		}

		int adjust = (int)(aLineMetrics.getHeight() - aLineMetrics.getDescent());

		aGraphics.drawString(aText, aOffsetX + mPadding.left, aOffsetY + adjust + mPadding.top);
	}


	static int getBaseLine(Font aFont, int aHeight)
	{
		LineMetrics lm = aFont.getLineMetrics("Adgj", DEFAULT_RENDER_CONTEXT);
		return (int)(lm.getHeight() - lm.getDescent());
	}
}
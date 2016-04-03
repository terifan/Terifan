package org.terifan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import org.terifan.util.log.Log;


/**
 * A TextPanel renders a TextBox inside a JComponent
 */
public class TextPanel extends JComponent
{
	private TextBox mTextBox;
	private Anchor mAnchor;


	public TextPanel(TextBox aTextBox)
	{
		this(aTextBox, Color.WHITE);
	}


	public TextPanel(TextBox aTextBox, Color aBackgroundColor)
	{
		mTextBox = aTextBox;
		
		super.setBackground(aBackgroundColor);
		super.setOpaque(aBackgroundColor != null);
	}


	public TextBox getTextBox()
	{
		return mTextBox;
	}


	public void setTextBox(TextBox aTextBox)
	{
		mTextBox = aTextBox;
	}


	public Anchor getAnchor()
	{
		return mAnchor;
	}
	

	public TextPanel setAnchor(Anchor aAnchor)
	{
		mAnchor = aAnchor;
		return this;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		if (isOpaque())
		{
			aGraphics.setColor(getBackground());
			aGraphics.fillRect(0, 0, getWidth(), getHeight());
		}
		
		if (mAnchor == null)
		{
			mTextBox.render(aGraphics);
		}
		else
		{
			mAnchor.translate(mTextBox.getBounds(), new Rectangle(0, 0, getWidth(), getHeight()));
			mTextBox.render(aGraphics);
		}
	}


	@Override
	public Dimension getPreferredSize()
	{
		Dimension size = mTextBox.getBounds().getSize();
		if (mTextBox.getMinWidth() > 0)
		{
			size.width = Math.max(size.width, mTextBox.getMinWidth());
		}
		if (mTextBox.getMaxWidth() > 0)
		{
			size.width = Math.min(size.width, mTextBox.getMaxWidth());
		}
		return size;
	}


	@Override
	public Dimension getMinimumSize()
	{
		Dimension size = super.getMinimumSize();
		if (mTextBox.getMinWidth() > 0)
		{
			size.width = Math.max(size.width, mTextBox.getMinWidth());
		}
		return size;
	}


	@Override
	public Dimension getMaximumSize()
	{
		Dimension size = super.getMaximumSize();
		if (mTextBox.getMaxWidth() > 0)
		{
			size.width = Math.min(size.width, mTextBox.getMaxWidth());
		}
		return size;
	}
}
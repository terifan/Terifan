package org.terifan.ui.propertygrid;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextField;


public class ColorChooser extends JTextField
{
	protected String mOldValue;

	public ColorChooser(String aColor)
	{
		setText(aColor);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		if (isFocusOwner())
		{
			super.paintComponent(aGraphics);
			return;
		}

		Color color = getColor();
		mOldValue = super.getText();

		aGraphics.setColor(getBackground());
		aGraphics.fillRect(0,0,getWidth(),getHeight());
		aGraphics.setColor(color);
		aGraphics.fillRect(0, 1, 12, 12);
		aGraphics.setColor(Color.BLACK);
		aGraphics.drawRect(0, 1, 12, 12);
		aGraphics.drawString("["+mOldValue+"]", 18, 11);
	}


	@Override
	public void setText(String aText)
	{
		if (decodeColor(aText) == null && mOldValue == null)
		{
			aText = "128,128,128";
		}

		super.setText(aText);
		mOldValue = aText;
	}


	public void setColor(Color aColor)
	{
		if (aColor.getAlpha() != 255)
		{
			setText(aColor.getRed()+","+aColor.getGreen()+","+aColor.getBlue()+","+aColor.getAlpha());
		}
		else
		{
			setText(aColor.getRed()+","+aColor.getGreen()+","+aColor.getBlue());
		}
	}


	public Color getColor()
	{
		Color color = decodeColor(getText());
		if (color == null)
		{
			color = decodeColor(mOldValue);
		}
		return color;
	}


	private Color decodeColor(String aColor)
	{
		String [] compStr = aColor.split(",");
		if (compStr.length != 3 && compStr.length != 4)
		{
			return null;
		}

		int [] comp = new int[3];
		for (int i = 0; i < 3; i++)
		{
			try
			{
				comp[i] = Integer.parseInt(compStr[i]);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}

		return new Color(comp[0], comp[1], comp[2]);
	}
}
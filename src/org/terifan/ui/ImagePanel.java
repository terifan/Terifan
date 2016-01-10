package org.terifan.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class ImagePanel extends JPanel
{
	private BufferedImage mImage;


	public ImagePanel(BufferedImage aImage)
	{
		mImage = aImage;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if (isOpaque())
		{
			aGraphics.setColor(getBackground());
			aGraphics.fillRect(0, 0, getWidth(), getHeight());
		}

		aGraphics.drawImage(mImage, 0, 0, getWidth(), getHeight(), null);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mImage.getWidth(), mImage.getHeight());
	}
}
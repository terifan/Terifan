package org.terifan.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class ImagePanel extends JPanel
{
	private BufferedImage mImage;


	public ImagePanel(BufferedImage aImage)
	{
		mImage = aImage;

		setOpaque(true);
	}


	public BufferedImage getImage()
	{
		return mImage;
	}


	public void setImage(BufferedImage aImage)
	{
		mImage = aImage;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
//		((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//
//		aGraphics.setColor(getBackground());
//		aGraphics.fillRect(0, 0, getWidth(), getHeight());
//
//		aGraphics.drawImage(mImage, 0, 0, getWidth(), getHeight(), null);

		aGraphics.drawImage(mImage, 0, 0, null);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mImage.getWidth(), mImage.getHeight());
	}
}
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
		this.mImage = aImage;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		aGraphics.setColor(java.awt.Color.BLACK);
		aGraphics.fillRect(0, 0, getWidth(), getHeight());
		aGraphics.drawImage(mImage, (getWidth()-mImage.getWidth())/2, (getHeight()-mImage.getHeight())/2, null);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mImage.getWidth(), mImage.getHeight());
	}
}
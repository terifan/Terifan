package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class BlackWindowPanel extends JPanel
{
	private final static long serialVersionUID = 1L;
	private BufferedImage mImage;


	public BlackWindowPanel() throws IOException
	{
		mImage = ImageIO.read(getClass().getResource("panel_border.png"));

		setBackground(new Color(51,51,51));
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setOpaque(true);
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int x1 = 0;
		int y1 = 0;
		int x2 = getWidth() - 8;
		int y2 = getHeight() - 8;

		aGraphics.drawImage(mImage, x1, y1, x1 + 8, y1 + 8, 0, 0, 8, 8, null);
		aGraphics.drawImage(mImage, x2, y1, x2 + 8, y1 + 8, 16, 0, 24, 8, null);
		aGraphics.drawImage(mImage, x1, y2, x1 + 8, y2 + 8, 0, 16, 8, 24, null);
		aGraphics.drawImage(mImage, x2, y2, x2 + 8, y2 + 8, 16, 16, 24, 24, null);
		aGraphics.drawImage(mImage, x1 + 8, y1, x2, y1 + 8, 8, 0, 16, 8, null);
		aGraphics.drawImage(mImage, x1 + 8, y2, x2, y2 + 8, 8, 16, 16, 24, null);
		aGraphics.drawImage(mImage, x1, y1 + 8, x1 + 8, y2, 0, 8, 8, 16, null);
		aGraphics.drawImage(mImage, x2, y1 + 8, x2 + 8, y2, 16, 8, 24, 16, null);

		aGraphics.setColor(getBackground());
		aGraphics.fillRect(x1+8, y1+8, x2-x1-8, y2-y1-8);
	}
}

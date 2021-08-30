package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JComponent;


public class TileLayoutItem extends JComponent
{
	private final static long serialVersionUID = 1L;

	private String mLabel;
	private BufferedImage mThumbnail;


	public TileLayoutItem(String aLabel, BufferedImage aThumbnail)
	{
		mLabel = aLabel;
		mThumbnail = aThumbnail;
	}


	@Override
	public Dimension getPreferredSize()
	{
//		return new Dimension(mThumbnail == null ? 100 : mThumbnail.getWidth(), getTileLayout().getRowHeight());
		if (mThumbnail == null)
		{
			return new Dimension(100, 50);
		}
		return new Dimension(mThumbnail.getWidth(), mThumbnail.getHeight());
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int w = getWidth();
		int h = getHeight();

		TileLayout layout = getTileLayout();
		Point pad = layout.getPadding();

		Graphics2D g = (Graphics2D)aGraphics;
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		if (mThumbnail != null)
		{
			paintThumbnail(g, pad.x, pad.y, w - 2 * pad.x, h - 2 * pad.y);
		}

//		if (mPreferredWidthWeight < 0)
//		{
//			Graphics gt = g.create(5, 5, w-10, h-10);
//			Utilities.enableTextAntialiasing(gt);
//			gt.setFont(new Font("arial", Font.PLAIN, 48));
//			gt.setColor(Color.WHITE);
//			gt.drawString(mLabel, 0, h/2);
//			gt.dispose();
//		}
	}


	private void paintThumbnail(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight)
	{
		int x = aX;
		int y = aY;
		int w = aWidth;
		int h = aHeight;

		int iw = mThumbnail.getWidth();
		int ih = mThumbnail.getHeight();

		int ow = iw * h / ih;
		int crop = (ow - w) / 2;

		aGraphics.drawImage(mThumbnail, x, y, x + w, y + h, crop, 0, crop + w, ih, null);
	}


	public TileLayout getTileLayout()
	{
		return (TileLayout)getParent().getLayout();
	}
}

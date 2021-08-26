package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JComponent;


public class TileLayoutItem extends JComponent
{
	private final static long serialVersionUID = 1L;

	private String mLabel;
	private BufferedImage mThumbnail;
	private int mPreferredWidth;
	private float mPreferredWidthWeight;


	public TileLayoutItem(String aLabel, int aPreferredWidth, BufferedImage aThumbnail)
	{
		mLabel = aLabel;
		mPreferredWidth = aPreferredWidth;
		mThumbnail = aThumbnail;
	}


	public TileLayoutItem(String aLabel, float aPreferredWidthWeight, BufferedImage aThumbnail)
	{
		mLabel = aLabel;
		mPreferredWidthWeight = aPreferredWidthWeight;
		mThumbnail = aThumbnail;
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(mPreferredWidth, getTileLayout().getRowHeight());
	}


	public float getPreferredWidthWeight()
	{
		return mPreferredWidthWeight;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int w = getWidth();
		int h = getHeight();

		TileLayout layout = getTileLayout();
		int padX = layout.getPaddingX();
		int padY = layout.getPaddingY();

		Graphics2D g = (Graphics2D)aGraphics;
		g.setColor(new Color(new Random(hashCode()).nextInt(0xffffff)));
		g.fillRect(0, 0, w, h);

		if (mThumbnail != null)
		{
			paintThumbnail(g, padX, padY, w - 2 * padX, h - 2 * padY);
		}

		if (mPreferredWidthWeight < 0)
		{
			g.setFont(new Font("arial", Font.PLAIN, 48));
			g.setColor(Color.WHITE);
			g.drawString(mLabel, 5, 65);
		}
		else
		{
			g.setColor(Color.WHITE);
			g.drawString(mLabel, 5, 15);
		}
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

		aGraphics.drawImage(mThumbnail, x, y, x + w, y + h, crop, 0, iw - crop, ih, null);
	}


	public TileLayout getTileLayout()
	{
		return (TileLayout)getParent().getLayout();
	}
}

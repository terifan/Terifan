package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.terifan.ui.Utilities;


public class TileLayoutItem extends JComponent
{
	private final static long serialVersionUID = 1L;

	private String mLabel;
	private BufferedImage mThumbnail;
	private Dimension mPreferredSize;


	public TileLayoutItem(String aLabel, Dimension aPreferredSize)
	{
		mLabel = aLabel;
		mPreferredSize = aPreferredSize;
	}


	public TileLayoutItem(String aLabel, BufferedImage aThumbnail)
	{
		mLabel = aLabel;
		mThumbnail = aThumbnail;
		mPreferredSize = new Dimension(mThumbnail.getWidth(), mThumbnail.getHeight());
	}


	@Override
	public Dimension getPreferredSize()
	{
		return mPreferredSize;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		int w = getWidth();
		int h = getHeight();

		Graphics2D g = (Graphics2D)aGraphics;
		g.setColor(getBackground());
		g.setColor(new Color(new java.util.Random(hashCode()).nextInt(0xffffff)));
		g.fillRect(0, 0, w, h);

		Point pad = new Point(2, 2);

		if (mThumbnail != null)
		{
			paintThumbnail(g, pad.x, pad.y, w - 2 * pad.x, h - 2 * pad.y);
		}

//		{
//			g.setColor(new Color(0, 0, 0, 128));
//			g.fillRect(pad.x, h - pad.y - 30, w - 2 * pad.x, 30);
//
//			Graphics gt = g.create(pad.x + 5, pad.y, w - 10 - 2 * pad.x, h - 2 * pad.y);
//
//			Utilities.enableTextAntialiasing(gt);
//			gt.setFont(getFont());
//			gt.setColor(Color.WHITE);
//			gt.drawString(mLabel, 0, h - 5 - 2 * pad.y - getFontMetrics(getFont()).getDescent());
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
}

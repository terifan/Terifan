package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
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


	public void setLabel(String aLabel)
	{
		mLabel = aLabel;
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
//		g.setColor(new Color(new java.util.Random(hashCode()).nextInt(0xffffff)));
		g.fillRect(0, 0, w, h);

//		for (int i = 0; i < 6; i++)
//		{
//			g.setColor(new Color(0, 0, 0, 20 / (1 + i)));
//			g.fillRoundRect(i, i, w - 2 * i, h - 2 * i, 6, 6);
//		}

//		Point pad = new Point(4, 4);
		Point pad = new Point(0, 0);

		if (mThumbnail != null)
		{
			paintThumbnail(g, pad.x, pad.y, w - 2 * pad.x, h - 2 * pad.y);
		}

		{
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(pad.x, h - pad.y - 30, w - 2 * pad.x, 30);

			Graphics gt = g.create(pad.x + 5, pad.y, w - 10 - 2 * pad.x, h - 2 * pad.y);

			Utilities.enableTextAntialiasing(gt);
			gt.setFont(getFont());
			gt.setColor(Color.WHITE);
			gt.drawString(mLabel, 0, h - 5 - 2 * pad.y - getFontMetrics(getFont()).getDescent());
			gt.dispose();
		}
	}


	private void paintThumbnail(Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight)
	{
		int iw = mThumbnail.getWidth();
		int ih = mThumbnail.getHeight();

		double s = Math.max(aWidth / (double)iw, aHeight / (double)ih);

		int siw = (int)(s * iw);
		int sih = (int)(s * ih);

		int csx = (aWidth - siw) / 2;
		int csy = (aHeight - sih) / 2;

		int dx = Math.max(0, csx);
		int dy = Math.max(0, csy);
		int dw = Math.min(aWidth, siw);
		int dh = Math.min(aHeight, sih);

		int sx = csx < 0 ? -(int)(csx / s) : 0;
		int sy = csy < 0 ? -(int)(csy / s) : 0;
		int sw2 = (int)Math.round(aWidth / s);
		int sh2 = (int)Math.round(aHeight / s);

		aGraphics.drawImage(mThumbnail, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw2, sy + sh2, null);
	}
}

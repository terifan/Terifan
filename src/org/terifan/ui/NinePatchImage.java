package org.terifan.ui;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;


/**
 * Renders a scalable image.
 *
 * http://developer.android.com/guide/topics/graphics/2d-graphics.html
 */
public class NinePatchImage
{
	private final static int COLOR_MASK = 0x00ffffff;

	private BufferedImage mImage;
	private int mSumBlackX;
	private int mSumBlackY;
	private boolean mStartBlackX;
	private boolean mStartBlackY;
	private boolean mEndBlackX;
	private boolean mEndBlackY;
	private int[] mResizeSegmentsX;
	private int[] mResizeSegmentsY;
	private Insets mPadding;


	public NinePatchImage(URL aURL)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(aURL);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Failed to read resource: " + aURL, e);
		}
		if (image == null)
		{
			throw new IllegalStateException("Resource not found: " + aURL);
		}
		init(image);
	}


	public NinePatchImage(BufferedImage aImage)
	{
		init(aImage);
	}


	private void init(BufferedImage aImage)
	{
		mImage = aImage;
		mPadding = new Insets(0, 0, 0, 0);

		for (int x = 1, w = mImage.getWidth() - 1, y = mImage.getHeight() - 1; x < w; x++)
		{
			if ((mImage.getRGB(x, y) & COLOR_MASK) == 0)
			{
				break;
			}
			mPadding.left++;
		}
		for (int x = mImage.getWidth() - 2, y = mImage.getHeight() - 1; x > 0; x--)
		{
			if ((mImage.getRGB(x, y) & COLOR_MASK) == 0)
			{
				break;
			}
			mPadding.right++;
		}
		for (int x = mImage.getWidth() - 1, y = 1, h = mImage.getHeight() - 1; y < h; y++)
		{
			if ((mImage.getRGB(x, y) & COLOR_MASK) == 0)
			{
				break;
			}
			mPadding.top++;
		}
		for (int x = mImage.getWidth() - 1, y = mImage.getHeight() - 2; y > 0; y--)
		{
			if ((mImage.getRGB(x, y) & COLOR_MASK) == 0)
			{
				break;
			}
			mPadding.bottom++;
		}

		mResizeSegmentsX = new int[100];
		mResizeSegmentsY = new int[100];

		int numSegmentsX = 0;
		int numSegmentsY = 0;

		for (int y = 1, py = 0, pc = -1, h = mImage.getHeight() - 1; y <= h; y++)
		{
			int c = y == h ? -1 : (mImage.getRGB(0, y) & COLOR_MASK) == 0 ? 0 : 1; // 0=black, 1=white, -1=other
			if (y == 1)
			{
				mStartBlackY = c == 0;
			}
			if (y == h)
			{
				mEndBlackY = c == 0;
			}
			if (c != pc)
			{
				if (pc == 0)
				{
					mSumBlackY += y - py;
				}
				if (y > 1)
				{
					mResizeSegmentsY[numSegmentsY++] = y - py;
				}
				pc = c;
				py = y;
			}
		}

		for (int x = 1, px = 0, pc = -1, w = mImage.getWidth() - 1; x <= w; x++)
		{
			int c = x == w ? -1 : (mImage.getRGB(x, 0) & COLOR_MASK) == 0 ? 0 : 1; // 0=black, 1=white, -1=other
			if (x == 1)
			{
				mStartBlackX = c == 0;
			}
			if (x == w)
			{
				mEndBlackX = c == 0;
			}
			if (c != pc)
			{
				if (pc == 0)
				{
					mSumBlackX += x - px;
				}
				if (x > 1)
				{
					mResizeSegmentsX[numSegmentsX++] = x - px;
				}
				pc = c;
				px = x;
			}
		}

		mResizeSegmentsX = Arrays.copyOfRange(mResizeSegmentsX, 0, numSegmentsX);
		mResizeSegmentsY = Arrays.copyOfRange(mResizeSegmentsY, 0, numSegmentsY);
	}


	public BufferedImage scaleImage(int aWidth, int aHeight)
	{
		BufferedImage image = new BufferedImage(aWidth, aHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		paintImage(g, 0, 0, aWidth, aHeight);
		g.dispose();
		return image;
	}


	public void paintSurrounding(Graphics aGraphics, int aOffsetX, int aOffsetY, int aWidth, int aHeight)
	{
		paintImage(aGraphics, aOffsetX - mPadding.left, aOffsetY - mPadding.top, aWidth + mPadding.left + mPadding.right, aHeight + mPadding.top + mPadding.bottom);
	}


	public void paintImage(Graphics aGraphics, int aOffsetX, int aOffsetY, int aWidth, int aHeight)
	{
		int extraX = aWidth - (mImage.getWidth() - 2) + mSumBlackX;
		int extraY = aHeight - (mImage.getHeight() - 2) + mSumBlackY;

		for (int ix = 0, dx = 0, sx = 1; ix < mResizeSegmentsX.length; ix++)
		{
			int sw = mResizeSegmentsX[ix];
			int dw = sw;

			if (mSumBlackX == 0 || mEndBlackX && ix == mResizeSegmentsX.length - 1)
			{
				dw = aWidth - dx;
			}
			else if (!mEndBlackX && ix == mResizeSegmentsX.length - 2)
			{
				dw = aWidth - dx - mResizeSegmentsX[mResizeSegmentsX.length - 1];
			}
			else if (mStartBlackX ^ (ix & 1) == 1)
			{
				dw = dw * extraX / mSumBlackX;
			}

			for (int iy = 0, dy = 0, sy = 1; iy < mResizeSegmentsY.length; iy++)
			{
				int sh = mResizeSegmentsY[iy];
				int dh = sh;

				if (mSumBlackY == 0 || mEndBlackY && iy == mResizeSegmentsY.length - 1)
				{
					dh = aHeight - dy;
				}
				else if (!mEndBlackY && iy == mResizeSegmentsY.length - 2)
				{
					dh = aHeight - dy - mResizeSegmentsY[mResizeSegmentsY.length - 1];
				}
				else if (mStartBlackY ^ (iy & 1) == 1)
				{
					dh = dh * extraY / mSumBlackY;
				}

				aGraphics.drawImage(mImage, aOffsetX + dx, aOffsetY + dy, aOffsetX + dx + dw, aOffsetY + dy + dh, sx, sy, sx + sw, sy + sh, null);

				sy += sh;
				dy += dh;
			}

			sx += sw;
			dx += dw;
		}
	}


	public Insets getPadding()
	{
		return mPadding;
	}


	public BufferedImage getImage()
	{
		return mImage;
	}
}

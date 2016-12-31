package org.terifan.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;


public class ImageResizer
{
	public static BufferedImage getScaledImageAspect(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		double scale = Math.min(aWidth / (double)aSource.getWidth(), aHeight / (double)aSource.getHeight());

		return getScaledImageAspectImpl(aSource, aWidth, aHeight, aQuality, scale);
	}


	public static BufferedImage getScaledImageAspectOuter(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		double scale = Math.max(aWidth / (double)aSource.getWidth(), aHeight / (double)aSource.getHeight());

		return getScaledImageAspectImpl(aSource, aWidth, aHeight, aQuality, scale);
	}


	private static BufferedImage getScaledImageAspectImpl(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality, double aScale)
	{
		int dw = (int)Math.round(aSource.getWidth() * aScale);
		int dh = (int)Math.round(aSource.getHeight() * aScale);

		// make sure one direction has specified dimension
		if (dw != aWidth && dh != aHeight)
		{
			if (Math.abs(aWidth - dw) < Math.abs(aHeight - dh))
			{
				dw = aWidth;
			}
			else
			{
				dh = aHeight;
			}
		}

		return getScaledImage(aSource, dw, dh, aQuality);
	}


	public static BufferedImage getScaledImage(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		if (aWidth < aSource.getWidth() || aHeight < aSource.getHeight())
		{
			aSource = resizeDown(aSource, aWidth, aHeight, aQuality);
		}
		if (aWidth > aSource.getWidth() || aHeight > aSource.getHeight())
		{
			aSource = resizeUp(aSource, aWidth, aHeight, aQuality);
		}

		return aSource;
	}


	private static BufferedImage resizeUp(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		return renderImage(aSource, aWidth, aHeight, aQuality);
	}


	private static BufferedImage resizeDown(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		if (aWidth <= 0 || aHeight <= 0)
		{
			throw new IllegalArgumentException("Target width or height is zero or less: width: " + aWidth + ", height: " + aHeight);
		}

		int currentWidth = aSource.getWidth();
		int currentHeight = aSource.getHeight();
		boolean flush = false;

		do
		{
			if (currentWidth > aWidth)
			{
				currentWidth = Math.max((currentWidth + 1) / 2, aWidth);
			}
			if (currentHeight > aHeight)
			{
				currentHeight = Math.max((currentHeight + 1) / 2, aHeight);
			}

			BufferedImage tmp = renderImage(aSource, currentWidth, currentHeight, aQuality);

			if (flush)
			{
				aSource.flush();
			}
			
			aSource = tmp;
			flush = true;
		}
		while (currentWidth > aWidth || currentHeight > aHeight);

		return aSource;
	}


	private static BufferedImage renderImage(BufferedImage aSource, int aWidth, int aHeight, boolean aQuality)
	{
		BufferedImage output = new BufferedImage(aWidth, aHeight, aSource.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = output.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, aQuality ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(aSource, 0, 0, aWidth, aHeight, null);
		g.dispose();

		return output;
	}


	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameLeft, int aFrameRight, boolean aQuality)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, aQuality ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX + aFrameLeft, aPositionY + aHeight, 0, 0, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX + aFrameLeft, aPositionY, aPositionX + aWidth - aFrameRight, aPositionY + aHeight, aFrameLeft, 0, tw - aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX + aWidth - aFrameRight, aPositionY, aPositionX + aWidth, aPositionY + aHeight, tw - aFrameRight, 0, tw, th, null);
	}


	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameTop, int aFrameLeft, int aFrameBottom, int aFrameRight, boolean aQuality)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, aQuality ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX + aFrameLeft, aPositionY + aFrameTop, 0, 0, aFrameLeft, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX + aFrameLeft, aPositionY, aPositionX + aWidth - aFrameRight, aPositionY + aFrameTop, aFrameLeft, 0, tw - aFrameRight, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX + aWidth - aFrameRight, aPositionY, aPositionX + aWidth, aPositionY + aFrameTop, tw - aFrameRight, 0, tw, aFrameTop, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY + aFrameTop, aPositionX + aFrameLeft, aPositionY + aHeight - aFrameBottom, 0, aFrameTop, aFrameLeft, th - aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX + aFrameLeft, aPositionY + aFrameTop, aPositionX + aWidth - aFrameRight, aPositionY + aHeight - aFrameBottom, aFrameLeft, aFrameTop, tw - aFrameRight, th - aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX + aWidth - aFrameRight, aPositionY + aFrameTop, aPositionX + aWidth, aPositionY + aHeight - aFrameBottom, tw - aFrameRight, aFrameTop, tw, th - aFrameBottom, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY + aHeight - aFrameBottom, aPositionX + aFrameLeft, aPositionY + aHeight, 0, th - aFrameBottom, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX + aFrameLeft, aPositionY + aHeight - aFrameBottom, aPositionX + aWidth - aFrameRight, aPositionY + aHeight, aFrameLeft, th - aFrameBottom, tw - aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX + aWidth - aFrameRight, aPositionY + aHeight - aFrameBottom, aPositionX + aWidth, aPositionY + aHeight, tw - aFrameRight, th - aFrameBottom, tw, th, null);
	}


	public static BufferedImage getScaledImage(BufferedImage aSource, int aWidth, int aHeight, int aFrameTop, int aFrameLeft, int aFrameBottom, int aFrameRight, boolean aQuality)
	{
		BufferedImage image = new BufferedImage(aWidth, aHeight, aSource.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = image.createGraphics();
		drawScaledImage(g, aSource, 0, 0, aWidth, aHeight, aFrameTop, aFrameLeft, aFrameBottom, aFrameRight, aQuality);
		g.dispose();

		return image;
	}
}

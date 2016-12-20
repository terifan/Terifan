package org.terifan.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;


public class ImageResizer
{
	public static BufferedImage resizeAspect(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, boolean aQuality)
	{
		double scale = Math.min(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aQuality);
	}


	public static BufferedImage resizeAspectOuter(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, boolean aQuality)
	{
		double scale = Math.max(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aQuality);
	}


	public static BufferedImage resize(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, boolean aQuality)
	{
		boolean opaque = aSourceImage.getTransparency() == Transparency.OPAQUE;
		int type = opaque ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

		if (aDstWidth < aSourceImage.getWidth() || aDstHeight < aSourceImage.getHeight())
		{
			aSourceImage = resizeDown(aSourceImage, aDstWidth, aDstHeight, aQuality);
		}

		if (aDstWidth > aSourceImage.getWidth() || aDstHeight > aSourceImage.getHeight())
		{
			aSourceImage = resizeUp(aSourceImage, aDstWidth, aDstHeight, aQuality);
		}

		return aSourceImage;
	}


	private static BufferedImage resizeUp(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, boolean aQuality)
	{
		BufferedImage outputImage = new BufferedImage(aDstWidth, aDstHeight, aSourceImage.getType());

		Graphics2D g = outputImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(aSourceImage, 0, 0, aDstWidth, aDstHeight, null);
		g.dispose();

		return outputImage;
	}


	private static BufferedImage resizeDown(BufferedImage aImage, int aTargetWidth, int aTargetHeight, boolean aQuality)
	{
		if (aTargetWidth <= 0 || aTargetHeight <= 0)
		{
			throw new IllegalArgumentException("Width or height is zero or less: width: " + aTargetWidth + ", height: " + aTargetHeight);
		}

		int currentWidth = aImage.getWidth();
		int currentHeight = aImage.getHeight();
		BufferedImage output = aImage;

		do
		{
			int prevCurrentWidth = currentWidth;
			int prevCurrentHeight = currentHeight;

			if (currentWidth > aTargetWidth)
			{
				currentWidth -= currentWidth / 2;
				if (currentWidth < aTargetWidth)
				{
					currentWidth = aTargetWidth;
				}
			}
			if (currentHeight > aTargetHeight)
			{
				currentHeight -= currentHeight / 2;
				if (currentHeight < aTargetHeight)
				{
					currentHeight = aTargetHeight;
				}
			}

			if (prevCurrentWidth == currentWidth && prevCurrentHeight == currentHeight)
			{
				break;
			}

			BufferedImage tmp = new BufferedImage(currentWidth, currentHeight, output.getType());

			Graphics2D g = tmp.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, aQuality ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(output, 0, 0, currentWidth, currentHeight, null);
			g.dispose();

			output = tmp;
		} while (currentWidth != aTargetWidth || currentHeight != aTargetHeight);

		return output;
	}
}

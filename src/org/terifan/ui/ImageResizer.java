package org.terifan.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import org.terifan.ui.FilterFactory.Filter;
import org.terifan.util.StopWatch;
import org.terifan.util.log.Log;


public class ImageResizer
{
	public static void main(String... args)
	{
		try
		{
			BufferedImage src = ImageIO.read(new File("f:/temp/1acpph578vor.jpg"));

//			int W = 1920;
//			int H = 1080;
			int W = 256;
			int H = 256;

			StopWatch stopWatch = new StopWatch();

			BufferedImage dst1 = resizeAspect(src, W, H, null, false);

			stopWatch.split();

			BufferedImage dst2 = resizeAspect(src, W, H, null, true);

			stopWatch.split();

			BufferedImage dst4 = Scalr.resize(src, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, W, H);

			stopWatch.split();

			BufferedImage dst5 = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, W, H);

			stopWatch.stop();

			Log.out.println(stopWatch);

			ImageIO.write(dst1, "png", new File("d:/output-1.png"));
			ImageIO.write(dst2, "png", new File("d:/output-2.png"));
			ImageIO.write(dst4, "png", new File("d:/output-4.png"));
			ImageIO.write(dst5, "png", new File("d:/output-5.png"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	public static BufferedImage resizeAspect(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, AtomicBoolean aAbortProcess, boolean aQuality)
	{
		double scale = Math.min(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aAbortProcess, aQuality);
	}


	public static BufferedImage resizeAspectOuter(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, AtomicBoolean aAbortProcess, boolean aQuality)
	{
		double scale = Math.max(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aAbortProcess, aQuality);
	}


	public static BufferedImage resize(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, AtomicBoolean aAbortProcess, boolean aQuality)
	{
		boolean opaque = aSourceImage.getTransparency() == Transparency.OPAQUE;
		int type = opaque ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

		if (aDstWidth < aSourceImage.getWidth() || aDstHeight < aSourceImage.getHeight())
		{
			aSourceImage = resizeDown(aSourceImage, aDstWidth, aDstHeight, aAbortProcess, aQuality);
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


	private static BufferedImage resizeDown(BufferedImage aImage, int aTargetWidth, int aTargetHeight, AtomicBoolean aAbortProcess, boolean aQuality)
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

			if (prevCurrentWidth == currentWidth && prevCurrentHeight == currentHeight || (aAbortProcess != null && aAbortProcess.get()))
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

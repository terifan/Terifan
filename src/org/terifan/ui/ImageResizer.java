package org.terifan.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import org.terifan.ui.FilterFactory.Filter;
import static org.terifan.ui.FilterFactory.HALF_FIXED_POINT_SCALE;
import org.terifan.util.log.Log;


public class ImageResizer
{
	public static void main(String... args)
	{
		try
		{
			BufferedImage src = ImageIO.read(new File("f:/0561847923.jpg"));

//			BufferedImage dst1 = resizeAspect(src, 1920, 1080, FilterFactory.Lanczos3, null);
			BufferedImage dst1 = resize(src, 706, 1246, FilterFactory.Lanczos3, null);
//			BufferedImage dst3 = resizeAspect(src, 256, 256, FilterFactory.Lanczos3, null);

			BufferedImage dst2 = Scalr.resize(src, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 1920, 1080);

			ImageIO.write(dst1, "png", new File("d:/output-1.png"));
			ImageIO.write(dst2, "png", new File("d:/output-2.png"));
//			ImageIO.write(dst3, "png", new File("d:/output-3.png"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	public static BufferedImage resizeAspect(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, Filter aFilter, AtomicBoolean aAbortProcess)
	{
		double scale = Math.min(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aFilter, null);
	}


	public static BufferedImage resizeAspectOuter(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, Filter aFilter, AtomicBoolean aAbortProcess)
	{
		double scale = Math.max(aDstWidth / (double)aSourceImage.getWidth(), aDstHeight / (double)aSourceImage.getHeight());

		int dw = (int)Math.round(aSourceImage.getWidth() * scale);
		int dh = (int)Math.round(aSourceImage.getHeight() * scale);

		return resize(aSourceImage, dw, dh, aFilter, null);
	}


	public static BufferedImage resize(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, Filter aFilter, AtomicBoolean aAbortProcess)
	{
		boolean opaque = aSourceImage.getTransparency() == Transparency.OPAQUE;
		int type = opaque ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

		if (aDstWidth < aSourceImage.getWidth() || aDstHeight < aSourceImage.getHeight())
		{
			aSourceImage = resizeDown(aSourceImage, aDstWidth, aDstHeight, type, aAbortProcess);
		}

		if (aDstWidth > aSourceImage.getWidth() || aDstHeight > aSourceImage.getHeight())
		{
			aSourceImage = resizeUp(aSourceImage, aDstWidth, aDstHeight, type, aAbortProcess, aFilter);
		}

		return aSourceImage;
	}


	private static BufferedImage resizeUp(BufferedImage aSourceImage, int aDstWidth, int aDstHeight, int aType, AtomicBoolean aAbortProcess, Filter aFilter)
	{
		BufferedImage outputImage = new BufferedImage(aDstWidth, aDstHeight, aType);

		int srcWidth = aSourceImage.getWidth();
		int srcHeight = aSourceImage.getHeight();
		
		double scaleX = srcWidth / (double)aDstWidth;
		double scaleY = srcHeight / (double)aDstHeight;
		
		int[] filterH = aFilter.getKernel1DInt((int)Math.ceil(aFilter.getRadius() * scaleX) * 2 + 1);
		int[] filterV = aFilter.getKernel1DInt((int)Math.ceil(aFilter.getRadius() * scaleY) * 2 + 1);

		int[] workPixels = new int[aDstWidth * srcHeight];
		
		processHorizontal(aSourceImage, srcWidth, srcHeight, aDstWidth, workPixels, scaleX, filterH, aAbortProcess);
		processVertical(outputImage, srcHeight, aDstWidth, aDstHeight, workPixels, scaleY, filterV, aAbortProcess);

		return outputImage;
	}


	private static void processHorizontal(BufferedImage aSourceImage, int aSrcWidth, int aSrcHeight, int aDstWidth, int[] aWorkPixels, double aScaleX, int[] aFilter, AtomicBoolean aAbortProcess)
	{
		if (aDstWidth == aSrcWidth)
		{
			aSourceImage.getRGB(0, 0, aSrcWidth, aSrcHeight, aWorkPixels, 0, aSrcWidth);
			return;
		}
		
		int[] intBuffer = new int[aSrcWidth];
		byte[] byteBuffer = new byte[4 * aSrcWidth];

		int filterSize = aFilter.length / 2;
		int scale = (int)(aScaleX / 2);

		for (int dy = 0; dy < aSrcHeight; dy++)
		{
			if (aAbortProcess != null && aAbortProcess.get())
			{
				return;
			}

			switch (aSourceImage.getType())
			{
				case BufferedImage.TYPE_3BYTE_BGR:
				case BufferedImage.TYPE_4BYTE_ABGR:
				case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				case BufferedImage.TYPE_BYTE_GRAY:
					aSourceImage.getRaster().getDataElements(0, dy, aSrcWidth, 1, byteBuffer);
					break;
				case BufferedImage.TYPE_INT_BGR:
				case BufferedImage.TYPE_INT_RGB:
				case BufferedImage.TYPE_INT_ARGB:
				case BufferedImage.TYPE_INT_ARGB_PRE:
					aSourceImage.getRaster().getDataElements(0, dy, aSrcWidth, 1, intBuffer);
					break;
				default:
					aSourceImage.getRGB(0, dy, aSrcWidth, 1, intBuffer, 0, aSrcWidth);
					break;
			}

			for (int dx = 0; dx < aDstWidth; dx++)
			{
				int min = Math.max(-filterSize, -dx - scale);
				int sx = (int)(dx * aScaleX + 0.5) + min;
				int max = Math.min(aSrcWidth - sx + min, Math.min(filterSize, aDstWidth - dx + scale));

				if (dy==0)Log.out.println(min+" "+max+" "+sx);
				
				min += filterSize;
				max += filterSize;

				int sample0 = 0;
				int sample1 = 0;
				int sample2 = 0;
				int sample3 = 0;
				int sum = 0;

				switch (aSourceImage.getType())
				{
					case BufferedImage.TYPE_3BYTE_BGR:
						sx *= 3;
						for (int z = min; z <= max; z++)
						{
							int weight = aFilter[z];
							sample0 += weight * (0xFF & byteBuffer[sx++]);
							sample1 += weight * (0xFF & byteBuffer[sx++]);
							sample2 += weight * (0xFF & byteBuffer[sx++]);
							sum += weight;
						}
						break;
					case BufferedImage.TYPE_4BYTE_ABGR:
					case BufferedImage.TYPE_4BYTE_ABGR_PRE:
						sx *= 4;
						for (int z = min; z <= max; z++)
						{
							int weight = aFilter[z];
							sample0 += weight * (0xFF & byteBuffer[sx++]);
							sample1 += weight * (0xFF & byteBuffer[sx++]);
							sample2 += weight * (0xFF & byteBuffer[sx++]);
							sample3 += weight * (0xFF & byteBuffer[sx++]);
							sum += weight;
						}
						break;
					case BufferedImage.TYPE_BYTE_GRAY:
						for (int z = min; z <= max; z++, sx++)
						{
							int weight = aFilter[z];
							sample0 += weight * (0xFF & byteBuffer[sx]);
							sum += weight;
						}
						sample1 = sample0;
						sample2 = sample0;
						break;
					case BufferedImage.TYPE_INT_BGR:
						for (int z = min; z <= max; z++, sx++)
						{
							int weight = aFilter[z];
							int c = intBuffer[sx];
							sample0 += weight * (0xFF & (c));
							sample1 += weight * (0xFF & (c >> 8));
							sample2 += weight * (0xFF & (c >> 16));
							sum += weight;
						}
						break;
					case BufferedImage.TYPE_INT_RGB:
						for (int z = min; z <= max; z++, sx++)
						{
							int weight = aFilter[z];
							int c = intBuffer[sx];
							sample0 += weight * (0xFF & (c >> 16));
							sample1 += weight * (0xFF & (c >> 8));
							sample2 += weight * (0xFF & (c));
							sum += weight;
						}
						break;
					case BufferedImage.TYPE_INT_ARGB:
					case BufferedImage.TYPE_INT_ARGB_PRE:
					default:
						for (int z = min; z <= max; z++, sx++)
						{
							int weight = aFilter[z];
							int c = intBuffer[sx];
							sample0 += weight * (0xFF & (c >> 16));
							sample1 += weight * (0xFF & (c >> 8));
							sample2 += weight * (0xFF & (c));
							sample3 += weight * (0xFF & (c >>> 24));
							sum += weight;
						}
						break;
				}

				aWorkPixels[dy * aDstWidth + dx] = toRGB(sample0, sample1, sample2, sample3, sum);
			}
		}
	}


	private static void processVertical(BufferedImage aOutputImage, int aSrcHeight, int aDstWidth, int aDstHeight, int[] aWorkPixels, double aScaleY, int[] aFilter, AtomicBoolean aAbortProcess)
	{
		if (aDstHeight == aSrcHeight)
		{
			aOutputImage.setRGB(0, 0, aDstWidth, aDstHeight, aWorkPixels, 0, aDstWidth);
			return;
		}

		int filterSize = aFilter.length / 2;
		int scale = (int)(aScaleY / 2);

		for (int dx = 0; dx < aDstWidth; dx++)
		{
			if (aAbortProcess != null && aAbortProcess.get())
			{
				return;
			}

			for (int dy = 0; dy < aDstHeight; dy++)
			{
				int sample0 = 0;
				int sample1 = 0;
				int sample2 = 0;
				int sample3 = 0;
				int sum = 0;

				int min = Math.max(-filterSize, -dy - scale);
				int sy = (int)((dy + 0.5) * aScaleY + 0.5) + min;
				int max = Math.min(aSrcHeight - sy + min - 1, Math.min(filterSize, aDstHeight - dy + scale));

				min += filterSize;
				max += filterSize;

				for (int z = min, wp = sy * aDstWidth + dx; z <= max; z++, wp+=aDstWidth)
				{
					int c = aWorkPixels[wp];
					int weight = aFilter[z];
					sample0 += weight * (0xff & (c >> 16));
					sample1 += weight * (0xff & (c >> 8));
					sample2 += weight * (0xff & (c));
					sample3 += weight * (0xff & (c >>> 24));
					sum += weight;
				}

				aOutputImage.setRGB(dx, dy, toRGB(sample0, sample1, sample2, sample3, sum));
			}
		}
	}


	private static int toRGB(int sample0, int sample1, int sample2, int sample3, int sum)
	{
		if (sum > 0)
		{
			sample0 = (sample0 + HALF_FIXED_POINT_SCALE) / sum;
			sample1 = (sample1 + HALF_FIXED_POINT_SCALE) / sum;
			sample2 = (sample2 + HALF_FIXED_POINT_SCALE) / sum;
			sample3 = (sample3 + HALF_FIXED_POINT_SCALE) / sum;
		}

		int r = sample0 < 0 ? 0 : sample0 > 255 ? 255 : sample0;
		int g = sample1 < 0 ? 0 : sample1 > 255 ? 255 : sample1;
		int b = sample2 < 0 ? 0 : sample2 > 255 ? 255 : sample2;
		int a = sample3 < 0 ? 0 : sample3 > 255 ? 255 : sample3;

		return (a << 24) + (r << 16) + (g << 8) + b;
	}


	private static BufferedImage resizeDown(BufferedImage aImage, int aTargetWidth, int aTargetHeight, int aType, AtomicBoolean aAbortProcess)
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

			BufferedImage tmp = new BufferedImage(currentWidth, currentHeight, aType);

			Graphics2D g = tmp.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(output, 0, 0, currentWidth, currentHeight, null);
			g.dispose();

			output = tmp;
		} while (currentWidth != aTargetWidth || currentHeight != aTargetHeight);

		return output;
	}
}

package org.terifan.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Function;


public class ColorHeatMap
{
	/** blue-to-red rainbow scale */
	public final static int[] NARROW =
	{
		0x0000FF, // Blue
		0x00FFFF, // Cyan
		0x00FF00, // Green
		0xFFFF00, // Yellow
		0xFF0000, // Red
	};
	/** blue-to-red rainbow scale with thin black minimum and white maximum */
	public final static int[] WIDE =
	{
		0x000000, // Black
		0x0000FF, // Blue
		0x0020FF,
		0x0040FF,
		0x0060FF,
		0x0080FF,
		0x00A0FF,
		0x00C0FF,
		0x00E0FF,
		0x00FFFF, // Cyan
		0x00FFE0,
		0x00FFC0,
		0x00FFA0,
		0x00FF80,
		0x00FF60,
		0x00FF40,
		0x00FF20,
		0x00FF00, // Green
		0x20FF00,
		0x40FF00,
		0x60FF00,
		0x80FF00,
		0xA0FF00,
		0xC0FF00,
		0xE0FF00,
		0xFFFF00, // Yellow
		0xFFE000,
		0xFFC000,
		0xFFA000,
		0xFF8000,
		0xFF6000,
		0xFF4000,
		0xFF2000,
		0xFF0000, // Red
		0xFFFFFF  // White
	};
	public final static int[] BLACK_BLUE =
	{
		0x000000,
		0x000040,
		0x000080,
		0x0000C0,
		0x0000FF
	};


	private final double mMaxValue;
	private final int[] mColors;


	public ColorHeatMap(double aMaxValueInclusive, int... aColors)
	{
		mMaxValue = aMaxValueInclusive;
		mColors = aColors;
	}


	public int getRGBForValue(double aValue)
	{
		if (aValue < 0 || aValue > mMaxValue)
		{
			throw new IllegalArgumentException("0 <= " + aValue + " <= " + mMaxValue);
		}

		double valPerc = aValue / mMaxValue;
		double colorPerc = 1.0 / (mColors.length - 1);
		double blockOfColor = valPerc / colorPerc;
		int blockIdx = (int)blockOfColor;
		double valPercResidual = valPerc - (blockIdx * colorPerc);
		double percOfColor = valPercResidual / colorPerc;

		int first = mColors[Math.min(blockIdx, mColors.length - 1)];
		int second = mColors[Math.min(blockIdx + 1, mColors.length - 1)];

		int r = Math.max(0, Math.min(255, (0xFF & (first >> 16)) + (int)(((0xFF & (second >> 16)) - (0xFF & (first >> 16))) * percOfColor)));
		int g = Math.max(0, Math.min(255, (0xFF & (first >>  8)) + (int)(((0xFF & (second >>  8)) - (0xFF & (first >>  8))) * percOfColor)));
		int b = Math.max(0, Math.min(255, (0xFF & (first >>  0)) + (int)(((0xFF & (second >>  0)) - (0xFF & (first >>  0))) * percOfColor)));

		return (r << 16) + (g << 8) + b;
	}


	public static void main(String ... args)
	{
		try
		{
			java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(1000, 50, java.awt.image.BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D g = image.createGraphics();
			int h = image.getHeight();
			int s = 0;
			ArrayList<Function<Integer, Integer>> op = new ArrayList<>();
			op.add(e -> new ColorHeatMap(image.getWidth(), WIDE).getRGBForValue(e));
			op.add(e -> new ColorHeatMap(image.getWidth(), NARROW).getRGBForValue(e));
			for (Function<Integer, Integer> fn : op)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					g.setColor(new Color(fn.apply(x)));
					g.drawLine(x, h * s / op.size(), x, h * (s + 1) / op.size());
				}
				s++;
			}
			javax.imageio.ImageIO.write(image, "png", new java.io.File("d:\\output.png"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

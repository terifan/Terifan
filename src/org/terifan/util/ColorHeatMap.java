package org.terifan.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Function;


public class ColorHeatMap
{
	public final static int[][] COLORS =
	{
		{
			0x0000FF, // Blue
			0x00FFFF, // Cyan
			0x00FF00, // Green
			0xFFFF00, // Yellow
			0xFF0000, // Red
		},
		{
			0x000000, // Black
			0x0000FF, // Blue
			0x0040FF, // Cyan
			0x0080FF, // Cyan
			0x00C0FF, // Cyan
			0x00FFFF, // Cyan
			0x00FFC0, // Green
			0x00FF80, // Green
			0x00FF40, // Green
			0x00FF00, // Green
			0x40FF00, // Yellow
			0x80FF00, // Yellow
			0xC0FF00, // Yellow
			0xFFFF00, // Yellow
			0xFFC000, // Red
			0xFF8000, // Red
			0xFF4000, // Red
			0xFF0000, // Red
			0xFFFFFF // White
		},
		{
			0x000000,
			0x000040,
			0x000080,
			0x0000C0,
			0x0000FF
		}
	};


	private ColorHeatMap()
	{
	}


	/** blue-to-red rainbow scale */
	public static int getNarrowScale(double aValue, double aMaxVal)
	{
		return getRGBForValue(COLORS[0], aValue, aMaxVal);
	}


	/** blue-to-red rainbow scale with black minimum and white maximum */
	public static int getWideScale(double aValue, double aMaxVal)
	{
		return getRGBForValue(COLORS[1], aValue, aMaxVal);
	}


	/** black-to-blue scale */
	public static int getBlueScale(double aValue, double aMaxVal)
	{
		return getRGBForValue(COLORS[2], aValue, aMaxVal);
	}


	public static int getRGBForValue(int[] aColors, double aValue, double aMaxVal)
	{
		if (aValue < 0)
		{
			throw new IllegalArgumentException("aValue < 0");
		}
		if (aMaxVal <= 0)
		{
			throw new IllegalArgumentException("aMaxVal <= 0");
		}

		int[] colors = aColors;

		double valPerc = aValue / aMaxVal;
		double colorPerc = 1.0 / (colors.length - 1);
		double blockOfColor = valPerc / colorPerc;
		int blockIdx = (int)blockOfColor;
		double valPercResidual = valPerc - (blockIdx * colorPerc);
		double percOfColor = valPercResidual / colorPerc;

		int first = colors[Math.min(blockIdx, colors.length - 1)];
		int second = colors[Math.min(blockIdx + 1, colors.length - 1)];

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
			op.add(e -> ColorHeatMap.getBlueScale(e, image.getWidth()));
			op.add(e -> ColorHeatMap.getNarrowScale(e, image.getWidth()));
			op.add(e -> ColorHeatMap.getWideScale(e, image.getWidth()));
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

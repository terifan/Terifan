package org.terifan.util;

import java.awt.Color;


public class ColorHeatMap
{
	public final static int[][][] COLORS =
	{
		{
			{0, 0, 0xFF}, // Blue
			{0, 0xFF, 0xFF}, // Cyan
			{0, 0xFF, 0x00}, // Green
			{0xFF, 0xFF, 0}, // Yellow
			{0xFF, 0x00, 0}, // Red
		},
		{
			{0, 0, 0}, // Black
			{0, 0, 0xFF}, // Blue
			{0, 0x40, 0xFF}, // Cyan
			{0, 0x80, 0xFF}, // Cyan
			{0, 0xC0, 0xFF}, // Cyan
			{0, 0xFF, 0xFF}, // Cyan
			{0, 0xFF, 0xC0}, // Green
			{0, 0xFF, 0x80}, // Green
			{0, 0xFF, 0x40}, // Green
			{0, 0xFF, 0x00}, // Green
			{0x40, 0xFF, 0}, // Yellow
			{0x80, 0xFF, 0}, // Yellow
			{0xC0, 0xFF, 0}, // Yellow
			{0xFF, 0xFF, 0}, // Yellow
			{0xFF, 0xC0, 0}, // Red
			{0xFF, 0x80, 0}, // Red
			{0xFF, 0x40, 0}, // Red
			{0xFF, 0x00, 0}, // Red
			{0xFF, 0xFF, 0xFF} // White
		}
	};


	private ColorHeatMap()
	{
	}


	/**
	 *
	 * @param aHighlightLimits
	 *   if true the scale starts with black for lowest values and ends with white for largest values
	 */
	public static int getRGBForValue(boolean aHighlightLimits, double aValue, double aMaxVal)
	{
		if (aValue < 0)
		{
			throw new IllegalArgumentException("aValue < 0");
		}
		if (aMaxVal <= 0)
		{
			throw new IllegalArgumentException("aMaxVal <= 0");
		}

		int[][] colors = COLORS[aHighlightLimits ? 1 : 0];

		double valPerc = aValue / aMaxVal;
		double colorPerc = 1.0 / (colors.length - 1);
		double blockOfColor = valPerc / colorPerc;
		int blockIdx = (int)blockOfColor;
		double valPercResidual = valPerc - (blockIdx * colorPerc);
		double percOfColor = valPercResidual / colorPerc;

		int[] first = colors[Math.min(blockIdx, colors.length - 1)];
		int[] second = colors[Math.min(blockIdx + 1, colors.length - 1)];

		int r = Math.max(0, Math.min(255, first[0] + (int)((second[0] - first[0]) * percOfColor)));
		int g = Math.max(0, Math.min(255, first[1] + (int)((second[1] - first[1]) * percOfColor)));
		int b = Math.max(0, Math.min(255, first[2] + (int)((second[2] - first[2]) * percOfColor)));

		return (r << 16) + (g << 8) + b;
	}


	public static Color getColorForValue(boolean aHighlightLimits, double aValue, double aMaxVal)
	{
		return new Color(getRGBForValue(aHighlightLimits, aValue, aMaxVal));
	}


	public static void main(String ... args)
	{
		try
		{
			java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(1000, 50, java.awt.image.BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D g = image.createGraphics();
			int h = image.getHeight();
			for (int s = 0; s <= 1; s++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					g.setColor(ColorHeatMap.getColorForValue(s == 1, x, image.getWidth()));
					g.drawLine(x, h*s/2, x, h*(s+1)/2);
				}
			}
			javax.imageio.ImageIO.write(image, "png", new java.io.File("d:\\output.png"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

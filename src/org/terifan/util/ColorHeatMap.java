package org.terifan.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Function;


public class ColorHeatMap
{
	/** fades blue, cyan, green, yellow to red */
	public final static ColorGradient WIDE = new ColorGradient(
		-1,
		-1,
		0x0000FF, // blue
		0x00FFFF, // cyan
		0x00FF00, // green
		0xFFFF00, // yellow
		0xFF0000 // red
	);
	/** fades blue, cyan, green, yellow to red with black as min and white as max */
	public final static ColorGradient MINWIDEMAX = new ColorGradient(
		0x000000, // black
		0xFFFFFF, // white
		0x0000FF, // blue
		0x00FFFF, // cyan
		0x00FF00, // green
		0xFFFF00, // yellow
		0xFF0000 // red
	);
	/** fades black to blue */
	public final static ColorGradient BB = new ColorGradient(
		-1,
		-1,
		0x000000, // black
		0x0000FF // blue
	);
	/** fades from yellow to red, min is green */
	public final static ColorGradient MINGYR = new ColorGradient(
		0x63BE7B, // green
		-1,
		0xFEEB84, // yellow
		0xF8696B // red
	);
	/** fades from yellow to green, min is red */
	public final static ColorGradient MINRYG = new ColorGradient(
		0xF8696B, // red
		-1,
		0xFEEB84, // yellow
		0x63BE7B // green
	);
	/** fades from green to yellow to red */
	public final static ColorGradient GYR = new ColorGradient(
		-1,
		-1,
		0x63BE7B, // green
		0xFEEB84, // yellow
		0xF8696B // red
	);
	/** fades from red to yellow to green */
	public final static ColorGradient RYG = new ColorGradient(
		-1,
		-1,
		0xF8696B, // red
		0xFEEB84, // yellow
		0x63BE7B // green
	);
	/** fades blue, magenta, red, yellow, green to cyan */
	public final static ColorGradient RAINBOW = new ColorGradient(
		-1,
		-1,
		0x0000FF,
		0xFF00FF,
		0xFF0000,
		0xFFFF00,
		0x00FF00,
		0x00FFFF
	);


	private ColorHeatMap()
	{
	}


	public static int getRGBForValue(double aMinValue, double aMaxValue, double aValue, ColorGradient aColors)
	{
		if (aValue <= aMinValue && aColors.mMin >= 0) return aColors.mMin;
		if (aValue >= aMaxValue && aColors.mMax >= 0) return aColors.mMax;

		aValue = Math.max(aValue, aMinValue);
		aValue = Math.min(aValue, aMaxValue);

		double valPerc = aValue / aMaxValue;
		double colorPerc = 1.0 / (aColors.mGradient.length - 1);
		double blockOfColor = valPerc / colorPerc;
		int blockIdx = (int)blockOfColor;
		double valPercResidual = valPerc - (blockIdx * colorPerc);
		double percOfColor = valPercResidual / colorPerc;

		int first = aColors.mGradient[Math.min(blockIdx, aColors.mGradient.length - 1)];
		int second = aColors.mGradient[Math.min(blockIdx + 1, aColors.mGradient.length - 1)];

		int r = Math.max(0, Math.min(255, (0xFF & (first >> 16)) + (int)(((0xFF & (second >> 16)) - (0xFF & (first >> 16))) * percOfColor)));
		int g = Math.max(0, Math.min(255, (0xFF & (first >>  8)) + (int)(((0xFF & (second >>  8)) - (0xFF & (first >>  8))) * percOfColor)));
		int b = Math.max(0, Math.min(255, (0xFF & (first >>  0)) + (int)(((0xFF & (second >>  0)) - (0xFF & (first >>  0))) * percOfColor)));

		return (r << 16) + (g << 8) + b;
	}


	public static class ColorGradient
	{
		final int mMin;
		final int mMax;
		final int[] mGradient;


		/**
		 * Create a color gradient with optional mimimum and maximum colors and a list of colors to fade between.
		 *
		 * @param aColorForMinimum
		 *   the RGB value for the minimum or a negative value if unused
		 * @param aColorForMaximum
		 *   the RGB value for the maximum or a negative value if unused
		 * @param aColorGradient
		 *   one or more colors to fade between
		 */
		public ColorGradient(int aColorForMinimum, int aColorForMaximum, int... aColorGradient)
		{
			if (aColorGradient.length == 0)
			{
				throw new IllegalArgumentException();
			}

			mMin = aColorForMinimum;
			mMax = aColorForMaximum;
			mGradient = aColorGradient;
		}
	}


	public static void main(String ... args)
	{
		try
		{
			java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(300, 300, java.awt.image.BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D g = image.createGraphics();
			int h = image.getHeight();
			int s = 0;

			ArrayList<Function<Integer, Integer>> op = new ArrayList<>();
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, MINWIDEMAX));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, WIDE));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, RYG));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, MINRYG));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, GYR));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, MINGYR));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, BB));
			op.add(e -> ColorHeatMap.getRGBForValue(0, image.getWidth() - 1, e, RAINBOW));

			for (Function<Integer, Integer> fn : op)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					g.setColor(new Color(fn.apply(x)));
					g.drawLine(x, h * s / op.size(), x, h * (s + 1) / op.size()-2);
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

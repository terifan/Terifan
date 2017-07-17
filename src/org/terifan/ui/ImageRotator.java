package org.terifan.ui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class ImageRotator
{
	public static BufferedImage rotate(BufferedImage aImage, double aAngle)
	{
		return rotate(aImage, aAngle, 1, 0xff000000);
	}


	public static BufferedImage rotate(BufferedImage aImage, double aAngle, int aQuality, int aBackgroundColor)
	{
		if (aAngle < 0 || aAngle >= 360)
		{
			throw new IllegalArgumentException("Angle not supported: " + aAngle);
		}

		int sw = aImage.getWidth();
		int sh = aImage.getHeight();

		if (aAngle == 90)
		{
			aImage = rotateFast(aImage, sh, sw, sh / 2, sh / 2, 1);
		}
		else if (aAngle == 180)
		{
			aImage = rotateFast(aImage, sw, sh, sw / 2, sh / 2, 2);
		}
		else if (aAngle == 270)
		{
			aImage = rotateFast(aImage, sh, sw, sw / 2, sw / 2, 3);
		}
		else if (aAngle != 0)
		{
			aImage = new ImageRotatorBuffer(aImage).rotate(aAngle, aQuality, aBackgroundColor).getImage();
		}

		return aImage;
	}


	private static BufferedImage rotateFast(BufferedImage aImage, int dw, int dh, int centerx, int centery, int steps)
	{
		BufferedImage dest = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dest.createGraphics();
		g.setTransform(AffineTransform.getQuadrantRotateInstance(steps, centerx, centery));
		g.drawImage(aImage, 0, 0, null);
		g.dispose();

		return dest;
	}


	public static BufferedImage flip(BufferedImage aImage)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -aImage.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		aImage = op.filter(aImage, null);

		return aImage;
	}


	public static BufferedImage mirror(BufferedImage aImage)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-aImage.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		aImage = op.filter(aImage, null);

		return aImage;
	}
}

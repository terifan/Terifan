package org.terifan.forms;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;


public class ImageIcon extends javax.swing.ImageIcon implements Icon
{
	public ImageIcon()
	{
	}


	public ImageIcon(byte[] aImageData)
	{
		this(aImageData, null);
	}


	public ImageIcon(byte[] aImageData, String aDescription)
	{
		try
		{
			setImage(ImageIO.read(new ByteArrayInputStream(aImageData)));
			setDescription(aDescription);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public ImageIcon(Image aImage)
	{
		this(aImage, null);
	}


	public ImageIcon(Image aImage, String aDescription)
	{
		super(aImage, aDescription);
	}


	public ImageIcon(URL aLocation)
	{
		this(aLocation, null);
	}


	public ImageIcon(URL aLocation, String aDescription)
	{
		try
		{
			setImage(ImageIO.read(aLocation));
			setDescription(aDescription);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public ImageIcon(InputStream aStream)
	{
		this(aStream, null);
	}


	public ImageIcon(InputStream aStream, String aDescription)
	{
		try
		{
			setImage(ImageIO.read(aStream));
			setDescription(aDescription);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public ImageIcon(String aFilename)
	{
		this(aFilename, null);
	}


	public ImageIcon(String aFilename, String aDescription)
	{
		try
		{
			setImage(ImageIO.read(new File(aFilename)));
			setDescription(aDescription);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public ImageIcon(Class aClass, String aFilename)
	{
		this(aClass, aFilename, null);
	}


	public ImageIcon(Class aClass, String aFilename, String aDescription)
	{
		try
		{
			InputStream s = aClass.getResourceAsStream(aFilename);
			setImage(ImageIO.read(s));
			setDescription(aDescription);
			s.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public ImageIcon(javax.swing.Icon aIcon)
	{
		this(aIcon, null);
	}


	public ImageIcon(javax.swing.Icon aIcon, String aDescription)
	{
		if (aIcon instanceof javax.swing.ImageIcon)
		{
			setImage(((javax.swing.ImageIcon)aIcon).getImage());
		}
		else
		{
			BufferedImage image = new BufferedImage(aIcon.getIconWidth(), aIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			aIcon.paintIcon(null, g, 0, 0);
			g.dispose();
			setImage(image);
		}
		setDescription(aDescription);
	}


	@Override
	public void paintIcon(Component aComponent, Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight)
	{
		if (getImageObserver() == null)
		{
			aGraphics.drawImage(getImage(), aOriginX, aOriginY, aWidth, aHeight, aComponent);
		}
		else
		{
			aGraphics.drawImage(getImage(), aOriginX, aOriginY, aWidth, aHeight, getImageObserver());
		}
	}
}

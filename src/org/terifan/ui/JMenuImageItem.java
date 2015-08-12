package org.terifan.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;


public class JMenuImageItem extends JMenuItem
{
	private BufferedImage mImage;
	private Integer mOffsetX;


	public JMenuImageItem(BufferedImage aImage)
	{
		mImage = aImage;
	}


	public JMenuImageItem(BufferedImage aImage, Action aAction)
	{
		super(aAction);
		mImage = aImage;
	}


	public JMenuImageItem(BufferedImage aImage, int aMnemonic, Action aAction)
	{
		super(aAction);
		aAction.putValue(AbstractAction.MNEMONIC_KEY, aMnemonic);
		mImage = aImage;
	}


	public JMenuImageItem(URL aURL, Action aAction) throws IOException
	{
		super(aAction);

		try (InputStream in = aURL.openStream())
		{
			mImage = ImageIO.read(in);
		}
	}


	public JMenuImageItem(URL aURL, int aMnemonic, Action aAction) throws IOException
	{
		super(aAction);
		aAction.putValue(AbstractAction.MNEMONIC_KEY, aMnemonic);

//		setUI(new BasicCheckBoxMenuItemUI() {
//			@Override
//			protected void doClick(MenuSelectionManager msm) {
//			   menuItem.doClick(0);
//			}
//		 });

		try (InputStream in = aURL.openStream())
		{
			mImage = ImageIO.read(in);
		}
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		super.paintComponent(aGraphics);

		aGraphics.drawImage(mImage, mOffsetX - 5, (getHeight() - mImage.getHeight()) / 2, null);
	}


	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();

		if (mOffsetX == null)
		{
			mOffsetX = d.width;
		}

		d.width += mImage.getWidth();
		d.height = Math.max(d.height, 6 + mImage.getHeight());

		return d;
	}
}

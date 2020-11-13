package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.terifan.ui.Anchor;
import org.terifan.ui.ColorSet;
import static org.terifan.ui.ColorSet.FOCUSED;
import static org.terifan.ui.ColorSet.UNFOCUSED;
import org.terifan.ui.NinePatchImage;
import org.terifan.ui.TextBox;


public class GreenWindowBorder extends DefaultWindowBorder
{
	protected NinePatchImage[] mTabsImages;
	protected NinePatchImage[] mMenuImages;


	@Override
	protected void setupStyle()
	{
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 13);

		mTitleBarBackground = new ColorSet()
			.add(new Color(235, 235, 235), UNFOCUSED)
			.add(new Color(154, 205, 61), FOCUSED);
		mTitleBarForeground = new ColorSet()
			.add(new Color(0, 0, 0), UNFOCUSED)
			.add(new Color(0, 0, 0), FOCUSED);
		mBorderInner = new ColorSet()
			.add(new Color(235, 235, 235), UNFOCUSED)
			.add(new Color(154, 205, 61), FOCUSED);
		mBorderOuter = new ColorSet()
			.add(new Color(211, 211, 211), UNFOCUSED)
			.add(new Color(118, 157, 47), FOCUSED);

		try
		{
			mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_2.png"));
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}

		try
		{
			BufferedImage template = ImageIO.read(FullScreenWindow.class.getResource("tab_buttons_1.png"));
			int h = template.getHeight() / 4;
			mTabsImages = new NinePatchImage[]
			{
				new NinePatchImage(template.getSubimage(0, h*0, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*1, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*2, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*3, template.getWidth(), h))
			};
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}

		try
		{
			BufferedImage template = ImageIO.read(FullScreenWindow.class.getResource("menu_buttons_1.png"));
			int h = template.getHeight() / 4;
			mMenuImages = new NinePatchImage[]
			{
				new NinePatchImage(template.getSubimage(0, h*0, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*1, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*2, template.getWidth(), h)),
				new NinePatchImage(template.getSubimage(0, h*3, template.getWidth(), h))
			};
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}

		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 4;

		mBorderSize = 6;
	}


	@Override
	protected void paintTitleText(Graphics2D aGraphics, FullScreenWindow aWindow, int aX, int aY, int aWidth, int aHeight, Point aPointer)
	{
		Graphics2D g = (Graphics2D)aGraphics;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		aX += mBorderSize;
		aWidth -= mBorderSize;

		String[] options =
		{
			"File", "Edit", "Window"
		};
		String[] tabs =
		{
			"Modeling", "Character", "Layout", "Rendering"
		};

		for (int i = 0; i < options.length; i++)
		{
			TextBox tb = new TextBox(options[i])
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			int z = 0;
			if (aPointer.x >= aX && aPointer.x < aX + m && aPointer.y >= aY && aPointer.y < aY + aHeight)
			{
				z = 2;
			}
			else
			{
				z = 0;
			}
			mMenuImages[z].paintImage(aGraphics, aX, aY + 6, m, aHeight - 10);

			tb.setPadding(0, 10, 0, 0).render(aGraphics);

			aX += m;
		}

		aX += 20;
		aY += 10;
		aHeight -= 10;

		for (int i = 0; i < tabs.length; i++)
		{
			TextBox tb = new TextBox(tabs[i])
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			int z = 0;
			if (aPointer.x >= aX && aPointer.x < aX + m && aPointer.y >= aY && aPointer.y < aY + aHeight)
			{
				if (i == 1)
				{
					z = 3;
				}
				else
				{
					z = 2;
				}
			}
			else
			{
				if (i == 1)
				{
					z = 1;
				}
				else
				{
					z = 0;
				}
			}
			mTabsImages[z].paintImage(aGraphics, aX, aY, m, aHeight);

			tb.setPadding(0, 10, 0, 0).render(aGraphics);

			aX += m + 5;
		}
	}
}

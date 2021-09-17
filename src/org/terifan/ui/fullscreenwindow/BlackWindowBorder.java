package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.terifan.ui.Anchor;
import org.terifan.ui.ColorSet;
import static org.terifan.ui.ColorSet.FOCUSED;
import static org.terifan.ui.ColorSet.UNFOCUSED;
import org.terifan.ui.NinePatchImage;
import org.terifan.ui.TextBox;


public class BlackWindowBorder extends DefaultWindowBorder
{
	protected NinePatchImage[] mTabImages;
	protected NinePatchImage[] mMenuImages;


	public BlackWindowBorder()
	{
		super();
	}


	@Override
	protected void paintBorder(FullScreenWindow aWindow, Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, WindowButtonType aHoverButton, WindowButtonType aArmedButton, Point aPointer)
	{
		layoutButtons(aX, aY, aWidth, aHeight);

		super.paintBorder(aWindow, aGraphics, aX, aY, aWidth, aHeight, aHoverButton, aArmedButton, aPointer);
	}


	@Override
	protected void setupStyle() throws IOException
	{
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 12);

		mTitleBarBackground = new ColorSet()
			.add(new Color(35, 35, 35), UNFOCUSED)
			.add(new Color(35, 35, 35), FOCUSED);
		mTitleBarForeground = new ColorSet()
			.add(new Color(201, 201, 201), UNFOCUSED)
			.add(new Color(201, 201, 201), FOCUSED);
		mBorderInner = new ColorSet()
			.add(new Color(35, 35, 35), UNFOCUSED)
			.add(new Color(35, 35, 35), FOCUSED);
		mBorderOuter = new ColorSet()
			.add(new Color(35, 35, 35), UNFOCUSED)
			.add(new Color(35, 35, 35), FOCUSED);

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_3.png"));

		BufferedImage template = ImageIO.read(FullScreenWindow.class.getResource("tab_buttons_1.png"));
		int h = template.getHeight() / 4;
		mTabImages = new NinePatchImage[]
		{
			new NinePatchImage(template.getSubimage(0, h * 0, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 1, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 2, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 3, template.getWidth(), h))
		};

		template = ImageIO.read(FullScreenWindow.class.getResource("menu_buttons_1.png"));
		h = template.getHeight() / 4;
		mMenuImages = new NinePatchImage[]
		{
			new NinePatchImage(template.getSubimage(0, h * 0, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 1, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 2, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h * 3, template.getWidth(), h))
		};

		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 4;

		mBorderSize = 6;
	}


	@Override
	protected void paintTitleText(Graphics2D aGraphics, FullScreenWindow aWindow, int aX, int aY, int aWidth, int aHeight, Point aPointer)
	{
		Graphics2D g = (Graphics2D)aGraphics.create(aX, aY, aWidth, aHeight);
		TextBox.enableAntialiasing(g);

		int index = 0;
		for (WindowMenuItem item : mMenuBar.getItems())
		{
			boolean selectedItem = index == mMenuBar.getSelectedIndex();
			boolean armedItem = item.getBounds().contains(aPointer);

			int mi = armedItem ? selectedItem ? 3 : 2 : selectedItem ? 1 : 0;

			new TextBox(item.getLabel())
				.setBounds(item.getBounds())
				.setShadow(new Color(0, 0, 0), 1, 1)
				.setForeground(selectedItem ? armedItem ? new Color(255, 255, 255) : new Color(238, 238, 238) : armedItem ? new Color(255, 255, 255) : new Color(201, 201, 201))
				.setBackground(mMenuImages[mi])
				.setPadding((mTitleBarHeight - item.getBounds().height) / 2, 0, 0, 0)
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.CENTER)
				.setMaxLineCount(1)
				.render(g);

			index++;
		}

		index = 0;
		for (WindowTabItem item : mTabBar.getItems())
		{
			boolean selectedTab = index == mTabBar.getSelectedIndex();
			boolean armedTab = item.getBounds().contains(aPointer);

			int mi = armedTab ? selectedTab ? 3 : 2 : selectedTab ? 1 : 0;

			new TextBox(item.getLabel())
				.setBounds(item.getBounds())
				.setShadow(new Color(43, 43, 43), 1, 1)
				.setForeground(selectedTab ? armedTab ? new Color(255, 255, 255) : new Color(238, 238, 238) : armedTab ? new Color(170, 170, 170) : new Color(147, 147, 147))
				.setBackground(mTabImages[mi])
				.setPadding(0, 0, 4, 0)
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.SOUTH)
				.setMaxLineCount(1)
				.render(g);

			index++;
		}
	}


	protected void layoutButtons(int aX, int aY, int aWidth, int aHeight)
	{
		aX += mBorderSize;

		for (WindowMenuItem item : mMenuBar.getItems())
		{
			Rectangle r = new TextBox(item.getLabel())
				.setBounds(aX, aY, aWidth, mTitleBarHeight - aY)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mMenuImages[0])
				.setFont(mTitleBarFont)
				.setMaxLineCount(1)
				.measure();

			item.setBounds(r.x, aY + mBorderSize, r.width, mTitleBarHeight - aY - mBorderSize - mBorderSize);

			aX += item.getBounds().width + 1;
		}

		aX += 20;

		for (WindowTabItem item : mTabBar.getItems())
		{
			Rectangle r = new TextBox(item.getLabel())
				.setBounds(aX, aY, aWidth, mTitleBarHeight - aY)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mTabImages[0])
				.setFont(mTitleBarFont)
				.setMaxLineCount(1)
				.measure();

			item.setBounds(r.x, mTitleBarHeight - r.height - 4, r.width, r.height + 4);

			aX += item.getBounds().width + 5;
		}
	}
}

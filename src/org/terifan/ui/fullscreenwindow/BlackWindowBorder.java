package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.terifan.ui.Anchor;
import org.terifan.ui.ColorSet;
import static org.terifan.ui.ColorSet.FOCUSED;
import static org.terifan.ui.ColorSet.UNFOCUSED;
import org.terifan.ui.NinePatchImage;
import org.terifan.ui.TextBox;


public class BlackWindowBorder extends DefaultWindowBorder
{
	protected NinePatchImage[] mTabsImages;
	protected NinePatchImage[] mMenuImages;

	protected ArrayList<Element> mOptions = new ArrayList<>(Arrays.asList(new Element("File"), new Element("Edit"), new Element("Window")));
	protected ArrayList<Element> mTabs = new ArrayList<>(Arrays.asList(new Element("Modeling"), new Element("Character", true), new Element("Layout"), new Element("Rendering")));


	public BlackWindowBorder() throws IOException
	{
		super();
	}


	@Override
	protected void paintBorder(FullScreenWindow aWindow, Graphics2D aGraphics, int aX, int aY, int aWidth, int aHeight, WindowButtonType aHoverButton, WindowButtonType aArmedButton, Point aPointer)
	{
		setup(aX, aY, aWidth, aHeight);

		super.paintBorder(aWindow, aGraphics, aX, aY, aWidth, aHeight, aHoverButton, aArmedButton, aPointer);
	}


	@Override
	protected void setupStyle() throws IOException
	{
		mTitleBarFont = new Font("segoe ui", Font.PLAIN, 13);

		mTitleBarBackground = new ColorSet()
			.add(new Color(35,35,35), UNFOCUSED)
			.add(new Color(35,35,35), FOCUSED);
		mTitleBarForeground = new ColorSet()
			.add(new Color(255,255,255), UNFOCUSED)
			.add(new Color(255,255,255), FOCUSED);
		mBorderInner = new ColorSet()
			.add(new Color(35,35,35), UNFOCUSED)
			.add(new Color(35,35,35), FOCUSED);
		mBorderOuter = new ColorSet()
			.add(new Color(35,35,35), UNFOCUSED)
			.add(new Color(35,35,35), FOCUSED);

		mButtonTemplateImage = ImageIO.read(FullScreenWindow.class.getResource("window_buttons_3.png"));

		BufferedImage template = ImageIO.read(FullScreenWindow.class.getResource("tab_buttons_1.png"));
		int h = template.getHeight() / 4;
		mTabsImages = new NinePatchImage[]
		{
			new NinePatchImage(template.getSubimage(0, h*0, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*1, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*2, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*3, template.getWidth(), h))
		};

		template = ImageIO.read(FullScreenWindow.class.getResource("menu_buttons_1.png"));
		h = template.getHeight() / 4;
		mMenuImages = new NinePatchImage[]
		{
			new NinePatchImage(template.getSubimage(0, h*0, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*1, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*2, template.getWidth(), h)),
			new NinePatchImage(template.getSubimage(0, h*3, template.getWidth(), h))
		};

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

		for (Element el : mOptions)
		{
			TextBox tb = new TextBox(el.getLabel())
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			int z = 0;
			if (el.contains(aPointer))
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

		for (Element el : mTabs)
		{
			TextBox tb = new TextBox(el.getLabel())
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			int z = 0;
			if (el.contains(aPointer))
			{
				z = el.isSelected() ? 3 : 2;
			}
			else
			{
				z = el.isSelected() ? 1 : 0;
			}
			mTabsImages[z].paintImage(aGraphics, aX, aY, m, aHeight);

			tb.setPadding(0, 10, 0, 0).render(aGraphics);

			aX += m + 5;
		}
	}


	protected void setup(int aX, int aY, int aWidth, int aHeight)
	{
		aX += mBorderSize;
		aWidth -= mBorderSize;

		for (Element el : mOptions)
		{
			TextBox tb = new TextBox(el.getLabel())
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			el.setBounds(aX, aY + 6, m, aHeight - 10);

			aX += m;
		}

		aX += 20;
		aY += 10;
		aHeight -= 10;

		for (Element el : mTabs)
		{
			TextBox tb = new TextBox(el.getLabel())
				.setBounds(aX, aY + 1, aWidth, aHeight - 1)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.WEST)
				.setMaxLineCount(1);

			int m = tb.measure().width + 20;

			el.setBounds(aX, aY, m, aHeight);

			aX += m + 5;
		}
	}


	@Override
	protected BorderIntersectionType intersectBorder(FullScreenWindow aWindow, Point aPoint)
	{
		if (aPoint.x >= mBorderSize && aPoint.x < mButtonBounds.x && aPoint.y >= mBorderSize && aPoint.y < mTitleBarHeight)
		{
			for (Element el : mOptions)
			{
				if (el.contains(aPoint))
				{
					return BorderIntersectionType.NONE;
				}
			}

			for (Element el : mTabs)
			{
				if (el.contains(aPoint))
				{
					return BorderIntersectionType.NONE;
				}
			}

			return BorderIntersectionType.MOVE;
		}

		return super.intersectBorder(aWindow, aPoint);
	}


	private static class Element extends Rectangle
	{
		private String mLabel;
		private boolean mSelected;


		public Element(String aLabel)
		{
			mLabel = aLabel;
		}


		public Element(String aLabel, boolean aSelected)
		{
			mLabel = aLabel;
			mSelected = aSelected;
		}


		private String getLabel()
		{
			return mLabel;
		}


		public boolean isSelected()
		{
			return mSelected;
		}
	}
}

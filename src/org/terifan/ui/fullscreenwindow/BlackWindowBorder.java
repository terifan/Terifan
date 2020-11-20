package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
	protected ArrayList<Element> mTabs = new ArrayList<>(Arrays.asList(new Element("Modeling"), new Element("Character"), new Element("Layout"), new Element("Rendering")));
	protected Element mSelectedTab = mTabs.get(1);


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
		TextBox.enableAntialiasing(aGraphics);

		for (Element el : mOptions)
		{
			int mi = el.contains(aPointer) ? 2 : 0;

			new TextBox(el.getLabel())
				.setBounds(el)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mMenuImages[mi])
				.setPadding(2, 0, 0, 0)
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.CENTER)
				.setMaxLineCount(1)
				.render(aGraphics);
		}

		for (Element el : mTabs)
		{
			int mi = el.contains(aPointer) ? mSelectedTab == el ? 3 : 2 : mSelectedTab == el ? 1 : 0;

			new TextBox(el.getLabel())
				.setBounds(el)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mTabsImages[mi])
//				.setPadding(0, 20, 4, 40)
				.setPadding(0, 0, 4, 0)
				.setFont(mTitleBarFont)
				.setAnchor(Anchor.SOUTH)
				.setMaxLineCount(1)
				.render(aGraphics);

//			aGraphics.setColor(Color.YELLOW);
//			aGraphics.drawRect(el.x+2, el.y+2, 16, 16);
//			aGraphics.drawRect(el.x+el.width-2-20-20, el.y+2, 16, 16);
//			aGraphics.drawRect(el.x+el.width-2-20, el.y+2, 16, 16);

//try
//{
//				int x = el.x+el.width-2-20;
//				int y = el.y+2;
//			aGraphics.drawImage(ImageIO.read(BlackWindowBorder.class.getResource("tab_inner_buttons_1.png")), x, y, x+16,y+16,0,0,16,16, null);
//}
//catch (Exception e)
//{
//	e.printStackTrace(System.out);
//}
		}
	}


	protected void setup(int aX, int aY, int aWidth, int aHeight)
	{
		aX += mBorderSize;

		for (Element el : mOptions)
		{
			Rectangle r = new TextBox(el.getLabel())
				.setBounds(aX, aY, aWidth, mTitleBarHeight - aY)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mMenuImages[0])
				.setFont(mTitleBarFont)
				.setMaxLineCount(1)
				.measure();

			el.setBounds(r.x, aY + mBorderSize, r.width, mTitleBarHeight - aY - mBorderSize - mBorderSize);

			aX += el.width;
		}

		aX += 20;

		for (Element el : mTabs)
		{
			Rectangle r = new TextBox(el.getLabel())
				.setBounds(aX, aY, aWidth, mTitleBarHeight - aY)
				.setForeground(mTitleBarForeground.get(mWindowFocused))
				.setBackground(mTabsImages[0])
				.setFont(mTitleBarFont)
				.setMaxLineCount(1)
				.measure();

//			el.setBounds(r.x, mTitleBarHeight - r.height, r.width + 60, r.height);
			el.setBounds(r.x, mTitleBarHeight - r.height, r.width, r.height);

			aX += el.width + 5;
		}
	}


	@Override
	protected BorderIntersectionType intersectBorder(FullScreenWindow aWindow, Point aPoint)
	{
		if (aPoint.x >= mBorderSize && aPoint.x < mButtonBounds.x && aPoint.y < mTitleBarHeight)
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


	protected static class Element extends Rectangle
	{
		private String mLabel;
		private ArrayList<Element> mChildren;


		public Element(String aLabel)
		{
			mLabel = aLabel;
		}


		public ArrayList<Element> getChildren()
		{
			return mChildren;
		}


		private String getLabel()
		{
			return mLabel;
		}
	}
}

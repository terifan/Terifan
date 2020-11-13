package org.terifan.ui.fullscreenwindow;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.terifan.ui.ColorSet;
import static org.terifan.ui.ColorSet.FOCUSED;
import static org.terifan.ui.ColorSet.UNFOCUSED;


public class GreenWindowBorder extends DefaultWindowBorder
{
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

		mButtonWidth = mButtonTemplateImage.getWidth() / 4;
		mButtonHeight = mButtonTemplateImage.getHeight() / 4;

		mBorderSize = 6;
	}
}

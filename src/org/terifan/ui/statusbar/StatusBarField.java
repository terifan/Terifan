package org.terifan.ui.statusbar;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.terifan.ui.NinePatchImage;


public class StatusBarField extends JLabel
{
	private static final long serialVersionUID = 1L;

	public final static Border LOWERED = BorderFactory.createLoweredBevelBorder();
	public final static Border RAISED = BorderFactory.createRaisedBevelBorder();
	public final static Border NONE = null;

	public final static int CONTENT = 0;
	public final static int SPRING = -1;

	private int mAutoSize;
	private NinePatchImage mBackgroundImage;


	public StatusBarField(String aText)
	{
		this(aText, SwingConstants.LEFT, CONTENT);
	}


	/**
	 * Create a StatusBar field
	 *
     * @param aText
	 *   The text to be displayed by the label.
     * @param aHorizontalAlignment
	 *   One of the following constants defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
	 * @param aSize
	 *   The size of the field in pixels or constants StatusBarField.SPRING or StatusBarField.CONTENT
	 */
	public StatusBarField(String aText, int aHorizontalAlignment, int aSize)
	{
		super(aText, aHorizontalAlignment);

		mAutoSize = aSize;

		super.setOpaque(true);
		super.setBorder(LOWERED);
	}


	public int getAutoSize()
	{
		return mAutoSize;
	}


	public StatusBarField setAutoSize(int aFixedSize)
	{
		mAutoSize = aFixedSize;
		return this;
	}


	public StatusBarField setBorderStyle(Border aBorder)
	{
		super.setBorder(aBorder);
		return this;
	}


	public NinePatchImage getBackgroundImage()
	{
		return mBackgroundImage;
	}


	public StatusBarField setBackgroundImage(NinePatchImage aBackgroundImage)
	{
		mBackgroundImage = aBackgroundImage;
		super.setOpaque(mBackgroundImage != null);
		return this;
	}


	public StatusBarField setTextColor(Color aColor)
	{
		super.setForeground(aColor);
		return this;
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		if (mBackgroundImage != null)
		{
			mBackgroundImage.paintImage(aGraphics, 0, 0, getWidth(), getHeight());
		}

		super.paintComponent(aGraphics);
	}
}

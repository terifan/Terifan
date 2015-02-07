package org.terifan.ui.statusbar;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.terifan.ui.NinePatchImage;


public class StatusBarField extends JLabel
{
	public final static int CONTENT = 0;
	public final static int SPRING = -1;

	public final static int LOWERED = 0;
	public final static int RAISED = 1;
	public final static int NONE = 2;

	private int mAutoSize;
	private int mBorderStyle;
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

		setForeground(Color.WHITE);
		setFont(new Font("arial", Font.PLAIN, 11));

		mAutoSize = aSize;
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


	public int getBorderStyle()
	{
		return mBorderStyle;
	}


	public StatusBarField setBorderStyle(int aBorder)
	{
		mBorderStyle = aBorder;
		return this;
	}


	public NinePatchImage getBackgroundImage()
	{
		return mBackgroundImage;
	}


	public StatusBarField setBackgroundImage(NinePatchImage aBackgroundImage)
	{
		mBackgroundImage = aBackgroundImage;
		return this;
	}


	public StatusBarField setTextColor(Color aColor)
	{
		super.setForeground(aColor);
		return this;
	}
}

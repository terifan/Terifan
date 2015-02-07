package org.terifan.ui.statusbar;

import java.awt.Graphics;
import javax.swing.SwingConstants;



public class StatusBarSeparator extends StatusBarField
{
	public StatusBarSeparator()
	{
		this(LOWERED);
	}

	
	public StatusBarSeparator(int aStyle)
	{
		super("", SwingConstants.LEFT, 2);
		
		setBorderStyle(aStyle);
	}


	@Override
	protected void paintComponent(Graphics g)
	{
	}
}

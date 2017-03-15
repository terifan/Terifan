package org.terifan.ui;

import java.awt.Color;
import java.util.HashMap;


public class ColorSet
{
	private HashMap<Integer, Color> mColors = new HashMap<>();
	
	public final static int DEFAULT = 0;
	public final static int FOCUSED = 1;
	public final static int ARMED = 2;
	public final static int PRESSED = 4;


	public ColorSet()
	{
	}


	public ColorSet add(Color aColor, int... aId)
	{
		for (int i : aId)
		{
			mColors.put(i, aColor);
		}
		return this;
	}


	public ColorSet add(boolean aFocused, Color aColor)
	{
		mColors.put(id(aFocused, false, false), aColor);
		return this;
	}


	public ColorSet add(boolean aFocused, boolean aArmed, Color aColor)
	{
		mColors.put(id(aFocused, aArmed, false), aColor);
		return this;
	}


	public ColorSet add(boolean aFocused, boolean aArmed, boolean aPressed, Color aColor)
	{
		mColors.put(id(aFocused, aArmed, aPressed), aColor);
		return this;
	}


	public Color get(int aId)
	{
		return get((aId & FOCUSED) != 0, (aId & ARMED) != 0, (aId & PRESSED) != 0);
	}


	public Color get(boolean aFocused)
	{
		return get(aFocused, false, false);
	}


	public Color get(boolean aFocused, boolean aArmed)
	{
		return get(aFocused, aArmed, false);
	}


	public Color get(boolean aFocused, boolean aArmed, boolean aPressed)
	{
		Color c = mColors.get(id(aFocused, aArmed, aPressed));
		if (c == null)
		{
			c = mColors.get(id(aFocused, aArmed, false));
		}
		if (c == null)
		{
			c = mColors.get(id(aFocused, false, false));
		}
		if (c == null)
		{
			c = mColors.get(id(false, false, false));
		}
		if (c == null)
		{
			c = Color.RED;
		}
		return c;
	}


	protected int id(boolean aFocused, boolean aArmed, boolean aPressed)
	{
		return (aFocused ? FOCUSED : 0) + (aArmed ? ARMED : 0) + (aPressed ? PRESSED : 0);
	}
}

package org.terifan.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;


public class StyleSet
{
	private HashMap<String, Color> mColors = new HashMap<>();
	private HashMap<String, Font> mFonts = new HashMap<>();
	private HashMap<String, Insets> mInsets = new HashMap<>();
	private HashMap<String, Integer> mIntegers = new HashMap<>();
	private HashMap<String, Image> mImages = new HashMap<>();
	private HashMap<String, Dimension> mDimensions = new HashMap<>();


	public static class State
	{
		public boolean armed;
		public boolean hover;
		public boolean selected;
		public boolean focused;
		public boolean unfocusedWindow;
	}


	public StyleSet()
	{
	}


	public StyleSet set(String aFilter, Color aColor)
	{
		put(mColors, aFilter, aColor);
		return this;
	}


	public StyleSet set(String aFilter, Font aFont)
	{
		put(mFonts, aFilter, aFont);
		return this;
	}


	public StyleSet set(String aFilter, int aInteger)
	{
		put(mIntegers, aFilter, aInteger);
		return this;
	}


	public StyleSet set(String aFilter, Insets aInsets)
	{
		put(mInsets, aFilter, aInsets);
		return this;
	}


	public StyleSet set(String aFilter, Image aImage)
	{
		put(mImages, aFilter, aImage);
		return this;
	}


	public StyleSet set(String aFilter, Dimension aDimension)
	{
		put(mDimensions, aFilter, aDimension);
		return this;
	}


	public int getInt(String aName)
	{
		return mIntegers.get(find(mIntegers, aName, false, false, false, false));
	}


	public Dimension getDimension(String aName)
	{
		return mDimensions.get(find(mDimensions, aName, false, false, false, false));
	}


	public Insets getInsets(String aName)
	{
		return mInsets.get(find(mInsets, aName, false, false, false, false));
	}


	public Font getFont(String aName)
	{
		return mFonts.get(find(mFonts, aName, false, false, false, false));
	}


	public Image getImage(String aName)
	{
		return mImages.get(find(mImages, aName, false, false, false, false));
	}


	public Color getColor(String aName, State aState)
	{
		return mColors.get(find(mColors, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	public int getInt(String aName, State aState)
	{
		return mIntegers.get(find(mIntegers, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	public Dimension getDimension(String aName, State aState)
	{
		return mDimensions.get(find(mDimensions, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	public Insets getInsets(String aName, State aState)
	{
		return mInsets.get(find(mInsets, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	public Font getFont(String aName, State aState)
	{
		return mFonts.get(find(mFonts, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	public Image getImage(String aName, State aState)
	{
		return mImages.get(find(mImages, aName, aState.armed, aState.hover, aState.selected, aState.unfocusedWindow));
	}


	private String find(Map<String, ?> aMap, String aKey, boolean aArmed, boolean aHover, boolean aSelected, boolean aUnfocusedWindow)
	{
		String type = toTypeString(aSelected, aArmed, aHover, aUnfocusedWindow);

		String bestKey = null;
		String bestMatches = "";

		for (String key : aMap.keySet())
		{
			String[] args = key.split(":");
			if (aKey.matches(args[0]))
			{
				if (args.length == 1)
				{
					if (bestMatches.isEmpty())
					{
						bestKey = key;
					}
				}
				else
				{
					for (String m : args[1].split("\\|"))
					{
						if (type.equals(toTypeString(m.contains("s"), m.contains("a"), m.contains("h"), m.contains("u"))))
						{
							bestKey = key;
							bestMatches = m;
						}
					}
				}
			}
		}

		return bestKey;
	}


	public String toTypeString(boolean aSelected, boolean aArmed, boolean aHover, boolean aUnfocusedWindow)
	{
		return (aSelected ? "s" : "-") + (aArmed ? "a" : "-") + (aHover ? "h" : "-") + (aUnfocusedWindow ? "u" : "-");
	}


	private <T> void put(HashMap<String, T> aMap, String aFilter, T aValue)
	{
		String[] args = aFilter.split(":");

		for (String key : args[0].split("\\|"))
		{
			if (args.length > 1)
			{
				key += ":" + args[1];
			}
			if (aValue == null)
			{
				aMap.remove(key);
			}
			else
			{
				aMap.put(key, aValue);
			}
		}
	}


	public static void main(String... args)
	{
		try
		{
			StyleSet styles = new StyleSet();
			styles.set("MenuForegroundShadow:uf,s;h,a;a", new Color(35, 35, 35));

			State state = new State();

//			styles.getColor("MenuForegroundShadow", state);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}

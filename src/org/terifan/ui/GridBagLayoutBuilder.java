package org.terifan.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;


/**
 * JPanel panel = new JPanel();
 * GridBagLayoutBuilder layout = new GridBagLayoutBuilder(panel);
 * 
 */
public class GridBagLayoutBuilder
{
	private HashMap<Integer, GridBagConstraints> mCache;
	private Container mParent;
	private GridBagLayout mLayout;
	private GridBagConstraints mConstraints;
	private GridBagConstraints mDefaultConstraints;
	private Font mFont;
	private Character mMnemonic;


	public GridBagLayoutBuilder(Container aParent)
	{
		mCache = new HashMap<>();
		mParent = aParent;
		mLayout = new GridBagLayout();
		mParent.setLayout(mLayout);
		mConstraints = new GridBagConstraints();
		mDefaultConstraints = new GridBagConstraints();
	}


	public void setDefault()
	{
		mDefaultConstraints = (GridBagConstraints)mConstraints.clone();
	}


	public void add(Component aComponent)
	{
		if (mFont != null)
		{
			aComponent.setFont(mFont);
			if (aComponent instanceof JScrollPane)
			{
				((JScrollPane)aComponent).getViewport().getView().setFont(mFont);
			}
		}
		if (mMnemonic != null && (aComponent instanceof AbstractButton))
		{
			((AbstractButton)aComponent).setMnemonic(mMnemonic);
		}
		if (mMnemonic != null && (aComponent instanceof JLabel))
		{
			((JLabel)aComponent).setDisplayedMnemonic(mMnemonic);
		}

		mLayout.setConstraints(aComponent, mConstraints);
		mParent.add(aComponent);
		mConstraints = (GridBagConstraints)mDefaultConstraints.clone();
	}


	public GridBagLayoutBuilder load(int v)
	{
		mConstraints = (GridBagConstraints)mCache.get(v).clone();
		return this;
	}


	public void save(int v)
	{
		mCache.put(v, (GridBagConstraints)mConstraints.clone());

		mConstraints = (GridBagConstraints)mDefaultConstraints.clone();
	}


	public GridBagLayoutBuilder font(Font v)
	{
		mFont = v;
		return this;
	}


	public GridBagLayoutBuilder mnemonic(Character v)
	{
		mMnemonic = v;
		return this;
	}


	public GridBagLayoutBuilder gridx(int v)
	{
		mConstraints.gridx = v;
		return this;
	}


	public GridBagLayoutBuilder gridy(int v)
	{
		mConstraints.gridy = v;
		return this;
	}


	public GridBagLayoutBuilder remainx()
	{
		mConstraints.gridwidth = GridBagConstraints.REMAINDER;
		return this;
	}


	public GridBagLayoutBuilder remainy()
	{
		mConstraints.gridheight = GridBagConstraints.REMAINDER;
		return this;
	}


	public GridBagLayoutBuilder relativex()
	{
		mConstraints.gridwidth = GridBagConstraints.RELATIVE;
		return this;
	}


	public GridBagLayoutBuilder relativey()
	{
		mConstraints.gridheight = GridBagConstraints.RELATIVE;
		return this;
	}


	public GridBagLayoutBuilder padx(int v)
	{
		mConstraints.ipadx = v;
		return this;
	}


	public GridBagLayoutBuilder pady(int v)
	{
		mConstraints.ipady = v;
		return this;
	}


	public GridBagLayoutBuilder gridw(int v)
	{
		mConstraints.gridwidth = v;
		return this;
	}


	public GridBagLayoutBuilder gridh(int v)
	{
		mConstraints.gridheight = v;
		return this;
	}


	public GridBagLayoutBuilder weightx(double v)
	{
		mConstraints.weightx = v;
		return this;
	}


	public GridBagLayoutBuilder weighty(double v)
	{
		mConstraints.weighty = v;
		return this;
	}


	public GridBagLayoutBuilder north()
	{
		mConstraints.anchor = GridBagConstraints.NORTH;
		return this;
	}


	public GridBagLayoutBuilder south()
	{
		mConstraints.anchor = GridBagConstraints.SOUTH;
		return this;
	}


	public GridBagLayoutBuilder west()
	{
		mConstraints.anchor = GridBagConstraints.WEST;
		return this;
	}


	public GridBagLayoutBuilder east()
	{
		mConstraints.anchor = GridBagConstraints.EAST;
		return this;
	}


	public GridBagLayoutBuilder northeast()
	{
		mConstraints.anchor = GridBagConstraints.NORTHEAST;
		return this;
	}


	public GridBagLayoutBuilder northwest()
	{
		mConstraints.anchor = GridBagConstraints.NORTHWEST;
		return this;
	}


	public GridBagLayoutBuilder southeast()
	{
		mConstraints.anchor = GridBagConstraints.SOUTHEAST;
		return this;
	}


	public GridBagLayoutBuilder southwest()
	{
		mConstraints.anchor = GridBagConstraints.SOUTHWEST;
		return this;
	}


	public GridBagLayoutBuilder center()
	{
		mConstraints.anchor = GridBagConstraints.CENTER;
		return this;
	}


	public GridBagLayoutBuilder fillx()
	{
		mConstraints.fill = GridBagConstraints.HORIZONTAL;
		return this;
	}


	public GridBagLayoutBuilder filly()
	{
		mConstraints.fill = GridBagConstraints.VERTICAL;
		return this;
	}


	public GridBagLayoutBuilder fillboth()
	{
		mConstraints.fill = GridBagConstraints.BOTH;
		return this;
	}


	public GridBagLayoutBuilder fillnone()
	{
		mConstraints.fill = GridBagConstraints.NONE;
		return this;
	}


	public GridBagLayoutBuilder insets(int top, int left, int bottom, int right)
	{
		mConstraints.insets.top = top;
		mConstraints.insets.left = left;
		mConstraints.insets.bottom = bottom;
		mConstraints.insets.right = right;
		return this;
	}
}

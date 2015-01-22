package org.terifan.ui.listview;

import org.terifan.forms.Icon;


public interface ListViewItem
{
	public Object getValue(int aIndex);

	public void setValue(int aIndex, Object aValue);

	public Icon getIcon(int aIndex);

	public void setIcon(int aIndex, Icon aIcon);

	public Object getRenderingHint(Object aKey);

	@Override
	public boolean equals(Object aObject);

	@Override
	public int hashCode();

	public void loadState();
}
package org.terifan.ui;

import javax.swing.JPopupMenu;


public interface PopupFactory<T>
{
	public JPopupMenu createPopup(T aOwner);
}

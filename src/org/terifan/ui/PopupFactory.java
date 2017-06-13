package org.terifan.ui;

import java.awt.Point;
import javax.swing.JPopupMenu;


public interface PopupFactory<T>
{
	public JPopupMenu createPopup(T aOwner, Point aPoint);
}

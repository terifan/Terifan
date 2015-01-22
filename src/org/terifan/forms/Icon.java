package org.terifan.forms;

import java.awt.Component;
import java.awt.Graphics;


public interface Icon extends javax.swing.Icon
{
	public void paintIcon(Component aComponent, Graphics aGraphics, int aOriginX, int aOriginY, int aWidth, int aHeight);
}

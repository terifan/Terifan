package org.terifan.ui.fullscreenwindow;


@FunctionalInterface
public interface WindowMenuSelectionHandler
{
	/**
	 * Handle the selection of a menu.
	 *
	 * @param aMenu
	 *   selected menu
	 * @return
	 *   the menu items to display
	 */
	boolean menuSelected(WindowMenuItem aMenu);
}

package org.terifan.ui.fullscreenwindow;


@FunctionalInterface
public interface TabSelectedHandler
{
	/**
	 * Windows signals that a tab has been selected. The implementation can veto the selection by returning false.
	 *
	 * @param aTab
	 *   selected tab
	 * @return
	 *   true if the tab should be selected, otherwise false
	 */
	boolean tabSelected(WindowTabItem aTab);
}

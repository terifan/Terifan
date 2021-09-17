package org.terifan.ui.fullscreenwindow;


@FunctionalInterface
public interface WindowTabSelectionHandler
{
	/**
	 * Handle the selection of a tab. The implementation can veto the selection by returning false.
	 *
	 * @param aTab
	 *   selected tab
	 * @return
	 *   true if the tab should be selected, otherwise false
	 */
	boolean tabSelected(WindowTabItem aTab);
}

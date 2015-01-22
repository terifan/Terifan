package org.terifan.ui.listview;


public interface ListViewListener
{
	public void selectionChanged(ListViewEvent aEvent);

	public void selectionAction(ListViewEvent aEvent);

	public void sortedColumnWillChange(ListViewEvent aEvent);

	public void sortedColumnChanged(ListViewEvent aEvent);
}
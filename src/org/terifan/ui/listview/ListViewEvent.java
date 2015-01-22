package org.terifan.ui.listview;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;


public class ListViewEvent
{
	protected ListView mListView;
	protected InputEvent mInputEvent;
	protected ListViewColumn mColumn;


	public ListViewEvent(ListView aListView, InputEvent aInputEvent)
	{
		mListView = aListView;
		mInputEvent = aInputEvent;
	}


	public ListView getListView()
	{
		return mListView;
	}


	public InputEvent getInputEvent()
	{
		return mInputEvent;
	}


	public void setListViewColumn(ListViewColumn aColumn)
	{
		mColumn = aColumn;
	}


	public ListViewColumn getListViewColumn()
	{
		return mColumn;
	}


	public MouseEvent getMouseEvent()
	{
		return (MouseEvent)mInputEvent;
	}
}
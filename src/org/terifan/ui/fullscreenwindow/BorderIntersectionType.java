package org.terifan.ui.fullscreenwindow;

import java.awt.Cursor;


public enum BorderIntersectionType
{
	WEST(Cursor.W_RESIZE_CURSOR, true),
	EAST(Cursor.E_RESIZE_CURSOR, true),
	SOUTH(Cursor.S_RESIZE_CURSOR, true),
	NORTH(Cursor.N_RESIZE_CURSOR, true),
	NORTHWEST(Cursor.NW_RESIZE_CURSOR, true),
	NORTHEAST(Cursor.NE_RESIZE_CURSOR, true),
	SOUTHWEST(Cursor.SW_RESIZE_CURSOR, true),
	SOUTHEAST(Cursor.SE_RESIZE_CURSOR, true),
	MOVE(Cursor.DEFAULT_CURSOR, false),
	NONE(Cursor.DEFAULT_CURSOR, false);

	final int CURSOR;
	final boolean RESIZE;


	private BorderIntersectionType(int aCursor, boolean aResize)
	{
		CURSOR = aCursor;
		RESIZE = aResize;
	}
}

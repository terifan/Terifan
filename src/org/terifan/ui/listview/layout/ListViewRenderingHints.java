package org.terifan.ui.listview.layout;


public class ListViewRenderingHints
{
	/**
	 * Enable/disable border rendering on the ThumbnailItemRenderer layout.
	 */
	public final static Object KEY_DRAW_BORDER = "LISTVIEW_RENDERING_HINT_DRAW_BORDER";

	/**
	 * Enable border rendering on the ThumbnailItemRenderer layout. This is 
	 * only applicable when the ListViewItem has an icon.
	 */
	public final static Object VALUE_DRAW_BORDER_ON = Boolean.TRUE;
	
	/**
	 * Disable border rendering on the ThumbnailItemRenderer layout.
	 */
	public final static Object VALUE_DRAW_BORDER_OFF = Boolean.FALSE;
}

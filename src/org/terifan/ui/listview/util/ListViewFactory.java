package org.terifan.ui.listview.util;

import org.terifan.ui.listview.*;
import java.awt.Color;
import java.awt.Dimension;
import org.terifan.ui.Orientation;
import org.terifan.ui.listview.layout.CardItemRenderer;
import org.terifan.ui.listview.layout.ColumnHeaderRenderer;
import org.terifan.ui.listview.layout.DetailItemRenderer;
import org.terifan.ui.listview.layout.TableItemRenderer;
import org.terifan.ui.listview.layout.ThumbnailItemRenderer;
import org.terifan.ui.listview.layout.TileItemRenderer;


public class ListViewFactory
{
	public enum Layout
	{
		Detail, CardHorizontal, CardVertical, Table, ThumbnailHorizontal, ThumbnailVertical, TileHorizontal, TileVertical;
	}


	public static void applyLayout(ListView aListView, Layout aLayout)
	{
		switch (aLayout)
		{
			case Detail: applyDetailLayout(aListView); break;
			case CardHorizontal: applyHorizontalCardLayout(aListView); break;
			case CardVertical: applyVerticalCardLayout(aListView); break;
			case Table: applyTableLayout(aListView); break;
			case ThumbnailHorizontal: applyHorizontalThumbnailLayout(aListView); break;
			case ThumbnailVertical: applyVerticalThumbnailLayout(aListView); break;
			case TileHorizontal: applyHorizontalTileLayout(aListView); break;
			case TileVertical: applyVerticalTileLayout(aListView); break;
		}
	}


	public static void applyVerticalCardLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new CardItemRenderer(new Dimension(200, 50), 75, Orientation.VERTICAL));
	}


	public static void applyHorizontalCardLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new CardItemRenderer(new Dimension(200, 50), 75, Orientation.HORIZONTAL));
	}


	public static void applyVerticalTileLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new TileItemRenderer(new Dimension(200, 100), 100, Orientation.VERTICAL));
	}


	public static void applyHorizontalTileLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new TileItemRenderer(new Dimension(200, 100), 100, Orientation.HORIZONTAL));
	}


	public static void applyVerticalThumbnailLayout(ListView aListView)
	{
		applyVerticalThumbnailLayout(aListView, 128, 128, ThumbnailItemRenderer.DEFAULT_LABEL_HEIGHT);
	}


	public static void applyVerticalThumbnailLayout(ListView aListView, int aItemWidth, int aItemHeight, int aLabelHeight)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new ThumbnailItemRenderer(new Dimension(aItemWidth, aItemHeight), Orientation.VERTICAL, aLabelHeight));
	}


	public static void applyHorizontalThumbnailLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(null);
		aListView.setItemRenderer(new ThumbnailItemRenderer(new Dimension(128, 128), Orientation.HORIZONTAL));
	}


	public static void applyTableLayout(ListView aListView)
	{
		aListView.setHeaderRenderer(new ColumnHeaderRenderer());
		aListView.setItemRenderer(new TableItemRenderer());
	}


	public static void applyDetailLayout(ListView aListView)
	{
		applyDetailLayout(aListView, false);
	}


	public static void applyDetailLayout(ListView aListView, boolean aExtendLastItem)
	{
		DetailItemRenderer renderer = new DetailItemRenderer();
		aListView.setBackground(Color.WHITE);
		aListView.setHeaderRenderer(new ColumnHeaderRenderer());
		aListView.setItemRenderer(renderer);
//		aListView.setRowHeaderRenderer(null);
		renderer.setExtendLastItem(aExtendLastItem);
	}
}

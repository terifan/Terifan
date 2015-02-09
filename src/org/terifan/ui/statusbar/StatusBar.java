package org.terifan.ui.statusbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import org.terifan.ui.NinePatchImage;


public class StatusBar extends JComponent implements LayoutManager, Iterable<StatusBarField>
{
	private final static Color DARKER = new Color(0,0,0,64);
	private final static Color BRIGHTER = new Color(255,255,255,64);

	private Insets mMargin;
	private Insets mPadding;
	private int mSpacing;
	private NinePatchImage mBackgroundImage;


	public StatusBar()
	{
		setLayout(this);

		mSpacing = 4;
		mMargin = new Insets(2, 5, 2, 5);
		mPadding = new Insets(2, 5, 2, 5);
	}


	public NinePatchImage getBackgroundImage()
	{
		return mBackgroundImage;
	}


	public StatusBar setBackgroundImage(NinePatchImage aBackgroundImage)
	{
		mBackgroundImage = aBackgroundImage;
		return this;
	}


	public Insets getMargin()
	{
		return mMargin;
	}


	public StatusBar setMargin(Insets aMargin)
	{
		mMargin = aMargin;
		return this;
	}


	public Insets getPadding()
	{
		return mPadding;
	}


	public StatusBar setPadding(Insets aPadding)
	{
		mPadding = aPadding;
		return this;
	}


	public int getSpacing()
	{
		return mSpacing;
	}


	public StatusBar setSpacing(int aSpacing)
	{
		mSpacing = aSpacing;
		return this;
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		mBackgroundImage.paintImage(g, 0, 0, getWidth(), getHeight());

		int n = getComponentCount();

		for (int i = 0; i < n; i++)
		{
			Component comp = getComponent(i);
			Rectangle r = comp.getBounds();

			if (comp instanceof StatusBarSeparator)
			{
				int x = r.x+r.width/2;
				paintBorder(comp, g, x, r.y-mPadding.top, 1, r.height+mPadding.top+mPadding.bottom);
			}
			else
			{
				NinePatchImage background = null;

				if (comp instanceof StatusBarField)
				{
					StatusBarField field = (StatusBarField)comp;

					background = field.getBackgroundImage();
				}

				if (background != null)
				{
					background.paintImage(g, r.x-mPadding.left, r.y-mPadding.top, r.width+mPadding.left+mPadding.right, r.height+mPadding.top+mPadding.bottom);
				}

				paintBorder(comp, g, r.x-mPadding.left, r.y-mPadding.top, r.width+mPadding.left+mPadding.right, r.height+mPadding.top+mPadding.bottom);
			}
		}
	}


	protected void paintBorder(Component comp, Graphics g, int x, int y, int w, int h)
	{
		boolean lowered = true;

		if (comp instanceof StatusBarField)
		{
			StatusBarField field = (StatusBarField)comp;
			lowered = field.getBorderStyle() == StatusBarField.LOWERED;

			if (field.getBorderStyle() == StatusBarField.NONE)
			{
				return;
			}
		}

		g.setColor(lowered ? DARKER : BRIGHTER);
		g.drawLine(x, y, x+w, y);
		g.drawLine(x, y+1, x, y+h);
		g.setColor(lowered ? BRIGHTER : DARKER);
		g.drawLine(x+w, y+1, x+w, y+h);
		g.drawLine(x+1, y+h, x+w-1, y+h);
	}


	@Override
	public void addLayoutComponent(String name, Component comp)
	{
	}


	@Override
	public void removeLayoutComponent(Component comp)
	{
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		int w = 0;
		int h = 0;
		int n = aParent.getComponentCount();

		for (int i = 0; i < n; i++)
		{
			Component comp = aParent.getComponent(i);
			Dimension d = comp.getMinimumSize();
			if (comp instanceof StatusBarField)
			{
				StatusBarField field = (StatusBarField)comp;
				if (field.getAutoSize() > 0)
				{
					w += field.getAutoSize();
				}
				else
				{
					w += d.width;
				}
			}
			else
			{
				w += d.width;
			}
			h = Math.max(h, d.height);
		}

		return new Dimension(w + (mPadding.left + mPadding.right) * n + mSpacing * (n - 1) + mMargin.left + mMargin.right, h + mMargin.top + mMargin.bottom + mPadding.top + mPadding.bottom);
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		int w = 0;
		int h = 0;
		int n = aParent.getComponentCount();

		for (int i = 0; i < n; i++)
		{
			Component comp = aParent.getComponent(i);
			Dimension d = comp.getPreferredSize();
			if (comp instanceof StatusBarField)
			{
				StatusBarField field = (StatusBarField)comp;
				if (field.getAutoSize() > 0)
				{
					w += field.getAutoSize();
				}
				else
				{
					w += d.width;
				}
			}
			else
			{
				w += d.width;
			}
			h = Math.max(h, d.height);
		}

		return new Dimension(w + (mPadding.left + mPadding.right) * n + mSpacing * (n - 1) + mMargin.left + mMargin.right, h + mMargin.top + mMargin.bottom + mPadding.top + mPadding.bottom);
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		int x = mMargin.left + mPadding.left;
		int y = mMargin.top + mPadding.top;
		int n = aParent.getComponentCount();
		int h = aParent.getHeight() - mMargin.top - mMargin.bottom - mPadding.top - mPadding.bottom;
		int extra = mPadding.left + mPadding.right;

		int parentWidth = aParent.getWidth();
		int width = preferredLayoutSize(aParent).width;

		for (int i = 0; i < n; i++)
		{
			Component comp = aParent.getComponent(i);
			int cw = comp.getPreferredSize().width + extra;
			int w = cw;

			if (comp instanceof StatusBarField)
			{
				StatusBarField field = (StatusBarField)comp;
				if (field.getAutoSize() > 0)
				{
					w = Math.max(cw, field.getAutoSize() + extra);
				}
				else if (field.getAutoSize() == StatusBarField.SPRING)
				{
					w = Math.max(cw, parentWidth - width + w);
				}
				if (width > parentWidth)
				{
					int corr = w - Math.max(cw, w - (width - parentWidth));
					w -= corr;
					width -= corr;
				}
			}

			comp.setBounds(x, y, w - extra, h);

			x += w + mSpacing;
		}
	}


	public StatusBarField getField(int aIndex)
	{
		return (StatusBarField)getComponent(aIndex);
	}


	@Override
	public Iterator<StatusBarField> iterator()
	{
		ArrayList<StatusBarField> fields = new ArrayList<StatusBarField>();
		for (int i = 0; i < getComponentCount(); i++)
		{
			Component comp = getComponent(i);
			if (comp instanceof StatusBarField)
			{
				fields.add((StatusBarField)comp);
			}
		}
		return fields.iterator();
	}


	public static void main(String ... args)
	{
		try
		{
			NinePatchImage background = new NinePatchImage(StatusBar.class.getResource("black_bar.png"));
			NinePatchImage fieldBackground = new NinePatchImage(StatusBar.class.getResource("black_field.png"));

			StatusBar sb1 = new StatusBar();
			sb1.setBackgroundImage(background);
			sb1.add(new StatusBarField("test1", SwingConstants.LEFT, 100).setBackgroundImage(fieldBackground));
			sb1.add(new StatusBarField("test2", SwingConstants.CENTER, 100).setBackgroundImage(fieldBackground));
			sb1.add(new StatusBarField("test3", SwingConstants.RIGHT, 100).setBackgroundImage(fieldBackground));
			sb1.add(new StatusBarField("test7", SwingConstants.RIGHT, StatusBarField.SPRING).setBorderStyle(StatusBarField.NONE));
			sb1.add(new StatusBarSeparator());
			sb1.add(new StatusBarField("test4").setBorderStyle(StatusBarField.NONE));
			sb1.add(new StatusBarSeparator());
			sb1.add(new StatusBarField("test5").setBorderStyle(StatusBarField.NONE));
			sb1.add(new StatusBarSeparator(StatusBarSeparator.RAISED));
			sb1.add(new StatusBarField("test6").setBorderStyle(StatusBarField.NONE));

			JFrame frame = new JFrame();
			frame.add(sb1, BorderLayout.SOUTH);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
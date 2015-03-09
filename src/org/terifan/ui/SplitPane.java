package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;


public class SplitPane extends JComponent
{
	public final static int VERTICAL_SPLIT = 1;
	public final static int HORIZONTAL_SPLIT = 2;

	public final static int NONE = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;

	private int mDividerPosition;
	private int mOrientation;
	private double mResizeWeight;
	private int mDividerSize;
	private Icon mDividerImage;
	private Color mDividerBackground;
	private int mLastLayoutSize;
	private int mFixedComponent;
	private boolean mResizing;
	private Point mClickPosition;
	private boolean mDividerBorderEnabled;


	/**
	 *
	 * @param aOrientation
	 * @param aResizeWeight
	 * @param aFixedComponent
	 *   Lock one of the components or none, use SplitPane.LEFT, SplitPane.RIGHT, SplitPane.NONE
	 * @param aLeftComponent
	 * @param aRightComponent
	 */
	public SplitPane(int aOrientation, double aResizeWeight, int aFixedComponent, Component aLeftComponent, Component aRightComponent)
	{
		Class clazz = getClass();
		while (!clazz.getName().equals("org.terifan.ui.SplitPane"))
		{
			clazz = clazz.getSuperclass();
		}

		mDividerPosition = -1;
		mResizeWeight = aResizeWeight;
		mFixedComponent = aFixedComponent;
		mOrientation = aOrientation;
		mDividerSize = 7;
		mDividerBackground = UIManager.getColor("Button.background");

		Component divider = createDivider();
		divider.addMouseListener(mMouseListener);
		divider.addMouseMotionListener(mMouseMotionListener);

		super.setLayout(mLayoutManager);
		super.add(divider);
		super.add(aLeftComponent);
		super.add(aRightComponent);
	}


	public void setDividerBackground(Color aDividerBackground)
	{
		mDividerBackground = aDividerBackground;
	}


	public boolean isDividerBorderEnabled()
	{
		return mDividerBorderEnabled;
	}


	public SplitPane setDividerBorderEnabled(boolean aDividerBorderEnabled)
	{
		mDividerBorderEnabled = aDividerBorderEnabled;

		return this;
	}


	public Color getDividerBackground()
	{
		return mDividerBackground;
	}


	public SplitPane setDividerImage(Icon aDividerImage)
	{
		mDividerImage = aDividerImage;

		return this;
	}


	public Icon getDividerImage()
	{
		return mDividerImage;
	}


	public SplitPane setDividerSize(int aDividerSize)
	{
		mDividerSize = aDividerSize;

		return this;
	}


	public int getDividerSize()
	{
		return mDividerSize;
	}


	public SplitPane setResizeWeight(double aResizeWeight)
	{
		mResizeWeight = aResizeWeight;

		return this;
	}


	public double getResizeWeight()
	{
		return mResizeWeight;
	}


	public SplitPane setOrientation(int aOrientation)
	{
		if (aOrientation != VERTICAL_SPLIT && aOrientation != HORIZONTAL_SPLIT)
		{
			throw new IllegalArgumentException("aOrientation has an illegal value: " + aOrientation);
		}

		mOrientation = aOrientation;

		return this;
	}


	public int getOrientation()
	{
		return mOrientation;
	}


	public SplitPane setLeftComponent(Component aComponent)
	{
		if (aComponent == null)
		{
			aComponent = new JLabel("<empty>");
		}

		super.remove(1);
		super.add(aComponent, 1);

		return this;
	}


	public Component getLeftComponent()
	{
		return getComponent(1);
	}


	public SplitPane setRightComponent(Component aComponent)
	{
		if (aComponent == null)
		{
			aComponent = new JLabel("<empty>");
		}

		super.remove(2);
		super.add(aComponent, 2);

		return this;
	}


	public Component getRightComponent()
	{
		return getComponent(2);
	}


	public Component getDividerComponent()
	{
		return getComponent(0);
	}


	public int getDividerPosition()
	{
		return mDividerPosition;
	}


	public SplitPane setDividerPosition(int aDividerPosition)
	{
		if (aDividerPosition < 0)
		{
			throw new IllegalArgumentException("aDividerPosition has an illegal value: " + aDividerPosition);
		}

		mDividerPosition = aDividerPosition;

		return this;
	}


	protected JComponent createDivider()
	{
		return new JComponent()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				int width = getWidth();
				int height = getHeight();

				g.setColor(mDividerBackground);
				g.fillRect(0, 0, width, height);

				if (mDividerBorderEnabled)
				{
					if (mOrientation == VERTICAL_SPLIT)
					{
						g.setColor(mDividerBackground.brighter());
						g.drawLine(0, 0, width, 0);
						g.setColor(mDividerBackground.darker());
						g.drawLine(0, height-1, width, height-1);
					}
					else
					{
						g.setColor(mDividerBackground.brighter());
						g.drawLine(0, 0, 0, height);
						g.setColor(mDividerBackground.darker());
						g.drawLine(width-1, 0, width-1, height);
					}
				}

				if (mDividerImage != null)
				{
					mDividerImage.paintIcon(this, g, (width - mDividerImage.getIconWidth()) / 2, (height - mDividerImage.getIconHeight()) / 2);
				}
			}
		};
	};


	private transient MouseListener mMouseListener = new MouseAdapter()
	{
		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mResizing = true;
			mClickPosition = aEvent.getPoint();
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			mResizing = false;
		}


		@Override
		public void mouseExited(MouseEvent aEvent)
		{
			if (!mResizing)
			{
				setCursor(Cursor.getDefaultCursor());
			}
		}


		@Override
		public void mouseEntered(MouseEvent aEvent)
		{
			if (mOrientation == VERTICAL_SPLIT)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			}
			else
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			}
		}
	};


	private transient MouseMotionListener mMouseMotionListener = new MouseMotionAdapter()
	{
		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			if (mResizing)
			{
				if (mOrientation == HORIZONTAL_SPLIT)
				{
					mDividerPosition += aEvent.getX() - mClickPosition.x;
				}
				else
				{
					mDividerPosition += aEvent.getY() - mClickPosition.y;
				}

				revalidate();
			}
		}
	};


	private transient LayoutManager mLayoutManager = new LayoutManager()
	{
		@Override
		public void addLayoutComponent(String name, Component comp)
		{
		}


		@Override
		public void removeLayoutComponent(Component comp)
		{
		}


		@Override
		public Dimension preferredLayoutSize(Container aParent)
		{
			Insets insets = getInsets();
			Dimension d1 = getLeftComponent().getPreferredSize();
			Dimension d2 = getRightComponent().getPreferredSize();

			if (mOrientation == HORIZONTAL_SPLIT)
			{
				return new Dimension(insets.left + insets.right + d1.width + mDividerSize + d2.width,
					insets.top + insets.bottom + Math.max(d1.height, d2.height));
			}
			else
			{
				return new Dimension(insets.left + insets.right + Math.max(d1.width, d2.width),
					insets.top + insets.bottom + d1.height + mDividerSize + d2.height);
			}
		}


		@Override
		public Dimension minimumLayoutSize(Container aParent)
		{
			Insets insets = getInsets();
			Dimension d1 = getLeftComponent().getMinimumSize();
			Dimension d2 = getRightComponent().getMinimumSize();

			if (mOrientation == HORIZONTAL_SPLIT)
			{
				return new Dimension(insets.left + insets.right + d1.width + mDividerSize + d2.width,
					insets.top + insets.bottom + Math.max(d1.height, d2.height));
			}
			else
			{
				return new Dimension(insets.left + insets.right + Math.max(d1.width, d2.width),
					insets.top + insets.bottom + d1.height + mDividerSize + d2.height);
			}
		}


		@Override
		public void layoutContainer(Container aParent)
		{
			Insets insets = getInsets();

			int w = aParent.getWidth() - insets.left - insets.right;
			int h = aParent.getHeight() - insets.top - insets.bottom;

			if (w <= 0 || h <= 0)
			{
				return;
			}

			Component c1 = getLeftComponent();
			Component c2 = getRightComponent();
			Component c3 = getDividerComponent();

			validateDivider(w, h);

			if (mOrientation == HORIZONTAL_SPLIT)
			{
				c1.setBounds(insets.left, insets.top, mDividerPosition, h);
				c3.setBounds(insets.left + mDividerPosition, insets.top, mDividerSize, h);
				c2.setBounds(insets.left + mDividerPosition + mDividerSize, insets.top, w - mDividerPosition - mDividerSize, h);
			}
			else
			{
				c1.setBounds(insets.left, insets.top, w, mDividerPosition);
				c3.setBounds(insets.left, insets.top + mDividerPosition, w, mDividerSize);
				c2.setBounds(insets.left, insets.top + mDividerPosition + mDividerSize, w, h - mDividerPosition - mDividerSize);
			}
		}


		private void validateDivider(int w, int h)
		{
			int newSize = mOrientation == HORIZONTAL_SPLIT ? w : h;
			int newPosition = mDividerPosition;

			if (newPosition <= -1)
			{
				newPosition = (int)(newSize * mResizeWeight);
			}
			else if (mFixedComponent == RIGHT)
			{
				newPosition += newSize - mLastLayoutSize;
			}
			else if (mFixedComponent == NONE)
			{
				newPosition += (int)((newSize-mLastLayoutSize) * mResizeWeight);
			}

			Component c1 = getLeftComponent();
			Component c2 = getRightComponent();

			int min, max;
			if (mOrientation == HORIZONTAL_SPLIT)
			{
				min = c1.getMinimumSize().width;
				max = newSize - c2.getMinimumSize().width;
			}
			else
			{
				min = c1.getMinimumSize().height;
				max = newSize - c2.getMinimumSize().height;
			}

			mLastLayoutSize = newSize;
			mDividerPosition = Math.min(Math.max(newPosition, min), max);
		}
	};
}
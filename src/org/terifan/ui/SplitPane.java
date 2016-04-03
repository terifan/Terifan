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
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;


public class SplitPane extends JComponent
{
	public enum FixedComponent
	{
		NONE,
		LEFT,
		RIGHT
	}

	private int mDividerPosition;
	private Orientation mOrientation;
	private double mResizeWeight;
	private int mDividerSize;
	private BufferedImage mDividerImage;
	private Color mDividerBackground;
	private int mLastLayoutSize;
	private FixedComponent mFixedComponent;
	private boolean mResizing;
	private Point mClickPosition;
	private boolean mDividerBorderEnabled;
	private ImageScaleFunction mDividerFunction;


	/**
	 *
	 * @param aOrientation
	 * @param aResizeWeight
	 * @param aFixedComponent
	 *   Lock one of the components or none, use SplitPane.LEFT, SplitPane.RIGHT, SplitPane.NONE
	 * @param aLeftComponent
	 * @param aRightComponent
	 */
	public SplitPane(Orientation aOrientation, double aResizeWeight, FixedComponent aFixedComponent, Component aLeftComponent, Component aRightComponent)
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
		mDividerFunction = ImageScaleFunction.RESIZE;

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


	public SplitPane setDividerImage(BufferedImage aDividerImage)
	{
		mDividerImage = aDividerImage;

		return this;
	}


	public BufferedImage getDividerImage()
	{
		return mDividerImage;
	}


	public SplitPane setDividerSize(int aDividerSize)
	{
		mDividerSize = aDividerSize;

		return this;
	}


	public ImageScaleFunction getDividerFunction()
	{
		return mDividerFunction;
	}


	public void setDividerFunction(ImageScaleFunction aDividerFunction)
	{
		mDividerFunction = aDividerFunction;
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


	public SplitPane setOrientation(Orientation aOrientation)
	{
		if (aOrientation != Orientation.VERTICAL && aOrientation != Orientation.HORIZONTAL)
		{
			throw new IllegalArgumentException("aOrientation has an illegal value: " + aOrientation);
		}

		mOrientation = aOrientation;

		return this;
	}


	public Orientation getOrientation()
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
					if (mOrientation == Orientation.VERTICAL)
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
					int dw = mDividerImage.getWidth();
					int dh = mDividerImage.getHeight();
					int cx = (width - dw) / 2;
					int cy = (height - dh) / 2;

					switch (mDividerFunction)
					{
						case RESIZE:
							if (mOrientation == Orientation.VERTICAL)
							{
								g.drawImage(mDividerImage, 0, cy, width, dh, null);
							}
							else
							{
								g.drawImage(mDividerImage, cx, 0, dw, height, null);
							}
							break;
						case CENTER:
							g.drawImage(mDividerImage, cx, cy, null);
							break;
						case REPEAT:
							if (mOrientation == Orientation.VERTICAL)
							{
								for (int x = 0; x < width; x+=dw)
								{
									g.drawImage(mDividerImage, x, cy, dw, dh, null);
								}
							}
							else
							{
								for (int y = 0; y < height; y+=dh)
								{
									g.drawImage(mDividerImage, cx, y, dw, dh, null);
								}
							}
							break;
					}
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
			if (mOrientation == Orientation.VERTICAL)
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
				if (mOrientation == Orientation.VERTICAL)
				{
					mDividerPosition += aEvent.getY() - mClickPosition.y;
				}
				else
				{
					mDividerPosition += aEvent.getX() - mClickPosition.x;
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

			if (mOrientation == Orientation.VERTICAL)
			{
				return new Dimension(insets.left + insets.right + Math.max(d1.width, d2.width),
					insets.top + insets.bottom + d1.height + mDividerSize + d2.height);
			}
			else
			{
				return new Dimension(insets.left + insets.right + d1.width + mDividerSize + d2.width,
					insets.top + insets.bottom + Math.max(d1.height, d2.height));
			}
		}


		@Override
		public Dimension minimumLayoutSize(Container aParent)
		{
			Insets insets = getInsets();
			Dimension d1 = getLeftComponent().getMinimumSize();
			Dimension d2 = getRightComponent().getMinimumSize();

			if (mOrientation == Orientation.VERTICAL)
			{
				return new Dimension(insets.left + insets.right + Math.max(d1.width, d2.width),
					insets.top + insets.bottom + d1.height + mDividerSize + d2.height);
			}
			else
			{
				return new Dimension(insets.left + insets.right + d1.width + mDividerSize + d2.width,
					insets.top + insets.bottom + Math.max(d1.height, d2.height));
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

			if (mOrientation == Orientation.VERTICAL)
			{
				c1.setBounds(insets.left, insets.top, w, mDividerPosition);
				c3.setBounds(insets.left, insets.top + mDividerPosition, w, mDividerSize);
				c2.setBounds(insets.left, insets.top + mDividerPosition + mDividerSize, w, h - mDividerPosition - mDividerSize);
			}
			else
			{
				c1.setBounds(insets.left, insets.top, mDividerPosition, h);
				c3.setBounds(insets.left + mDividerPosition, insets.top, mDividerSize, h);
				c2.setBounds(insets.left + mDividerPosition + mDividerSize, insets.top, w - mDividerPosition - mDividerSize, h);
			}
		}


		private void validateDivider(int w, int h)
		{
			int newSize = mOrientation == Orientation.VERTICAL ? h : w;
			int newPosition = mDividerPosition;

			if (newPosition <= -1)
			{
				newPosition = (int)(newSize * mResizeWeight);
			}
			else if (mFixedComponent == FixedComponent.RIGHT)
			{
				newPosition += newSize - mLastLayoutSize;
			}
			else if (mFixedComponent == FixedComponent.NONE)
			{
				newPosition += (int)((newSize-mLastLayoutSize) * mResizeWeight);
			}

			Component c1 = getLeftComponent();
			Component c2 = getRightComponent();

			int min, max;
			if (mOrientation == Orientation.VERTICAL)
			{
				min = c1.getMinimumSize().height;
				max = newSize - c2.getMinimumSize().height;
			}
			else
			{
				min = c1.getMinimumSize().width;
				max = newSize - c2.getMinimumSize().width;
			}

			mLastLayoutSize = newSize;
			mDividerPosition = Math.min(Math.max(newPosition, min), max);
		}
	};
}
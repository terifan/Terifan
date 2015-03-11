package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.terifan.util.log.Log;


public class ImagePane extends JPanel
{
	private final static double SCALE_STEP = 1.05;
	private final static double MAX_SCALE = 10.0;
	private final static double MIN_SCALE = 0.01;
	private final static int SCROLL_STEP = 20;


	public enum Action
	{
		ScaleUp,
		ScaleDown,
		ScaleInside,
		ScaleOutside,
		ScaleReset,
		ScaleFill,
		ScrollLeft,
		ScrollRight,
		ScrollUp,
		ScrollDown
	}
	private static Cursor OPEN_HAND_CURSOR;
	private static Cursor CLOSED_HAND_CURSOR;
	private static Cursor HIDDEN_CURSOR;
	private boolean mControlPressed;
	private BufferedImage mImage;
	private BufferedImage mScaledImage;
	private int mStartX, mStartY;
	private int mOffsetX, mOffsetY;
	private int mScaledOffsetX, mScaledOffsetY;
	private int mOldOffsetX, mOldOffsetY;
	private int mImageWidth, mImageHeight;
	private double mScaleValue;
	private boolean mDoScaleTouchInside;
	private boolean mDoScaleTouchOutside;
	private boolean mDoCenterOnMouse;
	private boolean mMouseButtonPressed;
	private double mScaledScale;
	private ImagePaneResampler mImageFilter;

	private BufferedImage mBackgroundImage;
	private BufferedImage mPlaceholder;
	private int mPlaceholderX, mPlaceholderY, mPlaceholderWidth, mPlaceholderHeight;


	public ImagePane()
	{
		this(null);
	}


	public ImagePane(BufferedImage aImage)
	{
		super(new BorderLayout());

		synchronized (ImagePane.class)
		{
			if (OPEN_HAND_CURSOR == null)
			{
				try
				{
					Toolkit toolkit = Toolkit.getDefaultToolkit();

					try (InputStream open = ImagePane.class.getResourceAsStream("imagepane_cursor_open.gif"))
					{
						OPEN_HAND_CURSOR = toolkit.createCustomCursor(ImageIO.read(open), new Point(16, 16), "open_hand");
					}
					try (InputStream closed = ImagePane.class.getResourceAsStream("imagepane_cursor_closed.gif"))
					{
						CLOSED_HAND_CURSOR = toolkit.createCustomCursor(ImageIO.read(closed), new Point(16, 16), "closed_hand");
					}

					HIDDEN_CURSOR = toolkit.createCustomCursor(createImage(new MemoryImageSource(32, 32, new int[1], 0, 32)), new Point(0, 0), "empty");
				}
				catch (IOException e)
				{
					e.printStackTrace(Log.out);
				}
			}
		}

		super.setFocusTraversalKeysEnabled(false);

		super.addMouseListener(new MouseListener());
		super.addMouseMotionListener(new MouseMotionListener());
		super.addComponentListener(new ComponentListener());
		super.addMouseWheelListener(new MyMouseWheelListener());

		super.getActionMap().put(Action.ScaleUp, new ScaleUpAction());
		super.getActionMap().put(Action.ScaleDown, new ScaleDownAction());
		super.getActionMap().put(Action.ScaleInside, new ScaleInsideAction());
		super.getActionMap().put(Action.ScaleOutside, new ScaleOutsideAction());
		super.getActionMap().put(Action.ScaleReset, new ScaleResetAction());
		super.getActionMap().put(Action.ScrollLeft, new ScrollLeftAction());
		super.getActionMap().put(Action.ScrollRight, new ScrollRightAction());
		super.getActionMap().put(Action.ScrollUp, new ScrollUpAction());
		super.getActionMap().put(Action.ScrollDown, new ScrollDownAction());

		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), Action.ScaleUp);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), Action.ScaleUp);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), Action.ScaleDown);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), Action.ScaleDown);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0), Action.ScaleInside);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), Action.ScaleInside);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), Action.ScaleOutside);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), Action.ScaleOutside);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), Action.ScaleReset);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), Action.ScaleReset);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), Action.ScrollLeft);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), Action.ScrollRight);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), Action.ScrollUp);
		super.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), Action.ScrollDown);

		super.setBackground(null);
		super.setForeground(Color.WHITE);
		super.setFocusable(true);
		super.setCursor(HIDDEN_CURSOR);
		super.requestFocusInWindow();

		setImage(aImage);
	}


	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((int)(mImageWidth * mScaleValue), (int)(mImageHeight * mScaleValue));
	}


	/**
	 * Sets the image and redraws the view while fading in the new image.
	 *
	 * @param aImage
	 *   the new image
	 * @param aFadeTimeMillis
	 *   time spent fading in, typically 500 ms
	 * @param aFilterImage
	 *   true if the faded image should be filtered, may cause an extra 500-1000 ms delay before image shown
	 */
	public synchronized void setImageFaded(BufferedImage aImage, int aFadeTimeMillis, boolean aFilterImage)
	{
		int w = getWidth();
		int h = getHeight();
		BufferedImage imageA = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageB = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageC = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		DataBufferInt bufA = (DataBufferInt)imageA.getRaster().getDataBuffer();
		DataBufferInt bufB = (DataBufferInt)imageB.getRaster().getDataBuffer();
		DataBufferInt bufC = (DataBufferInt)imageC.getRaster().getDataBuffer();
		int[] dataA = bufA.getData();
		int[] dataB = bufB.getData();
		int[] dataC = bufC.getData();

		Graphics2D g = imageA.createGraphics();
		paintImage(g, false, false);
		g.dispose();

		setImage(aImage);

		g = imageB.createGraphics();
		paintImage(g, false, aFilterImage);
		g.dispose();

		setImage(imageA);

		g = (Graphics2D)getGraphics();

		long t = System.currentTimeMillis();

		for (int alpha = 0; alpha < 255;)
		{
			alpha = Math.min(255, (int)(255 * ((System.currentTimeMillis() - t) / (double)aFadeTimeMillis)));

			blendBuffers(dataA, dataB, alpha, dataC);

			g.drawImage(imageC, 0, 0, null);
		}

		g.dispose();

		setImage(aImage);
		paintImage(g, !aFilterImage, aFilterImage);
	}


	private void blendBuffers(int[] aInputA, int[] aInputB, int aAlpha, int[] aOutput)
	{
		int invAlpha = 255 - aAlpha;

		for (int i = 0, j = aInputA.length; i < j; i++)
		{
			int x = aInputA[i];
			int y = aInputB[i];
			int r0 = 0xff & (x >> 16);
			int g0 = 0xff & (x >> 8);
			int b0 = 0xff & (x);
			int r1 = 0xff & (y >> 16);
			int g1 = 0xff & (y >> 8);
			int b1 = 0xff & (y);
			int r = (aAlpha * r1 + invAlpha * r0 + 1) >> 8;
			int g = (aAlpha * g1 + invAlpha * g0 + 1) >> 8;
			int b = (aAlpha * b1 + invAlpha * b0 + 1) >> 8;
			aOutput[i] = (r << 16) + (g << 8) + b;
		}
	}


	public synchronized void setImage(BufferedImage aImage)
	{
		mScaledImage = null;
		mScaledOffsetX = 0;
		mScaledOffsetY = 0;
		mImage = aImage;
		mScaleValue = 1;

//		if (mImage != null && mImage.getData().getTransferType() != DataBuffer.TYPE_INT)
//		{
//			int [] buffer = new int[mImage.getWidth() * mImage.getHeight()];
//			mImage.getRGB(0, 0, mImage.getWidth(), mImage.getHeight(), buffer, 0, mImage.getWidth());
//			BufferedImage temp = new BufferedImage(mImage.getWidth(), mImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//			temp.setRGB(0, 0, mImage.getWidth(), mImage.getHeight(), buffer, 0, mImage.getWidth());
//			mImage = temp;
//		}

		if (mImage == null)
		{
			mImageWidth = 0;
			mImageHeight = 0;
		}
		else
		{
			mImageWidth = mImage.getWidth(null);
			mImageHeight = mImage.getHeight(null);
			mDoScaleTouchInside = true;
			validateImageOffset();
		}

		// Warning!! Java 7 bug can cause Swing to dead-lock. Set cursor must be executed in swing thread!!
		SwingUtilities.invokeLater(() ->
		{
			setCursor(HIDDEN_CURSOR);
		});
	}


	public void setPlaceholder(BufferedImage aPlaceholder, int aOffsetX, int aOffsetY, int aWidth, int aHeight)
	{
		mPlaceholder = aPlaceholder;
		mPlaceholderX = aOffsetX;
		mPlaceholderY = aOffsetY;
		mPlaceholderWidth = aWidth;
		mPlaceholderHeight = aHeight;
	}


	public void setBackgroundImage(BufferedImage aBackgroundImage)
	{
		mBackgroundImage = aBackgroundImage;
	}


	public Image getBackgroundImage()
	{
		return mBackgroundImage;
	}


	public Image getImage()
	{
		return mImage;
	}


	private void updateCursor()
	{
		// Warning!! Risk of dead-lock. Set cursor must be executed in swing thread!!
		SwingUtilities.invokeLater(() ->
		{
			if ((mMouseButtonPressed || mControlPressed) && mImage != null && Math.min(getWidth() / (double) mImageWidth, getHeight() / (double) mImageHeight) < mScaleValue)
			{
				setCursor(mMouseButtonPressed ? CLOSED_HAND_CURSOR : OPEN_HAND_CURSOR);
			}
			else if (mMouseButtonPressed || mControlPressed)
			{
				setCursor(Cursor.getDefaultCursor());
			}
			else
			{
				setCursor(HIDDEN_CURSOR);
			}
		});
	}


	@Override
	protected synchronized void paintComponent(Graphics aGraphics)
	{
		paintImage((Graphics2D)aGraphics, true, false);
	}


	protected void paintImage(Graphics2D aGraphics, boolean aLazyFilterImage, boolean aDirectFilterImage)
	{
		if (mImageFilter != null && mScaleValue != mScaledScale)
		{
			mImageFilter.abort();
		}

		if (mDoScaleTouchInside)
		{
			setScale(Math.min(getWidth() / (double) mImageWidth, getHeight() / (double) mImageHeight));
			mDoScaleTouchInside = false;
			mOffsetX = 0;
			mOffsetY = 0;
		}
		if (mDoScaleTouchOutside)
		{
			setScale(Math.max(getWidth() / (double) mImageWidth, getHeight() / (double) mImageHeight));
			mDoScaleTouchOutside = false;

			if (mDoCenterOnMouse)
			{
				mDoCenterOnMouse = false;
			}
			else
			{
				mOffsetX = 0;
				mOffsetY = 0;
			}
		}

		validateImageOffset();

		if (mBackgroundImage != null)
		{
			aGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			aGraphics.drawImage(mBackgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
		else
		{
			aGraphics.setColor(getBackground());
			aGraphics.fillRect(0, 0, getWidth(), getHeight());
		}

		aGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if (mImage != null)
		{
			int w = (int) (mScaleValue * mImageWidth);
			int h = (int) (mScaleValue * mImageHeight);

			int x = (getWidth() - w) / 2 + mOffsetX;
			int y = (getHeight() - h) / 2 + mOffsetY;

			Rectangle2D screenRect = new Rectangle(x, y, w, h).createIntersection(new Rectangle(0, 0, getWidth(), getHeight()));

			int dx = Math.max(0, x);
			int dy = Math.max(0, y);
			int dw = Math.min(getWidth(), w);
			int dh = Math.min(getHeight(), h);

			int sx = x < 0 ? (int) (-x / mScaleValue) : 0;
			int sy = y < 0 ? (int) (-y / mScaleValue) : 0;
			int sw = (int)Math.round(screenRect.getWidth() / mScaleValue);
			int sh = (int)Math.round(screenRect.getHeight() / mScaleValue);

			double sc = mScaleValue;
			int ox = mOffsetX;
			int oy = mOffsetY;

			if (sc != mScaledScale || ox != mScaledOffsetX || oy != mScaledOffsetY)
			{
				mScaledImage = null;
			}

			if (mScaledImage == null && aDirectFilterImage && sc < 1)
			{
				new ImagePaneResampler(mImage, sx, sy, sw, sh, dw, dh, (e) ->
				{
					mScaledImage = e;
					mScaledScale = sc;
					mScaledOffsetX = ox;
					mScaledOffsetY = oy;
				}).run();
			}

			if (mScaledImage != null)
			{
				aGraphics.drawImage(mScaledImage, dx, dy, dw, dh, null);
			}
			else
			{
				aGraphics.drawImage(mImage, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, null);
			}

			if (mScaledImage == null && aLazyFilterImage)
			{
				if (sc < 1)
				{
					mImageFilter = new ImagePaneResampler(mImage, sx, sy, sw, sh, dw, dh, (e) ->
					{
						mScaledImage = e;
						mScaledScale = sc;
						mScaledOffsetX = ox;
						mScaledOffsetY = oy;
						repaint();
					});
					mImageFilter.start();
				}
				else
				{
					mScaledImage = null;
				}
			}
		}
		else if (mPlaceholder != null)
		{
			aGraphics.drawImage(mPlaceholder, mPlaceholderX, mPlaceholderY, mPlaceholderWidth, mPlaceholderHeight, this);
		}
	}


	private void validateImageOffset()
	{
		updateCursor();

		if (mImage != null)
		{
			int canvasWidth = getWidth();
			int canvasHeight = getHeight();
			int imageWidth = (int) (mImageWidth * mScaleValue);
			int imageHeight = (int) (mImageHeight * mScaleValue);

			if (canvasWidth >= imageWidth)
			{
				mOffsetX = 0;
			}
			else if (mOffsetX > (imageWidth - canvasWidth) / 2)
			{
				mOffsetX = (imageWidth - canvasWidth) / 2;
			}
			else if (mOffsetX < -(imageWidth - canvasWidth) / 2)
			{
				mOffsetX = -(imageWidth - canvasWidth) / 2;
			}

			if (canvasHeight >= imageHeight)
			{
				mOffsetY = 0;
			}
			else if (mOffsetY > (imageHeight - canvasHeight) / 2)
			{
				mOffsetY = (imageHeight - canvasHeight) / 2;
			}
			else if (mOffsetY < -(imageHeight - canvasHeight) / 2)
			{
				mOffsetY = -(imageHeight - canvasHeight) / 2;
			}
		}
	}


	private class ComponentListener extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent aEvent)
		{
			validateImageOffset();
		}
	}


	private class MouseListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent aEvent)
		{
			mMouseButtonPressed = true;
			mStartX = mOffsetX;
			mStartY = mOffsetY;
			mOldOffsetX = aEvent.getX();
			mOldOffsetY = aEvent.getY();
			updateCursor();
		}


		@Override
		public void mouseReleased(MouseEvent aEvent)
		{
			mMouseButtonPressed = false;
			updateCursor();
		}
	}


	private class MouseMotionListener extends MouseMotionAdapter
	{
		@Override
		public void mouseDragged(MouseEvent aEvent)
		{
			if (mImage != null)
			{
				mOffsetX = mStartX + aEvent.getX() - mOldOffsetX;
				mOffsetY = mStartY + aEvent.getY() - mOldOffsetY;

				repaint();
			}
		}
	}


	private class MyMouseWheelListener implements MouseWheelListener
	{
		@Override
		public void mouseWheelMoved(MouseWheelEvent aEvent)
		{
			if (aEvent.isControlDown() && !aEvent.isShiftDown() && !aEvent.isAltDown())
			{
				doScale(aEvent.getUnitsToScroll() < 0 ? -1 : 1, aEvent.getX(), aEvent.getY());
			}
		}
	}


	private class ScaleUpAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			doScale(1, getWidth() / 2, getHeight() / 2);
		}
	}


	private class ScaleDownAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			doScale(-1, getWidth() / 2, getHeight() / 2);
		}
	}


	private class ScaleInsideAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mDoScaleTouchInside = true;
			repaint();
		}
	}


	private class ScaleOutsideAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mDoScaleTouchOutside = true;
			repaint();
		}
	}


	private class ScaleResetAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mOffsetX = 0;
			mOffsetY = 0;
			setScale(1);
		}
	}


	private class ScrollLeftAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mOffsetX += SCROLL_STEP * Math.max(mScaleValue, 1);
			repaint();
		}
	}


	private class ScrollRightAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mOffsetX -= SCROLL_STEP * Math.max(mScaleValue, 1);
			repaint();
		}
	}


	private class ScrollUpAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mOffsetY += SCROLL_STEP * Math.max(mScaleValue, 1);
			repaint();
		}
	}


	private class ScrollDownAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			mOffsetY -= SCROLL_STEP * Math.max(mScaleValue, 1);
			repaint();
		}
	}


	public void fireAction(Action aAction)
	{
		getActionMap().get(aAction).actionPerformed(null);
	}


	public void setScale(double aScale)
	{
		mScaleValue = Math.max(Math.min(aScale, MAX_SCALE), MIN_SCALE);
		repaint();
	}


	private void doScale(int aDirection, int x, int y)
	{
		double px = getWidth() / 2.0 + mOffsetX - mScaleValue * mImageWidth / 2.0;
		double py = getHeight() / 2.0 + mOffsetY - mScaleValue * mImageHeight / 2.0;
		double pw = mScaleValue * mImageWidth;
		double ph = mScaleValue * mImageHeight;

		double mx = Math.min(Math.max(x, px), px + pw);
		double my = Math.min(Math.max(y, py), py + ph);

		int dx = (int) (mx - ImagePane.this.getWidth() / 2);
		int dy = (int) (my - ImagePane.this.getHeight() / 2);

		mOffsetX -= dx;
		mOffsetY -= dy;
		double scale = mScaleValue;

		if (aDirection > 0 && scale < MAX_SCALE)
		{
			scale *= SCALE_STEP;
			mOffsetX *= SCALE_STEP;
			mOffsetY *= SCALE_STEP;
		}
		else
		{
			if (aDirection < 0 && scale > MIN_SCALE)
			{
				scale /= SCALE_STEP;
				mOffsetX /= SCALE_STEP;
				mOffsetY /= SCALE_STEP;
			}
		}

		mOffsetX += dx;
		mOffsetY += dy;

		setScale(scale);
	}
}
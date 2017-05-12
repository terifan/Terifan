package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import static org.terifan.ui.TextRenderer.lineBreakText;


public final class Utilities
{
	protected final static FileSystemView mFileSystemView = FileSystemView.getFileSystemView();
	protected final static FontRenderContext mFontRenderContext = new FontRenderContext(new AffineTransform(), false, false);
	protected final static WeakHashMap<String,Icon> mFileIcons = new WeakHashMap<>();


	private Utilities()
	{
	}


	/**
	 * Gets the system Icon for a File.
	 */
	public static Icon getFileIcon(File aFile)
	{
		try
		{
			String name = aFile.getName();
			String extension = name.substring(name.lastIndexOf(".") + 1);

			Icon icon = mFileIcons.get(extension);

			if (icon == null)
			{
				icon = mFileSystemView.getSystemIcon(aFile);
				mFileIcons.put(extension, icon);
			}

			return icon;
		}
		catch (Throwable e)
		{
			return null;
		}
	}


	/**
	 * Adds an UndoManager to the provided text component that allow undo and
	 * redo actions using standard MS Windows key commands (ctrl+Z, ctrl+Y).
	 *
	 * @param aTextComponent
	 *   the text component
	 * @return
	 *   the UndoManager that has been added to the TextComponent
	 */
	public static UndoManager addUndoManager(JTextComponent aTextComponent)
	{
		final UndoManager undoManager = new UndoManager();

		aTextComponent.getDocument().addUndoableEditListener(undoManager);

		addKeyAction(aTextComponent, "ctrl Z", new AbstractAction("Undo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (undoManager.canUndo())
				{
					undoManager.undo();
				}
			}
		});

		addKeyAction(aTextComponent, "ctrl Y", new AbstractAction("Redo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (undoManager.canRedo())
				{
					undoManager.redo();
				}
			}
		});

		return undoManager;
	}


	/**
	 * Maps an input to execute the provided Action.
	 *
	 * @param aComponent
	 *   the target component
	 * @param aKeyStroke
	 *   the KeyStroke that should trigger the Action
	 * @param aAction
	 *   the Action to execute when the key stroke appears
	 */
	public static Action addKeyAction(JComponent aComponent, String aKeyStroke, Action aAction)
	{
		KeyStroke ks = KeyStroke.getKeyStroke(aKeyStroke);

		if (ks == null)
		{
			throw new IllegalArgumentException("Invalid KeyStroke: " + aKeyStroke);
		}

		return addKeyAction(aComponent, ks, aAction);
	}


	/**
	 * Maps an input to execute the provided Action.
	 *
	 * @param aComponent
	 *   the target component
	 * @param aKeyStroke
	 *   the KeyStroke that should trigger the Action
	 * @param aAction
	 *   the Action to execute when the key stroke appears
	 */
	public static Action addKeyAction(JComponent aComponent, KeyStroke aKeyStroke, Action aAction)
	{
		if (aComponent == null)
		{
			throw new IllegalArgumentException("Component is null.");
		}
		if (aKeyStroke == null)
		{
			throw new IllegalArgumentException("KeyStroke is null.");
		}
		if (aAction == null)
		{
			throw new IllegalArgumentException("Action is null.");
		}

		if (aAction.getValue(Action.NAME) == null || aAction.getValue(Action.NAME).equals(""))
		{
			aAction.putValue(Action.NAME, aAction.toString());
		}

		aComponent.getInputMap().put(aKeyStroke, aAction.getValue(Action.NAME));
		aComponent.getActionMap().put(aAction.getValue(Action.NAME), aAction);

		return aAction;
	}


	public static BufferedImage readImageResource(Object aObject, String aRelativePath)
	{
		try
		{
			BufferedImage image = ImageIO.read(aObject instanceof Class ? ((Class)aObject).getResource(aRelativePath) : aObject.getClass().getResource(aRelativePath));

			if (image == null)
			{
				throw new IOException("File not found");
			}

			return image;
		}
		catch (Throwable e)
		{
			throw new IllegalArgumentException("Reading the image resource caused an exception: relative-path: " + aRelativePath, e);
		}
	}


	private static ThisKeyEventDispatcher mKeyEventDispatcher;


	/**
	 * Maps an input to execute the provided Action.
	 *
	 * @param aKeyStroke
	 *   the KeyStroke that should trigger the Action
	 * @param aAction
	 *   the Action to execute when the key stroke appears
	 */
	public synchronized static Action addGlobalKeyAction(String aKeyStroke, Action aAction)
	{
		KeyStroke ks = KeyStroke.getKeyStroke(aKeyStroke);

		if (ks == null)
		{
			throw new IllegalArgumentException("Invalid KeyStroke: " + aKeyStroke);
		}

		return addGlobalKeyAction(ks, aAction);
	}


	/**
	 * Maps an input to execute the provided Action.
	 *
	 * @param aKeyStroke
	 *   the KeyStroke that should trigger the Action
	 * @param aAction
	 *   the Action to execute when the key stroke appears
	 */
	public synchronized static Action addGlobalKeyAction(KeyStroke aKeyStroke, Action aAction)
	{
		if (mKeyEventDispatcher == null)
		{
			mKeyEventDispatcher = new ThisKeyEventDispatcher();
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(mKeyEventDispatcher);
		}

		mKeyEventDispatcher.addAction(aKeyStroke, aAction);

		return aAction;
	}


	/**
	 * Removes a global KeyStroke mapping.
	 *
	 * @param aKeyStroke
	 *   the KeyStroke that triggered the Action
	 * @return
	 *   true if the mapping was removed
	 */
	public static boolean removeGlobalKeyAction(KeyStroke aKeyStroke)
	{
		if (mKeyEventDispatcher != null)
		{
			return mKeyEventDispatcher.removeAction(aKeyStroke);
		}

		return true;
	}


	private static class ThisKeyEventDispatcher implements KeyEventDispatcher
	{
		HashMap<KeyStroke,Action> mActions = new HashMap<>();


		public void addAction(KeyStroke aKeyStroke, Action aAction)
		{
			mActions.put(aKeyStroke, aAction);
		}


		public boolean removeAction(KeyStroke aKeyStroke)
		{
			return mActions.remove(aKeyStroke) != null;
		}


		@Override
		public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
			{
				KeyStroke ks = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
				if (mActions.containsKey(ks))
				{
					Action action = mActions.get(ks);
					action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), (String)action.getValue(Action.ACTION_COMMAND_KEY), e.getWhen(), e.getModifiers()));
					return true;
				}
			}

			return false;
		}
	}


	public static void addPopupMenuListener(Component aComponent, final JPopupMenu aMenu)
	{
		aComponent.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent aEvent)
			{
				//if (aEvent.isPopupTrigger())
				if (SwingUtilities.isRightMouseButton(aEvent))
				{
					aMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
				}
			}
		});
	}


	/**
	 * Clips the string provided to specified length.
	 */
	public static String clipString(String aString, FontMetrics aFontMetrics, int aLength)
	{
		aString = aString.trim();

		if (aString.isEmpty() || aLength == 0)
		{
			return "";
		}

		if (aFontMetrics.stringWidth(aString) < aLength)
		{
			return aString;
		}

		char[] chars = (aString + "..").toCharArray();
		int len = aString.length() + 2;

		for (;len > 0; len--)
		{
			if (len > 3)
			{
				chars[len - 3] = '.';
			}

			if (aFontMetrics.charsWidth(chars, 0, len) < aLength)
			{
				break;
			}
		}

		return new String(chars, 0, len);
	}

	public static String clipString(String aString, Font aFont, int aLength)
	{
		aString = aString.trim();

		if (aString.isEmpty() || aLength == 0)
		{
			return "";
		}

		if (aFont.getStringBounds(aString, mFontRenderContext).getWidth() < aLength)
		{
			return aString;
		}

		char[] chars = (aString + "..").toCharArray();
		int len = aString.length() + 2;

		for (;len > 0; len--)
		{
			if (len > 3)
			{
				chars[len - 3] = '.';
			}

			if (aFont.getStringBounds(chars, 0, len, mFontRenderContext).getWidth() < aLength)
			{
				break;
			}
		}

		return new String(chars, 0, len);
	}


	/**
	 * Draws a dotted rectangle.
	 */
	public static void drawDottedRect(Graphics aGraphics, int x, int y, int w, int h, boolean aAlign)
	{
		if (aAlign)
		{
			 w |= 1;
			 h |= 1;
		}

		int j = 0;
		for (int i = 0; i < w; i++, j++, x++)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		x--;
		j++;
		for (int i = 0; i < h; i++, j++, y++)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		y--;
		j++;
		for (int i = 0; i < w; i++, j++, x--)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
		x++;
		j++;
		for (int i = 0; i < h; i++, j++, y--)
		{
			if ((j & 1) == 0)
			{
				aGraphics.drawLine(x, y, x, y);
			}
		}
	}


	/**
	 * Draws a scaled image and it's frame border that is resized in only one
	 * direction. The center area of the image is resized in both directions.
	 *
	 * @param aGraphics
	 *   draws on this Graphics context
	 * @param aImage
	 *   the image to draw
	 * @param aPositionX
	 *   offset x
	 * @param aPositionY
	 *   offset y
	 * @param aWidth
	 *   width including frame width
	 * @param aHeight
	 *   height including frame height
	 * @param aFrameLeft
	 *   left frame width
	 * @param aFrameRight
	 *   right frame width
	 */
	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameLeft, int aFrameRight)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX+aFrameLeft, aPositionY+aHeight, 0, 0, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY, aPositionX+aWidth-aFrameRight, aPositionY+aHeight, aFrameLeft, 0, tw-aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY, aPositionX+aWidth, aPositionY+aHeight, tw-aFrameRight, 0, tw, th, null);
	}


	/**
	 * Draws a scaled image and it's frame border that is resized in only one
	 * direction. The center area of the image is resized in both directions.
	 *
	 * @param aGraphics
	 *   draws on this Graphics context
	 * @param aImage
	 *   the image to draw
	 * @param aPositionX
	 *   offset x
	 * @param aPositionY
	 *   offset y
	 * @param aWidth
	 *   width including frame width
	 * @param aHeight
	 *   height including frame height
	 * @param aFrameTop
	 *   top frame height
	 * @param aFrameLeft
	 *   left frame width
	 * @param aFrameBottom
	 *   bottom frame height
	 * @param aFrameRight
	 *   right frame width
	 */
	public static void drawScaledImage(Graphics aGraphics, BufferedImage aImage, int aPositionX, int aPositionY, int aWidth, int aHeight, int aFrameTop, int aFrameLeft, int aFrameBottom, int aFrameRight)
	{
		int tw = aImage.getWidth();
		int th = aImage.getHeight();

		aGraphics.drawImage(aImage, aPositionX, aPositionY, aPositionX+aFrameLeft, aPositionY+aFrameTop, 0, 0, aFrameLeft, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY, aPositionX+aWidth-aFrameRight, aPositionY+aFrameTop, aFrameLeft, 0, tw-aFrameRight, aFrameTop, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY, aPositionX+aWidth, aPositionY+aFrameTop, tw-aFrameRight, 0, tw, aFrameTop, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY+aFrameTop, aPositionX+aFrameLeft, aPositionY+aHeight-aFrameBottom, 0, aFrameTop, aFrameLeft, th-aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY+aFrameTop, aPositionX+aWidth-aFrameRight, aPositionY+aHeight-aFrameBottom, aFrameLeft, aFrameTop, tw-aFrameRight, th-aFrameBottom, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY+aFrameTop, aPositionX+aWidth, aPositionY+aHeight-aFrameBottom, tw-aFrameRight, aFrameTop, tw, th-aFrameBottom, null);

		aGraphics.drawImage(aImage, aPositionX, aPositionY+aHeight-aFrameBottom, aPositionX+aFrameLeft, aPositionY+aHeight, 0, th-aFrameBottom, aFrameLeft, th, null);
		aGraphics.drawImage(aImage, aPositionX+aFrameLeft, aPositionY+aHeight-aFrameBottom, aPositionX+aWidth-aFrameRight, aPositionY+aHeight, aFrameLeft, th-aFrameBottom, tw-aFrameRight, th, null);
		aGraphics.drawImage(aImage, aPositionX+aWidth-aFrameRight, aPositionY+aHeight-aFrameBottom, aPositionX+aWidth, aPositionY+aHeight, tw-aFrameRight, th-aFrameBottom, tw, th, null);
	}


	public static void enableTextAntialiasing(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		}
	}


	public static void enableAntialiasing(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}


	public static void enableBilinear(Graphics aGraphics)
	{
		if (aGraphics instanceof Graphics2D)
		{
			((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
	}


	public static boolean isWindowFocusOwner(Component aComponent)
	{
		while (aComponent != null && !(aComponent instanceof Window))
		{
			aComponent = aComponent.getParent();
		}
		return (aComponent instanceof Window) ? ((Window)aComponent).isFocused() : true;
	}


	public static void setSystemLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}
	}


	public static void drawString(Graphics aGraphics, String aString, Rectangle aBounds, Anchor aAnchor, Color aTextColor, Color aBackground, boolean aMultiline)
	{
		drawString(aGraphics, aString, aBounds.x, aBounds.y, aBounds.width, aBounds.height, aAnchor, aTextColor, aBackground, aMultiline);
	}


	/**
	 * Draws a multipline string within the bounds specified.
	 *
	 * @param aGraphics
	 *   draw on this context
	 * @param aString
	 *   the string to draw
	 * @param aPositionX
	 *   x offset
	 * @param aPositionY
	 *   y offset
	 * @param aWidth
	 *   width of the bounds to draw within
	 * @param aHeight
	 *   height of the bounds to draw within
	 * @param aAnchor
	 *   specifying the orientation of the text.
	 * @param aTextColor
	 *   Text color to use or null
	 * @param aBackground
	 *   Text background or null
	 */
	public static void drawString(Graphics aGraphics, String aString, int aPositionX, int aPositionY, int aWidth, int aHeight, Anchor aAnchor, Color aTextColor, Color aBackground, boolean aMultiline)
	{
		if (aString == null || aString.isEmpty())
		{
			return;
		}

		Font font = aGraphics.getFont();

		if (aTextColor == null && aBackground != null)
		{
			aTextColor = aGraphics.getColor();
		}

		ArrayList<String> list;

		if (aMultiline)
		{
			list = lineBreakText(aString, font, aWidth);
		}
		else if (aWidth > 0)
		{
			list = new ArrayList<>();
			list.add(clipString(aString, font, aWidth));
		}
		else
		{
			list = new ArrayList<>();
			list.add(aString);
		}

		int lineHeight = (int)font.getStringBounds(aString, mFontRenderContext).getHeight();

		LineMetrics lm = font.getLineMetrics("Adgj", mFontRenderContext);
		int ascent = (int)lm.getAscent();

		int lineCount;
		if (aHeight <= 0)
		{
			lineCount = list.size();
			aHeight = lineCount * lineHeight;

			if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
			{
				aPositionY -= aHeight;
			}
			if (aAnchor == Anchor.WEST || aAnchor == Anchor.CENTER || aAnchor == Anchor.EAST)
			{
				aPositionY -= aHeight/2;
			}
		}
		else
		{
			lineCount = Math.min(list.size(), aHeight / lineHeight);
		}

		if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
		{
			aPositionY += Math.max(0, aHeight-lineCount*lineHeight);
		}
		else if (aAnchor == Anchor.CENTER || aAnchor == Anchor.WEST || aAnchor == Anchor.EAST)
		{
			aPositionY += Math.max(0, (aHeight-lineCount*lineHeight)/2);
		}

		for (int i = 0; i < lineCount; i++)
		{
			String str = list.get(i);

			int x = aPositionX, w = -1;

			if (aAnchor == Anchor.NORTH || aAnchor == Anchor.CENTER || aAnchor == Anchor.SOUTH)
			{
				w = getStringLength(str, font);
				x += (aWidth-w)/2;
			}
			else if (aAnchor == Anchor.NORTH_EAST || aAnchor == Anchor.EAST || aAnchor == Anchor.SOUTH_EAST)
			{
				w = getStringLength(str, font);
				x += aWidth-w;
			}

			int y = aPositionY+i*lineHeight;

			if (aBackground != null)
			{
				if (w == -1)
				{
					w = getStringLength(str, font);
				}
				aGraphics.setColor(aBackground);
				aGraphics.fillRect(x, y, w+1, lineHeight+1);
			}
			if (aTextColor != null)
			{
				aGraphics.setColor(aTextColor);
			}

			aGraphics.drawString(str, x, y+ascent);
		}
	}


	public static int getStringLength(String aString, Font aFont)
	{
		return (int)aFont.getStringBounds(aString, mFontRenderContext).getWidth();
	}


	public static ArrayList<String> lineBreakText(String aString, Font aFont, int aWidth)
	{
		ArrayList<String> list = new ArrayList<>();

		for (String str : aString.split("\n"))
		{
			do
			{
				int w = getStringLength(str, aFont);
				String tmp;

				if (w > aWidth)
				{
					int offset = findStringLimit(str, aFont, aWidth);
					int temp = Math.max(str.lastIndexOf(' ', offset), Math.max(str.lastIndexOf('.', offset), Math.max(str.lastIndexOf('-', offset), str.lastIndexOf('_', offset))));
					offset = Math.max(1, temp > 1 ? temp : offset);

					tmp = str.substring(0,offset);
					str = str.substring(offset).trim();
				}
				else
				{
					tmp = str.trim();
					str = "";
				}

				list.add(tmp.trim());
			}
			while (str.length() > 0);
		}

		return list;
	}


	private static int findStringLimit(String aString, Font aFont, int aWidth)
	{
		int min = 0;
		int max = aString.length();

		while (Math.abs(min-max) > 1)
		{
			int mid = (max+min)/2;

			int w = getStringLength(aString.substring(0,mid), aFont);

			//System.out.printf("%d\t%d\t%d\t%d\t%d\n", min, max, mid, aWidth, w);

			if (w > aWidth)
			{
				max = mid;
			}
			else
			{
				min = mid;
			}
		}

		return min;
	}


	public static <T extends JComponent> T setNullBorder(T aComponent)
	{
		aComponent.setBorder(null);
		return (T)aComponent;
	}


	public static <T extends JComponent> T stripBorder(T aComponent)
	{
		aComponent.setBorder(null);
		return (T)aComponent;
	}


	public static float getDPIScale()
	{
		return Toolkit.getDefaultToolkit().getScreenSize().width / 1920f;
	}
}
package org.terifan.ui;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;


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
			try (InputStream in = aObject.getClass().getResourceAsStream(aRelativePath))
			{
				BufferedImage image = ImageIO.read(in);

				if (image == null)
				{
					throw new IOException("File not found");
				}

				return image;
			}
		}
		catch (Throwable e)
		{
			throw new IllegalArgumentException("Reading the large icon resource caused an exception: relative-path: " + aRelativePath, e);
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
}
package org.terifan.ui;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import org.terifan.util.log.Log;


/**
 * Default Drag-n-Drop implementation that wraps/hides Oracle's garbage implementation.
 *
 * <code>
 *	new DragAndDrop(component)
 *	{
 *		@Override
 *		public Object drag(Point aDragOrigin)
 *		{
 *			return "hello world"; // a serializable object
 *		}
 *
 *		@Override
 *		public void drop(DropEvent aDropEvent)
 *		{
 *			System.out.println(aDropEvent.getTransferData());
 *		}
 *	};
 * </code>
 */
public abstract class DragAndDrop
{
	private final static DataFlavor javaSerializedObjectMimeType = new DataFlavor(Serializable.class, "Java Serialized Object");

	protected JComponent mComponent;


	public DragAndDrop(JComponent aComponent)
	{
		mComponent = aComponent;

		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mComponent, DnDConstants.ACTION_COPY, new MyDragGestureListener());

		mComponent.setTransferHandler(new MyTransferHandler());
	}


	public JComponent getComponent()
	{
		return mComponent;
	}


	/**
	 * Handles drags. This implementation does nothing and returns null.
	 *
	 * @param aDragOrigin
	 *   the position on the component where the drag occurred.
	 */
	public Object drag(Point aDragOrigin)
	{
		return null;
	}


	/**
	 * Handles drops. This implementation does nothing.
	 *
	 * @param aDropEvent
	 *   an object containing details about the drop.
	 */
	public boolean canDrop(DropEvent aDropEvent)
	{
		return true;
	}


	/**
	 * Handles drops. This implementation does nothing.
	 *
	 * @param aDropEvent
	 *   an object containing details about the drop.
	 */
	public boolean drop(DropEvent aDropEvent)
	{
		return false;
	}


	/**
	 * Notified when a transfer has finished. This implementation does nothing.
	 *
	 * @param aDropEvent
	 *   an object containing details about the drop.
	 */
	public void dragEnd(boolean aSuccess, Object aDropValue)
	{
	}


	private class MyDragGestureListener implements DragGestureListener
	{
		@Override
		public void dragGestureRecognized(DragGestureEvent aDrag)
		{
			aDrag.startDrag(DragSource.DefaultCopyDrop, new MyTransferable(aDrag), new DragSourceAdapter()
			{
				@Override
				public void dragDropEnd(DragSourceDropEvent aDragSourceDropEvent)
				{
					try
					{
						dragEnd(aDragSourceDropEvent.getDropSuccess(), aDragSourceDropEvent.getDragSourceContext().getTransferable().getTransferData(javaSerializedObjectMimeType));
					}
					catch (Exception e)
					{
						e.printStackTrace(Log.out);
					}
				}
			});
		}
	}


	private class MyTransferHandler extends TransferHandler
	{
		@Override
		public boolean canImport(TransferSupport aSupport)
		{
			return aSupport.isDataFlavorSupported(javaSerializedObjectMimeType) && canDrop(new DropEvent(aSupport));
		}


		@Override
		public boolean importData(TransferSupport aSupport)
		{
			drop(new DropEvent(aSupport));
			return true;
		}


		@Override
		public int getSourceActions(JComponent aComponent)
		{
			return COPY_OR_MOVE;
		}


		@Override
		protected Transferable createTransferable(JComponent aComponent)
		{
			return new MyTransferable(null);
		}
	}


	private class MyTransferable implements Transferable
	{
		private Point mDragOrigin;


		public MyTransferable(DragGestureEvent aEvent)
		{
			mDragOrigin = aEvent.getDragOrigin();
		}


		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return new DataFlavor[]{javaSerializedObjectMimeType};
		}


		@Override
		public boolean isDataFlavorSupported(DataFlavor aFlavor)
		{
			return javaSerializedObjectMimeType.equals(aFlavor);
		}


		@Override
		public Object getTransferData(DataFlavor aFlavor)
		{
			return drag(mDragOrigin);
		}
	}


	public static class DropEvent
	{
		public final static int COPY = DnDConstants.ACTION_COPY;
		public final static int MOVE = DnDConstants.ACTION_MOVE;

		private int mDropAction;
		private Point mDropLocation;
		private Object mTransferData;


		public DropEvent(TransferSupport aSupport)
		{
			try
			{
				mTransferData = aSupport.getTransferable().getTransferData(javaSerializedObjectMimeType);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				throw new IllegalStateException(e);
			}
			mDropLocation = aSupport.getDropLocation().getDropPoint();
			mDropAction = aSupport.getDropAction();
		}


		public DropEvent(Point aDropLocation, int aDropActon, Object aTransferData)
		{
			mTransferData = aTransferData;
			mDropLocation = aDropLocation;
			mDropAction = aDropActon;
		}


		public Point getDropLocation()
		{
			return mDropLocation;
		}


		public int getDropAction()
		{
			return mDropAction;
		}


		public Object getTransferData()
		{
			return mTransferData;
		}


		@Override
		public String toString()
		{
			String action = mDropAction == MOVE ? "move" : mDropAction == COPY ? "copy" : "other";
			String at = "[" + mDropLocation.x + "," + mDropLocation.y + "]";
			return "{action=" + action + ", dropLocation=" + at + ", transferable=" + getTransferData() + "}";
		}
	}


	public static void main(String ... args)
	{
		try
		{
			JTree tree = new JTree();
			JPanel panel = new JPanel(null);

			new DragAndDrop(tree)
			{
				@Override
				public Object drag(Point aDragOrigin)
				{
					return tree.getClosestPathForLocation(aDragOrigin.x, aDragOrigin.y).getLastPathComponent().toString();
				}

				@Override
				public void dragEnd(boolean aSuccess, Object aDropValue)
				{
					Log.out.println(aSuccess+" "+aDropValue);
				}
			};

			new DragAndDrop(panel)
			{
				@Override
				public boolean canDrop(DropEvent aDropEvent)
				{
					return !"food".equals(aDropEvent.getTransferData().toString());
				}

				@Override
				public boolean drop(DropEvent aDropEvent)
				{
					JLabel label = new JLabel(aDropEvent.getTransferData().toString());
					label.setLocation(aDropEvent.getDropLocation());
					label.setSize(100,20);
					panel.add(label);
					panel.repaint();
					return true;
				}
			};

			JPanel pane = new JPanel(new GridLayout(1,2));
			pane.add(tree);
			pane.add(panel);

			JFrame frame = new JFrame();
			frame.add(pane);
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

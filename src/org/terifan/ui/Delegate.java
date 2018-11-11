package org.terifan.ui;

import java.awt.event.ActionEvent;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.Icon;
import javax.swing.JComponent;


/**
 * E.g.
 * <code>
 * JButton myButton = new JButton(new Delegate(this, "buttonClicked"));
 *
 * @DelegateTarget(name = "OK")
 * void buttonClicked() {}
 * </code>
 */
public class Delegate extends AbstractAction
{
	private Supplier mSupplier;
	private Object [] mParameters;
	private String mMethodName;
	private ArrayList<Object> mRunAfter;


	/**
	 * Create a new Delegate.
	 * <p>
	 * Note: this constructor will use a DelegateTarget annotation on the method if available to initialize the AbstractAction.
	 * </p>
	 * 
	 * @param aObject
	 *   a single Object to invoke the method on.
	 * @param aMethod
	 *   the name of the method to invoke
	 * @param aParameters 
	 *   parameters sent to the method when invoking it
	 */
	public Delegate(Object aObject, String aMethod, Object ... aParameters)
	{
		this(()->aObject, aMethod, aParameters);

		initAction(aObject, findMethod(aObject));
	}


	/**
	 * Create a new Delegate.
	 * <p>
	 * Note: DelegateTarget annotations aren't processed with this method.
	 * </p>
	 * 
	 * @param aSupplier
	 *   supplier that provide a single Object, an array or List of objects to invoke the method on.
	 * @param aMethod
	 *   the name of the method to invoke
	 * @param aParameters 
	 *   parameters sent to the method when invoking it
	 */
	public Delegate(Supplier aSupplier, String aMethod, Object ... aParameters)
	{
		mSupplier = aSupplier;
		mParameters = aParameters;
		mMethodName = aMethod;
	}


	public Icon getSmallIcon()
	{
		return (Icon)getValue(Action.SMALL_ICON);
	}


	public Delegate setSmallIcon(Icon aIcon)
	{
		putValue(Action.SMALL_ICON, aIcon);
		return this;
	}


	public String getName()
	{
		return (String)getValue(Action.NAME);
	}


	public Delegate setName(String aLabel)
	{
		putValue(Action.NAME, aLabel);
		return this;
	}


	public String getShortDescription()
	{
		return (String)getValue(Action.SHORT_DESCRIPTION);
	}


	public Delegate setShortDescription(String aShortDescription)
	{
		putValue(Action.SHORT_DESCRIPTION, aShortDescription);
		return this;
	}


	public String getLongDescription()
	{
		return (String)getValue(Action.LONG_DESCRIPTION);
	}


	public Delegate setLongDescription(String aLongDescription)
	{
		putValue(Action.LONG_DESCRIPTION, aLongDescription);
		return this;
	}


	public String getActionCommandKey()
	{
		return (String )getValue(Action.ACTION_COMMAND_KEY);
	}


	public Delegate setActionCommandKey(String aActionCommandKey)
	{
		putValue(Action.ACTION_COMMAND_KEY, aActionCommandKey);
		return this;
	}


	public KeyStroke getAcceleratorKey()
	{
		return (KeyStroke)getValue(Action.ACCELERATOR_KEY);
	}


	public Delegate setAcceleratorKey(KeyStroke aAcceleratorKey)
	{
		putValue(Action.ACCELERATOR_KEY, aAcceleratorKey);
		return this;
	}


	public Delegate setAcceleratorKey(String aAcceleratorKey)
	{
		return setAcceleratorKey(KeyStroke.getKeyStroke(aAcceleratorKey));
	}


	public Integer getMnemonicKey()
	{
		return (Integer)getValue(Action.SMALL_ICON);
	}


	public Delegate setMnemonicKey(int aMnemonicKey)
	{
		putValue(Action.MNEMONIC_KEY, aMnemonicKey);
		return this;
	}


	public boolean isSelected()
	{
		return (Boolean)getValue(Action.SELECTED_KEY);
	}


	public Delegate setSelected(boolean aSelected)
	{
		putValue(Action.SELECTED_KEY, aSelected);
		return this;
	}


	public Integer getDisplayedMnemonicKey()
	{
		return (Integer)getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
	}


	public Delegate setDisplayedMnemonicKey(int aDisplayedMnemonicKey)
	{
		putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, aDisplayedMnemonicKey);
		return this;
	}


	public Icon getLargeIcon()
	{
		return (Icon)getValue(Action.LARGE_ICON_KEY);
	}


	public Delegate setLargeIcon(Icon aIcon)
	{
		putValue(Action.LARGE_ICON_KEY, aIcon);
		return this;
	}


	public Delegate addKeyAction(JComponent aTarget, String aKeyStroke)
	{
		Utilities.addKeyAction(aTarget, KeyStroke.getKeyStroke(aKeyStroke), this);
		return this;
	}


	public Delegate addKeyAction(JComponent aTarget, int aKeyCode)
	{
		Utilities.addKeyAction(aTarget, KeyStroke.getKeyStroke(aKeyCode, 0), this);
		return this;
	}


	public Delegate addKeyAction(JComponent aTarget, int aKeyCode, int aModifiers)
	{
		Utilities.addKeyAction(aTarget, KeyStroke.getKeyStroke(aKeyCode, aModifiers), this);
		return this;
	}


	public Delegate runAfter(Consumer<Delegate> aConsumer)
	{
		if (mRunAfter == null)
		{
			mRunAfter = new ArrayList<>();
		}
		mRunAfter.add(aConsumer);
		return this;
	}


	public Delegate runAfter(Runnable aRunnable)
	{
		if (mRunAfter == null)
		{
			mRunAfter = new ArrayList<>();
		}
		mRunAfter.add(aRunnable);
		return this;
	}
	

	@Override
	public void actionPerformed(ActionEvent aEvent)
	{
		Object object = mSupplier.get();
		Object[] objects;
		
		if (object == null)
		{
			return;
		}
		
		if (object.getClass().isArray())
		{
			objects = (Object[])object;
		}
		else if (List.class.isAssignableFrom(object.getClass()))
		{
			List list = (List)object;
			if (list.isEmpty())
			{
				return;
			}
			objects = list.toArray(new Object[list.size()]);
		}
		else
		{
			objects = new Object[]{object};
		}

		Method method = findMethod(objects[0]);

		new Thread(() ->
		{
			try
			{
				for (Object o : objects)
				{
					if (mParameters.length == 0)
					{
						method.invoke(o);
					}
					else
					{
						method.invoke(o, mParameters);
					}
				}

				if (mRunAfter != null)
				{
					for (Object o : mRunAfter)
					{
						if (o instanceof Consumer)
						{
							((Consumer)o).accept(this);
						}
						else if (o instanceof Runnable)
						{
							((Runnable)o).run();
						}
					}
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				throw new IllegalStateException(e);
			}
		}).start();
	}
	
	
	private Method findMethod(Object aObject)
	{
		Method method = null;
		
		try
		{
			Class [] providedTypes = new Class[mParameters.length];
			for (int i = 0; i < mParameters.length; i++)
			{
				if (mParameters[i] == null)
				{
					throw new IllegalArgumentException("Method parameters must not be null: index: " + i);
				}

				providedTypes[i] = mParameters[i].getClass();
			}

			for (int run = 0; method == null && run < 2; run++)
			{
				for (Method m : run == 0 ? aObject.getClass().getDeclaredMethods() : aObject.getClass().getMethods())
				{
					if (m.getName().equals(mMethodName))
					{
						Class [] declaredTypes = m.getParameterTypes();

						if (declaredTypes.length == providedTypes.length)
						{
							boolean match = true;
							for (int i = 0; i < declaredTypes.length; i++)
							{
								if (!(declaredTypes[i] == providedTypes[i] || declaredTypes[i].isAssignableFrom(providedTypes[i]) || (declaredTypes[i].isPrimitive() && providedTypes[i].getField("TYPE").get((Object)null) == declaredTypes[i])))
								{
									match = false;
									break;
								}
							}

							if (match)
							{
								method = m;
								break;
							}
						}
					}
				}
			}

			if (method == null)
			{
				throw new NoSuchMethodException();
			}

			method.setAccessible(true);
		}
		catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException e)
		{
			ArrayList<Class> types = new ArrayList<>();
			for (int i = 0; i < mParameters.length; i++)
			{
				if (mParameters[i] == null)
				{
					throw new IllegalArgumentException("Argument " + i + " is null.");
				}

				types.add(mParameters[i].getClass());
			}

			throw new IllegalArgumentException("Failed to locate the method specified: class: " + aObject.getClass()+", name: " + mMethodName + ", parameters: " + types, e);
		}
		
		return method;
	}
	
	
	private void initAction(Object aObject, Method aMethod)
	{
		DelegateTarget params = aMethod.getAnnotation(DelegateTarget.class);

		if (params != null)
		{
			if (!params.accelerator().isEmpty())
			{
				KeyStroke key = KeyStroke.getKeyStroke(params.accelerator());
				if (key == null)
				{
					throw new IllegalArgumentException("Failed to decode ActionDelegate.accelerator: " + params.accelerator());
				}
				setAcceleratorKey(key);
			}
			if (!params.actionCommand().isEmpty())
			{
				setActionCommandKey(params.actionCommand());
			}
			if (params.displayMnemonic() != 0)
			{
				setDisplayedMnemonicKey(params.displayMnemonic());
			}
			if (!params.longDescription().isEmpty())
			{
				setLongDescription(params.longDescription());
			}
			if (params.mnemonic() != 0)
			{
				setMnemonicKey(params.mnemonic());
			}
			if (!params.name().isEmpty())
			{
				setName(params.name());
			}
			setSelected(params.selected());
			if (!params.shortDescription().isEmpty())
			{
				setShortDescription(params.shortDescription());
			}
			if (!params.largeIcon().isEmpty())
			{
				setLargeIcon(new ImageIcon(Utilities.readImageResource(aObject, params.largeIcon())));
			}
			if (!params.smallIcon().isEmpty())
			{
				setSmallIcon(new ImageIcon(Utilities.readImageResource(aObject, params.smallIcon())));
			}
			if (!params.keyStroke().isEmpty())
			{
				Utilities.addGlobalKeyAction(params.keyStroke(), this);
			}
		}
	}


	@Retention(value = RetentionPolicy.RUNTIME)
	@Documented
	public @interface DelegateTarget
	{
		/**
		 * The key combination pressed to fire this delegate eg. "ctrl S"
		 *
		 * @see javax.swing.Action#ACCELERATOR_KEY
		 */
		public String accelerator() default "";

		/**
		 * @see javax.swing.Action#ACTION_COMMAND_KEY
		 */
		public String actionCommand() default "";

		/**
		 * @see javax.swing.Action#DISPLAYED_MNEMONIC_INDEX_KEY
		 */
		public int displayMnemonic() default 0;

		/**
		 * @see javax.swing.Action#LONG_DESCRIPTION
		 */
		public String longDescription() default "";

		/**
		 * @see javax.swing.Action#MNEMONIC_KEY
		 */
		public int mnemonic() default 0;

		/**
		 * @see javax.swing.Action#NAME
		 */
		public String name() default "";

		/**
		 * @see javax.swing.Action#SELECTED_KEY
		 */
		public boolean selected() default false;

		/**
		 * @see javax.swing.Action#SHORT_DESCRIPTION
		 */
		public String shortDescription() default "";

		/**
		 * @see javax.swing.Action#SMALL_ICON
		 */
		public String smallIcon() default "";

		/**
		 * @see javax.swing.Action#LARGE_ICON_KEY
		 */
		public String largeIcon() default "";


		public String keyStroke() default "";
	}
}

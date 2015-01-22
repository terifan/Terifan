package org.terifan.ui;

import java.awt.event.ActionEvent;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


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
	private Object mObject;
	private Method mMethod;
	private Object [] mParameters;


	public Delegate(Object aObject, String aMethod, Object ... aParameters)
	{
		mObject = aObject;
		mParameters = aParameters;

		try
		{
			Class [] providedTypes = new Class[aParameters.length];
			for (int i = 0; i < aParameters.length; i++)
			{
				if (aParameters[i] == null)
				{
					throw new IllegalArgumentException("Method parameters must not be null: index: " + i);
				}

				providedTypes[i] = aParameters[i].getClass();
			}

			for (int run = 0; mMethod == null && run < 2; run++)
			{
				for (Method m : run == 0 ? aObject.getClass().getDeclaredMethods() : aObject.getClass().getMethods())
				{
					if (m.getName().equals(aMethod))
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
								mMethod = m;
								break;
							}
						}
					}
				}
			}

			if (mMethod == null)
			{
				throw new NoSuchMethodException();
			}

			mMethod.setAccessible(true);
		}
		catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException e)
		{
			ArrayList<Class> types = new ArrayList<>();
			for (int i = 0; i < aParameters.length; i++)
			{
				if (aParameters[i] == null)
				{
					throw new IllegalArgumentException("Argument " + i + " is null.");
				}

				types.add(aParameters[i].getClass());
			}

			throw new IllegalArgumentException("Failed to locate the method specified: class: " + aObject.getClass()+", name: " + aMethod + ", parameters: " + types, e);
		}

		DelegateTarget params = mMethod.getAnnotation(DelegateTarget.class);

		if (params != null)
		{
			if (!params.accelerator().isEmpty())
			{
				KeyStroke key = KeyStroke.getKeyStroke(params.accelerator());
				if (key == null)
				{
					throw new IllegalArgumentException("Failed to decode ActionDelegate.accelerator: " + params.accelerator());
				}
				putValue(Action.ACCELERATOR_KEY, key);
			}
			if (!params.actionCommand().isEmpty())
			{
				putValue(Action.ACTION_COMMAND_KEY, params.actionCommand());
			}
			if (params.displayMnemonic() != 0)
			{
				putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, params.displayMnemonic());
			}
			if (!params.longDescription().isEmpty())
			{
				putValue(Action.LONG_DESCRIPTION, params.longDescription());
			}
			if (!params.mnemonic().isEmpty())
			{
				putValue(Action.MNEMONIC_KEY, params.mnemonic());
			}
			if (!params.name().isEmpty())
			{
				putValue(Action.NAME, params.name());
			}
			putValue(Action.SELECTED_KEY, params.selected());
			if (!params.shortDescription().isEmpty())
			{
				putValue(Action.SHORT_DESCRIPTION, params.shortDescription());
			}
			if (!params.largeIcon().isEmpty())
			{
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(Utilities.readImageResource(aObject, params.largeIcon())));
			}
			if (!params.smallIcon().isEmpty())
			{
				putValue(Action.SMALL_ICON, new ImageIcon(Utilities.readImageResource(aObject, params.smallIcon())));
			}
			if (!params.keyStroke().isEmpty())
			{
				Utilities.addGlobalKeyAction(params.keyStroke(), this);
			}
		}
	}


	@Override
	public void actionPerformed(ActionEvent aEvent)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (mParameters.length == 0)
					{
						mMethod.invoke(mObject);
					}
					else
					{
						mMethod.invoke(mObject, mParameters);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					throw new RuntimeException(e);
				}
			}
		});
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
		public String mnemonic() default "";

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

package org.terifan.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.terifan.util.log.Log;


public class BindingListener implements DocumentListener
{
	private Object mModel;
	private String mFieldName;


	public BindingListener(Object model, String fieldName)
	{
		mModel = model;
		mFieldName = fieldName;
	}


	public static JTextField bind(JTextField aTextField, Object aModel, String aFieldName)
	{
		try
		{
			BindingListener bindingListener = new BindingListener(aModel, aFieldName);

			Object value = bindingListener.getValue();

			if (value != null)
			{
				aTextField.setText(value.toString());
			}

			aTextField.getDocument().addDocumentListener(bindingListener);
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e)
		{
			e.printStackTrace(Log.out);
		}

		return aTextField;
	}


	@Override
	public void insertUpdate(DocumentEvent e)
	{
		dataUpdated(e);
	}


	@Override
	public void removeUpdate(DocumentEvent e)
	{
		dataUpdated(e);
	}


	@Override
	public void changedUpdate(DocumentEvent e)
	{
		dataUpdated(e);
	}


	private void dataUpdated(DocumentEvent aEvent)
	{
		try
		{
			String text = aEvent.getDocument().getText(aEvent.getDocument().getStartPosition().getOffset(), aEvent.getDocument().getEndPosition().getOffset() - 1);

			setValue(text);
		}
		catch (BadLocationException e)
		{
			throw new IllegalStateException(e);
		}
	}


	private void setValue(String aText)
	{
		try
		{
			try
			{
				Method method = mModel.getClass().getDeclaredMethod("set" + getMethodName(), String.class);
				method.setAccessible(true);
				method.invoke(mModel, aText);
			}
			catch (NoSuchMethodException e)
			{
				Field field = mModel.getClass().getDeclaredField(mFieldName);
				field.setAccessible(true);
				field.set(mModel, aText);
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e)
		{
			throw new IllegalStateException(e);
		}
	}


	private Object getValue() throws IllegalAccessException, InvocationTargetException, NoSuchFieldException
	{
		Object value;
		try
		{
			Method method = mModel.getClass().getDeclaredMethod("get" + getMethodName());
			method.setAccessible(true);
			value = method.invoke(mModel);
		}
		catch (NoSuchMethodException e)
		{
			Field field = mModel.getClass().getDeclaredField(mFieldName);
			field.setAccessible(true);
			value = field.get(mModel);
		}
		return value;
	}
	
	
	private String getMethodName()
	{
		String methodName = mFieldName;

		if (Character.isLowerCase(methodName.charAt(0)))
		{
			methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		}

		return methodName;
	}
}

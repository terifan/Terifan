package org.terifan.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import org.terifan.util.log.Log;


public class ErrorReportWindow
{
	private JDialog mDialog;
	private JTextArea mTextArea;


	protected ErrorReportWindow()
	{
	}


	public ErrorReportWindow(Throwable aThrowable, boolean aShowContinue)
	{
		this(aThrowable, null, null, null, aShowContinue);
	}


	public ErrorReportWindow(Throwable aThrowable, String aTitle, String aDescription, boolean aShowContinue)
	{
		this(aThrowable, aTitle, aDescription, null, aShowContinue);
	}


	public ErrorReportWindow(Throwable aThrowable, String aTitle, String aDescription, File aLogFile, boolean aShowContinueButton)
	{
		if (aThrowable == null)
		{
			aThrowable = new IllegalArgumentException("Unknown error");
		}

		try
		{
			aThrowable.printStackTrace(Log.out);
		}
		catch (Throwable e)
		{
		}

		if (aLogFile != null)
		{
			try
			{
				try (FileOutputStream fos = new FileOutputStream(aLogFile, true); PrintStream ps = new PrintStream(fos))
				{
					ps.println("== " + Calendar.now() + " ======================================================================================================================================================================");
					aThrowable.printStackTrace(ps);
					ps.println();
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.err);
			}
		}

		try
		{
			String labelClose = "Stop application";
			String labelContinue = "Continue running";
			String title = aTitle == null ? "Unhandled exception" : aTitle;
			String description = aDescription == null ? aShowContinueButton ? "An unhandled exception has occured" : "An unhandled exception has occured and application will be closed." : aDescription;

			mDialog = new JDialog((JFrame)null, title, true);

			mTextArea = new JTextArea(20, 100);
			mTextArea.setFont(new Font("courier", Font.PLAIN, 11));

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(buffer, true);

			JButton continueButton;
			JButton closeButton = new JButton(labelClose);
			closeButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent aEvent)
				{
					mDialog.setVisible(false);
					mDialog.dispose();
					System.exit(-1);
				}
			});

			if (aShowContinueButton)
			{
				continueButton = new JButton(labelContinue);
				continueButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent aEvent)
					{
						mDialog.setVisible(false);
						mDialog.dispose();
					}
				});
			}
			else
			{
				continueButton = null;
			}

			mTextArea.setLineWrap(false);
			mTextArea.setEditable(false);
			mTextArea.setForeground(Color.BLACK);
			mTextArea.setBackground(Color.WHITE);

			JPanel textPanel = new JPanel(new BorderLayout());
			textPanel.add(new JScrollPane(mTextArea), BorderLayout.CENTER);
			textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			DialogPanel panel;

			if (continueButton != null)
			{
				panel = new DialogPanel(aTitle, description, null, textPanel, closeButton, continueButton);
			}
			else
			{
				panel = new DialogPanel(aTitle, description, null, textPanel, closeButton);
			}

			mDialog.add(panel, BorderLayout.CENTER);
			mDialog.setLocationByPlatform(true);
			mDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			try
			{
				aThrowable.printStackTrace(printStream);
			}
			catch (Throwable e)
			{
			}

			mDialog.getRootPane().setDefaultButton(continueButton != null ? continueButton : closeButton);

			mTextArea.append(new String(buffer.toByteArray()));
		}
		catch (Throwable e)
		{
			e.printStackTrace(Log.err);
		}
	}


	public void show()
	{
		mDialog.pack();
		mDialog.setVisible(true);
	}


	public static void show(Throwable aThrowable)
	{
		new ErrorReportWindow(aThrowable, null, null, null, true).show();
	}


	public static void show(Throwable aThrowable, boolean aContinueButton)
	{
		new ErrorReportWindow(aThrowable, null, null, null, aContinueButton).show();
	}


	public static void show(Throwable aThrowable, String aTitle, String aDescription, boolean aContinueButton)
	{
		new ErrorReportWindow(aThrowable, aTitle, aDescription, null, aContinueButton).show();
	}


	private static class DialogPanel extends JPanel
	{
		public DialogPanel(String aTitle, String aDescription, Icon aIcon, Component aChildComponent, JButton... aButtons)
		{
			if (aTitle == null)
			{
				aTitle = "Unhandled Exception";
			}
			if (aDescription == null)
			{
				aDescription = "An exception was thrown by the application:";
			}

			Font titleFont = new Font("arial", Font.BOLD, 11);
			Font buttonFont = new Font("arial", Font.PLAIN, 11);

			JLabel label1 = new JLabel(aTitle);
			JLabel label2 = new JLabel(aDescription);

			label1.setFont(titleFont);
			label2.setFont(buttonFont);

			JPanel labelPanel2 = new JPanel(new BorderLayout());
			labelPanel2.setBackground(Color.WHITE);
			labelPanel2.setBorder(BorderFactory.createEmptyBorder(5, 20, 3, 0));
			labelPanel2.add(label2, BorderLayout.NORTH);

			JPanel labelPanel1 = new JPanel(new BorderLayout());
			labelPanel1.setBackground(Color.WHITE);
			labelPanel1.add(label1, BorderLayout.NORTH);
			labelPanel1.add(labelPanel2, BorderLayout.CENTER);

			JPanel labelPanel = new JPanel(new BorderLayout());
			labelPanel.setBackground(Color.WHITE);
			labelPanel.setBorder(new LabelPanelBorder());
			labelPanel.add(labelPanel1, BorderLayout.CENTER);

			if (aIcon != null)
			{
				labelPanel.add(new JLabel(aIcon), BorderLayout.EAST);
			}

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			buttonPanel.setBorder(new ButtonPanelBorder());

			for (int i = 1; i < aButtons.length; i++)
			{
				aButtons[i].setFont(buttonFont);
				buttonPanel.add(aButtons[i]);
			}

			if (aButtons.length > 1)
			{
				buttonPanel.add(new JLabel("   "));
			}

			aButtons[0].setDefaultCapable(true);
			aButtons[0].setFont(buttonFont);
			buttonPanel.add(aButtons[0]);

			JPanel contentPanel = new JPanel(new BorderLayout());
			contentPanel.add(aChildComponent, BorderLayout.CENTER);
			contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			super.setLayout(new BorderLayout());
			super.add(labelPanel, BorderLayout.NORTH);
			super.add(contentPanel, BorderLayout.CENTER);
			super.add(buttonPanel, BorderLayout.SOUTH);
		}
	}


	private static class LabelPanelBorder implements Border
	{
		@Override
		public Insets getBorderInsets(Component c)
		{
			return new Insets(10, 20, 10, 10);
		}


		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}


		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			try
			{
				g.setColor(new Color(128, 128, 128));
				g.drawLine(x, y + height - 2, x + width, y + height - 2);
				g.setColor(Color.WHITE);
				g.drawLine(x, y + height - 1, x + width, y + height - 1);
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.err);
			}
		}
	}


	private static class ButtonPanelBorder implements Border
	{
		@Override
		public Insets getBorderInsets(Component c)
		{
			return new Insets(12, 10, 10, 10);
		}


		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}


		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			try
			{
				g.setColor(new Color(128, 128, 128));
				g.drawLine(x, 0, x + width, 0);
				g.setColor(Color.WHITE);
				g.drawLine(x, 1, x + width, 1);
			}
			catch (Throwable e)
			{
				e.printStackTrace(Log.err);
			}
		}
	}
}
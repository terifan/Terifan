package org.terifan.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


/**
 * PasswordDialog is used to request a password and optionally an user name from
 * an user. The dialog is created lazily when the show() method is called. The
 * dialog remains in memory and can be reused.
 */
public class PasswordDialog
{
	public enum Option
	{
		/** Enables the username field. */
		USER_NAME,
		/** Enables the confirm password field. */
		CONFIRM,
		/** Enables the remember password check-box. */
		REMEMBER,
		/** Enables the key file field. */
		KEY_FILE,
		/** Enables the select web service combo box */
		SELECTENVIRONMENT
	}
//	/** Enables the username field. */
//	public final static int USERNAME = 1;
//	/** Enables the confirm password field. */
//	public final static int CONFIRM = 2;
//	/** Enables the remember password checkbox. */
//	public final static int REMEMBER = 4;
//	/** Enables the key file field. */
//	public final static int KEYFILE = 8;

	protected String mLabelOk = "Ok";
	protected String mLabelCancel = "Cancel";
	protected String mLabelPassword = "Password:";
	protected String mLabelConfirmPassword = "Confirm Password:";
	protected String mLabelUserName = "User Name:";
	protected String mLabelRememberMyPassword = "Remember my password";
	protected String mLabelKeyFile = "Key File:";
	protected String mLabelSelectEnvironment = "Environment";
	protected Font mLabelFont = new Font("arial", Font.PLAIN, 11);
	protected Font mFieldFont = mLabelFont;
	protected Font mButtonFont = mLabelFont;

	private transient JPasswordField mPasswordField;
	private transient JPasswordField mConfirmPasswordField;
	private transient JTextField mUserNameField;
	private transient JTextField mKeyFileField;
	protected transient JComboBox mSelectEnvironmentComboBox;
	private boolean mWasCanceled;
	private JButton mOkButton;
	private JButton mCancelButton;
	private JDialog mDialog;
	private JFrame mParent;
	private int mPasswordMinimumLength;
	private JCheckBox mRememberMyPassword;

	private String mTitle;
	private String mDescription;
	private List<Option> mOptions;


	/**
	 * Constructs an instance of PasswordDialog.
	 *
	 * @param aParent
	 *   the parent frame or null if this dialog has no parent. The dialog will
	 *   center it self on top the parent window.
	 * @param aTitle
	 *   dialog title displayed in the window frame.
	 * @param aDescription
	 *   dialog description displayed above the input fields.
	 * @param aOptions
	 *   a combination of flags enabling features of the dialog.
	 * @param aPasswordMinimumLength
	 *   the minimum length of the password supplied.
	 */
	public PasswordDialog(JFrame aParent, String aTitle, String aDescription, int aPasswordMinimumLength, Option ... aOptions)
	{
		mParent = aParent;
		mPasswordMinimumLength = aPasswordMinimumLength;
		mTitle = aTitle;
		mDescription = aDescription;
		mOptions = Arrays.asList(aOptions);
	}


	/* *
	 * Constructs an instance of PasswordDialog.
	 *
	 * @param aParent
	 *   the parent frame or null if this dialog has no parent. The dialog will
	 *   center it self on top the parent window.
	 * @param aTitle
	 *   dialog title displayed in the window frame.
	 * @param aDescription
	 *   dialog description displayed above the input fields.
	 * @param aShowUserName
	 *   true if the user should enter a user name.
	 * @param aShowConfirmPassword
	 *   true if the user should enter a password confirmation. The password and
	 *   password verification must match.
	 * @param aShowRememberMyPassword
	 *   true if a checkbox should be displayed with allows the system to
	 *   remeber the user name and password. This dialog does not save the
	 *   information.
	 * @param aPasswordMinimumLength
	 *   the minimum length of the password supplied.
	 *
	 * @ deprecated use constructor with flags instead
	 */
//	public PasswordDialog(JFrame aParent, String aTitle, String aDescription, boolean aShowUserName, boolean aShowConfirmPassword, boolean aShowRememberMyPassword, int aPasswordMinimumLength)
//	{
//		mParent = aParent;
//		mPasswordMinimumLength = aPasswordMinimumLength;
//		mTitle = aTitle;
//		mDescription = aDescription;
//		mShowUserName = aShowUserName;
//		mShowConfirmPassword = aShowConfirmPassword;
//		mShowRememberMyPassword = aShowRememberMyPassword;
//	}


	/**
	 * Constructs the dialog gadgets.
	 */
	protected void constructDialog()
	{
		mDialog = new JDialog(mParent, mTitle, true);
		mSelectEnvironmentComboBox = new JComboBox();
		
		init();

		mPasswordField = new JPasswordField(20);
		mPasswordField.addKeyListener(new KeyListener());
		mPasswordField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		mOkButton = new JButton(mLabelOk);
		mOkButton.addActionListener(new MyActionListener());
		mOkButton.setDefaultCapable(true);
		mOkButton.addKeyListener(new KeyListener());
		mOkButton.setFont(mButtonFont);

		mCancelButton = new JButton(mLabelCancel);
		mCancelButton.addActionListener(new MyActionListener());
		mCancelButton.addKeyListener(new KeyListener());
		mCancelButton.setFont(mButtonFont);

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JComponent o;

		JPanel formPanel = new JPanel(gridBag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets.left = 10;
		c.insets.right = 10;
		c.insets.top = 0;
		c.insets.bottom = 0;
		o = new JLabel(" ");
		o.setFont(mLabelFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets.left = 10;
		c.insets.right = 10;
		c.insets.top = 0;
		c.insets.bottom = 0;
		o = new JLabel(mDescription);
		o.setFont(mLabelFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets.left = 10;
		c.insets.right = 10;
		c.insets.top = 0;
		c.insets.bottom = 0;
		o = new JLabel(" ");
		o.setFont(mLabelFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		if (mOptions.contains(Option.USER_NAME))
		{
			mUserNameField = new JTextField(20);
			mUserNameField.addKeyListener(new KeyListener());
			mUserNameField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			c.insets.left = 10;
			c.insets.right = 20;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = new JLabel(mLabelUserName);
			o.setFont(mLabelFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets.left = 0;
			c.insets.right = 10;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = mUserNameField;
			o.setFont(mFieldFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);
		}

		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets.left = 10;
		c.insets.right = 20;
		c.insets.top = 5;
		c.insets.bottom = 2;
		o = new JLabel(mLabelPassword);
		o.setFont(mLabelFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets.left = 0;
		c.insets.right = 10;
		c.insets.top = 5;
		c.insets.bottom = 2;
		o = mPasswordField;
		o.setFont(mFieldFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		if (mOptions.contains(Option.CONFIRM))
		{
			mConfirmPasswordField = new JPasswordField(20);
			mConfirmPasswordField.addKeyListener(new KeyListener());
			mConfirmPasswordField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			c.insets.left = 10;
			c.insets.right = 20;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = new JLabel(mLabelConfirmPassword);
			o.setFont(mLabelFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor =GridBagConstraints.NORTHWEST;
			c.insets.left = 0;
			c.insets.right = 10;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = mConfirmPasswordField;
			o.setFont(mFieldFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);
		}

		if (mOptions.contains(Option.KEY_FILE))
		{
			mKeyFileField = new JTextField(20);
			mKeyFileField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			c.insets.left = 10;
			c.insets.right = 20;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = new JLabel(mLabelKeyFile);
			o.setFont(mLabelFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets.left = 0;
			c.insets.right = 10;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = mKeyFileField;
			o.setFont(mFieldFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);
		}

		if (mOptions.contains(Option.REMEMBER))
		{
			mRememberMyPassword = new JCheckBox(mLabelRememberMyPassword);

			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			c.insets.left = 10;
			c.insets.right = 20;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = new JLabel("");
			gridBag.setConstraints(o, c);
			formPanel.add(o);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets.left = 0;
			c.insets.right = 10;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = mRememberMyPassword;
			o.setFont(mLabelFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);
		}

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets.left = 10;
		c.insets.right = 10;
		c.insets.top = 0;
		c.insets.bottom = 0;
		o = new JLabel(" ");
		o.setFont(mLabelFont);
		gridBag.setConstraints(o, c);
		formPanel.add(o);

		if(mOptions.contains(Option.SELECTENVIRONMENT))
		{
			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			c.insets.left = 10;
			c.insets.right = 20;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = new JLabel(mLabelSelectEnvironment);
			o.setFont(mLabelFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets.left = 0;
			c.insets.right = 10;
			c.insets.top = 5;
			c.insets.bottom = 2;
			o = mSelectEnvironmentComboBox;
			o.setFont(mFieldFont);
			gridBag.setConstraints(o, c);
			formPanel.add(o);
		}

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT))
		{
			{
				JPanel p = new JPanel(new GridLayout(1,2,5,0));
				p.add(mOkButton);
				p.add(mCancelButton);
				add(p);
				setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
			}
		};

		JPanel logoPanel = new JPanel()
		{
			ImageIcon icon = new ImageIcon(getClass().getResource("resources/password_dialog_top.png"));
			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(icon.getImage(), 0, 0, getWidth(), icon.getIconHeight(), null);
			}
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(icon.getIconWidth(), icon.getIconHeight());
			}
		};

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(logoPanel, BorderLayout.NORTH);
		mainPanel.add(formPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		mDialog.add(mainPanel);
		mDialog.setResizable(false);
		mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mDialog.getRootPane().setDefaultButton(mOkButton);
		mDialog.pack();

		if (mParent != null)
		{
			Rectangle bounds = mParent.getGraphicsConfiguration().getBounds();
			Dimension dimension = mDialog.getSize();
			mDialog.setLocation(bounds.width/2-dimension.width/2, bounds.height/2-dimension.height/2);
		}
		else
		{
			Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
			Dimension dimension = mDialog.getSize();
			mDialog.setLocation(center.x-dimension.width/2, center.y-dimension.height/2);
		}
	}


	/**
	 * This method is called by the constructor before creating any components.
	 * Override it to redefine labels and such.
	 */
	protected void init()
	{
	}


	/**
	 * Resets the input fields and displays the dialog. The first time this
	 * method is used the dialog gadgets are constructed. This method blocks
	 * until the user has closed the dialog.
	 */
	public boolean show()
	{
		return show("", "", false);
	}


	/**
	 * Resets the input fields and displays the dialog. The first time this
	 * method is used the dialog gadgets are constructed. This method blocks
	 * until the user has closed the dialog.
	 */
	public boolean show(String aInitialUserName, String aInitialPassword, boolean aRememberMyPasswordState)
	{
		if (mDialog == null)
		{
			constructDialog();
		}

		mPasswordField.setText(aInitialPassword);
		mOkButton.setEnabled(mPasswordMinimumLength == 0);
		mWasCanceled = true;

		if (mConfirmPasswordField != null)
		{
			mConfirmPasswordField.setText(aInitialPassword);
		}
		if (mUserNameField != null)
		{
			mUserNameField.setText(aInitialUserName);
		}
		if (mRememberMyPassword != null)
		{
			mRememberMyPassword.setSelected(aRememberMyPasswordState);
		}

		new KeyListener().keyReleased(null);

		mDialog.setVisible(true);
		mDialog.toFront();

		// Handle known Swing bug:
		try
		{
			mDialog.setAlwaysOnTop(true);
		}
		catch (NullPointerException e)
		{
			//java.lang.NullPointerException: null pData
	        //at sun.awt.windows.WWindowPeer.setAlwaysOnTop(Native Method)
	        //at sun.awt.windows.WWindowPeer.updateAlwaysOnTop(Unknown Source)
	        //at java.awt.Window.setAlwaysOnTop(Unknown Source)
	        //at org.terifan.swing.PasswordDialog.show(PasswordDialog.java:472)
		}

		return userApproved();
	}


	/**
	 * Hides the dialog.
	 */
	public void hide()
	{
		mDialog.dispose();
	}


	/**
	 * Returns the password entered or null if the user pressed cancel.
	 */
	public char [] getPassword()
	{
		if (mWasCanceled)
		{
			return null;
		}
		else
		{
			return mPasswordField.getPassword();
		}
	}


	/**
	 * Returns the user name entered or null if the user pressed cancel. This
	 * method also returns null if the dialog doesn't display the user name
	 * field.
	 */
	public String getUserName()
	{
		if (mWasCanceled)
		{
			return null;
		}
		else
		{
			return mUserNameField == null ? null : mUserNameField.getText();
		}
	}

	public String getEnvironment()
	{
		if(mWasCanceled)
		{
			return null;
		}
		else
		{
			if(mSelectEnvironmentComboBox == null)
			{
				return null;
			}

			String selectedEnvironment = (String)mSelectEnvironmentComboBox.getSelectedItem();
			return selectedEnvironment;
		}
	}

	/**
	 * Returns true if the user approved (the ok button was pressed).
	 */
	public boolean userApproved()
	{
		return !mWasCanceled;
	}


	/**
	 * Returns true if the user has checked the RememberMyPassword checkbox.
	 */
	public boolean getRememberMyPasswordState()
	{
		if (mRememberMyPassword == null)
		{
			return false;
		}
		else
		{
			return mRememberMyPassword.isSelected();
		}
	}


	private class MyActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent aEvent)
		{
			if (aEvent.getActionCommand().toLowerCase().equals("ok"))
			{
				if (mConfirmPasswordField != null && !new String(mPasswordField.getPassword()).equals(new String(mConfirmPasswordField.getPassword())))
				{
					return;
				}

				mWasCanceled = false;
			}

			mDialog.dispose();
		}
	}


	private class KeyListener extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent aEvent)
		{
			if (aEvent != null && aEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				mDialog.dispose();
			}

			if (mPasswordField.getPassword().length >= mPasswordMinimumLength && (mConfirmPasswordField == null || Arrays.equals(mPasswordField.getPassword(), mConfirmPasswordField.getPassword())))
			{
				mOkButton.setEnabled(true);
			}
			else
			{
				mOkButton.setEnabled(false);
			}
		}
	}


//	public static void main(String ... a)
//	{
//		try{
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		Toolkit.getDefaultToolkit().setDynamicLayout(true);
//		}catch(Exception e){}
//
//		JFrame f = new JFrame();
//		JPanel p = new JPanel(new FlowLayout());
//		f.setSize(800,600);
//		f.setVisible(true);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		PasswordDialog dialog = new PasswordDialog(f, "Unlock file", "Enter your password to unlock the file:", 5, PasswordDialog.Option.USER_NAME, PasswordDialog.Option.REMEMBER);
//		dialog.show("patrik", "test", true);
//		p.add(new JLabel("User name: " + (dialog.getUserName()==null?"[empty]":dialog.getUserName())));
//		p.add(new JLabel("Password: " + (dialog.getPassword()==null?"[empty]":new String(dialog.getPassword()))));
//		p.add(new JLabel("Remember: " + dialog.getRememberMyPasswordState()));
//		f.add(p);
//		f.validate();
//		f.repaint();
//	}
}
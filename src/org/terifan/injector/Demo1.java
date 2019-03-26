package org.terifan.injector;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;


public class Demo1
{
	public static void main(String... args)
	{
		try
		{
			InjectorOLD injector = new InjectorOLD();

			// normal running
//			injector.bindTypeMapping(UserService.class, UserService.class);

			// when developing & testing
			injector.bindSupplier(UserService.class, ()->new MockUserService(new User("dave", "asasasasas asasasasas"), new User("steve", "ghghghghgh ghghghgh ghghghgh")));

			injector.bindSingleton(Style.class, StyleInverted.class);

			UserPanel panel = injector.getInstance(UserPanel.class);

			JFrame frame = new JFrame();
			frame.add(panel);
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

	static class Style
	{
		Color text;
		Color background;

		public Style()
		{
			this.background = Color.WHITE;
			this.text = Color.BLACK;
		}
	}

	static class StyleInverted extends Style
	{
		StyleInverted()
		{
			this.background = Color.BLACK;
			this.text = Color.WHITE;
		}
	}

	static class UserPanel extends JPanel
	{
		private UserService mUserService;
		private User mUser;


		@Inject
		public UserPanel(Style aStyle, UserService aUserService)
		{
			mUserService = aUserService;

			JList<User> list = new JList<>(mUserService.getUsers());
			JTextArea text = new JTextArea();

			text.setForeground(aStyle.text);
			text.setBackground(aStyle.background);

			list.addListSelectionListener(aEvent ->
			{
				if (mUser != null && !mUser.mDescription.equals(text.getText()))
				{
					mUser.mDescription = text.getText();
					mUserService.save(mUser);
				}

				mUser = list.getModel().getElementAt(list.getSelectedIndex());

				text.setText(mUser.mDescription);
			});

			super.setLayout(new BorderLayout());
			super.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(list), new JScrollPane(text)));
		}
	}


	static class MockUserService extends UserService
	{
		User[] mUsers;


		public MockUserService()
		{
		}


		public MockUserService(User... aUsers)
		{
			mUsers = aUsers;
		}


		@Override
		public User[] getUsers()
		{
			return mUsers;
		}


		@Override
		public void save(User aUser)
		{
			System.out.println("saved user: " + aUser + "=" + aUser.mDescription);
		}
	}


	static class UserService
	{
		User[] getUsers()
		{
			throw new UnsupportedOperationException();
		}


		void save(User aUser)
		{
			throw new UnsupportedOperationException();
		}
	}


	static class User
	{
		String mName;
		String mDescription;


		public User(String aName, String aDescription)
		{
			mName = aName;
			mDescription = aDescription;
		}


		@Override
		public String toString()
		{
			return mName;
		}
	}
}

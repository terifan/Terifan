package org.terifan.factory;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;


public class Demo1
{
	public static void main(String ... args)
	{
		try
		{
			Factory factory = new Factory();
			factory.addSingleton(UserService.class, new MockUserService(new User("dave", "sfasdasdadad adasdasdasd"), new User("steve", "fhtfh fthfhf htyy")));

			JPanel panel = factory.newInstance(UserPanel.class);

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

	static class UserPanel extends JPanel
	{
		private final UserService mUserService;
		private User mUser;

		@Inject
		public UserPanel(UserService aUserService)
		{
			super(new BorderLayout());

			mUserService = aUserService;

			JList<User> list = new JList<>(mUserService.getUsers());
			JTextArea text = new JTextArea();

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

			super.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(text)));
		}
	}

	static class MockUserService implements UserService
	{
		User[] mUsers;
		public MockUserService(User... aUsers)
		{
			this.mUsers = aUsers;
		}
		@Override
		public User[] getUsers()
		{
			return mUsers;
		}

		@Override
		public void save(User aUser)
		{
			System.out.println("saved user: " + aUser+"="+aUser.mDescription);
		}
	}

	static interface UserService
	{
		User[] getUsers();

		void save(User aUser);
	}

	static class User
	{
		String mName;
		String mDescription;

		public User(String aName, String aDescription)
		{
			this.mName = aName;
			this.mDescription = aDescription;
		}

		@Override
		public String toString()
		{
			return mName;
		}
	}
}
